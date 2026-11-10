# Gboard Mirror Turkish Q - Modern API 102 RC3

Deneme sürümü. Çalışan v0.6 APK'sının InputMethodService tuş kancalarını korur; Gboard LatinIME sınıfının onKeyDown/onKeyUp override yöntemlerine de kanca kurmayı dener.

Doğrudan Android logcat'e GboardMirrorTR etiketiyle modül yükleme, hook kaydı ve ilk 60 tuşu yazar. Bu kayıtlar sorunun yükleme, kanca veya tuş sevki aşamasında olduğunu ayırt etmek içindir.

API: gerçek modern libxposed 102. Gboard kapsamı dışına çıkmaz. Yeni APK cihazda henüz test edilmedi ve final değildir.

Varsayılan klavye kontrolü: adb shell settings get secure default_input_method
Tanı logları: adb logcat -d -v time -s GboardMirrorTR:V

GitHub Actions debug imzası eski modülden farklı olabilir. Çalışan v0.6'yı yedekleyin.
