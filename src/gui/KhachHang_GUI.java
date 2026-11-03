package gui;

import connectDB.ConnectDB;

import dao.KhachHang_DAO;

import entity.KhachHang;
import entity.TaiKhoan;

import util.GiaoDien;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class KhachHang_GUI extends JPanel implements ActionListener {
    // MÃ u dÃ¹ng chung Ä‘Æ°á»£c khai bÃ¡o á»Ÿ Ä‘áº§u lá»›p.
    private static final Color PRIMARY = GiaoDien.PRIMARY,
            DARK = GiaoDien.DARK,
            BACKGROUND = GiaoDien.BACKGROUND,
            SURFACE = GiaoDien.SURFACE,
            TEXT = GiaoDien.TEXT,
            BORDER = GiaoDien.BORDER,
            ERROR = GiaoDien.ERROR;

    private final TaiKhoan tk;
    private final KhachHang_DAO dao = new KhachHang_DAO();
    private List<KhachHang> danhSach = new ArrayList<>(), hienThi = new ArrayList<>();
    private String maCu;
    private final JTextField maKhachHang = new JTextField(14);
    private final JTextField hoTen = new JTextField(14);
    private final JTextField soDienThoai = new JTextField(14);
    private final JTextField diaChi = new JTextField(14);
    private final JTextField email = new JTextField(14);
    private final JTextField txtTim = new JTextField(20);
    private final JLabel lblSoLuong = new JLabel();
    private final JButton btnMoi = taoNut("Táº¡o má»›i"),
            btnLuu = taoNut("LÆ°u"),
            btnXoa = taoNut("XÃ³a"),
            btnTim = taoNut("TÃ¬m"),
            btnTai = taoNut("Táº£i láº¡i / bá» lá»c");
    private final JButton btnLichSu = taoNut("Lá»‹ch sá»­ mua");
    private final JTextField diemTu = new JTextField(6), diemDen = new JTextField(6);
    private final DefaultTableModel model =
            taoModel(
                    "MÃ£ KH (KH001)", "Há» tÃªn", "Sá»‘ Ä‘iá»‡n thoáº¡i", "Äá»‹a chá»‰", "Email", "Äiá»ƒm hiá»‡n cÃ³");
    private final JTable table = taoBang(model);

    public KhachHang_GUI(TaiKhoan tk) {
        this.tk = tk;
        maKhachHang.setEditable(false);
        maKhachHang.setToolTipText("MÃ£ tá»± sinh khi táº¡o má»›i");
        GiaoDien.boCuc(this);

        JPanel form =
                GiaoDien.form(
                        2,
                        "MÃ£ khÃ¡ch hÃ ng",
                        maKhachHang,
                        "Há» tÃªn",
                        hoTen,
                        "Sá»‘ Ä‘iá»‡n thoáº¡i",
                        soDienThoai,
                        "Äá»‹a chá»‰",
                        diaChi,
                        "Email",
                        email);
        JPanel trai =
                GiaoDien.the(
                        "ThÃ´ng tin khÃ¡ch hÃ ng",
                        GiaoDien.cuonForm(form),
                        taoDong(btnMoi, btnLuu, btnXoa, btnTai));
        JPanel loc =
                GiaoDien.form(3, "MÃ£ / tÃªn / SÄT", txtTim, "Äiá»ƒm tá»«", diemTu, "Äiá»ƒm Ä‘áº¿n", diemDen);
        JPanel danhSach = new JPanel(new BorderLayout(0, 12));
        danhSach.setOpaque(false);
        danhSach.add(GiaoDien.doc(loc, taoDong(btnTim, lblSoLuong)), BorderLayout.NORTH);
        danhSach.add(GiaoDien.cuon(table), BorderLayout.CENTER);
        JPanel phai = GiaoDien.the("Danh sÃ¡ch khÃ¡ch hÃ ng", danhSach, taoDong(btnLichSu));
        trai.setMinimumSize(new Dimension(340, 180));
        phai.setMinimumSize(new Dimension(400, 180));
        add(
                GiaoDien.tach(javax.swing.JSplitPane.HORIZONTAL_SPLIT, trai, phai, .40),
                BorderLayout.CENTER);
        btnLichSu.addActionListener(this);
        GiaoDien.trangTri(this);
        btnMoi.addActionListener(this);
        btnLuu.addActionListener(this);
        btnXoa.addActionListener(this);
        btnTim.addActionListener(this);
        btnTai.addActionListener(this);
        txtTim.addActionListener(this);
        table.getSelectionModel()
                .addListSelectionListener(
                        e -> {
                            if (!e.getValueIsAdjusting()) chonDong();
                        });
        ganPhimTat();
        loadDataToTable();
    }

    private void loadDataToTable() {
        model.setRowCount(0);
        hienThi.clear();
        try {
            if (ConnectDB.con == null || ConnectDB.con.isClosed()) {
                ConnectDB.getInstance().connect();
            }

            List<KhachHang> list = dao.getAllKhachHang();
            danhSach = list;

            if (!diemTu.getText().isBlank()
                    && !diemDen.getText().isBlank()
                    && Integer.parseInt(diemTu.getText()) > Integer.parseInt(diemDen.getText()))
                throw new IllegalArgumentException("Äiá»ƒm tá»« khÃ´ng Ä‘Æ°á»£c lá»›n hÆ¡n Ä‘iá»ƒm Ä‘áº¿n.");
            String tim = txtTim.getText().trim().toLowerCase(java.util.Locale.ROOT);
            for (KhachHang sp : list) {
                String noiDung =
                        (sp.getMaKhachHang()
                                        + " "
                                        + sp.getHoTen()
                                        + " "
                                        + sp.getSoDienThoai()
                                        + " "
                                        + sp.getDiaChi()
                                        + " "
                                        + sp.getEmail())
                                .toLowerCase(java.util.Locale.ROOT);
                if (!noiDung.contains(tim)) continue;
                if (!diemTu.getText().isBlank()
                        && sp.getDiemTichLuy() < Integer.parseInt(diemTu.getText())) continue;
                if (!diemDen.getText().isBlank()
                        && sp.getDiemTichLuy() > Integer.parseInt(diemDen.getText())) continue;
                hienThi.add(sp);
                model.addRow(
                        new Object[] {
                            sp.getMaKhachHang(),
                            sp.getHoTen(),
                            sp.getSoDienThoai(),
                            sp.getDiaChi(),
                            sp.getEmail(),
                            sp.getDiemTichLuy()
                        });
            }
            lblSoLuong.setText("Tá»•ng: " + hienThi.size() + " báº£n ghi");
            if (maCu == null) hienMaTiepTheo();

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

    private void chonDong() {
        int i = table.getSelectedRow();
        if (i < 0 || i >= hienThi.size()) return;
        KhachHang sp = hienThi.get(i);
        maCu = sp.getMaKhachHang();
        maKhachHang.setEditable(false);
        maKhachHang.setText(String.valueOf(sp.getMaKhachHang()));
        hoTen.setText(String.valueOf(sp.getHoTen()));
        soDienThoai.setText(String.valueOf(sp.getSoDienThoai()));
        diaChi.setText(String.valueOf(sp.getDiaChi()));
        email.setText(String.valueOf(sp.getEmail()));
    }

    private void hienMaTiepTheo() {
        maKhachHang.setEditable(false);
        try {
            maKhachHang.setText(dao.layMaTiepTheo());
            maKhachHang.setToolTipText("MÃ£ tá»± sinh khi táº¡o má»›i");
        } catch (SQLException e) {
            maKhachHang.setText("");
            maKhachHang.setToolTipText("ChÆ°a láº¥y Ä‘Æ°á»£c mÃ£: " + e.getMessage());
        }
    }

    private void taoMoi() {
        maCu = null;
        table.clearSelection();
        maKhachHang.setText("");
        hoTen.setText("");
        soDienThoai.setText("");
        diaChi.setText("");
        email.setText("");
        hienMaTiepTheo();
        hoTen.requestFocusInWindow();
    }

    private void luu() throws Exception {
        KhachHang sp = new KhachHang();
        sp.setMaKhachHang(maCu);
        kiemTraRong(hoTen.getText().trim(), "Há» tÃªn");
        sp.setHoTen(hoTen.getText().trim());
        kiemTraRong(soDienThoai.getText().trim(), "Sá»‘ Ä‘iá»‡n thoáº¡i");
        sp.setSoDienThoai(soDienThoai.getText().trim());
        sp.setDiaChi(diaChi.getText().trim());
        sp.setEmail(email.getText().trim());
        kiemTraSDT(soDienThoai.getText());
        if (maCu == null) {
            dao.insertKhachHang(sp, tk);
            JOptionPane.showMessageDialog(this, "ÄÃ£ thÃªm khÃ¡ch hÃ ng " + sp.getMaKhachHang() + ".");
        } else {
            dao.updateKhachHang(sp, tk);
        }
        taoMoi();
        loadDataToTable();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        try {
            if (o.equals(btnMoi)) taoMoi();
            else if (o.equals(btnLuu)) luu();
            else if (o.equals(btnTim) || o.equals(txtTim)) loadDataToTable();
            else if (o.equals(btnTai)) {
                txtTim.setText("");
                diemTu.setText("");
                diemDen.setText("");
                loadDataToTable();
            } else if (o.equals(btnXoa)) {
                if (maCu == null) throw new IllegalArgumentException("Chá»n dÃ²ng cáº§n xÃ³a.");
                if (xacNhan(this, "XÃ³a dá»¯ liá»‡u chÆ°a cÃ³ liÃªn káº¿t?")) {
                    dao.deleteKhachHang(maCu, tk);
                    taoMoi();
                    loadDataToTable();
                }
            } else if (o.equals(btnLichSu)) {
                if (table.getSelectedRow() < 0)
                    throw new IllegalArgumentException("Chá»n khÃ¡ch hÃ ng.");
                JDialog d =
                        new JDialog(
                                (Frame) SwingUtilities.getWindowAncestor(this),
                                "Lá»‹ch sá»­ mua hÃ ng",
                                true);
                d.add(new HoaDon_GUI(tk, null, soDienThoai.getText()));
                d.setSize(1050, 650);
                d.setLocationRelativeTo(this);
                d.setVisible(true);
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

    private void kiemTraRong(String s, String ten) {
        if (s == null || s.trim().isEmpty())
            throw new IllegalArgumentException("Vui lÃ²ng nháº­p " + ten);
    }

    private void kiemTraSDT(String s) {
        if (s == null || !s.matches("0\\d{9}"))
            throw new IllegalArgumentException("SÄT pháº£i gá»“m 10 chá»¯ sá»‘, báº¯t Ä‘áº§u báº±ng 0.");
    }

    private JLabel taoTieuDe(String t) {
        return GiaoDien.tieuDe(t);
    }

    private boolean xacNhan(Component c, String s) {
        return JOptionPane.showConfirmDialog(c, s, "XÃ¡c nháº­n", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }

    private void ganPhimTat() {
        GiaoDien.phimTatChuan(this, btnMoi, btnLuu, btnXoa, btnTai, txtTim);
        GiaoDien.phimTat(this, "ctrl H", btnLichSu);
    }
}
