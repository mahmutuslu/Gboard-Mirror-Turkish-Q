# Gboard Mirror Turkish Q — Modern API 102 v1.0.0 Final

**Kararlı (stable) sürüm — 10 Kasım 2026.**

## Doğrulanan işlevler

Kullanıcı, Xiaomi PC Manager ekran yansıtmasından doğrudan yazarak Türkçe Q harfleri, noktalama, Shift + ?, özel karakterler ve RC8'de Ctrl+V desteği dahil tüm işlevlerin çalıştığını bildirdi.

- Fiziksel Windows Türkçe Q klavyesinden Gboard'a doğrudan karakter girişi
- Türkçe karakterler: ğ/Ğ, ü/Ü, ş/Ş, ı/I, i/İ, ö/Ö, ç/Ç
- Shift, AltGr, Türkçe Q noktalama ve özel işaretleri (özellikle ?)
- Ctrl+A (tümünü seç), Ctrl+C (kopyala), Ctrl+V (yapıştır), Ctrl+X (kes)
- Backspace ve yaygın düzenleme tuşları
- Modern libxposed API 102; LSPosed Gboard uygulama kapsamı

## Final paketin bütünlüğü

**Final APK, kullanıcı cihazında doğrulanan RC8 APK'sının byte-byte aynısıdır; yeniden derlenmemiş veya yeniden imzalanmamıştır.** Çalışan klavye kodu değiştirilmedi.

- Final sürüm etiketi: `v1.0.0`
- İndirilebilir paket: `Gboard-Mirror-Turkish-Q-v1.0.0-Final.apk`
- Kaynak kodu: `v1.0.0` etiketi altındaki depo
- SHA-256: `fc909f0f4842ed16e87118db4175067ffbff639ce2936fc5d3a52607bbb2a280`
- APK'nın **dahili** `versionName` değeri bilinçli olarak `1.0.0-modern-api102-rc8` (`versionCode=108`) kalır. Bu, final etiketiyle tutarsızlık değil; doğrulanan APK'yı aynen koruma tercihidir.

## Kurulum

1. Telefona Final APK'yı yükleyin; bu APK, RC8 ile aynı dosyadır. RC8 zaten çalışıyorsa yeniden kurulum gerekmez.
2. LSPosed'da modülü etkinleştirin, kapsamda yalnızca `com.google.android.inputmethod.latin` (Gboard) seçili olsun.
3. Yeni kurulum veya modül etkinleştirmesinden sonra telefonu yeniden başlatın.
4. Gboard'u varsayılan klavye olarak seçin; Xiaomi PC Manager ekran paylaşımında test edin.

Kullanılan derleme imzası, RC8 ile birebir aynıdır. Gelecekte farklı imzalı APK ile güncelleme yapılırsa Android bunu doğrudan üstüne yüklemeyebilir.

**Önemli:** Final etiketi burada doğrulanan cihaz yapılandırması için kararlı sürümü ifade eder; bütün Android/Gboard/Xiaomi PC Manager sürümlerinde çalıştığı iddia edilmez.
