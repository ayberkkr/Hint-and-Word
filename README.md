# 🌌 Codenames: Paradox Matrix - Tournament Edition

Bu proje, popüler masa oyunu **Codenames** mantığının siber punk ve kuantum temasıyla harmanlanarak Java dilinde sıfırdan geliştirilmiş **2 Oyunculu (Turnuva Tipi) Masaüstü Grafik Arayüzü** versiyonudur. 

Bursa Teknik Üniversitesi, Yapay Zeka ve Makine Öğrenmesi Bölümü dönem sonu projesi kapsamında geliştirilmiştir.

## 🛠️ Teknolojiler ve Mimari Bilgiler
- **Dil:** Java (JDK 11+)
- **Arayüz Teknolojisi:** Saf Java Swing & Graphics2D (Özel piksel çizimleri ve anti-aliasing filtreleri)
- **Asenkron Programlama:** 60 FPS akıcı kart dönme animasyonları ve zamanlayıcı akışı için bağımsız **Thread** ve **javax.swing.Timer** mimarisi.
- **Ses Motoru:** Klasör ve internet bağımlılığı olmayan, saf frekans dalgaları üreten **javax.sound.sampled** sentezleyicisi.

## 🚀 Öne Çıkan Gelişmiş Özellikler
1. **Yapay Zeka Casus Asistanı (AI Hint Validator):** Casus başının, tahtada kapalı duran kelimelerin aynısını veya kelime köklerini (Örn: Tahtada *Şemsiye* varken ipucu olarak *Şemsi* girilmesi) yazmasını String manipülasyon algoritmalarıyla tarayarak engeller.
2. **Kuantum Röntgen Modu (Spy Vision):** Sıra Casus Başı'na geçtiğinde kaba boyamalar yerine, kartların arkasındaki gerçek kimlik resimleri pürüzsüzce gösterilir. Sıra Saha Ajanına geçtiğinde sistem otomatik olarak kartları kapatır.
3. **Gelişmiş Outline Metin Çizimi:** Kartların üzerindeki yazılar koyu renkli ajan resimlerinin üzerinde de net okunabilsin diye içi beyaz, dışı 8 yönlü siyah kontürlü özel grafik algoritmasıyla çizilir.
4. **Dinamik Tur ve Zamanlayıcı Motoru:** Her oyuncu için 3 dakikalık (180 saniye) geri sayım sayacı çalışır. Süre bittiğinde sıra otomatik olarak karşıya geçer. Oyuncuların kararsız kaldığı anlar için taktiksel **PAS** (Sıra Devret) mekanizması aktiftir.

## 🎮 Nasıl Oynanır?
1. Oyun başlangıcında takımların (Casus Başı ve Saha Ajanı) isimleri girilir.
2. Ortak havuzda rastgele karıştırılmış 12 kart (4 Mavi, 4 Kırmızı, 3 Sivil, 1 Katil) konumlanır.
3. Sırası gelen Casus Başı ipucunu ve ilişkili kart sayısını girip gönderir. Ekran otomatik olarak Saha Ajanına devredilir.
4. Saha Ajanı belirlenen sayı kadar kart seçer. Yanlış veya sivil kart açılırsa ya da Pas butonuna basılırsa sıra karşıya geçer.
5. **Siyah Katil Kartı** çeviren takım maçı anında kaybeder!
