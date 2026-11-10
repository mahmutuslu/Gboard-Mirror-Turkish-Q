# Modern libxposed API 102 — RC5 test paketi

**Final değildir.** Xiaomi 15 Ultra'da modern RC1–RC4 çalışmadı. Çalışan ve korunması gereken referans, eski legacy v0.6 APK'sıdır.

## Kanıta dayalı değişiklik

RC4 kancaları `Set<String>` ile yalnızca `aeht#onKeyDown` gibi yöntem adları üzerinden izliyor ve `onPackageLoaded` esnasında erken varsayılan classloader ile kanca kurmayı deniyordu. Resmî libxposed 102 yaşam döngüsüne göre `onPackageReady` anındaki gerçek uygulama classloader'ı farklı olabilir. Bu durumda RC4, sınıf adı aynı diye gerçek yönteme kanca kurmayı atlayabilir.

RC5:
- Yalnızca `onPackageReady` ve gerçek `getClassLoader()` kullanır.
- Tekilleştirmeyi yöntem adı yerine `Method` nesnesine göre yapar.
- Eski v0.6 DEX'inde doğrulanan Gboard `aeht.onKeyDown/onKeyUp` yöntemlerini hedefler.
- Klavye olaylarının kancaya girip girmediğini ilk 64 olay için `GboardMirrorTR` logcat etiketinde gösterir (metin içerikleri kaydedilmez).
- Eski v0.6 tuş haritasını ve seçici Gboard orijinaline devam etme işleyişini korur.

**Telefon üzerinde test edilmemiştir.** Mevcut çalışan v0.6'nın değiştirilmesi zorunlu değildir.

```powershell
& 'C:\platform-tools\adb.exe' logcat -d -v time -s GboardMirrorTR:V
```

Beklenen yükleme kaydı: `RC5 HOOK ACTIVE ... aeht#onKeyDown`. Aksi durumda classloader veya Xposed kancası başarısız olmuştur.
