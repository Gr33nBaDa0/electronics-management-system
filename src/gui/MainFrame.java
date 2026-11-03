package gui;

import connectDB.ConnectDB;

import dao.SaoLuu_DAO;
import dao.TaiKhoan_DAO;

import entity.TaiKhoan;

import util.GiaoDien;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class MainFrame extends JFrame implements ActionListener {
    // MÃ u vÃ  font Ä‘áº·t á»Ÿ Ä‘áº§u lá»›p; navi sá»­ dá»¥ng chÃ­nh xÃ¡c #1B5E20.
    private static final Color MAU_NAVI = GiaoDien.DARK;
    private static final Color MAU_CHU_NAVI = GiaoDien.MENU_TEXT;
    private static final Color MAU_NHAN_NAVI = GiaoDien.MENU_MUTED;
    private static final Font FONT_THUONG_HIEU = GiaoDien.phong(Font.BOLD, 24);
    private final TaiKhoan tk;
    private final JPanel noiDung = new JPanel(new CardLayout());
    private final Map<String, JPanel> panels = new HashMap<>();
    private final Map<String, GiaoDien.NutMenu> menu = new LinkedHashMap<>();
    private final JLabel lblTrang = new JLabel("Dashboard"),
            lblGio = new JLabel(),
            lblKetNoi = new JLabel();
    private BanHang_GUI pos;
    private javax.swing.Timer backup, dongHo, lamMoi;

    // NhÃ³m menu: {nhÃ£n, gá»£i Ã½ phÃ­m, KeyStroke, chá»‰ quáº£n lÃ½ (true/false)}
    private static final String[][] NHOM_BAN_HANG = {
        {"BÃ¡n hÃ ng POS", "Ctrl+1", "ctrl 1", "0"},
        {"HÃ³a Ä‘Æ¡n", "Ctrl+2", "ctrl 2", "0"},
        {"Äá»•i tráº£", "Ctrl+3", "ctrl 3", "0"},
        {"KhÃ¡ch hÃ ng", "Ctrl+4", "ctrl 4", "0"}
    };
    private static final String[][] NHOM_KHO = {
        {"Sáº£n pháº©m", "Ctrl+5", "ctrl 5", "0"},
        {"Phiáº¿u nháº­p hÃ ng", "Ctrl+6", "ctrl 6", "0"},
        {"Khuyáº¿n mÃ£i", "Ctrl+7", "ctrl 7", "1"}
    };
    private static final String[][] NHOM_VAN_HANH = {
        {"Ca lÃ m viá»‡c", "Ctrl+8", "ctrl 8", "0"},
        {"Danh má»¥c ca", "Ctrl+9", "ctrl 9", "1"}
    };
    private static final String[][] NHOM_QUAN_TRI = {
        {"NhÃ¢n viÃªn / tÃ i khoáº£n", "Ctrl+0", "ctrl 0", "1"},
        {"BÃ¡o cÃ¡o", "Ctrl+B", "ctrl B", "1"},
        {"Sao lÆ°u / thiáº¿t láº­p", "Ctrl+G", "ctrl G", "1"}
    };

    public MainFrame(TaiKhoan tk) {
        this.tk = tk;
        GiaoDien.apDung();
        setTitle("GreenSmart â€” " + tk.getNhanVien().getHoTen());
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Insets edges = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
        int width = screen.width - edges.left - edges.right,
                height = screen.height - edges.top - edges.bottom;
        // Má»Ÿ háº¿t vÃ¹ng lÃ m viá»‡c; váº«n giá»¯ thanh tiÃªu Ä‘á» vÃ  nÃºt Ä‘Ã³ng cá»§a Windows.
        setSize(width, height);
        setMinimumSize(new Dimension(Math.min(1150, width), Math.min(700, height)));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());

        add(taoThanhMenu(), BorderLayout.WEST);
        JPanel phai = new JPanel(new BorderLayout());
        phai.add(taoThanhTieuDe(), BorderLayout.NORTH);
        noiDung.setBackground(GiaoDien.BACKGROUND);
        phai.add(noiDung, BorderLayout.CENTER);
        phai.add(taoThanhTrangThai(), BorderLayout.SOUTH);
        add(phai, BorderLayout.CENTER);

        JPanel chao = taoTrangChao();
        panels.put("ChÃ o", chao);
        noiDung.add(chao, "ChÃ o");

        addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        if (xacNhan(MainFrame.this, "ThoÃ¡t á»©ng dá»¥ng? Giá» chÆ°a lÆ°u sáº½ máº¥t.")) {
                            dungBackup();
                            if (dongHo != null) dongHo.stop();
                            dispose();
                        }
                    }
                });
        ganPhimTat();
        batDongHo();
        batBackup();
        lamMoi =
                new javax.swing.Timer(
                        60000,
                        e -> {
                            JPanel p = panels.get("ChÃ o");
                            if (p != null && p.isShowing()) lamMoiDashboard();
                        });
        lamMoi.start();
        danhDauMenu(
                "ChÃ o",
                "ADMIN".equals(tk.getQuyen()) ? "Dashboard Â· Quáº£n lÃ½" : "Dashboard Â· NhÃ¢n viÃªn");
    }

    // ===== Thanh menu bÃªn trÃ¡i =====
    private JPanel taoThanhMenu() {
        boolean admin = "ADMIN".equals(tk.getQuyen());
        JPanel navi = new JPanel(new BorderLayout());
        navi.setBackground(MAU_NAVI);
        navi.setPreferredSize(new Dimension(244, 700));
        JPanel dau = new JPanel();
        dau.setLayout(new BoxLayout(dau, BoxLayout.Y_AXIS));
        dau.setBackground(MAU_NAVI);
        dau.setBorder(BorderFactory.createEmptyBorder(16, 12, 8, 12));
        JLabel la = new JLabel(GiaoDien.logo(34), javax.swing.SwingConstants.CENTER);
        la.setOpaque(true);
        la.setBackground(GiaoDien.SURFACE);
        la.setPreferredSize(new Dimension(44, 44));
        JLabel logo = new JLabel("GreenSmart");
        logo.setForeground(Color.WHITE);
        logo.setFont(FONT_THUONG_HIEU);
        JLabel khau = new JLabel("QUáº¢N LÃ ÄIá»†N MÃY");
        khau.setFont(GiaoDien.phong(Font.PLAIN, 10));
        khau.setForeground(MAU_NHAN_NAVI);
        JPanel chu = new JPanel(new java.awt.GridLayout(2, 1));
        chu.setOpaque(false);
        chu.add(logo);
        chu.add(khau);
        JPanel thuongHieu = new JPanel(new BorderLayout(10, 0));
        thuongHieu.setOpaque(false);
        thuongHieu.add(la, BorderLayout.WEST);
        thuongHieu.add(chu, BorderLayout.CENTER);
        thuongHieu.setAlignmentX(Component.LEFT_ALIGNMENT);
        thuongHieu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        dau.add(thuongHieu);
        dau.add(Box.createVerticalStrut(14));
        GiaoDien.NutMenu trangChu = new GiaoDien.NutMenu("Dashboard", "Alt+Home");
        trangChu.setActionCommand("ChÃ o");
        trangChu.addActionListener(this);
        trangChu.datHoatDong(true);
        menu.put("ChÃ o", trangChu);
        dau.add(trangChu);

        navi.add(dau, BorderLayout.NORTH);
        JPanel m = new JPanel();
        m.setLayout(new BoxLayout(m, BoxLayout.Y_AXIS));
        m.setBackground(MAU_NAVI);
        m.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        themNhom(m, "BÃN HÃ€NG", NHOM_BAN_HANG, admin);
        themNhom(m, "KHO VÃ€ DANH Má»¤C", NHOM_KHO, admin);
        themNhom(m, "Váº¬N HÃ€NH", NHOM_VAN_HANH, admin);
        if (admin) themNhom(m, "QUáº¢N TRá»Š", NHOM_QUAN_TRI, admin);
        JScrollPane sp = new JScrollPane(m);
        sp.setBorder(null);
        sp.setBackground(MAU_NAVI);
        sp.getViewport().setBackground(MAU_NAVI);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getVerticalScrollBar().setUnitIncrement(24);
        navi.add(sp, BorderLayout.CENTER);

        JPanel cuoi = new JPanel();
        cuoi.setLayout(new BoxLayout(cuoi, BoxLayout.Y_AXIS));
        cuoi.setBackground(MAU_NAVI);
        cuoi.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));
        GiaoDien.NutMenu troGiup = new GiaoDien.NutMenu("Trá»£ giÃºp", "F1");
        troGiup.setActionCommand("Trá»£ giÃºp");
        troGiup.addActionListener(this);
        menu.put("Trá»£ giÃºp", troGiup);
        cuoi.add(troGiup);
        cuoi.add(Box.createVerticalStrut(6));
        cuoi.add(taoTheNguoiDung());
        cuoi.add(Box.createVerticalStrut(6));
        JPanel hangCuoi = new JPanel(new java.awt.GridLayout(1, 2, 6, 0));
        hangCuoi.setOpaque(false);
        for (String ten : new String[] {"Äá»•i máº­t kháº©u", "ÄÄƒng xuáº¥t"}) {
            JButton nut = GiaoDien.nut(ten);
            nut.setActionCommand(ten);
            nut.addActionListener(this);
            nut.setPreferredSize(new Dimension(90, 32));
            nut.setFont(GiaoDien.phong(Font.BOLD, 12));
            nut.setToolTipText(ten + ("ÄÄƒng xuáº¥t".equals(ten) ? " (Ctrl+L)" : " (Ctrl+Shift+P)"));
            hangCuoi.add(nut);
        }
        hangCuoi.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        hangCuoi.setAlignmentX(Component.LEFT_ALIGNMENT);
        cuoi.add(hangCuoi);
        navi.add(cuoi, BorderLayout.SOUTH);
        return navi;
    }

    private void themNhom(JPanel m, String tieuDe, String[][] muc, boolean admin) {
        JLabel l = new JLabel(tieuDe);
        l.setFont(GiaoDien.phong(Font.BOLD, 11));
        l.setForeground(MAU_NHAN_NAVI);
        l.setBorder(BorderFactory.createEmptyBorder(11, 8, 3, 0));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        m.add(l);
        for (String[] a : muc) {
            if (!admin && "1".equals(a[3])) continue;
            GiaoDien.NutMenu b = new GiaoDien.NutMenu(a[0], a[1]);
            b.setActionCommand(a[0]);
            b.addActionListener(this);
            menu.put(a[0], b);
            m.add(b);
        }
    }

    /** Tháº» nhá» cuá»‘i menu: chá»¯ cÃ¡i Ä‘áº§u cá»§a tÃªn trong vÃ²ng trÃ²n, há» tÃªn vÃ  vai trÃ². */
    private JPanel taoTheNguoiDung() {
        String ten = String.valueOf(tk.getNhanVien().getHoTen());
        String chu =
                ten.trim().isEmpty()
                        ? "?"
                        : ten.trim()
                                .substring(ten.trim().lastIndexOf(' ') + 1)
                                .substring(0, 1)
                                .toUpperCase();
        JPanel avatar =
                new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g0) {
                        Graphics2D g = (Graphics2D) g0.create();
                        g.setRenderingHint(
                                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g.setRenderingHint(
                                RenderingHints.KEY_TEXT_ANTIALIASING,
                                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                        g.setColor(GiaoDien.PRIMARY_SOFT);
                        g.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                        g.setColor(GiaoDien.DARK);
                        g.setFont(GiaoDien.phong(Font.BOLD, 16));
                        int w = g.getFontMetrics().stringWidth(chu);
                        g.drawString(
                                chu,
                                (getWidth() - w) / 2,
                                (getHeight() + g.getFontMetrics().getAscent()) / 2 - 2);
                        g.dispose();
                    }
                };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(38, 38));
        JLabel hoTen = new JLabel(ten);
        hoTen.setForeground(Color.WHITE);
        hoTen.setFont(GiaoDien.phong(Font.BOLD, 13));
        JLabel vaiTro =
                new JLabel(
                        ("ADMIN".equals(tk.getQuyen()) ? "Quáº£n lÃ½" : "BÃ¡n hÃ ng")
                                + " Â· "
                                + tk.getTenDangNhap());
        vaiTro.setForeground(MAU_CHU_NAVI);
        vaiTro.setFont(GiaoDien.phong(Font.PLAIN, 12));
        JPanel chu2 = new JPanel(new java.awt.GridLayout(2, 1));
        chu2.setOpaque(false);
        chu2.add(hoTen);
        chu2.add(vaiTro);
        JPanel the = new JPanel(new BorderLayout(10, 0));
        the.setBackground(GiaoDien.DARK_HOVER);
        the.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        the.add(avatar, BorderLayout.WEST);
        the.add(chu2, BorderLayout.CENTER);
        the.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        the.setAlignmentX(Component.LEFT_ALIGNMENT);
        return the;
    }

    // ===== Thanh tiÃªu Ä‘á» vÃ  thanh tráº¡ng thÃ¡i =====
    private JPanel taoThanhTieuDe() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(GiaoDien.SURFACE);
        p.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, GiaoDien.BORDER),
                        BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        lblTrang.setFont(GiaoDien.phong(Font.BOLD, 18));
        lblTrang.setForeground(GiaoDien.PRIMARY_DARK);
        lblGio.setForeground(GiaoDien.MUTED);
        p.add(lblTrang, BorderLayout.WEST);
        p.add(lblGio, BorderLayout.EAST);
        return p;
    }

    private JPanel taoThanhTrangThai() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(GiaoDien.SURFACE);
        p.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(1, 0, 0, 0, GiaoDien.BORDER),
                        BorderFactory.createEmptyBorder(5, 20, 5, 20)));
        lblKetNoi.setFont(GiaoDien.phong(Font.PLAIN, 12));
        JLabel goiY = new JLabel("F1: Trá»£ giÃºp Â· Shift+F1: phÃ­m táº¯t");
        goiY.setFont(GiaoDien.phong(Font.PLAIN, 12));
        goiY.setForeground(GiaoDien.MUTED);
        p.add(lblKetNoi, BorderLayout.WEST);
        p.add(goiY, BorderLayout.EAST);
        capNhatTrangThai();
        return p;
    }

    private void capNhatTrangThai() {
        boolean noi;
        try {
            noi = ConnectDB.con != null && !ConnectDB.con.isClosed();
        } catch (SQLException e) {
            noi = false;
        }
        lblKetNoi.setText(noi ? "â—  ÄÃ£ káº¿t ná»‘i SQL Server" : "â—  ChÆ°a káº¿t ná»‘i SQL Server");
        lblKetNoi.setForeground(noi ? GiaoDien.SUCCESS : GiaoDien.ERROR);
        lblGio.setText(
                LocalDateTime.now()
                        .format(
                                DateTimeFormatter.ofPattern(
                                        "EEEE, dd/MM/yyyy Â· HH:mm", new Locale("vi", "VN"))));
    }

    private void batDongHo() {
        dongHo = new javax.swing.Timer(15000, e -> capNhatTrangThai());
        dongHo.start();
    }

    // ===== Trang chÃ o =====
    private JPanel taoTrangChao() {
        return "ADMIN".equals(tk.getQuyen())
                ? new DashboardQuanLy_GUI(tk, this)
                : new DashboardNhanVien_GUI(tk, this);
    }

    private void lamMoiDashboard() {
        JPanel p = panels.get("ChÃ o");
        if (p instanceof DashboardQuanLy_GUI) ((DashboardQuanLy_GUI) p).taiDuLieu();
        if (p instanceof DashboardNhanVien_GUI) ((DashboardNhanVien_GUI) p).taiDuLieu();
    }

    // ===== PhÃ­m táº¯t =====
    private void ganPhimTat() {
        boolean admin = "ADMIN".equals(tk.getQuyen());
        String[][][] tatCa = {NHOM_BAN_HANG, NHOM_KHO, NHOM_VAN_HANH, NHOM_QUAN_TRI};
        for (String[][] nhom : tatCa)
            for (String[] a : nhom) {
                if (!admin && "1".equals(a[3])) continue;
                String ten = a[0];
                GiaoDien.phimTat(getRootPane(), a[2], () -> moMan(ten));
            }
        GiaoDien.phimTat(getRootPane(), "ctrl L", () -> moMan("ÄÄƒng xuáº¥t"));
        GiaoDien.phimTat(getRootPane(), "ctrl shift P", () -> moMan("Äá»•i máº­t kháº©u"));
        GiaoDien.phimTat(getRootPane(), "F1", () -> moMan("Trá»£ giÃºp"));
        GiaoDien.phimTat(getRootPane(), "shift F1", this::hienPhimTat);
        // PhÃ­m "Trang chá»§": Alt+Home
        GiaoDien.phimTat(
                getRootPane(),
                "alt HOME",
                () -> {
                    moMan("ChÃ o");
                });
    }

    private void hienPhimTat() {
        JTextArea t =
                new JTextArea(
                        GiaoDien.lietKe(
                                "CHUYá»‚N MÃ€N HÃŒNH",
                                "  Ctrl+1 BÃ¡n hÃ ng POS     Ctrl+2 HÃ³a Ä‘Æ¡n      Ctrl+3 Äá»•i tráº£",
                                "  Ctrl+4 KhÃ¡ch hÃ ng       Ctrl+5 Sáº£n pháº©m     Ctrl+6 Phiáº¿u nháº­p",
                                "  Ctrl+7 Khuyáº¿n mÃ£i (QL)  Ctrl+8 Ca lÃ m viá»‡c  Ctrl+9 Danh má»¥c ca"
                                        + " (QL)",
                                "  Ctrl+0 NhÃ¢n viÃªn (QL)   Ctrl+B BÃ¡o cÃ¡o (QL) Ctrl+G Sao lÆ°u/thiáº¿t"
                                        + " láº­p (QL)",
                                "  Alt+Home Dashboard      Ctrl+L ÄÄƒng xuáº¥t    Ctrl+Shift+P Äá»•i máº­t"
                                        + " kháº©u",
                                "",
                                "TRONG MÃ€N HÃŒNH DANH Má»¤C",
                                "  Ctrl+N Táº¡o má»›i   Ctrl+S LÆ°u   Ctrl+D XÃ³a   F5 Táº£i láº¡i   Ctrl+F Ã”"
                                        + " tÃ¬m kiáº¿m",
                                "",
                                "BÃN HÃ€NG POS",
                                "  F2 TÃ¬m sáº£n pháº©m   Báº¥m ThÃªm trÃªn tháº» / báº¥m Ä‘Ãºp trong báº£ng",
                                "  Trong giá»: âˆ’ / + Ä‘á»•i sá»‘ lÆ°á»£ng, Ã— bá» dÃ²ng, Chá»n KM Ä‘á»ƒ giáº£m",
                                "  F8 LÆ°u Ä‘Æ¡n   F9 Tiá»n máº·t   F10 Chuyá»ƒn khoáº£n QR",
                                "  Ctrl+N ÄÆ¡n má»›i",
                                "",
                                "Há»˜P THANH TOÃN",
                                "  F2 Hiá»‡n QR   Enter Thanh toÃ¡n & xuáº¥t hÃ³a Ä‘Æ¡n   Esc Äá»ƒ sau",
                                "",
                                "TRá»¢ GIÃšP VÃ€ HÃ“A ÄÆ N",
                                "  F1 HÆ°á»›ng dáº«n sá»­ dá»¥ng   Shift+F1 PhÃ­m táº¯t",
                                "  Ctrl+P trong HÃ³a Ä‘Æ¡n: xuáº¥t PDF vÃ o D:\\HoaDonGreenSmart",
                                "",
                                "RÃª chuá»™t lÃªn nÃºt Ä‘á»ƒ xem phÃ­m táº¯t tÆ°Æ¡ng á»©ng."));
        t.setEditable(false);
        t.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        t.setBackground(GiaoDien.SURFACE);
        t.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        JOptionPane.showMessageDialog(this, t, "PhÃ­m táº¯t", JOptionPane.PLAIN_MESSAGE);
    }

    // ===== Sao lÆ°u tá»± Ä‘á»™ng =====
    private void batBackup() {
        int n = docSoThietLap("backup.minutes", 0);
        if (n <= 0 || n > 10080 || !"ADMIN".equals(tk.getQuyen())) return;
        backup =
                new javax.swing.Timer(
                        n * 60000,
                        e -> {
                            try {
                                if (ConnectDB.con == null || ConnectDB.con.isClosed()) {
                                    ConnectDB.getInstance().connect();
                                }
                                String file = docThietLap("backup.file", "");
                                if (file.isEmpty()) return;
                                String ten =
                                        file.replaceFirst("\\.bak$", "")
                                                + "_"
                                                + java.time.LocalDateTime.now()
                                                        .format(
                                                                java.time.format.DateTimeFormatter
                                                                        .ofPattern(
                                                                                "yyyyMMdd_HHmmss"))
                                                + ".bak";
                                new SaoLuu_DAO().saoLuu(ten, tk);
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                System.err.println("Sao lÆ°u tá»± Ä‘á»™ng tháº¥t báº¡i: " + ex.getMessage());
                            }
                        });
        backup.start();
    }

    private void dungBackup() {
        if (backup != null) backup.stop();
    }

    // ===== Äiá»u hÆ°á»›ng =====
    private BanHang_GUI pos() {
        if (pos == null) {
            pos = new BanHang_GUI(tk);
            panels.put("BÃ¡n hÃ ng POS", pos);
            noiDung.add(pos, "BÃ¡n hÃ ng POS");
        }
        return pos;
    }

    private void danhDauMenu(String hienTai, String tieuDe) {
        for (Map.Entry<String, GiaoDien.NutMenu> e : menu.entrySet())
            e.getValue().datHoatDong(e.getKey().equals(hienTai));
        lblTrang.setText(tieuDe);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        moMan(e.getActionCommand());
    }

    public void moMan(String s) {
        try {
            if (s.equals("ÄÄƒng xuáº¥t")) {
                if (xacNhan(this, "ÄÄƒng xuáº¥t? Giá» chÆ°a lÆ°u sáº½ máº¥t.")) {
                    dungBackup();
                    if (dongHo != null) dongHo.stop();
                    if (ConnectDB.con != null) ConnectDB.con.close();
                    dispose();
                    new DangNhap_GUI().setVisible(true);
                }
                return;
            }
            if (s.equals("Äá»•i máº­t kháº©u")) {
                JPasswordField f = new JPasswordField();
                if (JOptionPane.showConfirmDialog(
                                this, f, "Máº­t kháº©u má»›i", JOptionPane.OK_CANCEL_OPTION)
                        == JOptionPane.OK_OPTION) {
                    new TaiKhoan_DAO()
                            .updateMatKhau(tk.getTenDangNhap(), new String(f.getPassword()), tk);
                    JOptionPane.showMessageDialog(this, "ÄÃ£ Ä‘á»•i máº­t kháº©u.");
                }
                return;
            }
            if (!"ADMIN".equals(tk.getQuyen())
                    && java.util.Arrays.asList(
                                    "Khuyáº¿n mÃ£i",
                                    "Danh má»¥c ca",
                                    "NhÃ¢n viÃªn / tÃ i khoáº£n",
                                    "BÃ¡o cÃ¡o",
                                    "Sao lÆ°u / thiáº¿t láº­p")
                            .contains(s)) throw new SQLException("Chá»©c nÄƒng dÃ nh cho quáº£n lÃ½.");
            boolean daMo = panels.containsKey(s);
            if (!daMo) {
                JPanel p;
                switch (s) {
                    case "Trá»£ giÃºp":
                        p = new TroGiup_GUI(tk);
                        break;
                    case "BÃ¡n hÃ ng POS":
                        p = pos();
                        break;
                    case "HÃ³a Ä‘Æ¡n":
                        p = new HoaDon_GUI(tk, pos());
                        break;
                    case "Sáº£n pháº©m":
                        p = new SanPham_GUI(tk);
                        break;
                    case "KhÃ¡ch hÃ ng":
                        p = new KhachHang_GUI(tk);
                        break;
                    case "Khuyáº¿n mÃ£i":
                        p = new ChuongTrinhKhuyenMai_GUI(tk);
                        break;
                    case "Phiáº¿u nháº­p hÃ ng":
                        p = new PhieuNhapHang_GUI(tk);
                        break;
                    case "Äá»•i tráº£":
                        p = new PhieuDoiTra_GUI(tk);
                        break;
                    case "Ca lÃ m viá»‡c":
                        p = new PhanCongCa_GUI(tk);
                        break;
                    case "Danh má»¥c ca":
                        p = new CaLamViec_GUI(tk);
                        break;
                    case "NhÃ¢n viÃªn / tÃ i khoáº£n":
                        p = new NhanVien_GUI(tk);
                        break;
                    case "BÃ¡o cÃ¡o":
                        p = new BaoCao_GUI(tk);
                        break;
                    case "Sao lÆ°u / thiáº¿t láº­p":
                        p = new SaoLuu_GUI(tk);
                        break;
                    default:
                        return;
                }
                panels.put(s, p);
                noiDung.add(p, s);
            }
            ((CardLayout) noiDung.getLayout()).show(noiDung, s);
            if (daMo && panels.get(s) instanceof BanHang_GUI)
                ((BanHang_GUI) panels.get(s)).loadDanhMuc();
            danhDauMenu(
                    s,
                    "ChÃ o".equals(s)
                            ? ("ADMIN".equals(tk.getQuyen())
                                    ? "Dashboard Â· Quáº£n lÃ½"
                                    : "Dashboard Â· NhÃ¢n viÃªn")
                            : s);
            if ("ChÃ o".equals(s)) lamMoiDashboard();
        } catch (java.sql.SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this, "Lá»—i SQL:\n" + ex.getMessage(), "Lá»—i SQL", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            thongBaoLoi(this, ex);
        }
    }

    /** Má»Ÿ Ä‘Ãºng cÃ´ng viá»‡c Ä‘ang Ä‘Æ°á»£c chá»n á»Ÿ dashboard. KhÃ´ng tá»± duyá»‡t hay ghi giao dá»‹ch. */
    public void moCongViec(String loai, String ma) throws Exception {
        if ("HÃ³a Ä‘Æ¡n".equals(loai)) {
            pos().moHoaDon(ma);
            moMan("BÃ¡n hÃ ng POS");
        } else if ("Nháº­p hÃ ng".equals(loai)) {
            moMan("Phiáº¿u nháº­p hÃ ng");
            JPanel p = panels.get("Phiáº¿u nháº­p hÃ ng");
            if (p instanceof PhieuNhapHang_GUI) ((PhieuNhapHang_GUI) p).chonPhieu(ma);
        } else if ("Äá»•i tráº£".equals(loai)) {
            moMan("Äá»•i tráº£");
            JPanel p = panels.get("Äá»•i tráº£");
            if (p instanceof PhieuDoiTra_GUI) ((PhieuDoiTra_GUI) p).chonPhieu(ma);
        } else if ("Sáº£n pháº©m".equals(loai)) {
            moMan("Sáº£n pháº©m");
            JPanel p = panels.get("Sáº£n pháº©m");
            if (p instanceof SanPham_GUI) ((SanPham_GUI) p).chonSanPham(ma);
        } else if ("Má»Ÿ ca".equals(loai) || "Ca lÃ m viá»‡c".equals(loai)) {
            moMan("Ca lÃ m viá»‡c");
            JPanel p = panels.get("Ca lÃ m viá»‡c");
            if (p instanceof PhanCongCa_GUI) {
                PhanCongCa_GUI gui = (PhanCongCa_GUI) p;
                gui.chonPhanCong(ma);
                if ("Má»Ÿ ca".equals(loai)) gui.moCaDaChon();
            }
        }
    }

    private void thongBaoLoi(Component c, Exception e) {
        JOptionPane.showMessageDialog(
                c, e.getMessage(), "KhÃ´ng thá»±c hiá»‡n Ä‘Æ°á»£c", JOptionPane.ERROR_MESSAGE);
    }

    private boolean xacNhan(Component c, String s) {
        return JOptionPane.showConfirmDialog(c, s, "XÃ¡c nháº­n", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }

    private static String docThietLap(String key, String macDinh) {
        java.util.Properties p = new java.util.Properties();
        java.nio.file.Path file = java.nio.file.Paths.get("config.properties");
        if (!java.nio.file.Files.exists(file))
            file = java.nio.file.Paths.get("config.example.properties");
        if (!java.nio.file.Files.exists(file)) return macDinh;
        try (java.io.Reader r = java.nio.file.Files.newBufferedReader(file)) {
            p.load(r);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return macDinh;
        }
        String v = p.getProperty(key, macDinh);
        return v == null || v.trim().isEmpty() ? macDinh : v.trim();
    }

    private static int docSoThietLap(String key, int macDinh) {
        try {
            return Integer.parseInt(docThietLap(key, ""));
        } catch (NumberFormatException e) {
            return macDinh;
        }
    }

    @Override
    public void dispose() {
        dungBackup();
        if (dongHo != null) dongHo.stop();
        if (lamMoi != null) lamMoi.stop();
        super.dispose();
    }
}
