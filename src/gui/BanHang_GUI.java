package gui;

import connectDB.ConnectDB;

import dao.*;

import entity.*;

import until.GiaoDien;
import until.InHoaDon;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class BanHang_GUI extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;
	private static final Color PRIMARY = GiaoDien.PRIMARY,
            TEXT = GiaoDien.TEXT,
            MUTED = GiaoDien.MUTED,
            BORDER = GiaoDien.BORDER,
            SURFACE = GiaoDien.SURFACE;
    private static final double VAT = 0.10;
    private static final int CAO_THE_SP = 242;
    private final TaiKhoan tk;
    private final List<ChiTietHoaDon> gio = new ArrayList<>();
    private List<SanPham> sanPham = new ArrayList<>(), hienThi = new ArrayList<>();
    private final List<ChuongTrinhKhuyenMai> khuyenMai = new ArrayList<>();
    private String maHoaDon, loaiDangChon;
    private double thueSuat = VAT;
    private boolean dangNap;
    private final JTextField
            txtTim =
                    new JTextField() {
                        @Override
                        protected void paintComponent(Graphics g) {
                            super.paintComponent(g);
                            if (getText().isEmpty() && !hasFocus()) {
                                g.setColor(MUTED);
                                g.setFont(GiaoDien.phong(Font.PLAIN, 13));
                                g.drawString(
                                        "Tìm mã hoặc tên sản phẩm… · F2",
                                        12,
                                        (getHeight() + g.getFontMetrics().getAscent()) / 2 - 2);
                            }
                        }
                    },
            txtDiem = new JTextField("0", 5);
    private final JComboBox<KhachHang> cboKhach = new JComboBox<>();
    private final JButton btnTai = GiaoDien.nut("Tải lại"),
            btnBang = GiaoDien.nut("Xem bảng"),
            btnKhach = GiaoDien.nut("Tìm"),
            btnMoi = GiaoDien.nut("Đơn mới"),
            btnLuu = GiaoDien.nut("Lưu đơn"),
            btnTra = GiaoDien.nutChinh("Tiền mặt · F9"),
            btnQR = GiaoDien.nutVang("QR · F10");
    private final JLabel lblSoSP = nhan("Sản phẩm", 15, true, TEXT),
            lblDon = nhan("Đơn mới", 12, false, MUTED),
            lblSoDong = nhan("0 sản phẩm", 12, true, PRIMARY),
            lblDiemCo = nhan("Khách vãng lai", 12, false, MUTED),
            lblHang = nhan("0 đ", 13, false, TEXT),
            lblGiam = nhan("0 đ", 13, false, PRIMARY),
            lblQuyDoi = nhan("0 đ", 13, false, PRIMARY),
            lblVAT = nhan("0 đ", 13, false, TEXT),
            lblTenVAT = nhan("VAT (10%)", 13, false, MUTED),
            lblTong = nhan("0 đ", 24, true, PRIMARY),
            lblLoi = nhan(" ", 12, false, GiaoDien.ERROR);
    private final JPanel nhomLoai = GiaoDien.dong(),
            noiGio = new JPanel(new GridLayout(0, 1)),
            cheDoSP = new JPanel(new CardLayout());
    private final LuoiSanPham luoi = new LuoiSanPham();
    private final JScrollPane cuonGio = GiaoDien.cuonForm(noiGio);
    private final DefaultTableModel modelSP =
            new DefaultTableModel(new String[] {"Mã", "Sản phẩm", "Giá bán", "Tồn"}, 0) {
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };
    private final JTable tableSP = GiaoDien.bang(modelSP);

    public BanHang_GUI(TaiKhoan tk) {
        this.tk = tk;
        GiaoDien.boCuc(this);
        noiGio.setBackground(SURFACE);
        JPanel tim = new JPanel(new BorderLayout(8, 0));
        tim.setOpaque(false);
        txtTim.setToolTipText("Tìm mã, tên, loại hàng hoặc thương hiệu · F2");
        tim.add(txtTim, BorderLayout.CENTER);
        tim.add(btnTai, BorderLayout.EAST);
        JPanel dauSP = GiaoDien.doc(tim, hang(lblSoSP, btnBang), nhomLoai);
        cheDoSP.setOpaque(false);
        JScrollPane cuonSP = GiaoDien.cuon(luoi);
        cuonSP.setBorder(null);
        cuonSP.getViewport().setBackground(GiaoDien.BACKGROUND);
        cheDoSP.add(cuonSP, "Lưới");
        tableSP.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        cheDoSP.add(GiaoDien.cuon(tableSP), "Bảng");
        JPanel trai = new JPanel(new BorderLayout(0, 10));
        trai.setOpaque(false);
        trai.add(dauSP, BorderLayout.NORTH);
        trai.add(cheDoSP, BorderLayout.CENTER);
        cboKhach.setRenderer(
                new DefaultListCellRenderer() {
                    public Component getListCellRendererComponent(
                            JList<?> l, Object v, int i, boolean s, boolean f) {
                        KhachHang k = (KhachHang) v;
                        return super.getListCellRendererComponent(
                                l,
                                k == null
                                        ? "Khách vãng lai"
                                        : k.getHoTen() + " · " + k.getSoDienThoai(),
                                i,
                                s,
                                f);
                    }
                });
        JPanel khach = new JPanel(new BorderLayout(6, 0));
        khach.setOpaque(false);
        btnKhach.setPreferredSize(new Dimension(56, 34));
        btnKhach.setToolTipText("Tìm khách bằng SĐT, tên hoặc mã");
        khach.add(cboKhach, BorderLayout.CENTER);
        khach.add(btnKhach, BorderLayout.EAST);
        JPanel dauGio =
                cot(
                        6,
                        hang(nhan("Giỏ hàng hiện tại", 18, true, TEXT), lblSoDong),
                        lblDon,
                        khach,
                        lblDiemCo);
        JPanel giua = new JPanel(new BorderLayout(0, 8));
        giua.setOpaque(false);
        giua.add(dauGio, BorderLayout.NORTH);
        giua.add(cuonGio, BorderLayout.CENTER);
        JPanel diem = new JPanel(new BorderLayout(10, 0));
        diem.setOpaque(false);
        diem.add(nhan("Điểm sử dụng", 13, false, TEXT), BorderLayout.WEST);
        diem.add(txtDiem, BorderLayout.EAST);
        txtDiem.setHorizontalAlignment(JTextField.RIGHT);
        JPanel tong =
                GiaoDien.the(null, hang(nhan("THÀNH TIỀN", 12, true, PRIMARY), lblTong), null);
        tong.setBackground(GiaoDien.PRIMARY_SOFT);
        JPanel thanhToan = new JPanel(new GridLayout(1, 2, 8, 0));
        thanhToan.setOpaque(false);
        thanhToan.add(btnTra);
        thanhToan.add(btnQR);
        btnTra.setPreferredSize(new Dimension(150, 42));
        btnQR.setPreferredSize(new Dimension(150, 42));
        JPanel phu = new JPanel(new GridLayout(1, 2, 8, 0));
        phu.setOpaque(false);
        phu.add(btnLuu);
        phu.add(btnMoi);
        JPanel cuoi =
                cot(
                        6,
                        diem,
                        hang(nhan("Tiền hàng", 13, false, MUTED), lblHang),
                        hang(nhan("Giảm khuyến mãi", 13, false, MUTED), lblGiam),
                        hang(nhan("Quy đổi điểm", 13, false, MUTED), lblQuyDoi),
                        hang(lblTenVAT, lblVAT),
                        lblLoi,
                        tong,
                        thanhToan,
                        phu);
        JPanel phai = GiaoDien.the(null, giua, cuoi);
        trai.setMinimumSize(new Dimension(360, 220));
        phai.setMinimumSize(new Dimension(350, 220));
        JSplitPane chia = GiaoDien.tach(JSplitPane.HORIZONTAL_SPLIT, trai, phai, .64);
        chia.setResizeWeight(1.0);
        add(chia, BorderLayout.CENTER);
        GiaoDien.trangTri(this);
        for (JButton b : new JButton[] {btnTai, btnBang, btnKhach, btnMoi, btnLuu, btnTra, btnQR})
            b.addActionListener(this);
        cboKhach.addActionListener(this);
        txtTim.getDocument()
                .addDocumentListener(
                        new DocumentListener() {
                            public void insertUpdate(DocumentEvent e) {
                                locSanPham();
                            }

                            public void removeUpdate(DocumentEvent e) {
                                locSanPham();
                            }

                            public void changedUpdate(DocumentEvent e) {
                                locSanPham();
                            }
                        });
        txtDiem.getDocument()
                .addDocumentListener(
                        new DocumentListener() {
                            public void insertUpdate(DocumentEvent e) {
                                tinh();
                            }

                            public void removeUpdate(DocumentEvent e) {
                                tinh();
                            }

                            public void changedUpdate(DocumentEvent e) {
                                tinh();
                            }
                        });
        tableSP.addMouseListener(
                new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2 && tableSP.getSelectedRow() >= 0) themTuBang();
                    }
                });
        GiaoDien.phimTat(this, "F2", () -> GiaoDien.tieuDiem(txtTim));
        GiaoDien.phimTat(this, "F5", btnTai);
        GiaoDien.phimTat(this, "F8", btnLuu);
        GiaoDien.phimTat(this, "F9", btnTra);
        GiaoDien.phimTat(this, "F10", btnQR);
        GiaoDien.phimTat(this, "ctrl N", btnMoi);
        GiaoDien.phimTat(this, "ctrl ENTER", btnTra);
        loadDanhMuc();
        capNhatGio();
    }

    public void loadDanhMuc() {
        KhachHang cu = (KhachHang) cboKhach.getSelectedItem();
        dangNap = true;
        try {
            if (ConnectDB.con == null || ConnectDB.con.isClosed())
                ConnectDB.getInstance().connect();
            sanPham = new SanPham_DAO().getAllSanPham();
            List<KhachHang> ds = new KhachHang_DAO().getAllKhachHang();
            cboKhach.removeAllItems();
            cboKhach.addItem(null);
            for (KhachHang k : ds) {
                cboKhach.addItem(k);
                if (cu != null && cu.getMaKhachHang().equals(k.getMaKhachHang()))
                    cboKhach.setSelectedItem(k);
            }
            khuyenMai.clear();
            khuyenMai.addAll(new ChuongTrinhKhuyenMai_DAO().getAllChuongTrinhKhuyenMai());
            veNhomLoai();
            locSanPham();
        } catch (SQLException e) {
            baoLoi(e);
        } finally {
            dangNap = false;
            tinh();
        }
    }

    /** Nạp List -> for -> model.addRow; cùng dữ liệu cho lưới và bảng. */
    private void locSanPham() {
        modelSP.setRowCount(0);
        hienThi.clear();
        luoi.removeAll();
        String tu = txtTim.getText().trim().toLowerCase(Locale.ROOT);
        for (SanPham sp : sanPham) {
            String noiDung =
                    (sp.getMaSanPham()
                                    + " "
                                    + sp.getTenSanPham()
                                    + " "
                                    + sp.getLoaiHang()
                                    + " "
                                    + sp.getThuongHieu())
                            .toLowerCase(Locale.ROOT);
            if (!"Đang bán".equals(sp.getTrangThai())
                    || !noiDung.contains(tu)
                    || (loaiDangChon != null && !loaiDangChon.equals(sp.getLoaiHang()))) continue;
            hienThi.add(sp);
            modelSP.addRow(
                    new Object[] {
                        sp.getMaSanPham(),
                        sp.getTenSanPham(),
                        formatGia(sp.getDonGiaBan()),
                        sp.getSoLuongTon()
                    });
            luoi.add(theSanPham(sp));
        }
        lblSoSP.setText("Sản phẩm · " + hienThi.size());
        if (hienThi.isEmpty()) luoi.add(nhan("Không có sản phẩm phù hợp.", 14, false, MUTED));
        luoi.revalidate();
        luoi.repaint();
    }

    private void veNhomLoai() {
        nhomLoai.removeAll();
        java.util.Set<String> ds = new java.util.TreeSet<>();
        for (SanPham s : sanPham) if ("Đang bán".equals(s.getTrangThai())) ds.add(s.getLoaiHang());
        if (loaiDangChon != null && !ds.contains(loaiDangChon)) loaiDangChon = null;
        themNhom("Tất cả", null);
        for (String loai : ds) themNhom(loai, loai);
        nhomLoai.revalidate();
        nhomLoai.repaint();
    }

    private void themNhom(String ten, String loai) {
        JButton b =
                java.util.Objects.equals(loai, loaiDangChon)
                        ? GiaoDien.nutChinh(ten)
                        : GiaoDien.nut(ten);
        b.setFont(GiaoDien.phong(Font.PLAIN, 12));
        b.setPreferredSize(
                new Dimension(
                        Math.max(60, b.getFontMetrics(b.getFont()).stringWidth(ten) + 22), 28));
        b.addActionListener(
                e -> {
                    loaiDangChon = loai;
                    veNhomLoai();
                    locSanPham();
                });
        nhomLoai.add(b);
    }

    private JPanel theSanPham(SanPham sp) {
        JPanel anh = new JPanel(new BorderLayout());
        anh.setBackground(GiaoDien.PRIMARY_SOFT);
        JLabel tag = nhan(sp.getLoaiHang(), 11, true, PRIMARY);
        tag.setBorder(new EmptyBorder(5, 8, 0, 8));
        anh.add(tag, BorderLayout.NORTH);
        anh.add(new JLabel(anhSanPham(sp), SwingConstants.CENTER), BorderLayout.CENTER);
        anh.setPreferredSize(new Dimension(160, 110));
        JTextArea ten = new JTextArea(sp.getTenSanPham());
        ten.setFont(GiaoDien.phong(Font.BOLD, 13));
        ten.setForeground(TEXT);
        ten.setLineWrap(true);
        ten.setWrapStyleWord(true);
        ten.setEditable(false);
        ten.setOpaque(false);
        ten.setPreferredSize(new Dimension(100, 34));
        JButton them = GiaoDien.nut("+ Thêm");
        them.setPreferredSize(new Dimension(100, 30));
        them.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        them.setEnabled(sp.getSoLuongTon() > 0);
        them.addActionListener(
                e -> {
                    try {
                        themSanPham(sp);
                    } catch (Exception ex) {
                        baoLoi(ex);
                    }
                });
        JPanel thongTin =
                cot(
                        4,
                        nhan(sp.getMaSanPham(), 11, false, MUTED),
                        ten,
                        hang(
                                nhan(formatGia(sp.getDonGiaBan()) + " đ", 13, true, PRIMARY),
                                nhan(
                                        "Tồn " + sp.getSoLuongTon(),
                                        11,
                                        false,
                                        sp.getSoLuongTon() <= 5 ? GiaoDien.ERROR : MUTED)),
                        them);
        thongTin.setBorder(new EmptyBorder(8, 10, 8, 10));
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(SURFACE);
        p.setBorder(BorderFactory.createLineBorder(BORDER));
        p.add(anh, BorderLayout.NORTH);
        p.add(thongTin, BorderLayout.CENTER);
        return p;
    }

    private void themTuBang() {
        try {
            int i = tableSP.getSelectedRow();
            if (i >= 0) themSanPham(hienThi.get(tableSP.convertRowIndexToModel(i)));
        } catch (Exception e) {
            baoLoi(e);
        }
    }

    private void themSanPham(SanPham sp) {
        for (ChiTietHoaDon d : gio)
            if (d.getSanPham().getMaSanPham().equals(sp.getMaSanPham())) {
                doiSoLuong(d, d.getSoLuong() + 1);
                return;
            }
        if (sp.getSoLuongTon() < 1) throw new IllegalArgumentException("Sản phẩm đã hết hàng.");
        ChiTietHoaDon d = new ChiTietHoaDon();
        d.setSanPham(sp);
        d.setSoLuong(1);
        d.setDonGia(sp.getDonGiaBan());
        d.setGiamGia(0.0);
        gio.add(d);
        capNhatGio();
    }

    private int tonHienTai(ChiTietHoaDon d) {
        for (SanPham sp : sanPham)
            if (sp.getMaSanPham().equals(d.getSanPham().getMaSanPham())) return sp.getSoLuongTon();
        return 0;
    }

    private void doiSoLuong(ChiTietHoaDon d, int n) {
        if (n < 1 || n > tonHienTai(d))
            throw new IllegalArgumentException(
                    "Số lượng phải từ 1 đến tồn hiện có: " + tonHienTai(d));
        d.setSoLuong(n);
        d.setGiamGia(giam(d));
        capNhatGio();
    }

    private double giam(ChiTietHoaDon d) {
        ChuongTrinhKhuyenMai k = d.getChuongTrinhKhuyenMai();
        return k == null
                ? 0.0
                : InHoaDon.tron(
                        d.getDonGia() * d.getSoLuong() * k.getGiaTriGiam() / 100.0);
    }

    private void capNhatGio() {
        int viTri = cuonGio.getVerticalScrollBar().getValue();
        noiGio.removeAll();
        int sl = 0;
        for (ChiTietHoaDon d : gio) {
            noiGio.add(dongGio(d));
            sl += d.getSoLuong();
        }
        if (gio.isEmpty())
            noiGio.add(
                    GiaoDien.the(
                            null,
                            GiaoDien.ghiChu("Giỏ đang trống. Chọn Thêm trên sản phẩm để bắt đầu."),
                            null));
        lblSoDong.setText(sl + " sản phẩm · " + gio.size() + " dòng");
        noiGio.revalidate();
        noiGio.repaint();
        SwingUtilities.invokeLater(() -> cuonGio.getVerticalScrollBar().setValue(viTri));
        tinh();
    }

    private JPanel dongGio(ChiTietHoaDon d) {
        JTextArea ten = new JTextArea(d.getSanPham().getTenSanPham());
        ten.setEditable(false);
        ten.setLineWrap(true);
        ten.setWrapStyleWord(true);
        ten.setOpaque(false);
        ten.setForeground(TEXT);
        ten.setFont(GiaoDien.phong(Font.BOLD, 13));
        ten.setPreferredSize(new Dimension(100, 34));
        JButton bo = GiaoDien.nutNho("×");
        bo.setToolTipText("Bỏ sản phẩm khỏi giỏ");
        bo.addActionListener(
                e -> {
                    gio.remove(d);
                    capNhatGio();
                });
        JButton tru = GiaoDien.nutNho("−"), cong = GiaoDien.nutNho("+");
        tru.setEnabled(d.getSoLuong() > 1);
        cong.setEnabled(d.getSoLuong() < tonHienTai(d));
        tru.addActionListener(
                e -> {
                    try {
                        doiSoLuong(d, d.getSoLuong() - 1);
                    } catch (Exception ex) {
                        baoLoi(ex);
                    }
                });
        cong.addActionListener(
                e -> {
                    try {
                        doiSoLuong(d, d.getSoLuong() + 1);
                    } catch (Exception ex) {
                        baoLoi(ex);
                    }
                });
        JLabel sl = nhan(String.valueOf(d.getSoLuong()), 13, true, TEXT);
        sl.setHorizontalAlignment(SwingConstants.CENTER);
        sl.setPreferredSize(new Dimension(28, 28));
        JPanel dem = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        dem.setOpaque(false);
        dem.add(tru);
        dem.add(sl);
        dem.add(cong);
        JButton km =
                GiaoDien.nut(
                        d.getChuongTrinhKhuyenMai() == null
                                ? "Chọn KM"
                                : "KM "
                                        + formatGia(d.getChuongTrinhKhuyenMai().getGiaTriGiam())
                                        + "%");
        km.setPreferredSize(new Dimension(88, 26));
        km.setFont(GiaoDien.phong(Font.PLAIN, 11));
        km.addActionListener(e -> chonKhuyenMai(d));
        JPanel p =
                cot(
                        4,
                        hang(ten, bo),
                        hang(
                                nhan(d.getSanPham().getMaSanPham(), 11, false, MUTED),
                                nhan(formatGia(d.getDonGia()) + " đ / SP", 11, false, MUTED)),
                        hang(dem, nhan(formatGia(d.getThanhTienChiTiet()) + " đ", 14, true, TEXT)),
                        hang(
                                km,
                                nhan(
                                        d.getGiamGia() > 0
                                                ? "Giảm " + formatGia(d.getGiamGia()) + " đ"
                                                : "",
                                        11,
                                        false,
                                        PRIMARY)));
        p.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                        new EmptyBorder(10, 0, 10, 0)));
        return p;
    }

    private void chonKhuyenMai(ChiTietHoaDon d) {
        JComboBox<ChuongTrinhKhuyenMai> cb = new JComboBox<>();
        cb.addItem(null);
        LocalDate ngay = LocalDate.now();
        for (ChuongTrinhKhuyenMai k : khuyenMai)
            if ("Đang áp dụng".equals(k.getTrangThai())
                    && !ngay.isBefore(k.getNgayBatDau())
                    && !ngay.isAfter(k.getNgayKetThuc())) cb.addItem(k);
        cb.setRenderer(
                new DefaultListCellRenderer() {
                    public Component getListCellRendererComponent(
                            JList<?> l, Object v, int i, boolean s, boolean f) {
                        return super.getListCellRendererComponent(
                                l, v == null ? "Không khuyến mãi" : v, i, s, f);
                    }
                });
        if (d.getChuongTrinhKhuyenMai() != null)
            for (int i = 1; i < cb.getItemCount(); i++)
                if (d.getChuongTrinhKhuyenMai()
                        .getMaKhuyenMai()
                        .equals(cb.getItemAt(i).getMaKhuyenMai())) cb.setSelectedIndex(i);
        if (JOptionPane.showConfirmDialog(
                        this,
                        cb,
                        "Khuyến mãi cho sản phẩm",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE)
                == JOptionPane.OK_OPTION) {
            d.setChuongTrinhKhuyenMai((ChuongTrinhKhuyenMai) cb.getSelectedItem());
            d.setGiamGia(giam(d));
            capNhatGio();
        }
    }

    private int diemHopLe() {
        int diem;
        try {
            diem = Integer.parseInt(txtDiem.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Nhập điểm nguyên không âm.");
        }
        KhachHang k = (KhachHang) cboKhach.getSelectedItem();
        int max =
                InHoaDon.diemToiDa(InHoaDon.sauKhuyenMai(gio), k == null ? 0 : k.getDiemTichLuy());
        if (diem < 0 || diem > max)
            throw new IllegalArgumentException("Điểm có thể dùng: 0 đến " + max + ".");
        return diem;
    }

    private void tinh() {
        if (dangNap) return;
        KhachHang k = (KhachHang) cboKhach.getSelectedItem();
        lblDiemCo.setText(
                k == null
                        ? "Khách vãng lai · Không dùng điểm"
                        : "Điểm hiện có: " + k.getDiemTichLuy());
        try {
            int diem = diemHopLe();
            lblHang.setText(formatGia(InHoaDon.tongHang(gio)) + " đ");
            lblGiam.setText(
                    "− "
                            + formatGia(InHoaDon.tron(InHoaDon.tongHang(gio) - InHoaDon.sauKhuyenMai(gio)))
                            + " đ");
            lblQuyDoi.setText(
                    "− " + formatGia(InHoaDon.MOT_DIEM * diem) + " đ");
            lblVAT.setText(formatGia(InHoaDon.vat(gio, diem, thueSuat)) + " đ");
            lblTong.setText(formatGia(InHoaDon.phaiTra(gio, diem, thueSuat)) + " đ");
            lblTenVAT.setText(
                    "VAT ("
                            + formatGia(thueSuat * 100.0)
                            + "%)");
            lblLoi.setText(" ");
            btnLuu.setEnabled(!gio.isEmpty());
            btnTra.setEnabled(!gio.isEmpty());
            btnQR.setEnabled(!gio.isEmpty());
        } catch (IllegalArgumentException e) {
            lblLoi.setText(e.getMessage());
            lblTong.setText("—");
            btnLuu.setEnabled(false);
            btnTra.setEnabled(false);
            btnQR.setEnabled(false);
        }
    }

    private void donMoi() {
        gio.clear();
        maHoaDon = null;
        thueSuat = VAT;
        txtDiem.setText("0");
        cboKhach.setSelectedIndex(0);
        lblDon.setText("Đơn mới");
        loadDanhMuc();
        capNhatGio();
    }

    public void moHoaDon(String ma) throws Exception {
        HoaDon h = new HoaDon_DAO().tim(ma);
        if (h == null || !"Chưa thanh toán".equals(h.getTrangThaiThanhToan()))
            throw new IllegalArgumentException("Chỉ mở đơn chưa thanh toán.");
        if (!tk.getNhanVien().getMaNhanVien().equals(h.getNhanVien().getMaNhanVien()))
            throw new IllegalArgumentException("Chỉ mở đơn do mình lập.");
        if (!gio.isEmpty()
                && !ma.equals(maHoaDon)
                && JOptionPane.showConfirmDialog(
                                this,
                                "Thay giỏ hiện tại bằng đơn " + ma + "?",
                                "Mở hóa đơn",
                                JOptionPane.YES_NO_OPTION)
                        != JOptionPane.YES_OPTION) return;
        loadDanhMuc();
        dangNap = true;
        try {
            gio.clear();
            gio.addAll(new ChiTietHoaDon_DAO().getTheoHoaDon(ma));
            for (ChiTietHoaDon d : gio)
                if (d.getChuongTrinhKhuyenMai() != null)
                    for (ChuongTrinhKhuyenMai km : khuyenMai)
                        if (km.getMaKhuyenMai()
                                .equals(d.getChuongTrinhKhuyenMai().getMaKhuyenMai()))
                            d.setChuongTrinhKhuyenMai(km);
            cboKhach.setSelectedIndex(0);
            if (h.getKhachHang() != null)
                for (int i = 1; i < cboKhach.getItemCount(); i++)
                    if (h.getKhachHang()
                            .getMaKhachHang()
                            .equals(cboKhach.getItemAt(i).getMaKhachHang()))
                        cboKhach.setSelectedIndex(i);
            maHoaDon = ma;
            thueSuat = h.getThueSuatVAT();
            txtDiem.setText(String.valueOf(h.getDiemSuDung()));
            lblDon.setText(ma);
        } finally {
            dangNap = false;
            capNhatGio();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        try {
            if (o == btnTai) {
                loadDanhMuc();
                capNhatGio();
            } else if (o == cboKhach) {
                if (!dangNap && cboKhach.getSelectedItem() == null) txtDiem.setText("0");
                tinh();
            } else if (o == btnKhach) {
                timKhachHang();
            } else if (o == btnBang) {
                boolean bang = "Xem bảng".equals(btnBang.getText());
                ((CardLayout) cheDoSP.getLayout()).show(cheDoSP, bang ? "Bảng" : "Lưới");
                btnBang.setText(bang ? "Xem lưới" : "Xem bảng");
            } else if (o == btnMoi) {
                if (gio.isEmpty()
                        || JOptionPane.showConfirmDialog(
                                        this,
                                        "Tạo đơn mới? Giỏ chưa lưu sẽ bị bỏ.",
                                        "Đơn mới",
                                        JOptionPane.YES_NO_OPTION)
                                == JOptionPane.YES_OPTION) donMoi();
            } else if (o == btnLuu || o == btnTra || o == btnQR) {
                if (gio.isEmpty()) throw new IllegalArgumentException("Giỏ hàng đang trống.");
                HoaDon h = new HoaDon(maHoaDon);
                h.setKhachHang((KhachHang) cboKhach.getSelectedItem());
                h.setDiemSuDung(diemHopLe());
                h.setThueSuatVAT(thueSuat);
                maHoaDon = new HoaDon_DAO().luuNhap(h, gio, tk);
                lblDon.setText(maHoaDon);
                if (o == btnLuu) JOptionPane.showMessageDialog(this, "Đã lưu đơn chưa thanh toán.");
                else {
                    ThanhToan_GUI p =
                            new ThanhToan_GUI(
                                    (Frame) SwingUtilities.getWindowAncestor(this), maHoaDon, tk);
                    p.chonHinhThuc(o == btnQR ? "Chuyển khoản QR" : "Tiền mặt");
                    p.setVisible(true);
                    if (p.daThanhToan()) donMoi();
                }
            }
        } catch (Exception ex) {
            baoLoi(ex);
        }
    }

    private void timKhachHang() {
        String tu = JOptionPane.showInputDialog(this, "Nhập số điện thoại, tên hoặc mã khách:");
        if (tu == null || tu.isBlank()) return;
        tu = tu.trim().toLowerCase(Locale.ROOT);
        List<KhachHang> ds = new ArrayList<>();
        for (int i = 1; i < cboKhach.getItemCount(); i++) {
            KhachHang k = cboKhach.getItemAt(i);
            if ((k.getMaKhachHang() + " " + k.getHoTen() + " " + k.getSoDienThoai())
                    .toLowerCase(Locale.ROOT)
                    .contains(tu)) ds.add(k);
        }
        if (ds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng phù hợp.");
        } else if (ds.size() == 1) {
            cboKhach.setSelectedItem(ds.get(0));
        } else {
            JComboBox<KhachHang> chon = new JComboBox<>();
            for (KhachHang k : ds) chon.addItem(k);
            chon.setRenderer(cboKhach.getRenderer());
            if (JOptionPane.showConfirmDialog(
                            this,
                            chon,
                            "Chọn khách hàng",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE)
                    == JOptionPane.OK_OPTION) cboKhach.setSelectedItem(chon.getSelectedItem());
        }
    }

    private void baoLoi(Exception e) {
        if (e instanceof SQLException) e.printStackTrace();
        JOptionPane.showMessageDialog(
                this, e.getMessage(), "Không thực hiện được", JOptionPane.ERROR_MESSAGE);
    }

    private String formatGia(double n) {
        return InHoaDon.tien(n);
    }

    private JLabel nhan(String s, int co, boolean dam, Color mau) {
        JLabel l = new JLabel(s);
        l.setFont(GiaoDien.phong(dam ? Font.BOLD : Font.PLAIN, co));
        l.setForeground(mau);
        return l;
    }

    private JPanel hang(Component a, Component b) {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setOpaque(false);
        p.add(a, BorderLayout.CENTER);
        p.add(b, BorderLayout.EAST);
        return p;
    }

    private JPanel cot(int gap, Component... cs) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        for (int i = 0; i < cs.length; i++) {
            Component c = cs[i];
            if (c instanceof JComponent) ((JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
            p.add(c);
            if (i < cs.length - 1) p.add(Box.createVerticalStrut(gap));
        }
        return p;
    }

    private final class LuoiSanPham extends JPanel implements Scrollable {
        LuoiSanPham() {
            super(new GridLayout(0, 3, 10, 10));
            setOpaque(false);
        }

        public Dimension getPreferredSize() {
            int w = getParent() == null ? 660 : Math.max(190, getParent().getWidth());
            int n = Math.max(1, Math.min(4, (w + 10) / 195));
            ((GridLayout) getLayout()).setColumns(n);
            int hang = Math.max(1, (getComponentCount() + n - 1) / n);
            return new Dimension(w, hang * CAO_THE_SP + (hang - 1) * 10);
        }

        public Dimension getPreferredScrollableViewportSize() {
            return new Dimension(600, 480);
        }

        public int getScrollableUnitIncrement(Rectangle r, int o, int d) {
            return 24;
        }

        public int getScrollableBlockIncrement(Rectangle r, int o, int d) {
            return 200;
        }

        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    /** Có thể đặt ảnh ở resource/sanpham/MaSP.png; thiếu ảnh vẫn có biểu tượng loại hàng. */
    private Icon anhSanPham(SanPham sp) {
        for (String ext : new String[] {"png", "jpg", "jpeg"}) {
            File f = new File("resource/hinhanh/" + sp.getMaSanPham() + "." + ext);
            if (f.isFile()) {
                ImageIcon i = new ImageIcon(f.getAbsolutePath());
                if (i.getIconWidth() > 0) {
                    double t = Math.min(160.0 / i.getIconWidth(), 88.0 / i.getIconHeight());
                    return new ImageIcon(
                            i.getImage()
                                    .getScaledInstance(
                                            (int) (i.getIconWidth() * t),
                                            (int) (i.getIconHeight() * t),
                                            Image.SCALE_SMOOTH));
                }
            }
        }
        final String loai = sp.getLoaiHang().toLowerCase(Locale.ROOT);
        return new Icon() {
            public int getIconWidth() {
                return 160;
            }

            public int getIconHeight() {
                return 88;
            }

            public void paintIcon(Component c, Graphics g0, int x, int y) {
                Graphics2D g = (Graphics2D) g0.create();
                g.translate(x, y);
                g.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setStroke(new BasicStroke(3));
                g.setColor(PRIMARY);
                if (loai.contains("lạnh")) {
                    g.drawRoundRect(54, 4, 52, 76, 7, 7);
                    g.drawLine(54, 31, 106, 31);
                    g.drawLine(62, 15, 62, 24);
                    g.drawLine(62, 42, 62, 58);
                } else if (loai.contains("laptop") || loai.contains("máy tính")) {
                    g.drawRoundRect(35, 12, 90, 54, 6, 6);
                    g.drawLine(25, 74, 135, 74);
                    g.drawLine(35, 66, 25, 74);
                    g.drawLine(125, 66, 135, 74);
                } else if (loai.contains("tivi") || loai.contains("tv")) {
                    g.drawRoundRect(22, 9, 116, 62, 6, 6);
                    g.drawLine(65, 72, 57, 81);
                    g.drawLine(95, 72, 103, 81);
                } else if (loai.contains("điều")) {
                    g.drawRoundRect(20, 19, 120, 44, 10, 10);
                    g.drawLine(28, 49, 132, 49);
                    g.drawLine(42, 68, 39, 80);
                    g.drawLine(80, 68, 77, 80);
                    g.drawLine(115, 68, 112, 80);
                } else {
                    g.drawRoundRect(42, 12, 76, 63, 12, 12);
                    g.drawOval(62, 29, 36, 36);
                    g.fillOval(89, 19, 5, 5);
                    g.drawLine(52, 79, 108, 79);
                }
                g.dispose();
            }
        };
    }
}
