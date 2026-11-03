package gui;

import connectDB.ConnectDB;

import dao.BaoCao_DAO;
import dao.Dashboard_DAO;
import dao.TaiKhoan_DAO;

import entity.TaiKhoan;

import until.GiaoDien;
import until.InHoaDon;

import java.awt.*;
import java.awt.event.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DashboardQuanLy_GUI extends JPanel implements ActionListener {
    private static final Color PRIMARY = GiaoDien.PRIMARY,
            MUTED = GiaoDien.MUTED,
            ERROR = GiaoDien.ERROR;
    private final TaiKhoan tk;
    private final MainFrame main;
    private final JLabel thu = new JLabel("—"),
            don = new JLabel("—"),
            nhap = new JLabel("—"),
            tra = new JLabel("—"),
            lblKeHoach = GiaoDien.nhan("Chưa đặt mục tiêu hôm nay", 14, true, PRIMARY),
            lblCapNhat = GiaoDien.nhan(" ", 12, false, MUTED),
            lblLoi = GiaoDien.nhan(" ", 12, false, ERROR),
            lblViec = GiaoDien.nhan(" ", 12, false, MUTED),
            lblTon = GiaoDien.nhan(" ", 12, false, MUTED);
    private final JProgressBar tienDo = new JProgressBar(0, 100);
    private final JButton btnTai = GiaoDien.nut("Tải lại · F5"),
            btnMucTieu = GiaoDien.nut("Đặt mục tiêu"),
            btnXem = GiaoDien.nutChinh("Xử lý phiếu đã chọn"),
            btnSP = GiaoDien.nut("Xem sản phẩm");
    private final DefaultTableModel
            model = GiaoDien.model("Loại", "Mã phiếu", "NV lập", "Ngày", "Giá trị dự kiến"),
            modelSP = GiaoDien.model("Mã SP", "Sản phẩm", "Tồn", "Chú ý");
    private final JTable table = GiaoDien.bang(model), tableSP = GiaoDien.bang(modelSP);
    private final BieuDoThu bieuDo = new BieuDoThu();
    private List<Object[]> phieu = new ArrayList<>();
    private double thucTe = 0.0, mucTieu = 0.0;

    public DashboardQuanLy_GUI(TaiKhoan tk, MainFrame main) {
        this.tk = tk;
        this.main = main;
        GiaoDien.boCuc(this);
        JPanel capNhat = new JPanel(new BorderLayout());
        capNhat.setOpaque(false);
        capNhat.add(lblCapNhat, BorderLayout.CENTER);
        capNhat.add(btnTai, BorderLayout.EAST);
        JPanel chiSo = new JPanel(new GridLayout(1, 4, 10, 0));
        chiSo.setOpaque(false);
        chiSo.add(
                GiaoDien.soLieu(
                        "Thu sau hoàn hôm nay", thu, "Tiền thu trừ tiền hoàn theo ngày ca"));
        chiSo.add(GiaoDien.soLieu("Hóa đơn thanh toán", don, "Của cửa hàng trong ngày ca"));
        chiSo.add(GiaoDien.soLieu("Nhập hàng chờ duyệt", nhap, "Cần kiểm tra hàng và chứng từ"));
        chiSo.add(GiaoDien.soLieu("Đổi trả chờ duyệt", tra, "Cần xem xét điều kiện đổi trả"));
        tienDo.setStringPainted(true);
        tienDo.setForeground(PRIMARY);
        tienDo.setBackground(GiaoDien.PRIMARY_SOFT);
        tienDo.setPreferredSize(new Dimension(200, 18));
        JPanel keHoach = new JPanel(new BorderLayout(14, 6));
        keHoach.setOpaque(false);
        keHoach.add(GiaoDien.doc(lblKeHoach, tienDo), BorderLayout.CENTER);
        keHoach.add(btnMucTieu, BorderLayout.EAST);
        add(
                GiaoDien.doc(capNhat, chiSo, GiaoDien.the(null, keHoach, null), lblLoi),
                BorderLayout.NORTH);
        JPanel choDuyet =
                GiaoDien.the(
                        "Phiếu cần duyệt",
                        GiaoDien.cuon(table),
                        GiaoDien.doc(lblViec, GiaoDien.dong(btnXem)));
        JPanel ton =
                GiaoDien.the(
                        "Sản phẩm cần xem",
                        GiaoDien.cuon(tableSP),
                        GiaoDien.doc(lblTon, GiaoDien.dong(btnSP)));
        JPanel xuHuong = GiaoDien.the("Thu sau hoàn · 7 ngày gần nhất", bieuDo, null);
        ton.setMinimumSize(new Dimension(300, 170));
        xuHuong.setMinimumSize(new Dimension(300, 155));
        JSplitPane phai = GiaoDien.tach(JSplitPane.VERTICAL_SPLIT, ton, xuHuong, .64);
        choDuyet.setMinimumSize(new Dimension(380, 200));
        phai.setMinimumSize(new Dimension(340, 330));
        add(GiaoDien.tach(JSplitPane.HORIZONTAL_SPLIT, choDuyet, phai, .57), BorderLayout.CENTER);
        for (JButton b : new JButton[] {btnTai, btnMucTieu, btnXem, btnSP})
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
            Object[] a = dao.tongHopNgay(tk, LocalDate.now(), false);
            List<Object[]> ds = dao.phieuChoDuyet(tk), sp = dao.sanPhamCanXem(tk);
            Map<LocalDate, Double> ngay = new LinkedHashMap<>();
            for (int i = 6; i >= 0; i--) ngay.put(LocalDate.now().minusDays(i), 0.0);
            for (Object[] r :
                    new BaoCao_DAO().doanhThu(LocalDate.now().minusDays(6), LocalDate.now(), tk)) {
                LocalDate n = ((java.sql.Date) r[0]).toLocalDate();
                ngay.put(n, InHoaDon.tron(ngay.getOrDefault(n, 0.0) + ((Number) r[5]).doubleValue()));
            }
            thucTe = InHoaDon.tron(((Number) a[0]).doubleValue() - ((Number) a[1]).doubleValue());
            thu.setText(InHoaDon.tien(thucTe) + " đ");
            thu.setForeground(thucTe < 0 ? ERROR : PRIMARY);
            don.setText(a[2].toString());
            nhap.setText(a[4].toString());
            tra.setText(a[5].toString());
            phieu = ds;
            model.setRowCount(0);
            modelSP.setRowCount(0);
            for (Object[] r : ds)
                model.addRow(
                        new Object[] {
                            r[0],
                            r[1],
                            r[2],
                            ((LocalDateTime) r[3])
                                    .format(DateTimeFormatter.ofPattern("dd/MM HH:mm")),
                            InHoaDon.tien(((Number) r[4]).doubleValue()) + " đ"
                        });
            for (Object[] r : sp) modelSP.addRow(r);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            int[] rong = {90, 174, 80, 104, 126};
            for (int i = 0; i < rong.length; i++) {
                table.getColumnModel().getColumn(i).setMinWidth(rong[i]);
                table.getColumnModel().getColumn(i).setPreferredWidth(rong[i]);
            }
            lblViec.setText(
                    ds.isEmpty()
                            ? "Không có phiếu đang chờ duyệt."
                            : ds.size()
                                    + " phiếu đang chờ · "
                                    + a[6]
                                    + " phiếu trả đã duyệt chờ hoàn.");
            lblTon.setText(
                    sp.isEmpty()
                            ? "Các sản phẩm đang bán đều trên ngưỡng tồn thấp."
                            : sp.size()
                                    + " sản phẩm đang bán có tồn ≤ "
                                    + Dashboard_DAO.NGUONG_TON_THAP
                                    + ".");
            bieuDo.duLieu = ngay;
            bieuDo.repaint();
            mucTieu = docMucTieu();
            hienKeHoach();
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

    private void hienKeHoach() {
        if (mucTieu <= 0) {
            lblKeHoach.setText("Chưa đặt mục tiêu thu sau hoàn hôm nay");
            tienDo.setValue(0);
            tienDo.setString("Chưa đặt mục tiêu");
            return;
        }
        double pt =
                Math.round(thucTe * 1000.0 / mucTieu) / 10.0;
        lblKeHoach.setText(
                InHoaDon.tien(thucTe)
                        + " / "
                        + InHoaDon.tien(mucTieu)
                        + " đ · "
                        + (InHoaDon.soSanhTien(thucTe, mucTieu) >= 0
                                ? "Đã đạt mục tiêu"
                                : "Còn " + InHoaDon.tien(InHoaDon.tron(mucTieu - thucTe)) + " đ"));
        tienDo.setValue((int) Math.max(0.0, Math.min(100.0, pt)));
        tienDo.setString(String.format(java.util.Locale.US, "%.1f", pt) + "% mục tiêu");
    }

    private Properties docThietLap() throws java.io.IOException {
        Properties p = new Properties();
        Path f = Paths.get("config.properties");
        if (!Files.exists(f)) f = Paths.get("config.example.properties");
        if (Files.exists(f))
            try (java.io.Reader r = Files.newBufferedReader(f)) {
                p.load(r);
            }
        return p;
    }

    private double docMucTieu() throws java.io.IOException {
        String s = docThietLap().getProperty("dashboard.target." + LocalDate.now(), "");
        try {
            return s.isBlank() ? 0.0 : Math.max(0.0, InHoaDon.soTien(s));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private void nhapMucTieu() throws Exception {
        new TaiKhoan_DAO().kiemTraQuyen(tk, true);
        String s =
                JOptionPane.showInputDialog(
                        this,
                        "Mục tiêu thu sau hoàn hôm nay (VND):",
                        mucTieu > 0 ? InHoaDon.tien(mucTieu).replace(",", "") : "");
        if (s == null) return;
        double tien = InHoaDon.soTien(s);
        if (tien <= 0 || !Double.isFinite(tien) || tien >= 1.0E18)
            throw new IllegalArgumentException("Nhập mục tiêu dương, tối đa 18 chữ số.");
        Properties p = docThietLap();
        p.setProperty("dashboard.target." + LocalDate.now(), InHoaDon.tien(tien).replace(",", ""));
        try (java.io.Writer w = Files.newBufferedWriter(Paths.get("config.properties"))) {
            p.store(w, "GreenSmart settings");
        }
        mucTieu = tien;
        hienKeHoach();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        try {
            if (o == btnTai) taiDuLieu();
            else if (o == btnMucTieu) nhapMucTieu();
            else if (o == btnXem) {
                int i = table.getSelectedRow();
                if (i < 0) throw new IllegalArgumentException("Chọn phiếu cần xử lý.");
                Object[] r = phieu.get(table.convertRowIndexToModel(i));
                main.moCongViec(r[0].toString(), r[1].toString());
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

    private static final class BieuDoThu extends JPanel {
        Map<LocalDate, Double> duLieu = new LinkedHashMap<>();

        BieuDoThu() {
            setOpaque(false);
            setPreferredSize(new Dimension(400, 170));
            setToolTipText("Thu sau hoàn theo ngày ca; nhãn biểu đồ tính bằng triệu đồng.");
        }

        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (duLieu.isEmpty()) {
                g.setColor(MUTED);
                g.drawString("Chưa có dữ liệu", 12, 30);
                g.dispose();
                return;
            }
            double max = 0, min = 0;
            for (double v : duLieu.values()) {
                max = Math.max(max, v);
                min = Math.min(min, v);
            }
            if (max == min) {
                g.setColor(MUTED);
                g.setFont(GiaoDien.phong(Font.PLAIN, 12));
                g.drawString(
                        "Thu sau hoàn bằng 0 trong cả 7 ngày.", 10, Math.max(24, getHeight() / 2));
                g.dispose();
                return;
            }
            int h = Math.max(10, getHeight() - 62),
                    w = Math.max(100, getWidth() - 20),
                    top = 26,
                    base = top + (int) (h * max / (max - min));
            g.setColor(GiaoDien.BORDER);
            g.drawLine(10, base, getWidth() - 10, base);
            int i = 0, n = duLieu.size(), bar = Math.max(10, Math.min(36, w / n - 12));
            for (Map.Entry<LocalDate, Double> e : duLieu.entrySet()) {
                int x = 10 + i * w / n + (w / n - bar) / 2,
                        y = top + (int) (h * (max - e.getValue().doubleValue()) / (max - min));
                g.setColor(e.getValue() < 0 ? DashboardQuanLy_GUI.ERROR : PRIMARY);
                if (e.getValue() != 0)
                    g.fillRoundRect(
                            x, Math.min(base, y), bar, Math.max(2, Math.abs(base - y)), 4, 4);
                String s =
                        InHoaDon.tien(Math.copySign(Math.floor(Math.abs(e.getValue()) / 100000.0 + 0.5) / 10.0, e.getValue()));
                g.setFont(GiaoDien.phong(Font.PLAIN, 10));
                g.setColor(MUTED);
                g.drawString(
                        s,
                        x + (bar - g.getFontMetrics().stringWidth(s)) / 2,
                        e.getValue() < 0 ? y + 12 : Math.max(12, y - 4));
                String ngay = e.getKey().format(DateTimeFormatter.ofPattern("dd/MM"));
                g.drawString(
                        ngay,
                        x + (bar - g.getFontMetrics().stringWidth(ngay)) / 2,
                        getHeight() - 8);
                i++;
            }
            g.setFont(GiaoDien.phong(Font.PLAIN, 10));
            g.drawString("Triệu đồng", 10, 11);
            g.dispose();
        }
    }
}
