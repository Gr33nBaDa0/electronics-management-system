package gui;

import entity.TaiKhoan;
import util.GiaoDien;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** HÆ°á»›ng dáº«n thao tÃ¡c; chá»‰ Ä‘á»c ná»™i dung, khÃ´ng truy cáº­p hoáº·c thay Ä‘á»•i dá»¯ liá»‡u. */
public class TroGiup_GUI extends JPanel implements DocumentListener {
    private static final Color PRIMARY = GiaoDien.PRIMARY,
            BACKGROUND = GiaoDien.BACKGROUND,
            SURFACE = GiaoDien.SURFACE,
            TEXT = GiaoDien.TEXT,
            MUTED = GiaoDien.MUTED;

    private final Map<String, String> huongDan = new LinkedHashMap<>();
    private final DefaultListModel<String> model = new DefaultListModel<>();
    private final JList<String> danhMuc = new JList<>(model);
    private final JTextField tim = new JTextField(18);
    private final JButton btnBoLoc = GiaoDien.nut("Bá» lá»c");
    private final JLabel lblSoMuc = new JLabel(), lblChuDe = new JLabel();
    private final JTextArea noiDung = new JTextArea();

    public TroGiup_GUI(TaiKhoan tk) {
        boolean quanLy = tk != null && "ADMIN".equals(tk.getQuyen());
        taoHuongDan(quanLy);
        GiaoDien.boCuc(this);

        JPanel dau = GiaoDien.doc(
                GiaoDien.tieuDe("HÆ°á»›ng dáº«n sá»­ dá»¥ng GreenSmart"),
                GiaoDien.ghiChu("Chá»n cÃ´ng viá»‡c bÃªn trÃ¡i Ä‘á»ƒ xem tá»«ng bÆ°á»›c. Báº¡n Ä‘ang xem hÆ°á»›ng dáº«n cho "
                        + (quanLy ? "Quáº£n lÃ½." : "BÃ¡n hÃ ng.")));
        add(dau, BorderLayout.NORTH);

        tim.setToolTipText("TÃ¬m theo ná»™i dung, vÃ­ dá»¥: má»Ÿ ca, QR, in hÃ³a Ä‘Æ¡n. CÃ³ thá»ƒ gÃµ khÃ´ng dáº¥u.");
        JPanel timKiem = new JPanel(new BorderLayout(6, 6));
        timKiem.setOpaque(false);
        timKiem.add(new JLabel("TÃ¬m hÆ°á»›ng dáº«n"), BorderLayout.NORTH);
        timKiem.add(tim, BorderLayout.CENTER);
        timKiem.add(btnBoLoc, BorderLayout.EAST);
        danhMuc.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        danhMuc.setFixedCellHeight(40);
        danhMuc.setVisibleRowCount(10);
        JPanel trai = new JPanel(new BorderLayout(0, 10));
        trai.setBackground(SURFACE);
        trai.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        trai.add(timKiem, BorderLayout.NORTH);
        trai.add(GiaoDien.cuon(danhMuc), BorderLayout.CENTER);
        trai.add(lblSoMuc, BorderLayout.SOUTH);
        trai.setMinimumSize(new Dimension(270, 200));

        noiDung.setEditable(false);
        noiDung.setLineWrap(true);
        noiDung.setWrapStyleWord(true);
        noiDung.setMargin(new Insets(12, 0, 12, 10));
        JScrollPane cuon = new JScrollPane(noiDung);
        cuon.setBorder(null);
        cuon.getVerticalScrollBar().setUnitIncrement(20);
        JPanel phai = new JPanel(new BorderLayout(0, 8));
        phai.setBackground(SURFACE);
        phai.setBorder(BorderFactory.createEmptyBorder(18, 22, 14, 18));
        phai.add(lblChuDe, BorderLayout.NORTH);
        phai.add(cuon, BorderLayout.CENTER);
        phai.add(GiaoDien.ghiChu("Ctrl+F: tÃ¬m hÆ°á»›ng dáº«n Â· F1: má»Ÿ Trá»£ giÃºp Â· Shift+F1: báº£ng phÃ­m táº¯t"), BorderLayout.SOUTH);
        phai.setMinimumSize(new Dimension(380, 200));

        JSplitPane chia = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, trai, phai);
        chia.setResizeWeight(0);
        chia.setDividerLocation(330);
        chia.setDividerSize(10);
        chia.setBorder(null);
        chia.setBackground(BACKGROUND);
        add(chia, BorderLayout.CENTER);
        GiaoDien.trangTri(this);
        lblChuDe.setFont(GiaoDien.phong(Font.BOLD, 21));
        lblChuDe.setForeground(PRIMARY);
        lblSoMuc.setForeground(MUTED);
        noiDung.setFont(GiaoDien.phong(Font.PLAIN, 15));
        noiDung.setBackground(SURFACE);
        noiDung.setForeground(TEXT);
        danhMuc.setFont(GiaoDien.phong(Font.PLAIN, 14));
        danhMuc.setSelectionBackground(PRIMARY);
        danhMuc.setSelectionForeground(Color.WHITE);
        danhMuc.setBackground(SURFACE);
        danhMuc.setForeground(TEXT);
        tim.getAccessibleContext().setAccessibleName("TÃ¬m hÆ°á»›ng dáº«n sá»­ dá»¥ng");
        danhMuc.getAccessibleContext().setAccessibleName("Danh má»¥c cÃ´ng viá»‡c");
        noiDung.getAccessibleContext().setAccessibleName("CÃ¡c bÆ°á»›c thao tÃ¡c");

