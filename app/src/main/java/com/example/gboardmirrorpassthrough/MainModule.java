package com.example.gboardmirrorpassthrough;

import android.inputmethodservice.InputMethodService;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.inputmethod.InputConnection;
import io.github.libxposed.api.XposedInterface.HookHandle;
import io.github.libxposed.api.XposedModule;
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam;
import io.github.libxposed.api.XposedModuleInterface.PackageLoadedParam;
import io.github.libxposed.api.XposedModuleInterface.PackageReadyParam;
import java.lang.reflect.Method;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Native libxposed API 102 port of user-supplied Gboard Mirror Turkish Q v0.6.
 * Reimplemented behavior from v0.6 DEX. RC2 is untested on a physical phone.
 */
public final class MainModule extends XposedModule {

    private static final String TAG = "GboardMirrorTR";
    private static final String GBOARD = "com.google.android.inputmethod.latin";
    private static final int MIRROR_DEVICE_ID = -100;
    private final WeakHashMap<Object, SparseBooleanArray> consumedKeys = new WeakHashMap<>();
    private final AtomicInteger eventLogs = new AtomicInteger();
    private final java.util.Set<String> hookedMethods = new java.util.HashSet<>();
    private final java.util.List<HookHandle> liveHooks = new java.util.ArrayList<>();

    private void trace(int level, String message) {
        Log.println(level, TAG, message);
        log(level, TAG, message);
    }
    private void traceError(String message, Throwable e) {
        Log.e(TAG, message, e);
        log(Log.ERROR, TAG, message, e);
    }

    @Override
    public void onModuleLoaded(ModuleLoadedParam param) {
        trace(Log.INFO, "RC4 onModuleLoaded process=" + param.getProcessName());
    }

    @Override
    public void onPackageLoaded(PackageLoadedParam param) {
        if (GBOARD.equals(param.getPackageName())) {
            install("onPackageLoaded", param.getDefaultClassLoader());
        }
    }

    @Override
    public void onPackageReady(PackageReadyParam param) {
        if (GBOARD.equals(param.getPackageName())) {
            install("onPackageReady", param.getClassLoader());
        }
    }

    private synchronized void install(String stage, ClassLoader loader) {
        if (loader == null) {
            trace(Log.WARN, "RC4: classloader missing in " + stage);
            return;
        }
        try {
            // The verified v0.6 DEX hooks "aeht" rather than InputMethodService.
            Class<?> target = Class.forName("aeht", false, loader);
            trace(Log.INFO, "RC4 original target found: " + target.getName());
            installTarget(target, stage);
        } catch (ClassNotFoundException error) {
            trace(Log.WARN, "RC4 aeht not found in " + stage);
        } catch (Throwable error) {
            traceError("RC4 aeht lookup failed", error);
        }
    }

    private void installTarget(Class<?> owner, String stage) {
        for (int action = 0; action < 2; action++) {
            final boolean down = action == 0;
            final String methodName = down ? "onKeyDown" : "onKeyUp";
            final String key = owner.getName() + "#" + methodName;
            if (hookedMethods.contains(key)) continue;
            final Method method;
            try {
                method = owner.getDeclaredMethod(methodName, int.class, KeyEvent.class);
            } catch (NoSuchMethodException noOverride) {
                continue;
            }
            try {
                HookHandle handle = hook(method)
                        .setId("gboard-tr-rc3-" + key)
                        .intercept(chain -> {
                            Object receiver = chain.getThisObject();
                            int code = (Integer) chain.getArg(0);
                            KeyEvent event = (KeyEvent) chain.getArg(1);
                            if (down) {
                                if (event != null && eventLogs.getAndIncrement() < 60) {
                                    trace(Log.INFO, "RC4 KEYDOWN code=" + code
                                            + " device=" + event.getDeviceId()
                                            + " source=0x" + Integer.toHexString(event.getSource())
                                            + " unicode=" + event.getUnicodeChar()
                                            + " method=" + key);
                                }
                                if (receiver instanceof InputMethodService && isMirror(event)) {
                                    try {
                                        Boolean result = handleDown((InputMethodService) receiver, code, event);
                                        if (result != null) {
                                            if (result) {
                                                synchronized (consumedKeys) {
                                                    keyState(receiver).put(code, true);
                                                }
                                            }
                                            return result;
                                        }
                                    } catch (Throwable error) {
                                        traceError("RC4 handleDown failed code=" + code, error);
                                    }
                                }
                            } else if (receiver instanceof InputMethodService && isMirror(event)) {
                                synchronized (consumedKeys) {
                                    SparseBooleanArray keys = keyState(receiver);
                                    if (keys.get(code)) {
                                        keys.delete(code);
                                        return true;
                                    }
                                    if (!passPrintable(event) || passEditingKey(code)
                                            || code == KeyEvent.KEYCODE_DEL
                                            || code == KeyEvent.KEYCODE_FORWARD_DEL) {
                                        return false;
                                    }
                                }
                            }
                            return chain.proceed();
                        });
                liveHooks.add(handle);
                hookedMethods.add(key);
                trace(Log.INFO, "RC4 HOOK ACTIVE " + stage + " " + key);
            } catch (Throwable error) {
                traceError("RC4 HOOK FAILED " + stage + " " + key, error);
            }
        }
    }

