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

import util.GiaoDien;

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
    // MÃ u dÃ¹ng chung Ä‘Æ°á»£c khai bÃ¡o á»Ÿ Ä‘áº§u lá»›p.
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
    private final JComboBox<String> tinhTrang = new JComboBox<>(new String[] {"NguyÃªn váº¹n", "Lá»—i"}),
            loc =
                    new JComboBox<>(
                            new String[] {
                                "Táº¥t cáº£", "Chá» duyá»‡t", "ÄÃ£ duyá»‡t", "HoÃ n táº¥t", "Tá»« chá»‘i"
                            });
    private final JSpinner sl = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
    private final JButton btnTai = taoNut("TÃ¬m / lá»c"),
            btnMoi = taoNut("Phiáº¿u má»›i"),
            btnSua = taoNut("Má»Ÿ sá»­a / xem"),
            btnXoa = taoNut("XÃ³a phiáº¿u"),
            btnDuyet = taoNut("Duyá»‡t"),
            btnTuChoi = taoNut("Tá»« chá»‘i"),
            btnHoan = taoNut("XÃ¡c nháº­n hoÃ n táº¥t"),
            btnTaiHD = taoNut("Láº¥y dÃ²ng hÃ³a Ä‘Æ¡n"),
            btnThem = taoNut("ThÃªm dÃ²ng tráº£"),
            btnBo = taoNut("Bá» dÃ²ng"),
            btnLuu = taoNut("LÆ°u chá» duyá»‡t");
    private final JLabel lblMa = new JLabel("Phiáº¿u má»›i");
    private final DefaultTableModel
            model =
                    taoModel(
                            "MÃ£ phiáº¿u",
                            "HÃ³a Ä‘Æ¡n gá»‘c",
                            "NgÃ y láº­p",
                            "NgÆ°á»i láº­p",
                            "Tráº¡ng thÃ¡i",
                            "Äiá»ƒm hoÃ n",
                            "Äiá»ƒm thu há»“i",
                            "LÃ½ do"),
            modelCT = taoModel("MÃ£ SP", "SL tráº£", "TÃ¬nh tráº¡ng", "Tiá»n hoÃ n dá»± kiáº¿n / Ä‘Ã£ chá»‘t");
    private final JTable table = taoBang(model), tableCT = taoBang(modelCT);

    public PhieuDoiTra_GUI(TaiKhoan tk) {
        this.tk = tk;
        hienThiLuaChon(dongGoc, "Chá»n dÃ²ng gá»‘c", 300);
        GiaoDien.boCuc(this);

        btnTaiHD.setText("Láº¥y sáº£n pháº©m");
        btnHoan.setText("HoÃ n táº¥t tráº£ hÃ ng");
        btnSua.setText("Má»Ÿ / xem phiáº¿u");
        JPanel danhSach = new JPanel(new BorderLayout(0, 8));
        danhSach.setOpaque(false);
        txtTim.setColumns(14);
        danhSach.add(
                GiaoDien.doc(
                        GiaoDien.form(
                                4,
                                "Phiáº¿u / HÄ / SÄT / NV",
                                txtTim,
                                "Tráº¡ng thÃ¡i",
                                loc,
                                "Tá»« ngÃ y (yyyy-MM-dd)",
                                tu,
                                "Äáº¿n ngÃ y (yyyy-MM-dd)",
                                den),
                        taoDong(btnTai)),
                BorderLayout.NORTH);
        danhSach.add(GiaoDien.cuon(table), BorderLayout.CENTER);
        JPanel tren =
                GiaoDien.the(
                        "Danh sÃ¡ch phiáº¿u Ä‘á»•i tráº£",
                        danhSach,
                        taoDong(btnMoi, btnSua, btnXoa, btnDuyet, btnTuChoi, btnHoan));
        JPanel hoaDonGoc = new JPanel(new BorderLayout(8, 0));
        hoaDonGoc.setOpaque(false);
        hoaDonGoc.add(hd, BorderLayout.CENTER);
        hoaDonGoc.add(btnTaiHD, BorderLayout.EAST);
        JPanel form = GiaoDien.form(2, "HÃ³a Ä‘Æ¡n gá»‘c", hoaDonGoc, "LÃ½ do Ä‘á»•i tráº£", lyDo);
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
                        "Ná»™i dung phiáº¿u",
                        chiTiet,
                        taoDong(btnLuu, new JLabel("HÃ ng nguyÃªn váº¹n Ä‘Æ°á»£c cá»™ng tá»“n khi hoÃ n táº¥t.")));
        tabs.addTab("Danh sÃ¡ch phiáº¿u", tren);
        tabs.addTab("Chi tiáº¿t phiáº¿u", duoi);
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
        tu.setToolTipText("NgÃ y láº­p tá»«: yyyy-MM-dd");
        den.setToolTipText("NgÃ y láº­p Ä‘áº¿n: yyyy-MM-dd");
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
            if (b.isBefore(a)) throw new IllegalArgumentException("Khoáº£ng ngÃ y khÃ´ng há»£p lá»‡.");
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
                        lblMa.setText(ma + " â€” " + p.getTrangThai());
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
                    "Lá»—i táº£i dá»¯ liá»‡u SQL:\n" + e.getMessage(),
                    "Lá»—i SQL",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    this, e.getMessage(), "ThÃ´ng bÃ¡o", JOptionPane.WARNING_MESSAGE);
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
                        d.getPhieuDoiTra() == null ? "TÃ­nh khi lÆ°u" : formatGia(d.getTienHoan())
                    });
        capNhatForm();
    }

    private void taiDongHD() throws Exception {
        dongGoc.removeAllItems();
        HoaDon h = new HoaDon_DAO().tim(hd.getText().trim());
        if (h == null || !"ÄÃ£ thanh toÃ¡n".equals(h.getTrangThaiThanhToan()))
            throw new IllegalArgumentException(
                    "Nháº­p mÃ£ hÃ³a Ä‘Æ¡n Ä‘Ã£ thanh toÃ¡n. Tra SÄT trong má»¥c HÃ³a Ä‘Æ¡n Ä‘á»ƒ láº¥y mÃ£.");
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
                lblMa.setText("Phiáº¿u má»›i");
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
                if (g == null) throw new IllegalArgumentException("Láº¥y dÃ²ng hÃ³a Ä‘Æ¡n trÆ°á»›c.");
                if (!hd.getText().trim().equals(g.getHoaDon().getMaHoaDon()))
                    throw new IllegalArgumentException("MÃ£ hÃ³a Ä‘Æ¡n Ä‘Ã£ Ä‘á»•i, láº¥y láº¡i dÃ²ng.");
                for (ChiTietDoiTra a : dong)
                    if (a.getChiTietHoaDon()
                            .getSanPham()
                            .getMaSanPham()
                            .equals(g.getSanPham().getMaSanPham()))
                        throw new IllegalArgumentException(
                                "DÃ²ng Ä‘Ã£ cÃ³, bá» rá»“i thÃªm láº¡i Ä‘á»ƒ Ä‘á»•i SL.");
                int n = (Integer) sl.getValue();
                if (ma == null
                        && n > dao.soConTra(hd.getText().trim(), g.getSanPham().getMaSanPham()))
                    throw new IllegalArgumentException(
                            "VÆ°á»£t sá»‘ cÃ²n Ä‘Æ°á»£c tráº£, Ä‘Ã£ tÃ­nh phiáº¿u Ä‘ang xá»­ lÃ½.");
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
                lblMa.setText(ma + " â€” Chá» duyá»‡t");
                hienDong();
                loadDataToTable();
                return;
            }
            int i = table.getSelectedRow();
            if (i < 0) throw new IllegalArgumentException("Chá»n phiáº¿u.");
            PhieuDoiTra p = hienThi.get(i);
            if (o.equals(btnSua)) {
                phieuDangMo = p;
                ma = p.getMaPhieuDoiTra();
                hd.setText(p.getHoaDonGoc().getMaHoaDon());
                hd.setEditable(false);
                lyDo.setText(p.getLyDo());
                dong.clear();
                dong.addAll(new ChiTietDoiTra_DAO().getTheoPhieu(ma));
                lblMa.setText(ma + " â€” " + p.getTrangThai());
                taiDongHD();
                hienDong();
                tabs.setSelectedIndex(1);
            } else if (o.equals(btnXoa)) {
                if (xacNhan(this, "XÃ³a phiáº¿u chá» cá»§a mÃ¬nh?")) dao.xoa(p.getMaPhieuDoiTra(), tk);
            } else if (o.equals(btnDuyet)) {
                if (xacNhan(this, "Duyá»‡t phiáº¿u? BÆ°á»›c nÃ y chÆ°a hoÃ n tiá»n, tá»“n hoáº·c Ä‘iá»ƒm."))
                    dao.duyet(p.getMaPhieuDoiTra(), true, null, tk);
            } else if (o.equals(btnTuChoi)) {
                String reason = JOptionPane.showInputDialog(this, "LÃ½ do tá»« chá»‘i:");
                if (reason != null) dao.duyet(p.getMaPhieuDoiTra(), false, reason, tk);
            } else if (o.equals(btnHoan)) {
                double tien = dao.duTinh(p.getMaPhieuDoiTra(), tk);
                HoaDon h = new HoaDon_DAO().tim(p.getHoaDonGoc().getMaHoaDon());
                if (xacNhan(
                        this,
                        "HoÃ n "
                                + formatGia(tien)
                                + " VND theo "
                                + h.getHinhThucThanhToan()
                                + ". ÄÃ£ kiá»ƒm tra giao dá»‹ch hoÃ n thá»±c táº¿?\n"
                                + "Chá»‰ xÃ¡c nháº­n sau khi hoÃ n tiá»n cho khÃ¡ch.")) {
                    dao.hoanTat(p.getMaPhieuDoiTra(), tien, tk);
                    JOptionPane.showMessageDialog(
                            this, "ÄÃ£ ghi nháº­n hoÃ n táº¥t. Tá»“n vÃ  Ä‘iá»ƒm cáº­p nháº­t cÃ¹ng giao dá»‹ch.");
                }
            }
            loadDataToTable();
        } catch (java.sql.SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this, "Lá»—i SQL:\n" + ex.getMessage(), "Lá»—i SQL", JOptionPane.ERROR_MESSAGE);
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
        boolean choDuyet = p != null && "Chá» duyá»‡t".equals(p.getTrangThai());
        boolean quanLy = "ADMIN".equals(tk.getQuyen());
        btnSua.setEnabled(p != null);
        btnXoa.setEnabled(choDuyet && cuaMinh(p));
        btnDuyet.setEnabled(choDuyet && quanLy);
        btnTuChoi.setEnabled(choDuyet && quanLy);
        btnHoan.setEnabled(p != null && "ÄÃ£ duyá»‡t".equals(p.getTrangThai()));
    }

    /** Chá»‰ sá»­a phiáº¿u chá» duyá»‡t cá»§a chÃ­nh nhÃ¢n viÃªn Ä‘ang Ä‘Äƒng nháº­p. */
    private void capNhatForm() {
        boolean sua =
                ma == null
                        || (phieuDangMo != null
                                && "Chá» duyá»‡t".equals(phieuDangMo.getTrangThai())
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
                c, e.getMessage(), "KhÃ´ng thá»±c hiá»‡n Ä‘Æ°á»£c", JOptionPane.ERROR_MESSAGE);
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
        return JOptionPane.showConfirmDialog(c, s, "XÃ¡c nháº­n", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }

    private void ganPhimTat() {
        GiaoDien.phimTatChuan(this, btnMoi, btnLuu, btnXoa, btnTai, txtTim);
        GiaoDien.phimTat(this, "F4", btnThem);
    }
}
