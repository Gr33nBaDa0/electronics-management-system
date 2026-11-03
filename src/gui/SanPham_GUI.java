package gui;

import connectDB.ConnectDB;

import dao.SanPham_DAO;

import entity.SanPham;
import entity.TaiKhoan;

import util.GiaoDien;
import util.InHoaDon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
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

public class SanPham_GUI extends JPanel implements ActionListener {
    // Màu dùng chung được khai báo ở đầu lớp.
    private static final Color PRIMARY = GiaoDien.PRIMARY,
            DARK = GiaoDien.DARK,
            BACKGROUND = GiaoDien.BACKGROUND,
            SURFACE = GiaoDien.SURFACE,
            TEXT = GiaoDien.TEXT,
            BORDER = GiaoDien.BORDER,
            ERROR = GiaoDien.ERROR;

    private final TaiKhoan tk;
    private final SanPham_DAO sanPhamDAO = new SanPham_DAO();
    private List<SanPham> danhSach = new ArrayList<>(), hienThi = new ArrayList<>();
    private String maCu;
    private final JTextField maSanPham = new JTextField(14);
    private final JTextField tenSanPham = new JTextField(14);
    private final JTextField loaiHang = new JTextField(14);
    private final JTextField thuongHieu = new JTextField(14);
    private final JTextField donGiaBan = new JTextField(14);
    private final JTextField giaNhap = new JTextField(14);
    private final JTextField thongSoKiThuat = new JTextField(14);
    private final JTextField xuatXu = new JTextField(14);
    private final JTextField thoiHanBaoHanh = new JTextField(14);
    private final JComboBox<String> trangThai =
            new JComboBox<>(new String[] {"Đang bán", "Ngưng bán"});
    private final JTextField txtTim = new JTextField(20);
    private final JLabel lblSoLuong = new JLabel();
    private final JButton btnMoi = taoNut("Tạo mới"),
            btnLuu = taoNut("Lưu"),
            btnXoa = taoNut("Xóa"),
            btnTim = taoNut("Tìm"),
            btnTai = taoNut("Tải lại / bỏ lọc");
    private final JTextField giaTu = new JTextField(8), giaDen = new JTextField(8);
    private final JComboBox<String> locLoai = new JComboBox<>(),
            locHang = new JComboBox<>(),
            locTT = new JComboBox<>(new String[] {"Tất cả", "Đang bán", "Ngưng bán"});
    private final DefaultTableModel model =
            taoModel(
                    "Mã SP (SP0001)",
                    "Tên sản phẩm",
                    "Loại hàng",
                    "Thương hiệu",
                    "Giá bán",
                    "Giá nhập",
                    "Thông số kỹ thuật",
                    "Xuất xứ",
                    "Bảo hành (tháng)",
                    "Trạng thái",
                    "Tồn kho");
    private final JTable table = taoBang(model);

