# Gboard Mirror Turkish Q — Modern API 102 RC6

**Ön sürüm / deneysel. RC6, Xiaomi 15 Ultra üzerinde henüz test edilmedi.**

## Kullanıcının RC5 logu ile doğrulanan durum

- `aeht.onKeyDown` modern libxposed API 102 kancası aktif.
- PC Manager ekran yansıtmasındaki tuşlar `device=-100`, `source=0x101` ile kancaya ulaşıyor.
- Harfler (`code=32` D, `code=40` L), noktalama (`code=69` -), Backspace (`code=67`) ve Shift (`code=59`) gözlemlendi.
- Klavye girişinin nerede kesildiği henüz bilinmiyor; RC5 yalnızca hook girişini logluyordu.

## RC6 düzeltmesi ve tanılama

Orijinal v0.6 DEX kodu, normal harflerde Gboard'un kendi `onKeyDown` yöntemine devam ediyor. RC5 aynı mantığı koruyordu ancak kullanıcı testinde normal harfler yazılmadı. RC6, **normal basılabilir Unicode karakterleri doğrudan `InputConnection.commitText` ile yazar ve başarılıysa Gboard'un orijinal yöntemine gitmeden tuşu tüketir**. Türkçe özel tuşlar, Shift/AltGr, düzenleme ve silme kodu korunur.

RC6 ayrıca tuşun Gboard IME nesnesinden mi geldiğini, aktif InputConnection bulunup bulunmadığını, `commitText` sonucunu ve yerel Gboard yöntemine devam edildiğinde dönüş değerini kaydeder. Kayıtlara yazılan karakterin metni alınmaz; sadece kod ve işlem sonucu görünür.

Bu çözüm *v0.6'nın birebir portu değil*, modern interceptor uygulamasında karşılaşılan regresyonu gidermeyi hedefleyen kontrollü farklı davranıştır.

**Çalışan v0.6 APK mutlaka saklanmalı. RC6 final değil.**

## Yükleme ve teşhis

1. RC6'yı yalnızca test için kurun; imza çakışırsa eski modülü kaldırmak gerekebilir.
2. LSPosed kapsamında `com.google.android.inputmethod.latin` seçili olsun. Gboard yeniden başlatılsın.
3. Ekran yansıtma metin alanında `abc123ğüşiöçı` yazmayı deneyin.

```powershell
& 'C:\platform-tools\adb.exe' logcat -d -v time -s GboardMirrorTR:V
```

`RC6 PRINTABLE COMMIT code=... accepted=true` görünmesi karakterin InputConnection tarafından kabul edildiğini gösterir; ekranda gerçekten yazıldığı ayrıca test edilmeli.

### Durum

Android/Gradle CI derlemesi, giriş metadata doğrulaması ve kaynak kontrolü yapılmış olsa bile cihaz testi tamamlanmadan `Final` değil.