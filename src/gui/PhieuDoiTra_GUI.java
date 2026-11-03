package gui;

import connectDB.ConnectDB;

import dao.ChiTietDoiTra_DAO;
import dao.ChiTietHoaDon_DAO;
import dao.HoaDon_DAO;
import dao.PhieuDoiTra_DAO;

import entity.ChiTietDoiTra;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.PhieuDoiTra;
import entity.TaiKhoan;

import until.GiaoDien;

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

public class PhieuDoiTra_GUI extends JPanel implements ActionListener {
    // Màu dùng chung được khai báo ở đầu lớp.
    private static final Color PRIMARY = GiaoDien.PRIMARY,
            DARK = GiaoDien.DARK,
            BACKGROUND = GiaoDien.BACKGROUND,
            SURFACE = GiaoDien.SURFACE,
            TEXT = GiaoDien.TEXT,
            BORDER = GiaoDien.BORDER,
            ERROR = GiaoDien.ERROR;

    private final TaiKhoan tk;
    private String ma;
    private PhieuDoiTra phieuDangMo;
    private final javax.swing.JTabbedPane tabs = new javax.swing.JTabbedPane();
    private List<PhieuDoiTra> ds = new ArrayList<>(), hienThi = new ArrayList<>();
    private final List<ChiTietDoiTra> dong = new ArrayList<>();
    private final JTextField hd = new JTextField(20),
            lyDo = new JTextField(32),
            txtTim = new JTextField(16),
            tu = new JTextField("2020-01-01", 10),
            den = new JTextField(java.time.LocalDate.now().toString(), 10);
    private final JComboBox<ChiTietHoaDon> dongGoc = new JComboBox<>();
    private final JComboBox<String> tinhTrang = new JComboBox<>(new String[] {"Nguyên vẹn", "Lỗi"}),
            loc =
                    new JComboBox<>(
                            new String[] {
                                "Tất cả", "Chờ duyệt", "Đã duyệt", "Hoàn tất", "Từ chối"
                            });
    private final JSpinner sl = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
    private final JButton btnTai = taoNut("Tìm / lọc"),
            btnMoi = taoNut("Phiếu mới"),
            btnSua = taoNut("Mở sửa / xem"),
            btnXoa = taoNut("Xóa phiếu"),
            btnDuyet = taoNut("Duyệt"),
            btnTuChoi = taoNut("Từ chối"),
            btnHoan = taoNut("Xác nhận hoàn tất"),
            btnTaiHD = taoNut("Lấy dòng hóa đơn"),
            btnThem = taoNut("Thêm dòng trả"),
            btnBo = taoNut("Bỏ dòng"),
            btnLuu = taoNut("Lưu chờ duyệt");
    private final JLabel lblMa = new JLabel("Phiếu mới");
    private final DefaultTableModel
            model =
                    taoModel(
                            "Mã phiếu",
                            "Hóa đơn gốc",
                            "Ngày lập",
                            "Người lập",
                            "Trạng thái",
                            "Điểm hoàn",
                            "Điểm thu hồi",
                            "Lý do"),
            modelCT = taoModel("Mã SP", "SL trả", "Tình trạng", "Tiền hoàn dự kiến / đã chốt");
    private final JTable table = taoBang(model), tableCT = taoBang(modelCT);

