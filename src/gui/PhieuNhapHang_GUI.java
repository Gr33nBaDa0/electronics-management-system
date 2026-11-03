package gui;

import connectDB.ConnectDB;

import dao.ChiTietPhieuNhapHang_DAO;
import dao.PhieuNhapHang_DAO;
import dao.SanPham_DAO;

import entity.ChiTietPhieuNhapHang;
import entity.PhieuNhapHang;
import entity.SanPham;
import entity.TaiKhoan;

import until.GiaoDien;
import until.InHoaDon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

public class PhieuNhapHang_GUI extends JPanel implements ActionListener {
    // Màu dùng chung được khai báo ở đầu lớp.
    private static final Color PRIMARY = GiaoDien.PRIMARY,
            DARK = GiaoDien.DARK,
            BACKGROUND = GiaoDien.BACKGROUND,
            SURFACE = GiaoDien.SURFACE,
            TEXT = GiaoDien.TEXT,
            BORDER = GiaoDien.BORDER,
            ERROR = GiaoDien.ERROR;

    private final TaiKhoan tk;
    private List<PhieuNhapHang> ds = new ArrayList<>(), hienThi = new ArrayList<>();
    private final List<ChiTietPhieuNhapHang> dong = new ArrayList<>();
    private String ma;
    private PhieuNhapHang phieuDangMo;
    private final javax.swing.JTabbedPane tabs = new javax.swing.JTabbedPane();
    private final JTextField nhaCC = new JTextField(18),
            chungTu = new JTextField(12),
            nguoiGiao = new JTextField(16),
            gia = new JTextField(12),
            txtTim = new JTextField(18);
    private final JSpinner sl = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
    private final JComboBox<SanPham> sp = new JComboBox<>();
    private final JComboBox<String> loc =
            new JComboBox<>(new String[] {"Tất cả", "Chờ duyệt", "Đã nhập", "Từ chối"});
    private final JButton btnTai = taoNut("Tải lại / lọc"),
            btnMoi = taoNut("Phiếu mới"),
            btnSua = taoNut("Mở sửa / chi tiết"),
            btnXoa = taoNut("Xóa phiếu"),
            btnDuyet = taoNut("Duyệt hàng thực nhận"),
            btnTuChoi = taoNut("Từ chối"),
            btnThem = taoNut("Thêm dòng"),
            btnBo = taoNut("Bỏ dòng"),
            btnLuu = taoNut("Lưu chờ duyệt");
    private final JLabel lblMa = new JLabel("Phiếu mới"), lblTong = new JLabel();
    private final DefaultTableModel
            model =
                    taoModel(
                            "Mã phiếu",
                            "Ngày lập",
                            "Nhà cung cấp",
                            "Người lập",
                            "Trạng thái",
                            "Người duyệt",
                            "Lý do từ chối"),
            modelCT = taoModel("Mã SP", "Tên", "Số lượng", "Giá nhập", "Thành tiền");
    private final JTable table = taoBang(model), tableCT = taoBang(modelCT);

    public PhieuNhapHang_GUI(TaiKhoan tk) {
        this.tk = tk;
        hienThiLuaChon(sp, "Chọn sản phẩm", 260);
        GiaoDien.boCuc(this);

        JPanel danhSach = new JPanel(new BorderLayout(0, 8));
        danhSach.setOpaque(false);
        danhSach.add(taoDong(new JLabel("Tìm phiếu:"), txtTim, loc, btnTai), BorderLayout.NORTH);
        danhSach.add(GiaoDien.cuon(table), BorderLayout.CENTER);
        JPanel tren =
                GiaoDien.the(
                        "Danh sách phiếu nhập",
                        danhSach,
                        taoDong(btnMoi, btnSua, btnXoa, btnDuyet, btnTuChoi));
        JPanel thongTin =
                GiaoDien.form(
                        3, "Nhà cung cấp", nhaCC, "Số chứng từ", chungTu, "Người giao", nguoiGiao);
        JPanel nhapDong = new JPanel(new BorderLayout(8, 0));
        nhapDong.setOpaque(false);
        nhapDong.add(sp, BorderLayout.CENTER);
        nhapDong.add(
                taoDong(new JLabel("SL:"), sl, new JLabel("Giá nhập:"), gia, btnThem, btnBo),
                BorderLayout.EAST);
        lblMa.setForeground(PRIMARY);
        lblMa.setFont(GiaoDien.phong(Font.BOLD, 14));
        JPanel noiDung = new JPanel(new BorderLayout(0, 8));
        noiDung.setOpaque(false);
        noiDung.add(GiaoDien.doc(lblMa, thongTin, nhapDong), BorderLayout.NORTH);
        noiDung.add(GiaoDien.cuon(tableCT), BorderLayout.CENTER);
        lblTong.setForeground(PRIMARY);
        lblTong.setFont(GiaoDien.phong(Font.BOLD, 16));
        JPanel duoi = GiaoDien.the("Chi tiết hàng nhập", noiDung, taoDong(btnLuu, lblTong));
        tabs.addTab("Danh sách phiếu", tren);
        tabs.addTab("Chi tiết phiếu", duoi);
        add(tabs, BorderLayout.CENTER);
        GiaoDien.trangTri(this);
        for (JButton b :
                new JButton[] {
                    btnTai, btnMoi, btnSua, btnXoa, btnDuyet, btnTuChoi, btnThem, btnBo, btnLuu
                }) b.addActionListener(this);
        table.getSelectionModel()
                .addListSelectionListener(
                        e -> {
                            if (!e.getValueIsAdjusting()) capNhatNutPhieu();
                        });
        try {
            if (ConnectDB.con == null || ConnectDB.con.isClosed())
                ConnectDB.getInstance().connect();
            for (SanPham x : new SanPham_DAO().getAllSanPham()) sp.addItem(x);
        } catch (Exception e) {
            thongBaoLoi(this, e);
        }
        ganPhimTat();
        loadDataToTable();
        capNhatForm();
    }

