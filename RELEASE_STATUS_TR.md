# Gboard Mirror Turkish Q — RC6 durum

- Çalışan referans: legacy v0.6 (kullanıcı tarafından doğrulandı).
- Modern RC1–RC5: ekran yansıtma klavyesinde çalışmadı.
- RC5 logu: `aeht#onKeyDown`, `device=-100`, `mirror=true` görüldü.
- RC6: `InputConnection` doğrudan basılabilir karakter commit + karar loglama.
- CI: GitHub Actions'ta derleme ile doğrulanmalı.
- Gerçek cihaz testi: henüz yapılmadı; final değil.