    public PhieuDoiTra_GUI(TaiKhoan tk) {
        this.tk = tk;
        hienThiLuaChon(dongGoc, "Chọn dòng gốc", 300);
        GiaoDien.boCuc(this);

        btnTaiHD.setText("Lấy sản phẩm");
        btnHoan.setText("Hoàn tất trả hàng");
        btnSua.setText("Mở / xem phiếu");
        JPanel danhSach = new JPanel(new BorderLayout(0, 8));
        danhSach.setOpaque(false);
        txtTim.setColumns(14);
        danhSach.add(
                GiaoDien.doc(
                        GiaoDien.form(
                                4,
                                "Phiếu / HĐ / SĐT / NV",
                                txtTim,
                                "Trạng thái",
                                loc,
                                "Từ ngày (yyyy-MM-dd)",
                                tu,
                                "Đến ngày (yyyy-MM-dd)",
                                den),
                        taoDong(btnTai)),
                BorderLayout.NORTH);
        danhSach.add(GiaoDien.cuon(table), BorderLayout.CENTER);
        JPanel tren =
                GiaoDien.the(
                        "Danh sách phiếu đổi trả",
                        danhSach,
                        taoDong(btnMoi, btnSua, btnXoa, btnDuyet, btnTuChoi, btnHoan));
        JPanel hoaDonGoc = new JPanel(new BorderLayout(8, 0));
        hoaDonGoc.setOpaque(false);
        hoaDonGoc.add(hd, BorderLayout.CENTER);
        hoaDonGoc.add(btnTaiHD, BorderLayout.EAST);
        JPanel form = GiaoDien.form(2, "Hóa đơn gốc", hoaDonGoc, "Lý do đổi trả", lyDo);
        JPanel nhapDong = new JPanel(new BorderLayout(8, 0));
        nhapDong.setOpaque(false);
        nhapDong.add(dongGoc, BorderLayout.CENTER);
        nhapDong.add(taoDong(new JLabel("SL:"), sl, tinhTrang, btnThem, btnBo), BorderLayout.EAST);
        lblMa.setFont(GiaoDien.phong(Font.BOLD, 14));
        lblMa.setForeground(PRIMARY);
        JPanel chiTiet = new JPanel(new BorderLayout(0, 8));
        chiTiet.setOpaque(false);
        chiTiet.add(GiaoDien.doc(lblMa, form, nhapDong), BorderLayout.NORTH);
        chiTiet.add(GiaoDien.cuon(tableCT), BorderLayout.CENTER);
        JPanel duoi =
                GiaoDien.the(
                        "Nội dung phiếu",
                        chiTiet,
                        taoDong(btnLuu, new JLabel("Hàng nguyên vẹn được cộng tồn khi hoàn tất.")));
        tabs.addTab("Danh sách phiếu", tren);
        tabs.addTab("Chi tiết phiếu", duoi);
        add(tabs, BorderLayout.CENTER);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        GiaoDien.trangTri(this);
        for (JButton b :
                new JButton[] {
                    btnTai, btnMoi, btnSua, btnXoa, btnDuyet, btnTuChoi, btnHoan, btnTaiHD, btnThem,
                    btnBo, btnLuu
                }) b.addActionListener(this);
        table.getSelectionModel()
                .addListSelectionListener(
                        e -> {
                            if (!e.getValueIsAdjusting()) capNhatNutPhieu();
                        });
        tu.setToolTipText("Ngày lập từ: yyyy-MM-dd");
        den.setToolTipText("Ngày lập đến: yyyy-MM-dd");
        ganPhimTat();
        loadDataToTable();
        capNhatForm();
    }

