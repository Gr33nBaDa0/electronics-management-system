package gui;

import dao.TaiKhoan_DAO;
import entity.TaiKhoan;
import common.UserSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private JButton btnDangNhap;

    public LoginFrame() {
        setTitle("Đăng nhập hệ thống - GreenSmart");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Căn giữa màn hình
        setResizable(false);

        // Tạo panel chính
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(245, 247, 246)); // Màu nền UITheme
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tiêu đề
        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ GREENSMART", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(new Color(22, 101, 52)); // Màu xanh lá
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);

        // Tên đăng nhập
        gbc.gridwidth = 1; gbc.gridy = 1;
        panel.add(new JLabel("Tên đăng nhập:"), gbc);
        txtTenDangNhap = new JTextField();
        gbc.gridx = 1;
        panel.add(txtTenDangNhap, gbc);

        // Mật khẩu
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Mật khẩu:"), gbc);
        txtMatKhau = new JPasswordField();
        gbc.gridx = 1;
        panel.add(txtMatKhau, gbc);

        // Nút đăng nhập
        btnDangNhap = new JButton("ĐĂNG NHẬP");
        btnDangNhap.setBackground(new Color(22, 101, 52));
        btnDangNhap.setForeground(Color.WHITE);
        btnDangNhap.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(btnDangNhap, gbc);

        add(panel);

        // Xử lý sự kiện bấm nút Đăng nhập
        btnDangNhap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLyDangNhap();
            }
        });
    }

    private void xuLyDangNhap() {
        String tenDN = txtTenDangNhap.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword());

        if (tenDN.isEmpty() || matKhau.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        TaiKhoan_DAO dao = new TaiKhoan_DAO();
        TaiKhoan tk = dao.checkLogin(tenDN, matKhau);

        if (tk != null) {
            // Lưu vào Session
            UserSession.currentAccount = tk;
            UserSession.currentUser = tk.getNhanVien();

            JOptionPane.showMessageDialog(this, "Đăng nhập thành công!\nXin chào: " + UserSession.currentUser.getHoTen());
            
            // Mở MainFrame và đóng LoginFrame
            new MainFrame().setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập hoặc mật khẩu không đúng!", "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
        }
    }
}