    private SparseBooleanArray keyState(Object receiver) {
        SparseBooleanArray keys = consumedKeys.get(receiver);
        if (keys == null) {
            keys = new SparseBooleanArray();
            consumedKeys.put(receiver, keys);
        }
        return keys;
    }

    private static boolean isMirror(KeyEvent event) {
        return event != null && event.getDeviceId() == MIRROR_DEVICE_ID
                && (event.getSource() & InputDevice.SOURCE_KEYBOARD)
                        == InputDevice.SOURCE_KEYBOARD;
    }


    // null => invoke Gboard's original method; Boolean => override its return.
    private Boolean handleDown(InputMethodService ime, int code, KeyEvent e) {
        InputConnection connection = ime.getCurrentInputConnection();

        // Working v0.6: Ctrl+A/C/X; let other Ctrl shortcuts reach Gboard.
        if (e.isCtrlPressed() && !e.isAltPressed() && !e.isMetaPressed()
                && !e.isShiftPressed() && e.getRepeatCount() == 0) {
            int action = 0;
            if (code == KeyEvent.KEYCODE_A) action = android.R.id.selectAll;
            else if (code == KeyEvent.KEYCODE_C) action = android.R.id.copy;
            else if (code == KeyEvent.KEYCODE_X) action = android.R.id.cut;
            if (action != 0 && connection != null
                    && connection.performContextMenuAction(action)) return true;
        }

        // Xiaomi-specific shifted unknown-key event in v0.6.
        if (code == KeyEvent.KEYCODE_UNKNOWN && e.getScanCode() != 0
                && e.getUnicodeChar() == 0 && e.isShiftPressed()
                && e.getRepeatCount() == 0 && (e.getFlags() & 8) != 0
                && connection != null && connection.commitText("?", 1)) return true;

        if ((code == KeyEvent.KEYCODE_DEL || code == KeyEvent.KEYCODE_FORWARD_DEL)
                && !ordinaryShortcut(e) && !isAltGr(e)) {
            return connection != null && deleteText(connection,
                    code == KeyEvent.KEYCODE_FORWARD_DEL);
        }
        if (ordinaryShortcut(e)) return null;
        String text = mappedText(code, e, isAltGr(e));
        if (text != null && connection != null && connection.commitText(text, 1)) return true;
        // Preserve native Gboard handling for normal printable keys.
        if (!passPrintable(e) || passEditingKey(code)) return false;
        return null;
    }

    private static boolean deleteText(InputConnection connection, boolean forward) {
        try {
            CharSequence selected = connection.getSelectedText(0);
            if (selected != null && selected.length() > 0) return connection.commitText("", 1);
            return connection.deleteSurroundingTextInCodePoints(forward ? 0 : 1,
                    forward ? 1 : 0);
        } catch (Throwable error) {
            return false;
        }
    }

    private static boolean ordinaryShortcut(KeyEvent e) {
        int meta = e.getMetaState();
        return (meta & 0x10000) != 0 || ((meta & 0x1002) != 0 && !isAltGr(e));
    }

    private static boolean passPrintable(KeyEvent e) {
        if (ordinaryShortcut(e) || isAltGr(e) || e.getRepeatCount() != 0) return false;
        int unicode = e.getUnicodeChar();
        return unicode >= 32 && Character.isValidCodePoint(unicode)
                && !Character.isISOControl(unicode);
    }

    private static boolean deleteBackward(InputConnection connection) {
        CharSequence selected = connection.getSelectedText(0);
        if (selected != null && selected.length() > 0)
            return connection.commitText("", 1);
        return connection.deleteSurroundingTextInCodePoints(1, 0);
    }

    private static boolean performShortcut(InputConnection connection, int code) {
        switch (code) {
            case KeyEvent.KEYCODE_A:
                return connection.performContextMenuAction(android.R.id.selectAll);
            case KeyEvent.KEYCODE_C:
                return connection.performContextMenuAction(android.R.id.copy);
            case KeyEvent.KEYCODE_V:
                return connection.performContextMenuAction(android.R.id.paste);
            case KeyEvent.KEYCODE_X:
                return connection.performContextMenuAction(android.R.id.cut);
            default:
                return false;
        }
    }

    private static boolean passEditingKey(int code) {
        switch (code) {
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_TAB:
            case KeyEvent.KEYCODE_ESCAPE:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_PAGE_UP:
            case KeyEvent.KEYCODE_PAGE_DOWN:
            case KeyEvent.KEYCODE_MOVE_HOME:
            case KeyEvent.KEYCODE_MOVE_END:
                return true;
            default:
                return false;
        }
    }

