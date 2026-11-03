package gui;

import connectDB.ConnectDB;

import dao.TaiKhoan_DAO;

import entity.TaiKhoan;

import util.GiaoDien;

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
    private JLabel lblStatus;
    private char kyTuAnMatKhau;

    public DangNhap_GUI() {
        GiaoDien.apDung();
        setTitle("GreenSmart - ÄÄƒng nháº­p");
        setSize(1100, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 0, 0));

        // Cá»™t trÃ¡i: logo chiáº¿c lÃ¡ vÃ  tÃªn á»©ng dá»¥ng.
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

        JLabel lblMoTa = new JLabel("QUáº¢N LÃ Cá»¬A HÃ€NG ÄIá»†N MÃY", SwingConstants.CENTER);
        lblMoTa.setFont(FONT_CHU_NHO);
        lblMoTa.setForeground(MAU_CHU_PHU);
        trai.gridy = 2;
        trai.insets = new Insets(0, 0, 0, 0);
        leftPanel.add(lblMoTa, trai);

        // Cá»™t pháº£i: form Ä‘Äƒng nháº­p. GridBagLayout giá»¯ cÃ¡c Ã´ cÃ¹ng chiá»u rá»™ng.
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(MAU_NEN_PHAI);
        rightPanel.setBorder(new EmptyBorder(40, 56, 40, 56));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.LINE_START;

        JLabel lblTitle = new JLabel("ChÃ o má»«ng trá»Ÿ láº¡i!");
        lblTitle.setFont(FONT_TIEU_DE);
        lblTitle.setForeground(MAU_CHU);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 7, 0);
        rightPanel.add(lblTitle, gbc);

        JLabel lblSubtitle = new JLabel("ÄÄƒng nháº­p Ä‘á»ƒ tiáº¿p tá»¥c");
        lblSubtitle.setFont(FONT_NOI_DUNG);
        lblSubtitle.setForeground(MAU_CHU_PHU);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 32, 0);
        rightPanel.add(lblSubtitle, gbc);

        JLabel lblUser = new JLabel("TÃ i khoáº£n");
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

        JLabel lblPass = new JLabel("Máº­t kháº©u");
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

        chkShowPass = new JCheckBox("Hiá»‡n máº­t kháº©u");
        chkShowPass.setBackground(MAU_NEN_PHAI);
        chkShowPass.setForeground(MAU_CHU_PHU);
        chkShowPass.setFont(FONT_NOI_DUNG);
        chkShowPass.setFocusPainted(false);
        chkShowPass.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 25, 0);
        rightPanel.add(chkShowPass, gbc);

        btnLogin = new JButton("ÄÄ‚NG NHáº¬P");
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
            lblStatus.setText("Káº¿t ná»‘i SQL thÃ nh cÃ´ng.");
        } catch (SQLException e) {
            e.printStackTrace();
            lblStatus.setForeground(MAU_LOI);
            lblStatus.setText("ChÆ°a káº¿t ná»‘i SQL. Báº¥m ÄÄƒng nháº­p Ä‘á»ƒ thá»­ láº¡i.");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(chkShowPass)) {
            txtPassword.setEchoChar(chkShowPass.isSelected() ? (char) 0 : kyTuAnMatKhau);
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
                    "Vui lÃ²ng nháº­p tÃ i khoáº£n vÃ  máº­t kháº©u!",
                    "ThÃ´ng bÃ¡o",
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
                        "ÄÄƒng nháº­p thÃ nh cÃ´ng!",
                        "ThÃ´ng bÃ¡o",
                        JOptionPane.INFORMATION_MESSAGE);
                new MainFrame(tk).setVisible(true);
                dispose();
            } else {
                lblStatus.setForeground(MAU_LOI);
                lblStatus.setText("TÃ i khoáº£n hoáº·c máº­t kháº©u khÃ´ng chÃ­nh xÃ¡c.");
                JOptionPane.showMessageDialog(
                        this,
                        "TÃ i khoáº£n hoáº·c máº­t kháº©u khÃ´ng chÃ­nh xÃ¡c!",
                        "Lá»—i Ä‘Äƒng nháº­p",
                        JOptionPane.ERROR_MESSAGE);
                txtPassword.setText("");
                txtPassword.requestFocusInWindow();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lblStatus.setForeground(MAU_LOI);
            lblStatus.setText("KhÃ´ng thá»ƒ káº¿t ná»‘i hoáº·c truy váº¥n SQL.");
            JOptionPane.showMessageDialog(
                    this,
                    "Lá»—i káº¿t ná»‘i hoáº·c truy váº¥n SQL:\n" + e.getMessage(),
                    "Lá»—i database",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            btnLogin.setEnabled(true);
        }
    }

    private Border taoVienNhap(Color mauVien) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(mauVien, 1), new EmptyBorder(10, 14, 10, 14));
    }
}
