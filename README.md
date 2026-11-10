# Gboard Mirror Turkish Q — Modern libxposed API 102

**En son kararlı sürüm: [v1.0.0 Final](https://github.com/mahmutuslu/Gboard-Mirror-Turkish-Q/releases/tag/v1.0.0)**

Xiaomi PC Manager ekran yansıtmasında Windows'un fiziksel **Türkçe Q klavyesinden** Gboard'a doğrudan yazma desteği sunan Modern Xposed API 102 modülü.

## Durum

10 Kasım 2026'da kullanıcı, Xiaomi 15 Ultra üzerindeki Gboard ile ekran yansıtmasından doğrudan Türkçe Q metin, özel karakterler, soru işareti ve Ctrl+C/V dahil tüm işlevleri çalışır doğruladı.

**Final dağıtım, çalışan RC8 APK'sıyla byte-byte aynıdır.** Bu nedenle modülün davranışı veya imzası değişmemiştir. APK içinde eski `1.0.0-modern-api102-rc8` dahili sürüm adı ve `versionCode=108` bulunması beklenen bir durumdur. GitHub'daki Final dağıtım etiketi `v1.0.0`'dır.

## Özellikler

- Türkçe Q harfleri: `ğ ü ş ı ö ç` ve büyük harfleri
- Standart harfler, özel karakterler, Shift, AltGr, özellikle Shift ile `?`
- Ctrl+A / Ctrl+C / Ctrl+V / Ctrl+X
- Backspace ve olağan düzenleme tuşları
- Modern API 102, Gboard `aeht.onKeyDown/onKeyUp` kancaları
- Sadece Xiaomi mirror klavye olaylarında giriş düzeltmesi (sanal cihaz kimliği `-100`)

## Kurulum

1. **[Final APK](https://github.com/mahmutuslu/Gboard-Mirror-Turkish-Q/releases/download/v1.0.0/Gboard-Mirror-Turkish-Q-v1.0.0-Final.apk)** dosyasını indirin ve yükleyin.
2. LSPosed'da etkinleştirip `com.google.android.inputmethod.latin` kapsamını seçin.
3. Gboard varsayılan klavyeyken telefonu yeniden başlatın ve Xiaomi PC Manager ekran yansıtmasında test edin.

RC8 zaten yüklü ve çalışıyorsa tekrar yüklemeniz gerekmez; Final'deki APK birebir aynıdır.

## Geliştirme

- Paket adı: `com.example.gboardmirrortrv06`
- Hedef: `com.google.android.inputmethod.latin`
- libxposed bağımlılığı: `io.github.libxposed:api:102.0.0`
- Android SDK 35, JDK 17, Gradle 8.9
- Kaynak: `app/src/main/java/com/example/gboardmirrorpassthrough/MainModule.java`
- Tanılama: `adb logcat -d -v time -s GboardMirrorTR:V`

Final APK yeniden derlenerek üretilmez; çalıştığı doğrulanan RC8 sürümünün SHA-256 bütünlük kontrolünden geçmiş kopyası yayımlanır. Gelecekte yeniden derlenecek güncellemeler, mevcut APK'yla imza uyumluluğu açısından ayrıca değerlendirilmelidir.