    public SanPham_GUI(TaiKhoan tk) {
        this.tk = tk;
        maSanPham.setEditable(false);
        maSanPham.setToolTipText("Mã tự sinh khi tạo mới");
        GiaoDien.boCuc(this);

        JPanel form =
                GiaoDien.form(
                        2,
                        "Mã sản phẩm",
                        maSanPham,
                        "Tên sản phẩm",
                        tenSanPham,
                        "Loại hàng",
                        loaiHang,
                        "Thương hiệu",
                        thuongHieu,
                        "Giá bán (VND)",
                        donGiaBan,
                        "Giá nhập (VND)",
                        giaNhap,
                        "Thông số kỹ thuật",
                        thongSoKiThuat,
                        "Xuất xứ",
                        xuatXu,
                        "Bảo hành (tháng)",
                        thoiHanBaoHanh,
                        "Trạng thái",
                        trangThai);
        JPanel trai =
                GiaoDien.the(
                        "Thông tin sản phẩm",
                        GiaoDien.cuonForm(form),
                        taoDong(btnMoi, btnLuu, btnXoa, btnTai));
        JPanel loc =
                GiaoDien.form(
                        3,
                        "Mã / tên / thông tin",
                        txtTim,
                        "Loại hàng",
                        locLoai,
                        "Thương hiệu",
                        locHang,
                        "Trạng thái",
                        locTT,
                        "Giá từ",
                        giaTu,
                        "Giá đến",
                        giaDen);
        JPanel danhSach = new JPanel(new BorderLayout(0, 12));
        danhSach.setOpaque(false);
        danhSach.add(GiaoDien.doc(loc, taoDong(btnTim, lblSoLuong)), BorderLayout.NORTH);
        danhSach.add(GiaoDien.cuon(table), BorderLayout.CENTER);
        JPanel phai = GiaoDien.the("Danh sách sản phẩm", danhSach, null);
        trai.setMinimumSize(new Dimension(340, 180));
        phai.setMinimumSize(new Dimension(400, 180));
        add(
                GiaoDien.tach(javax.swing.JSplitPane.HORIZONTAL_SPLIT, trai, phai, .43),
                BorderLayout.CENTER);
        GiaoDien.trangTri(this);
        btnMoi.addActionListener(this);
        btnLuu.addActionListener(this);
        btnXoa.addActionListener(this);
        btnTim.addActionListener(this);
        btnTai.addActionListener(this);
        txtTim.addActionListener(this);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < table.getColumnCount(); i++)
            table.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(i == 1 ? 240 : i == 6 ? 260 : 130);
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
    }

    private void loadDataToTable() {
        model.setRowCount(0);
        hienThi.clear();
        try {
            if (ConnectDB.con == null || ConnectDB.con.isClosed()) {
                ConnectDB.getInstance().connect();
            }

            List<SanPham> list = sanPhamDAO.getAllSanPham();
            danhSach = list;

            Object loaiDangChon = locLoai.getSelectedItem();
            Object hangDangChon = locHang.getSelectedItem();

            locLoai.removeAllItems();
            locHang.removeAllItems();
            locLoai.addItem("Tất cả");
            locHang.addItem("Tất cả");
            java.util.Set<String> loai = new java.util.TreeSet<>();
            java.util.Set<String> hang = new java.util.TreeSet<>();
            for (SanPham sp : list) {
                loai.add(sp.getLoaiHang());
                hang.add(sp.getThuongHieu());
            }
            for (String ten : loai) locLoai.addItem(ten);
            for (String ten : hang) locHang.addItem(ten);

            if (loaiDangChon != null) locLoai.setSelectedItem(loaiDangChon);
            if (hangDangChon != null) locHang.setSelectedItem(hangDangChon);

            if (!giaTu.getText().isBlank()
                    && !giaDen.getText().isBlank()
                    && InHoaDon.soTien(giaTu.getText()).compareTo(InHoaDon.soTien(giaDen.getText()))
                            > 0)
                throw new IllegalArgumentException("Giá từ không được lớn hơn giá đến.");
            String tim = txtTim.getText().trim().toLowerCase(java.util.Locale.ROOT);
            for (SanPham sp : list) {
                String noiDung =
                        (sp.getMaSanPham()
                                        + " "
                                        + sp.getTenSanPham()
                                        + " "
                                        + sp.getLoaiHang()
                                        + " "
                                        + sp.getThuongHieu()
                                        + " "
                                        + sp.getDonGiaBan()
                                        + " "
                                        + sp.getGiaNhap()
                                        + " "
                                        + sp.getThongSoKiThuat()
                                        + " "
                                        + sp.getXuatXu()
                                        + " "
                                        + sp.getThoiHanBaoHanh()
                                        + " "
                                        + sp.getTrangThai())
                                .toLowerCase(java.util.Locale.ROOT);
                if (!noiDung.contains(tim)) continue;
                if (locLoai.getSelectedIndex() > 0
                        && !sp.getLoaiHang().equals(locLoai.getSelectedItem())) continue;
                if (locHang.getSelectedIndex() > 0
                        && !sp.getThuongHieu().equals(locHang.getSelectedItem())) continue;
                if (locTT.getSelectedIndex() > 0
                        && !sp.getTrangThai().equals(locTT.getSelectedItem())) continue;
                if (!giaTu.getText().isBlank()
                        && sp.getDonGiaBan().compareTo(InHoaDon.soTien(giaTu.getText())) < 0)
                    continue;
                if (!giaDen.getText().isBlank()
                        && sp.getDonGiaBan().compareTo(InHoaDon.soTien(giaDen.getText())) > 0)
                    continue;
                hienThi.add(sp);
                model.addRow(
                        new Object[] {
                            sp.getMaSanPham(),
                            sp.getTenSanPham(),
                            sp.getLoaiHang(),
                            sp.getThuongHieu(),
                            formatGia(sp.getDonGiaBan()),
                            formatGia(sp.getGiaNhap()),
                            sp.getThongSoKiThuat(),
                            sp.getXuatXu(),
                            sp.getThoiHanBaoHanh(),
                            sp.getTrangThai(),
                            sp.getSoLuongTon()
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
        SanPham sp = hienThi.get(i);
        maCu = sp.getMaSanPham();
        maSanPham.setEditable(false);
        maSanPham.setText(String.valueOf(sp.getMaSanPham()));
        tenSanPham.setText(String.valueOf(sp.getTenSanPham()));
        loaiHang.setText(String.valueOf(sp.getLoaiHang()));
        thuongHieu.setText(String.valueOf(sp.getThuongHieu()));
        donGiaBan.setText(formatGia(sp.getDonGiaBan()));
        giaNhap.setText(formatGia(sp.getGiaNhap()));
        thongSoKiThuat.setText(String.valueOf(sp.getThongSoKiThuat()));
        xuatXu.setText(String.valueOf(sp.getXuatXu()));
        thoiHanBaoHanh.setText(String.valueOf(sp.getThoiHanBaoHanh()));
        trangThai.setSelectedItem(sp.getTrangThai());
    }

    private void hienMaTiepTheo() {
        maSanPham.setEditable(false);
        try {
            maSanPham.setText(sanPhamDAO.layMaTiepTheo());
            maSanPham.setToolTipText("Mã tự sinh khi tạo mới");
        } catch (SQLException e) {
            maSanPham.setText("");
            maSanPham.setToolTipText("Chưa lấy được mã: " + e.getMessage());
        }
    }

    private void taoMoi() {
        maCu = null;
        table.clearSelection();
        maSanPham.setText("");
        tenSanPham.setText("");
        loaiHang.setText("");
        thuongHieu.setText("");
        donGiaBan.setText("");
        giaNhap.setText("");
        thongSoKiThuat.setText("");
        xuatXu.setText("");
        thoiHanBaoHanh.setText("");
        trangThai.setSelectedIndex(0);
        hienMaTiepTheo();
        tenSanPham.requestFocusInWindow();
    }

    private void luu() throws Exception {
        SanPham sp = new SanPham();
        sp.setMaSanPham(maCu);
        kiemTraRong(tenSanPham.getText().trim(), "Tên sản phẩm");
        sp.setTenSanPham(tenSanPham.getText().trim());
        kiemTraRong(loaiHang.getText().trim(), "Loại hàng");
        sp.setLoaiHang(loaiHang.getText().trim());
        kiemTraRong(thuongHieu.getText().trim(), "Thương hiệu");
        sp.setThuongHieu(thuongHieu.getText().trim());
        sp.setDonGiaBan(InHoaDon.soTien(donGiaBan.getText().trim()));
        sp.setGiaNhap(InHoaDon.soTien(giaNhap.getText().trim()));
        sp.setThongSoKiThuat(thongSoKiThuat.getText().trim());
        kiemTraRong(xuatXu.getText().trim(), "Xuất xứ");
        sp.setXuatXu(xuatXu.getText().trim());
        sp.setThoiHanBaoHanh(Integer.parseInt(thoiHanBaoHanh.getText().trim()));
        sp.setTrangThai((String) trangThai.getSelectedItem());
        if (maCu == null) {
            sanPhamDAO.insertSanPham(sp, tk);
            JOptionPane.showMessageDialog(this, "Đã thêm sản phẩm " + sp.getMaSanPham() + ".");
        } else {
            sanPhamDAO.updateSanPham(sp, tk);
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
                giaTu.setText("");
                giaDen.setText("");
                locTT.setSelectedIndex(0);
                loadDataToTable();
            } else if (o.equals(btnXoa)) {
                if (maCu == null) throw new IllegalArgumentException("Chọn dòng cần xóa.");
                if (xacNhan(this, "Xóa dữ liệu chưa có liên kết?")) {
                    sanPhamDAO.deleteSanPham(maCu, tk);
                    taoMoi();
                    loadDataToTable();
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

    public void chonSanPham(String maCanXem) {
        txtTim.setText(maCanXem);
        locLoai.setSelectedIndex(0);
        locHang.setSelectedIndex(0);
        locTT.setSelectedIndex(0);
        giaTu.setText("");
        giaDen.setText("");
        loadDataToTable();
        if (model.getRowCount() > 0) table.setRowSelectionInterval(0, 0);
    }

    private String formatGia(java.math.BigDecimal gia) {
        java.text.DecimalFormat df =
                new java.text.DecimalFormat(
                        "#,##0.##",
                        java.text.DecimalFormatSymbols.getInstance(java.util.Locale.US));
        return gia == null ? "" : df.format(gia);
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
