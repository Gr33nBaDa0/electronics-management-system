package gui;

import connectDB.ConnectDB;

import dao.ChuongTrinhKhuyenMai_DAO;

import entity.ChuongTrinhKhuyenMai;
import entity.TaiKhoan;

import until.GiaoDien;
import until.InHoaDon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class ChuongTrinhKhuyenMai_GUI extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

	private static final Color PRIMARY = GiaoDien.PRIMARY,
            DARK = GiaoDien.DARK,
            BACKGROUND = GiaoDien.BACKGROUND,
            SURFACE = GiaoDien.SURFACE,
            TEXT = GiaoDien.TEXT,
            BORDER = GiaoDien.BORDER,
            ERROR = GiaoDien.ERROR;

    private final TaiKhoan tk;
    private final ChuongTrinhKhuyenMai_DAO dao = new ChuongTrinhKhuyenMai_DAO();
    private List<ChuongTrinhKhuyenMai> danhSach = new ArrayList<>(), hienThi = new ArrayList<>();
    private String maCu;
    private final JTextField maKhuyenMai = new JTextField(14);
    private final JTextField tenChuongTrinh = new JTextField(14);
    private final JTextField giaTriGiam = new JTextField(14);
    private final JTextField ngayBatDau = new JTextField(14);
    private final JTextField ngayKetThuc = new JTextField(14);
    private final JComboBox<String> trangThai =
            new JComboBox<>(new String[] {"Đang áp dụng", "Tạm ngưng", "Hết hạn"});
    private final JTextField txtTim = new JTextField(20);
    private final JLabel lblSoLuong = new JLabel();
    private final JButton btnMoi = taoNut("Tạo mới"),
            btnLuu = taoNut("Lưu"),
            btnXoa = taoNut("Xóa"),
            btnTim = taoNut("Tìm"),
            btnTai = taoNut("Tải lại / bỏ lọc");
    private final DefaultTableModel model =
            taoModel(
                    "Mã KM (KM2026001)",
                    "Tên chương trình",
                    "Giảm (%)",
                    "Bắt đầu yyyy-MM-dd",
                    "Kết thúc yyyy-MM-dd",
                    "Trạng thái");
    private final JTable table = taoBang(model);

    public ChuongTrinhKhuyenMai_GUI(TaiKhoan tk) {
        this.tk = tk;
        GiaoDien.boCuc(this);

        maKhuyenMai.setEditable(false);
        maKhuyenMai.setToolTipText("Mã khuyến mãi được tạo tự động khi lưu");
        ngayBatDau.setToolTipText("Nhập theo yyyy-MM-dd, ví dụ 2026-11-01");
        ngayKetThuc.setToolTipText("Nhập theo yyyy-MM-dd, ví dụ 2026-11-30");
        JPanel form =
                GiaoDien.form(
                        2,
                        "Mã khuyến mãi",
                        maKhuyenMai,
                        "Tên chương trình",
                        tenChuongTrinh,
                        "Mức giảm (%)",
                        giaTriGiam,
                        "Trạng thái",
                        trangThai,
                        "Ngày bắt đầu",
                        ngayBatDau,
                        "Ngày kết thúc",
                        ngayKetThuc);
        JPanel trai =
                GiaoDien.the(
                        "Thông tin chương trình",
                        GiaoDien.cuonForm(form),
                        taoDong(btnMoi, btnLuu, btnXoa, btnTai));
        JPanel danhSach = new JPanel(new BorderLayout(0, 12));
        danhSach.setOpaque(false);
        danhSach.add(taoDong(new JLabel("Tìm kiếm:"), txtTim, btnTim), BorderLayout.NORTH);
        danhSach.add(GiaoDien.cuon(table), BorderLayout.CENTER);
        JPanel phai = GiaoDien.the("Danh sách khuyến mãi", danhSach, taoDong(lblSoLuong));
        trai.setMinimumSize(new Dimension(340, 180));
        phai.setMinimumSize(new Dimension(400, 180));
        add(
                GiaoDien.tach(javax.swing.JSplitPane.HORIZONTAL_SPLIT, trai, phai, .42),
                BorderLayout.CENTER);
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
        boolean quanLy = "ADMIN".equals(tk.getQuyen());
        btnMoi.setEnabled(quanLy);
        btnLuu.setEnabled(quanLy);
        btnXoa.setEnabled(quanLy);
        ganPhimTat();
        loadDataToTable();
        taoMoi();
    }

    private void loadDataToTable() {
        model.setRowCount(0);
        hienThi.clear();
        try {
            if (ConnectDB.con == null || ConnectDB.con.isClosed()) {
                ConnectDB.getInstance().connect();
            }

            List<ChuongTrinhKhuyenMai> list = dao.getAllChuongTrinhKhuyenMai();
            danhSach = list;

            String tim = txtTim.getText().trim().toLowerCase(java.util.Locale.ROOT);
            for (ChuongTrinhKhuyenMai sp : list) {
                String noiDung =
                        (sp.getMaKhuyenMai()
                                        + " "
                                        + sp.getTenChuongTrinh()
                                        + " "
                                        + sp.getGiaTriGiam()
                                        + " "
                                        + sp.getNgayBatDau()
                                        + " "
                                        + sp.getNgayKetThuc()
                                        + " "
                                        + sp.getTrangThai())
                                .toLowerCase(java.util.Locale.ROOT);
                if (!noiDung.contains(tim)) continue;
                hienThi.add(sp);
                model.addRow(
                        new Object[] {
                            sp.getMaKhuyenMai(),
                            sp.getTenChuongTrinh(),
                            formatGia(sp.getGiaTriGiam()),
                            sp.getNgayBatDau(),
                            sp.getNgayKetThuc(),
                            sp.getTrangThai()
                        });
            }
            lblSoLuong.setText("Tổng: " + hienThi.size() + " bản ghi");

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
        ChuongTrinhKhuyenMai sp = hienThi.get(i);
        maCu = sp.getMaKhuyenMai();
        maKhuyenMai.setEditable(false);
        maKhuyenMai.setText(String.valueOf(sp.getMaKhuyenMai()));
        tenChuongTrinh.setText(String.valueOf(sp.getTenChuongTrinh()));
        giaTriGiam.setText(formatGia(sp.getGiaTriGiam()));
        ngayBatDau.setText(String.valueOf(sp.getNgayBatDau()));
        ngayKetThuc.setText(String.valueOf(sp.getNgayKetThuc()));
        trangThai.setSelectedItem(sp.getTrangThai());
    }

    private void hienMaTiepTheo() {
        maKhuyenMai.setEditable(false);
        try {
            maKhuyenMai.setText(dao.layMaTiepTheo());
            maKhuyenMai.setToolTipText("Mã tự sinh khi tạo mới, không chỉnh sửa");
        } catch (SQLException e) {
            maKhuyenMai.setText("");
            maKhuyenMai.setToolTipText("Chưa lấy được mã: " + e.getMessage());
        }
    }

    private void taoMoi() {
        maCu = null;
        table.clearSelection();
        maKhuyenMai.setText("");
        tenChuongTrinh.setText("");
        giaTriGiam.setText("");
        ngayBatDau.setText("");
        ngayKetThuc.setText("");
        trangThai.setSelectedIndex(0);
        hienMaTiepTheo();
        tenChuongTrinh.requestFocusInWindow();
    }

    private void luu() throws Exception {
        ChuongTrinhKhuyenMai sp = new ChuongTrinhKhuyenMai();
        sp.setMaKhuyenMai(maCu);
        kiemTraRong(tenChuongTrinh.getText().trim(), "Tên chương trình");
        sp.setTenChuongTrinh(tenChuongTrinh.getText().trim());
        sp.setGiaTriGiam(InHoaDon.soTien(giaTriGiam.getText().trim()));
        sp.setNgayBatDau(LocalDate.parse(ngayBatDau.getText().trim()));
        sp.setNgayKetThuc(LocalDate.parse(ngayKetThuc.getText().trim()));
        sp.setTrangThai((String) trangThai.getSelectedItem());
        if (maCu == null) {
            dao.insertChuongTrinhKhuyenMai(sp, tk);
        } else {
            dao.updateChuongTrinhKhuyenMai(sp, tk);
        }
        loadDataToTable();
        taoMoi();
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
                loadDataToTable();
                taoMoi();
            } else if (o.equals(btnXoa)) {
                if (maCu == null) throw new IllegalArgumentException("Chọn dòng cần xóa.");
                if (xacNhan(this, "Xóa dữ liệu chưa có liên kết?")) {
                    dao.deleteChuongTrinhKhuyenMai(maCu, tk);
                    loadDataToTable();
                    taoMoi();
                }
            }
        } catch (java.sql.SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this, "Lỗi SQL:\n" + ex.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            thongBaoLoi(this, ex);
        }
    }

    private String formatGia(double gia) {
        java.text.DecimalFormat df =
                new java.text.DecimalFormat(
                        "#,##0.##",
                        java.text.DecimalFormatSymbols.getInstance(java.util.Locale.US));
        return df.format(gia);
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

    private JLabel taoTieuDe(String t) {
        return GiaoDien.tieuDe(t);
    }

    private boolean xacNhan(Component c, String s) {
        return JOptionPane.showConfirmDialog(c, s, "Xác nhận", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }

    private void ganPhimTat() {
        GiaoDien.phimTatChuan(this, btnMoi, btnLuu, btnXoa, btnTai, txtTim);
    }
}
