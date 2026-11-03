package gui;

import connectDB.ConnectDB;

import dao.CaLamViec_DAO;
import dao.NhanVien_DAO;
import dao.PhanCongCa_DAO;

import entity.CaLamViec;
import entity.NhanVien;
import entity.PhanCongCa;
import entity.TaiKhoan;

import util.GiaoDien;
import util.InHoaDon;

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
    // MÃ u dÃ¹ng chung Ä‘Æ°á»£c khai bÃ¡o á»Ÿ Ä‘áº§u lá»›p.
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
            new JComboBox<>(new String[] {"Táº¥t cáº£", "ChÆ°a má»Ÿ", "Äang má»Ÿ", "ÄÃ£ Ä‘Ã³ng"});
    private final JButton btnPhan = taoNut("PhÃ¢n cÃ´ng"),
            btnXoa = taoNut("XÃ³a phÃ¢n cÃ´ng"),
            btnMo = taoNut("Má»Ÿ ca"),
            btnDong = taoNut("Äá»‘i soÃ¡t / Ä‘Ã³ng ca"),
            btnTai = taoNut("Táº£i láº¡i / lá»c"),
            btnNgay = taoNut("Chá»n ngÃ y");
    private final DefaultTableModel model =
            taoModel(
                    "MÃ£ phÃ¢n cÃ´ng",
                    "NhÃ¢n viÃªn",
                    "NgÃ y",
                    "Ca",
                    "Quá»¹ Ä‘áº§u",
                    "Thá»±c Ä‘áº¿m",
                    "Tráº¡ng thÃ¡i",
                    "Má»Ÿ lÃºc",
                    "ÄÃ³ng lÃºc",
                    "Ghi chÃº");
    private final JTable table = taoBang(model);
    private List<PhanCongCa> hienThi = new ArrayList<>();

    public PhanCongCa_GUI(TaiKhoan tk) {
        this.tk = tk;
        GiaoDien.boCuc(this);
        nv.setEditable(false);
        nv.setMaximumRowCount(10);
        nv.setToolTipText("Chá»n mÃ£ vÃ  há» tÃªn nhÃ¢n viÃªn cÃ³ trong SQL");
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
        ngay.setToolTipText("Báº¥m Chá»n ngÃ y Ä‘á»ƒ má»Ÿ lá»‹ch");
        JPanel oNgay = new JPanel(new BorderLayout(6, 0));
        oNgay.setOpaque(false);
        oNgay.add(ngay, BorderLayout.CENTER);
        oNgay.add(btnNgay, BorderLayout.EAST);
        JPanel dau =
                GiaoDien.doc(
                        GiaoDien.the(
                                "PhÃ¢n cÃ´ng nhÃ¢n viÃªn",
                                GiaoDien.form(
                                        4,
                                        "MÃ£ nhÃ¢n viÃªn",
                                        nv,
                                        "NgÃ y (yyyy-MM-dd)",
                                        oNgay,
                                        "Ca lÃ m viá»‡c",
                                        ca,
                                        "Ghi chÃº",
                                        ghiChu),
                                taoDong(btnPhan, btnXoa)));
        add(dau, BorderLayout.NORTH);
        JPanel danhSach = new JPanel(new BorderLayout(0, 12));
        danhSach.setOpaque(false);
        danhSach.add(taoDong(new JLabel("MÃ£ NV / ngÃ y:"), txtTim, loc, btnTai), BorderLayout.NORTH);
        danhSach.add(GiaoDien.cuon(table), BorderLayout.CENTER);
        JPanel thaoTac =
                GiaoDien.doc(
                        taoDong(btnMo, btnDong),
                        GiaoDien.ghiChu("NhÃ¢n viÃªn chá»‰ má»Ÿ vÃ  Ä‘Ã³ng ca Ä‘Æ°á»£c phÃ¢n cÃ´ng cho mÃ¬nh."));
        add(GiaoDien.the("Lá»‹ch phÃ¢n cÃ´ng", danhSach, thaoTac), BorderLayout.CENTER);
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
                    "Lá»—i táº£i dá»¯ liá»‡u SQL:\n" + e.getMessage(),
                    "Lá»—i SQL",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    this, e.getMessage(), "ThÃ´ng bÃ¡o", JOptionPane.WARNING_MESSAGE);
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
                    throw new IllegalArgumentException("ChÆ°a cÃ³ nhÃ¢n viÃªn trong SQL.");
                if (ca.getSelectedItem() == null)
                    throw new IllegalArgumentException("ChÆ°a cÃ³ ca trong SQL.");
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
            if (i < 0) throw new IllegalArgumentException("Chá»n phÃ¢n cÃ´ng ca.");
            PhanCongCa a = hienThi.get(i);
            if (o.equals(btnXoa)) {
                if (xacNhan(this, "XÃ³a phÃ¢n cÃ´ng chÆ°a má»Ÿ?")) dao.xoa(a.getMaPhanCong(), tk);
            } else if (o.equals(btnMo)) {
                String tien =
                        JOptionPane.showInputDialog(
                                this, "Tiá»n quá»¹ Ä‘áº§u ca (sá»‘ khÃ´ng phÃ¢n cÃ¡ch):", "1000000");
                if (tien != null) dao.moCa(a.getMaPhanCong(), InHoaDon.soTien(tien), tk);
            } else if (o.equals(btnDong)) {
                double lt = dao.tienLyThuyet(a.getMaPhanCong(), tk);
                String dem =
                        JOptionPane.showInputDialog(
                                this,
                                "Quá»¹ lÃ½ thuyáº¿t: " + formatGia(lt) + " VND. Nháº­p tiá»n thá»±c Ä‘áº¿m:");
                if (dem == null) return;
                double thuc = InHoaDon.soTien(dem);
                String lyDo = ghiChu.getText();
                if (InHoaDon.soSanhTien(thuc, lt) != 0) {
                    lyDo =
                            JOptionPane.showInputDialog(
                                    this,
                                    "ChÃªnh lá»‡ch "
                                            + formatGia(InHoaDon.tron(thuc - lt))
                                            + ". Nháº­p giáº£i trÃ¬nh:");
                    if (lyDo == null) return;
                }
                if (xacNhan(this, "ÄÃ³ng ca? Sau khi Ä‘Ã³ng khÃ´ng Ä‘Æ°á»£c sá»­a."))
                    dao.dongCa(a.getMaPhanCong(), thuc, lyDo, tk);
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

    /** Lá»‹ch chá»n ngÃ y báº±ng Swing sáºµn cÃ³, khÃ´ng cáº§n thÃªm thÆ° viá»‡n. */
    private void chonNgay() {
        LocalDate dangChon = LocalDate.parse(ngay.getText());
        YearMonth[] thang = {
            YearMonth.from(dangChon.isBefore(LocalDate.now()) ? LocalDate.now() : dangChon)
        };
        JDialog lich =
                new JDialog(
                        SwingUtilities.getWindowAncestor(this),
                        "Chá»n ngÃ y phÃ¢n cÃ´ng",
                        Dialog.ModalityType.APPLICATION_MODAL);
        lich.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        JPanel noiDung = new JPanel(new BorderLayout(0, 12));
        noiDung.setBackground(SURFACE);
        noiDung.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JButton truoc = taoNut("<"),
                sau = taoNut(">"),
                homNay = taoNut("HÃ´m nay"),
                huy = taoNut("Há»§y");
        truoc.setToolTipText("ThÃ¡ng trÆ°á»›c");
        sau.setToolTipText("ThÃ¡ng sau");
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
                            "ThÃ¡ng " + thang[0].getMonthValue() + " / " + thang[0].getYear());
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
        GiaoDien.phimTatChuan(this, null, btnPhan, btnXoa, btnTai, txtTim);
        GiaoDien.phimTat(this, "F6", btnMo);
        GiaoDien.phimTat(this, "F7", btnDong);
    }
}
