# Gboard Mirror Turkish Q — v1.0.0 Modern API 102 RC1

**Durum: ön sürüm / cihaz testi gerekli.**

Bu sürüm eski APK'nın manifestindeki API sürümünü değiştirerek değil, Gboard klavye işleyicilerini gerçek modern libxposed API 102 ile yeniden yazarak hazırlanmıştır.

## İçerik
- Modern `XposedModule` ve `hook(...).intercept(...)` giriş sistemi
- `minApiVersion=102`, `targetApiVersion=102`
- Gboard (`com.google.android.inputmethod.latin`) kapsamı
- Xiaomi ekran yansıtma klavyesinden gelen (`deviceId=-100`) olayları ayırt etme
- Türkçe i/ı/İ, AltGr karakterleri, silme ve Ctrl+A/C/V/X için ilk uygulama

## Önemli
- Orijinal v0.6 APK cihazda çalıştığı doğrulanan *legacy uyumluluk* sürümüdür; bu yeni modern RC1 henüz cihazda test edilmemiştir.
- Bu kod, orijinal geliştiricinin Java kaynakları değil; APK'daki davranışlardan yeniden yazılmış kaynaklardır.
- GitHub Actions tarafından derlenen APK debug sertifikasıyla imzalanır. Farklı imza nedeniyle var olan modülün üzerine yüklenemeyebilir.
- Kurmadan önce çalışan sürümü yedekleyin. LSPosed kapsamını Gboard'a verin, sonra Gboard'u yeniden başlatın.
- Sorun olursa çalışma günlüklerini ve Gboard sürümünü paylaşın.

## Test edilmesi gerekenler
Türkçe Q (ğ/Ğ, ü/Ü, ş/Ş, ı/I, ö/Ö, ç/Ç, i/İ), AltGr, silme, Ctrl kısayolları, Windows fiziksel klavyesinden gecikmesiz yazma.
