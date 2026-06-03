import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.Date;
import javax.sound.sampled.*;

/**
 * =============================================================================
 * BURSA TEKNİK ÜNİVERSİTESİ - YAPAY ZEKA VE MAKİNE ÖĞRENMESİ BÖLÜMÜ
 * DÖNEM SONU GRAFİK ARAYÜZÜ PROJESİ: CODENAMES - PARADOX MATRIX SÜRÜMÜ
 * * Geliştirici Ekip: Ayberk Kar & Proje Grubu
 * Teslim Tarihi: Son 4 Gün Sınırı
 * =============================================================================
 * * Proje Özellikleri:
 * 1. 16:10 Premium Sinematik Dikdörtgen Kart Yapısı (4:3 Basıklık Giderildi)
 * 2. 60 FPS Thread-Based Akıcı Kart Dönme Animasyonu
 * 3. HTML Destekli Siber Turnuva Akış ve Operasyon Günlüğü (Log)
 * 4. Dahili Sentezleyici ile Tamamen Bağımsız Ses Efektleri Motoru (Audio System)
 * 5. Gerçek Zamanlı 3 Dakikalık Geri Sayım Sayaç Motoru (Dinamik Timer)
 * 6. Kelime Köklerini ve Kopyaları Engelleyen Yapay Zeka Casus Asistanı (AI Validator)
 * =============================================================================
 */
public class Main {
    
    // =============================================================================
    // GLOBAL KULLANICI ARAYÜZÜ BİLEŞENLERİ (GUI COMPONENTS)
    // =============================================================================
    private static JFrame frame;
    private static JPanel topPanel;
    private static JPanel boardPanel; 
    private static JPanel bottomPanel; 
    private static JTextPane logPane;

    // =============================================================================
    // METİNSEL VE DURUMSAL ETİKETLER (LABELS & FIELDS)
    // =============================================================================
    private static JLabel turnLabel;
    private static JLabel activeClueLabel; 
    private static JLabel blueScoreLabel;
    private static JLabel redScoreLabel;
    private static JLabel timerLabel; 
    private static JTextField clueField;
    private static JButton submitClueBtn;
    private static JButton passTurnBtn; 
    private static JButton restartBtn;  
    private static JButton helpBtn;

    // ==========================================
    // SEÇİM HAKKI VE TUR SAYAÇLARI
    // ==========================================
    private static int clueCount = 1;
    private static int currentTurnGuesses = 0; 
    private static JLabel countValueLabel;

    // ==========================================
    // KRONOMETRE VE SAYAÇ DEĞİŞKENLERİ
    // ==========================================
    private static javax.swing.Timer gameTimer;
    private static int remainingSeconds = 180; // Oyuncuların maksimum düşünme süresi: 3 Dakika

    // ==========================================
    // DİNAMİK OYUNCU İSİMLERİ VERİ BELLEĞİ
    // ==========================================
    private static String maviCasusIsim = "Mavi Casus";
    private static String maviAjanIsim = "Mavi Ajan";
    private static String kirmiziCasusIsim = "Kırmızı Casus";
    private static String kirmiziAjanIsim = "Kırmızı Ajan";

    // ==========================================
    // OYUN DURUM OTOMATI (GAME STATE MACHINE)
    // ==========================================
    private enum GameState {
        MAVI_CASUS,
        MAVI_AJAN,
        KIRMIZI_CASUS,
        KIRMIZI_AJAN
    }
    private static GameState currentState = GameState.MAVI_CASUS;

    // Skor Kontrolleri (4 Mavi, 4 Kırmızı Başlangıç Sınırı)
    private static int blueLeft = 4; 
    private static int redLeft = 4;

    /**
     * Anonim iç sınıflar ve Lambda ifadelerinde değişkenlerin 
     * 'effectively final' olma zorunluluğunu aşmak için kurulan veri sarmalayıcı sınıf.
     */
    private static class TurnControl {
        boolean turnContinues = false;
    }

    // Kart Nesnelerinin Hafıza Havuzları
    private static ArrayList<Card> activeCards = new ArrayList<>();
    private static ArrayList<GameCardButton> boardButtons = new ArrayList<>();

    // Premium Tasarım Sabitleri (Kart Oranları)
    private static final int CARD_WIDTH = 230;
    private static final int CARD_HEIGHT = 145;

    // ULTRA MODERN SİBER PUNK RENK KODLARI
    private static final Color BG_COLOR = new Color(15, 15, 20);
    private static final Color PANEL_BG = new Color(24, 25, 35);
    private static final Color INPUT_BG = new Color(36, 38, 52);
    private static final Color NEON_BLUE = new Color(0, 180, 216);
    private static final Color NEON_RED = new Color(239, 35, 60);
    private static final Color NEON_GREEN = new Color(46, 196, 182);
    private static final Color GOLD_COLOR = new Color(255, 193, 7);

    // =============================================================================
    // ANA METOT (PROGRAM BAŞLANGICI)
    // =============================================================================
    public static void main(String[] args) {
        
        // Kelime havuzunu ilklendir ve matrisi karıştır
        prepareAdvancedMatrixData();

        // Java Swing varsayılan pencerelerini modern çapraz platform arayüz motoruna bağlama
        try { 
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); 
        } catch (Exception e) {
            System.out.println("Sistem grafik motoru başlatılamadı, varsayılan sistem stili devrede.");
        }

