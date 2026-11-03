package gui;

import connectDB.ConnectDB;

import dao.CaLamViec_DAO;
import dao.NhanVien_DAO;
import dao.PhanCongCa_DAO;

import entity.CaLamViec;
import entity.NhanVien;
import entity.PhanCongCa;
import entity.TaiKhoan;

import until.GiaoDien;
import until.InHoaDon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class PhanCongCa_GUI extends JPanel implements ActionListener {
    // Màu dùng chung được khai báo ở đầu lớp.
    private static final Color PRIMARY = GiaoDien.PRIMARY,
            DARK = GiaoDien.DARK,
            BACKGROUND = GiaoDien.BACKGROUND,
            SURFACE = GiaoDien.SURFACE,
            TEXT = GiaoDien.TEXT,
            BORDER = GiaoDien.BORDER,
            ERROR = GiaoDien.ERROR;

    private final TaiKhoan tk;
    private List<PhanCongCa> ds = new ArrayList<>();
    private final JComboBox<NhanVien> nv = new JComboBox<>();
    private final JTextField ngay = new JTextField(LocalDate.now().toString(), 8),
            ghiChu = new JTextField(24),
            txtTim = new JTextField(16);
    private final JComboBox<CaLamViec> ca = new JComboBox<>();
    private final JComboBox<String> loc =
            new JComboBox<>(new String[] {"Tất cả", "Chưa mở", "Đang mở", "Đã đóng"});
    private final JButton btnPhan = taoNut("Phân công"),
            btnXoa = taoNut("Xóa phân công"),
            btnMo = taoNut("Mở ca"),
            btnDong = taoNut("Đối soát / đóng ca"),
            btnTai = taoNut("Tải lại / lọc"),
            btnNgay = taoNut("Chọn ngày");
    private final DefaultTableModel model =
            taoModel(
                    "Mã phân công",
                    "Nhân viên",
                    "Ngày",
                    "Ca",
                    "Quỹ đầu",
                    "Thực đếm",
                    "Trạng thái",
                    "Mở lúc",
                    "Đóng lúc",
                    "Ghi chú");
    private final JTable table = taoBang(model);
    private List<PhanCongCa> hienThi = new ArrayList<>();

    public PhanCongCa_GUI(TaiKhoan tk) {
        this.tk = tk;
        GiaoDien.boCuc(this);
        nv.setEditable(false);
        nv.setMaximumRowCount(10);
        nv.setToolTipText("Chọn mã và họ tên nhân viên có trong SQL");
        nv.setRenderer(
                new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(
                            JList<?> list,
                            Object value,
                            int index,
                            boolean selected,
                            boolean focus) {
                        super.getListCellRendererComponent(list, value, index, selected, focus);
                        if (value instanceof NhanVien) {
                            NhanVien n = (NhanVien) value;
                            setText(n.getMaNhanVien() + " - " + n.getHoTen());
                        }
                        return this;
                    }
                });
        ngay.setEditable(false);
        ngay.setToolTipText("Bấm Chọn ngày để mở lịch");
        JPanel oNgay = new JPanel(new BorderLayout(6, 0));
        oNgay.setOpaque(false);
        oNgay.add(ngay, BorderLayout.CENTER);
        oNgay.add(btnNgay, BorderLayout.EAST);
        JPanel dau =
                GiaoDien.doc(
                        GiaoDien.the(
                                "Phân công nhân viên",
                                GiaoDien.form(
                                        4,
                                        "Mã nhân viên",
                                        nv,
                                        "Ngày (yyyy-MM-dd)",
                                        oNgay,
                                        "Ca làm việc",
                                        ca,
                                        "Ghi chú",
                                        ghiChu),
                                taoDong(btnPhan, btnXoa)));
        add(dau, BorderLayout.NORTH);
        JPanel danhSach = new JPanel(new BorderLayout(0, 12));
        danhSach.setOpaque(false);
        danhSach.add(taoDong(new JLabel("Mã NV / ngày:"), txtTim, loc, btnTai), BorderLayout.NORTH);
        danhSach.add(GiaoDien.cuon(table), BorderLayout.CENTER);
        JPanel thaoTac =
                GiaoDien.doc(
                        taoDong(btnMo, btnDong),
                        GiaoDien.ghiChu("Nhân viên chỉ mở và đóng ca được phân công cho mình."));
        add(GiaoDien.the("Lịch phân công", danhSach, thaoTac), BorderLayout.CENTER);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        GiaoDien.trangTri(this);
        for (JButton b : new JButton[] {btnPhan, btnXoa, btnMo, btnDong, btnTai, btnNgay})
            b.addActionListener(this);
        boolean admin = "ADMIN".equals(tk.getQuyen());
        btnPhan.setEnabled(admin);
        btnXoa.setEnabled(admin);
        nv.setEnabled(admin);
        ca.setEnabled(admin);
        btnNgay.setEnabled(admin);
        ganPhimTat();
        loadDataToTable();
    }

    private void loadDataToTable() {
        model.setRowCount(0);
        try {
            if (ConnectDB.con == null || ConnectDB.con.isClosed()) {
                ConnectDB.getInstance().connect();
            }

            NhanVien nvCu = (NhanVien) nv.getSelectedItem();
            CaLamViec caCu = (CaLamViec) ca.getSelectedItem();
            nv.removeAllItems();
            if ("ADMIN".equals(tk.getQuyen())) {
                List<NhanVien> nhanVien = new NhanVien_DAO().getAllNhanVien();
                for (NhanVien n : nhanVien) {
                    nv.addItem(n);
                    if (nvCu != null && nvCu.getMaNhanVien().equals(n.getMaNhanVien()))
                        nv.setSelectedItem(n);
                }
            } else {
                nv.addItem(tk.getNhanVien());
            }
            ca.removeAllItems();
            for (CaLamViec c : new CaLamViec_DAO().getAllCaLamViec()) {
                ca.addItem(c);
                if (caCu != null && caCu.getMaCa().equals(c.getMaCa())) ca.setSelectedItem(c);
            }
            btnPhan.setEnabled(
                    "ADMIN".equals(tk.getQuyen())
                            && nv.getItemCount() > 0
                            && ca.getItemCount() > 0);
            List<PhanCongCa> list = new PhanCongCa_DAO().getAllPhanCongCa(tk);
            ds = list;
            hienThi.clear();
            for (PhanCongCa a : list) {
                if (!(a.getNhanVien().getMaNhanVien() + " " + a.getNgay())
                        .contains(txtTim.getText().trim())) continue;
                if (loc.getSelectedIndex() > 0 && !a.getTrangThai().equals(loc.getSelectedItem()))
                    continue;
                hienThi.add(a);
                model.addRow(
                        new Object[] {
                            a.getMaPhanCong(),
                            a.getNhanVien().getMaNhanVien(),
                            a.getNgay(),
                            a.getCaLamViec().getMaCa(),
                            formatGia(a.getTienQuyDauCa()),
                            a.getGioDongCa() == null ? "" : formatGia(a.getTienMatThucDem()),
                            a.getTrangThai(),
                            a.getGioMoCa(),
                            a.getGioDongCa(),
                            a.getGhiChu()
                        });
            }
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

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        try {
            if (o.equals(btnNgay)) {
                chonNgay();
                return;
            }
            PhanCongCa_DAO dao = new PhanCongCa_DAO();
            if (o.equals(btnTai)) {
                loadDataToTable();
                return;
            }
            if (o.equals(btnPhan)) {
                if (nv.getSelectedItem() == null)
                    throw new IllegalArgumentException("Chưa có nhân viên trong SQL.");
                if (ca.getSelectedItem() == null)
                    throw new IllegalArgumentException("Chưa có ca trong SQL.");
                dao.phanCong(
                        ((NhanVien) nv.getSelectedItem()).getMaNhanVien(),
                        ((CaLamViec) ca.getSelectedItem()).getMaCa(),
                        LocalDate.parse(ngay.getText()),
                        ghiChu.getText(),
                        tk);
                loadDataToTable();
                return;
            }
            int i = table.getSelectedRow();
            if (i < 0) throw new IllegalArgumentException("Chọn phân công ca.");
            PhanCongCa a = hienThi.get(i);
            if (o.equals(btnXoa)) {
                if (xacNhan(this, "Xóa phân công chưa mở?")) dao.xoa(a.getMaPhanCong(), tk);
            } else if (o.equals(btnMo)) {
                String tien =
                        JOptionPane.showInputDialog(
                                this, "Tiền quỹ đầu ca (số không phân cách):", "1000000");
                if (tien != null) dao.moCa(a.getMaPhanCong(), InHoaDon.soTien(tien), tk);
            } else if (o.equals(btnDong)) {
                double lt = dao.tienLyThuyet(a.getMaPhanCong(), tk);
                String dem =
                        JOptionPane.showInputDialog(
                                this,
                                "Quỹ lý thuyết: " + formatGia(lt) + " VND. Nhập tiền thực đếm:");
                if (dem == null) return;
                double thuc = InHoaDon.soTien(dem);
                String lyDo = ghiChu.getText();
                if (InHoaDon.soSanhTien(thuc, lt) != 0) {
                    lyDo =
                            JOptionPane.showInputDialog(
                                    this,
                                    "Chênh lệch "
                                            + formatGia(InHoaDon.tron(thuc - lt))
                                            + ". Nhập giải trình:");
                    if (lyDo == null) return;
                }
                if (xacNhan(this, "Đóng ca? Sau khi đóng không được sửa."))
                    dao.dongCa(a.getMaPhanCong(), thuc, lyDo, tk);
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

    /** Lịch chọn ngày bằng Swing sẵn có, không cần thêm thư viện. */
    private void chonNgay() {
        LocalDate dangChon = LocalDate.parse(ngay.getText());
        YearMonth[] thang = {
            YearMonth.from(dangChon.isBefore(LocalDate.now()) ? LocalDate.now() : dangChon)
        };
        JDialog lich =
                new JDialog(
                        SwingUtilities.getWindowAncestor(this),
                        "Chọn ngày phân công",
                        Dialog.ModalityType.APPLICATION_MODAL);
        lich.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        JPanel noiDung = new JPanel(new BorderLayout(0, 12));
        noiDung.setBackground(SURFACE);
        noiDung.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JButton truoc = taoNut("<"),
                sau = taoNut(">"),
                homNay = taoNut("Hôm nay"),
                huy = taoNut("Hủy");
        truoc.setToolTipText("Tháng trước");
        sau.setToolTipText("Tháng sau");
        JLabel tieuDe = new JLabel("", SwingConstants.CENTER);
        JPanel dau = new JPanel(new BorderLayout(8, 0));
        dau.setOpaque(false);
        dau.add(truoc, BorderLayout.WEST);
        dau.add(tieuDe, BorderLayout.CENTER);
        dau.add(sau, BorderLayout.EAST);
        JPanel luoi = new JPanel(new GridLayout(7, 7, 4, 4));
        luoi.setOpaque(false);
        noiDung.add(dau, BorderLayout.NORTH);
        noiDung.add(luoi, BorderLayout.CENTER);
        noiDung.add(taoDong(homNay, huy), BorderLayout.SOUTH);
        Runnable veLai =
                () -> {
                    tieuDe.setText(
                            "Tháng " + thang[0].getMonthValue() + " / " + thang[0].getYear());
                    truoc.setEnabled(thang[0].isAfter(YearMonth.now()));
                    sau.setEnabled(thang[0].isBefore(YearMonth.of(9999, 12)));
                    veLich(luoi, thang[0], dangChon, lich);
                };
        truoc.addActionListener(
                e -> {
                    thang[0] = thang[0].minusMonths(1);
                    veLai.run();
                });
        sau.addActionListener(
                e -> {
                    thang[0] = thang[0].plusMonths(1);
                    veLai.run();
                });
        homNay.addActionListener(
                e -> {
                    ngay.setText(LocalDate.now().toString());
                    lich.dispose();
                });
        huy.addActionListener(e -> lich.dispose());
        GiaoDien.trangTri(noiDung);
        veLai.run();
        lich.setContentPane(noiDung);
        lich.setSize(new Dimension(420, 440));
        lich.setResizable(false);
        lich.setLocationRelativeTo(this);
        lich.getRootPane()
                .registerKeyboardAction(
                        e -> lich.dispose(),
                        javax.swing.KeyStroke.getKeyStroke("ESCAPE"),
                        javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
        lich.setVisible(true);
    }

    private void veLich(JPanel luoi, YearMonth thang, LocalDate dangChon, JDialog lich) {
        luoi.removeAll();
        for (String thu : new String[] {"T2", "T3", "T4", "T5", "T6", "T7", "CN"}) {
            JLabel nhan = new JLabel(thu, SwingConstants.CENTER);
            nhan.setForeground(DARK);
            luoi.add(nhan);
        }
        int batDau = thang.atDay(1).getDayOfWeek().getValue() - 1;
        for (int o = 0; o < 42; o++) {
            int soNgay = o - batDau + 1;
            if (soNgay < 1 || soNgay > thang.lengthOfMonth()) {
                luoi.add(new JLabel());
                continue;
            }
            LocalDate giaTri = thang.atDay(soNgay);
            JButton nut =
                    giaTri.equals(dangChon)
                            ? GiaoDien.nutChinh(String.valueOf(soNgay))
                            : taoNut(String.valueOf(soNgay));
            nut.setToolTipText(giaTri.toString());
            nut.setEnabled(!giaTri.isBefore(LocalDate.now()));
            nut.addActionListener(
                    e -> {
                        ngay.setText(giaTri.toString());
                        lich.dispose();
                    });
            luoi.add(nut);
        }
        luoi.revalidate();
        luoi.repaint();
    }

    public void chonPhanCong(String maCanChon) {
        txtTim.setText("");
        loc.setSelectedIndex(0);
        loadDataToTable();
        for (int i = 0; i < model.getRowCount(); i++)
            if (maCanChon.equals(model.getValueAt(i, 0))) {
                table.setRowSelectionInterval(i, i);
                table.scrollRectToVisible(table.getCellRect(i, 0, true));
                return;
            }
    }

    public void moCaDaChon() {
        if (table.getSelectedRow() >= 0) btnMo.doClick();
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

    private JLabel taoTieuDe(String t) {
        return GiaoDien.tieuDe(t);
    }

    private boolean xacNhan(Component c, String s) {
        return JOptionPane.showConfirmDialog(c, s, "Xác nhận", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }

    private void ganPhimTat() {
        GiaoDien.phimTatChuan(this, null, btnPhan, btnXoa, btnTai, txtTim);
        GiaoDien.phimTat(this, "F6", btnMo);
        GiaoDien.phimTat(this, "F7", btnDong);
    }
}
