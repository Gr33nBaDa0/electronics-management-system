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
    // Màu dùng chung được khai báo ở đầu lớp.
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
            tt = new JComboBox<>(new String[] {"", "Chưa thanh toán", "Đã thanh toán", "Đã hủy"}),
            ht = new JComboBox<>(new String[] {"", "Tiền mặt", "Chuyển khoản QR"});
    private final JButton btnTim = taoNut("Tìm / lọc"),
            btnXem = taoNut("Xem / in"),
            btnMo = taoNut("Mở đơn tại POS"),
            btnHuy = taoNut("Hủy đơn"),
            btnBo = taoNut("Bỏ lọc"),
            btnBH = taoNut("Tra bảo hành");
    private final DefaultTableModel model =
            taoModel(
                    "Mã hóa đơn",
                    "Ngày lập",
                    "Nhân viên",
                    "Khách",
                    "Trạng thái",
                    "Hình thức",
                    "Điểm dùng");
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
                        "Mã hóa đơn / SĐT",
                        txtTim,
                        "Từ ngày (yyyy-MM-dd)",
                        tu,
                        "Đến ngày (yyyy-MM-dd)",
                        den,
                        "Trạng thái",
                        tt,
                        "Hình thức thanh toán",
                        ht);
        tt.setRenderer(
                new javax.swing.DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(
                            javax.swing.JList<?> l, Object v, int i, boolean s, boolean f) {
                        return super.getListCellRendererComponent(
                                l,
                                v == null || v.toString().isEmpty() ? "Tất cả trạng thái" : v,
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
                                v == null || v.toString().isEmpty() ? "Tất cả hình thức" : v,
                                i,
                                s,
                                f);
                    }
                });
        add(
                GiaoDien.doc(GiaoDien.the("Bộ lọc hóa đơn", loc, taoDong(btnTim, btnBo))),
                BorderLayout.NORTH);
        add(
                GiaoDien.the(
                        "Danh sách hóa đơn",
                        GiaoDien.cuon(table),
                        taoDong(btnXem, btnMo, btnHuy, btnBH)),
                BorderLayout.CENTER);
        GiaoDien.trangTri(this);
        for (JButton b : new JButton[] {btnTim, btnBo, btnXem, btnMo, btnHuy, btnBH})
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
                                    ? "Vãng lai"
                                    : h.getKhachHang().getMaKhachHang(),
                            h.getTrangThaiThanhToan(),
                            h.getHinhThucThanhToan(),
                            h.getDiemSuDung()
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
            if (i < 0) throw new IllegalArgumentException("Chọn hóa đơn.");
            HoaDon h = new HoaDon_DAO().tim(ds.get(i).getMaHoaDon());
            if (o.equals(btnBH)) {
                if (!"Đã thanh toán".equals(h.getTrangThaiThanhToan()))
                    throw new IllegalArgumentException("Chỉ tra hóa đơn đã thanh toán.");
                List<ChiTietHoaDon> ds = new ChiTietHoaDon_DAO().getTheoHoaDon(h.getMaHoaDon());
                ChiTietHoaDon d =
                        (ChiTietHoaDon)
                                JOptionPane.showInputDialog(
                                        this,
                                        "Chọn sản phẩm đã mua:",
                                        "Tra bảo hành",
                                        JOptionPane.PLAIN_MESSAGE,
                                        null,
                                        ds.toArray(),
                                        ds.isEmpty() ? null : ds.get(0));
                if (d != null) {
                    int thang = d.getSanPham().getThoiHanBaoHanh();
                    JOptionPane.showMessageDialog(
                            this,
                            thang == 0
                                    ? "Sản phẩm không áp dụng bảo hành."
                                    : "Ngày lập hóa đơn: "
                                            + h.getNgayLap().toLocalDate()
                                            + "\nThời hạn sản phẩm hiện tại: "
                                            + thang
                                            + " tháng\nHạn tham khảo: "
                                            + h.getNgayLap().toLocalDate().plusMonths(thang)
                                            + "\n"
                                            + "Vui lòng đối chiếu chứng từ và điều kiện bảo hành"
                                            + " thực tế.");
                }
            } else if (o.equals(btnHuy)) {
                if (xacNhan(this, "Hủy hóa đơn chưa thanh toán?")) {
                    new HoaDon_DAO().huy(h.getMaHoaDon(), tk);
                    loadDataToTable();
                }
            } else if (o.equals(btnMo)) {
                pos.moHoaDon(h.getMaHoaDon());
                JOptionPane.showMessageDialog(this, "Đã nạp đơn. Chọn menu Bán hàng để tiếp tục.");
            } else if (o.equals(btnXem)) {
                List<ChiTietHoaDon> dong = new ChiTietHoaDon_DAO().getTheoHoaDon(h.getMaHoaDon());
                JTextArea text = new JTextArea(InHoaDon.noiDung(h, dong), 25, 65);
                text.setEditable(false);
                JPanel p = new JPanel(new BorderLayout());
                p.add(new JScrollPane(text), BorderLayout.CENTER);
                JButton in = taoNut("In hóa đơn");
                in.setEnabled("Đã thanh toán".equals(h.getTrangThaiThanhToan()));
                in.addActionListener(
                        a -> {
                            try {
                                text.print();
                            } catch (Exception ex) {
                                thongBaoLoi(this, ex);
                            }
                        });
                p.add(in, BorderLayout.SOUTH);
                JOptionPane.showMessageDialog(
                        this, p, "Chi tiết hóa đơn", JOptionPane.PLAIN_MESSAGE);
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
        GiaoDien.phimTatChuan(this, null, null, btnHuy, btnTim, txtTim);
        GiaoDien.phimTat(this, "ctrl P", btnXem);
        GiaoDien.phimTat(this, "ctrl O", btnMo);
    }
}
