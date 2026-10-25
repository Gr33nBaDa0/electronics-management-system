package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import connectDB.ConnectDB;
import dao.TaiKhoan_DAO;
import entity.TaiKhoan;

public class DangNhap_GUI extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;

    private JTextField txtTaiKhoan;
    private JPasswordField txtMatKhau;
    private JCheckBox chkHienMatKhau;
    private JButton btnDangNhap;
    private JButton btnThoat;
    private JLabel lblThongBao;
    private char kyTuAnMatKhau;

    public DangNhap_GUI() {
        setTitle("GreenSmart - Đăng nhập");
        setSize(500, 310);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel pnlChinh = new JPanel(new BorderLayout(10, 18));
        pnlChinh.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        pnlChinh.add(new JLabel("ĐĂNG NHẬP GREENSMART", JLabel.CENTER), BorderLayout.NORTH);

        JPanel pnlNhap = new JPanel(new GridLayout(3, 2, 10, 12));
        pnlNhap.add(new JLabel("Tài khoản:"));
        txtTaiKhoan = new JTextField();
        pnlNhap.add(txtTaiKhoan);

        pnlNhap.add(new JLabel("Mật khẩu:"));
        txtMatKhau = new JPasswordField();
        kyTuAnMatKhau = txtMatKhau.getEchoChar();
        pnlNhap.add(txtMatKhau);

        pnlNhap.add(new JLabel(""));
        chkHienMatKhau = new JCheckBox("Hiện mật khẩu");
        pnlNhap.add(chkHienMatKhau);
        pnlChinh.add(pnlNhap, BorderLayout.CENTER);

        JPanel pnlDuoi = new JPanel(new BorderLayout(0, 10));
        lblThongBao = new JLabel("Nhập tài khoản và mật khẩu của bạn.");
        pnlDuoi.add(lblThongBao, BorderLayout.NORTH);

        JPanel pnlNut = new JPanel();
        btnDangNhap = new JButton("Đăng nhập");
        btnThoat = new JButton("Thoát");
        pnlNut.add(btnDangNhap);
        pnlNut.add(btnThoat);
        pnlDuoi.add(pnlNut, BorderLayout.SOUTH);
        pnlChinh.add(pnlDuoi, BorderLayout.SOUTH);
        setContentPane(pnlChinh);

        chkHienMatKhau.addActionListener(this);
        btnDangNhap.addActionListener(this);
        btnThoat.addActionListener(this);
        txtTaiKhoan.addActionListener(this);
        txtMatKhau.addActionListener(this);
        getRootPane().setDefaultButton(btnDangNhap);

        try {
            ConnectDB.getInstance().connect();
            lblThongBao.setText("Kết nối SQL thành công. Vui lòng đăng nhập.");
        } catch (SQLException e) {
            e.printStackTrace();
            lblThongBao.setText("Chưa kết nối được SQL. Bấm Đăng nhập để thử lại.");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o.equals(chkHienMatKhau)) {
            if (chkHienMatKhau.isSelected()) {
                txtMatKhau.setEchoChar((char) 0);
            } else {
                txtMatKhau.setEchoChar(kyTuAnMatKhau);
            }
        } else if (o.equals(btnDangNhap) || o.equals(txtMatKhau)) {
            xulyDangNhap();
        } else if (o.equals(txtTaiKhoan)) {
            txtMatKhau.requestFocusInWindow();
        } else if (o.equals(btnThoat)) {
            dispose();
        }
    }

    private void xulyDangNhap() {
        String user = txtTaiKhoan.getText().trim();
        String pass = new String(txtMatKhau.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập tài khoản và mật khẩu!",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            if (user.isEmpty()) {
                txtTaiKhoan.requestFocusInWindow();
            } else {
                txtMatKhau.requestFocusInWindow();
            }
            return;
        }

        if (user.length() > 5 || pass.length() > 128) {
            JOptionPane.showMessageDialog(this,
                    "Tài khoản tối đa 5 ký tự, mật khẩu tối đa 128 ký tự!",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (ConnectDB.con == null || ConnectDB.con.isClosed()) {
                ConnectDB.getInstance().connect();
            }

            TaiKhoan_DAO dao = new TaiKhoan_DAO();
            TaiKhoan tk = dao.kiemTraDangNhap(user, pass);

            if (tk != null) {
                String vaiTro = tk.getNhanVien().getVaiTro();
                boolean hopLe = ("ADMIN".equals(tk.getQuyen()) && "Quản lý".equals(vaiTro))
                        || ("BAN_HANG".equals(tk.getQuyen()) && "Bán hàng".equals(vaiTro));

                if (!hopLe) {
                    JOptionPane.showMessageDialog(this,
                            "Quyền tài khoản chưa khớp vai trò nhân viên. Vui lòng liên hệ quản lý!",
                            "Thông báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                JOptionPane.showMessageDialog(this, "Đăng nhập thành công!",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                new SanPham_GUI(tk).setVisible(true);
                dispose();
            } else {
                lblThongBao.setText("Đăng nhập chưa thành công.");
                JOptionPane.showMessageDialog(this,
                        "Tài khoản hoặc mật khẩu không chính xác!",
                        "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
                txtMatKhau.setText("");
                txtMatKhau.requestFocusInWindow();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lblThongBao.setText("Không thể đăng nhập do lỗi kết nối hoặc truy vấn SQL.");
            JOptionPane.showMessageDialog(this,
                    "Không thể kết nối hoặc truy vấn SQL. Vui lòng kiểm tra SQL Server và ConnectDB!",
                    "Lỗi database", JOptionPane.ERROR_MESSAGE);
        }
    }
}
