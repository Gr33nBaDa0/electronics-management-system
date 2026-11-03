package gui;

import connectDB.ConnectDB;

import dao.ChiTietHoaDon_DAO;
import dao.HoaDon_DAO;

import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.TaiKhoan;

import util.*;
import util.GiaoDien;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class HoaDon_GUI extends JPanel implements ActionListener {
    // MÃ u dÃ¹ng chung Ä‘Æ°á»£c khai bÃ¡o á»Ÿ Ä‘áº§u lá»›p.
    private static final Color PRIMARY = GiaoDien.PRIMARY,
            DARK = GiaoDien.DARK,
            BACKGROUND = GiaoDien.BACKGROUND,
            SURFACE = GiaoDien.SURFACE,
            TEXT = GiaoDien.TEXT,
            BORDER = GiaoDien.BORDER,
            ERROR = GiaoDien.ERROR;

    private final TaiKhoan tk;
    private final BanHang_GUI pos;
    private List<HoaDon> ds = new ArrayList<>();
    private final JTextField txtTim = new JTextField(16),
            tu = new JTextField(LocalDate.now().minusMonths(1).toString(), 10),
            den = new JTextField(LocalDate.now().toString(), 10);
    private final JComboBox<String>
            tt = new JComboBox<>(new String[] {"", "ChÆ°a thanh toÃ¡n", "ÄÃ£ thanh toÃ¡n", "ÄÃ£ há»§y"}),
            ht = new JComboBox<>(new String[] {"", "Tiá»n máº·t", "Chuyá»ƒn khoáº£n QR"});
    private final JButton btnTim = taoNut("TÃ¬m / lá»c"),
            btnXem = taoNut("Xem chi tiáº¿t"),
            btnIn = taoNut("In hÃ³a Ä‘Æ¡n"),
            btnMo = taoNut("Má»Ÿ Ä‘Æ¡n táº¡i POS"),
            btnHuy = taoNut("Há»§y Ä‘Æ¡n"),
            btnBo = taoNut("Bá» lá»c"),
            btnBH = taoNut("Tra báº£o hÃ nh");
    private final DefaultTableModel model =
            taoModel(
                    "MÃ£ hÃ³a Ä‘Æ¡n",
                    "NgÃ y láº­p",
                    "NhÃ¢n viÃªn",
                    "KhÃ¡ch",
                    "Tráº¡ng thÃ¡i",
                    "HÃ¬nh thá»©c",
                    "Äiá»ƒm dÃ¹ng");
    private final JTable table = taoBang(model);

    public HoaDon_GUI(TaiKhoan tk, BanHang_GUI pos) {
        this(tk, pos, "");
    }

    public HoaDon_GUI(TaiKhoan tk, BanHang_GUI pos, String sdt) {
        this.tk = tk;
        this.pos = pos;
        txtTim.setText(sdt);
        GiaoDien.boCuc(this);
        JPanel loc =
                GiaoDien.form(
                        3,
                        "MÃ£ hÃ³a Ä‘Æ¡n / SÄT",
                        txtTim,
                        "Tá»« ngÃ y (yyyy-MM-dd)",
                        tu,
                        "Äáº¿n ngÃ y (yyyy-MM-dd)",
                        den,
                        "Tráº¡ng thÃ¡i",
                        tt,
                        "HÃ¬nh thá»©c thanh toÃ¡n",
                        ht);
        tt.setRenderer(
                new javax.swing.DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(
                            javax.swing.JList<?> l, Object v, int i, boolean s, boolean f) {
                        return super.getListCellRendererComponent(
                                l,
                                v == null || v.toString().isEmpty() ? "Táº¥t cáº£ tráº¡ng thÃ¡i" : v,
                                i,
                                s,
                                f);
                    }
                });
        ht.setRenderer(
                new javax.swing.DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(
                            javax.swing.JList<?> l, Object v, int i, boolean s, boolean f) {
                        return super.getListCellRendererComponent(
                                l,
                                v == null || v.toString().isEmpty() ? "Táº¥t cáº£ hÃ¬nh thá»©c" : v,
                                i,
                                s,
                                f);
                    }
                });
        add(
                GiaoDien.doc(GiaoDien.the("Bá»™ lá»c hÃ³a Ä‘Æ¡n", loc, taoDong(btnTim, btnBo))),
                BorderLayout.NORTH);
        add(
                GiaoDien.the(
                        "Danh sÃ¡ch hÃ³a Ä‘Æ¡n",
                        GiaoDien.cuon(table),
                        taoDong(btnXem, btnIn, btnMo, btnHuy, btnBH)),
                BorderLayout.CENTER);
        GiaoDien.trangTri(this);
        for (JButton b : new JButton[] {btnTim, btnBo, btnXem, btnIn, btnMo, btnHuy, btnBH})
            b.addActionListener(this);
        btnMo.setEnabled(pos != null);
        ganPhimTat();
        loadDataToTable();
    }

    private void loadDataToTable() {
        model.setRowCount(0);
        try {
            if (ConnectDB.con == null || ConnectDB.con.isClosed()) {
                ConnectDB.getInstance().connect();
            }

            ds =
                    new HoaDon_DAO()
                            .timKiem(
                                    txtTim.getText().trim(),
                                    LocalDate.parse(tu.getText()),
                                    LocalDate.parse(den.getText()),
                                    (String) tt.getSelectedItem(),
                                    (String) ht.getSelectedItem());
            for (HoaDon h : ds)
                model.addRow(
                        new Object[] {
                            h.getMaHoaDon(),
                            h.getNgayLap(),
                            h.getNhanVien().getMaNhanVien(),
                            h.getKhachHang() == null
                                    ? "VÃ£ng lai"
                                    : h.getKhachHang().getMaKhachHang(),
                            h.getTrangThaiThanhToan(),
                            h.getHinhThucThanhToan(),
                            h.getDiemSuDung()
                        });
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
            if (o.equals(btnTim)) {
                loadDataToTable();
                return;
            }
            if (o.equals(btnBo)) {
                txtTim.setText("");
                tt.setSelectedIndex(0);
                ht.setSelectedIndex(0);
                tu.setText(LocalDate.now().minusMonths(1).toString());
                den.setText(LocalDate.now().toString());
                loadDataToTable();
                return;
            }
            int i = table.getSelectedRow();
            if (i < 0) throw new IllegalArgumentException("Chá»n hÃ³a Ä‘Æ¡n.");
            i = table.convertRowIndexToModel(i);
            HoaDon h = new HoaDon_DAO().tim(ds.get(i).getMaHoaDon());
            if (o.equals(btnIn)) {
                java.io.File file = InHoaDon.xuatFilePDF(h.getMaHoaDon());
                try {
                    if (!InHoaDon.moFilePDF(file))
                        JOptionPane.showMessageDialog(this, "ÄÃ£ xuáº¥t hÃ³a Ä‘Æ¡n: " + file.getAbsolutePath());
                } catch (Exception | LinkageError loiMo) {
                    JOptionPane.showMessageDialog(this, "ÄÃ£ xuáº¥t hÃ³a Ä‘Æ¡n: " + file.getAbsolutePath()
                            + "\nChÆ°a má»Ÿ Ä‘Æ°á»£c PDF tá»± Ä‘á»™ng. Báº¡n cÃ³ thá»ƒ má»Ÿ file Ä‘Ã£ lÆ°u trong á»• D.");
                }
            } else if (o.equals(btnBH)) {
                if (!"ÄÃ£ thanh toÃ¡n".equals(h.getTrangThaiThanhToan()))
                    throw new IllegalArgumentException("Chá»‰ tra hÃ³a Ä‘Æ¡n Ä‘Ã£ thanh toÃ¡n.");
                List<ChiTietHoaDon> ds = new ChiTietHoaDon_DAO().getTheoHoaDon(h.getMaHoaDon());
                ChiTietHoaDon d =
                        (ChiTietHoaDon)
                                JOptionPane.showInputDialog(
                                        this,
                                        "Chá»n sáº£n pháº©m Ä‘Ã£ mua:",
                                        "Tra báº£o hÃ nh",
                                        JOptionPane.PLAIN_MESSAGE,
                                        null,
                                        ds.toArray(),
                                        ds.isEmpty() ? null : ds.get(0));
                if (d != null) {
                    int thang = d.getSanPham().getThoiHanBaoHanh();
                    JOptionPane.showMessageDialog(
                            this,
                            thang == 0
                                    ? "Sáº£n pháº©m khÃ´ng Ã¡p dá»¥ng báº£o hÃ nh."
                                    : "NgÃ y láº­p hÃ³a Ä‘Æ¡n: "
                                            + h.getNgayLap().toLocalDate()
                                            + "\nThá»i háº¡n sáº£n pháº©m hiá»‡n táº¡i: "
                                            + thang
                                            + " thÃ¡ng\nHáº¡n tham kháº£o: "
                                            + h.getNgayLap().toLocalDate().plusMonths(thang)
                                            + "\n"
                                            + "Vui lÃ²ng Ä‘á»‘i chiáº¿u chá»©ng tá»« vÃ  Ä‘iá»u kiá»‡n báº£o hÃ nh"
                                            + " thá»±c táº¿.");
                }
            } else if (o.equals(btnHuy)) {
                if (xacNhan(this, "Há»§y hÃ³a Ä‘Æ¡n chÆ°a thanh toÃ¡n?")) {
                    new HoaDon_DAO().huy(h.getMaHoaDon(), tk);
                    loadDataToTable();
                }
            } else if (o.equals(btnMo)) {
                pos.moHoaDon(h.getMaHoaDon());
                JOptionPane.showMessageDialog(this, "ÄÃ£ náº¡p Ä‘Æ¡n. Chá»n menu BÃ¡n hÃ ng Ä‘á»ƒ tiáº¿p tá»¥c.");
            } else if (o.equals(btnXem)) {
                List<ChiTietHoaDon> dong = new ChiTietHoaDon_DAO().getTheoHoaDon(h.getMaHoaDon());
                JTextArea text = new JTextArea(InHoaDon.noiDung(h, dong), 25, 65);
                text.setEditable(false);
                JOptionPane.showMessageDialog(
                        this, new JScrollPane(text), "Chi tiáº¿t hÃ³a Ä‘Æ¡n", JOptionPane.PLAIN_MESSAGE);
            }
        } catch (java.sql.SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this, "Lá»—i SQL:\n" + ex.getMessage(), "Lá»—i SQL", JOptionPane.ERROR_MESSAGE);
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
        GiaoDien.phimTatChuan(this, null, null, btnHuy, btnTim, txtTim);
        GiaoDien.phimTat(this, "ctrl P", btnIn);
        GiaoDien.phimTat(this, "ctrl O", btnMo);
    }
}