    private void loadDataToTable() {
        int dongChon = table.getSelectedRow();
        String maChon = dongChon < 0 ? ma : String.valueOf(table.getValueAt(dongChon, 0));
        model.setRowCount(0);
        try {
            if (ConnectDB.con == null || ConnectDB.con.isClosed()) {
                ConnectDB.getInstance().connect();
            }

            List<PhieuNhapHang> list = new PhieuNhapHang_DAO().getAllPhieuNhapHang();
            ds = list;
            hienThi.clear();
            String t = txtTim.getText().toLowerCase(java.util.Locale.ROOT);
            for (PhieuNhapHang p : list) {
                if (!(p.getMaPhieuNhap()
                                + " "
                                + p.getNhaCungCap()
                                + " "
                                + p.getNhanVienLap().getMaNhanVien()
                                + " "
                                + p.getNgayLap())
                        .toLowerCase(java.util.Locale.ROOT)
                        .contains(t)) continue;
                if (loc.getSelectedIndex() > 0 && !p.getTrangThai().equals(loc.getSelectedItem()))
                    continue;
                hienThi.add(p);
                model.addRow(
                        new Object[] {
                            p.getMaPhieuNhap(),
                            p.getNgayLap(),
                            p.getNhaCungCap(),
                            p.getNhanVienLap().getMaNhanVien(),
                            p.getTrangThai(),
                            p.getQuanLyDuyet(),
                            p.getLyDoTuChoi()
                        });
            }
            if (ma != null) {
                phieuDangMo = null;
                for (PhieuNhapHang p : ds) {
                    if (ma.equals(p.getMaPhieuNhap())) {
                        phieuDangMo = p;
                        lblMa.setText(ma + " — " + p.getTrangThai());
                        break;
                    }
                }
            }
            if (maChon != null) {
                for (int i = 0; i < model.getRowCount(); i++) {
                    if (maChon.equals(model.getValueAt(i, 0))) {
                        table.setRowSelectionInterval(i, i);
                        break;
                    }
                }
            }
            capNhatNutPhieu();
            capNhatForm();
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

    private void hienDong() {
        modelCT.setRowCount(0);
        double total = 0.0;
        for (ChiTietPhieuNhapHang d : dong) {
            modelCT.addRow(
                    new Object[] {
                        d.getSanPham().getMaSanPham(),
                        d.getSanPham().getTenSanPham(),
                        d.getSoLuong(),
                        formatGia(d.getDonGiaNhap()),
                        formatGia(d.getThanhTien())
                    });
            total = InHoaDon.tron(total + d.getThanhTien());
        }
        lblTong.setText("Tổng nhập: " + formatGia(total));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        try {
            PhieuNhapHang_DAO dao = new PhieuNhapHang_DAO();
            if (o.equals(btnTai)) {
                loadDataToTable();
                return;
            }
            if (o.equals(btnMoi)) {
                ma = null;
                phieuDangMo = null;
                table.clearSelection();
                dong.clear();
                nhaCC.setText("");
                chungTu.setText("");
                nguoiGiao.setText("");
                lblMa.setText("Phiếu mới");
                hienDong();
                capNhatForm();
                tabs.setSelectedIndex(1);
                return;
            }
            if (o.equals(btnThem)) {
                SanPham x = (SanPham) sp.getSelectedItem();
                if (x == null) throw new IllegalArgumentException("Chưa có sản phẩm.");
                for (ChiTietPhieuNhapHang d : dong)
                    if (d.getSanPham().getMaSanPham().equals(x.getMaSanPham()))
                        throw new IllegalArgumentException(
                                "Sản phẩm đã có, bỏ dòng cũ rồi thêm lại để đổi số lượng.");
                ChiTietPhieuNhapHang d = new ChiTietPhieuNhapHang();
                d.setSanPham(x);
                d.setSoLuong((Integer) sl.getValue());
                d.setDonGiaNhap(InHoaDon.soTien(gia.getText()));
                dong.add(d);
                hienDong();
                return;
            }
            if (o.equals(btnBo)) {
                int i = tableCT.getSelectedRow();
                if (i >= 0) dong.remove(i);
                hienDong();
                return;
            }
            if (o.equals(btnLuu)) {
                PhieuNhapHang p = new PhieuNhapHang(ma);
                p.setNhaCungCap(nhaCC.getText());
                p.setNguoiGiao(nguoiGiao.getText());
                p.setSoChungTu(chungTu.getText());
                ma = dao.luu(p, dong, tk);
                lblMa.setText(ma + " — Chờ duyệt");
                loadDataToTable();
                return;
            }
            int i = table.getSelectedRow();
            if (i < 0) throw new IllegalArgumentException("Chọn phiếu.");
            PhieuNhapHang p = hienThi.get(i);
            if (o.equals(btnSua)) {
                phieuDangMo = p;
                ma = p.getMaPhieuNhap();
                nhaCC.setText(p.getNhaCungCap());
                chungTu.setText(p.getSoChungTu());
                nguoiGiao.setText(p.getNguoiGiao());
                dong.clear();
                dong.addAll(new ChiTietPhieuNhapHang_DAO().getTheoPhieu(ma));
                lblMa.setText(ma + " — " + p.getTrangThai());
                hienDong();
                capNhatForm();
                tabs.setSelectedIndex(1);
            } else if (o.equals(btnXoa)) {
                if (xacNhan(this, "Xóa phiếu chờ duyệt của mình?")) dao.xoa(p.getMaPhieuNhap(), tk);
            } else if (o.equals(btnDuyet)) {
                if (xacNhan(this, "Đã kiểm đếm hàng thực nhận và đồng ý cộng tồn?"))
                    dao.duyet(p.getMaPhieuNhap(), true, null, tk);
            } else if (o.equals(btnTuChoi)) {
                String lyDo = JOptionPane.showInputDialog(this, "Lý do từ chối:");
                if (lyDo != null) dao.duyet(p.getMaPhieuNhap(), false, lyDo, tk);
            }
            loadDataToTable();
        } catch (java.sql.SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this, "Lỗi SQL:\n" + ex.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            thongBaoLoi(this, ex);
        }
    }

    private boolean cuaMinh(PhieuNhapHang p) {
        return p != null
                && p.getNhanVienLap() != null
                && tk.getNhanVien().getMaNhanVien().equals(p.getNhanVienLap().getMaNhanVien());
    }

    private void capNhatNutPhieu() {
        int i = table.getSelectedRow();
        PhieuNhapHang p =
                i < 0 || i >= hienThi.size() ? null : hienThi.get(table.convertRowIndexToModel(i));
        boolean cho = p != null && "Chờ duyệt".equals(p.getTrangThai());
        boolean duyet = cho && "ADMIN".equals(tk.getQuyen()) && !cuaMinh(p);
        btnSua.setEnabled(p != null);
        btnXoa.setEnabled(cho && cuaMinh(p));
        btnDuyet.setEnabled(duyet);
        btnTuChoi.setEnabled(duyet);
    }

    private void capNhatForm() {
        boolean sua =
                ma == null
                        || (phieuDangMo != null
                                && "Chờ duyệt".equals(phieuDangMo.getTrangThai())
                                && cuaMinh(phieuDangMo));
        nhaCC.setEditable(sua);
        chungTu.setEditable(sua);
        nguoiGiao.setEditable(sua);
        gia.setEditable(sua);
        sp.setEnabled(sua);
        sl.setEnabled(sua);
        btnThem.setEnabled(sua);
        btnBo.setEnabled(sua);
        btnLuu.setEnabled(sua);
    }

    public void chonPhieu(String maCanXem) throws SQLException {
        txtTim.setText(maCanXem);
        loc.setSelectedIndex(0);
        loadDataToTable();
        tabs.setSelectedIndex(0);
        for (int i = 0; i < model.getRowCount(); i++) {
            if (maCanXem.equals(model.getValueAt(i, 0))) {
                table.setRowSelectionInterval(i, i);
                table.scrollRectToVisible(table.getCellRect(i, 0, true));
                return;
            }
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

    private void hienThiLuaChon(JComboBox<?> c, String khiRong, int width) {
        c.setPreferredSize(new Dimension(width, 32));
        c.setRenderer(
                new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(
                            JList<?> l, Object v, int i, boolean selected, boolean focus) {
                        return super.getListCellRendererComponent(
                                l, v == null ? khiRong : v, i, selected, focus);
                    }
                });
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

    private JLabel taoTieuDe(String t) {
        return GiaoDien.tieuDe(t);
    }

    private boolean xacNhan(Component c, String s) {
        return JOptionPane.showConfirmDialog(c, s, "Xác nhận", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }

    private void ganPhimTat() {
        GiaoDien.phimTatChuan(this, btnMoi, btnLuu, btnXoa, btnTai, txtTim);
        GiaoDien.phimTat(this, "F4", btnThem);
    }
}
