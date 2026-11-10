# Gboard Mirror Turkish Q — Modern API 102 RC8 (Ctrl+V fix)

**Test ön sürümü.** RC7'nin temel klavye davranışı gerçek Xiaomi 15 Ultra + Windows PC yansıtmasında kullanıcı tarafından doğrulandı; RC8'deki doğrudan Ctrl+V desteği henüz cihazda doğrulanmadı.

## RC8'in tek işlevsel değişikliği

- `Ctrl+V`, `InputConnection.performContextMenuAction(android.R.id.paste)` üzerinden doğrudan yapıştırma işlemini çağırır.
- `Ctrl+A`, `Ctrl+C`, `Ctrl+X` mevcut işlem yolunu korur.
- Yapıştırma çağrısı başarısızsa Gboard'un orijinal işleyicisi devreye girebilir.
- Türkçe Q, Shift + ?, AltGr, harfler, Backspace ve diğer RC7 davranışları değiştirilmedi.
- Tanılama etiketleri RC8 olarak güncellendi.

## Kurulum ve kısa kontrol

1. Çalışan RC7 APK'yı koruyun; RC8 APK'yı üzerine yükleyin.
2. `autoHotReload=false`: modül yenilemesi için **telefonu yeniden başlatın**.
3. LSPosed kapsamı: `com.google.android.inputmethod.latin`.
4. Xiaomi PC Manager ekran yansıtmasında metin seçip sırasıyla `Ctrl+C`, `Ctrl+V`, `Ctrl+A`, `Ctrl+X`; sonra `ğüşiöçı`, `Shift+?` ve Backspace deneyin.

```powershell
& 'C:\platform-tools\adb.exe' logcat -d -v time -s GboardMirrorTR:V
```

**CI derlemesi başarılı olsa bile RC8'in gerçek cihazda yapıştırmayı düzelttiği ayrıca doğrulanmalıdır. Final sürüm değildir.**
