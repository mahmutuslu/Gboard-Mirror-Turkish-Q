# Gboard Mirror Turkish Q — modern libxposed API 102

Xiaomi PC Manager ekran yansıtmasındaki fiziksel Türkçe Q klavye girişini Gboard'da işlemek için libxposed API 102'ye taşıma projesi.

**Çalışan referans:** v0.6 legacy APK. Kullanıcı bunu Xiaomi 15 Ultra ve PC Manager 6.0.2.30'da doğruladı.

**Modern sürüm:** RC1–RC4 cihaz testinde başarısız. RC7'de temel klavye ve Shift+? kullanıcı tarafından doğrulandı; RC8'de Ctrl+V doğrudan yapıştırma düzeltmesi **henüz cihazda test edilmedi**, Final değildir. Eski v0.6'yı saklayın.

## RC7'te denenen düzeltme

- Gerçek eski hedef Gboard `aeht.onKeyDown/onKeyUp` (v0.6 DEX kodunda doğrulandı).
- Modern API 102'de `onPackageLoaded` aşamasındaki **erken varsayılan classloader** yerine `onPackageReady` aşamasındaki **uygulamanın etkin classloader'ı** kullanılır.
- Yöntemleri yalnızca sınıf adına göre değil `java.lang.reflect.Method` nesnesine göre izleyerek farklı classloader'ların aynı adlı yöntemlerini birbirine karıştırmaz.
- Türkçe Q, Shift, AltGr ve düzenleme tuşlarının v0.6'dan taşınan seçici işlenişi korunur.
- Modül yükleme ve tuş olayları `GboardMirrorTR` Android logcat etiketine yazılır; karakter içeriği loglanmaz.

**Şu an bu bir kök neden hipotezidir; cihaz loglarıyla doğrulanması gerekir.**

## Geliştirme ve derleme

- `io.github.libxposed:api:102.0.0`, `compileOnly`
- `META-INF/xposed/java_init.list`, `scope.list`, `module.prop`
- JDK 17, Gradle 8.9, Android SDK 35
- `gradle :app:assembleDebug`
- Hedef uygulama ve modül kapsamı: `com.google.android.inputmethod.latin`

Cihaz testi sırasında, yalnızca RC7 yüklüyken şu komut tanılama için kullanılabilir:

```powershell
& 'C:\platform-tools\adb.exe' logcat -d -v time -s GboardMirrorTR:V
```

**Not:** GitHub Actions derleme başarısı, cihazda ekran yansıtma klavyesinin çalıştığını kanıtlamaz.

## Shift + ? düzeltmesi (RC7)

Sadece Shift sonrası gelen `KEYCODE_UNKNOWN(0)` için geçici soru işareti eşlemesi ve ayrıntılı meta/scan tanılama eklendi. Diğer karakter işleme yolları değiştirilmedi. Cihazda doğrulanmadığı için bu ön sürümdür.

## RC8 — Ctrl+V doğrudan yapıştırma

RC7'de Ctrl+A/C/X doğrudan desteklenirken Ctrl+V, Gboard'un orijinal yoluna bırakılıyordu. RC8, Ctrl+V için `performContextMenuAction(android.R.id.paste)` ekler. RC7'de çalışan Türkçe Q, Shift+? ve Backspace kodları değiştirilmedi; RC8 cihaz testi gereklidir.
