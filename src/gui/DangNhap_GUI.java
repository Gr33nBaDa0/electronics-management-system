package gui;

import connectDB.ConnectDB;

import dao.TaiKhoan_DAO;

import entity.TaiKhoan;

import until.GiaoDien;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class DangNhap_GUI extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;

    private static final Color MAU_CHINH = GiaoDien.PRIMARY;
    private static final Color MAU_NUT_HOVER = GiaoDien.PRIMARY_DARK;
    private static final Color MAU_NEN_TRAI = GiaoDien.PRIMARY_SOFT;
    private static final Color MAU_NEN_PHAI = GiaoDien.FORM_BACKGROUND;
    private static final Color MAU_NEN_CHUNG = GiaoDien.BACKGROUND;
    private static final Color MAU_O_NHAP = Color.WHITE;
    private static final Color MAU_CHU = GiaoDien.TEXT;
    private static final Color MAU_CHU_PHU = GiaoDien.MUTED;
    private static final Color MAU_CHU_TREN_NUT = Color.WHITE;
    private static final Color MAU_VIEN = GiaoDien.BORDER;
    private static final Color MAU_BOI_DEN = GiaoDien.SELECT;
    private static final Color MAU_CHON_BANG = GiaoDien.SELECT;
    private static final Color MAU_LOI = GiaoDien.ERROR;

    private static final Font FONT_THUONG_HIEU = GiaoDien.phong(Font.BOLD, 36);
    private static final Font FONT_GIAO_DIEN_CHUNG = GiaoDien.phong(Font.PLAIN, 14);
    private static final Font FONT_TIEU_DE = GiaoDien.phong(Font.BOLD, 28);
    private static final Font FONT_NOI_DUNG = GiaoDien.phong(Font.PLAIN, 15);
    private static final Font FONT_NHAN = GiaoDien.phong(Font.BOLD, 14);
    private static final Font FONT_O_NHAP = GiaoDien.phong(Font.PLAIN, 16);
    private static final Font FONT_NUT = GiaoDien.phong(Font.BOLD, 16);
    private static final Font FONT_CHU_NHO = GiaoDien.phong(Font.PLAIN, 12);
    private static final String DUONG_DAN_LOGO = "resource/logo_greensmart.png";

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkShowPass;
    private JButton btnLogin;
    private JButton btnQuenMatKhau;
    private JLabel lblStatus;
    private char kyTuAnMatKhau;

    public DangNhap_GUI() {
        GiaoDien.apDung();
        setTitle("GreenSmart - Đăng nhập");
        setSize(1100, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 0, 0));

        // Cột trái: logo chiếc lá và tên ứng dụng.
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(MAU_NEN_TRAI);
        leftPanel.setBorder(new EmptyBorder(35, 30, 35, 30));
        GridBagConstraints trai = new GridBagConstraints();
        trai.gridx = 0;
        trai.gridy = 0;
        trai.anchor = GridBagConstraints.CENTER;

        JLabel lblLogo = new JLabel("", SwingConstants.CENTER);
        lblLogo.setPreferredSize(new Dimension(340, 300));
        lblLogo.setBackground(MAU_NEN_TRAI);
        lblLogo.setIcon(GiaoDien.logo(280));
        leftPanel.add(lblLogo, trai);

        JLabel lblThuongHieu = new JLabel("GREENSMART", SwingConstants.CENTER);
        lblThuongHieu.setFont(FONT_THUONG_HIEU);
        lblThuongHieu.setForeground(MAU_NUT_HOVER);
        trai.gridy = 1;
        trai.insets = new Insets(2, 0, 12, 0);
        leftPanel.add(lblThuongHieu, trai);

        JLabel lblMoTa = new JLabel("QUẢN LÝ CỬA HÀNG ĐIỆN MÁY", SwingConstants.CENTER);
        lblMoTa.setFont(FONT_CHU_NHO);
        lblMoTa.setForeground(MAU_CHU_PHU);
        trai.gridy = 2;
        trai.insets = new Insets(0, 0, 0, 0);
        leftPanel.add(lblMoTa, trai);

        // Cột phải: form đăng nhập. GridBagLayout giữ các ô cùng chiều rộng.
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(MAU_NEN_PHAI);
        rightPanel.setBorder(new EmptyBorder(40, 56, 40, 56));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.LINE_START;

        JLabel lblTitle = new JLabel("Chào mừng trở lại!");
        lblTitle.setFont(FONT_TIEU_DE);
        lblTitle.setForeground(MAU_CHU);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 7, 0);
        rightPanel.add(lblTitle, gbc);

        JLabel lblSubtitle = new JLabel("Đăng nhập để tiếp tục");
        lblSubtitle.setFont(FONT_NOI_DUNG);
        lblSubtitle.setForeground(MAU_CHU_PHU);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 32, 0);
        rightPanel.add(lblSubtitle, gbc);

        JLabel lblUser = new JLabel("Tài khoản");
        lblUser.setFont(FONT_NHAN);
        lblUser.setForeground(MAU_CHU);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 7, 0);
        rightPanel.add(lblUser, gbc);

        txtUsername = new JTextField();
        txtUsername.setFont(FONT_O_NHAP);
        txtUsername.setBackground(MAU_O_NHAP);
        txtUsername.setForeground(MAU_CHU);
        txtUsername.setCaretColor(MAU_CHINH);
        txtUsername.setSelectionColor(MAU_BOI_DEN);
        txtUsername.setSelectedTextColor(MAU_CHU);
        txtUsername.setBorder(taoVienNhap(MAU_VIEN));
        txtUsername.setPreferredSize(new Dimension(0, 52));
        lblUser.setLabelFor(txtUsername);
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 23, 0);
        rightPanel.add(txtUsername, gbc);

        JLabel lblPass = new JLabel("Mật khẩu");
        lblPass.setFont(FONT_NHAN);
        lblPass.setForeground(MAU_CHU);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 7, 0);
        rightPanel.add(lblPass, gbc);

        txtPassword = new JPasswordField();
        txtPassword.setFont(FONT_O_NHAP);
        txtPassword.setBackground(MAU_O_NHAP);
        txtPassword.setForeground(MAU_CHU);
        txtPassword.setCaretColor(MAU_CHINH);
        txtPassword.setSelectionColor(MAU_BOI_DEN);
        txtPassword.setSelectedTextColor(MAU_CHU);
        txtPassword.setBorder(taoVienNhap(MAU_VIEN));
        txtPassword.setPreferredSize(new Dimension(0, 52));
        kyTuAnMatKhau = txtPassword.getEchoChar();
        lblPass.setLabelFor(txtPassword);
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 10, 0);
        rightPanel.add(txtPassword, gbc);

        JPanel pnlTienIch = new JPanel(new java.awt.BorderLayout());
        pnlTienIch.setBackground(MAU_NEN_PHAI);

        chkShowPass = new JCheckBox("Hiện mật khẩu");
        chkShowPass.setBackground(MAU_NEN_PHAI);
        chkShowPass.setForeground(MAU_CHU_PHU);
        chkShowPass.setFont(FONT_NOI_DUNG);
        chkShowPass.setFocusPainted(false);
        chkShowPass.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        pnlTienIch.add(chkShowPass, java.awt.BorderLayout.WEST);

        btnQuenMatKhau = new JButton("Quên mật khẩu?");
        btnQuenMatKhau.setFont(FONT_NOI_DUNG);
        btnQuenMatKhau.setForeground(MAU_CHINH);
        btnQuenMatKhau.setBackground(MAU_NEN_PHAI);
        btnQuenMatKhau.setBorderPainted(false);
        btnQuenMatKhau.setContentAreaFilled(false);
        btnQuenMatKhau.setFocusPainted(false);
        btnQuenMatKhau.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnQuenMatKhau.setBorder(new EmptyBorder(0, 0, 0, 0));
        btnQuenMatKhau.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        btnQuenMatKhau.setForeground(MAU_NUT_HOVER);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        btnQuenMatKhau.setForeground(MAU_CHINH);
                    }
                });
        pnlTienIch.add(btnQuenMatKhau, java.awt.BorderLayout.EAST);

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 25, 0);
        rightPanel.add(pnlTienIch, gbc);

        btnLogin = new JButton("ĐĂNG NHẬP");
        btnLogin.setFont(FONT_NUT);
        btnLogin.setBackground(MAU_CHINH);
        btnLogin.setForeground(MAU_CHU_TREN_NUT);
        btnLogin.setOpaque(true);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setBorder(new EmptyBorder(14, 18, 14, 18));
        btnLogin.setPreferredSize(new Dimension(0, 54));
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 16, 0);
        rightPanel.add(btnLogin, gbc);

        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(FONT_CHU_NHO);
        lblStatus.setForeground(MAU_CHU_PHU);
        lblStatus.setPreferredSize(new Dimension(0, 24));
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 0, 0);
        rightPanel.add(lblStatus, gbc);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        setContentPane(mainPanel);

        chkShowPass.addActionListener(this);
        btnQuenMatKhau.addActionListener(this);
        btnLogin.addActionListener(this);
        txtUsername.addActionListener(this);
        txtPassword.addActionListener(this);
        getRootPane().setDefaultButton(btnLogin);

        FocusAdapter doiVien =
                new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        ((JTextField) e.getSource()).setBorder(taoVienNhap(MAU_CHINH));
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        ((JTextField) e.getSource()).setBorder(taoVienNhap(MAU_VIEN));
                    }
                };
        txtUsername.addFocusListener(doiVien);
        txtPassword.addFocusListener(doiVien);
        btnLogin.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        btnLogin.setBackground(MAU_NUT_HOVER);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        btnLogin.setBackground(MAU_CHINH);
                    }
                });
        addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowOpened(WindowEvent e) {
                        txtUsername.requestFocusInWindow();
                    }
                });

        try {
            ConnectDB.getInstance().connect();
            lblStatus.setForeground(MAU_CHINH);
            lblStatus.setText("Kết nối SQL thành công.");
        } catch (SQLException e) {
            e.printStackTrace();
            lblStatus.setForeground(MAU_LOI);
            lblStatus.setText("Chưa kết nối SQL. Bấm Đăng nhập để thử lại.");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(chkShowPass)) {
            txtPassword.setEchoChar(chkShowPass.isSelected() ? (char) 0 : kyTuAnMatKhau);
        } else if (o.equals(btnQuenMatKhau)) {
            hienThiDialogQuenMatKhau();
        } else if (o.equals(btnLogin) || o.equals(txtPassword)) {
            xulyDangNhap();
        } else if (o.equals(txtUsername)) {
            txtPassword.requestFocusInWindow();
        }
    }

    private void xulyDangNhap() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng nhập tài khoản và mật khẩu!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            if (user.isEmpty()) txtUsername.requestFocusInWindow();
            else txtPassword.requestFocusInWindow();
            return;
        }

        btnLogin.setEnabled(false);
        try {
            if (ConnectDB.con == null || ConnectDB.con.isClosed()) {
                ConnectDB.getInstance().connect();
            }
            TaiKhoan_DAO dao = new TaiKhoan_DAO();
            TaiKhoan tk = dao.kiemTraDangNhap(user, pass);
            if (tk != null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Đăng nhập thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                new MainFrame(tk).setVisible(true);
                dispose();
            } else {
                lblStatus.setForeground(MAU_LOI);
                lblStatus.setText("Tài khoản hoặc mật khẩu không chính xác.");
                JOptionPane.showMessageDialog(
                        this,
                        "Tài khoản hoặc mật khẩu không chính xác!",
                        "Lỗi đăng nhập",
                        JOptionPane.ERROR_MESSAGE);
                txtPassword.setText("");
                txtPassword.requestFocusInWindow();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lblStatus.setForeground(MAU_LOI);
            lblStatus.setText("Không thể kết nối hoặc truy vấn SQL.");
            JOptionPane.showMessageDialog(
                    this,
                    "Lỗi kết nối hoặc truy vấn SQL:\n" + e.getMessage(),
                    "Lỗi database",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            btnLogin.setEnabled(true);
        }
    }

    private void hienThiDialogQuenMatKhau() {
        JDialog dialog = new JDialog(this, "Khôi phục mật khẩu - GreenSmart", true);
        dialog.setSize(520, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel pnlChinh = new JPanel(new java.awt.BorderLayout(0, 16));
        pnlChinh.setBackground(MAU_NEN_PHAI);
        pnlChinh.setBorder(new EmptyBorder(24, 32, 24, 32));

        // Header
        JPanel pnlHeader = new JPanel(new GridLayout(2, 1, 0, 4));
        pnlHeader.setOpaque(false);
        JLabel lblTieuDeDlg = new JLabel("KHÔI PHỤC MẬT KHẨU", SwingConstants.CENTER);
        lblTieuDeDlg.setFont(GiaoDien.phong(Font.BOLD, 22));
        lblTieuDeDlg.setForeground(MAU_CHINH);
        JLabel lblMoTaDlg = new JLabel("Nhập thông tin xác thực để đặt lại mật khẩu mới", SwingConstants.CENTER);
        lblMoTaDlg.setFont(FONT_CHU_NHO);
        lblMoTaDlg.setForeground(MAU_CHU_PHU);
        pnlHeader.add(lblTieuDeDlg);
        pnlHeader.add(lblMoTaDlg);
        pnlChinh.add(pnlHeader, java.awt.BorderLayout.NORTH);

        // Form
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.gridx = 0;

        JTextField txtDlgUser = new JTextField(txtUsername.getText().trim());
        txtDlgUser.setFont(FONT_O_NHAP);
        txtDlgUser.setPreferredSize(new Dimension(0, 42));
        txtDlgUser.setBorder(taoVienNhap(MAU_VIEN));

        JTextField txtDlgSdt = new JTextField();
        txtDlgSdt.setFont(FONT_O_NHAP);
        txtDlgSdt.setPreferredSize(new Dimension(0, 42));
        txtDlgSdt.setBorder(taoVienNhap(MAU_VIEN));

        JTextField txtDlgCCCD = new JTextField();
        txtDlgCCCD.setFont(FONT_O_NHAP);
        txtDlgCCCD.setPreferredSize(new Dimension(0, 42));
        txtDlgCCCD.setBorder(taoVienNhap(MAU_VIEN));

        JPasswordField txtDlgPassMoi = new JPasswordField();
        txtDlgPassMoi.setFont(FONT_O_NHAP);
        txtDlgPassMoi.setPreferredSize(new Dimension(0, 42));
        txtDlgPassMoi.setBorder(taoVienNhap(MAU_VIEN));

        JPasswordField txtDlgPassXacNhan = new JPasswordField();
        txtDlgPassXacNhan.setFont(FONT_O_NHAP);
        txtDlgPassXacNhan.setPreferredSize(new Dimension(0, 42));
        txtDlgPassXacNhan.setBorder(taoVienNhap(MAU_VIEN));

        JCheckBox chkDlgHienPass = new JCheckBox("Hiện mật khẩu");
        chkDlgHienPass.setFont(FONT_CHU_NHO);
        chkDlgHienPass.setForeground(MAU_CHU_PHU);
        chkDlgHienPass.setOpaque(false);
        chkDlgHienPass.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        char echoChar = txtDlgPassMoi.getEchoChar();
        chkDlgHienPass.addActionListener(ev -> {
            char ec = chkDlgHienPass.isSelected() ? (char) 0 : echoChar;
            txtDlgPassMoi.setEchoChar(ec);
            txtDlgPassXacNhan.setEchoChar(ec);
        });

        int row = 0;
        themHangForm(pnlForm, c, row++, "Tên đăng nhập:", txtDlgUser);
        themHangForm(pnlForm, c, row++, "Số điện thoại:", txtDlgSdt);
        themHangForm(pnlForm, c, row++, "Số CCCD:", txtDlgCCCD);
        themHangForm(pnlForm, c, row++, "Mật khẩu mới (tối thiểu 8 ký tự):", txtDlgPassMoi);
        themHangForm(pnlForm, c, row++, "Xác nhận mật khẩu mới:", txtDlgPassXacNhan);

        c.gridy = row * 2;
        c.insets = new Insets(6, 0, 8, 0);
        pnlForm.add(chkDlgHienPass, c);

        pnlChinh.add(pnlForm, java.awt.BorderLayout.CENTER);

        // Buttons
        JPanel pnlNut = new JPanel(new GridLayout(1, 2, 12, 0));
        pnlNut.setOpaque(false);
        pnlNut.setPreferredSize(new Dimension(0, 48));

        JButton btnXacNhanDlg = new JButton("ĐẶT LẠI MẬT KHẨU");
        btnXacNhanDlg.setFont(FONT_NUT);
        btnXacNhanDlg.setBackground(MAU_CHINH);
        btnXacNhanDlg.setForeground(MAU_CHU_TREN_NUT);
        btnXacNhanDlg.setOpaque(true);
        btnXacNhanDlg.setFocusPainted(false);
        btnXacNhanDlg.setBorderPainted(false);
        btnXacNhanDlg.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton btnHuyDlg = new JButton("HỦY BỎ");
        btnHuyDlg.setFont(FONT_NUT);
        btnHuyDlg.setBackground(Color.WHITE);
        btnHuyDlg.setForeground(MAU_CHU);
        btnHuyDlg.setBorder(BorderFactory.createLineBorder(MAU_VIEN, 1));
        btnHuyDlg.setFocusPainted(false);
        btnHuyDlg.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnHuyDlg.addActionListener(ev -> dialog.dispose());

        btnXacNhanDlg.addActionListener(ev -> {
            String u = txtDlgUser.getText().trim();
            String s = txtDlgSdt.getText().trim();
            String cccdVal = txtDlgCCCD.getText().trim();
            String pMoi = new String(txtDlgPassMoi.getPassword());
            String pXacNhan = new String(txtDlgPassXacNhan.getPassword());

            if (u.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập tên đăng nhập!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                txtDlgUser.requestFocusInWindow();
                return;
            }
            if (s.isEmpty() || !s.matches("^0[35789]\\d{8}$")) {
                JOptionPane.showMessageDialog(dialog, "Số điện thoại không hợp lệ (10 số, đầu 03/05/07/08/09)!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                txtDlgSdt.requestFocusInWindow();
                return;
            }
            if (cccdVal.isEmpty() || !cccdVal.matches("^\\d{12}$")) {
                JOptionPane.showMessageDialog(dialog, "Số CCCD phải gồm đúng 12 chữ số!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                txtDlgCCCD.requestFocusInWindow();
                return;
            }
            if (pMoi.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập mật khẩu mới!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                txtDlgPassMoi.requestFocusInWindow();
                return;
            }
            if (pMoi.length() < 8) {
                JOptionPane.showMessageDialog(dialog, "Mật khẩu mới phải có ít nhất 8 ký tự!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                txtDlgPassMoi.requestFocusInWindow();
                return;
            }
            if (!pMoi.equals(pXacNhan)) {
                JOptionPane.showMessageDialog(dialog, "Xác nhận mật khẩu mới không khớp!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                txtDlgPassXacNhan.requestFocusInWindow();
                return;
            }

            try {
                if (ConnectDB.con == null || ConnectDB.con.isClosed()) {
                    ConnectDB.getInstance().connect();
                }
                TaiKhoan_DAO dao = new TaiKhoan_DAO();
                boolean ok = dao.datLaiMatKhauQuen(u, s, cccdVal, pMoi);
                if (ok) {
                    JOptionPane.showMessageDialog(dialog, "Đặt lại mật khẩu thành công! Vui lòng đăng nhập với mật khẩu mới.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    txtUsername.setText(u);
                    txtPassword.setText("");
                    txtPassword.requestFocusInWindow();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Không thể cập nhật mật khẩu. Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
            }
        });

        pnlNut.add(btnXacNhanDlg);
        pnlNut.add(btnHuyDlg);
        pnlChinh.add(pnlNut, java.awt.BorderLayout.SOUTH);

        dialog.setContentPane(pnlChinh);
        dialog.setVisible(true);
    }

    private void themHangForm(JPanel pnl, GridBagConstraints c, int row, String nhan, JComponent comp) {
        JLabel lbl = new JLabel(nhan);
        lbl.setFont(FONT_NHAN);
        lbl.setForeground(MAU_CHU);
        c.gridy = row * 2;
        c.insets = new Insets(row == 0 ? 0 : 8, 0, 4, 0);
        pnl.add(lbl, c);

        c.gridy = row * 2 + 1;
        c.insets = new Insets(0, 0, 4, 0);
        pnl.add(comp, c);
    }

    private Border taoVienNhap(Color mauVien) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(mauVien, 1), new EmptyBorder(10, 14, 10, 14));
    }
}
