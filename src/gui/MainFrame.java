package gui;

import common.UserSession;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private JPanel contentPanel; // Vùng chứa nội dung thay đổi

    public MainFrame() {
        setTitle("Hệ thống Quản lý Siêu thị Điện máy GreenSmart");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout chính: BorderLayout (Sidebar bên trái, Content ở giữa)
        setLayout(new BorderLayout());

        // --- 1. TẠO SIDEBAR (MENU TRÁI) ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(22, 101, 52)); // Màu xanh đậm
        sidebar.setPreferredSize(new Dimension(220, 800));

        // Logo / Tiêu đề
        JLabel lblLogo = new JLabel("GreenSmart", SwingConstants.CENTER);
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(new Font("Arial", Font.BOLD, 20));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblLogo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        sidebar.add(lblLogo);

        // Các nút menu
        JButton btnBanHang = createMenuButton("Bán hàng (POS)");
        JButton btnSanPham = createMenuButton("Quản lý Sản phẩm");
        JButton btnKhachHang = createMenuButton("Quản lý Khách hàng");
        JButton btnNhapHang = createMenuButton("Nhập hàng");
        JButton btnDoiTra = createMenuButton("Đổi trả");
        JButton btnNhanVien = createMenuButton("Quản lý Nhân viên");
        JButton btnBaoCao = createMenuButton("Thống kê - Báo cáo");
        JButton btnDangXuat = createMenuButton("Đăng xuất");

        sidebar.add(btnBanHang);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnSanPham);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnKhachHang);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnNhapHang);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnDoiTra);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnNhanVien);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnBaoCao);
        sidebar.add(Box.createVerticalGlue()); // Đẩy nút Đăng xuất xuống đáy
        sidebar.add(btnDangXuat);

        add(sidebar, BorderLayout.WEST);

        // --- 2. PHÂN QUYỀN ---
        // Nếu là BAN_HANG, ẩn các nút Quản lý
        if ("BAN_HANG".equals(UserSession.currentAccount.getQuyen())) {
            btnSanPham.setVisible(false); // Nhân viên không được quản lý sản phẩm
            btnNhanVien.setVisible(false); // Không quản lý nhân viên
            btnBaoCao.setVisible(false);   // Không xem báo cáo
        }

        // --- 3. TẠO CONTENT PANEL (VÙNG NỘI DUNG) ---
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(new Color(245, 247, 246));
        
        // Hiển thị màn hình chào mừng mặc định
        JLabel lblWelcome = new JLabel("Chào mừng " + UserSession.currentUser.getHoTen() + " đến với hệ thống!", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 24));
        contentPanel.add(lblWelcome, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        // --- 4. GẮN SỰ KIỆN CHO CÁC NÚT ---
        btnDangXuat.addActionListener(e -> {
            UserSession.logout();
            this.dispose();
            new LoginFrame().setVisible(true);
        });

        // Gắn sự kiện mở các màn hình (Tạm thời hiển thị thông báo, GĐ sau sẽ thay bằng Panel thật)
        btnBanHang.addActionListener(e -> showPanel(new JPanel(), "Bán hàng"));
        btnSanPham.addActionListener(e -> showPanel(new JPanel(), "Quản lý Sản phẩm"));
    }

    // Hàm tạo nút Menu cho đồng bộ
    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(220, 40));
        btn.setBackground(new Color(22, 101, 52));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        // Hiệu ứng hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(new Color(20, 83, 45)); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(new Color(22, 101, 52)); }
        });
        return btn;
    }

    // Hàm thay đổi nội dung bên phải
    private void showPanel(JPanel panel, String title) {
        contentPanel.removeAll();
        // Tạo thanh tiêu đề trên cùng
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        contentPanel.add(lblTitle, BorderLayout.NORTH);
        contentPanel.add(panel, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}