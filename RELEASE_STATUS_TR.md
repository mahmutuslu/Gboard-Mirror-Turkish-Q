# Gboard Mirror Turkish Q — RC8 durum

- Legacy v0.6 çalışan referanstır.
- Modern API 102 RC7: kullanıcı ekran yansıtmasından normal harfleri, Türkçe Q karakterlerini ve `Shift+?` yazdığını doğruladı.
- RC7 kısayol incelemesi: `Ctrl+A/C/X` doğrudan `performContextMenuAction` ile işleniyor; `Ctrl+V` sadece Gboard'un yerel işlemine bırakılıyordu.
- RC8: `Ctrl+V` için doğrudan `android.R.id.paste` eklendi. RC7'nin klavye algoritması değiştirilmedi.
- RC8 cihaz testi henüz yapılmadı; Final değil.
