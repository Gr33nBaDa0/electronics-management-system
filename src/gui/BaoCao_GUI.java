package gui;

import util.InHoaDon;

import connectDB.ConnectDB;

import dao.BaoCao_DAO;

import entity.TaiKhoan;

import util.GiaoDien;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class BaoCao_GUI extends JPanel implements ActionListener {
    // MÃ u dÃ¹ng chung Ä‘Æ°á»£c khai bÃ¡o á»Ÿ Ä‘áº§u lá»›p.
    private static final Color PRIMARY = GiaoDien.PRIMARY,
            DARK = GiaoDien.DARK,
            BACKGROUND = GiaoDien.BACKGROUND,
            SURFACE = GiaoDien.SURFACE,
            TEXT = GiaoDien.TEXT,
            BORDER = GiaoDien.BORDER,
            ERROR = GiaoDien.ERROR;

    private final TaiKhoan tk;
    private final JTextField tu = new JTextField(LocalDate.now().withDayOfMonth(1).toString(), 10),
            den = new JTextField(LocalDate.now().toString(), 10);
    private final JButton btnTai = taoNut("Láº­p bÃ¡o cÃ¡o"), btnXuat = taoNut("Xuáº¥t báº£ng CSV");
    private final JLabel tong = new JLabel();
    private final DefaultTableModel
            model =
                    taoModel(
                            "NgÃ y ca",
                            "HÃ¬nh thá»©c",
                            "Sá»‘ Ä‘Æ¡n thu",
                            "Tiá»n thu",
                            "Tiá»n hoÃ n",
                            "Thu sau hoÃ n"),
            modelSP =
                    taoModel(
                            "MÃ£ SP",
                            "Sáº£n pháº©m",
                            "Tá»“n hiá»‡n táº¡i",
                            "SL bÃ¡n trong ká»³",
                            "SL tráº£ trong ká»³",
                            "Cáº£nh bÃ¡o tá»“n");
    private final JTable table = taoBang(model), tableSP = taoBang(modelSP);
    private final JTabbedPane tabs = new JTabbedPane();

    public BaoCao_GUI(TaiKhoan tk) {
        this.tk = tk;
        GiaoDien.boCuc(this);
        JPanel loc =
                GiaoDien.form(2, "Tá»« ngÃ y ca (yyyy-MM-dd)", tu, "Äáº¿n ngÃ y ca (yyyy-MM-dd)", den);
        JPanel boLoc = new JPanel(new BorderLayout(16, 0));
        boLoc.setOpaque(false);
        boLoc.add(loc, BorderLayout.CENTER);
        boLoc.add(taoDong(btnTai, btnXuat), BorderLayout.EAST);
        tong.setFont(GiaoDien.phong(Font.BOLD, 19));
        tong.setForeground(PRIMARY);
        tong.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        tong.setOpaque(true);
        tong.setBackground(GiaoDien.PRIMARY_SOFT);
        add(GiaoDien.doc(GiaoDien.the("Ká»³ bÃ¡o cÃ¡o", boLoc, tong)), BorderLayout.NORTH);

        tabs.addTab("Tiá»n thu vÃ  hoÃ n", GiaoDien.cuon(table));
        tabs.addTab("Sáº£n pháº©m vÃ  tá»“n", GiaoDien.cuon(tableSP));
        add(
                GiaoDien.the(
                        "Chi tiáº¿t bÃ¡o cÃ¡o",
                        tabs,
                        GiaoDien.ghiChu(
                                "Tiá»n thu gá»“m VAT. HoÃ n tiá»n theo ngÃ y ca hoÃ n; tá»“n kho lÃ  sá»‘ lÆ°á»£ng"
                                        + " hiá»‡n táº¡i.")),
                BorderLayout.CENTER);
        GiaoDien.trangTri(this);
        btnTai.addActionListener(this);
        btnXuat.addActionListener(this);
        ganPhimTat();
        loadDataToTable();
    }

    private void loadDataToTable() {
        model.setRowCount(0);
        try {
            if (ConnectDB.con == null || ConnectDB.con.isClosed()) {
                ConnectDB.getInstance().connect();
            }

            BaoCao_DAO d = new BaoCao_DAO();
            LocalDate a = LocalDate.parse(tu.getText()), b = LocalDate.parse(den.getText());
            double thu = 0.0, hoan = 0.0;
            List<Object[]> list = d.doanhThu(a, b, tk);
            for (Object[] r : list) {
                thu = InHoaDon.tron(thu + ((Number) r[3]).doubleValue());
                hoan = InHoaDon.tron(hoan + ((Number) r[4]).doubleValue());
                model.addRow(
                        new Object[] {
                            r[0],
                            r[1],
                            r[2],
                            formatGia(((Number) r[3]).doubleValue()),
                            formatGia(((Number) r[4]).doubleValue()),
                            formatGia(((Number) r[5]).doubleValue())
                        });
            }
            modelSP.setRowCount(0);
            List<Object[]> listSanPham = d.sanPham(a, b, tk);
            for (Object[] r : listSanPham) {
                modelSP.addRow(r);
            }
            tong.setText(
                    "Thu: "
                            + formatGia(thu)
                            + " Â· HoÃ n: "
                            + formatGia(hoan)
                            + " Â· Sau hoÃ n: "
                            + formatGia(InHoaDon.tron(thu - hoan))
                            + " VND");
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
        if (e.getSource().equals(btnTai)) {
            loadDataToTable();
            return;
        }
        try {
            JFileChooser f = new JFileChooser();
            f.setSelectedFile(new java.io.File("BaoCao.csv"));
            if (f.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
            if (f.getSelectedFile().exists() && !xacNhan(this, "Ghi Ä‘Ã¨ file Ä‘Ã£ cÃ³?")) return;
            DefaultTableModel m = tabs.getSelectedIndex() == 0 ? model : modelSP;
            StringBuilder s = new StringBuilder("\ufeff");
            for (int r = -1; r < m.getRowCount(); r++) {
                for (int c = 0; c < m.getColumnCount(); c++) {
                    if (c > 0) s.append(',');
                    String v = String.valueOf(r < 0 ? m.getColumnName(c) : m.getValueAt(r, c));
                    s.append('"').append(v.replace("\"", "\"\"")).append('"');
                }
                s.append('\n');
            }
            Files.writeString(f.getSelectedFile().toPath(), s.toString(), StandardCharsets.UTF_8);
            JOptionPane.showMessageDialog(this, "ÄÃ£ xuáº¥t CSV.");
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
        GiaoDien.phimTatChuan(this, null, null, null, btnTai, null);
        GiaoDien.phimTat(this, "ctrl E", btnXuat);
    }
}