        tim.getDocument().addDocumentListener(this);
        btnBoLoc.addActionListener(e -> {
            tim.setText("");
            locChuDe();
            tim.requestFocusInWindow();
        });
        danhMuc.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) hienChuDe();
        });
        GiaoDien.phimTat(this, "ctrl F", () -> {
            tim.requestFocusInWindow();
            tim.selectAll();
        });
        locChuDe();
    }

    private void them(String ten, String... dong) {
        huongDan.put(ten, String.join("\n\n", dong));
    }

    private void taoHuongDan(boolean quanLy) {
        them("Báº¯t Ä‘áº§u sá»­ dá»¥ng",
                "Má»ž á»ž ÄÃ‚U: ÄÄƒng nháº­p â†’ Dashboard.",
                "1. ÄÄƒng nháº­p báº±ng tÃ i khoáº£n Ä‘Æ°á»£c cáº¥p. TÃªn ngÆ°á»i dÃ¹ng vÃ  vai trÃ² hiá»ƒn thá»‹ trÃªn thanh menu bÃªn trÃ¡i.",
                quanLy ? "2. TrÃªn Dashboard quáº£n lÃ½, xem phiáº¿u chá» duyá»‡t, ca cáº§n xá»­ lÃ½ vÃ  sáº£n pháº©m cáº§n chÃº Ã½ trÆ°á»›c khi má»Ÿ tá»«ng chá»©c nÄƒng."
                        : "2. TrÃªn Dashboard nhÃ¢n viÃªn, xem lá»‹ch ca, tráº¡ng thÃ¡i ca, hÃ³a Ä‘Æ¡n Ä‘ang xá»­ lÃ½ vÃ  sáº£n pháº©m cáº§n chÃº Ã½.",
                "3. TrÆ°á»›c khi bÃ¡n hÃ ng, má»Ÿ Ä‘Ãºng ca Ä‘Æ°á»£c phÃ¢n cÃ´ng cho mÃ¬nh. Náº¿u chÆ°a cÃ³ lá»‹ch ca, liÃªn há»‡ Quáº£n lÃ½ Ä‘á»ƒ Ä‘Æ°á»£c phÃ¢n cÃ´ng.",
                "4. Chá»n chá»©c nÄƒng á»Ÿ menu bÃªn trÃ¡i. Khi cáº§n hÆ°á»›ng dáº«n, báº¥m Trá»£ giÃºp á»Ÿ cuá»‘i menu hoáº·c nháº¥n F1.",
                "CÃCH Äá»ŒC: Chá»n tÃªn cÃ´ng viá»‡c bÃªn trÃ¡i. Ã” TÃ¬m hÆ°á»›ng dáº«n nháº­n cáº£ tá»« cÃ³ dáº¥u vÃ  khÃ´ng dáº¥u, vÃ­ dá»¥: mo ca, QR, VAT, in hoa don.");
        them("Má»Ÿ ca lÃ m viá»‡c",
                "Má»ž á»ž ÄÃ‚U: Menu Ca lÃ m viá»‡c hoáº·c nÃºt Má»Ÿ ca trÃªn Dashboard nhÃ¢n viÃªn.",
                "1. Trong Ca lÃ m viá»‡c, báº¥m Táº£i láº¡i / lá»c. CÃ³ thá»ƒ nháº­p mÃ£ nhÃ¢n viÃªn hoáº·c ngÃ y yyyy-MM-dd vÃ o Ã´ MÃ£ NV / ngÃ y.",
                "2. Chá»n dÃ²ng phÃ¢n cÃ´ng cá»§a chÃ­nh mÃ¬nh, Ä‘Ãºng ngÃ y lÃ m viá»‡c, cÃ³ tráº¡ng thÃ¡i ChÆ°a má»Ÿ.",
                "3. Báº¥m Má»Ÿ ca, nháº­p tiá»n quá»¹ Ä‘áº§u ca báº±ng sá»‘ khÃ´ng phÃ¢n cÃ¡ch, vÃ­ dá»¥ 1000000. Nháº­p 0 náº¿u thá»±c táº¿ khÃ´ng cÃ³ tiá»n quá»¹ Ä‘áº§u.",
                "4. XÃ¡c nháº­n vÃ  kiá»ƒm tra dÃ²ng chuyá»ƒn sang Äang má»Ÿ. Sau Ä‘Ã³ má»Ÿ BÃ¡n hÃ ng POS Ä‘á»ƒ láº­p hÃ³a Ä‘Æ¡n.",
                "Náº¾U CHÆ¯A Má»ž ÄÆ¯á»¢C: Khi Ä‘Æ°á»£c bÃ¡o cÃ²n ca Ä‘ang má»Ÿ, chá»n bá»™ lá»c Äang má»Ÿ vÃ  kiá»ƒm tra cáº£ cÃ¡c ca ngÃ y trÆ°á»›c. Äá»‘i soÃ¡t / Ä‘Ã³ng ca cÅ© cá»§a mÃ¬nh trÆ°á»›c khi má»Ÿ ca má»›i. KhÃ´ng cÃ³ phÃ¢n cÃ´ng thÃ¬ liÃªn há»‡ Quáº£n lÃ½.");
        them("Äá»‘i soÃ¡t vÃ  Ä‘Ã³ng ca",
                "Má»ž á»ž ÄÃ‚U: Menu Ca lÃ m viá»‡c.",
                "1. HoÃ n thÃ nh cÃ¡c giao dá»‹ch trong ca, kiá»ƒm Ä‘áº¿m tiá»n máº·t thá»±c táº¿ táº¡i quáº§y.",
                "2. Chá»n dÃ²ng ca cá»§a mÃ¬nh Ä‘ang Äang má»Ÿ, báº¥m Äá»‘i soÃ¡t / Ä‘Ã³ng ca.",
                "3. Äá»c quá»¹ lÃ½ thuyáº¿t Ä‘Æ°á»£c hiá»ƒn thá»‹ vÃ  nháº­p sá»‘ tiá»n thá»±c Ä‘áº¿m. Tiá»n chuyá»ƒn khoáº£n khÃ´ng Ä‘Æ°á»£c tÃ­nh lÃ  tiá»n máº·t trong kÃ©t.",
                "4. Náº¿u cÃ³ chÃªnh lá»‡ch, nháº­p giáº£i trÃ¬nh theo yÃªu cáº§u. Kiá»ƒm tra láº¡i rá»“i xÃ¡c nháº­n Ä‘Ã³ng ca.",
                "Káº¾T QUáº¢: Ca chuyá»ƒn sang ÄÃ£ Ä‘Ã³ng vÃ  khÃ´ng tiáº¿p tá»¥c sá»­a giao dá»‹ch cá»§a ca Ä‘Ã³. Má»Ÿ ca Ä‘Æ°á»£c phÃ¢n cÃ´ng tiáº¿p theo trÆ°á»›c khi bÃ¡n tiáº¿p.");
        them("BÃ¡n hÃ ng vÃ  giá» hÃ ng",
                "Má»ž á»ž ÄÃ‚U: Menu BÃ¡n hÃ ng POS. Cáº§n cÃ³ ca cá»§a mÃ¬nh Ä‘ang má»Ÿ.",
                "1. Nháº­p mÃ£ hoáº·c tÃªn sáº£n pháº©m vÃ o Ã´ tÃ¬m kiáº¿m. Báº¥m + ThÃªm trÃªn tháº»; á»Ÿ cháº¿ Ä‘á»™ Xem báº£ng cÃ³ thá»ƒ báº¥m Ä‘Ãºp sáº£n pháº©m.",
                "2. Trong giá», dÃ¹ng nÃºt âˆ’ / + Ä‘á»ƒ Ä‘á»•i sá»‘ lÆ°á»£ng, nÃºt Ã— Ä‘á»ƒ bá» má»™t dÃ²ng. Kiá»ƒm tra sá»‘ lÆ°á»£ng vÃ  tá»“n trÆ°á»›c khi thanh toÃ¡n.",
                "3. TÃ¬m khÃ¡ch báº±ng sá»‘ Ä‘iá»‡n thoáº¡i vÃ  chá»n Ä‘Ãºng khÃ¡ch; Ä‘á»ƒ KhÃ¡ch vÃ£ng lai náº¿u khÃ¡ch khÃ´ng cung cáº¥p thÃ´ng tin. Chá»n khuyáº¿n mÃ£i vÃ  Ä‘iá»ƒm sá»­ dá»¥ng náº¿u cÃ³.",
                "4. Kiá»ƒm tra tiá»n hÃ ng, giáº£m khuyáº¿n mÃ£i, tiá»n quy Ä‘á»•i Ä‘iá»ƒm, VAT vÃ  tá»•ng thanh toÃ¡n.",
                "5. Báº¥m LÆ°u Ä‘Æ¡n náº¿u chÆ°a thanh toÃ¡n; hoáº·c Tiá»n máº·t Â· F9 / QR Â· F10 Ä‘á»ƒ má»Ÿ há»™p thanh toÃ¡n.",
                "LÆ¯U Ã: LÆ°u Ä‘Æ¡n chá»‰ táº¡o hÃ³a Ä‘Æ¡n ChÆ°a thanh toÃ¡n. Báº¥m ÄÆ¡n má»›i sáº½ bá» giá» chÆ°a lÆ°u; lÆ°u Ä‘Æ¡n cáº§n giá»¯ trÆ°á»›c khi táº¡o Ä‘Æ¡n khÃ¡c.");
        them("KhÃ¡ch hÃ ng vÃ  Ä‘iá»ƒm",
                "Má»ž á»ž ÄÃ‚U: Menu KhÃ¡ch hÃ ng; chá»n khÃ¡ch trÃªn BÃ¡n hÃ ng POS khi bÃ¡n.",
                "1. Nháº­p tÃªn hoáº·c sá»‘ Ä‘iá»‡n thoáº¡i rá»“i báº¥m TÃ¬m Ä‘á»ƒ kiá»ƒm tra khÃ¡ch Ä‘Ã£ cÃ³ trong danh sÃ¡ch.",
                "2. Khi thÃªm khÃ¡ch, báº¥m Táº¡o má»›i, nháº­p thÃ´ng tin vÃ  báº¥m LÆ°u. MÃ£ khÃ¡ch tá»± hiá»‡n; khÃ´ng tá»± nháº­p mÃ£ khÃ¡c. Sá»‘ Ä‘iá»‡n thoáº¡i pháº£i há»£p lá»‡ vÃ  khÃ´ng trÃ¹ng khÃ¡ch khÃ¡c.",
                "3. Muá»‘n sá»­a, chá»n khÃ¡ch trong báº£ng, cáº­p nháº­t thÃ´ng tin vÃ  báº¥m LÆ°u. DÃ¹ng Lá»‹ch sá»­ mua Ä‘á»ƒ tra hÃ³a Ä‘Æ¡n cá»§a khÃ¡ch.",
                "4. Táº¡i POS, chá»n Ä‘Ãºng khÃ¡ch trÆ°á»›c khi nháº­p Äiá»ƒm sá»­ dá»¥ng. Nháº­p sá»‘ Ä‘iá»ƒm, khÃ´ng nháº­p sá»‘ tiá»n; app kiá»ƒm tra sá»‘ dÆ° vÃ  giá»›i háº¡n Ä‘Æ°á»£c dÃ¹ng.",
                "Káº¾T QUáº¢: Äiá»ƒm chá»‰ Ä‘Æ°á»£c Ä‘iá»u chá»‰nh khi thanh toÃ¡n hoáº·c hoÃ n táº¥t Ä‘á»•i tráº£. KhÃ´ng sá»­a trá»±c tiáº¿p sá»‘ dÆ° Ä‘iá»ƒm trong thÃ´ng tin khÃ¡ch.");
        them("Thanh toÃ¡n tiá»n máº·t",
                "Má»ž á»ž ÄÃ‚U: BÃ¡n hÃ ng POS â†’ Tiá»n máº·t Â· F9.",
                "1. Kiá»ƒm tra giá» hÃ ng, khÃ¡ch, khuyáº¿n mÃ£i, Ä‘iá»ƒm vÃ  tá»•ng tiá»n trÆ°á»›c khi báº¥m Tiá»n máº·t Â· F9.",
                "2. Trong há»™p Thanh toÃ¡n, chá»n Tiá»n máº·t vÃ  nháº­p Tiá»n khÃ¡ch Ä‘Æ°a, vÃ­ dá»¥ 1000000. Kiá»ƒm tra thÃ´ng bÃ¡o cÃ²n thiáº¿u hoáº·c tiá»n tráº£ láº¡i.",
                "3. Khi Ä‘Ã£ nháº­n Ä‘á»§ tiá»n, báº¥m Thanh toÃ¡n & xuáº¥t hÃ³a Ä‘Æ¡n. Má»™t láº§n báº¥m hoÃ n táº¥t giao dá»‹ch vÃ  táº¡o PDF.",
                "4. Tráº£ tiá»n thá»«a cho khÃ¡ch theo sá»‘ app hiá»ƒn thá»‹. PDF tá»± lÆ°u táº¡i D:\\HoaDonGreenSmart vÃ  Ä‘Æ°á»£c má»Ÿ Ä‘á»ƒ xem.",
                "Náº¾U Lá»–I XUáº¤T PDF: Náº¿u thÃ´ng bÃ¡o Ä‘Ã£ thanh toÃ¡n thÃ nh cÃ´ng, vÃ o HÃ³a Ä‘Æ¡n Ä‘á»ƒ In hÃ³a Ä‘Æ¡n láº¡i. KhÃ´ng thá»±c hiá»‡n thanh toÃ¡n thÃªm láº§n ná»¯a Ä‘á»ƒ láº¥y PDF.");
        them("Thanh toÃ¡n báº±ng QR",
                "Má»ž á»ž ÄÃ‚U: BÃ¡n hÃ ng POS â†’ QR Â· F10.",
                "1. Kiá»ƒm tra tá»•ng tiá»n vÃ  má»Ÿ há»™p Thanh toÃ¡n, chá»n Chuyá»ƒn khoáº£n QR náº¿u cáº§n.",
                "2. Báº¥m Hiá»‡n QR, má»i khÃ¡ch quÃ©t mÃ£ vÃ  Ä‘á»‘i chiáº¿u sá»‘ tiá»n, ngÃ¢n hÃ ng, tÃ i khoáº£n nháº­n tiá»n cá»§a cá»­a hÃ ng.",
                "3. Kiá»ƒm tra tiá»n Ä‘Ã£ vÃ o tÃ i khoáº£n cá»­a hÃ ng. Chá»‰ khi Ä‘Ã£ nháº­n tiá»n, báº¥m XÃC NHáº¬N: TÃ”I ÄÃƒ NHáº¬N TIá»€N. App hoÃ n táº¥t thanh toÃ¡n vÃ  xuáº¥t PDF.",
                "4. Náº¿u dÃ¹ng nÃºt Thanh toÃ¡n & xuáº¥t hÃ³a Ä‘Æ¡n á»Ÿ há»™p thanh toÃ¡n chÃ­nh, pháº£i Ä‘Ã¡nh dáº¥u ÄÃ£ kiá»ƒm tra tiá»n chuyá»ƒn khoáº£n vÃ o tÃ i khoáº£n cá»­a hÃ ng sau khi kiá»ƒm tra thá»±c táº¿.",
                "LÆ¯U Ã: Hiá»‡n QR khÃ´ng tá»± xÃ¡c minh giao dá»‹ch ngÃ¢n hÃ ng. ÄÃ³ng há»™p QR / ÄÃ³ng / Ä‘á»ƒ sau thÃ¬ chÆ°a ghi nháº­n Ä‘Ã£ thanh toÃ¡n.",
                "Náº¾U KHÃ”NG HIá»†N QR: Kiá»ƒm tra Internet, thá»­ láº¡i hoáº·c nhá» Quáº£n lÃ½ kiá»ƒm tra thÃ´ng tin ngÃ¢n hÃ ng trong Sao lÆ°u / thiáº¿t láº­p.");
        them("Tra cá»©u vÃ  in hÃ³a Ä‘Æ¡n",
                "Má»ž á»ž ÄÃ‚U: Menu HÃ³a Ä‘Æ¡n.",
                "1. Nháº­p mÃ£ hÃ³a Ä‘Æ¡n hoáº·c SÄT khÃ¡ch, chá»‰nh Tá»« ngÃ y / Äáº¿n ngÃ y theo yyyy-MM-dd, chá»n tráº¡ng thÃ¡i hoáº·c hÃ¬nh thá»©c thanh toÃ¡n náº¿u cáº§n, rá»“i báº¥m TÃ¬m / lá»c.",
                "2. Chá»n hÃ³a Ä‘Æ¡n trong báº£ng. Xem chi tiáº¿t má»Ÿ ná»™i dung cÃ¡c dÃ²ng sáº£n pháº©m; In hÃ³a Ä‘Æ¡n xuáº¥t tháº³ng PDF cá»§a hÃ³a Ä‘Æ¡n Ä‘Ã£ thanh toÃ¡n.",
                "3. PDF tá»± lÆ°u theo tÃªn D:\\HoaDonGreenSmart\\HoaDon_<MaHoaDon>.pdf, khÃ´ng cáº§n chá»n nÆ¡i lÆ°u. Nháº¥n Ctrl+P táº¡i danh sÃ¡ch cÅ©ng xuáº¥t hÃ³a Ä‘Æ¡n Ä‘ang chá»n.",
                "4. Muá»‘n tiáº¿p tá»¥c Ä‘Æ¡n ChÆ°a thanh toÃ¡n, chá»n Ä‘Æ¡n â†’ Má»Ÿ Ä‘Æ¡n táº¡i POS â†’ chuyá»ƒn menu BÃ¡n hÃ ng POS. Chá»‰ Há»§y Ä‘Æ¡n khi Ä‘Æ¡n cÃ²n chÆ°a thanh toÃ¡n vÃ  báº¡n cÃ³ quyá»n thao tÃ¡c.",
                "LÆ¯U Ã: In láº¡i khÃ´ng thu tiá»n hoáº·c trá»« tá»“n thÃªm. Náº¿u PDF Ä‘ang Ä‘Æ°á»£c á»©ng dá»¥ng khÃ¡c giá»¯ nÃªn khÃ´ng ghi Ä‘Ã¨ Ä‘Æ°á»£c, Ä‘Ã³ng PDF cÅ© rá»“i xuáº¥t láº¡i. Khi cáº§n in giáº¥y, dÃ¹ng chá»©c nÄƒng in cá»§a á»©ng dá»¥ng Ä‘á»c PDF.");
        them("Láº­p phiáº¿u nháº­p hÃ ng",
                "Má»ž á»ž ÄÃ‚U: Menu Phiáº¿u nháº­p hÃ ng.",
                "1. Khi Ä‘Ã£ nháº­n vÃ  kiá»ƒm Ä‘áº¿m hÃ ng, báº¥m Phiáº¿u má»›i. Äiá»n nhÃ  cung cáº¥p, chá»©ng tá»«, ngÆ°á»i giao theo thÃ´ng tin thá»±c táº¿.",
                "2. Chá»n sáº£n pháº©m, nháº­p sá»‘ lÆ°á»£ng thá»±c nháº­n vÃ  Ä‘Æ¡n giÃ¡ nháº­p, báº¥m ThÃªm dÃ²ng. Láº·p láº¡i cho tá»«ng sáº£n pháº©m; dÃ¹ng Bá» dÃ²ng Ä‘á»ƒ sá»­a dÃ²ng nháº­p sai.",
                "3. Äá»‘i chiáº¿u cÃ¡c dÃ²ng vÃ  tá»•ng nháº­p rá»“i báº¥m LÆ°u chá» duyá»‡t. Phiáº¿u chuyá»ƒn sang Chá» duyá»‡t.",
                "4. Äá» nghá»‹ Quáº£n lÃ½ kiá»ƒm tra vÃ  duyá»‡t hÃ ng thá»±c nháº­n. DÃ¹ng Táº£i láº¡i / lá»c, chá»n phiáº¿u rá»“i Má»Ÿ sá»­a / chi tiáº¿t Ä‘á»ƒ theo dÃµi.",
                "Káº¾T QUáº¢: Phiáº¿u Chá» duyá»‡t chÆ°a tÄƒng tá»“n. Khi Ä‘Æ°á»£c duyá»‡t, tráº¡ng thÃ¡i lÃ  ÄÃ£ nháº­p vÃ  tá»“n tÄƒng Ä‘Ãºng má»™t láº§n. ÄÃ¢y lÃ  ghi nháº­n hÃ ng nháº­n thá»±c táº¿, khÃ´ng pháº£i Ä‘Æ¡n Ä‘áº·t mua.");
        them("Láº­p phiáº¿u Ä‘á»•i tráº£",
                "Má»ž á»ž ÄÃ‚U: Menu Äá»•i tráº£. Cáº§n xÃ¡c Ä‘á»‹nh hÃ³a Ä‘Æ¡n mua Ä‘Ã£ thanh toÃ¡n.",
                "1. Báº¥m Phiáº¿u má»›i, nháº­p mÃ£ hÃ³a Ä‘Æ¡n gá»‘c vÃ  lÃ½ do. Báº¥m Láº¥y dÃ²ng hÃ³a Ä‘Æ¡n.",
                "2. Chá»n sáº£n pháº©m trong cÃ¡c dÃ²ng Ä‘Ã£ mua, sá»‘ lÆ°á»£ng tráº£ vÃ  tÃ¬nh tráº¡ng NguyÃªn váº¹n / Lá»—i, báº¥m ThÃªm dÃ²ng tráº£. KhÃ´ng nháº­p vÆ°á»£t sá»‘ cÃ²n Ä‘Æ°á»£c tráº£.",
                "3. Kiá»ƒm tra cÃ¡c dÃ²ng rá»“i báº¥m LÆ°u chá» duyá»‡t. Quáº£n lÃ½ xem xÃ©t Ä‘á»ƒ Duyá»‡t hoáº·c Tá»« chá»‘i.",
                "4. Khi phiáº¿u ÄÃ£ duyá»‡t, chá»n phiáº¿u vÃ  báº¥m XÃ¡c nháº­n hoÃ n táº¥t. Äá»c sá»‘ tiá»n vÃ  hÃ¬nh thá»©c hoÃ n Ä‘Æ°á»£c hiá»ƒn thá»‹; thá»±c hiá»‡n / kiá»ƒm tra viá»‡c hoÃ n tiá»n trÆ°á»›c khi xÃ¡c nháº­n.",
                "Káº¾T QUáº¢: Chá»‰ HoÃ n táº¥t má»›i cáº­p nháº­t tiá»n hoÃ n, tá»“n vÃ  Ä‘iá»ƒm theo giao dá»‹ch. Duyá»‡t phiáº¿u chÆ°a pháº£i lÃ  hoÃ n tiá»n. Chá»‰ Ä‘Æ°á»£c sá»­a / xÃ³a phiáº¿u chá» khi app cho phÃ©p.");
        them("Tra cá»©u báº£o hÃ nh",
                "Má»ž á»ž ÄÃ‚U: HÃ³a Ä‘Æ¡n â†’ chá»n hÃ³a Ä‘Æ¡n Ä‘Ã£ thanh toÃ¡n â†’ Tra báº£o hÃ nh.",
                "1. TÃ¬m Ä‘Ãºng hÃ³a Ä‘Æ¡n cá»§a khÃ¡ch báº±ng mÃ£ hÃ³a Ä‘Æ¡n hoáº·c SÄT vÃ  khoáº£ng ngÃ y.",
                "2. Báº¥m Tra báº£o hÃ nh vÃ  chá»n sáº£n pháº©m Ä‘Ã£ mua trong danh sÃ¡ch app hiá»ƒn thá»‹.",
                "3. Äá»c ngÃ y láº­p, thá»i háº¡n vÃ  háº¡n tham kháº£o. Sáº£n pháº©m cÃ³ 0 thÃ¡ng Ä‘Æ°á»£c bÃ¡o khÃ´ng Ã¡p dá»¥ng báº£o hÃ nh.",
                "LÆ¯U Ã: Äá»‘i chiáº¿u chá»©ng tá»« vÃ  Ä‘iá»u kiá»‡n báº£o hÃ nh thá»±c táº¿ trÆ°á»›c khi hÆ°á»›ng dáº«n khÃ¡ch liÃªn há»‡ Ä‘Æ¡n vá»‹ phá»¥ trÃ¡ch. App há»— trá»£ tra cá»©u, khÃ´ng cÃ³ quy trÃ¬nh láº­p phiáº¿u sá»­a chá»¯a.");
        them("Sáº£n pháº©m vÃ  tá»“n kho",
                "Má»ž á»ž ÄÃ‚U: Menu Sáº£n pháº©m hoáº·c tháº» sáº£n pháº©m táº¡i POS.",
                "1. Nháº­p mÃ£ / tÃªn sáº£n pháº©m, chá»n bá»™ lá»c loáº¡i, thÆ°Æ¡ng hiá»‡u, giÃ¡ hoáº·c tráº¡ng thÃ¡i rá»“i báº¥m TÃ¬m.",
                "2. Chá»n má»™t dÃ²ng Ä‘á»ƒ Ä‘á»c thÃ´ng sá»‘, giÃ¡, báº£o hÃ nh, tá»“n vÃ  áº£nh. Báº¥m Táº£i láº¡i / bá» lá»c Ä‘á»ƒ xem láº¡i toÃ n bá»™.",
                "3. Táº¡i POS, kiá»ƒm tra sá»‘ lÆ°á»£ng cÃ²n trÆ°á»›c khi thÃªm vÃ o giá». Sáº£n pháº©m ngÆ°ng bÃ¡n khÃ´ng Ä‘Æ°á»£c dÃ¹ng nhÆ° sáº£n pháº©m Ä‘ang bÃ¡n.",
                "LÆ¯U Ã: TÃ i khoáº£n BÃ¡n hÃ ng xem danh má»¥c; Quáº£n lÃ½ cáº­p nháº­t thÃ´ng tin vÃ  áº£nh. Tá»“n Ä‘Æ°á»£c cáº­p nháº­t qua nháº­p hÃ ng, thanh toÃ¡n vÃ  Ä‘á»•i tráº£, khÃ´ng sá»­a tay á»Ÿ form sáº£n pháº©m.");
        them("Äá»•i máº­t kháº©u, Ä‘Äƒng xuáº¥t",
                "Má»ž á»ž ÄÃ‚U: CÃ¡c nÃºt cuá»‘i menu bÃªn trÃ¡i.",
                "1. Äá»ƒ Ä‘á»•i máº­t kháº©u cá»§a mÃ¬nh, báº¥m Äá»•i máº­t kháº©u, nháº­p máº­t kháº©u má»›i vÃ o há»™p thoáº¡i rá»“i xÃ¡c nháº­n.",
                "2. TrÆ°á»›c khi ÄÄƒng xuáº¥t, lÆ°u Ä‘Æ¡n Ä‘ang lÃ m náº¿u cáº§n giá»¯ láº¡i vÃ  kiá»ƒm tra viá»‡c Ä‘á»‘i soÃ¡t ca. Giá» chÆ°a lÆ°u sáº½ máº¥t khi thoÃ¡t phiÃªn.",
                "3. Báº¥m ÄÄƒng xuáº¥t, Ä‘á»c thÃ´ng bÃ¡o vÃ  xÃ¡c nháº­n Ä‘á»ƒ trá»Ÿ vá» mÃ n ÄÄƒng nháº­p.",
                "PHÃM Táº®T: Ctrl+Shift+P Ä‘á»•i máº­t kháº©u; Ctrl+L Ä‘Äƒng xuáº¥t. Chá»‰ dÃ¹ng tÃ i khoáº£n Ä‘Æ°á»£c cáº¥p cho mÃ¬nh Ä‘á»ƒ giao dá»‹ch Ä‘Æ°á»£c ghi nháº­n Ä‘Ãºng ngÆ°á»i.");
        them("CÃ¡c phÃ­m táº¯t",
                "TOÃ€N á»¨NG Dá»¤NG: F1 má»Ÿ Trá»£ giÃºp; Shift+F1 má»Ÿ báº£ng phÃ­m táº¯t; Alt+Home vá» Dashboard.",
                "CHUYá»‚N MÃ€N: Ctrl+1 BÃ¡n hÃ ng POS; Ctrl+2 HÃ³a Ä‘Æ¡n; Ctrl+3 Äá»•i tráº£; Ctrl+4 KhÃ¡ch hÃ ng; Ctrl+5 Sáº£n pháº©m; Ctrl+6 Phiáº¿u nháº­p hÃ ng; Ctrl+8 Ca lÃ m viá»‡c.",
                quanLy ? "DÃ€NH CHO QUáº¢N LÃ: Ctrl+7 Khuyáº¿n mÃ£i; Ctrl+9 Danh má»¥c ca; Ctrl+0 NhÃ¢n viÃªn / tÃ i khoáº£n; Ctrl+B BÃ¡o cÃ¡o; Ctrl+G Sao lÆ°u / thiáº¿t láº­p."
                        : "CÃC Má»¤C QUáº¢N LÃ: Náº¿u chá»©c nÄƒng khÃ´ng xuáº¥t hiá»‡n trong menu cá»§a báº¡n, liÃªn há»‡ Quáº£n lÃ½ khi cáº§n Ä‘Æ°á»£c há»— trá»£.",
                "TRONG POS: F2 tÃ¬m sáº£n pháº©m; F8 lÆ°u Ä‘Æ¡n; F9 tiá»n máº·t; F10 QR; Ctrl+N táº¡o Ä‘Æ¡n má»›i.",
                "HÃ“A ÄÆ N: Ctrl+P xuáº¥t PDF cá»§a dÃ²ng Ä‘ang chá»n. THANH TOÃN: F2 hiá»‡n QR, Enter thanh toÃ¡n & xuáº¥t hÃ³a Ä‘Æ¡n, Esc Ä‘á»ƒ sau.",
                "MÃ€N DANH Má»¤C: Ctrl+N táº¡o má»›i, Ctrl+S lÆ°u, Ctrl+D xÃ³a, Ctrl+F tÃ¬m kiáº¿m, F5 táº£i láº¡i táº¡i cÃ¡c mÃ n cÃ³ nÃºt tÆ°Æ¡ng á»©ng. Trong Trá»£ giÃºp, Ctrl+F Ä‘Æ°a con trá» vÃ o Ã´ tÃ¬m hÆ°á»›ng dáº«n.");
        them("Khi gáº·p vÆ°á»›ng máº¯c",
                "CHÆ¯A BÃN ÄÆ¯á»¢C: Kiá»ƒm tra Ä‘Ã£ má»Ÿ ca cá»§a mÃ¬nh vÃ  giá» cÃ³ sáº£n pháº©m. Náº¿u chÆ°a Ä‘Æ°á»£c phÃ¢n cÃ´ng, nhá» Quáº£n lÃ½ phÃ¢n cÃ´ng trÆ°á»›c.",
                "BÃO CÃ’N CA ÄANG Má»ž: VÃ o Ca lÃ m viá»‡c, bá» tá»« khÃ³a lá»c náº¿u cáº§n, lá»c Äang má»Ÿ rá»“i báº¥m Táº£i láº¡i / lá»c. Kiá»ƒm tra cáº£ ca cÅ© cá»§a mÃ¬nh vÃ  Ä‘á»‘i soÃ¡t / Ä‘Ã³ng ca Ä‘Ã³ trÆ°á»›c.",
                "THIáº¾U HÃ€NG: Kiá»ƒm tra láº¡i tá»“n, giáº£m sá»‘ lÆ°á»£ng trong giá» hoáº·c liÃªn há»‡ Quáº£n lÃ½. KhÃ´ng tÄƒng tá»“n báº±ng cÃ¡ch sá»­a thÃ´ng tin sáº£n pháº©m.",
                "KHÃ”NG HIá»†N QR: Kiá»ƒm tra Internet, thá»­ láº¡i; nhá» Quáº£n lÃ½ kiá»ƒm tra ngÃ¢n hÃ ng vÃ  tÃ i khoáº£n nháº­n tiá»n. Chá»‰ xÃ¡c nháº­n khi Ä‘Ã£ nháº­n tiá»n thá»±c táº¿.",
                "ÄÃƒ THANH TOÃN NHÆ¯NG CHÆ¯A CÃ“ PDF: TÃ¬m hÃ³a Ä‘Æ¡n á»Ÿ menu HÃ³a Ä‘Æ¡n vÃ  báº¥m In hÃ³a Ä‘Æ¡n. MÃ¡y cáº§n cÃ³ á»• D; Ä‘Ã³ng file PDF cÅ© náº¿u Ä‘ang bá»‹ giá»¯ rá»“i thá»­ láº¡i. KhÃ´ng thanh toÃ¡n láº¡i Ä‘á»ƒ láº¥y hÃ³a Ä‘Æ¡n.",
                "KHÃ”NG Táº¢I ÄÆ¯á»¢C Dá»® LIá»†U: Ghi láº¡i thÃ´ng bÃ¡o lá»—i vÃ  viá»‡c Ä‘ang lÃ m Ä‘á»ƒ bÃ¡o Quáº£n lÃ½ / ngÆ°á»i há»— trá»£. Dá»¯ liá»‡u chÆ°a lÆ°u nÃªn Ä‘Æ°á»£c kiá»ƒm tra trÆ°á»›c khi táº£i láº¡i mÃ n hÃ¬nh.");

        if (!quanLy) return;
        them("NhÃ¢n viÃªn vÃ  tÃ i khoáº£n",
                "Má»ž á»ž ÄÃ‚U: NhÃ¢n viÃªn / tÃ i khoáº£n. DÃ nh cho Quáº£n lÃ½.",
                "1. Báº¥m Táº¡o má»›i, Ä‘iá»n mÃ£, há» tÃªn, vai trÃ² vÃ  thÃ´ng tin nhÃ¢n viÃªn; nháº­p ngÃ y theo yyyy-MM-dd.",
                "2. Äiá»n TÃªn Ä‘Äƒng nháº­p má»›i vÃ  Máº­t kháº©u má»›i cho há»“ sÆ¡ má»›i. Chá»n áº£nh náº¿u cáº§n, rá»“i báº¥m LÆ°u.",
                "3. Khi sá»­a, chá»n nhÃ¢n viÃªn trong báº£ng, kiá»ƒm tra thÃ´ng tin Ä‘ang má»Ÿ rá»“i LÆ°u. DÃ¹ng tÃ¬m mÃ£ / há» tÃªn vÃ  lá»c Vai trÃ² Ä‘á»ƒ tra nhanh.",
                "LÆ¯U Ã: Má»—i nhÃ¢n viÃªn gáº¯n vá»›i má»™t tÃ i khoáº£n. Há»“ sÆ¡ Ä‘Ã£ cÃ³ lá»‹ch sá»­ cÃ³ thá»ƒ khÃ´ng xÃ³a Ä‘Æ°á»£c; Ä‘á»c thÃ´ng bÃ¡o trÆ°á»›c khi Ä‘á»•i hoáº·c xÃ³a dá»¯ liá»‡u.");
        them("Danh má»¥c ca vÃ  phÃ¢n cÃ´ng",
                "Má»ž á»ž ÄÃ‚U: Danh má»¥c ca; sau Ä‘Ã³ Ca lÃ m viá»‡c. DÃ nh cho Quáº£n lÃ½.",
                "1. Trong Danh má»¥c ca, báº¥m Táº¡o má»›i, nháº­p mÃ£ ca chÆ°a cÃ³ trong danh sÃ¡ch, tÃªn ca vÃ  giá» báº¯t Ä‘áº§u / káº¿t thÃºc theo HH:mm rá»“i LÆ°u.",
                "2. Chuyá»ƒn sang Ca lÃ m viá»‡c. Chá»n nhÃ¢n viÃªn cÃ³ sáºµn tá»« danh sÃ¡ch MÃ£ nhÃ¢n viÃªn, báº¥m Chá»n ngÃ y Ä‘á»ƒ má»Ÿ lá»‹ch, rá»“i chá»n Ca lÃ m viá»‡c.",
                "3. Äiá»n ghi chÃº náº¿u cÃ³ vÃ  báº¥m PhÃ¢n cÃ´ng. Kiá»ƒm tra mÃ£ phÃ¢n cÃ´ng, nhÃ¢n viÃªn, ngÃ y, ca trong báº£ng.",
                "4. HÆ°á»›ng dáº«n nhÃ¢n viÃªn má»Ÿ Ä‘Ãºng phÃ¢n cÃ´ng cá»§a mÃ¬nh. KhÃ´ng phÃ¢n cÃ´ng chá»“ng giá»; chá»‰ XÃ³a phÃ¢n cÃ´ng khi chÆ°a má»Ÿ vÃ  app cho phÃ©p.");
        them("Quáº£n lÃ½ sáº£n pháº©m vÃ  áº£nh",
                "Má»ž á»ž ÄÃ‚U: Sáº£n pháº©m. Thao tÃ¡c thÃªm / sá»­a dÃ nh cho Quáº£n lÃ½.",
                "1. Báº¥m Táº¡o má»›i. MÃ£ sáº£n pháº©m Ä‘Æ°á»£c Ä‘iá»n sáºµn; nháº­p tÃªn, loáº¡i, thÆ°Æ¡ng hiá»‡u, giÃ¡, thÃ´ng sá»‘, xuáº¥t xá»©, sá»‘ thÃ¡ng báº£o hÃ nh vÃ  tráº¡ng thÃ¡i.",
                "2. Báº¥m Chá»n áº£nh, chá»n JPG hoáº·c PNG khÃ´ng quÃ¡ 10 MB, kiá»ƒm tra áº£nh xem trÆ°á»›c rá»“i LÆ°u. Muá»‘n bá» áº£nh, báº¥m Bá» áº£nh rá»“i LÆ°u.",
                "3. Muá»‘n sá»­a, chá»n Ä‘Ãºng dÃ²ng trong báº£ng trÆ°á»›c khi cáº­p nháº­t. Náº¿u chá»‰ ngá»«ng kinh doanh, chá»n tráº¡ng thÃ¡i NgÆ°ng bÃ¡n rá»“i LÆ°u.",
                "4. Táº¡o Phiáº¿u nháº­p hÃ ng vÃ  duyá»‡t hÃ ng thá»±c nháº­n Ä‘á»ƒ tÄƒng tá»“n. Sau khi lÆ°u áº£nh, chuyá»ƒn láº¡i POS Ä‘á»ƒ Ä‘á»c áº£nh má»›i.",
                "LÆ¯U Ã: KhÃ´ng Ä‘iá»n tá»“n Ä‘áº§u vÃ o form sá»­a sáº£n pháº©m; sáº£n pháº©m má»›i Ä‘Æ°á»£c bá»• sung tá»“n qua nghiá»‡p vá»¥ nháº­p hÃ ng.");
        them("Quáº£n lÃ½ khuyáº¿n mÃ£i",
                "Má»ž á»ž ÄÃ‚U: Khuyáº¿n mÃ£i. DÃ nh cho Quáº£n lÃ½.",
                "1. Báº¥m Táº¡o má»›i, Ä‘iá»n mÃ£, tÃªn chÆ°Æ¡ng trÃ¬nh, má»©c giáº£m (%), ngÃ y báº¯t Ä‘áº§u / káº¿t thÃºc vÃ  tráº¡ng thÃ¡i.",
                "2. Kiá»ƒm tra ngÃ y theo yyyy-MM-dd vÃ  giÃ¡ trá»‹ giáº£m há»£p lá»‡ rá»“i báº¥m LÆ°u.",
                "3. Äá»ƒ cáº­p nháº­t, chá»n chÆ°Æ¡ng trÃ¬nh trong báº£ng rá»“i sá»­a vÃ  LÆ°u. DÃ¹ng TÃ¬m hoáº·c Táº£i láº¡i / bá» lá»c Ä‘á»ƒ tra cá»©u.",
                "LÆ¯U Ã: POS chá»‰ Ã¡p dá»¥ng chÆ°Æ¡ng trÃ¬nh phÃ¹ há»£p hiá»‡u lá»±c. ChÆ°Æ¡ng trÃ¬nh Ä‘Ã£ cÃ³ lá»‹ch sá»­ sá»­ dá»¥ng khÃ´ng Ä‘Æ°á»£c xÃ³a tÃ¹y Ã½; dÃ¹ng tráº¡ng thÃ¡i phÃ¹ há»£p Ä‘á»ƒ dá»«ng Ã¡p dá»¥ng má»›i.");
        them("Duyá»‡t nháº­p hÃ ng vÃ  Ä‘á»•i tráº£",
                "Má»ž á»ž ÄÃ‚U: Phiáº¿u nháº­p hÃ ng hoáº·c Äá»•i tráº£; cÅ©ng cÃ³ thá»ƒ má»Ÿ cÃ´ng viá»‡c cáº§n xá»­ lÃ½ tá»« Dashboard quáº£n lÃ½.",
                "NHáº¬P HÃ€NG: Lá»c Chá» duyá»‡t, chá»n phiáº¿u â†’ Má»Ÿ sá»­a / chi tiáº¿t, Ä‘á»‘i chiáº¿u chá»©ng tá»« vÃ  hÃ ng thá»±c nháº­n. Báº¥m Duyá»‡t hÃ ng thá»±c nháº­n khi Ä‘Ã£ kiá»ƒm Ä‘áº¿m; hoáº·c Tá»« chá»‘i vÃ  nháº­p lÃ½ do.",
                "Káº¾T QUáº¢ NHáº¬P: ÄÃ£ nháº­p má»›i cá»™ng tá»“n. Phiáº¿u Chá» duyá»‡t hoáº·c Tá»« chá»‘i chÆ°a tÄƒng tá»“n vÃ  khÃ´ng duyá»‡t láº¡i phiáº¿u Ä‘Ã£ nháº­p.",
                "Äá»”I TRáº¢: Má»Ÿ Äá»•i tráº£, lá»c Chá» duyá»‡t, chá»n phiáº¿u â†’ Má»Ÿ sá»­a / xem. Kiá»ƒm tra hÃ³a Ä‘Æ¡n gá»‘c, sá»‘ lÆ°á»£ng vÃ  tÃ¬nh tráº¡ng hÃ ng rá»“i Duyá»‡t hoáº·c Tá»« chá»‘i cÃ³ lÃ½ do.",
                "Káº¾T QUáº¢ Äá»”I TRáº¢: Duyá»‡t má»›i chuyá»ƒn sang ÄÃ£ duyá»‡t. NgÆ°á»i xá»­ lÃ½ cáº§n thá»±c hiá»‡n / kiá»ƒm tra hoÃ n tiá»n rá»“i XÃ¡c nháº­n hoÃ n táº¥t; khi Ä‘Ã³ má»›i cáº­p nháº­t tiá»n, tá»“n vÃ  Ä‘iá»ƒm.");
        them("BÃ¡o cÃ¡o vÃ  Dashboard quáº£n lÃ½",
                "Má»ž á»ž ÄÃ‚U: Dashboard vÃ  BÃ¡o cÃ¡o. DÃ nh cho Quáº£n lÃ½.",
                "1. TrÃªn Dashboard, Ä‘á»c cÃ¡c chá»‰ sá»‘, phiáº¿u cáº§n duyá»‡t, cáº£nh bÃ¡o tá»“n vÃ  ca cáº§n xá»­ lÃ½ Ä‘á»ƒ biáº¿t viá»‡c cáº§n Æ°u tiÃªn.",
                "2. VÃ o BÃ¡o cÃ¡o, chá»n Tá»« ngÃ y ca / Äáº¿n ngÃ y ca theo yyyy-MM-dd rá»“i báº¥m Láº­p bÃ¡o cÃ¡o.",
                "3. Xem cÃ¡c cá»™t tiá»n thu, tiá»n hoÃ n, thu sau hoÃ n; chuyá»ƒn báº£ng sáº£n pháº©m Ä‘á»ƒ xem tá»“n vÃ  lÆ°á»£ng bÃ¡n / tráº£ trong ká»³.",
                "4. Báº¥m Xuáº¥t báº£ng CSV khi cáº§n lÆ°u báº£ng bÃ¡o cÃ¡o. Chá»n Ä‘Ãºng ká»³ trÆ°á»›c khi xuáº¥t Ä‘á»ƒ file khá»›p dá»¯ liá»‡u Ä‘ang xem.");
        them("Sao lÆ°u vÃ  thiáº¿t láº­p QR",
                "Má»ž á»ž ÄÃ‚U: Sao lÆ°u / thiáº¿t láº­p. DÃ nh cho Quáº£n lÃ½.",
                "SAO LÆ¯U: Äiá»n ÄÆ°á»ng dáº«n .bak trÃªn mÃ¡y SQL Server, báº¥m Sao lÆ°u vÃ  kiá»ƒm tra. DÃ¹ng Kiá»ƒm tra báº£n .bak Ä‘á»ƒ kiá»ƒm tra báº£n sao táº¡i Ä‘Æ°á»ng dáº«n Ä‘Ã³.",
                "Tá»° Äá»˜NG: Nháº­p Chu ká»³ sao lÆ°u (phÃºt), vÃ­ dá»¥ 60; 0 lÃ  táº¯t. Báº¥m LÆ°u cáº¥u hÃ¬nh rá»“i Ä‘Äƒng nháº­p láº¡i Ä‘á»ƒ Ã¡p dá»¥ng lá»‹ch.",
                "ÄIá»€U KIá»†N CHáº Y: Sao lÆ°u tá»± Ä‘á»™ng chá»‰ cháº¡y khi á»©ng dá»¥ng cÃ²n má»Ÿ vÃ  Quáº£n lÃ½ Ä‘ang Ä‘Äƒng nháº­p. ÄÆ°á»ng dáº«n .bak thuá»™c mÃ¡y SQL Server vÃ  pháº£i lÃ  nÆ¡i há»‡ thá»‘ng Ä‘Æ°á»£c phÃ©p ghi.",
                "THIáº¾T Láº¬P QR: Äiá»n BIN ngÃ¢n hÃ ng 6 sá»‘, sá»‘ tÃ i khoáº£n nháº­n tiá»n tháº­t cá»§a cá»­a hÃ ng, tÃªn chá»§ tÃ i khoáº£n vÃ  tÃªn ngÃ¢n hÃ ng; báº¥m LÆ°u cáº¥u hÃ¬nh. Äá»‘i chiáº¿u thÃ´ng tin trÆ°á»›c khi cho khÃ¡ch chuyá»ƒn khoáº£n.",
                "LÆ¯U Ã: Thiáº¿t láº­p QR khÃ´ng tá»± xÃ¡c nháº­n tiá»n Ä‘Ã£ vÃ o. NhÃ¢n viÃªn váº«n pháº£i kiá»ƒm tra giao dá»‹ch thá»±c táº¿ trÆ°á»›c khi báº¥m xÃ¡c nháº­n nháº­n tiá»n.");
    }

    private String khongDau(String text) {
        return Normalizer.normalize(text.toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "").replace("\u0111", "d");
    }

    private void locChuDe() {
        String chon = danhMuc.getSelectedValue();
        String tuKhoa = khongDau(tim.getText().trim());
        model.clear();
        for (Map.Entry<String, String> muc : huongDan.entrySet())
            if (khongDau(muc.getKey() + " " + muc.getValue()).contains(tuKhoa))
                model.addElement(muc.getKey());
        lblSoMuc.setText(model.size() + " / " + huongDan.size() + " chá»§ Ä‘á»");
        if (!model.isEmpty()) {
            if (chon != null && model.contains(chon)) danhMuc.setSelectedValue(chon, true);
            else danhMuc.setSelectedIndex(0);
        } else hienChuDe();
    }

    private void hienChuDe() {
        String ten = danhMuc.getSelectedValue();
        lblChuDe.setText(ten == null ? "ChÆ°a tÃ¬m tháº¥y hÆ°á»›ng dáº«n" : ten);
        noiDung.setText(ten == null
                ? "Thá»­ tá»« khÃ³a ngáº¯n hÆ¡n nhÆ°: ca, QR, hÃ³a Ä‘Æ¡n.\n\nBáº¥m Bá» lá»c Ä‘á»ƒ xem toÃ n bá»™ hÆ°á»›ng dáº«n phÃ¹ há»£p vai trÃ² cá»§a báº¡n."
                : huongDan.get(ten));
        noiDung.setCaretPosition(0);
    }

    @Override public void insertUpdate(DocumentEvent e) { locChuDe(); }
    @Override public void removeUpdate(DocumentEvent e) { locChuDe(); }
    @Override public void changedUpdate(DocumentEvent e) { locChuDe(); }
}
