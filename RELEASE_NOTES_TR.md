# Gboard Mirror Turkish Q — Modern API 102 RC7

**Deneysel ön sürüm. RC7 henüz Xiaomi 15 Ultra cihazında doğrulanmadı.**

## RC6 sonucuyla doğrulananlar

- Modern API 102 ile Gboard `aeht.onKeyDown/onKeyUp` kancaları aktif.
- Xiaomi PC Manager üzerinden normal harfler ve Türkçe Q karakterleri çalışıyor; `commitText` `accepted=true`.
- Kullanıcının 10 Kasım 2026 logları `Shift=59` ile beraber `KEYCODE_UNKNOWN=0 unicode=0` içeriyor; bu olayların Shift+? ile ilişkili olması kuvvetli olasılık ama henüz skan kodu doğrulanmadı.

## RC7 tek hedefli değişiklik

- Shift tuşunun basılı durumunu Gboard IME nesnesine bağlı, kısa süreli (1800 ms) tutar ve key-up ile temizler.
- Yalnızca Xiaomi yansıtmasının `deviceId=-100` tuşlarında; Shift ile beraber gelen `KEYCODE_UNKNOWN, unicode=0, repeat=0` olayını `?` karakterine dönüştürmeyi dener.
- CTRL, ALT, META kısayollarına dokunmaz. Shift olmadan gelen bilinmeyen tuşları Gboard'a bırakır.
- Bilinmeyen tuş için `scanCode, flags, metaState, repeatCount, shiftRecent` tanı bilgilerini loglar, metin alanının içeriğini kaydetmez.
- Diğer harfler, Türkçe Q, silme ve mevcut tuş eşleme kodları RC6 ile aynıdır.

## Test

1. Çalışan eski v0.6 APK'sını saklayın.
2. RC7 APK'yı kurun ve **telefonu yeniden başlatın** (`autoHotReload=false`).
3. Gboard varsayılan ve LSPosed kapsamında seçili iken ekran yansıtmasında `a`, `*`, `Shift+*` (Türkçe Q soru işareti), `ğüşiöçı` ve Backspace deneyin.
4. Gerekirse `adb logcat -d -v time -s GboardMirrorTR:V` ile `RC7 QUESTION COMMIT` ve `RC7 UNKNOWN KEYDOWN` kayıtlarını kontrol edin.

**Bu sürüm test içindir, Final değildir.**
