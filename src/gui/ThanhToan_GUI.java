package gui;

import dao.ChiTietHoaDon_DAO;
import dao.HoaDon_DAO;

import entity.HoaDon;
import entity.TaiKhoan;

import util.*;
import util.GiaoDien;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ThanhToan_GUI extends JDialog implements ActionListener {
    // MÃ u dÃ¹ng chung Ä‘Æ°á»£c khai bÃ¡o á»Ÿ Ä‘áº§u lá»›p.
    private static final Color PRIMARY = GiaoDien.PRIMARY,
            DARK = GiaoDien.DARK,
            BACKGROUND = GiaoDien.BACKGROUND,
            SURFACE = GiaoDien.SURFACE,
            TEXT = GiaoDien.TEXT,
            BORDER = GiaoDien.BORDER,
            ERROR = GiaoDien.ERROR;

    private final String ma;
    private final TaiKhoan tk;
    private final double total;
    private boolean daThanhToan;
    private final JComboBox<String> hinhThuc =
            new JComboBox<>(new String[] {"Tiá»n máº·t", "Chuyá»ƒn khoáº£n QR"});
    private final JTextField tienNhan = new JTextField(16);
    private final JLabel lblTienThua = new JLabel("Nháº­p sá»‘ tiá»n khÃ¡ch Ä‘Æ°a.");
    private final JCheckBox daNhan =
            new JCheckBox("ÄÃ£ kiá»ƒm tra tiá»n chuyá»ƒn khoáº£n vÃ o tÃ i khoáº£n cá»­a hÃ ng");
    private final JButton btnQR = taoNut("Hiá»‡n QR"),
            btnTra = taoNut("Thanh toÃ¡n & xuáº¥t hÃ³a Ä‘Æ¡n"),
            btnDong = taoNut("Äá»ƒ sau");

    public ThanhToan_GUI(Frame chu, String ma, TaiKhoan tk) throws Exception {
        super(chu, "Thanh toÃ¡n " + ma, true);
        this.ma = ma;
        this.tk = tk;
        HoaDon h = new HoaDon_DAO().tim(ma);
        if (h == null || !"ChÆ°a thanh toÃ¡n".equals(h.getTrangThaiThanhToan()))
            throw new IllegalArgumentException(
                    "HÃ³a Ä‘Æ¡n khÃ´ng tá»“n táº¡i hoáº·c khÃ´ng cÃ²n chá» thanh toÃ¡n.");
        total =
                InHoaDon.phaiTra(
                        new ChiTietHoaDon_DAO().getTheoHoaDon(ma),
                        h.getDiemSuDung(),
                        h.getThueSuatVAT());
        JPanel p = new JPanel();
        GiaoDien.boCuc(p);
        JLabel soTien = new JLabel(formatGia(total) + " VND");
        soTien.setFont(GiaoDien.phong(Font.BOLD, 30));
        soTien.setForeground(PRIMARY);
        JPanel tongTien = GiaoDien.the("Tá»”NG Cáº¦N THANH TOÃN", soTien, null);
        tongTien.setBackground(GiaoDien.PRIMARY_SOFT);
        JPanel form =
                GiaoDien.form(
                        1, "HÃ¬nh thá»©c thanh toÃ¡n", hinhThuc, "Tiá»n khÃ¡ch Ä‘Æ°a (VND)", tienNhan);
        lblTienThua.setForeground(GiaoDien.MUTED);
        JPanel noiDung = GiaoDien.doc(form, lblTienThua, taoDong(btnQR), daNhan);
        p.add(tongTien, BorderLayout.NORTH);
        p.add(GiaoDien.the("ThÃ´ng tin thanh toÃ¡n", noiDung, null), BorderLayout.CENTER);
        p.add(taoDong(btnTra, btnDong), BorderLayout.SOUTH);
        setContentPane(p);
        GiaoDien.trangTri(p);
        tienNhan.setToolTipText("VÃ­ dá»¥: 1000000 hoáº·c 1,000,000");
        btnQR.addActionListener(this);
        btnTra.addActionListener(this);
        btnDong.addActionListener(this);
        hinhThuc.addActionListener(this);
        tienNhan.getDocument()
                .addDocumentListener(
                        new javax.swing.event.DocumentListener() {
                            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                                hienTienThua();
                            }

                            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                                hienTienThua();
                            }

                            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                                hienTienThua();
                            }
                        });
        capNhatHinhThuc();
        setSize(720, 640);
        setResizable(false);
        setLocationRelativeTo(chu);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getRootPane().setDefaultButton(btnTra);
        GiaoDien.phimTat(getRootPane(), "ESCAPE", btnDong);
        GiaoDien.phimTat(getRootPane(), "F2", btnQR);
    }

    public boolean daThanhToan() {
        return daThanhToan;
    }

    public void chonHinhThuc(String ten) {
        hinhThuc.setSelectedItem(ten);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        try {
            if (o.equals(btnDong)) dispose();
            else if (o.equals(hinhThuc)) capNhatHinhThuc();
            else if (o.equals(btnQR)) {
                if (hienThiDialogQR()) {
                    daNhan.setSelected(true);
                    btnTra.doClick();
                }
            } else if (o.equals(btnTra)) {
                String ht = (String) hinhThuc.getSelectedItem();
                double nhan =
                        "Tiá»n máº·t".equals(ht)
                                ? InHoaDon.soTien(tienNhan.getText())
                                : 0.0;
                btnTra.setEnabled(false);
                double thua = new HoaDon_DAO().thanhToan(ma, ht, nhan, daNhan.isSelected(), tk);
                daThanhToan = true;
                String thongBao = "Thanh toÃ¡n thÃ nh cÃ´ng. Tiá»n thá»«a: " + formatGia(thua) + " VND";
                java.io.File pdf = null;
                try {
                    pdf = InHoaDon.xuatFilePDF(ma);
                    if (!InHoaDon.moFilePDF(pdf))
                        thongBao += "\nPDF Ä‘Ã£ lÆ°u: " + pdf.getAbsolutePath();
                } catch (Exception | LinkageError loiIn) {
                    thongBao +=
                            pdf == null
                                    ? "\nChÆ°a xuáº¥t Ä‘Æ°á»£c PDF: "
                                            + loiIn.getMessage()
                                            + "\nBáº¡n cÃ³ thá»ƒ in láº¡i táº¡i mÃ n HÃ³a Ä‘Æ¡n."
                                    : "\nPDF Ä‘Ã£ lÆ°u: "
                                            + pdf.getAbsolutePath()
                                            + "\nChÆ°a má»Ÿ tá»± Ä‘á»™ng Ä‘Æ°á»£c file PDF.";
                }
                JOptionPane.showMessageDialog(this, thongBao);
                dispose();
            }
        } catch (Exception ex) {
            btnTra.setEnabled(true);
            thongBaoLoi(this, ex);
        }
    }

    /** CÃ¡ch bá»‘ trÃ­ QR vÃ  nÃºt xÃ¡c nháº­n nháº­n tiá»n theo file máº«u. */
    private boolean hienThiDialogQR() throws Exception {
        java.awt.image.BufferedImage img = InHoaDon.taoQR(ma, total);
        final boolean[] daNhanTien = {false};
        JDialog qrDialog = new JDialog(this, "QuÃ©t mÃ£ chuyá»ƒn khoáº£n", true);
        JPanel p = new JPanel();
        GiaoDien.boCuc(p);
        double tyLe = Math.min(360.0 / img.getWidth(), 420.0 / img.getHeight());
        JLabel anh =
                new JLabel(
                        new ImageIcon(
                                img.getScaledInstance(
                                        Math.max(1, (int) (img.getWidth() * tyLe)),
                                        Math.max(1, (int) (img.getHeight() * tyLe)),
                                        Image.SCALE_SMOOTH)));
        JLabel tieuDe =
                new JLabel("Má»i khÃ¡ch quÃ©t mÃ£ - " + formatGia(total) + " VND", JLabel.CENTER);
        JButton xacNhan = taoNut("XÃC NHáº¬N: TÃ”I ÄÃƒ NHáº¬N TIá»€N");
        JButton dong = taoNut("ÄÃ³ng / Ä‘á»ƒ sau");
        xacNhan.addActionListener(
                a -> {
                    daNhanTien[0] = true;
                    qrDialog.dispose();
                });
        dong.addActionListener(a -> qrDialog.dispose());
        p.add(GiaoDien.doc(tieuDe, new JLabel("HÃ³a Ä‘Æ¡n: " + ma)), BorderLayout.NORTH);
        p.add(anh, BorderLayout.CENTER);
        p.add(
                GiaoDien.doc(
                        GiaoDien.ghiChu(
                                "Kiá»ƒm tra giao dá»‹ch vÃ o tÃ i khoáº£n cá»­a hÃ ng trÆ°á»›c khi xÃ¡c nháº­n."),
                        taoDong(xacNhan, dong)),
                BorderLayout.SOUTH);
        GiaoDien.trangTri(p);
        qrDialog.setContentPane(p);
        qrDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        qrDialog.setSize(540, 660);
        qrDialog.setLocationRelativeTo(this);
        GiaoDien.phimTat(qrDialog.getRootPane(), "ESCAPE", dong);
        qrDialog.setVisible(true);
        return daNhanTien[0];
    }

    private void capNhatHinhThuc() {
        boolean tienMat = hinhThuc.getSelectedIndex() == 0;
        tienNhan.setEnabled(tienMat);
        btnQR.setEnabled(!tienMat);
        daNhan.setEnabled(!tienMat);
        daNhan.setSelected(false);
        if (tienMat) {
            hienTienThua();
            tienNhan.requestFocusInWindow();
        } else {
            lblTienThua.setForeground(GiaoDien.MUTED);
            lblTienThua.setText("XÃ¡c nháº­n sau khi Ä‘Ã£ nháº­n tiá»n vÃ o tÃ i khoáº£n cá»­a hÃ ng.");
        }
    }

    private void hienTienThua() {
        if (hinhThuc.getSelectedIndex() != 0) return;
        try {
            double thua = InHoaDon.tron(InHoaDon.soTien(tienNhan.getText()) - total);
            lblTienThua.setForeground(thua < 0 ? ERROR : PRIMARY);
            lblTienThua.setText(
                    (thua < 0 ? "CÃ²n thiáº¿u: " : "Tiá»n tráº£ láº¡i: ")
                            + formatGia(Math.abs(thua))
                            + " VND");
        } catch (NumberFormatException e) {
            lblTienThua.setForeground(GiaoDien.MUTED);
            lblTienThua.setText("Nháº­p sá»‘ tiá»n khÃ¡ch Ä‘Æ°a.");
        }
    }

    private String formatGia(double gia) {
        java.text.DecimalFormat df =
                new java.text.DecimalFormat(
                        "#,##0.##",
                        java.text.DecimalFormatSymbols.getInstance(java.util.Locale.US));
        return df.format(gia);
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
}
