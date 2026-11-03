package gui;

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
import java.math.BigDecimal;
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
    // Màu dùng chung được khai báo ở đầu lớp.
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
    private final JButton btnTai = taoNut("Lập báo cáo"), btnXuat = taoNut("Xuất bảng CSV");
    private final JLabel tong = new JLabel();
    private final DefaultTableModel
            model =
                    taoModel(
                            "Ngày ca",
                            "Hình thức",
                            "Số đơn thu",
                            "Tiền thu",
                            "Tiền hoàn",
                            "Thu sau hoàn"),
            modelSP =
                    taoModel(
                            "Mã SP",
                            "Sản phẩm",
                            "Tồn hiện tại",
                            "SL bán trong kỳ",
                            "SL trả trong kỳ",
                            "Cảnh báo tồn");
    private final JTable table = taoBang(model), tableSP = taoBang(modelSP);
    private final JTabbedPane tabs = new JTabbedPane();

    public BaoCao_GUI(TaiKhoan tk) {
        this.tk = tk;
        GiaoDien.boCuc(this);
        JPanel loc =
                GiaoDien.form(2, "Từ ngày ca (yyyy-MM-dd)", tu, "Đến ngày ca (yyyy-MM-dd)", den);
        JPanel boLoc = new JPanel(new BorderLayout(16, 0));
        boLoc.setOpaque(false);
        boLoc.add(loc, BorderLayout.CENTER);
        boLoc.add(taoDong(btnTai, btnXuat), BorderLayout.EAST);
        tong.setFont(GiaoDien.phong(Font.BOLD, 19));
        tong.setForeground(PRIMARY);
        tong.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        tong.setOpaque(true);
        tong.setBackground(GiaoDien.PRIMARY_SOFT);
        add(GiaoDien.doc(GiaoDien.the("Kỳ báo cáo", boLoc, tong)), BorderLayout.NORTH);

        tabs.addTab("Tiền thu và hoàn", GiaoDien.cuon(table));
        tabs.addTab("Sản phẩm và tồn", GiaoDien.cuon(tableSP));
        add(
                GiaoDien.the(
                        "Chi tiết báo cáo",
                        tabs,
                        GiaoDien.ghiChu(
                                "Tiền thu gồm VAT. Hoàn tiền theo ngày ca hoàn; tồn kho là số lượng"
                                        + " hiện tại.")),
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
            BigDecimal thu = BigDecimal.ZERO, hoan = BigDecimal.ZERO;
            List<Object[]> list = d.doanhThu(a, b, tk);
            for (Object[] r : list) {
                thu = thu.add((BigDecimal) r[3]);
                hoan = hoan.add((BigDecimal) r[4]);
                model.addRow(
                        new Object[] {
                            r[0],
                            r[1],
                            r[2],
                            formatGia((BigDecimal) r[3]),
                            formatGia((BigDecimal) r[4]),
                            formatGia((BigDecimal) r[5])
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
                            + " · Hoàn: "
                            + formatGia(hoan)
                            + " · Sau hoàn: "
                            + formatGia(thu.subtract(hoan))
                            + " VND");
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
        if (e.getSource().equals(btnTai)) {
            loadDataToTable();
            return;
        }
        try {
            JFileChooser f = new JFileChooser();
            f.setSelectedFile(new java.io.File("BaoCao.csv"));
            if (f.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
            if (f.getSelectedFile().exists() && !xacNhan(this, "Ghi đè file đã có?")) return;
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
            JOptionPane.showMessageDialog(this, "Đã xuất CSV.");
        } catch (Exception ex) {
            thongBaoLoi(this, ex);
        }
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

    private JLabel taoTieuDe(String t) {
        return GiaoDien.tieuDe(t);
    }

    private boolean xacNhan(Component c, String s) {
        return JOptionPane.showConfirmDialog(c, s, "Xác nhận", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }

    private void ganPhimTat() {
        GiaoDien.phimTatChuan(this, null, null, null, btnTai, null);
        GiaoDien.phimTat(this, "ctrl E", btnXuat);
    }
}
