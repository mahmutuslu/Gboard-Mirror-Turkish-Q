# Gboard Mirror Turkish Q — Modern API 102 RC2

**RC1'de ekran yansıtma sırasında yazı girişi çalışmadığı bildirildi. RC2 test içindir.**

- Gerçek modern libxposed API 102; eski legacy API içermez.
- Orijinal v0.6 DEX'i incelenerek Türkçe Q fiziksel tuş kodları tekrar eklendi: ı/I, i/İ, ğ/Ğ, ü/Ü, ş/Ş, ö/Ö, ç/Ç.
- AltGr ve Shift noktalama, silme ve Ctrl kısayolları düzeltildi.
- Gboard için onPackageLoaded aşamasında erken hook kaydı, onPackageReady yedek kaydı.
- İlk 24 klavye olayını ve hook yükleme adımlarını GboardMirrorTR etiketiyle kaydeder.
- Bu sürüm henüz Xiaomi 15 Ultra'da test edilmediğinden ön sürümdür.
- RC1 APK'yı kaldırıp yenisini kurman gerekebilir (GitHub Actions debug sertifikası).
- LSPosed modülünü etkinleştir; kapsamı com.google.android.inputmethod.latin (Gboard) olarak seç; Gboard'u zorla durdurup aç.
- Sorun sürerse Windows PowerShell: & 'C:\platform-tools\adb.exe' logcat -d -v time | Select-String 'GboardMirrorTR|LSPosedFramework' | Select-Object -Last 80
- RC1 ve eski çalışan v0.6 birbirinden farklıdır; gerekli olursa v0.6'ya geri dön.
