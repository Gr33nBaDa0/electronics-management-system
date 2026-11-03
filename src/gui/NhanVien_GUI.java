package gui;

import connectDB.ConnectDB;

import dao.NhanVien_DAO;
import dao.TaiKhoan_DAO;

import entity.NhanVien;
import entity.TaiKhoan;

import util.GiaoDien;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class NhanVien_GUI extends JPanel implements ActionListener {
    // MÃ u dÃ¹ng chung Ä‘Æ°á»£c khai bÃ¡o á»Ÿ Ä‘áº§u lá»›p.
    private static final Color PRIMARY = GiaoDien.PRIMARY,
            DARK = GiaoDien.DARK,
            BACKGROUND = GiaoDien.BACKGROUND,
            SURFACE = GiaoDien.SURFACE,
            TEXT = GiaoDien.TEXT,
            BORDER = GiaoDien.BORDER,
            ERROR = GiaoDien.ERROR;

    private final TaiKhoan tk;
    private List<NhanVien> ds = new ArrayList<>(), hienThi = new ArrayList<>();
    private String maCu;
    private byte[] anh;
    private final JTextField ma = new JTextField(12),
            ten = new JTextField(15),
            sdt = new JTextField(12),
            email = new JTextField(15),
            diaChi = new JTextField(15),
            cccd = new JTextField(15),
            sinh = new JTextField("2000-01-01", 12),
            vaoLam = new JTextField(LocalDate.now().toString(), 12),
            user = new JTextField(12),
            txtTim = new JTextField(18);
    private final JPasswordField pass = new JPasswordField(12);
    private final JComboBox<String> vaiTro = new JComboBox<>(new String[] {"BÃ¡n hÃ ng", "Quáº£n lÃ½"}),
            gioiTinh = new JComboBox<>(new String[] {"Nam", "Ná»¯", "KhÃ¡c"}),
            locVaiTro = new JComboBox<>(new String[] {"Táº¥t cáº£", "BÃ¡n hÃ ng", "Quáº£n lÃ½"});
    private final JButton btnMoi = taoNut("Táº¡o má»›i"),
            btnLuu = taoNut("LÆ°u há»“ sÆ¡ + tÃ i khoáº£n"),
            btnXoa = taoNut("XÃ³a"),
            btnTim = taoNut("TÃ¬m"),
            btnAnh = taoNut("Chá»n áº£nh tháº»"),
            btnReset = taoNut("Äáº·t láº¡i máº­t kháº©u"),
            btnTai = taoNut("Táº£i láº¡i / bá» lá»c");
    private final JLabel lblAnh = new JLabel("ChÆ°a chá»n áº£nh"), lblTong = new JLabel();
    private final DefaultTableModel model =
            taoModel(
                    "MÃ£ NV",
                    "Há» tÃªn",
                    "Vai trÃ²",
                    "SÄT",
                    "Email",
                    "CCCD",
                    "Giá»›i tÃ­nh",
                    "NgÃ y sinh",
                    "NgÃ y vÃ o lÃ m");
    private final JTable table = taoBang(model);

    public NhanVien_GUI(TaiKhoan tk) {
        this.tk = tk;
        GiaoDien.boCuc(this);

        btnLuu.setText("LÆ°u");
        btnAnh.setText("Chá»n áº£nh");
        btnLuu.setPreferredSize(new Dimension(94, 38));
        btnAnh.setPreferredSize(new Dimension(112, 38));
        sinh.setToolTipText("NgÃ y sinh: yyyy-MM-dd");
        vaoLam.setToolTipText("NgÃ y vÃ o lÃ m: yyyy-MM-dd");
        JPanel form =
                GiaoDien.form(
                        2,
                        "MÃ£ nhÃ¢n viÃªn",
                        ma,
                        "Há» tÃªn",
                        ten,
                        "Vai trÃ²",
                        vaiTro,
                        "Sá»‘ Ä‘iá»‡n thoáº¡i",
                        sdt,
                        "Email",
                        email,
                        "Äá»‹a chá»‰",
                        diaChi,
                        "Sá»‘ CCCD",
                        cccd,
                        "Giá»›i tÃ­nh",
                        gioiTinh,
                        "NgÃ y sinh",
                        sinh,
                        "NgÃ y vÃ o lÃ m",
                        vaoLam,
                        "TÃªn Ä‘Äƒng nháº­p má»›i",
                        user,
                        "Máº­t kháº©u má»›i",
                        pass);
        JPanel trai =
                GiaoDien.the(
                        "Há»“ sÆ¡ nhÃ¢n viÃªn",
                        GiaoDien.cuonForm(form),
                        GiaoDien.doc(
                                taoDong(btnAnh, lblAnh),
                                taoDong(btnMoi, btnLuu, btnXoa),
                                taoDong(btnReset)));
        JPanel danhSach = new JPanel(new BorderLayout(0, 12));
        danhSach.setOpaque(false);
        danhSach.add(
                GiaoDien.doc(
                        GiaoDien.form(2, "MÃ£ / há» tÃªn", txtTim, "Vai trÃ²", locVaiTro),
                        taoDong(btnTim, btnTai)),
                BorderLayout.NORTH);
        danhSach.add(GiaoDien.cuon(table), BorderLayout.CENTER);
        JPanel phai = GiaoDien.the("Danh sÃ¡ch nhÃ¢n viÃªn", danhSach, taoDong(lblTong));
        trai.setMinimumSize(new Dimension(360, 180));
        phai.setMinimumSize(new Dimension(400, 180));
        add(
                GiaoDien.tach(javax.swing.JSplitPane.HORIZONTAL_SPLIT, trai, phai, .44),
                BorderLayout.CENTER);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        GiaoDien.trangTri(this);
        for (JButton b : new JButton[] {btnMoi, btnLuu, btnXoa, btnAnh, btnReset, btnTim, btnTai})
            b.addActionListener(this);
        table.getSelectionModel()
                .addListSelectionListener(
                        e -> {
                            if (!e.getValueIsAdjusting()) chon();
                        });
        ganPhimTat();
        loadDataToTable();
    }

    private void loadDataToTable() {
        model.setRowCount(0);
        hienThi.clear();
        try {
            if (ConnectDB.con == null || ConnectDB.con.isClosed()) {
                ConnectDB.getInstance().connect();
            }

            List<NhanVien> list = new NhanVien_DAO().getAllNhanVien();
            ds = list;

            String tim = txtTim.getText().toLowerCase(java.util.Locale.ROOT);
            for (NhanVien n : list) {
                if (!(n.getMaNhanVien() + " " + n.getHoTen())
                        .toLowerCase(java.util.Locale.ROOT)
                        .contains(tim)) continue;
                if (locVaiTro.getSelectedIndex() > 0
                        && !n.getVaiTro().equals(locVaiTro.getSelectedItem())) continue;
                hienThi.add(n);
                model.addRow(
                        new Object[] {
                            n.getMaNhanVien(),
                            n.getHoTen(),
                            n.getVaiTro(),
                            n.getSdt(),
                            n.getEmail(),
                            n.getSoCCCD(),
                            n.getGioiTinh(),
                            n.getNgaySinh(),
                            n.getNgayBatDauLV()
                        });
            }
            lblTong.setText("Tá»•ng: " + hienThi.size() + " nhÃ¢n viÃªn");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Lá»—i táº£i dá»¯ liá»‡u SQL:\n" + e.getMessage(),
                    "Lá»—i SQL",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    this, e.getMessage(), "ThÃ´ng bÃ¡o", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void chon() {
        int i = table.getSelectedRow();
        if (i < 0 || i >= hienThi.size()) return;
        NhanVien n = hienThi.get(i);
        maCu = n.getMaNhanVien();
        ma.setText(maCu);
        ma.setEditable(false);
        ten.setText(n.getHoTen());
        vaiTro.setSelectedItem(n.getVaiTro());
        sdt.setText(n.getSdt());
        email.setText(n.getEmail());
        diaChi.setText(n.getDiaChi());
        cccd.setText(n.getSoCCCD());
        gioiTinh.setSelectedItem(n.getGioiTinh());
        sinh.setText(String.valueOf(n.getNgaySinh()));
        vaoLam.setText(String.valueOf(n.getNgayBatDauLV()));
        anh = n.getHinhTheNV();
        lblAnh.setText(anh == null ? "ChÆ°a cÃ³ áº£nh" : "CÃ³ áº£nh tháº»");
        user.setText("");
        user.setEditable(false);
        pass.setText("");
        pass.setEnabled(false);
    }

    private void moi() {
        maCu = null;
        anh = null;
        table.clearSelection();
        for (JTextField f : new JTextField[] {ma, ten, sdt, email, diaChi, cccd, user, pass})
            f.setText("");
        ma.setEditable(true);
        user.setEditable(true);
        pass.setEnabled(true);
        lblAnh.setText("ChÆ°a chá»n áº£nh");
    }

    private void luu() throws Exception {
        if (!ma.getText().matches("(NV|QL)\\d{3}"))
            throw new IllegalArgumentException("MÃ£ vÃ­ dá»¥ NV001 hoáº·c QL001.");
        kiemTraRong(ten.getText(), "há» tÃªn");
        kiemTraSDT(sdt.getText());
        if (!cccd.getText().matches("\\d{12}"))
            throw new IllegalArgumentException("CCCD gá»“m 12 chá»¯ sá»‘.");
        NhanVien n = new NhanVien(ma.getText());
        n.setHoTen(ten.getText().trim());
        n.setVaiTro((String) vaiTro.getSelectedItem());
        n.setSdt(sdt.getText());
        n.setEmail(email.getText().trim());
        n.setDiaChi(diaChi.getText().trim());
        n.setSoCCCD(cccd.getText());
        n.setHinhTheNV(anh);
        n.setGioiTinh((String) gioiTinh.getSelectedItem());
        n.setNgaySinh(LocalDate.parse(sinh.getText()));
        n.setNgayBatDauLV(LocalDate.parse(vaoLam.getText()));
        TaiKhoan a = null;
        if (maCu == null) {
            String mk = new String(pass.getPassword());
            kiemTraDoDai(user.getText(), 5, "tÃªn Ä‘Äƒng nháº­p");
            kiemTraDoDai(mk, 128, "máº­t kháº©u");
            a =
                    new TaiKhoan(
                            user.getText().trim(),
                            "Quáº£n lÃ½".equals(n.getVaiTro()) ? "ADMIN" : "BAN_HANG",
                            mk,
                            n);
        }
        if (maCu == null) {
            new NhanVien_DAO().insertNhanVien(n, a, tk);
        } else {
            new NhanVien_DAO().updateNhanVien(n, tk);
        }
        moi();
        loadDataToTable();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        try {
            if (o.equals(btnMoi)) moi();
            else if (o.equals(btnLuu)) luu();
            else if (o.equals(btnTim)) loadDataToTable();
            else if (o.equals(btnTai)) {
                txtTim.setText("");
                locVaiTro.setSelectedIndex(0);
                loadDataToTable();
            } else if (o.equals(btnAnh)) {
                JFileChooser f = new JFileChooser();
                if (f.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    if (f.getSelectedFile().length() > 2 * 1024 * 1024)
                        throw new IllegalArgumentException("áº¢nh tá»‘i Ä‘a 2 MB.");
                    anh = Files.readAllBytes(f.getSelectedFile().toPath());
                    if (javax.imageio.ImageIO.read(new java.io.ByteArrayInputStream(anh)) == null)
                        throw new IllegalArgumentException("File khÃ´ng pháº£i áº£nh.");
                    lblAnh.setText(f.getSelectedFile().getName());
                }
            } else if (o.equals(btnXoa)) {
                if (maCu == null) throw new IllegalArgumentException("Chá»n nhÃ¢n viÃªn.");
                if (xacNhan(this, "XÃ³a há»“ sÆ¡ vÃ  tÃ i khoáº£n chÆ°a cÃ³ lá»‹ch sá»­?")) {
                    new NhanVien_DAO().deleteNhanVien(maCu, tk);
                    moi();
                    loadDataToTable();
                }
            } else if (o.equals(btnReset)) {
                if (maCu == null) throw new IllegalArgumentException("Chá»n nhÃ¢n viÃªn.");
                String username = null;
                for (TaiKhoan a : new TaiKhoan_DAO().getAllTaiKhoan(tk))
                    if (maCu.equals(a.getNhanVien().getMaNhanVien())) username = a.getTenDangNhap();
                if (username == null) throw new IllegalArgumentException("ChÆ°a cÃ³ tÃ i khoáº£n.");
                JPasswordField f = new JPasswordField();
                if (JOptionPane.showConfirmDialog(
                                this,
                                f,
                                "Máº­t kháº©u má»›i cá»§a " + username,
                                JOptionPane.OK_CANCEL_OPTION)
                        == JOptionPane.OK_OPTION)
                    new TaiKhoan_DAO().updateMatKhau(username, new String(f.getPassword()), tk);
            }
        } catch (java.sql.SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this, "Lá»—i SQL:\n" + ex.getMessage(), "Lá»—i SQL", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            thongBaoLoi(this, ex);
        }
    }

    private JTable taoBang(DefaultTableModel m) {
        return GiaoDien.bang(m);
    }

    private JPanel taoDong(Component... cs) {
        return GiaoDien.dong(cs);
    }

    private void thongBaoLoi(Component c, Exception e) {
        JOptionPane.showMessageDialog(
                c, e.getMessage(), "KhÃ´ng thá»±c hiá»‡n Ä‘Æ°á»£c", JOptionPane.ERROR_MESSAGE);
    }

    private void kiemTraDoDai(String s, int n, String ten) {
        kiemTraRong(s, ten);
        if (s.length() > n) throw new IllegalArgumentException(ten + " tá»‘i Ä‘a " + n + " kÃ½ tá»±.");
    }

    private DefaultTableModel taoModel(String... cot) {
        return new DefaultTableModel(cot, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
    }

    private JButton taoNut(String t) {
        return GiaoDien.nut(t);
    }

    private void kiemTraRong(String s, String ten) {
        if (s == null || s.trim().isEmpty())
            throw new IllegalArgumentException("Vui lÃ²ng nháº­p " + ten);
    }

    private void kiemTraSDT(String s) {
        if (s == null || !s.matches("0\\d{9}"))
            throw new IllegalArgumentException("SÄT pháº£i gá»“m 10 chá»¯ sá»‘, báº¯t Ä‘áº§u báº±ng 0.");
    }

    private JLabel taoTieuDe(String t) {
        return GiaoDien.tieuDe(t);
    }

    private boolean xacNhan(Component c, String s) {
        return JOptionPane.showConfirmDialog(c, s, "XÃ¡c nháº­n", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }

    private void ganPhimTat() {
        GiaoDien.phimTatChuan(this, btnMoi, btnLuu, btnXoa, btnTai, txtTim);
        GiaoDien.phimTat(this, "ctrl R", btnReset);
    }
}
