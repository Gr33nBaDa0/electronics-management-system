package util;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Path2D;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.JTextComponent;

/**
 * Giao diện chung của GreenSmart: bảng màu, phông chữ, nút, bảng, tiêu đề và phím tắt. Mọi màn hình
 * lấy màu từ đây nên muốn đổi diện mạo chỉ cần sửa các hằng số bên dưới.
 */
public final class GiaoDien {
    // Màu dùng chung, cùng phong cách với màn đăng nhập. Navi đúng mã #1B5E20.
    public static final Color PRIMARY = new Color(0x1B5E20);
    public static final Color PRIMARY_DARK = new Color(0x144618);
    public static final Color PRIMARY_SOFT = new Color(0xE5EFE8);
    public static final Color DARK = new Color(0x1B5E20);
    public static final Color DARK_HOVER = new Color(0x2E7D32);
    public static final Color MENU_ACTIVE = new Color(0x388E3C);
    public static final Color MENU_TEXT = new Color(0xE5EFE8);
    public static final Color MENU_MUTED = new Color(0xB9D8BB);
    public static final Color ACCENT = new Color(0xF59E0B);
    public static final Color BACKGROUND = new Color(0xF5F7F6);
    public static final Color FORM_BACKGROUND = new Color(0xF8FBF8);
    public static final Color SURFACE = Color.WHITE;
    public static final Color TEXT = new Color(0x243B2E);
    public static final Color MUTED = new Color(0x627368);
    public static final Color BORDER = new Color(0xCEDDD2);
    public static final Color ERROR = new Color(0xB42318);
    public static final Color DANGER = new Color(0xDC2626);
    public static final Color SUCCESS = new Color(0x15803D);
    public static final Color ROW_ALT = new Color(0xF8FBF8);
    public static final Color SELECT = new Color(0xDCEFE1);
    public static final Color DISABLED = new Color(0xEDF1EE);
    public static final int CO_CHU = 14;
    public static final int CAO_O_NHAP = 36;
    public static final String FONT_UU_TIEN = "Segoe UI";

    private static String hoPhong;
    private static boolean daApDung;

    public static Font phong(int kieu, int co) {
        return new Font(hoPhong(), kieu, co);
    }

    private static String hoPhong() {
        if (hoPhong == null) {
            String uuTien = FONT_UU_TIEN;
            hoPhong = "SansSerif";
            for (String f :
                    GraphicsEnvironment.getLocalGraphicsEnvironment()
                            .getAvailableFontFamilyNames()) {
                if (f.equalsIgnoreCase(uuTien)) {
                    hoPhong = f;
                    break;
                }
            }
        }
        return hoPhong;
    }

