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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class SanPham_GUI extends JPanel implements ActionListener {
    // MÃ u dÃ¹ng chung Ä‘Æ°á»£c khai bÃ¡o á»Ÿ Ä‘áº§u lá»›p.
    private static final Color PRIMARY = GiaoDien.PRIMARY,
            DARK = GiaoDien.DARK,
            BACKGROUND = GiaoDien.BACKGROUND,
            SURFACE = GiaoDien.SURFACE,
            TEXT = GiaoDien.TEXT,
            MUTED = GiaoDien.MUTED,
            BORDER = GiaoDien.BORDER,
            ERROR = GiaoDien.ERROR;

    private final TaiKhoan tk;
    private final SanPham_DAO sanPhamDAO = new SanPham_DAO();
    private List<SanPham> danhSach = new ArrayList<>(), hienThi = new ArrayList<>();
    private String maCu;

    // --- Quáº£n lÃ½ hÃ¬nh áº£nh ---
    private final JLabel lblHinhAnh = new JLabel();
    private final JButton btnChonAnh = taoNut("Chá»n áº£nh..."),
            btnXoaAnh = taoNut("Bá» áº£nh");
    private String duongDanAnhHienTai;
    private File fileAnhMoi;

    // --- Form nháº­p liá»‡u ---
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
            new JComboBox<>(new String[] {"Äang bÃ¡n", "Ngá»«ng bÃ¡n"});
    private final JTextField txtTim = new JTextField(20);
    private final JLabel lblSoLuong = new JLabel();
    private final JButton btnMoi = taoNut("Táº¡o má»›i"),
            btnLuu = taoNut("LÆ°u"),
            btnXoa = taoNut("XÃ³a"),
            btnTim = taoNut("TÃ¬m"),
            btnTai = taoNut("Táº£i láº¡i / bá» lá»c");
    private final JTextField giaTu = new JTextField(8), giaDen = new JTextField(8);
    private final JComboBox<String> locLoai = new JComboBox<>(),
            locHang = new JComboBox<>(),
            locTT = new JComboBox<>(new String[] {"Táº¥t cáº£", "Äang bÃ¡n", "Ngá»«ng bÃ¡n"});
    private final DefaultTableModel model =
            taoModel(
                    "MÃ£ SP (SP0001)",
                    "TÃªn sáº£n pháº©m",
                    "Loáº¡i hÃ ng",
                    "ThÆ°Æ¡ng hiá»‡u",
                    "GiÃ¡ bÃ¡n",
                    "GiÃ¡ nháº­p",
                    "ThÃ´ng sá»‘ kÄ© thuáº­t",
                    "Xuáº¥t xá»©",
                    "Báº£o hÃ nh (thÃ¡ng)",
                    "Tráº¡ng thÃ¡i",
                    "Tá»“n kho");
    private final JTable table = taoBang(model);

    public SanPham_GUI(TaiKhoan tk) {
        this.tk = tk;
        maSanPham.setEditable(false);
        maSanPham.setToolTipText("MÃ£ tá»± sinh khi táº¡o má»›i");
        GiaoDien.boCuc(this);

        // Khung hiá»ƒn thá»‹ áº£nh sáº£n pháº©m xem trÆ°á»›c
        lblHinhAnh.setPreferredSize(new Dimension(140, 140));
        lblHinhAnh.setMaximumSize(new Dimension(140, 140));
        lblHinhAnh.setHorizontalAlignment(SwingConstants.CENTER);
        lblHinhAnh.setVerticalAlignment(SwingConstants.CENTER);
        lblHinhAnh.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
        lblHinhAnh.setBackground(SURFACE);
        lblHinhAnh.setOpaque(true);
        xoaAnh();

        JPanel pnlNutAnh = new JPanel(new GridLayout(2, 1, 0, 8));
        pnlNutAnh.setOpaque(false);
        pnlNutAnh.add(btnChonAnh);
        pnlNutAnh.add(btnXoaAnh);

        JPanel pnlAnh = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        pnlAnh.setOpaque(false);
        pnlAnh.add(lblHinhAnh);
        pnlAnh.add(pnlNutAnh);

        JPanel form =
                GiaoDien.form(
                        2,
                        "MÃ£ sáº£n pháº©m",
                        maSanPham,
                        "TÃªn sáº£n pháº©m",
                        tenSanPham,
                        "Loáº¡i hÃ ng",
                        loaiHang,
                        "ThÆ°Æ¡ng hiá»‡u",
                        thuongHieu,
                        "GiÃ¡ bÃ¡n (VND)",
                        donGiaBan,
                        "GiÃ¡ nháº­p (VND)",
                        giaNhap,
                        "ThÃ´ng sá»‘ kÄ© thuáº­t",
                        thongSoKiThuat,
                        "Xuáº¥t xá»©",
                        xuatXu,
                        "Báº£o hÃ nh (thÃ¡ng)",
                        thoiHanBaoHanh,
                        "Tráº¡ng thÃ¡i",
                        trangThai);

        JPanel pnlThongTinForm = GiaoDien.doc(pnlAnh, form);

        JPanel trai =
                GiaoDien.the(
                        "ThÃ´ng tin sáº£n pháº©m",
                        GiaoDien.cuonForm(pnlThongTinForm),
                        taoDong(btnMoi, btnLuu, btnXoa, btnTai));
        JPanel loc =
                GiaoDien.form(
                        3,
                        "MÃ£ / tÃªn / thÃ´ng tin",
                        txtTim,
                        "Loáº¡i hÃ ng",
                        locLoai,
                        "ThÆ°Æ¡ng hiá»‡u",
                        locHang,
                        "Tráº¡ng thÃ¡i",
                        locTT,
                        "GiÃ¡ tá»«",
                        giaTu,
                        "GiÃ¡ Ä‘áº¿n",
                        giaDen);
        JPanel danhSach = new JPanel(new BorderLayout(0, 12));
        danhSach.setOpaque(false);
        danhSach.add(GiaoDien.doc(loc, taoDong(btnTim, lblSoLuong)), BorderLayout.NORTH);
        danhSach.add(GiaoDien.cuon(table), BorderLayout.CENTER);
        JPanel phai = GiaoDien.the("Danh sÃ¡ch sáº£n pháº©m", danhSach, null);
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
        btnChonAnh.addActionListener(this);
        btnXoaAnh.addActionListener(this);
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
        btnChonAnh.setEnabled(quanLy);
        btnXoaAnh.setEnabled(quanLy);

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
            locLoai.addItem("Táº¥t cáº£");
            locHang.addItem("Táº¥t cáº£");
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
                    && InHoaDon.soTien(giaTu.getText()) > InHoaDon.soTien(giaDen.getText()))
                throw new IllegalArgumentException("GiÃ¡ tá»« khÃ´ng Ä‘Æ°á»£c lá»›n hÆ¡n giÃ¡ Ä‘áº¿n.");
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
                        && sp.getDonGiaBan() < InHoaDon.soTien(giaTu.getText()))
                    continue;
                if (!giaDen.getText().isBlank()
                        && sp.getDonGiaBan() > InHoaDon.soTien(giaDen.getText()))
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
            lblSoLuong.setText("Tá»•ng: " + hienThi.size() + " báº£n ghi");
            if (maCu == null) hienMaTiepTheo();

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

        // Hiá»ƒn thá»‹ áº£nh cá»§a sáº£n pháº©m Ä‘Æ°á»£c chá»n
        fileAnhMoi = null;
        duongDanAnhHienTai = sp.getHinhAnh();
        taiAnhLenKhung(duongDanAnhHienTai, sp.getMaSanPham());
    }

    private void hienMaTiepTheo() {
        maSanPham.setEditable(false);
        try {
            maSanPham.setText(sanPhamDAO.layMaTiepTheo());
            maSanPham.setToolTipText("MÃ£ tá»± sinh khi táº¡o má»›i");
        } catch (SQLException e) {
            maSanPham.setText("");
            maSanPham.setToolTipText("ChÆ°a láº¥y Ä‘Æ°á»£c mÃ£: " + e.getMessage());
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

        // Reset áº£nh
        fileAnhMoi = null;
        duongDanAnhHienTai = null;
        xoaAnh();

        hienMaTiepTheo();
        tenSanPham.requestFocusInWindow();
    }

    private void chonAnh() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chá»n hÃ¬nh áº£nh sáº£n pháº©m");
        chooser.setFileFilter(
                new FileNameExtensionFilter(
                        "HÃ¬nh áº£nh (*.png, *.jpg, *.jpeg)", "png", "jpg", "jpeg"));
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            fileAnhMoi = chooser.getSelectedFile();
            duongDanAnhHienTai = fileAnhMoi.getAbsolutePath();
            taiAnhLenKhung(duongDanAnhHienTai, maCu != null ? maCu : maSanPham.getText().trim());
        }
    }

    private void boAnh() {
        fileAnhMoi = null;
        duongDanAnhHienTai = null;
        xoaAnh();
    }

    private void xoaAnh() {
        lblHinhAnh.setIcon(null);
        lblHinhAnh.setText(
                "<html><center><span style='color:#627368;'>ChÆ°a cÃ³ áº£nh</span><br>"
                        + "<small style='color:#9E9E9E;'>(Báº¥m Chá»n áº£nh...)</small></center></html>");
    }

    /**
     * Táº£i vÃ  co dÃ£n áº£nh Ä‘áº¡i diá»‡n cho sáº£n pháº©m. CÃ³ cÆ¡ cháº¿ fallback an toÃ n khi file thiáº¿u hoáº·c sai Ä‘Æ°á»ng dáº«n.
     */
    private void taiAnhLenKhung(String duongDan, String ma) {
        File f = null;
        if (duongDan != null && !duongDan.isBlank()) {
            f = new File(duongDan);
            if (!f.isFile()) {
                f = new File("resource/hinhanh/" + duongDan);
            }
            if (!f.isFile()) {
                f = new File("resource/sanpham/" + duongDan);
            }
        }
        // Thá»­ tÃ¬m theo mÃ£ sáº£n pháº©m náº¿u Ä‘Æ°á»ng dáº«n trong DB khÃ´ng tÃ¬m tháº¥y
        if ((f == null || !f.isFile()) && ma != null && !ma.isBlank()) {
            for (String ext : new String[] {"png", "jpg", "jpeg"}) {
                File thu = new File("resource/hinhanh/" + ma + "." + ext);
                if (thu.isFile()) {
                    f = thu;
                    break;
                }
                thu = new File("resource/sanpham/" + ma + "." + ext);
                if (thu.isFile()) {
                    f = thu;
                    break;
                }
            }
        }

        if (f != null && f.isFile()) {
            try {
                ImageIcon icon = new ImageIcon(f.getAbsolutePath());
                Image img = icon.getImage();
                if (img.getWidth(null) > 0 && img.getHeight(null) > 0) {
                    int wMax = 130, hMax = 130;
                    double tyLe =
                            Math.min(
                                    (double) wMax / img.getWidth(null),
                                    (double) hMax / img.getHeight(null));
                    int newW = Math.max(1, (int) (img.getWidth(null) * tyLe));
                    int newH = Math.max(1, (int) (img.getHeight(null) * tyLe));
                    Image scaled = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
                    lblHinhAnh.setIcon(new ImageIcon(scaled));
                    lblHinhAnh.setText("");
                    return;
                }
            } catch (Exception ignored) {
            }
        }

        // TrÆ°á»ng há»£p khÃ´ng cÃ³ áº£nh hoáº·c Ä‘Æ°á»ng dáº«n sai
        lblHinhAnh.setIcon(null);
        lblHinhAnh.setText(
                "<html><center><span style='color:#B42318;'>áº¢nh khÃ´ng tá»“n táº¡i</span><br>"
                        + "<small style='color:#627368;'>(Báº¥m Chá»n áº£nh...)</small></center></html>");
    }

    private void luu() throws Exception {
        SanPham sp = new SanPham();
        sp.setMaSanPham(maCu);
        kiemTraRong(tenSanPham.getText().trim(), "TÃªn sáº£n pháº©m");
        sp.setTenSanPham(tenSanPham.getText().trim());
        kiemTraRong(loaiHang.getText().trim(), "Loáº¡i hÃ ng");
        sp.setLoaiHang(loaiHang.getText().trim());
        kiemTraRong(thuongHieu.getText().trim(), "ThÆ°Æ¡ng hiá»‡u");
        sp.setThuongHieu(thuongHieu.getText().trim());
        sp.setDonGiaBan(InHoaDon.soTien(donGiaBan.getText().trim()));
        sp.setGiaNhap(InHoaDon.soTien(giaNhap.getText().trim()));
        sp.setThongSoKiThuat(thongSoKiThuat.getText().trim());
        kiemTraRong(xuatXu.getText().trim(), "Xuáº¥t xá»©");
        sp.setXuatXu(xuatXu.getText().trim());
        sp.setThoiHanBaoHanh(Integer.parseInt(thoiHanBaoHanh.getText().trim()));
        sp.setTrangThai((String) trangThai.getSelectedItem());

        // Xá»­ lÃ½ lÆ°u Ä‘Æ°á»ng dáº«n áº£nh
        String maLuu = (maCu != null) ? maCu : maSanPham.getText().trim();
        String duongDanLuu = duongDanAnhHienTai;

        if (fileAnhMoi != null && fileAnhMoi.isFile()) {
            File thuMucAnh = new File("resource/hinhanh");
            if (!thuMucAnh.exists()) thuMucAnh.mkdirs();

            String tenGoc = fileAnhMoi.getName();
            String ext = ".png";
            int idx = tenGoc.lastIndexOf('.');
            if (idx > 0) ext = tenGoc.substring(idx).toLowerCase(java.util.Locale.ROOT);

            String tenDich = (maLuu.isBlank() ? "SP_" + System.currentTimeMillis() : maLuu) + ext;
            File fileDich = new File(thuMucAnh, tenDich);
            Files.copy(
                    fileAnhMoi.toPath(),
                    fileDich.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            duongDanLuu = "resource/hinhanh/" + tenDich;
        }

        sp.setHinhAnh(duongDanLuu);

        if (maCu == null) {
            sanPhamDAO.insertSanPham(sp, tk);
            JOptionPane.showMessageDialog(this, "ÄÃ£ thÃªm sáº£n pháº©m " + sp.getMaSanPham() + ".");
        } else {
            sanPhamDAO.updateSanPham(sp, tk);
            JOptionPane.showMessageDialog(this, "ÄÃ£ cáº­p nháº­t sáº£n pháº©m " + sp.getMaSanPham() + ".");
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
            else if (o.equals(btnChonAnh)) chonAnh();
            else if (o.equals(btnXoaAnh)) boAnh();
            else if (o.equals(btnTim) || o.equals(txtTim)) loadDataToTable();
            else if (o.equals(btnTai)) {
                txtTim.setText("");
                giaTu.setText("");
                giaDen.setText("");
                locTT.setSelectedIndex(0);
                loadDataToTable();
            } else if (o.equals(btnXoa)) {
                if (maCu == null) throw new IllegalArgumentException("Chá»n dÃ²ng cáº§n xÃ³a.");
                if (xacNhan(this, "XÃ³a dá»¯ liá»‡u chÆ°a cÃ³ liÃªn káº¿t?")) {
                    sanPhamDAO.deleteSanPham(maCu, tk);
                    taoMoi();
                    loadDataToTable();
                }
            }
        } catch (java.sql.SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this, "Lá»—i SQL:\n" + ex.getMessage(), "Lá»—i SQL", JOptionPane.ERROR_MESSAGE);
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

    private void kiemTraRong(String s, String ten) {
        if (s == null || s.trim().isEmpty())
            throw new IllegalArgumentException("Vui lÃ²ng nháº­p " + ten);
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
    }
}