        // Oyun açılışında oyuncu profillerini alan arayüz paneli tetiklenir
        openPlayerNameDialog();

        // Ana Pencere (JFrame) Mimarisi İnşası
        frame = new JFrame("CODENAMES: PARADOX TURNUVA SÜRÜMÜ v4.0");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1450, 820); 
        frame.setLayout(new BorderLayout(15, 15));
        frame.getContentPane().setBackground(BG_COLOR);
        frame.setLocationRelativeTo(null); 

        // ==========================================
        // BÖLÜM 1: ÜST PANEL VE OPERASYON KOMUT MERKEZİ
        // ==========================================
        topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(PANEL_BG);
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, NEON_BLUE));
        GridBagConstraints tGbc = new GridBagConstraints();
        tGbc.insets = new Insets(8, 20, 8, 20);

        // Sol Buton Kombinasyon Grubu (Kılavuz & Yenile)
        JPanel leftBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftBtnPanel.setOpaque(false);
        helpBtn = new createModernButton(" [ ? ] KILAVUZ ", GOLD_COLOR);
        restartBtn = new createModernButton(" 🔄 YENİ OYUN ", NEON_GREEN); 
        leftBtnPanel.add(helpBtn);
        leftBtnPanel.add(restartBtn);
        
        tGbc.gridx = 0; 
        tGbc.gridy = 0; 
        tGbc.anchor = GridBagConstraints.WEST;
        topPanel.add(leftBtnPanel, tGbc);

        // Dinamik Tur Gösterge Alanı
        turnLabel = new JLabel("SIRA: " + maviCasusIsim.toUpperCase() + " (İPUCU BEKLENİYOR)");
        turnLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        turnLabel.setForeground(NEON_BLUE);
        tGbc.gridx = 1; 
        tGbc.gridy = 0; 
        tGbc.anchor = GridBagConstraints.CENTER;
        topPanel.add(turnLabel, tGbc);

        // Sağ Bilgi İstasyonu (Neon Kronometre ve Skorlar)
        JPanel rightInfoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        rightInfoPanel.setOpaque(false);
        
        timerLabel = new JLabel("03:00");
        timerLabel.setFont(new Font("Consolas", Font.BOLD, 22));
        timerLabel.setForeground(GOLD_COLOR);
        
        blueScoreLabel = new JLabel("MAVİ KALAN: " + blueLeft);
        blueScoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        blueScoreLabel.setForeground(NEON_BLUE);
        
        redScoreLabel = new JLabel("KIRMIZI KALAN: " + redLeft);
        redScoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        redScoreLabel.setForeground(NEON_RED);
        
        rightInfoPanel.add(timerLabel);
        rightInfoPanel.add(blueScoreLabel);
        rightInfoPanel.add(redScoreLabel);
        
        tGbc.gridx = 2; 
        tGbc.gridy = 0; 
        tGbc.anchor = GridBagConstraints.EAST;
        topPanel.add(rightInfoPanel, tGbc);

        // İpucu Verme Giriş Formu Bileşenleri
        JPanel controlRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        controlRow.setOpaque(false);

        JLabel clueLabel = new JLabel("İpucu Kelimesi:");
        clueLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        clueLabel.setForeground(Color.WHITE);
        
        clueField = new JTextField(12) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(INPUT_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        clueField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        clueField.setForeground(Color.WHITE);
        clueField.setCaretColor(Color.WHITE);
        clueField.setOpaque(false);
        clueField.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        JLabel numberLabel = new JLabel("Sayı:");
        numberLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        numberLabel.setForeground(Color.WHITE);

        // Özelleştirilmiş Hak Sayacı Aracı (+ / - Butonları)
        JPanel customSpinnerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        customSpinnerPanel.setOpaque(false);
        JButton minusBtn = new createModernButton(" - ", INPUT_BG);
        countValueLabel = new JLabel("1", SwingConstants.CENTER);
        countValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        countValueLabel.setForeground(Color.WHITE);
        countValueLabel.setPreferredSize(new Dimension(20, 30));
        JButton plusBtn = new createModernButton(" + ", INPUT_BG);

        customSpinnerPanel.add(minusBtn);
        customSpinnerPanel.add(countValueLabel);
        customSpinnerPanel.add(plusBtn);

        // Sayaç Azaltma Tetikleyicisi
        minusBtn.addActionListener(e -> { 
            if (clueCount > 1) { 
                clueCount--; 
                countValueLabel.setText(String.valueOf(clueCount)); 
            }
        });
        
        // Sayaç Artırma Tetikleyicisi
        plusBtn.addActionListener(e -> { 
            if (clueCount < 4) { 
                clueCount++; 
                countValueLabel.setText(String.valueOf(clueCount)); 
            }
        });

        submitClueBtn = new createModernButton("İpucu Gönder", NEON_BLUE);
        
        controlRow.add(clueLabel);
        controlRow.add(clueField);
        controlRow.add(numberLabel);
        controlRow.add(customSpinnerPanel);
        controlRow.add(submitClueBtn);

        tGbc.gridx = 0; 
        tGbc.gridy = 1; 
        tGbc.gridwidth = 3; 
        tGbc.anchor = GridBagConstraints.CENTER;
        topPanel.add(controlRow, tGbc);

        // ==========================================
        // BÖLÜM 2: MERKEZİ MATRİS VE TERMİNAL PANELİ
        // ==========================================
        JPanel mainCenterContainer = new JPanel(new BorderLayout(20, 0));
        mainCenterContainer.setOpaque(false);
        mainCenterContainer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        activeClueLabel = new JLabel("AKTİF İPUCU: BEKLENİYOR...", SwingConstants.CENTER);
        activeClueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        activeClueLabel.setForeground(GOLD_COLOR);
        activeClueLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JPanel leftContainer = new JPanel(new BorderLayout());
        leftContainer.setOpaque(false);

        boardPanel = new JPanel(new GridLayout(3, 4, 15, 15));
        boardPanel.setBackground(PANEL_BG);
        boardPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(60, 65, 80), 1), "OYUN TAHTASI", 
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
            new Font("Segoe UI", Font.BOLD, 15), Color.WHITE));

        // Matris kartlarını panele ekle
        buildMatrixBoardUI();

        leftContainer.add(activeClueLabel, BorderLayout.NORTH);
        leftContainer.add(boardPanel, BorderLayout.CENTER);

        // Operasyon Günlüğü (Siber HTML Log) Alanı Girişi
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBackground(PANEL_BG);
        logPanel.setPreferredSize(new Dimension(300, 0));
        logPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(60, 65, 80), 1), "OPERASYON GÜNLÜĞÜ", 
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
            new Font("Segoe UI", Font.BOLD, 13), Color.LIGHT_GRAY));

        logPane = new JTextPane();
        logPane.setEditable(false);
        logPane.setContentType("text/html");
        logPane.setBackground(new Color(14, 14, 20));
        logPane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        logPane.setText("<html><body style='font-family:Consolas,monospaced; font-size:11px; color:#aebbc7; margin:0; padding:0;'></body></html>");

        JScrollPane logScroll = new JScrollPane(logPane);
        logScroll.setBorder(null);
        logPanel.add(logScroll, BorderLayout.CENTER);

        mainCenterContainer.add(leftContainer, BorderLayout.CENTER);
        mainCenterContainer.add(logPanel, BorderLayout.EAST);

        // ==========================================
        // BÖLÜM 3: STRATEJİK ALT PANEL MİMARİSİ
        // ==========================================
        bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 12));
        bottomPanel.setBackground(new Color(12, 12, 16));
        bottomPanel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(40, 40, 50)));

        passTurnBtn = new createModernButton("SIRA DEVRET (PAS)", GOLD_COLOR);
        passTurnBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        passTurnBtn.setPreferredSize(new Dimension(200, 38));
        passTurnBtn.setEnabled(false); 

        bottomPanel.add(passTurnBtn);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(mainCenterContainer, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Geri sayım sayacını aktif et
        setupRealTimeTimer();

        // ==========================================
        // DİNLEYİCİ VE TETİKLEME YÖNETİMLERİ (LISTENERS)
        // ==========================================

        // Pas Geçme Eylemi
        passTurnBtn.addActionListener(e -> {
            String ajanIsim = (currentState == GameState.MAVI_AJAN) ? maviAjanIsim : kirmiziAjanIsim;
            appendColoredLog("🏳️ <b>" + ajanIsim + "</b> tur hakkı bitmeden pas dedi. Sıra karşı tarafa devredildi.", "#ffc107");
            playSynthSound(450, 200); 
            showInGameMessage("Tur pas geçildi! Sıra karşı takımın Casus Başına devrediliyor.", GOLD_COLOR);
            switchTurnToNextCasus();
        });

        // Kılavuz Penceresi Eylemi
        helpBtn.addActionListener(e -> {
            openHelpGuideDialog();
        });

        // Yeniden Başlatma Eylemi
        restartBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(frame, "Profilleri sıfırlamadan yeni bir oyun başlatmak istiyor musunuz?", "Yeni Oyun", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                resetWholeGameMatrix();
            }
        });

        // İpucu Kelimesi Gönderme ve Doğrulama Mekanizması
        submitClueBtn.addActionListener(e -> {
            String clue = clueField.getText().trim();
            if(clue.isEmpty()) {
                playSynthSound(150, 150); 
                showInGameMessage("Zaman sinyali boş gönderilemez!", NEON_RED);
                return;
            }
            
            // 🧠 YAPAY ZEKA DOĞRULAYICI SİSTEMİ (AI Hint Validator Kontrolü)
            String validationError = validateHintWithMatrixWords(clue);
            if (validationError != null) {
                playSynthSound(100, 400); 
                showInGameMessage(validationError, NEON_RED);
                appendColoredLog("⚠️ <b>İllegal İpucu Engellendi:</b> '" + clue.toUpperCase() + "' kural dışıdır.", "#ef233c");
                return;
            }
            
            playSynthSound(600, 100); 
            
            String logColor = (currentState == GameState.MAVI_CASUS) ? "#00b4d8" : "#ef233c";
            String senderIsim = (currentState == GameState.MAVI_CASUS) ? maviCasusIsim : kirmiziCasusIsim;
            
            appendColoredLog("📡 <b>" + senderIsim + "</b> ipucunu ağa yükledi: <b style='color:#ffc107;'>" + clue.toUpperCase() + "</b> (" + clueCount + ")", logColor);
            
            activeClueLabel.setText("AKTİF İPUCU: " + clue.toUpperCase() + " (" + clueCount + ")");
            clueField.setText("");
            currentTurnGuesses = 0; 
            resetTurnTimer(); 

            if (currentState == GameState.MAVI_CASUS) {
                currentState = GameState.MAVI_AJAN;
                turnLabel.setText("SIRA: " + maviAjanIsim.toUpperCase() + " (KART SEÇİLİYOR)");
                turnLabel.setForeground(NEON_BLUE);
            } else if (currentState == GameState.KIRMIZI_CASUS) {
                currentState = GameState.KIRMIZI_AJAN;
                turnLabel.setText("SIRA: " + kirmiziAjanIsim.toUpperCase() + " (KART SEÇİLİYOR)");
                turnLabel.setForeground(NEON_RED);
            }
            
            clueField.setEnabled(false);
            submitClueBtn.setEnabled(false);
            passTurnBtn.setEnabled(true); 
            frame.repaint();
            showInGameMessage("İpucu onaylandı! Sıra Saha Ajanında. Süre: 3 Dakika.", GOLD_COLOR);
        });

        appendColoredLog(" Kuantum ağı kararlı hale getirildi. Sistem Hazır.", "#2ec4b6");
        frame.setVisible(true);
    }

    /**
     * Gelişmiş Algoritmik İpucu Doğrulayıcı Metodu (AI Hint Validator)
     * String manipülasyon metotları ile tahtadaki kelimelerin bütünlüğünü korur.
     */
    private static String validateHintWithMatrixWords(String clue) {
        String lowerClue = clue.toLowerCase(java.util.Locale.forLanguageTag("tr"));
        
        for (Card card : activeCards) {
            if (!card.isRevealed()) {
                String cardTextLower = card.getText().toLowerCase(java.util.Locale.forLanguageTag("tr"));
                
                // 1. Durum: Kelimenin birebir aynısının kopyalanması engellenir
                if (lowerClue.equals(cardTextLower)) {
                    return "Kural İhlali: İpucu kelimesi, tahtadaki gizli bir kelimenin aynısı olamaz!";
                }
                
                // 2. Durum: Kelime köklerinin iç içe geçmesi engellenir
                if (cardTextLower.contains(lowerClue) || lowerClue.contains(cardTextLower)) {
                    return "Kural İhlali: İpucu, tahtadaki '" + card.getText() + "' kelimesiyle doğrudan kök bağlamı içeriyor!";
                }
            }
        }
        return null; 
    }

    // =============================================================================
    // MODAL PENCERELER VE PROFIL EKLEME PANELİ
    // =============================================================================
    private static void openPlayerNameDialog() {
        JDialog dialog = new JDialog((Frame)null, "Oyuncu Profilleri", true);
        dialog.setUndecorated(true); 
        dialog.setSize(400, 320);
        dialog.setLocationRelativeTo(null);
        
        JPanel p = new JPanel(new GridLayout(5, 2, 10, 15)); 
        p.setBackground(PANEL_BG);
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(NEON_BLUE, 2), " OYUNCU İSİMLERİNİ GİRİNİZ ", 0, 0, new Font("Segoe UI", Font.BOLD, 14), Color.WHITE));

        JLabel l1 = new JLabel("  Mavi Casus Başı:", SwingConstants.LEFT); l1.setForeground(NEON_BLUE);
        JTextField t1 = new JTextField("Ayberk");
        JLabel l2 = new JLabel("  Mavi Saha Ajanı:", SwingConstants.LEFT); l2.setForeground(NEON_BLUE);
        JTextField t2 = new JTextField("Emre");
        JLabel l3 = new JLabel("  Kırmızı Casus Başı:", SwingConstants.LEFT); l3.setForeground(NEON_RED);
        JTextField t3 = new JTextField("Elif");
        JLabel l4 = new JLabel("  Kırmızı Saha Ajanı:", SwingConstants.LEFT); l4.setForeground(NEON_RED);
        JTextField t4 = new JTextField("Gizem");

        JButton startBtn = new createModernButton("OPERASYONU BAŞLAT", NEON_GREEN);
        startBtn.addActionListener(e -> {
            if(!t1.getText().trim().isEmpty()) maviCasusIsim = t1.getText().trim();
            if(!t2.getText().trim().isEmpty()) maviAjanIsim = t2.getText().trim();
            if(!t3.getText().trim().isEmpty()) kirmiziCasusIsim = t3.getText().trim();
            if(!t4.getText().trim().isEmpty()) kirmiziAjanIsim = t4.getText().trim();
            dialog.dispose();
        });

        p.add(l1); p.add(t1); 
        p.add(l2); p.add(t2); 
        p.add(l3); p.add(t3); 
        p.add(l4); p.add(t4); 
        p.add(new JLabel("")); p.add(startBtn);
        dialog.add(p); 
        dialog.setVisible(true);
    }

    // =============================================================================
    // KRONOMETRE VE SAYAÇ THREAD SİSTEMLERİ
    // =============================================================================
    private static void setupRealTimeTimer() {
        gameTimer = new javax.swing.Timer(1000, e -> {
            remainingSeconds--;
            int min = remainingSeconds / 60; 
            int sec = remainingSeconds % 60;
            timerLabel.setText(String.format("%02d:%02d", min, sec));

            if (remainingSeconds <= 10) {
                timerLabel.setForeground(NEON_RED); 
                playSynthSound(900, 50); 
            } else { 
                timerLabel.setForeground(GOLD_COLOR); 
            }

            if (remainingSeconds <= 0) {
                playSynthSound(200, 500); 
                appendColoredLog("⏰ <b>Süre Sınırı Aşıldı!</b> Aktif oyuncunun tur süresi doldu.", "#ef233c");
                showInGameMessage("3 Dakikalık süre sınırı aşıldı! Sıra otomatik olarak karşı takıma geçti.", NEON_RED);
                switchTurnToNextCasus();
            }
        });
        gameTimer.start();
    }

    private static void resetTurnTimer() {
        remainingSeconds = 180; 
        timerLabel.setText("03:00");
    }

    // =============================================================================
    // AUDIO ENGINE - SAF FREKANS SENTEZLEYİCİSİ (KLASÖRDEN BAĞIMSIZ SES)
    // =============================================================================
    private static void playSynthSound(int hz, int msecs) {
        try {
            byte[] buf = new byte[msecs * 8];
            for (int i = 0; i < buf.length; i++) {
                double angle = i / (8000.0 / hz) * 2.0 * Math.PI;
                buf[i] = (byte) (Math.sin(angle) * 80.0);
            }
            AudioFormat af = new AudioFormat(8000f, 8, 1, true, false);
            SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
            sdl.open(af); 
            sdl.start(); 
            sdl.write(buf, 0, buf.length); 
            sdl.drain(); 
            sdl.close();
        } catch (Exception ex) { 
            System.out.println("Sistem ses kanalları meşgul veya sürücü bulunamadı."); 
        }
    }

    // =============================================================================
    // RESTART VE MATRIX SIFIRLAMA MOTORU
    // =============================================================================
    private static void resetWholeGameMatrix() {
        activeCards.clear();
        prepareAdvancedMatrixData(); 
        
        blueLeft = 4;
        redLeft = 4;
        currentTurnGuesses = 0;
        clueCount = 1;
        countValueLabel.setText("1");
        
        blueScoreLabel.setText("MAVİ KALAN: " + blueLeft);
        redScoreLabel.setText("KIRMIZI KALAN: " + redLeft);
        activeClueLabel.setText("AKTİF İPUCU: BEKLENİYOR...");
        
        currentState = GameState.MAVI_CASUS;
        turnLabel.setText("SIRA: " + maviCasusIsim.toUpperCase() + " (CASUS BAŞI)");
        turnLabel.setForeground(NEON_BLUE);
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, NEON_BLUE));
        submitClueBtn.setBackground(NEON_BLUE);

        clueField.setEnabled(true);
        submitClueBtn.setEnabled(true);
        clueField.setText("");
        passTurnBtn.setEnabled(false);

        resetTurnTimer();
        buildMatrixBoardUI(); 
        
        appendColoredLog("🔄 <b>MATRİX SIFIRLANDI!</b> Tüm kelimeler yenilendi, turnuva baştan başladı.", "#2ec4b6");
    }

    // =============================================================================
    // GRİD MATRIX KART BUTONLARI YERLEŞİM MANTIKLARI
    // =============================================================================
    private static void buildMatrixBoardUI() {
        boardPanel.removeAll();
        boardButtons.clear();
        for (Card card : activeCards) {
            GameCardButton btn = new GameCardButton("assets/kapali_kart.png", card.getText(), card);
            
            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    // HATA VEREN METHOD YERİNE DOĞRUDAN DURUM KONTROLÜ SAĞLANDI kanka
                    if (card.isRevealed() || (currentState == GameState.MAVI_CASUS || currentState == GameState.KIRMIZI_CASUS)) return;

                    TurnControl tc = new TurnControl();
                    card.reveal();
                    currentTurnGuesses++; 
                    String targetImagePath = "assets/kapali_kart.png";
                    String logRole = ""; String logColor = "#ffffff";

                    String aktifAjanIsim = (currentState == GameState.MAVI_AJAN) ? maviAjanIsim : kirmiziAjanIsim;

                    switch (card.getType()) {
                        case BLUE:
                            targetImagePath = "assets/mavi_ajan.png";
                            logRole = "MAVİ KART"; logColor = "#00b4d8";
                            blueLeft--; 
                            blueScoreLabel.setText("MAVİ KALAN: " + blueLeft);
                            if (currentState == GameState.MAVI_AJAN) { 
                                tc.turnContinues = true; 
                                playSynthSound(750, 150); 
                            } else { 
                                playSynthSound(250, 200); 
                            }
                            break;
                        case RED:
                            targetImagePath = "assets/kirmizi_ajan.png";
                            logRole = "KIRMIZI KART"; logColor = "#ef233c";
                            redLeft--; 
                            redScoreLabel.setText("KIRMIZI KALAN: " + redLeft);
                            if (currentState == GameState.KIRMIZI_AJAN) { 
                                tc.turnContinues = true; 
                                playSynthSound(750, 150); 
                            } else { 
                                playSynthSound(250, 200); 
                            }
                            break;
                        case EMPTY:
                            targetImagePath = "assets/bos_sivil.png";
                            logRole = "SİVİL (NÖTR)"; logColor = "#e2e8f0"; 
                            playSynthSound(400, 200); 
                            break;
                        case BLACK:
                            targetImagePath = "assets/siyah_kart.png";
                            btn.startFlipAnimation(targetImagePath, "");
                            playSynthSound(80, 800); 
                            
                            String winnerIsim = (currentState == GameState.MAVI_AJAN) ? kirmiziAjanIsim : maviAjanIsim;
                            appendColoredLog("🚨 <b>" + aktifAjanIsim + "</b> SİYAH KATIL KARTI çevirdi!", "#ff0055");
                            
                            Timer t = new Timer(800, ev -> {
                                showInGameMessage(aktifAjanIsim.toUpperCase() + " SİYAH KATİL KARTINI ÇEVİRDİ! KAZANAN: " + winnerIsim.toUpperCase(), NEON_RED);
                                resetWholeGameMatrix(); 
                            });
                            t.setRepeats(false); t.start();
                            return;
                    }

                    appendColoredLog("🃏 <b>" + aktifAjanIsim + "</b> kart açtı: <b>" + card.getText() + "</b> → <span style='color:" + logColor + ";'>" + logRole + "</span>", "#aebbc7");
                    btn.startFlipAnimation(targetImagePath, "");

                    // Galibiyet Durum Analizleri
                    if (blueLeft <= 0) {
                        playSynthSound(1000, 600);
                        showInGameMessage("TEBRİKLER! MAVİ TAKIM MAÇI KAZANDI!", NEON_BLUE);
                        resetWholeGameMatrix(); return;
                    } else if (redLeft <= 0) {
                        playSynthSound(1000, 600);
                        showInGameMessage("TEBRİKLER! KIRMIZI TAKIM MAÇI KAZANDI!", NEON_RED);
                        resetWholeGameMatrix(); return;
                    }

                    Timer turnTimer = new Timer(1000, ev -> {
                        if (currentTurnGuesses >= clueCount) {
                            appendColoredLog("🔄 Hak sınırına ulaşıldı. Sıra karşı takıma geçiyor.", "#ffc107");
                            showInGameMessage("Belirlenen seçim sayısı sınırına ulaşıldı! Sıra karşı takıma geçiyor.", NEON_GREEN);
                            switchTurnToNextCasus();
                        } else {
                            if (!tc.turnContinues) {
                                appendColoredLog("❌ Yanlış takım kartı veya sivil açıldı. Sıra devrediliyor.", "#ef233c");
                                showInGameMessage("Hatalı Kart Açıldı! Sıra karşı takıma geçiyor.", NEON_RED);
                                switchTurnToNextCasus();
                            } else {
                                int remaining = clueCount - currentTurnGuesses;
                                showInGameMessage("Doğru Kart! Çevirmeniz gereken kalan kart sayısı: " + remaining, GOLD_COLOR);
                            }
                        }
                    });
                    turnTimer.setRepeats(false); turnTimer.start();
                }
            });
            
            boardButtons.add(btn); 
            boardPanel.add(btn);
        }
        boardPanel.revalidate(); 
        boardPanel.repaint();
    }

    private static void switchTurnToNextCasus() {
        clueField.setEnabled(true);
        submitClueBtn.setEnabled(true);
        clueField.setText("");
        activeClueLabel.setText("AKTİF İPUCU: BEKLENİYOR...");
        currentTurnGuesses = 0; 
        passTurnBtn.setEnabled(false); 
        resetTurnTimer();

        if (currentState == GameState.MAVI_AJAN || currentState == GameState.MAVI_CASUS) {
            currentState = GameState.KIRMIZI_CASUS;
            turnLabel.setText("SIRA: " + kirmiziCasusIsim.toUpperCase() + " (KARTLARI ŞİFRELE)");
            turnLabel.setForeground(NEON_RED);
            topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, NEON_RED));
            submitClueBtn.setBackground(NEON_RED);
        } else {
            currentState = GameState.MAVI_CASUS;
            turnLabel.setText("SIRA: " + maviCasusIsim.toUpperCase() + " (KARTLARI ŞİFRELE)");
            turnLabel.setForeground(NEON_BLUE);
            topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, NEON_BLUE));
            submitClueBtn.setBackground(NEON_BLUE);
        }
        frame.repaint();
    }

    private static void appendColoredLog(String htmlText, String hexColor) {
        try {
            HTMLDocument doc = (HTMLDocument) logPane.getDocument();
            HTMLEditorKit kit = (HTMLEditorKit) logPane.getEditorKit();
            String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
            String logLine = "<div style='margin-bottom:4px; color:" + hexColor + ";'>"
                           + "<span style='color:#6c757d;'>[" + timeStamp + "]</span> " 
                           + htmlText + "</div>";
            kit.insertHTML(doc, doc.getLength(), logLine, 0, 0, null);
            logPane.setCaretPosition(doc.getLength()); 
        } catch (Exception ex) { 
            System.out.println("Log alanına veri yazma hatası."); 
        }
    }

    // ==========================================
    // GELİŞMİŞ HTML DESTEKLİ GÖRKEMLİ REHBER POPUPU
    // ==========================================
    private static void openHelpGuideDialog() {
        JDialog helpDialog = new JDialog(frame, "Codenames Kılavuzu", true);
        helpDialog.setUndecorated(true);
        helpDialog.setSize(520, 420); 
        helpDialog.setLocationRelativeTo(frame); 
        helpDialog.setLayout(new BorderLayout());
        
        JPanel p = new JPanel(new BorderLayout()); 
        p.setBackground(PANEL_BG); 
        p.setBorder(BorderFactory.createLineBorder(GOLD_COLOR, 2));
        
        JTextPane guidePane = new JTextPane();
        guidePane.setEditable(false);
        guidePane.setContentType("text/html");
        guidePane.setBackground(new Color(16, 16, 24));
        guidePane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        String htmlRules = "<html><body style='font-family:Segoe UI,sans-serif; color:#e2e8f0; margin:0; padding:0;'>"
                + "<h2 style='color:#ffc107; text-align:center; margin-top:0;'>⚔️ TOURNAMENT OPERASYON REHBERİ</h2>"
                + "<hr style='border:0; border-top:1px solid #3c4150; margin-bottom:12px;'>"
                + "<b>1. MATRİX DAĞILIMI:</b><br>"
                + "Tahtada toplam 12 siber kart bulunur: <span style='color:#00b4d8;'>4 Mavi</span>, "
                + "<span style='color:#ef233c;'>4 Kırmızı</span>, 3 Sivil ve <span style='color:#ff0055;'>1 Siyah Katil</span> kart.<br><br>"
                + "<b>2. YAPAY ZEKA DOĞRULAYICI (AI Validator):</b><br>"
                + "Casus Başı ipucu girerken, kuantum doğrulayıcı çalışır. Tahtadaki kapalı kelimelerle "
                + "birebir aynı olan veya kelime kökünü ihlal eden kopyalar <u>otomatik engellenir</u>.<br><br>"
                + "<b>3. SÜRE VE HAK SINIRI:</b><br>"
                + "Her tur için işlem süresi tam <b>3 dakikadır</b>. Saha Ajanı, Casus Başının belirttiği sayı "
                + "kadar seçim hakkına sahiptir. Yanlış kart açılırsa sıra anında karşı takıma devredilir.<br><br>"
                + "<b>4. STRATEJİK PAS EYLEMİ:</b><br>"
                + "Saha Ajanları, katil karta basma riski hissettikleri an alt paneldeki <i>'SIRA DEVRET (PAS)'</i> "
                + "butonunu kullanarak operasyon sırasını güvenle karşı tarafa bırakabilirler."
                + "</body></html>";
                
        guidePane.setText(htmlRules);
        
        JButton close = new createModernButton("ANLAŞILDI, SAHAYA DÖN", GOLD_COLOR); 
        close.setPreferredSize(new Dimension(0, 40));
        close.addActionListener(ev -> helpDialog.dispose());
        
        p.add(new JScrollPane(guidePane), BorderLayout.CENTER); 
        p.add(close, BorderLayout.SOUTH);
        helpDialog.add(p); 
        helpDialog.setVisible(true);
    }

    private static void showInGameMessage(String msg, Color glowColor) {
        JDialog dialog = new JDialog(frame, "Oyun Bildirimi", true);
        dialog.setUndecorated(true); 
        dialog.setSize(440, 110); 
        dialog.setLocationRelativeTo(frame);
        
        JPanel panel = new JPanel(new BorderLayout()); 
        panel.setBackground(PANEL_BG); 
        panel.setBorder(BorderFactory.createLineBorder(glowColor, 2));
        
        JLabel textLabel = new JLabel(msg, SwingConstants.CENTER); 
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 13)); 
        textLabel.setForeground(Color.WHITE);
        
        JButton closeBtn = new createModernButton("DEVAM ET", glowColor); 
        closeBtn.addActionListener(e -> dialog.dispose());
        
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.CENTER)); 
        btnWrap.setOpaque(false); 
        btnWrap.add(closeBtn);
        
        panel.add(textLabel, BorderLayout.CENTER); 
        panel.add(btnWrap, BorderLayout.SOUTH);
        dialog.add(panel); 
        dialog.setVisible(true);
    }

    // =============================================================================
    // CUSTOM GRAFİK KOMPONENTLERİ VE KONTÜRLÜ METİN ÇİZİMLERİ (60 FPS MOTORU)
    // =============================================================================
    static class GameCardButton extends JComponent {
        private Image currentImg; String cardText; Color borderColor = null; boolean isRevealed = false; Card cardData; double scaleX = 1.0; boolean isHovered = false;
        public GameCardButton(String imgPath, String text, Card card) {
            this.currentImg = new ImageIcon(imgPath).getImage(); this.cardText = text; this.cardData = card; setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
            addMouseListener(new MouseAdapter() {
                // HATA VEREN METHOD YERİNE DOĞRUDAN DURUM KONTROLÜ ENJEKTE EDİLDİ kanka
                @Override public void mouseEntered(MouseEvent e) { if(!isRevealed && !(currentState == GameState.MAVI_CASUS || currentState == GameState.KIRMIZI_CASUS)) { isHovered = true; repaint(); } }
                @Override public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });
        }
        public void setSelectedBorder(Color color) { this.borderColor = color; repaint(); }
        public void startFlipAnimation(String newImgPath, String newText) {
            try {
                for (int i = 10; i >= 0; i--) { scaleX = i / 10.0; repaint(); Thread.sleep(16); }
                this.isRevealed = true; this.currentImg = new ImageIcon(newImgPath).getImage(); this.cardText = newText;
                for (int i = 0; i <= 10; i++) { scaleX = i / 10.0; repaint(); Thread.sleep(16); }
            } catch (InterruptedException ex) { 
                System.out.println("Kart animasyon kanalı kesildi."); 
            }
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g); Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int animatedWidth = (int) (getWidth() * scaleX); int xOffset = (getWidth() - animatedWidth) / 2;
            
            // HATA VEREN METHOD YERİNE DOĞRUDAN DURUM KONTROLÜ ENJEKTE EDİLDİ kanka
            if (!isRevealed && (currentState == GameState.MAVI_CASUS || currentState == GameState.KIRMIZI_CASUS)) {
                String spyImgPath = "assets/kapali_kart.png";
                switch (cardData.getType()) {
                    case BLUE: spyImgPath = "assets/mavi_ajan.png"; break;
                    case RED: spyImgPath = "assets/kirmizi_ajan.png"; break;
                    case EMPTY: spyImgPath = "assets/bos_sivil.png"; break;
                    case BLACK: spyImgPath = "assets/siyah_kart.png"; break;
                }
                g2.drawImage(new ImageIcon(spyImgPath).getImage(), xOffset, 0, animatedWidth, getHeight(), this); drawCardText(g2, xOffset, animatedWidth);
            } else {
                g2.drawImage(currentImg, xOffset, 0, animatedWidth, getHeight(), this); if (!isRevealed) drawCardText(g2, xOffset, animatedWidth);
            }
            if (isHovered && scaleX > 0.8) { g2.setColor(new Color(255, 255, 255, 35)); g2.fillRect(xOffset, 0, animatedWidth, getHeight()); }
            if (borderColor != null) { g2.setColor(borderColor); g2.setStroke(new BasicStroke(4)); g2.drawRect(xOffset + 2, 2, animatedWidth - 4, getHeight() - 4); }
        }
        
        /**
         * 🎯 KONTÜRLÜ OKUNAKLI FONT MOTORU METODU
         * İçi jilet beyazı parlayan, dışı ise 8 yönlü siyah maskeyle çevrilen yazı çizimi.
         */
        private void drawCardText(Graphics2D g2, int xOffset, int animatedWidth) {
            if (!cardText.isEmpty() && scaleX > 0.3) {
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16)); 
                FontMetrics fm = g2.getFontMetrics();
                
                int tx = xOffset + (animatedWidth - fm.stringWidth(cardText)) / 2; 
                int ty = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                
                // 1. Kademe Çizim: Siyah Dış Hat Gölgelendirmesi (Outline Kontürü kanka)
                g2.setColor(new Color(15, 15, 15)); 
                g2.drawString(cardText, tx - 1, ty - 1);
                g2.drawString(cardText, tx + 1, ty - 1);
                g2.drawString(cardText, tx - 1, ty + 1);
                g2.drawString(cardText, tx + 1, ty + 1);
                g2.drawString(cardText, tx - 2, ty);
                g2.drawString(cardText, tx + 2, ty);
                g2.drawString(cardText, tx, ty - 2);
                g2.drawString(cardText, tx, ty + 2);
                
                // 2. Kademe Çizim: Merkez Beyaz İç Dolgu
                g2.setColor(new Color(255, 255, 255)); 
                g2.drawString(cardText, tx, ty);
            }
        }
    }

    static class createModernButton extends JButton {
        private Color baseColor;
        public createModernButton(String text, Color baseColor) {
            super(text); 
            this.baseColor = baseColor; 
            setFont(new Font("Segoe UI", Font.BOLD, 13)); 
            setForeground(Color.WHITE); 
            setBackground(baseColor); 
            setContentAreaFilled(false); 
            setFocusPainted(false); 
            setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { setBackground(baseColor.brighter()); }
                @Override public void mouseExited(MouseEvent e) { setBackground(baseColor); }
            });
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create(); 
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (!isEnabled()) g2.setColor(Color.DARK_GRAY); else g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8); 
            g2.dispose(); 
            super.paintComponent(g);
        }
    }

    // ==========================================
    // DATA INJECTION MODULE (GENİŞ VERİ ENJEKSİYONU)
    // ==========================================
    private static void prepareAdvancedMatrixData() {
        ArrayList<String> pool = new ArrayList<>(Arrays.asList(
            "İtalya", "Japonya", "Şemsiye", "Sandalye", "Gramofon", "Ayna",
            "Pusula", "Kaktüs", "Dantel", "Teleskop", "Madalya", "Piyano",
            "Mısır", "Kanada", "Dolap", "Gözlük", "Plak", "Gaz lambası",
            "Londra", "Kravat", "Bavul", "Heykel", "Harita", "Köstekli Saat",
            "Şapka", "Tablo", "Akçe", "Daktilo", "Korsan", "Pusula",
            "Venedik", "Porselen", "Kalkan", "Parşömen", "Kandil", "Mürekkep"
        ));
        Collections.shuffle(pool); 

        activeCards.add(new Card(pool.get(0), CardType.BLUE));
        activeCards.add(new Card(pool.get(1), CardType.BLUE));
        activeCards.add(new Card(pool.get(2), CardType.BLUE));
        activeCards.add(new Card(pool.get(3), CardType.BLUE));

        activeCards.add(new Card(pool.get(4), CardType.RED));
        activeCards.add(new Card(pool.get(5), CardType.RED));
        activeCards.add(new Card(pool.get(6), CardType.RED));
        activeCards.add(new Card(pool.get(7), CardType.RED));

        activeCards.add(new Card(pool.get(8), CardType.EMPTY));
        activeCards.add(new Card(pool.get(9), CardType.EMPTY));
        activeCards.add(new Card(pool.get(10), CardType.EMPTY));

        activeCards.add(new Card(pool.get(11), CardType.BLACK));

        Collections.shuffle(activeCards); 
    }
}