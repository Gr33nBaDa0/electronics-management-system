package gui;

import connectDB.ConnectDB;

import dao.Dashboard_DAO;

import entity.TaiKhoan;

import util.GiaoDien;
import util.InHoaDon;

import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
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
    private final JLabel thu = new JLabel("—"),
            don = new JLabel("—"),
            cho = new JLabel("—"),
            tra = new JLabel("—"),
            lblViec = GiaoDien.nhan("Đang tải ca làm việc…", 18, true, PRIMARY),
            lblGoiY = GiaoDien.nhan(" ", 12, false, MUTED),
            lblCapNhat = GiaoDien.nhan(" ", 12, false, MUTED),
            lblLoi = GiaoDien.nhan(" ", 12, false, ERROR),
            lblTrong = GiaoDien.nhan(" ", 12, false, MUTED);
    private final JButton btnTai = GiaoDien.nut("Tải lại · F5"),
            btnBatDau = GiaoDien.nutChinh("Xem ca"),
            btnXuLy = GiaoDien.nut("Tiếp tục xử lý"),
            btnXemCa = GiaoDien.nut("Xem ca đã chọn"),
            btnSP = GiaoDien.nut("Xem sản phẩm");
    private final DefaultTableModel
            model = GiaoDien.model("Loại việc", "Mã", "Trạng thái", "Giá trị dự kiến"),
            modelCa = GiaoDien.model("Ngày", "Ca", "Giờ", "Trạng thái"),
            modelSP = GiaoDien.model("Mã SP", "Sản phẩm", "Tồn", "Chú ý");
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
        chiSo.add(GiaoDien.soLieu("Tiền thu hôm nay", thu, "Giao dịch của bạn theo ngày ca"));
        chiSo.add(GiaoDien.soLieu("Hóa đơn đã thanh toán", don, "Trong ngày ca hôm nay"));
        chiSo.add(GiaoDien.soLieu("Đơn chưa thanh toán", cho, "Đơn do bạn lập, còn đang xử lý"));
        chiSo.add(GiaoDien.soLieu("Đổi trả chờ hoàn", tra, "Phiếu của bạn đã được duyệt"));
        JPanel viec = new JPanel(new BorderLayout(12, 0));
        viec.setOpaque(false);
        viec.add(GiaoDien.doc(lblViec, lblGoiY), BorderLayout.CENTER);
        viec.add(btnBatDau, BorderLayout.EAST);
        JPanel uuTien = GiaoDien.the("VIỆC ĐẦU TIÊN", viec, null);
        uuTien.setBackground(GiaoDien.PRIMARY_SOFT);
        JPanel dau = GiaoDien.doc(thongTin, chiSo, uuTien, lblLoi);
        add(dau, BorderLayout.NORTH);
        JPanel danhSach =
                GiaoDien.the(
                        "Công việc đang chờ",
                        GiaoDien.cuon(table),
                        GiaoDien.doc(lblTrong, GiaoDien.dong(btnXuLy)));
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab(
                "Lịch cá nhân · 7 ngày",
                GiaoDien.the(null, GiaoDien.cuon(tableCa), GiaoDien.dong(btnXemCa)));
        tabs.addTab(
                "Sản phẩm tồn thấp",
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
            thu.setText(InHoaDon.tien((BigDecimal) a[0]) + " đ");
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
                        new Object[] {r[0], r[1], r[3], InHoaDon.tien((BigDecimal) r[4]) + " đ"});
            for (Object[] r : ca)
                modelCa.addRow(
                        new Object[] {
                            ((LocalDate) r[1]).format(NGAY),
                            r[2],
                            ((LocalTime) r[3]).format(GIO) + "–" + ((LocalTime) r[4]).format(GIO),
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
                            ? "Không có đơn nháp hoặc phiếu của bạn đang chờ hoàn."
                            : viec.size() + " công việc cần kiểm tra.");
            Object[] mo = null, choMo = null;
            boolean coCa = false;
            for (Object[] r : ca)
                if (LocalDate.now().equals(r[1])) {
                    coCa = true;
                    if ("Đang mở".equals(r[5])) mo = r;
                    if (choMo == null && "Chưa mở".equals(r[5])) choMo = r;
                }
            dangMo = mo != null;
            caMo = choMo == null ? null : choMo[0].toString();
            if (mo != null) {
                lblViec.setText(
                        "Bạn đang làm "
                                + mo[2]
                                + " · "
                                + ((LocalTime) mo[3]).format(GIO)
                                + "–"
                                + ((LocalTime) mo[4]).format(GIO));
                lblGoiY.setText(
                        "Quỹ đầu ca: "
                                + InHoaDon.tien((BigDecimal) mo[6])
                                + " đ. Có thể bắt đầu bán hàng.");
                btnBatDau.setText("Bán hàng");
            } else if (choMo != null) {
                lblViec.setText("Mở " + choMo[2] + " để bắt đầu làm việc");
                lblGoiY.setText(
                        "Đã được phân công hôm nay · "
                                + ((LocalTime) choMo[3]).format(GIO)
                                + "–"
                                + ((LocalTime) choMo[4]).format(GIO));
                btnBatDau.setText("Mở ca");
            } else {
                lblViec.setText(
                        coCa
                                ? "Các ca hôm nay của bạn đã kết thúc"
                                : "Bạn chưa được phân công ca hôm nay");
                lblGoiY.setText(
                        coCa
                                ? "Kiểm tra lịch sắp tới và các công việc còn chờ."
                                : "Liên hệ quản lý để được phân công trước khi bán hàng.");
                btnBatDau.setText("Xem lịch ca");
            }
            btnBatDau.setPreferredSize(new Dimension(150, 38));
            lblCapNhat.setText(
                    "Hôm nay "
                            + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            + " · Cập nhật "
                            + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            lblLoi.setText(" ");
        } catch (Exception e) {
            e.printStackTrace();
            lblLoi.setText("Chưa tải được dữ liệu. Nhấn Tải lại để thử tiếp.");
            lblLoi.setToolTipText(e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        try {
            if (o == btnTai) taiDuLieu();
            else if (o == btnBatDau) {
                if (dangMo) main.moMan("Bán hàng POS");
                else if (caMo != null) main.moCongViec("Mở ca", caMo);
                else main.moMan("Ca làm việc");
            } else if (o == btnXuLy) {
                int i = table.getSelectedRow();
                if (i < 0) throw new IllegalArgumentException("Chọn một công việc trong bảng.");
                Object[] r = congViec.get(table.convertRowIndexToModel(i));
                main.moCongViec(r[0].toString(), r[1].toString());
            } else if (o == btnXemCa) {
                int i = tableCa.getSelectedRow();
                if (i < 0) throw new IllegalArgumentException("Chọn ca trong lịch.");
                main.moCongViec(
                        "Ca làm việc", lich.get(tableCa.convertRowIndexToModel(i))[0].toString());
            } else if (o == btnSP) {
                int i = tableSP.getSelectedRow();
                if (i < 0) throw new IllegalArgumentException("Chọn sản phẩm cần xem.");
                main.moCongViec(
                        "Sản phẩm",
                        modelSP.getValueAt(tableSP.convertRowIndexToModel(i), 0).toString());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this, ex.getMessage(), "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }
}
