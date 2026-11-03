package gui;

import connectDB.ConnectDB;

import dao.Dashboard_DAO;

import entity.TaiKhoan;

import util.GiaoDien;
import util.InHoaDon;

import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DashboardNhanVien_GUI extends JPanel implements ActionListener {
    private static final Color PRIMARY = GiaoDien.PRIMARY,
            MUTED = GiaoDien.MUTED,
            ERROR = GiaoDien.ERROR;
    private static final DateTimeFormatter NGAY = DateTimeFormatter.ofPattern("dd/MM"),
            GIO = DateTimeFormatter.ofPattern("HH:mm");
    private final TaiKhoan tk;
    private final MainFrame main;
    private final JLabel thu = new JLabel("â€”"),
            don = new JLabel("â€”"),
            cho = new JLabel("â€”"),
            tra = new JLabel("â€”"),
            lblViec = GiaoDien.nhan("Äang táº£i ca lÃ m viá»‡câ€¦", 18, true, PRIMARY),
            lblGoiY = GiaoDien.nhan(" ", 12, false, MUTED),
            lblCapNhat = GiaoDien.nhan(" ", 12, false, MUTED),
            lblLoi = GiaoDien.nhan(" ", 12, false, ERROR),
            lblTrong = GiaoDien.nhan(" ", 12, false, MUTED);
    private final JButton btnTai = GiaoDien.nut("Táº£i láº¡i Â· F5"),
            btnBatDau = GiaoDien.nutChinh("Xem ca"),
            btnXuLy = GiaoDien.nut("Tiáº¿p tá»¥c xá»­ lÃ½"),
            btnXemCa = GiaoDien.nut("Xem ca Ä‘Ã£ chá»n"),
            btnSP = GiaoDien.nut("Xem sáº£n pháº©m");
    private final DefaultTableModel
            model = GiaoDien.model("Loáº¡i viá»‡c", "MÃ£", "Tráº¡ng thÃ¡i", "GiÃ¡ trá»‹ dá»± kiáº¿n"),
            modelCa = GiaoDien.model("NgÃ y", "Ca", "Giá»", "Tráº¡ng thÃ¡i"),
            modelSP = GiaoDien.model("MÃ£ SP", "Sáº£n pháº©m", "Tá»“n", "ChÃº Ã½");
    private final JTable table = GiaoDien.bang(model),
            tableCa = GiaoDien.bang(modelCa),
            tableSP = GiaoDien.bang(modelSP);
    private List<Object[]> congViec = new ArrayList<>(), lich = new ArrayList<>();
    private String caMo;
    private boolean dangMo;

    public DashboardNhanVien_GUI(TaiKhoan tk, MainFrame main) {
        this.tk = tk;
        this.main = main;
        GiaoDien.boCuc(this);
        JPanel thongTin = new JPanel(new BorderLayout());
        thongTin.setOpaque(false);
        thongTin.add(lblCapNhat, BorderLayout.CENTER);
        thongTin.add(btnTai, BorderLayout.EAST);
        JPanel chiSo = new JPanel(new GridLayout(1, 4, 10, 0));
        chiSo.setOpaque(false);
        chiSo.add(GiaoDien.soLieu("Tiá»n thu hÃ´m nay", thu, "Giao dá»‹ch cá»§a báº¡n theo ngÃ y ca"));
        chiSo.add(GiaoDien.soLieu("HÃ³a Ä‘Æ¡n Ä‘Ã£ thanh toÃ¡n", don, "Trong ngÃ y ca hÃ´m nay"));
        chiSo.add(GiaoDien.soLieu("ÄÆ¡n chÆ°a thanh toÃ¡n", cho, "ÄÆ¡n do báº¡n láº­p, cÃ²n Ä‘ang xá»­ lÃ½"));
        chiSo.add(GiaoDien.soLieu("Äá»•i tráº£ chá» hoÃ n", tra, "Phiáº¿u cá»§a báº¡n Ä‘Ã£ Ä‘Æ°á»£c duyá»‡t"));
        JPanel viec = new JPanel(new BorderLayout(12, 0));
        viec.setOpaque(false);
        viec.add(GiaoDien.doc(lblViec, lblGoiY), BorderLayout.CENTER);
        viec.add(btnBatDau, BorderLayout.EAST);
        JPanel uuTien = GiaoDien.the("VIá»†C Äáº¦U TIÃŠN", viec, null);
        uuTien.setBackground(GiaoDien.PRIMARY_SOFT);
        JPanel dau = GiaoDien.doc(thongTin, chiSo, uuTien, lblLoi);
        add(dau, BorderLayout.NORTH);
        JPanel danhSach =
                GiaoDien.the(
                        "CÃ´ng viá»‡c Ä‘ang chá»",
                        GiaoDien.cuon(table),
                        GiaoDien.doc(lblTrong, GiaoDien.dong(btnXuLy)));
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab(
                "Lá»‹ch cÃ¡ nhÃ¢n Â· 7 ngÃ y",
                GiaoDien.the(null, GiaoDien.cuon(tableCa), GiaoDien.dong(btnXemCa)));
        tabs.addTab(
                "Sáº£n pháº©m tá»“n tháº¥p",
                GiaoDien.the(null, GiaoDien.cuon(tableSP), GiaoDien.dong(btnSP)));
        danhSach.setMinimumSize(new Dimension(380, 180));
        tabs.setMinimumSize(new Dimension(320, 180));
        add(GiaoDien.tach(JSplitPane.HORIZONTAL_SPLIT, danhSach, tabs, .55), BorderLayout.CENTER);
        for (JButton b : new JButton[] {btnTai, btnBatDau, btnXuLy, btnXemCa, btnSP})
            b.addActionListener(this);
        GiaoDien.phimTat(this, "F5", btnTai);
        GiaoDien.trangTri(this);
        taiDuLieu();
    }

    public void taiDuLieu() {
        try {
            if (ConnectDB.con == null || ConnectDB.con.isClosed())
                ConnectDB.getInstance().connect();
            Dashboard_DAO dao = new Dashboard_DAO();
            Object[] a = dao.tongHopNgay(tk, LocalDate.now(), true);
            List<Object[]> viec = dao.congViecCuaToi(tk),
                    ca = dao.lichCuaToi(tk),
                    sp = dao.sanPhamCanXem(tk);
            thu.setText(InHoaDon.tien(((Number) a[0]).doubleValue()) + " Ä‘");
            don.setText(a[2].toString());
            cho.setText(a[3].toString());
            tra.setText(a[6].toString());
            congViec = viec;
            lich = ca;
            model.setRowCount(0);
            modelCa.setRowCount(0);
            modelSP.setRowCount(0);
            for (Object[] r : viec)
                model.addRow(
                        new Object[] {r[0], r[1], r[3], InHoaDon.tien(((Number) r[4]).doubleValue()) + " Ä‘"});
            for (Object[] r : ca)
                modelCa.addRow(
                        new Object[] {
                            ((LocalDate) r[1]).format(NGAY),
                            r[2],
                            ((LocalTime) r[3]).format(GIO) + "â€“" + ((LocalTime) r[4]).format(GIO),
                            r[5]
                        });
            for (Object[] r : sp) modelSP.addRow(r);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            int[] rong = {80, 174, 145, 130};
            for (int i = 0; i < rong.length; i++) {
                table.getColumnModel().getColumn(i).setMinWidth(rong[i]);
                table.getColumnModel().getColumn(i).setPreferredWidth(rong[i]);
            }
            lblTrong.setText(
                    viec.isEmpty()
                            ? "KhÃ´ng cÃ³ Ä‘Æ¡n nhÃ¡p hoáº·c phiáº¿u cá»§a báº¡n Ä‘ang chá» hoÃ n."
                            : viec.size() + " cÃ´ng viá»‡c cáº§n kiá»ƒm tra.");
            Object[] mo = null, choMo = null;
            boolean coCa = false;
            for (Object[] r : ca)
                if (LocalDate.now().equals(r[1])) {
                    coCa = true;
                    if ("Äang má»Ÿ".equals(r[5])) mo = r;
                    if (choMo == null && "ChÆ°a má»Ÿ".equals(r[5])) choMo = r;
                }
            dangMo = mo != null;
            caMo = choMo == null ? null : choMo[0].toString();
            if (mo != null) {
                lblViec.setText(
                        "Báº¡n Ä‘ang lÃ m "
                                + mo[2]
                                + " Â· "
                                + ((LocalTime) mo[3]).format(GIO)
                                + "â€“"
                                + ((LocalTime) mo[4]).format(GIO));
                lblGoiY.setText(
                        "Quá»¹ Ä‘áº§u ca: "
                                + InHoaDon.tien(((Number) mo[6]).doubleValue())
                                + " Ä‘. CÃ³ thá»ƒ báº¯t Ä‘áº§u bÃ¡n hÃ ng.");
                btnBatDau.setText("BÃ¡n hÃ ng");
            } else if (choMo != null) {
                lblViec.setText("Má»Ÿ " + choMo[2] + " Ä‘á»ƒ báº¯t Ä‘áº§u lÃ m viá»‡c");
                lblGoiY.setText(
                        "ÄÃ£ Ä‘Æ°á»£c phÃ¢n cÃ´ng hÃ´m nay Â· "
                                + ((LocalTime) choMo[3]).format(GIO)
                                + "â€“"
                                + ((LocalTime) choMo[4]).format(GIO));
                btnBatDau.setText("Má»Ÿ ca");
            } else {
                lblViec.setText(
                        coCa
                                ? "CÃ¡c ca hÃ´m nay cá»§a báº¡n Ä‘Ã£ káº¿t thÃºc"
                                : "Báº¡n chÆ°a Ä‘Æ°á»£c phÃ¢n cÃ´ng ca hÃ´m nay");
                lblGoiY.setText(
                        coCa
                                ? "Kiá»ƒm tra lá»‹ch sáº¯p tá»›i vÃ  cÃ¡c cÃ´ng viá»‡c cÃ²n chá»."
                                : "LiÃªn há»‡ quáº£n lÃ½ Ä‘á»ƒ Ä‘Æ°á»£c phÃ¢n cÃ´ng trÆ°á»›c khi bÃ¡n hÃ ng.");
                btnBatDau.setText("Xem lá»‹ch ca");
            }
            btnBatDau.setPreferredSize(new Dimension(150, 38));
            lblCapNhat.setText(
                    "HÃ´m nay "
                            + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            + " Â· Cáº­p nháº­t "
                            + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            lblLoi.setText(" ");
        } catch (Exception e) {
            e.printStackTrace();
            lblLoi.setText("ChÆ°a táº£i Ä‘Æ°á»£c dá»¯ liá»‡u. Nháº¥n Táº£i láº¡i Ä‘á»ƒ thá»­ tiáº¿p.");
            lblLoi.setToolTipText(e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        try {
            if (o == btnTai) taiDuLieu();
            else if (o == btnBatDau) {
                if (dangMo) main.moMan("BÃ¡n hÃ ng POS");
                else if (caMo != null) main.moCongViec("Má»Ÿ ca", caMo);
                else main.moMan("Ca lÃ m viá»‡c");
            } else if (o == btnXuLy) {
                int i = table.getSelectedRow();
                if (i < 0) throw new IllegalArgumentException("Chá»n má»™t cÃ´ng viá»‡c trong báº£ng.");
                Object[] r = congViec.get(table.convertRowIndexToModel(i));
                main.moCongViec(r[0].toString(), r[1].toString());
            } else if (o == btnXemCa) {
                int i = tableCa.getSelectedRow();
                if (i < 0) throw new IllegalArgumentException("Chá»n ca trong lá»‹ch.");
                main.moCongViec(
                        "Ca lÃ m viá»‡c", lich.get(tableCa.convertRowIndexToModel(i))[0].toString());
            } else if (o == btnSP) {
                int i = tableSP.getSelectedRow();
                if (i < 0) throw new IllegalArgumentException("Chá»n sáº£n pháº©m cáº§n xem.");
                main.moCongViec(
                        "Sáº£n pháº©m",
                        modelSP.getValueAt(tableSP.convertRowIndexToModel(i), 0).toString());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this, ex.getMessage(), "ThÃ´ng bÃ¡o", JOptionPane.WARNING_MESSAGE);
        }
    }
}
