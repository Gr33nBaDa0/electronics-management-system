package gui;

import connectDB.ConnectDB;

import dao.SaoLuu_DAO;
import dao.TaiKhoan_DAO;

import entity.TaiKhoan;

import until.GiaoDien;

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
    // Màu và font đặt ở đầu lớp; navi sử dụng chính xác #1B5E20.
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

    // Nhóm menu: {nhãn, gợi ý phím, KeyStroke, chỉ quản lý (true/false)}
    private static final String[][] NHOM_BAN_HANG = {
        {"Bán hàng POS", "", "ctrl 1", "0"},
        {"Hóa đơn", "", "ctrl 2", "0"},
        {"Đổi trả", "", "ctrl 3", "0"},
        {"Khách hàng", "", "ctrl 4", "0"}
    };
    private static final String[][] NHOM_KHO = {
        {"Sản phẩm", "", "ctrl 5", "0"},
        {"Phiếu nhập hàng", "", "ctrl 6", "0"},
        {"Khuyến mãi", "", "ctrl 7", "1"}
    };
    private static final String[][] NHOM_VAN_HANH = {
        {"Ca làm việc", "", "ctrl 8", "0"},
        {"Danh mục ca", "", "ctrl 9", "1"}
    };
    private static final String[][] NHOM_QUAN_TRI = {
        {"Nhân viên / tài khoản", "", "ctrl 0", "1"},
        {"Báo cáo", "", "ctrl B", "1"},
        {"Sao lưu / thiết lập", "", "ctrl G", "1"}
    };

    public MainFrame(TaiKhoan tk) {
        this.tk = tk;
        GiaoDien.apDung();
        setTitle("GreenSmart — " + tk.getNhanVien().getHoTen());
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Insets edges = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
        int width = screen.width - edges.left - edges.right,
                height = screen.height - edges.top - edges.bottom;
        // Mở hết vùng làm việc; vẫn giữ thanh tiêu đề và nút đóng của Windows.
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
        panels.put("Chào", chao);
        noiDung.add(chao, "Chào");

        addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        if (xacNhan(MainFrame.this, "Thoát ứng dụng? Giỏ chưa lưu sẽ mất.")) {
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
                            JPanel p = panels.get("Chào");
                            if (p != null && p.isShowing()) lamMoiDashboard();
                        });
        lamMoi.start();
        danhDauMenu(
                "Chào",
                "ADMIN".equals(tk.getQuyen()) ? "Dashboard · Quản lý" : "Dashboard · Nhân viên");
    }

    // ===== Thanh menu bên trái =====
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
        JLabel khau = new JLabel("QUẢN LÝ ĐIỆN MÁY");
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
        GiaoDien.NutMenu trangChu = new GiaoDien.NutMenu("Dashboard", "");
        trangChu.setActionCommand("Chào");
        trangChu.addActionListener(this);
        trangChu.datHoatDong(true);
        menu.put("Chào", trangChu);
        dau.add(trangChu);

        navi.add(dau, BorderLayout.NORTH);
        JPanel m = new JPanel();
        m.setLayout(new BoxLayout(m, BoxLayout.Y_AXIS));
        m.setBackground(MAU_NAVI);
        m.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        themNhom(m, "BÁN HÀNG", NHOM_BAN_HANG, admin);
        themNhom(m, "KHO VÀ DANH MỤC", NHOM_KHO, admin);
        themNhom(m, "VẬN HÀNH", NHOM_VAN_HANH, admin);
        if (admin) themNhom(m, "QUẢN TRỊ", NHOM_QUAN_TRI, admin);
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
        GiaoDien.NutMenu troGiup = new GiaoDien.NutMenu("Trợ giúp", "F1");
        troGiup.setActionCommand("Trợ giúp");
        troGiup.addActionListener(this);
        menu.put("Trợ giúp", troGiup);
        cuoi.add(troGiup);
        cuoi.add(Box.createVerticalStrut(6));
        cuoi.add(taoTheNguoiDung());
        cuoi.add(Box.createVerticalStrut(6));
        JPanel hangCuoi = new JPanel(new java.awt.GridLayout(1, 2, 6, 0));
        hangCuoi.setOpaque(false);
        for (String ten : new String[] {"Đổi mật khẩu", "Đăng xuất"}) {
            JButton nut = GiaoDien.nut(ten);
            nut.setActionCommand(ten);
            nut.addActionListener(this);
            nut.setPreferredSize(new Dimension(90, 32));
            nut.setFont(GiaoDien.phong(Font.BOLD, 12));
            nut.setToolTipText(ten + ("Đăng xuất".equals(ten) ? " (Ctrl+L)" : " (Ctrl+Shift+P)"));
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

    /** Thẻ nhỏ cuối menu: chữ cái đầu của tên trong vòng tròn, họ tên và vai trò. */
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
                        ("ADMIN".equals(tk.getQuyen()) ? "Quản lý" : "Bán hàng")
                                + " · "
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

    // ===== Thanh tiêu đề và thanh trạng thái =====
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
        JLabel goiY = new JLabel("F1: Trợ giúp · Shift+F1: phím tắt");
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
        lblKetNoi.setText(noi ? "●  Đã kết nối SQL Server" : "●  Chưa kết nối SQL Server");
        lblKetNoi.setForeground(noi ? GiaoDien.SUCCESS : GiaoDien.ERROR);
        lblGio.setText(
                LocalDateTime.now()
                        .format(
                                DateTimeFormatter.ofPattern(
                                        "EEEE, dd/MM/yyyy · HH:mm", new Locale("vi", "VN"))));
    }

    private void batDongHo() {
        dongHo = new javax.swing.Timer(15000, e -> capNhatTrangThai());
        dongHo.start();
    }

    // ===== Trang chào =====
    private JPanel taoTrangChao() {
        return "ADMIN".equals(tk.getQuyen())
                ? new DashboardQuanLy_GUI(tk, this)
                : new DashboardNhanVien_GUI(tk, this);
    }

    private void lamMoiDashboard() {
        JPanel p = panels.get("Chào");
        if (p instanceof DashboardQuanLy_GUI) ((DashboardQuanLy_GUI) p).taiDuLieu();
        if (p instanceof DashboardNhanVien_GUI) ((DashboardNhanVien_GUI) p).taiDuLieu();
    }

    // ===== Phím tắt =====
    private void ganPhimTat() {
        boolean admin = "ADMIN".equals(tk.getQuyen());
        String[][][] tatCa = {NHOM_BAN_HANG, NHOM_KHO, NHOM_VAN_HANH, NHOM_QUAN_TRI};
        for (String[][] nhom : tatCa)
            for (String[] a : nhom) {
                if (!admin && "1".equals(a[3])) continue;
                String ten = a[0];
                GiaoDien.phimTat(getRootPane(), a[2], () -> moMan(ten));
            }
        GiaoDien.phimTat(getRootPane(), "ctrl L", () -> moMan("Đăng xuất"));
        GiaoDien.phimTat(getRootPane(), "ctrl shift P", () -> moMan("Đổi mật khẩu"));
        GiaoDien.phimTat(getRootPane(), "F1", () -> moMan("Trợ giúp"));
        GiaoDien.phimTat(getRootPane(), "shift F1", this::hienPhimTat);
        // Phím "Trang chủ": Alt+Home
        GiaoDien.phimTat(
                getRootPane(),
                "alt HOME",
                () -> {
                    moMan("Chào");
                });
    }

    private void hienPhimTat() {
        JTextArea t =
                new JTextArea(
                        GiaoDien.lietKe(
                                "CHUYỂN MÀN HÌNH",
                                "  Ctrl+1 Bán hàng POS     Ctrl+2 Hóa đơn      Ctrl+3 Đổi trả",
                                "  Ctrl+4 Khách hàng       Ctrl+5 Sản phẩm     Ctrl+6 Phiếu nhập",
                                "  Ctrl+7 Khuyến mãi (QL)  Ctrl+8 Ca làm việc  Ctrl+9 Danh mục ca"
                                        + " (QL)",
                                "  Ctrl+0 Nhân viên (QL)   Ctrl+B Báo cáo (QL) Ctrl+G Sao lưu/thiết"
                                        + " lập (QL)",
                                "  Alt+Home Dashboard      Ctrl+L Đăng xuất    Ctrl+Shift+P Đổi mật"
                                        + " khẩu",
                                "",
                                "TRONG MÀN HÌNH DANH MỤC",
                                "  Ctrl+N Tạo mới   Ctrl+S Lưu   Ctrl+D Xóa   F5 Tải lại   Ctrl+F Ô"
                                        + " tìm kiếm",
                                "",
                                "BÁN HÀNG POS",
                                "  F2 Tìm sản phẩm   Bấm Thêm trên thẻ / bấm đúp trong bảng",
                                "  Trong giỏ: − / + đổi số lượng, × bỏ dòng, Chọn KM để giảm",
                                "  F8 Lưu đơn   F9 Tiền mặt   F10 Chuyển khoản QR",
                                "  Ctrl+N Đơn mới",
                                "",
                                "HỘP THANH TOÁN",
                                "  F2 Hiện QR   Enter Thanh toán & xuất hóa đơn   Esc Để sau",
                                "",
                                "TRỢ GIÚP VÀ HÓA ĐƠN",
                                "  F1 Hướng dẫn sử dụng   Shift+F1 Phím tắt",
                                "  Ctrl+P trong Hóa đơn: xuất PDF vào D:\\HoaDonGreenSmart",
                                "",
                                "Rê chuột lên nút để xem phím tắt tương ứng."));
        t.setEditable(false);
        t.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        t.setBackground(GiaoDien.SURFACE);
        t.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        JOptionPane.showMessageDialog(this, t, "Phím tắt", JOptionPane.PLAIN_MESSAGE);
    }

    // ===== Sao lưu tự động =====
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
                                System.err.println("Sao lưu tự động thất bại: " + ex.getMessage());
                            }
                        });
        backup.start();
    }

    private void dungBackup() {
        if (backup != null) backup.stop();
    }

    // ===== Điều hướng =====
    private BanHang_GUI pos() {
        if (pos == null) {
            pos = new BanHang_GUI(tk);
            panels.put("Bán hàng POS", pos);
            noiDung.add(pos, "Bán hàng POS");
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
            if (s.equals("Đăng xuất")) {
                if (xacNhan(this, "Đăng xuất? Giỏ chưa lưu sẽ mất.")) {
                    dungBackup();
                    if (dongHo != null) dongHo.stop();
                    if (ConnectDB.con != null) ConnectDB.con.close();
                    dispose();
                    new DangNhap_GUI().setVisible(true);
                }
                return;
            }
            if (s.equals("Đổi mật khẩu")) {
                JPasswordField f = new JPasswordField();
                if (JOptionPane.showConfirmDialog(
                                this, f, "Mật khẩu mới", JOptionPane.OK_CANCEL_OPTION)
                        == JOptionPane.OK_OPTION) {
                    new TaiKhoan_DAO()
                            .updateMatKhau(tk.getTenDangNhap(), new String(f.getPassword()), tk);
                    JOptionPane.showMessageDialog(this, "Đã đổi mật khẩu.");
                }
                return;
            }
            if (!"ADMIN".equals(tk.getQuyen())
                    && java.util.Arrays.asList(
                                    "Khuyến mãi",
                                    "Danh mục ca",
                                    "Nhân viên / tài khoản",
                                    "Báo cáo",
                                    "Sao lưu / thiết lập")
                            .contains(s)) throw new SQLException("Chức năng dành cho quản lý.");
            boolean daMo = panels.containsKey(s);
            if (!daMo) {
                JPanel p;
                switch (s) {
                    case "Trợ giúp":
                        p = new TroGiup_GUI(tk);
                        break;
                    case "Bán hàng POS":
                        p = pos();
                        break;
                    case "Hóa đơn":
                        p = new HoaDon_GUI(tk, pos());
                        break;
                    case "Sản phẩm":
                        p = new SanPham_GUI(tk);
                        break;
                    case "Khách hàng":
                        p = new KhachHang_GUI(tk);
                        break;
                    case "Khuyến mãi":
                        p = new ChuongTrinhKhuyenMai_GUI(tk);
                        break;
                    case "Phiếu nhập hàng":
                        p = new PhieuNhapHang_GUI(tk);
                        break;
                    case "Đổi trả":
                        p = new PhieuDoiTra_GUI(tk);
                        break;
                    case "Ca làm việc":
                        p = new PhanCongCa_GUI(tk);
                        break;
                    case "Danh mục ca":
                        p = new CaLamViec_GUI(tk);
                        break;
                    case "Nhân viên / tài khoản":
                        p = new NhanVien_GUI(tk);
                        break;
                    case "Báo cáo":
                        p = new BaoCao_GUI(tk);
                        break;
                    case "Sao lưu / thiết lập":
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
                    "Chào".equals(s)
                            ? ("ADMIN".equals(tk.getQuyen())
                                    ? "Dashboard · Quản lý"
                                    : "Dashboard · Nhân viên")
                            : s);
            if ("Chào".equals(s)) lamMoiDashboard();
        } catch (java.sql.SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this, "Lỗi SQL:\n" + ex.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            thongBaoLoi(this, ex);
        }
    }

    /** Mở đúng công việc đang được chọn ở dashboard. Không tự duyệt hay ghi giao dịch. */
    public void moCongViec(String loai, String ma) throws Exception {
        if ("Hóa đơn".equals(loai)) {
            pos().moHoaDon(ma);
            moMan("Bán hàng POS");
        } else if ("Nhập hàng".equals(loai)) {
            moMan("Phiếu nhập hàng");
            JPanel p = panels.get("Phiếu nhập hàng");
            if (p instanceof PhieuNhapHang_GUI) ((PhieuNhapHang_GUI) p).chonPhieu(ma);
        } else if ("Đổi trả".equals(loai)) {
            moMan("Đổi trả");
            JPanel p = panels.get("Đổi trả");
            if (p instanceof PhieuDoiTra_GUI) ((PhieuDoiTra_GUI) p).chonPhieu(ma);
        } else if ("Sản phẩm".equals(loai)) {
            moMan("Sản phẩm");
            JPanel p = panels.get("Sản phẩm");
            if (p instanceof SanPham_GUI) ((SanPham_GUI) p).chonSanPham(ma);
        } else if ("Mở ca".equals(loai) || "Ca làm việc".equals(loai)) {
            moMan("Ca làm việc");
            JPanel p = panels.get("Ca làm việc");
            if (p instanceof PhanCongCa_GUI) {
                PhanCongCa_GUI gui = (PhanCongCa_GUI) p;
                gui.chonPhanCong(ma);
                if ("Mở ca".equals(loai)) gui.moCaDaChon();
            }
        }
    }

    private void thongBaoLoi(Component c, Exception e) {
        JOptionPane.showMessageDialog(
                c, e.getMessage(), "Không thực hiện được", JOptionPane.ERROR_MESSAGE);
    }

    private boolean xacNhan(Component c, String s) {
        return JOptionPane.showConfirmDialog(c, s, "Xác nhận", JOptionPane.YES_NO_OPTION)
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
