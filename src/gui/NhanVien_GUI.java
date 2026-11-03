package gui;

import connectDB.ConnectDB;

import dao.NhanVien_DAO;
import dao.TaiKhoan_DAO;

import entity.NhanVien;
import entity.TaiKhoan;

import until.GiaoDien;

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
    // Màu dùng chung được khai báo ở đầu lớp.
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
    private final JComboBox<String> vaiTro = new JComboBox<>(new String[] {"Bán hàng", "Quản lý"}),
            gioiTinh = new JComboBox<>(new String[] {"Nam", "Nữ", "Khác"}),
            locVaiTro = new JComboBox<>(new String[] {"Tất cả", "Bán hàng", "Quản lý"});
    private final JButton btnMoi = taoNut("Tạo mới"),
            btnLuu = taoNut("Lưu hồ sơ + tài khoản"),
            btnXoa = taoNut("Xóa"),
            btnTim = taoNut("Tìm"),
            btnAnh = taoNut("Chọn ảnh thẻ"),
            btnReset = taoNut("Đặt lại mật khẩu"),
            btnTai = taoNut("Tải lại / bỏ lọc");
    private final JLabel lblAnh = new JLabel("Chưa chọn ảnh"), lblTong = new JLabel();
    private final DefaultTableModel model =
            taoModel(
                    "Mã NV",
                    "Họ tên",
                    "Vai trò",
                    "SĐT",
                    "Email",
                    "CCCD",
                    "Giới tính",
                    "Ngày sinh",
                    "Ngày vào làm");
    private final JTable table = taoBang(model);

    public NhanVien_GUI(TaiKhoan tk) {
        this.tk = tk;
        GiaoDien.boCuc(this);

        btnLuu.setText("Lưu");
        btnAnh.setText("Chọn ảnh");
        btnLuu.setPreferredSize(new Dimension(94, 38));
        btnAnh.setPreferredSize(new Dimension(112, 38));
        sinh.setToolTipText("Ngày sinh: yyyy-MM-dd");
        vaoLam.setToolTipText("Ngày vào làm: yyyy-MM-dd");
        JPanel form =
                GiaoDien.form(
                        2,
                        "Mã nhân viên",
                        ma,
                        "Họ tên",
                        ten,
                        "Vai trò",
                        vaiTro,
                        "Số điện thoại",
                        sdt,
                        "Email",
                        email,
                        "Địa chỉ",
                        diaChi,
                        "Số CCCD",
                        cccd,
                        "Giới tính",
                        gioiTinh,
                        "Ngày sinh",
                        sinh,
                        "Ngày vào làm",
                        vaoLam,
                        "Tên đăng nhập mới",
                        user,
                        "Mật khẩu mới",
                        pass);
        JPanel trai =
                GiaoDien.the(
                        "Hồ sơ nhân viên",
                        GiaoDien.cuonForm(form),
                        GiaoDien.doc(
                                taoDong(btnAnh, lblAnh),
                                taoDong(btnMoi, btnLuu, btnXoa),
                                taoDong(btnReset)));
        JPanel danhSach = new JPanel(new BorderLayout(0, 12));
        danhSach.setOpaque(false);
        danhSach.add(
                GiaoDien.doc(
                        GiaoDien.form(2, "Mã / họ tên", txtTim, "Vai trò", locVaiTro),
                        taoDong(btnTim, btnTai)),
                BorderLayout.NORTH);
        danhSach.add(GiaoDien.cuon(table), BorderLayout.CENTER);
        JPanel phai = GiaoDien.the("Danh sách nhân viên", danhSach, taoDong(lblTong));
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
        vaiTro.addActionListener(e -> {
            if (maCu == null) {
                capNhatMaNhanVienTiepTheo();
            }
        });
        loadDataToTable();
        moi();
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
            lblTong.setText("Tổng: " + hienThi.size() + " nhân viên");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Lỗi tải dữ liệu SQL:\n" + e.getMessage(),
                    "Lỗi SQL",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    this, e.getMessage(), "Thông báo", JOptionPane.WARNING_MESSAGE);
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
        lblAnh.setText(anh == null ? "Chưa có ảnh" : "Có ảnh thẻ");
        user.setText("");
        user.setEditable(false);
        pass.setText("");
        pass.setEnabled(false);
    }

    private void capNhatMaNhanVienTiepTheo() {
        try {
            String role = (String) vaiTro.getSelectedItem();
            String tienTo = "Quản lý".equals(role) ? "QL" : "NV";
            String maTiepTheo = new NhanVien_DAO().layMaTiepTheo(tienTo);
            ma.setText(maTiepTheo);
            ma.setEditable(false);
            if (maCu == null) {
                user.setText(maTiepTheo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void moi() {
        maCu = null;
        anh = null;
        table.clearSelection();
        for (JTextField f : new JTextField[] {ten, sdt, email, diaChi, cccd, user, pass})
            f.setText("");
        sinh.setText("2000-01-01");
        vaoLam.setText(LocalDate.now().toString());
        capNhatMaNhanVienTiepTheo();
        ma.setEditable(false);
        user.setEditable(true);
        pass.setEnabled(true);
        lblAnh.setText("Chưa chọn ảnh");
    }

    private void luu() throws Exception {
        if (!ma.getText().matches("(NV|QL)\\d{3}"))
            throw new IllegalArgumentException("Mã nhân viên ví dụ NV001 hoặc QL001.");
        kiemTraRong(ten.getText(), "họ tên");
        if (ten.getText().matches(".*\\d.*")) {
            throw new IllegalArgumentException("Họ tên không được chứa chữ số.");
        }
        kiemTraSDT(sdt.getText());
        if (!cccd.getText().matches("\\d{12}"))
            throw new IllegalArgumentException("Số CCCD phải gồm đúng 12 chữ số.");

        NhanVien_DAO nvDao = new NhanVien_DAO();
        // Kiểm tra trùng SĐT
        if (nvDao.kiemTraTrungSDT(sdt.getText(), maCu)) {
            throw new IllegalArgumentException("Số điện thoại '" + sdt.getText() + "' đã tồn tại trong hệ thống.");
        }
        // Kiểm tra trùng CCCD
        if (nvDao.kiemTraTrungCCCD(cccd.getText(), maCu)) {
            throw new IllegalArgumentException("Số CCCD '" + cccd.getText() + "' đã tồn tại trong hệ thống.");
        }
        // Kiểm tra trùng Email
        String emailVal = email.getText().trim();
        if (!emailVal.isEmpty()) {
            if (!emailVal.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new IllegalArgumentException("Email không đúng định dạng.");
            }
            if (nvDao.kiemTraTrungEmail(emailVal, maCu)) {
                throw new IllegalArgumentException("Email '" + emailVal + "' đã tồn tại trong hệ thống.");
            }
        }

        NhanVien n = new NhanVien(ma.getText());
        n.setHoTen(ten.getText().trim());
        n.setVaiTro((String) vaiTro.getSelectedItem());
        n.setSdt(sdt.getText());
        n.setEmail(emailVal.isEmpty() ? null : emailVal);
        n.setDiaChi(diaChi.getText().trim());
        n.setSoCCCD(cccd.getText());
        n.setHinhTheNV(anh);
        n.setGioiTinh((String) gioiTinh.getSelectedItem());
        n.setNgaySinh(LocalDate.parse(sinh.getText()));
        n.setNgayBatDauLV(LocalDate.parse(vaoLam.getText()));

        TaiKhoan a = null;
        if (maCu == null) {
            String u = user.getText().trim();
            kiemTraRong(u, "tên đăng nhập");
            kiemTraDoDai(u, 5, "tên đăng nhập");
            
            // Kiểm tra trùng tên đăng nhập
            if (new TaiKhoan_DAO().kiemTraTrungTenDangNhap(u)) {
                throw new IllegalArgumentException("Tên đăng nhập '" + u + "' đã tồn tại trong hệ thống. Vui lòng chọn tên đăng nhập khác.");
            }

            String mk = new String(pass.getPassword());
            kiemTraRong(mk, "mật khẩu");
            if (mk.length() < 8) {
                throw new IllegalArgumentException("Mật khẩu phải có ít nhất 8 ký tự.");
            }
            kiemTraDoDai(mk, 128, "mật khẩu");
            a =
                    new TaiKhoan(
                            u,
                            "Quản lý".equals(n.getVaiTro()) ? "ADMIN" : "BAN_HANG",
                            mk,
                            n);
        }
        if (maCu == null) {
            nvDao.insertNhanVien(n, a, tk);
            JOptionPane.showMessageDialog(this, "Thêm nhân viên và tài khoản thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            nvDao.updateNhanVien(n, tk);
            JOptionPane.showMessageDialog(this, "Cập nhật hồ sơ nhân viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
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
                        throw new IllegalArgumentException("Ảnh tối đa 2 MB.");
                    anh = Files.readAllBytes(f.getSelectedFile().toPath());
                    if (javax.imageio.ImageIO.read(new java.io.ByteArrayInputStream(anh)) == null)
                        throw new IllegalArgumentException("File không phải ảnh.");
                    lblAnh.setText(f.getSelectedFile().getName());
                }
            } else if (o.equals(btnXoa)) {
                if (maCu == null) throw new IllegalArgumentException("Chọn nhân viên.");
                if (xacNhan(this, "Xóa hồ sơ và tài khoản chưa có lịch sử?")) {
                    new NhanVien_DAO().deleteNhanVien(maCu, tk);
                    moi();
                    loadDataToTable();
                }
            } else if (o.equals(btnReset)) {
                if (maCu == null) throw new IllegalArgumentException("Chọn nhân viên.");
                String username = null;
                for (TaiKhoan a : new TaiKhoan_DAO().getAllTaiKhoan(tk))
                    if (maCu.equals(a.getNhanVien().getMaNhanVien())) username = a.getTenDangNhap();
                if (username == null) throw new IllegalArgumentException("Chưa có tài khoản.");
                JPasswordField f = new JPasswordField();
                if (JOptionPane.showConfirmDialog(
                                this,
                                f,
                                "Mật khẩu mới của " + username,
                                JOptionPane.OK_CANCEL_OPTION)
                        == JOptionPane.OK_OPTION)
                    new TaiKhoan_DAO().updateMatKhau(username, new String(f.getPassword()), tk);
            }
        } catch (java.sql.SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this, "Lỗi SQL:\n" + ex.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
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
                c, e.getMessage(), "Không thực hiện được", JOptionPane.ERROR_MESSAGE);
    }

    private void kiemTraDoDai(String s, int n, String ten) {
        kiemTraRong(s, ten);
        if (s.length() > n) throw new IllegalArgumentException(ten + " tối đa " + n + " ký tự.");
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
            throw new IllegalArgumentException("Vui lòng nhập " + ten);
    }

    private void kiemTraSDT(String s) {
        if (s == null || !s.matches("^0[35789]\\d{8}$"))
            throw new IllegalArgumentException("Số điện thoại không hợp lệ (10 số, đầu 03/05/07/08/09).");
    }

    private JLabel taoTieuDe(String t) {
        return GiaoDien.tieuDe(t);
    }

    private boolean xacNhan(Component c, String s) {
        return JOptionPane.showConfirmDialog(c, s, "Xác nhận", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }

    private void ganPhimTat() {
        GiaoDien.phimTatChuan(this, btnMoi, btnLuu, btnXoa, btnTai, txtTim);
        GiaoDien.phimTat(this, "ctrl R", btnReset);
    }
}
