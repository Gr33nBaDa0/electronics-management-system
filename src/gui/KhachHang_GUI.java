package gui;

import connectDB.ConnectDB;

import dao.KhachHang_DAO;

import entity.KhachHang;
import entity.TaiKhoan;

import util.GiaoDien;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class KhachHang_GUI extends JPanel implements ActionListener {
    // Màu dùng chung được khai báo ở đầu lớp.
    private static final Color PRIMARY = GiaoDien.PRIMARY,
            DARK = GiaoDien.DARK,
            BACKGROUND = GiaoDien.BACKGROUND,
            SURFACE = GiaoDien.SURFACE,
            TEXT = GiaoDien.TEXT,
            BORDER = GiaoDien.BORDER,
            ERROR = GiaoDien.ERROR;

    private final TaiKhoan tk;
    private final KhachHang_DAO dao = new KhachHang_DAO();
    private List<KhachHang> danhSach = new ArrayList<>(), hienThi = new ArrayList<>();
    private String maCu;
    private final JTextField maKhachHang = new JTextField(14);
    private final JTextField hoTen = new JTextField(14);
    private final JTextField soDienThoai = new JTextField(14);
    private final JTextField diaChi = new JTextField(14);
    private final JTextField email = new JTextField(14);
    private final JTextField txtTim = new JTextField(20);
    private final JLabel lblSoLuong = new JLabel();
    private final JButton btnMoi = taoNut("Tạo mới"),
            btnLuu = taoNut("Lưu"),
            btnXoa = taoNut("Xóa"),
            btnTim = taoNut("Tìm"),
            btnTai = taoNut("Tải lại / bỏ lọc");
    private final JButton btnLichSu = taoNut("Lịch sử mua");
    private final JTextField diemTu = new JTextField(6), diemDen = new JTextField(6);
    private final DefaultTableModel model =
            taoModel(
                    "Mã KH (KH001)", "Họ tên", "Số điện thoại", "Địa chỉ", "Email", "Điểm hiện có");
    private final JTable table = taoBang(model);

    public KhachHang_GUI(TaiKhoan tk) {
        this.tk = tk;
        maKhachHang.setEditable(false);
        maKhachHang.setToolTipText("Mã tự sinh khi tạo mới");
        GiaoDien.boCuc(this);

        JPanel form =
                GiaoDien.form(
                        2,
                        "Mã khách hàng",
                        maKhachHang,
                        "Họ tên",
                        hoTen,
                        "Số điện thoại",
                        soDienThoai,
                        "Địa chỉ",
                        diaChi,
                        "Email",
                        email);
        JPanel trai =
                GiaoDien.the(
                        "Thông tin khách hàng",
                        GiaoDien.cuonForm(form),
                        taoDong(btnMoi, btnLuu, btnXoa, btnTai));
        JPanel loc =
                GiaoDien.form(3, "Mã / tên / SĐT", txtTim, "Điểm từ", diemTu, "Điểm đến", diemDen);
        JPanel danhSach = new JPanel(new BorderLayout(0, 12));
        danhSach.setOpaque(false);
        danhSach.add(GiaoDien.doc(loc, taoDong(btnTim, lblSoLuong)), BorderLayout.NORTH);
        danhSach.add(GiaoDien.cuon(table), BorderLayout.CENTER);
        JPanel phai = GiaoDien.the("Danh sách khách hàng", danhSach, taoDong(btnLichSu));
        trai.setMinimumSize(new Dimension(340, 180));
        phai.setMinimumSize(new Dimension(400, 180));
        add(
                GiaoDien.tach(javax.swing.JSplitPane.HORIZONTAL_SPLIT, trai, phai, .40),
                BorderLayout.CENTER);
        btnLichSu.addActionListener(this);
        GiaoDien.trangTri(this);
        btnMoi.addActionListener(this);
        btnLuu.addActionListener(this);
        btnXoa.addActionListener(this);
        btnTim.addActionListener(this);
        btnTai.addActionListener(this);
        txtTim.addActionListener(this);
        table.getSelectionModel()
                .addListSelectionListener(
                        e -> {
                            if (!e.getValueIsAdjusting()) chonDong();
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

            List<KhachHang> list = dao.getAllKhachHang();
            danhSach = list;

            if (!diemTu.getText().isBlank()
                    && !diemDen.getText().isBlank()
                    && Integer.parseInt(diemTu.getText()) > Integer.parseInt(diemDen.getText()))
                throw new IllegalArgumentException("Điểm từ không được lớn hơn điểm đến.");
            String tim = txtTim.getText().trim().toLowerCase(java.util.Locale.ROOT);
            for (KhachHang sp : list) {
                String noiDung =
                        (sp.getMaKhachHang()
                                        + " "
                                        + sp.getHoTen()
                                        + " "
                                        + sp.getSoDienThoai()
                                        + " "
                                        + sp.getDiaChi()
                                        + " "
                                        + sp.getEmail())
                                .toLowerCase(java.util.Locale.ROOT);
                if (!noiDung.contains(tim)) continue;
                if (!diemTu.getText().isBlank()
                        && sp.getDiemTichLuy() < Integer.parseInt(diemTu.getText())) continue;
                if (!diemDen.getText().isBlank()
                        && sp.getDiemTichLuy() > Integer.parseInt(diemDen.getText())) continue;
                hienThi.add(sp);
                model.addRow(
                        new Object[] {
                            sp.getMaKhachHang(),
                            sp.getHoTen(),
                            sp.getSoDienThoai(),
                            sp.getDiaChi(),
                            sp.getEmail(),
                            sp.getDiemTichLuy()
                        });
            }
            lblSoLuong.setText("Tổng: " + hienThi.size() + " bản ghi");
            if (maCu == null) hienMaTiepTheo();

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

    private void chonDong() {
        int i = table.getSelectedRow();
        if (i < 0 || i >= hienThi.size()) return;
        KhachHang sp = hienThi.get(i);
        maCu = sp.getMaKhachHang();
        maKhachHang.setEditable(false);
        maKhachHang.setText(String.valueOf(sp.getMaKhachHang()));
        hoTen.setText(String.valueOf(sp.getHoTen()));
        soDienThoai.setText(String.valueOf(sp.getSoDienThoai()));
        diaChi.setText(String.valueOf(sp.getDiaChi()));
        email.setText(String.valueOf(sp.getEmail()));
    }

    private void hienMaTiepTheo() {
        maKhachHang.setEditable(false);
        try {
            maKhachHang.setText(dao.layMaTiepTheo());
            maKhachHang.setToolTipText("Mã tự sinh khi tạo mới");
        } catch (SQLException e) {
            maKhachHang.setText("");
            maKhachHang.setToolTipText("Chưa lấy được mã: " + e.getMessage());
        }
    }

    private void taoMoi() {
        maCu = null;
        table.clearSelection();
        maKhachHang.setText("");
        hoTen.setText("");
        soDienThoai.setText("");
        diaChi.setText("");
        email.setText("");
        hienMaTiepTheo();
        hoTen.requestFocusInWindow();
    }

    private void luu() throws Exception {
        KhachHang sp = new KhachHang();
        sp.setMaKhachHang(maCu);
        kiemTraRong(hoTen.getText().trim(), "Họ tên");
        sp.setHoTen(hoTen.getText().trim());
        kiemTraRong(soDienThoai.getText().trim(), "Số điện thoại");
        sp.setSoDienThoai(soDienThoai.getText().trim());
        sp.setDiaChi(diaChi.getText().trim());
        sp.setEmail(email.getText().trim());
        kiemTraSDT(soDienThoai.getText());
        if (maCu == null) {
            dao.insertKhachHang(sp, tk);
            JOptionPane.showMessageDialog(this, "Đã thêm khách hàng " + sp.getMaKhachHang() + ".");
        } else {
            dao.updateKhachHang(sp, tk);
        }
        taoMoi();
        loadDataToTable();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        try {
            if (o.equals(btnMoi)) taoMoi();
            else if (o.equals(btnLuu)) luu();
            else if (o.equals(btnTim) || o.equals(txtTim)) loadDataToTable();
            else if (o.equals(btnTai)) {
                txtTim.setText("");
                diemTu.setText("");
                diemDen.setText("");
                loadDataToTable();
            } else if (o.equals(btnXoa)) {
                if (maCu == null) throw new IllegalArgumentException("Chọn dòng cần xóa.");
                if (xacNhan(this, "Xóa dữ liệu chưa có liên kết?")) {
                    dao.deleteKhachHang(maCu, tk);
                    taoMoi();
                    loadDataToTable();
                }
            } else if (o.equals(btnLichSu)) {
                if (table.getSelectedRow() < 0)
                    throw new IllegalArgumentException("Chọn khách hàng.");
                JDialog d =
                        new JDialog(
                                (Frame) SwingUtilities.getWindowAncestor(this),
                                "Lịch sử mua hàng",
                                true);
                d.add(new HoaDon_GUI(tk, null, soDienThoai.getText()));
                d.setSize(1050, 650);
                d.setLocationRelativeTo(this);
                d.setVisible(true);
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
        if (s == null || !s.matches("0\\d{9}"))
            throw new IllegalArgumentException("SĐT phải gồm 10 chữ số, bắt đầu bằng 0.");
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
        GiaoDien.phimTat(this, "ctrl H", btnLichSu);
    }
}
