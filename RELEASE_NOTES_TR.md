# Gboard Mirror Turkish Q — Modern API 102 RC4

**Test sürümü (pre-release). RC4 henüz kullanıcı telefonunda doğrulanmadı.**

## Çalışan v0.6 ile yapılan somut karşılaştırma
Orijinal v0.6 APK'sının `classes2.dex` kodu incelendi. `MainHook.handleLoadPackage()` içinde gerçek Gboard kancası `XposedHelpers.findAndHookMethod("aeht", classLoader, "onKeyDown"/"onKeyUp")` olarak kurulu. Önceki RC1–RC3 derlemeleri yanlışlıkla Android'in genel `InputMethodService` yöntemini ve başka Gboard sınıf adlarını kancalıyordu. Bu durum ekran yansıtma tuşlarını neden kaçırdıklarını açıklıyor.

RC4, Gboard classloader'ındaki `aeht` sınıfına modern `libxposed:api:102.0.0` kancası kuruyor. v0.6'daki Türkçe Q, AltGr, Shift, silme, Ctrl+A/C/X ve Xiaomi Shift+UNKNOWN işleyişi geri taşındı. Normal ASCII harflerini Gboard'un kendi yöntemine bırakıyor.

## Kurulum ve test
1. **Çalışan v0.6 APK'sını saklayın.** RC4 debug sertifikası farklı olduğundan eskisini kaldırmak gerekebilir.
2. RC4'ü kurun, LSPosed'de etkinleştirin ve kapsam olarak **Gboard** (`com.google.android.inputmethod.latin`) seçin.
3. Gboard'u yeniden başlatın. Xiaomi PC Manager 6.0.2.30 ekran yansıtmasında Windows fiziksel Türkçe Q klavyeyle yazmayı deneyin.
4. Sorun varsa aşağıdaki hedefli logu gönderin:

```powershell
& 'C:\platform-tools\adb.exe' logcat -d -v time -s GboardMirrorTR:V
```

Başarılı kanca kurulumu `RC4 HOOK ACTIVE ... aeht#onKeyDown`, tuş sevki `RC4 KEYDOWN ... device=-100` olarak görünür.

### Sürüm durumu
GitHub Actions Android SDK/Gradle derlemesi ve `META-INF/xposed` kontrolü başarılı. **Xiaomi 15 Ultra'da RC4 çalışma testi henüz yapılmadı. Bu sürüm final değildir.**