    private void loadDataToTable() {
        PhieuDoiTra dangChon = phieuDangChon();
        String maChon = dangChon == null ? ma : dangChon.getMaPhieuDoiTra();
        model.setRowCount(0);
        try {
            if (ConnectDB.con == null || ConnectDB.con.isClosed()) {
                ConnectDB.getInstance().connect();
            }

            List<PhieuDoiTra> list = new PhieuDoiTra_DAO().getAllPhieuDoiTra();
            ds = list;
            hienThi.clear();
            String t = txtTim.getText().trim().toLowerCase(java.util.Locale.ROOT);
            java.time.LocalDate a = java.time.LocalDate.parse(tu.getText()),
                    b = java.time.LocalDate.parse(den.getText());
            if (b.isBefore(a)) throw new IllegalArgumentException("Khoảng ngày không hợp lệ.");
            java.util.Set<String> maHD = new java.util.HashSet<>();
            if (!t.isEmpty())
                for (HoaDon h : new HoaDon_DAO().timKiem(t, a, b, "", ""))
                    maHD.add(h.getMaHoaDon());
            for (PhieuDoiTra p : list) {
                if (p.getNgayLap().toLocalDate().isBefore(a)
                        || p.getNgayLap().toLocalDate().isAfter(b)) continue;
                if (!(p.getMaPhieuDoiTra()
                                        + " "
                                        + p.getHoaDonGoc().getMaHoaDon()
                                        + " "
                                        + p.getNhanVienLap().getMaNhanVien())
                                .toLowerCase(java.util.Locale.ROOT)
                                .contains(t)
                        && !maHD.contains(p.getHoaDonGoc().getMaHoaDon())) continue;
                if (loc.getSelectedIndex() > 0 && !p.getTrangThai().equals(loc.getSelectedItem()))
                    continue;
                hienThi.add(p);
                model.addRow(
                        new Object[] {
                            p.getMaPhieuDoiTra(),
                            p.getHoaDonGoc().getMaHoaDon(),
                            p.getNgayLap(),
                            p.getNhanVienLap().getMaNhanVien(),
                            p.getTrangThai(),
                            p.getDiemHoanLai(),
                            p.getDiemThuHoi(),
                            p.getLyDo()
                        });
            }
            if (ma != null) {
                phieuDangMo = null;
                for (PhieuDoiTra p : ds) {
                    if (ma.equals(p.getMaPhieuDoiTra())) {
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
            capNhatForm();
            capNhatNutPhieu();
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
        for (ChiTietDoiTra d : dong)
            modelCT.addRow(
                    new Object[] {
                        d.getChiTietHoaDon().getSanPham().getMaSanPham(),
                        d.getSoLuongTra(),
                        d.getTinhTrangHang(),
                        d.getPhieuDoiTra() == null ? "Tính khi lưu" : formatGia(d.getTienHoan())
                    });
        capNhatForm();
    }

    private void taiDongHD() throws Exception {
        dongGoc.removeAllItems();
        HoaDon h = new HoaDon_DAO().tim(hd.getText().trim());
        if (h == null || !"Đã thanh toán".equals(h.getTrangThaiThanhToan()))
            throw new IllegalArgumentException(
                    "Nhập mã hóa đơn đã thanh toán. Tra SĐT trong mục Hóa đơn để lấy mã.");
        for (ChiTietHoaDon d : new ChiTietHoaDon_DAO().getTheoHoaDon(h.getMaHoaDon()))
            dongGoc.addItem(d);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        try {
            PhieuDoiTra_DAO dao = new PhieuDoiTra_DAO();
            if (o.equals(btnTai)) {
                loadDataToTable();
                return;
            }
            if (o.equals(btnMoi)) {
                ma = null;
                phieuDangMo = null;
                table.clearSelection();
                hd.setEditable(true);
                hd.setText("");
                lyDo.setText("");
                dong.clear();
                dongGoc.removeAllItems();
                lblMa.setText("Phiếu mới");
                hienDong();
                tabs.setSelectedIndex(1);
                return;
            }
            if (o.equals(btnTaiHD)) {
                taiDongHD();
                return;
            }
            if (o.equals(btnThem)) {
                ChiTietHoaDon g = (ChiTietHoaDon) dongGoc.getSelectedItem();
                if (g == null) throw new IllegalArgumentException("Lấy dòng hóa đơn trước.");
                if (!hd.getText().trim().equals(g.getHoaDon().getMaHoaDon()))
                    throw new IllegalArgumentException("Mã hóa đơn đã đổi, lấy lại dòng.");
                for (ChiTietDoiTra a : dong)
                    if (a.getChiTietHoaDon()
                            .getSanPham()
                            .getMaSanPham()
                            .equals(g.getSanPham().getMaSanPham()))
                        throw new IllegalArgumentException(
                                "Dòng đã có, bỏ rồi thêm lại để đổi SL.");
                int n = (Integer) sl.getValue();
                if (ma == null
                        && n > dao.soConTra(hd.getText().trim(), g.getSanPham().getMaSanPham()))
                    throw new IllegalArgumentException(
                            "Vượt số còn được trả, đã tính phiếu đang xử lý.");
                ChiTietDoiTra d = new ChiTietDoiTra();
                d.setChiTietHoaDon(g);
                d.setSoLuongTra(n);
                d.setTinhTrangHang((String) tinhTrang.getSelectedItem());
                d.setTienHoan(0.0);
                dong.add(d);
                hd.setEditable(false);
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
                ma = dao.luu(ma, hd.getText().trim(), lyDo.getText(), dong, tk);
                dong.clear();
                dong.addAll(new ChiTietDoiTra_DAO().getTheoPhieu(ma));
                lblMa.setText(ma + " — Chờ duyệt");
                hienDong();
                loadDataToTable();
                return;
            }
            int i = table.getSelectedRow();
            if (i < 0) throw new IllegalArgumentException("Chọn phiếu.");
            PhieuDoiTra p = hienThi.get(i);
            if (o.equals(btnSua)) {
                phieuDangMo = p;
                ma = p.getMaPhieuDoiTra();
                hd.setText(p.getHoaDonGoc().getMaHoaDon());
                hd.setEditable(false);
                lyDo.setText(p.getLyDo());
                dong.clear();
                dong.addAll(new ChiTietDoiTra_DAO().getTheoPhieu(ma));
                lblMa.setText(ma + " — " + p.getTrangThai());
                taiDongHD();
                hienDong();
                tabs.setSelectedIndex(1);
            } else if (o.equals(btnXoa)) {
                if (xacNhan(this, "Xóa phiếu chờ của mình?")) dao.xoa(p.getMaPhieuDoiTra(), tk);
            } else if (o.equals(btnDuyet)) {
                if (xacNhan(this, "Duyệt phiếu? Bước này chưa hoàn tiền, tồn hoặc điểm."))
                    dao.duyet(p.getMaPhieuDoiTra(), true, null, tk);
            } else if (o.equals(btnTuChoi)) {
                String reason = JOptionPane.showInputDialog(this, "Lý do từ chối:");
                if (reason != null) dao.duyet(p.getMaPhieuDoiTra(), false, reason, tk);
            } else if (o.equals(btnHoan)) {
                double tien = dao.duTinh(p.getMaPhieuDoiTra(), tk);
                HoaDon h = new HoaDon_DAO().tim(p.getHoaDonGoc().getMaHoaDon());
                if (xacNhan(
                        this,
                        "Hoàn "
                                + formatGia(tien)
                                + " VND theo "
                                + h.getHinhThucThanhToan()
                                + ". Đã kiểm tra giao dịch hoàn thực tế?\n"
                                + "Chỉ xác nhận sau khi hoàn tiền cho khách.")) {
                    dao.hoanTat(p.getMaPhieuDoiTra(), tien, tk);
                    JOptionPane.showMessageDialog(
                            this, "Đã ghi nhận hoàn tất. Tồn và điểm cập nhật cùng giao dịch.");
                }
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

    private PhieuDoiTra phieuDangChon() {
        int i = table.getSelectedRow();
        if (i < 0) return null;
        i = table.convertRowIndexToModel(i);
        return i < hienThi.size() ? hienThi.get(i) : null;
    }

    private boolean cuaMinh(PhieuDoiTra p) {
        return p != null
                && p.getNhanVienLap() != null
                && tk.getNhanVien().getMaNhanVien().equals(p.getNhanVienLap().getMaNhanVien());
    }

    private void capNhatNutPhieu() {
        PhieuDoiTra p = phieuDangChon();
        boolean choDuyet = p != null && "Chờ duyệt".equals(p.getTrangThai());
        boolean quanLy = "ADMIN".equals(tk.getQuyen());
        btnSua.setEnabled(p != null);
        btnXoa.setEnabled(choDuyet && cuaMinh(p));
        btnDuyet.setEnabled(choDuyet && quanLy);
        btnTuChoi.setEnabled(choDuyet && quanLy);
        btnHoan.setEnabled(p != null && "Đã duyệt".equals(p.getTrangThai()));
    }

    /** Chỉ sửa phiếu chờ duyệt của chính nhân viên đang đăng nhập. */
    private void capNhatForm() {
        boolean sua =
                ma == null
                        || (phieuDangMo != null
                                && "Chờ duyệt".equals(phieuDangMo.getTrangThai())
                                && cuaMinh(phieuDangMo));
        hd.setEditable(sua && ma == null && dong.isEmpty());
        lyDo.setEditable(sua);
        dongGoc.setEnabled(sua);
        sl.setEnabled(sua);
        tinhTrang.setEnabled(sua);
        btnTaiHD.setEnabled(sua);
        btnThem.setEnabled(sua);
        btnBo.setEnabled(sua);
        btnLuu.setEnabled(sua);
    }

    public void chonPhieu(String maCanXem) throws SQLException {
        txtTim.setText(maCanXem);
        loc.setSelectedIndex(0);
        for (PhieuDoiTra p : new PhieuDoiTra_DAO().getAllPhieuDoiTra()) {
            if (maCanXem.equals(p.getMaPhieuDoiTra())) {
                tu.setText(p.getNgayLap().toLocalDate().toString());
                den.setText(p.getNgayLap().toLocalDate().toString());
                break;
            }
        }
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
