package com.example.gboardmirrorpassthrough;

import android.inputmethodservice.InputMethodService;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.inputmethod.InputConnection;

import io.github.libxposed.api.XposedModule;
import io.github.libxposed.api.XposedModuleInterface.PackageReadyParam;

import java.lang.reflect.Method;
import java.util.WeakHashMap;

/**
 * Modern libxposed API 102 rewrite of the observed Gboard Mirror Turkish Q v0.6
 * behavior. This is NOT the original Java source. Device testing is required.
 */
public final class MainModule extends XposedModule {
    private static final String TARGET_PACKAGE = "com.google.android.inputmethod.latin";
    private static final int MIRROR_DEVICE_ID = -100;
    private final WeakHashMap<Object, SparseBooleanArray> consumedKeys = new WeakHashMap<>();
    private boolean hooksInstalled;

    @Override
    public void onPackageReady(PackageReadyParam param) {
        if (!TARGET_PACKAGE.equals(param.getPackageName()) || hooksInstalled) return;
        try {
            Method keyDown = InputMethodService.class.getMethod(
                    "onKeyDown", int.class, KeyEvent.class);
            Method keyUp = InputMethodService.class.getMethod(
                    "onKeyUp", int.class, KeyEvent.class);
            hook(keyDown).setId("gboard-mirror-tr-keydown").intercept(chain -> {
                Object receiver = chain.getThisObject();
                int keyCode = (Integer) chain.getArg(0);
                KeyEvent event = (KeyEvent) chain.getArg(1);

                if (receiver instanceof InputMethodService && event != null
                        && isMirrored(event)) {
                    if (handleDown((InputMethodService) receiver, keyCode, event)) {
                        synchronized (consumedKeys) {
                            getConsumed(receiver).put(keyCode, true);
                        }
                        return true;
                    }
                }
                return chain.proceed();
            });
            hook(keyUp).setId("gboard-mirror-tr-keyup").intercept(chain -> {
                Object receiver = chain.getThisObject();
                int keyCode = (Integer) chain.getArg(0);
                KeyEvent event = (KeyEvent) chain.getArg(1);
                if (receiver instanceof InputMethodService && event != null
                        && isMirrored(event)) {
                    synchronized (consumedKeys) {
                        SparseBooleanArray keys = getConsumed(receiver);
                        if (keys.get(keyCode)) {
                            keys.delete(keyCode);
                            return true;
                        }
                    }
                }
                return chain.proceed();
            });
            hooksInstalled = true;
            log("Gboard Mirror TR modern API102: hooks installed");
        } catch (Throwable error) {
            log("Gboard Mirror TR modern API102: hook install failed: " + error);
        }
    }

    private SparseBooleanArray getConsumed(Object receiver) {
        SparseBooleanArray keys = consumedKeys.get(receiver);
        if (keys == null) {
            keys = new SparseBooleanArray();
            consumedKeys.put(receiver, keys);
        }
        return keys;
    }

    private static boolean isMirrored(KeyEvent event) {
        return event.getDeviceId() == MIRROR_DEVICE_ID;
    }

    private static boolean handleDown(
            InputMethodService service, int code, KeyEvent event) {
        InputConnection input = service.getCurrentInputConnection();
        if (input == null) return false;
        if (event.getRepeatCount() > 0 && code != KeyEvent.KEYCODE_DEL) return false;
        boolean altGr = isAltGr(event);
        if (event.isMetaPressed()) return false;
        if (event.isCtrlPressed() && !altGr) return handleShortcut(input, code);
        if (isNavigationOrEditing(code)) return false;
        if (code == KeyEvent.KEYCODE_DEL) return deleteBackward(input);
        if (code == KeyEvent.KEYCODE_FORWARD_DEL)
            return input.deleteSurroundingTextInCodePoints(0, 1);

        String text = printable(code, event, altGr);
        if (text == null || text.isEmpty()) return false;
        return input.commitText(text, 1);
    }

    private static boolean deleteBackward(InputConnection input) {
        CharSequence selected = input.getSelectedText(0);
        if (selected != null && selected.length() > 0)
            return input.commitText("", 1);
        return input.deleteSurroundingTextInCodePoints(1, 0);
    }

    private static boolean handleShortcut(InputConnection input, int code) {
        switch (code) {
            case KeyEvent.KEYCODE_A:
                return input.performContextMenuAction(android.R.id.selectAll);
            case KeyEvent.KEYCODE_C:
                return input.performContextMenuAction(android.R.id.copy);
            case KeyEvent.KEYCODE_V:
                return input.performContextMenuAction(android.R.id.paste);
            case KeyEvent.KEYCODE_X:
                return input.performContextMenuAction(android.R.id.cut);
            default:
                return false;
        }
    }

    private static boolean isNavigationOrEditing(int code) {
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

    private static boolean isAltGr(KeyEvent event) {
        int state = event.getMetaState();
        return (state & KeyEvent.META_ALT_RIGHT_ON) != 0
                || (event.isAltPressed() && event.isCtrlPressed());
    }

    private static String printable(int code, KeyEvent event, boolean altGr) {
        if (altGr) {
            String symbol = altGrSymbol(code);
            if (symbol != null) return symbol;
        }
        int unicode = event.getUnicodeChar(event.getMetaState());
        if (unicode <= 0 || !Character.isValidCodePoint(unicode)
                || Character.isISOControl(unicode)) return null;
        String result = new String(Character.toChars(unicode));
        boolean upper = event.isShiftPressed() ^ event.isCapsLockOn();
        if (upper && "i".equals(result)) return "İ";
        if (upper && "ı".equals(result)) return "I";
        return result;
    }

    private static String altGrSymbol(int code) {
        switch (code) {
            case KeyEvent.KEYCODE_Q: return "@";
            case KeyEvent.KEYCODE_E: return "€";
            case KeyEvent.KEYCODE_2: return "£";
            case KeyEvent.KEYCODE_3: return "#";
            case KeyEvent.KEYCODE_4: return "$";
            case KeyEvent.KEYCODE_7: return "{";
            case KeyEvent.KEYCODE_8: return "[";
            case KeyEvent.KEYCODE_9: return "]";
            case KeyEvent.KEYCODE_0: return "}";
            default: return null;
        }
    }
}
