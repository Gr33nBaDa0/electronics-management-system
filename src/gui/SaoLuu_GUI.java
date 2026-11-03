package gui;

import dao.SaoLuu_DAO;
import dao.TaiKhoan_DAO;

import entity.TaiKhoan;

import util.GiaoDien;

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
    // MÃ u dÃ¹ng chung Ä‘Æ°á»£c khai bÃ¡o á»Ÿ Ä‘áº§u lá»›p.
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
    private final JButton btnBackup = taoNut("Sao lÆ°u vÃ  kiá»ƒm tra"),
            btnVerify = taoNut("Kiá»ƒm tra báº£n .bak"),
            btnSave = taoNut("LÆ°u cáº¥u hÃ¬nh");

    public SaoLuu_GUI(TaiKhoan tk) {
        this.tk = tk;
        GiaoDien.boCuc(this);

        JPanel saoLuu =
                GiaoDien.doc(
                        GiaoDien.form(
                                1,
                                "ÄÆ°á»ng dáº«n .bak trÃªn mÃ¡y SQL Server",
                                path,
                                "Chu ká»³ sao lÆ°u (phÃºt), 0 = táº¯t",
                                minutes),
                        GiaoDien.ghiChu(
                                "Sao lÆ°u tá»± Ä‘á»™ng cháº¡y khi á»©ng dá»¥ng cÃ²n má»Ÿ vÃ  quáº£n lÃ½ Ä‘ang Ä‘Äƒng"
                                        + " nháº­p. LÆ°u thiáº¿t láº­p rá»“i Ä‘Äƒng nháº­p láº¡i Ä‘á»ƒ Ä‘á»•i lá»‹ch."));
        JPanel nganHang =
                GiaoDien.doc(
                        GiaoDien.form(
                                1,
                                "BIN ngÃ¢n hÃ ng (6 sá»‘)",
                                bin,
                                "Sá»‘ tÃ i khoáº£n nháº­n tiá»n",
                                account,
                                "TÃªn chá»§ tÃ i khoáº£n",
                                name,
                                "TÃªn ngÃ¢n hÃ ng",
                                bank),
                        GiaoDien.ghiChu(
                                "Äiá»n tÃ i khoáº£n tháº­t cá»§a cá»­a hÃ ng. Kiá»ƒm tra giao dá»‹ch ngÃ¢n hÃ ng"
                                        + " trÆ°á»›c khi xÃ¡c nháº­n thanh toÃ¡n."));
        JPanel haiCot = new JPanel(new GridLayout(1, 2, 14, 0));
        haiCot.setOpaque(false);
        haiCot.add(
                GiaoDien.the(
                        "Sao lÆ°u dá»¯ liá»‡u",
                        GiaoDien.cuonForm(saoLuu),
                        taoDong(btnBackup, btnVerify)));
        haiCot.add(GiaoDien.the("Thanh toÃ¡n ngÃ¢n hÃ ng", GiaoDien.cuonForm(nganHang), null));
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
                    throw new IllegalArgumentException("Chu ká»³ tá»« 0 Ä‘áº¿n 10080 phÃºt.");
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
                        this, "ÄÃ£ lÆ°u cáº¥u hÃ¬nh riÃªng mÃ¡y. ÄÄƒng nháº­p láº¡i Ä‘á»ƒ Ã¡p dá»¥ng lá»‹ch backup.");
            } else if (o.equals(btnBackup)) {
                if (xacNhan(this, "Sao lÆ°u database Ä‘ang káº¿t ná»‘i tá»›i Ä‘Æ°á»ng dáº«n Ä‘Ã£ nháº­p?")) {
                    new SaoLuu_DAO().saoLuu(path.getText().trim(), tk);
                    JOptionPane.showMessageDialog(
                            this, "Sao lÆ°u vÃ  RESTORE VERIFYONLY thÃ nh cÃ´ng.");
                }
            } else if (o.equals(btnVerify)) {
                new SaoLuu_DAO().kiemTra(path.getText().trim(), tk);
                JOptionPane.showMessageDialog(this, "SQL Server xÃ¡c nháº­n báº£n backup Ä‘á»c Ä‘Æ°á»£c.");
            }
        } catch (java.sql.SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this, "Lá»—i SQL:\n" + ex.getMessage(), "Lá»—i SQL", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            thongBaoLoi(this, ex);
        }
    }

    private JPanel taoDong(Component... cs) {
        return GiaoDien.dong(cs);
    }

    private void thongBaoLoi(Component c, Exception e) {
        JOptionPane.showMessageDialog(
                c, e.getMessage(), "KhÃ´ng thá»±c hiá»‡n Ä‘Æ°á»£c", JOptionPane.ERROR_MESSAGE);
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
