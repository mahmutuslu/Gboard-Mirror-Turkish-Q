# Gboard Mirror Turkish Q — Modern Xposed API 102

Modern `libxposed` API 102 tabanlı Gboard ekran yansıtma Türkçe Q tuş köprüsünün açık kaynak **RC1 deneme sürümü**.

**Durum:** Kaynak proje GitHub'a aktarıldı; modern APK'nın GitHub Actions ile derlenmesi ve Xiaomi 15 Ultra üzerinde test edilmesi gerekiyor. Önceki legacy API uyumluluk APK'sının çalışması modern sürümün doğrulandığı anlamına gelmez.

## Teknik bilgiler
- Hedef: `com.google.android.inputmethod.latin` (Gboard)
- Modern giriş: `META-INF/xposed/java_init.list`
- `io.github.libxposed:api:102.0.0` (`compileOnly`)
- Giriş sınıfı: `com.example.gboardmirrorpassthrough.MainModule`
- Klavye: Türkçe Q / ekran yansıtma olayları (`deviceId=-100`)

## Derleme
JDK 17, Android SDK 35 ve Gradle 8.9 ile: `gradle :app:assembleDebug`.

GitHub Actions derleme başarılı olursa bir **ön sürüm** oluşturmak üzere yapılandırılmıştır. Derleme hatalıysa Release oluşturulmamalıdır.

## Test notları
Orijinal v0.6 APK'sından gözlemlenen davranışlar yeniden uygulanmıştır; kaynaklar orijinal proje kaynakları değildir. Türkçe karakterler, AltGr ve kısayollar gerçek cihazda doğrulanmalıdır. Kurulu çalışan eski modülü yedekleyin; yeni debug imzası mevcut sürümün üzerine kurulmayı engelleyebilir.

## Lisans
Orijinal APK ve varsa üçüncü taraf kaynaklarının hakları ilgili sahiplerine aittir; bu depoda yalnızca yeniden yazılmış uygulama kodu ve derleme dosyaları bulunur.
