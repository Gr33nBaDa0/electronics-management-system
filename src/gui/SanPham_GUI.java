package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import dao.SanPham_DAO;
import entity.SanPham;
import entity.TaiKhoan;

public class SanPham_GUI extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private final SanPham_DAO sanPhamDAO = new SanPham_DAO();
    private final JButton btnTaiLai = new JButton("Tải lại dữ liệu SQL");
    private final JButton btnDangXuat = new JButton("Đăng xuất");
    private final JLabel lblTrangThai = new JLabel("Đang tải sản phẩm...");
    private final DefaultTableModel model;
    private final JTable table;

    public SanPham_GUI(TaiKhoan taiKhoan) {
        if (taiKhoan == null || taiKhoan.getNhanVien() == null) {
            throw new IllegalArgumentException("Vui lòng đăng nhập trước.");
        }
        setTitle("GreenSmart - Danh sách sản phẩm");
        setSize(1100, 580);
        setMinimumSize(new Dimension(900, 450));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel pnlChinh = new JPanel(new BorderLayout(10, 15));
        pnlChinh.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel pnlDau = new JPanel(new BorderLayout(10, 10));
        pnlDau.add(new JLabel("DANH SÁCH SẢN PHẨM"), BorderLayout.NORTH);
        pnlDau.add(new JLabel("Nhân viên: " + taiKhoan.getNhanVien().getHoTen()
                + " | Tài khoản: " + taiKhoan.getTenDangNhap()
                + " | Quyền: " + taiKhoan.getQuyen()), BorderLayout.CENTER);
        JPanel pnlNut = new JPanel();
        pnlNut.add(btnTaiLai);
        pnlNut.add(btnDangXuat);
        pnlDau.add(pnlNut, BorderLayout.EAST);
        pnlChinh.add(pnlDau, BorderLayout.NORTH);

        String[] cot = { "Mã sản phẩm", "Tên sản phẩm", "Loại hàng", "Thương hiệu",
                "Giá bán (VNĐ)", "Tồn kho", "BH (tháng)", "Trạng thái" };
        model = new DefaultTableModel(cot, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(27);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.getColumnModel().getColumn(1).setPreferredWidth(260);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(55);
        table.getColumnModel().getColumn(6).setPreferredWidth(70);
        pnlChinh.add(new JScrollPane(table), BorderLayout.CENTER);
        pnlChinh.add(lblTrangThai, BorderLayout.SOUTH);
        setContentPane(pnlChinh);
        btnTaiLai.addActionListener(this);
        btnDangXuat.addActionListener(this);
        loadDataToTable();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(btnTaiLai)) {
            loadDataToTable();
        } else if (o.equals(btnDangXuat)) {
            new DangNhap_GUI().setVisible(true);
            dispose();
        }
    }

    private void loadDataToTable() {
        model.setRowCount(0);
        try {
            List<SanPham> list = sanPhamDAO.getAllSanPham();
            for (SanPham sp : list) {
                model.addRow(new Object[]{
                    sp.getMaSanPham(), sp.getTenSanPham(),
                    sp.getLoaiHang(), sp.getThuongHieu(),
                    formatGia(sp.getDonGiaBan()), sp.getSoLuongTon(),
                    sp.getThoiHanBaoHanh(), sp.getTrangThai()
                });
            }
            lblTrangThai.setText("Tổng: " + list.size() + " sản phẩm");
        } catch (SQLException e) {
            e.printStackTrace();
            lblTrangThai.setText("Không tải được dữ liệu SQL. Bấm Tải lại để thử lại.");
            JOptionPane.showMessageDialog(this,
                    "Lỗi tải sản phẩm:\n" + e.getMessage(),
                    "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
        }
    }
    private String formatGia(BigDecimal gia) {
        DecimalFormat df = new DecimalFormat("#,##0.##");
        return gia == null ? "" : df.format(gia);
    }
}
