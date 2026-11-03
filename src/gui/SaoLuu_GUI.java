package gui;

import dao.SaoLuu_DAO;
import dao.TaiKhoan_DAO;

import entity.TaiKhoan;

import until.GiaoDien;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SaoLuu_GUI extends JPanel implements ActionListener {
    // Màu dùng chung được khai báo ở đầu lớp.
    private static final Color PRIMARY = GiaoDien.PRIMARY,
            DARK = GiaoDien.DARK,
            BACKGROUND = GiaoDien.BACKGROUND,
            SURFACE = GiaoDien.SURFACE,
            TEXT = GiaoDien.TEXT,
            BORDER = GiaoDien.BORDER,
            ERROR = GiaoDien.ERROR;

    private final TaiKhoan tk;
    private final JTextField
            path = new JTextField(docThietLap("backup.file", "D:\\GreenSmart_Backup.bak"), 45),
            bin = new JTextField(docThietLap("payment.bankBin", ""), 12),
            account = new JTextField(docThietLap("payment.account", ""), 18),
            name = new JTextField(docThietLap("payment.accountName", ""), 24),
            bank = new JTextField(docThietLap("payment.bankName", ""), 18),
            minutes = new JTextField(docThietLap("backup.minutes", "0"), 8);
    private final JButton btnBackup = taoNut("Sao lưu và kiểm tra"),
            btnVerify = taoNut("Kiểm tra bản .bak"),
            btnSave = taoNut("Lưu cấu hình");

    public SaoLuu_GUI(TaiKhoan tk) {
        this.tk = tk;
        GiaoDien.boCuc(this);

        JPanel saoLuu =
                GiaoDien.doc(
                        GiaoDien.form(
                                1,
                                "Đường dẫn .bak trên máy SQL Server",
                                path,
                                "Chu kỳ sao lưu (phút), 0 = tắt",
                                minutes),
                        GiaoDien.ghiChu(
                                "Sao lưu tự động chạy khi ứng dụng còn mở và quản lý đang đăng"
                                        + " nhập. Lưu thiết lập rồi đăng nhập lại để đổi lịch."));
        JPanel nganHang =
                GiaoDien.doc(
                        GiaoDien.form(
                                1,
                                "BIN ngân hàng (6 số)",
                                bin,
                                "Số tài khoản nhận tiền",
                                account,
                                "Tên chủ tài khoản",
                                name,
                                "Tên ngân hàng",
                                bank),
                        GiaoDien.ghiChu(
                                "Điền tài khoản thật của cửa hàng. Kiểm tra giao dịch ngân hàng"
                                        + " trước khi xác nhận thanh toán."));
        JPanel haiCot = new JPanel(new GridLayout(1, 2, 14, 0));
        haiCot.setOpaque(false);
        haiCot.add(
                GiaoDien.the(
                        "Sao lưu dữ liệu",
                        GiaoDien.cuonForm(saoLuu),
                        taoDong(btnBackup, btnVerify)));
        haiCot.add(GiaoDien.the("Thanh toán ngân hàng", GiaoDien.cuonForm(nganHang), null));
        add(haiCot, BorderLayout.CENTER);
        add(taoDong(btnSave), BorderLayout.SOUTH);
        GiaoDien.trangTri(this);
        btnSave.addActionListener(this);
        btnBackup.addActionListener(this);
        ganPhimTat();
        btnVerify.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            Object o = e.getSource();
            if (o.equals(btnSave)) {
                new TaiKhoan_DAO().kiemTraQuyen(tk, true);
                int n = Integer.parseInt(minutes.getText());
                if (n < 0 || n > 10080)
                    throw new IllegalArgumentException("Chu kỳ từ 0 đến 10080 phút.");
                Properties p = new Properties();
                p.setProperty("backup.file", path.getText().trim());
                p.setProperty("backup.minutes", String.valueOf(n));
                p.setProperty("payment.bankBin", bin.getText().trim());
                p.setProperty("payment.account", account.getText().trim());
                p.setProperty("payment.accountName", name.getText().trim());
                p.setProperty("payment.bankName", bank.getText().trim());
                java.util.Properties daLuu = new java.util.Properties();
                java.nio.file.Path file = java.nio.file.Paths.get("config.properties");
                java.nio.file.Path nguon =
                        java.nio.file.Files.exists(file)
                                ? file
                                : java.nio.file.Paths.get("config.example.properties");
                if (java.nio.file.Files.exists(nguon)) {
                    try (java.io.Reader reader = java.nio.file.Files.newBufferedReader(nguon)) {
                        daLuu.load(reader);
                    }
                }
                daLuu.putAll(p);
                try (java.io.Writer writer = java.nio.file.Files.newBufferedWriter(file)) {
                    daLuu.store(writer, "QR, han tra va sao luu");
                }
                JOptionPane.showMessageDialog(
                        this, "Đã lưu cấu hình riêng máy. Đăng nhập lại để áp dụng lịch backup.");
            } else if (o.equals(btnBackup)) {
                if (xacNhan(this, "Sao lưu database đang kết nối tới đường dẫn đã nhập?")) {
                    new SaoLuu_DAO().saoLuu(path.getText().trim(), tk);
                    JOptionPane.showMessageDialog(
                            this, "Sao lưu và RESTORE VERIFYONLY thành công.");
                }
            } else if (o.equals(btnVerify)) {
                new SaoLuu_DAO().kiemTra(path.getText().trim(), tk);
                JOptionPane.showMessageDialog(this, "SQL Server xác nhận bản backup đọc được.");
            }
        } catch (java.sql.SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this, "Lỗi SQL:\n" + ex.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            thongBaoLoi(this, ex);
        }
    }

    private JPanel taoDong(Component... cs) {
        return GiaoDien.dong(cs);
    }

    private void thongBaoLoi(Component c, Exception e) {
        JOptionPane.showMessageDialog(
                c, e.getMessage(), "Không thực hiện được", JOptionPane.ERROR_MESSAGE);
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

    private static String docThietLap(String key, String macDinh) {
        java.util.Properties p = new java.util.Properties();
        java.nio.file.Path file = java.nio.file.Paths.get("config.properties");
        if (!java.nio.file.Files.exists(file))
            file = java.nio.file.Paths.get("config.example.properties");
        if (!java.nio.file.Files.exists(file)) return macDinh;
        try (java.io.Reader r = java.nio.file.Files.newBufferedReader(file)) {
            p.load(r);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return macDinh;
        }
        String v = p.getProperty(key, macDinh);
        return v == null || v.trim().isEmpty() ? macDinh : v.trim();
    }

    private void ganPhimTat() {
        GiaoDien.phimTatChuan(this, null, btnSave, null, null, null);
        GiaoDien.phimTat(this, "F6", btnBackup);
        GiaoDien.phimTat(this, "F7", btnVerify);
    }
}