    private static boolean isAltGr(KeyEvent e) {
        return (e.getMetaState() & KeyEvent.META_ALT_RIGHT_ON) != 0
                || (e.isAltPressed() && e.isCtrlPressed());
    }

    private static boolean upper(KeyEvent e) {
        return e.isShiftPressed() ^ e.isCapsLockOn();
    }

    /**
     * Mirrors original v0.6 priority: AltGr, manual Turkish Q keys,
     * shifted symbols, Unicode fallback.
     */
    private static String mappedText(int code, KeyEvent e, boolean altGr) {
        if (code == KeyEvent.KEYCODE_AT) return "@";
        if (code == KeyEvent.KEYCODE_Q && e.getUnicodeChar() == '@') return "@";
        if (altGr) return altGrSymbol(code, e);
        boolean capital = upper(e);
        switch (code) {
            case KeyEvent.KEYCODE_I: return capital ? "I" : "ı";
            case KeyEvent.KEYCODE_LEFT_BRACKET: return capital ? "Ğ" : "ğ";
            case KeyEvent.KEYCODE_RIGHT_BRACKET: return capital ? "Ü" : "ü";
            case KeyEvent.KEYCODE_BACKSLASH: return capital ? "Ç" : "ç";
            case KeyEvent.KEYCODE_SEMICOLON: return capital ? "Ş" : "ş";
            case KeyEvent.KEYCODE_APOSTROPHE: return capital ? "İ" : "i";
            case KeyEvent.KEYCODE_SLASH: return capital ? "Ö" : "ö";
        }
        String symbol = turkishSymbol(code, e.isShiftPressed());
        if (symbol != null) return symbol;
        if (code >= KeyEvent.KEYCODE_A && code <= KeyEvent.KEYCODE_Z) {
            char letter = (char) ('a' + code - KeyEvent.KEYCODE_A);
            return String.valueOf(capital ? Character.toUpperCase(letter) : letter);
        }
        if (code == KeyEvent.KEYCODE_SPACE) return " ";
        int c = e.getUnicodeChar();
        if (c <= 0 || !Character.isValidCodePoint(c) || Character.isISOControl(c))
            return null;
        return new String(Character.toChars(c));
    }

    private static String turkishSymbol(int code, boolean shift) {
        switch (code) {
            case KeyEvent.KEYCODE_0: return shift ? "=" : "0";
            case KeyEvent.KEYCODE_1: return shift ? "!" : "1";
            case KeyEvent.KEYCODE_2: return shift ? "'" : "2";
            case KeyEvent.KEYCODE_3: return shift ? "^" : "3";
            case KeyEvent.KEYCODE_4: return shift ? "+" : "4";
            case KeyEvent.KEYCODE_5: return shift ? "%" : "5";
            case KeyEvent.KEYCODE_6: return shift ? "&" : "6";
            case KeyEvent.KEYCODE_7: return shift ? "/" : "7";
            case KeyEvent.KEYCODE_8: return shift ? "(" : "8";
            case KeyEvent.KEYCODE_9: return shift ? ")" : "9";
            case KeyEvent.KEYCODE_COMMA: return shift ? ";" : ",";
            case KeyEvent.KEYCODE_PERIOD: return shift ? ":" : ".";
            case KeyEvent.KEYCODE_GRAVE: return shift ? "é" : "\"";
            case KeyEvent.KEYCODE_MINUS: return shift ? "_" : "-";
            case KeyEvent.KEYCODE_EQUALS: return shift ? "?" : "*";
            default: return null;
        }
    }

    private static String altGrSymbol(int code, KeyEvent e) {
        switch (code) {
            case KeyEvent.KEYCODE_0: return "}";
            case KeyEvent.KEYCODE_2: return "£";
            case KeyEvent.KEYCODE_3: return "#";
            case KeyEvent.KEYCODE_4: return "$";
            case KeyEvent.KEYCODE_5: return "½";
            case KeyEvent.KEYCODE_7: return "{";
            case KeyEvent.KEYCODE_8: return "[";
            case KeyEvent.KEYCODE_9: return "]";
            case KeyEvent.KEYCODE_A: return e.isShiftPressed() ? "Æ" : "æ";
            case KeyEvent.KEYCODE_E: return "€";
            case KeyEvent.KEYCODE_I: return e.isShiftPressed() ? "İ" : "i";
            case KeyEvent.KEYCODE_Q: return "@";
            case KeyEvent.KEYCODE_S: return "ß";
            case KeyEvent.KEYCODE_T: return "₺";
            case KeyEvent.KEYCODE_GRAVE: return "<";
            case KeyEvent.KEYCODE_MINUS: return "\\";
            case KeyEvent.KEYCODE_EQUALS: return "|";
            default: return null;
        }
    }
}