    /** Gọi một lần trước khi tạo cửa sổ đầu tiên. */
    public static void apDung() {
        if (daApDung) return;
        daApDung = true;
        UIManager.put("control", BACKGROUND);
        UIManager.put("nimbusBase", PRIMARY_DARK);
        UIManager.put("nimbusBlueGrey", BORDER);
        UIManager.put("nimbusFocus", ACCENT);
        UIManager.put("nimbusSelectionBackground", PRIMARY);
        UIManager.put("nimbusLightBackground", SURFACE);
        UIManager.put("text", TEXT);
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            // Dùng giao diện hiện có nếu hệ điều hành không hỗ trợ.
        }
        int co = CO_CHU;
        Font f = phong(Font.PLAIN, co);
        for (Object k : Collections.list(UIManager.getDefaults().keys())) {
            if (k.toString().endsWith(".font")) UIManager.put(k, f);
        }
        UIManager.put("defaultFont", f);
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("OptionPane.background", BACKGROUND);
        UIManager.put("Label.foreground", TEXT);
        UIManager.put("TextField.background", SURFACE);
        UIManager.put("PasswordField.background", SURFACE);
        UIManager.put("TextField.selectionBackground", SELECT);
        UIManager.put("PasswordField.selectionBackground", SELECT);
        UIManager.put("ComboBox.background", SURFACE);
        UIManager.put("ComboBox.selectionBackground", SELECT);
        UIManager.put("ComboBox.selectionForeground", TEXT);
        UIManager.put("Table.selectionBackground", SELECT);
        UIManager.put("Table.selectionForeground", TEXT);
        UIManager.put("ToolTip.background", DARK);
        UIManager.put("ToolTip.foreground", Color.WHITE);
        UIManager.put("SplitPane.dividerSize", 10);
    }

    // ===== Nút bấm =====
    private static final int CHINH = 0, PHU = 1, NGUY_HIEM = 2, NOI_BAT = 3;

    private static int kieuNut(String t) {
        String s = t.toLowerCase();
        if (s.contains("xóa")
                || s.contains("hủy")
                || s.contains("từ chối")
                || s.contains("bỏ dòng")) return NGUY_HIEM;
        String[] chinh = {
            "lưu", "thanh toán", "xác nhận", "duyệt", "phân công", "mở ca", "đối soát",
            "lập báo cáo", "đăng nhập", "thêm", "sao lưu", "lấy", "xuất", "in "
        };
        for (String c : chinh) if (s.startsWith(c)) return CHINH;
        return PHU;
    }

    public static JButton nut(String text) {
        return new NutBo(text, kieuNut(text));
    }

    public static JButton nutChinh(String text) {
        return new NutBo(text, CHINH);
    }

    public static JButton nutVang(String text) {
        return new NutBo(text, NOI_BAT);
    }

    public static JButton nutNho(String text) {
        JButton b = new NutBo(text, PHU);
        b.setPreferredSize(new Dimension(30, 28));
        b.setFont(phong(Font.BOLD, 13));
        return b;
    }

    /**
     * Nút tự vẽ: bo góc, đổi màu khi rê chuột/nhấn/vô hiệu hóa, không phụ thuộc giao diện hệ điều
     * hành.
     */
    private static final class NutBo extends JButton {
        private final int kieu;

        NutBo(String text, int kieu) {
            super(text);
            this.kieu = kieu;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setFont(phong(Font.BOLD, 13));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setToolTipText(text);
            FontMetrics fm = getFontMetrics(getFont());
            setPreferredSize(new Dimension(Math.max(94, fm.stringWidth(text) + 28), 38));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            boolean bat = isEnabled();
            boolean tren = bat && getModel().isRollover();
            boolean nhan = bat && getModel().isArmed();
            Color nen, chu, vien = null;
            if (!bat) {
                nen = DISABLED;
                chu = new Color(0x9CA3AF);
            } else if (kieu == PHU) {
                nen = nhan ? SELECT : tren ? FORM_BACKGROUND : SURFACE;
                chu = PRIMARY_DARK;
                vien = tren || nhan ? PRIMARY : BORDER;
            } else {
                Color goc = kieu == NGUY_HIEM ? DANGER : kieu == NOI_BAT ? ACCENT : PRIMARY;
                nen = nhan ? goc.darker().darker() : tren ? goc.darker() : goc;
                chu = kieu == NOI_BAT ? PRIMARY_DARK : Color.WHITE;
            }
            g.setColor(nen);
            g.fillRoundRect(0, 0, w - 1, h - 1, 12, 12);
            if (vien != null) {
                g.setColor(vien);
                g.setStroke(new BasicStroke(1.2f));
                g.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);
            }
            if (hasFocus() && bat) {
                g.setColor(ACCENT);
                g.setStroke(new BasicStroke(2f));
                g.drawRoundRect(2, 2, w - 5, h - 5, 9, 9);
            }
            g.setFont(getFont());
            FontMetrics fm = g.getFontMetrics();
            g.setColor(chu);
            String t = getText();
            g.drawString(t, (w - fm.stringWidth(t)) / 2, (h - fm.getHeight()) / 2 + fm.getAscent());
            g.dispose();
        }
    }

    /** Nút menu bên trái: nền tối, vạch nhấn cho mục đang mở, gợi ý phím tắt ở bên phải. */
    public static final class NutMenu extends JButton {
        private boolean hoatDong;
        private final String phim;

        public NutMenu(String text, String phim) {
            super(text);
            this.phim = phim == null ? "" : phim;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setHorizontalAlignment(SwingConstants.LEFT);
            setFont(phong(Font.PLAIN, 14));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(216, 34));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
            setMinimumSize(new Dimension(200, 34));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setToolTipText(text + (this.phim.isEmpty() ? "" : " (" + this.phim + ")"));
        }

        public void datHoatDong(boolean b) {
            hoatDong = b;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            if (hoatDong) {
                g.setColor(MENU_ACTIVE);
                g.fillRoundRect(0, 2, w, h - 4, 10, 10);
                g.setColor(ACCENT);
                g.fillRoundRect(0, 8, 4, h - 16, 4, 4);
            } else if (getModel().isRollover() || getModel().isArmed()) {
                g.setColor(DARK_HOVER);
                g.fillRoundRect(0, 2, w, h - 4, 10, 10);
            }
            FontMetrics fm = g.getFontMetrics(getFont());
            int y = (h - fm.getHeight()) / 2 + fm.getAscent();
            g.setFont(hoatDong ? getFont().deriveFont(Font.BOLD) : getFont());
            g.setColor(hoatDong ? Color.WHITE : MENU_TEXT);
            int rongPhim =
                    phim.isEmpty()
                            ? 0
                            : g.getFontMetrics(phong(Font.PLAIN, 11)).stringWidth(phim) + 18;
            int rongChu = Math.max(30, w - 24 - rongPhim);
            String nhan = getText();
            FontMetrics fmChu = g.getFontMetrics();
            if (fmChu.stringWidth(nhan) > rongChu) {
                while (!nhan.isEmpty() && fmChu.stringWidth(nhan + "…") > rongChu)
                    nhan = nhan.substring(0, nhan.length() - 1);
                nhan += "…";
            }
            g.drawString(nhan, 12, y);
            if (!phim.isEmpty()) {
                Font nho = phong(Font.PLAIN, 11);
                g.setFont(nho);
                g.setColor(hoatDong ? Color.WHITE : MENU_MUTED);
                g.drawString(phim, w - g.getFontMetrics().stringWidth(phim) - 10, y);
            }
            g.dispose();
        }
    }

    // ===== Bảng dữ liệu =====
    public static JTable bang(DefaultTableModel m) {
        JTable t =
                new JTable(m) {
                    @Override
                    public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                        Component c = super.prepareRenderer(r, row, col);
                        boolean chon = isRowSelected(row);
                        c.setBackground(chon ? SELECT : row % 2 == 0 ? SURFACE : ROW_ALT);
                        c.setForeground(TEXT);
                        if (c instanceof JLabel) {
                            JLabel l = (JLabel) c;
                            Object v = getValueAt(row, col);
                            boolean so =
                                    v instanceof Number
                                            || (v instanceof String
                                                    && ((String) v)
                                                            .matches(
                                                                    "-?\\d{1,3}(,\\d{3})+(\\.\\d+)?|-?\\d+\\.\\d+|-?(0|[1-9]\\d{0,8})"));
                            l.setHorizontalAlignment(
                                    so ? SwingConstants.RIGHT : SwingConstants.LEFT);
                            l.setBorder(new EmptyBorder(0, 10, 0, 10));
                        }
                        return c;
                    }
                };
        t.setRowHeight(38);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setFillsViewportHeight(true);
        t.setBackground(SURFACE);
        t.setGridColor(BORDER);
        t.setSelectionBackground(SELECT);
        t.setSelectionForeground(TEXT);
        t.getTableHeader().setReorderingAllowed(false);
        t.getTableHeader().setPreferredSize(new Dimension(100, 40));
        t.getTableHeader()
                .setDefaultRenderer(
                        new DefaultTableCellRenderer() {
                            @Override
                            public Component getTableCellRendererComponent(
                                    JTable tb, Object v, boolean s, boolean f, int r, int c) {
                                JLabel l = new JLabel(v == null ? "" : v.toString());
                                l.setOpaque(true);
                                l.setBackground(PRIMARY_SOFT);
                                l.setForeground(PRIMARY);
                                l.setFont(phong(Font.BOLD, 13));
                                l.setBorder(
                                        BorderFactory.createCompoundBorder(
                                                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                                                new EmptyBorder(0, 10, 0, 10)));
                                return l;
                            }
                        });
        m.addTableModelListener(e -> chinhCot(t));
        // Khung viền gọn cho thanh cuộn chứa bảng, tạo ngay khi bảng được đặt vào JScrollPane.
        t.addHierarchyListener(
                e -> {
                    if (t.getParent() instanceof JViewport
                            && t.getParent().getParent() instanceof JScrollPane) {
                        JScrollPane sp = (JScrollPane) t.getParent().getParent();
                        sp.setBorder(BorderFactory.createLineBorder(BORDER));
                        sp.getViewport().setBackground(SURFACE);
                    }
                });
        chinhCot(t);
        return t;
    }

    /** Đặt độ rộng cột theo tiêu đề và nội dung (tối đa 200 dòng đầu) để chữ không bị cắt. */
    public static void chinhCot(JTable t) {
        for (int c = 0; c < t.getColumnCount(); c++) {
            javax.swing.table.TableColumn col = t.getColumnModel().getColumn(c);
            Component h =
                    t.getTableHeader()
                            .getDefaultRenderer()
                            .getTableCellRendererComponent(
                                    t, col.getHeaderValue(), false, false, -1, c);
            int w = h.getPreferredSize().width + 8;
            int n = Math.min(t.getRowCount(), 200);
            for (int r = 0; r < n; r++) {
                Component x = t.prepareRenderer(t.getCellRenderer(r, c), r, c);
                w = Math.max(w, x.getPreferredSize().width);
            }
            int rong = Math.max(50, Math.min(w + 12, 460));
            col.setPreferredWidth(rong);
            // Cột ngắn (mã, số, tiền) giữ nguyên đủ chữ; chỉ cột dài như tên/địa chỉ mới co lại khi
            // hẹp.
            col.setMinWidth(rong <= 170 ? rong : 60);
        }
    }

    // ===== Thành phần nhỏ =====
    public static JLabel tieuDe(String text) {
        JLabel l = new JLabel(text);
        l.setFont(phong(Font.BOLD, 24));
        l.setForeground(PRIMARY_DARK);
        l.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(), new EmptyBorder(0, 0, 4, 0)));
        return l;
    }

    public static JPanel dong(Component... cs) {
        JPanel p = new JPanel(new DongTuNgat());
        p.setOpaque(false);
        for (Component c : cs) p.add(c);
        return p;
    }

    // ===== Bố cục dùng chung cho các GUI =====
    public static void boCuc(JPanel p) {
        p.setLayout(new BorderLayout(10, 10));
        p.setBackground(BACKGROUND);
        p.setBorder(new EmptyBorder(12, 12, 12, 12));
    }

    public static JPanel dauTrang(String ten, String moTa) {
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.setOpaque(false);
        p.add(tieuDe(ten), BorderLayout.NORTH);
        p.add(ghiChu(moTa), BorderLayout.CENTER);
        return p;
    }

    public static JTextArea ghiChu(String text) {
        JTextArea a = new JTextArea(text);
        a.setEditable(false);
        a.setOpaque(false);
        a.setFocusable(false);
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setFont(phong(Font.PLAIN, 13));
        a.setForeground(MUTED);
        a.setPreferredSize(new Dimension(100, 32));
        return a;
    }

    /** Nhãn ở trên ô nhập, các ô trong cùng hàng có chiều rộng bằng nhau. */
    public static JPanel form(int soCot, Object... nhanVaO) {
        if (nhanVaO.length % 2 != 0) throw new IllegalArgumentException("Thiếu nhãn hoặc ô nhập.");
        JPanel p = new JPanel(new GridLayout(0, soCot, 14, 10));
        p.setOpaque(false);
        for (int i = 0; i < nhanVaO.length; i += 2) {
            JPanel o = new JPanel(new BorderLayout(0, 5));
            o.setOpaque(false);
            JLabel l = new JLabel(String.valueOf(nhanVaO[i]));
            l.setFont(phong(Font.BOLD, 13));
            l.setForeground(TEXT);
            Component c = (Component) nhanVaO[i + 1];
            trangTri(c);
            o.add(l, BorderLayout.NORTH);
            o.add(c, BorderLayout.CENTER);
            p.add(o);
        }
        int soO = nhanVaO.length / 2;
        while (soO++ % soCot != 0) {
            JPanel rong = new JPanel();
            rong.setOpaque(false);
            p.add(rong);
        }
        return p;
    }

    /** Khối trắng: tiêu đề nhỏ, nội dung và hàng thao tác ở cuối. */
    public static JPanel the(String ten, Component noiDung, Component thaoTac) {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(SURFACE);
        p.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER), new EmptyBorder(12, 12, 12, 12)));
        if (ten != null && !ten.isEmpty()) {
            JLabel l = new JLabel(ten);
            l.setFont(phong(Font.BOLD, 15));
            l.setForeground(PRIMARY);
            p.add(l, BorderLayout.NORTH);
        }
        if (noiDung != null) p.add(noiDung, BorderLayout.CENTER);
        if (thaoTac != null) p.add(thaoTac, BorderLayout.SOUTH);
        return p;
    }

    public static JLabel nhan(String text, int co, boolean dam, Color mau) {
        JLabel l = new JLabel(text);
        l.setFont(phong(dam ? Font.BOLD : Font.PLAIN, co));
        l.setForeground(mau);
        return l;
    }

    public static JPanel soLieu(String ten, JLabel giaTri, String moTa) {
        giaTri.setFont(phong(Font.BOLD, 23));
        giaTri.setForeground(PRIMARY);
        JPanel p = the(ten, giaTri, nhan(moTa, 11, false, MUTED));
        p.setPreferredSize(new Dimension(180, 104));
        return p;
    }

    public static DefaultTableModel model(String... cot) {
        return new DefaultTableModel(cot, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
    }

    public static JPanel doc(Component... thanhPhan) {
        JPanel p = new JPanel();
        p.setLayout(new javax.swing.BoxLayout(p, javax.swing.BoxLayout.Y_AXIS));
        p.setOpaque(false);
        for (int i = 0; i < thanhPhan.length; i++) {
            Component c = thanhPhan[i];
            if (c instanceof JComponent) ((JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
            p.add(c);
            if (i < thanhPhan.length - 1) p.add(javax.swing.Box.createVerticalStrut(12));
        }
        return p;
    }

    public static JScrollPane cuon(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(BorderFactory.createLineBorder(BORDER));
        sp.setBackground(SURFACE);
        sp.getViewport().setBackground(SURFACE);
        sp.getVerticalScrollBar().setUnitIncrement(24);
        return sp;
    }

    public static JScrollPane cuonForm(Component form) {
        class NoiDungForm extends JPanel implements javax.swing.Scrollable {
            NoiDungForm() {
                super(new BorderLayout());
                setBackground(SURFACE);
                add(form, BorderLayout.NORTH);
            }

            public Dimension getPreferredScrollableViewportSize() {
                return getPreferredSize();
            }

            public int getScrollableUnitIncrement(java.awt.Rectangle r, int o, int d) {
                return 24;
            }

            public int getScrollableBlockIncrement(java.awt.Rectangle r, int o, int d) {
                return 120;
            }

            public boolean getScrollableTracksViewportWidth() {
                return true;
            }

            public boolean getScrollableTracksViewportHeight() {
                return false;
            }
        }
        JScrollPane s = cuon(new NoiDungForm());
        s.setBorder(null);
        s.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return s;
    }

    public static JSplitPane tach(int kieu, Component a, Component b, double tyLe) {
        JSplitPane s = new JSplitPane(kieu, a, b);
        s.setBorder(null);
        s.setBackground(BACKGROUND);
        s.setDividerSize(12);
        s.setResizeWeight(tyLe);
        s.setContinuousLayout(true);
        s.setOneTouchExpandable(true);
        s.addHierarchyListener(
                e -> {
                    if (s.isShowing() && s.getClientProperty("greensmart.chia") == null) {
                        s.putClientProperty("greensmart.chia", Boolean.TRUE);
                        javax.swing.SwingUtilities.invokeLater(() -> s.setDividerLocation(tyLe));
                    }
                });
        return s;
    }

    /** Chỉ trang trí thành phần; dữ liệu vẫn được GUI nạp bằng model.addRow. */
    public static void trangTri(Component c) {
        if (c instanceof JTextComponent && !(c instanceof JTextArea)) {
            JTextComponent t = (JTextComponent) c;
            t.setFont(phong(Font.PLAIN, 14));
            t.setForeground(TEXT);
            t.setCaretColor(PRIMARY);
            t.setSelectionColor(SELECT);
            t.setSelectedTextColor(TEXT);
            t.setBackground(t.isEditable() ? SURFACE : DISABLED);
            Dimension d = t.getPreferredSize();
            t.setPreferredSize(new Dimension(d.width, CAO_O_NHAP));
            t.setMinimumSize(new Dimension(50, CAO_O_NHAP));
            if (t.getClientProperty("greensmart.oinhap") == null) {
                t.putClientProperty("greensmart.oinhap", Boolean.TRUE);
                t.setBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(BORDER),
                                new EmptyBorder(0, 10, 0, 10)));
                t.addFocusListener(
                        new FocusAdapter() {
                            @Override
                            public void focusGained(FocusEvent e) {
                                t.setBorder(
                                        BorderFactory.createCompoundBorder(
                                                BorderFactory.createLineBorder(PRIMARY),
                                                new EmptyBorder(0, 10, 0, 10)));
                            }

                            @Override
                            public void focusLost(FocusEvent e) {
                                t.setBorder(
                                        BorderFactory.createCompoundBorder(
                                                BorderFactory.createLineBorder(BORDER),
                                                new EmptyBorder(0, 10, 0, 10)));
                            }
                        });
                t.addPropertyChangeListener(
                        "editable", e -> t.setBackground(t.isEditable() ? SURFACE : DISABLED));
            }
        } else if (c instanceof JComboBox) {
            JComboBox<?> cb = (JComboBox<?>) c;
            cb.setFont(phong(Font.PLAIN, 14));
            cb.setBackground(SURFACE);
            cb.setForeground(TEXT);
            Dimension d = cb.getPreferredSize();
            cb.setPreferredSize(new Dimension(Math.max(140, d.width), CAO_O_NHAP));
            cb.setMinimumSize(new Dimension(80, CAO_O_NHAP));
        } else if (c instanceof JSpinner) {
            JSpinner sp = (JSpinner) c;
            sp.setFont(phong(Font.PLAIN, 14));
            sp.setPreferredSize(new Dimension(90, CAO_O_NHAP));
        } else if (c instanceof JCheckBox) {
            JCheckBox cb = (JCheckBox) c;
            cb.setOpaque(false);
            cb.setForeground(MUTED);
            cb.setFont(phong(Font.PLAIN, 13));
        } else if (c instanceof JTabbedPane) {
            c.setFont(phong(Font.BOLD, 14));
            c.setBackground(SURFACE);
            c.setForeground(PRIMARY);
        }
        if (c instanceof Container) {
            for (Component x : ((Container) c).getComponents()) trangTri(x);
        }
    }

    /** Dùng PNG chiếc lá đã có; nếu thiếu ảnh vẫn hiện biểu tượng lá, không cần thêm resource. */
    public static Icon logo(int kichThuoc) {
        URL u = GiaoDien.class.getResource("/resource/logo_greensmart.png");
        if (u == null) u = GiaoDien.class.getResource("/logo_greensmart.png");
        ImageIcon anh = u == null ? null : new ImageIcon(u);
        if (anh == null) {
            File f = new File("resource/logo_greensmart.png");
            if (f.isFile()) anh = new ImageIcon(f.getAbsolutePath());
        }
        if (anh != null && anh.getIconWidth() > 0 && anh.getIconHeight() > 0) {
            int w = anh.getIconWidth(), h = anh.getIconHeight();
            double tiLe = Math.min((double) kichThuoc / w, (double) kichThuoc / h);
            return new ImageIcon(
                    anh.getImage()
                            .getScaledInstance(
                                    (int) (w * tiLe), (int) (h * tiLe), Image.SCALE_SMOOTH));
        }
        return new Icon() {
            public int getIconWidth() {
                return kichThuoc;
            }

            public int getIconHeight() {
                return kichThuoc;
            }

            public void paintIcon(Component c, Graphics g0, int x, int y) {
                Graphics2D g = (Graphics2D) g0.create();
                g.translate(x, y);
                g.scale(kichThuoc / 100.0, kichThuoc / 100.0);
                g.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Path2D la = new Path2D.Double();
                la.moveTo(22, 75);
                la.curveTo(0, 46, 24, 21, 48, 20);
                la.curveTo(67, 19, 81, 14, 89, 5);
                la.curveTo(89, 43, 84, 70, 60, 82);
                la.curveTo(47, 89, 33, 86, 26, 80);
                la.lineTo(15, 94);
                la.quadTo(11, 98, 8, 93);
                la.closePath();
                g.setColor(PRIMARY);
                g.fill(la);
                Path2D gan = new Path2D.Double();
                gan.moveTo(23, 80);
                gan.quadTo(52, 48, 76, 29);
                g.setColor(c.getBackground());
                g.setStroke(new BasicStroke(3.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.draw(gan);
                g.dispose();
            }
        };
    }

    /** FlowLayout vẫn tự xuống dòng; tính lại chiều cao để hàng sau không che lên bảng. */
    private static final class DongTuNgat extends FlowLayout {
        DongTuNgat() {
            super(FlowLayout.LEFT, 8, 5);
        }

        @Override
        public Dimension preferredLayoutSize(Container target) {
            int rong = target.getWidth();
            Container cha = target.getParent();
            while (rong <= 0 && cha != null) {
                rong = cha.getWidth() - 32;
                cha = cha.getParent();
            }
            if (rong <= 0) return super.preferredLayoutSize(target);
            Insets in = target.getInsets();
            int toiDa = Math.max(100, rong - in.left - in.right - getHgap() * 2);
            int x = 0, h = 0, tong = getVgap() * 2, max = 0;
            for (Component c : target.getComponents()) {
                if (!c.isVisible()) continue;
                Dimension d = c.getPreferredSize();
                if (x > 0 && x + getHgap() + d.width > toiDa) {
                    max = Math.max(max, x);
                    tong += h + getVgap();
                    x = 0;
                    h = 0;
                }
                x += (x == 0 ? 0 : getHgap()) + d.width;
                h = Math.max(h, d.height);
            }
            max = Math.max(max, x);
            return new Dimension(
                    max + in.left + in.right + getHgap() * 2, tong + h + in.top + in.bottom);
        }

        @Override
        public Dimension minimumLayoutSize(Container target) {
            Dimension d = preferredLayoutSize(target);
            d.width = 100;
            return d;
        }
    }

    // ===== Phím tắt =====
    public static String tenPhim(KeyStroke ks) {
        String s = "";
        int m = ks.getModifiers();
        if ((m & InputEvent.CTRL_DOWN_MASK) != 0) s += "Ctrl+";
        if ((m & InputEvent.ALT_DOWN_MASK) != 0) s += "Alt+";
        if ((m & InputEvent.SHIFT_DOWN_MASK) != 0) s += "Shift+";
        return s + KeyEvent.getKeyText(ks.getKeyCode());
    }

    /** Gán phím tắt chạy một hành động; chỉ hoạt động khi màn hình đang hiển thị. */
    public static void phimTat(JComponent goc, String phim, Runnable viec) {
        KeyStroke ks = KeyStroke.getKeyStroke(phim);
        if (ks == null) throw new IllegalArgumentException("Phím không hợp lệ: " + phim);
        String ten = "phimtat-" + phim;
        goc.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks, ten);
        goc.getActionMap()
                .put(
                        ten,
                        new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (goc.isShowing()) viec.run();
                            }
                        });
    }

    /** Gán phím tắt bấm một nút (bỏ qua nếu nút đang bị khóa) và ghi gợi ý vào tooltip. */
    public static void phimTat(JComponent goc, String phim, AbstractButton nut) {
        phimTat(
                goc,
                phim,
                () -> {
                    if (nut.isEnabled()) nut.doClick();
                });
        nut.setToolTipText(nut.getText() + " (" + tenPhim(KeyStroke.getKeyStroke(phim)) + ")");
    }

    /** Phím tắt chỉ có hiệu lực khi bảng đang giữ tiêu điểm, ví dụ Delete để bỏ dòng. */
    public static void phimTatBang(JTable bang, String phim, AbstractButton nut) {
        KeyStroke ks = KeyStroke.getKeyStroke(phim);
        String ten = "bang-" + phim;
        bang.getInputMap(JComponent.WHEN_FOCUSED).put(ks, ten);
        bang.getActionMap()
                .put(
                        ten,
                        new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (nut.isEnabled()) nut.doClick();
                            }
                        });
    }

    /**
     * Bộ phím tắt chuẩn cho màn hình danh mục: Ctrl+N tạo mới, Ctrl+S lưu, Ctrl+D xóa, F5 tải lại,
     * Ctrl+F nhảy vào ô tìm kiếm. Tham số nào không có thì truyền null.
     */
    public static void phimTatChuan(
            JComponent goc,
            AbstractButton moi,
            AbstractButton luu,
            AbstractButton xoa,
            AbstractButton tai,
            JComponent tim) {
        if (moi != null) phimTat(goc, "ctrl N", moi);
        if (luu != null) phimTat(goc, "ctrl S", luu);
        if (xoa != null) phimTat(goc, "ctrl D", xoa);
        if (tai != null) phimTat(goc, "F5", tai);
        if (tim != null) {
            phimTat(goc, "ctrl F", () -> tieuDiem(tim));
            tim.setToolTipText("Tìm kiếm (Ctrl+F)");
        }
    }

    public static void tieuDiem(JComponent c) {
        c.requestFocusInWindow();
        if (c instanceof javax.swing.text.JTextComponent)
            ((javax.swing.text.JTextComponent) c).selectAll();
    }

    public static String lietKe(String... dong) {
        return String.join("\n", Arrays.asList(dong));
    }

    private GiaoDien() {}
}
