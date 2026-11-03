package gui;

import connectDB.ConnectDB;

import dao.CaLamViec_DAO;

import entity.CaLamViec;
import entity.TaiKhoan;

import until.GiaoDien;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class CaLamViec_GUI extends JPanel implements ActionListener {
    // Màu dùng chung được khai báo ở đầu lớp.
    private static final Color PRIMARY = GiaoDien.PRIMARY,
            DARK = GiaoDien.DARK,
            BACKGROUND = GiaoDien.BACKGROUND,
            SURFACE = GiaoDien.SURFACE,
            TEXT = GiaoDien.TEXT,
            BORDER = GiaoDien.BORDER,
            ERROR = GiaoDien.ERROR;

    private final TaiKhoan tk;
    private boolean them = true;
    private List<CaLamViec> ds = new ArrayList<>();
    private final JTextField ma = new JTextField(8),
            ten = new JTextField(15),
            batDau = new JTextField("08:00", 8),
            ketThuc = new JTextField("12:00", 8);
    private final JButton btnMoi = taoNut("Tạo mới"),
            btnLuu = taoNut("Lưu"),
            btnXoa = taoNut("Xóa ca"),
            btnTai = taoNut("Tải lại");
    private final DefaultTableModel model = taoModel("Mã ca", "Tên ca", "Bắt đầu", "Kết thúc");
    private final JTable table = taoBang(model);

    public CaLamViec_GUI(TaiKhoan tk) {
        this.tk = tk;
        GiaoDien.boCuc(this);

        JPanel form =
                GiaoDien.form(
                        2,
                        "Mã ca (CA01)",
                        ma,
                        "Tên ca",
                        ten,
                        "Bắt đầu (HH:mm)",
                        batDau,
                        "Kết thúc (HH:mm)",
                        ketThuc);
        JPanel trai =
                GiaoDien.the(
                        "Thông tin ca",
                        GiaoDien.cuonForm(form),
                        taoDong(btnMoi, btnLuu, btnXoa, btnTai));
        JPanel phai =
                GiaoDien.the(
                        "Các ca làm việc",
                        GiaoDien.cuon(table),
                        GiaoDien.ghiChu(
                                "Ca đã có phân công giữ nguyên giờ. Tạo ca mới khi cần đổi khung"
                                        + " giờ."));
        trai.setMinimumSize(new Dimension(340, 180));
        phai.setMinimumSize(new Dimension(400, 180));
        add(
                GiaoDien.tach(javax.swing.JSplitPane.HORIZONTAL_SPLIT, trai, phai, .40),
                BorderLayout.CENTER);
        GiaoDien.trangTri(this);
        for (JButton b : new JButton[] {btnMoi, btnLuu, btnXoa, btnTai}) b.addActionListener(this);
        table.getSelectionModel()
                .addListSelectionListener(
                        e -> {
                            int i = table.getSelectedRow();
                            if (i >= 0 && i < ds.size()) {
                                CaLamViec c = ds.get(i);
                                ma.setText(c.getMaCa());
                                ma.setEditable(false);
                                ten.setText(c.getTenCa());
                                batDau.setText(c.getGioBatDau().toString());
                                ketThuc.setText(c.getGioKetThuc().toString());
                                them = false;
                            }
                        });
        ganPhimTat();
        loadDataToTable();
    }

    private void loadDataToTable() {
        model.setRowCount(0);
        try {
            if (ConnectDB.con == null || ConnectDB.con.isClosed()) {
                ConnectDB.getInstance().connect();
            }

            List<CaLamViec> list = new CaLamViec_DAO().getAllCaLamViec();
            ds = list;
            for (CaLamViec c : list)
                model.addRow(
                        new Object[] {
                            c.getMaCa(), c.getTenCa(), c.getGioBatDau(), c.getGioKetThuc()
                        });
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
            if (o.equals(btnMoi)) {
                them = true;
                table.clearSelection();
                ma.setEditable(true);
                ma.setText("");
                ten.setText("");
            } else if (o.equals(btnTai)) loadDataToTable();
            else if (o.equals(btnLuu)) {
                if (!ma.getText().matches("CA\\d{2}"))
                    throw new IllegalArgumentException("Mã ca ví dụ CA01.");
                kiemTraDoDai(ten.getText(), 20, "tên ca");
                CaLamViec c =
                        new CaLamViec(
                                ma.getText(),
                                ten.getText().trim(),
                                LocalTime.parse(batDau.getText().trim()),
                                LocalTime.parse(ketThuc.getText().trim()));
                if (them) {
                    new CaLamViec_DAO().insertCaLamViec(c, tk);
                } else {
                    new CaLamViec_DAO().updateCaLamViec(c, tk);
                }
                loadDataToTable();
            } else if (o.equals(btnXoa)) {
                if (them) throw new IllegalArgumentException("Chọn ca.");
                if (xacNhan(this, "Xóa ca chưa có phân công?")) {
                    new CaLamViec_DAO().deleteCaLamViec(ma.getText(), tk);
                    loadDataToTable();
                }
            }
        } catch (java.sql.SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this, "Lỗi SQL:\n" + ex.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
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
                c, e.getMessage(), "Không thực hiện được", JOptionPane.ERROR_MESSAGE);
    }

    private void kiemTraDoDai(String s, int n, String ten) {
        kiemTraRong(s, ten);
        if (s.length() > n) throw new IllegalArgumentException(ten + " tối đa " + n + " ký tự.");
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
            throw new IllegalArgumentException("Vui lòng nhập " + ten);
    }

    private JLabel taoTieuDe(String t) {
        return GiaoDien.tieuDe(t);
    }

    private boolean xacNhan(Component c, String s) {
        return JOptionPane.showConfirmDialog(c, s, "Xác nhận", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }

    private void ganPhimTat() {
        GiaoDien.phimTatChuan(this, btnMoi, btnLuu, btnXoa, btnTai, null);
    }
}
