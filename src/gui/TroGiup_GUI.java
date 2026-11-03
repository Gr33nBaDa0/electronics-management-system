package gui;

import entity.TaiKhoan;
import until.GiaoDien;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** Hướng dẫn thao tác; chỉ đọc nội dung, không truy cập hoặc thay đổi dữ liệu. */
public class TroGiup_GUI extends JPanel implements DocumentListener {
    private static final Color PRIMARY = GiaoDien.PRIMARY,
            BACKGROUND = GiaoDien.BACKGROUND,
            SURFACE = GiaoDien.SURFACE,
            TEXT = GiaoDien.TEXT,
            MUTED = GiaoDien.MUTED;

    private final Map<String, String> huongDan = new LinkedHashMap<>();
    private final DefaultListModel<String> model = new DefaultListModel<>();
    private final JList<String> danhMuc = new JList<>(model);
    private final JTextField tim = new JTextField(18);
    private final JButton btnBoLoc = GiaoDien.nut("Bỏ lọc");
    private final JLabel lblSoMuc = new JLabel(), lblChuDe = new JLabel();
    private final JTextArea noiDung = new JTextArea();

    public TroGiup_GUI(TaiKhoan tk) {
        boolean quanLy = tk != null && "ADMIN".equals(tk.getQuyen());
        taoHuongDan(quanLy);
        GiaoDien.boCuc(this);

        JPanel dau = GiaoDien.doc(
                GiaoDien.tieuDe("Hướng dẫn sử dụng GreenSmart"),
                GiaoDien.ghiChu("Chọn công việc bên trái để xem từng bước. Bạn đang xem hướng dẫn cho "
                        + (quanLy ? "Quản lý." : "Bán hàng.")));
        add(dau, BorderLayout.NORTH);

        tim.setToolTipText("Tìm theo nội dung, ví dụ: mở ca, QR, in hóa đơn. Có thể gõ không dấu.");
        JPanel timKiem = new JPanel(new BorderLayout(6, 6));
        timKiem.setOpaque(false);
        timKiem.add(new JLabel("Tìm hướng dẫn"), BorderLayout.NORTH);
        timKiem.add(tim, BorderLayout.CENTER);
        timKiem.add(btnBoLoc, BorderLayout.EAST);
        danhMuc.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        danhMuc.setFixedCellHeight(40);
        danhMuc.setVisibleRowCount(10);
        JPanel trai = new JPanel(new BorderLayout(0, 10));
        trai.setBackground(SURFACE);
        trai.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        trai.add(timKiem, BorderLayout.NORTH);
        trai.add(GiaoDien.cuon(danhMuc), BorderLayout.CENTER);
        trai.add(lblSoMuc, BorderLayout.SOUTH);
        trai.setMinimumSize(new Dimension(270, 200));

        noiDung.setEditable(false);
        noiDung.setLineWrap(true);
        noiDung.setWrapStyleWord(true);
        noiDung.setMargin(new Insets(12, 0, 12, 10));
        JScrollPane cuon = new JScrollPane(noiDung);
        cuon.setBorder(null);
        cuon.getVerticalScrollBar().setUnitIncrement(20);
        JPanel phai = new JPanel(new BorderLayout(0, 8));
        phai.setBackground(SURFACE);
        phai.setBorder(BorderFactory.createEmptyBorder(18, 22, 14, 18));
        phai.add(lblChuDe, BorderLayout.NORTH);
        phai.add(cuon, BorderLayout.CENTER);
        phai.add(GiaoDien.ghiChu("Ctrl+F: tìm hướng dẫn · F1: mở Trợ giúp · Shift+F1: bảng phím tắt"), BorderLayout.SOUTH);
        phai.setMinimumSize(new Dimension(380, 200));

        JSplitPane chia = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, trai, phai);
        chia.setResizeWeight(0);
        chia.setDividerLocation(330);
        chia.setDividerSize(10);
        chia.setBorder(null);
        chia.setBackground(BACKGROUND);
        add(chia, BorderLayout.CENTER);
        GiaoDien.trangTri(this);
        lblChuDe.setFont(GiaoDien.phong(Font.BOLD, 21));
        lblChuDe.setForeground(PRIMARY);
        lblSoMuc.setForeground(MUTED);
        noiDung.setFont(GiaoDien.phong(Font.PLAIN, 15));
        noiDung.setBackground(SURFACE);
        noiDung.setForeground(TEXT);
        danhMuc.setFont(GiaoDien.phong(Font.PLAIN, 14));
        danhMuc.setSelectionBackground(PRIMARY);
        danhMuc.setSelectionForeground(Color.WHITE);
        danhMuc.setBackground(SURFACE);
        danhMuc.setForeground(TEXT);
        tim.getAccessibleContext().setAccessibleName("Tìm hướng dẫn sử dụng");
        danhMuc.getAccessibleContext().setAccessibleName("Danh mục công việc");
        noiDung.getAccessibleContext().setAccessibleName("Các bước thao tác");

        tim.getDocument().addDocumentListener(this);
        btnBoLoc.addActionListener(e -> {
            tim.setText("");
            locChuDe();
            tim.requestFocusInWindow();
        });
        danhMuc.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) hienChuDe();
        });
        GiaoDien.phimTat(this, "ctrl F", () -> {
            tim.requestFocusInWindow();
            tim.selectAll();
        });
        locChuDe();
    }

    private void them(String ten, String... dong) {
        huongDan.put(ten, String.join("\n\n", dong));
    }

    private void taoHuongDan(boolean quanLy) {
        them("Bắt đầu sử dụng",
                "MỞ Ở ĐÂU: Đăng nhập → Dashboard.",
                "1. Đăng nhập bằng tài khoản được cấp. Tên người dùng và vai trò hiển thị trên thanh menu bên trái.",
                quanLy ? "2. Trên Dashboard quản lý, xem phiếu chờ duyệt, ca cần xử lý và sản phẩm cần chú ý trước khi mở từng chức năng."
                        : "2. Trên Dashboard nhân viên, xem lịch ca, trạng thái ca, hóa đơn đang xử lý và sản phẩm cần chú ý.",
                "3. Trước khi bán hàng, mở đúng ca được phân công cho mình. Nếu chưa có lịch ca, liên hệ Quản lý để được phân công.",
                "4. Chọn chức năng ở menu bên trái. Khi cần hướng dẫn, bấm Trợ giúp ở cuối menu hoặc nhấn F1.",
                "CÁCH ĐỌC: Chọn tên công việc bên trái. Ô Tìm hướng dẫn nhận cả từ có dấu và không dấu, ví dụ: mo ca, QR, VAT, in hoa don.");
        them("Mở ca làm việc",
                "MỞ Ở ĐÂU: Menu Ca làm việc hoặc nút Mở ca trên Dashboard nhân viên.",
                "1. Trong Ca làm việc, bấm Tải lại / lọc. Có thể nhập mã nhân viên hoặc ngày yyyy-MM-dd vào ô Mã NV / ngày.",
                "2. Chọn dòng phân công của chính mình, đúng ngày làm việc, có trạng thái Chưa mở.",
                "3. Bấm Mở ca, nhập tiền quỹ đầu ca bằng số không phân cách, ví dụ 1000000. Nhập 0 nếu thực tế không có tiền quỹ đầu.",
                "4. Xác nhận và kiểm tra dòng chuyển sang Đang mở. Sau đó mở Bán hàng POS để lập hóa đơn.",
                "NẾU CHƯA MỞ ĐƯỢC: Khi được báo còn ca đang mở, chọn bộ lọc Đang mở và kiểm tra cả các ca ngày trước. Đối soát / đóng ca cũ của mình trước khi mở ca mới. Không có phân công thì liên hệ Quản lý.");
        them("Đối soát và đóng ca",
                "MỞ Ở ĐÂU: Menu Ca làm việc.",
                "1. Hoàn thành các giao dịch trong ca, kiểm đếm tiền mặt thực tế tại quầy.",
                "2. Chọn dòng ca của mình đang Đang mở, bấm Đối soát / đóng ca.",
                "3. Đọc quỹ lý thuyết được hiển thị và nhập số tiền thực đếm. Tiền chuyển khoản không được tính là tiền mặt trong két.",
                "4. Nếu có chênh lệch, nhập giải trình theo yêu cầu. Kiểm tra lại rồi xác nhận đóng ca.",
                "KẾT QUẢ: Ca chuyển sang Đã đóng và không tiếp tục sửa giao dịch của ca đó. Mở ca được phân công tiếp theo trước khi bán tiếp.");
        them("Bán hàng và giỏ hàng",
                "MỞ Ở ĐÂU: Menu Bán hàng POS. Cần có ca của mình đang mở.",
                "1. Nhập mã hoặc tên sản phẩm vào ô tìm kiếm. Bấm + Thêm trên thẻ; ở chế độ Xem bảng có thể bấm đúp sản phẩm.",
                "2. Trong giỏ, dùng nút − / + để đổi số lượng, nút × để bỏ một dòng. Kiểm tra số lượng và tồn trước khi thanh toán.",
                "3. Tìm khách bằng số điện thoại và chọn đúng khách; để Khách vãng lai nếu khách không cung cấp thông tin. Chọn khuyến mãi và điểm sử dụng nếu có.",
                "4. Kiểm tra tiền hàng, giảm khuyến mãi, tiền quy đổi điểm, VAT và tổng thanh toán.",
                "5. Bấm Lưu đơn nếu chưa thanh toán; hoặc Tiền mặt · F9 / QR · F10 để mở hộp thanh toán.",
                "LƯU Ý: Lưu đơn chỉ tạo hóa đơn Chưa thanh toán. Bấm Đơn mới sẽ bỏ giỏ chưa lưu; lưu đơn cần giữ trước khi tạo đơn khác.");
        them("Khách hàng và điểm",
                "MỞ Ở ĐÂU: Menu Khách hàng; chọn khách trên Bán hàng POS khi bán.",
                "1. Nhập tên hoặc số điện thoại rồi bấm Tìm để kiểm tra khách đã có trong danh sách.",
                "2. Khi thêm khách, bấm Tạo mới, nhập thông tin và bấm Lưu. Mã khách tự hiện; không tự nhập mã khác. Số điện thoại phải hợp lệ và không trùng khách khác.",
                "3. Muốn sửa, chọn khách trong bảng, cập nhật thông tin và bấm Lưu. Dùng Lịch sử mua để tra hóa đơn của khách.",
                "4. Tại POS, chọn đúng khách trước khi nhập Điểm sử dụng. Nhập số điểm, không nhập số tiền; app kiểm tra số dư và giới hạn được dùng.",
                "KẾT QUẢ: Điểm chỉ được điều chỉnh khi thanh toán hoặc hoàn tất đổi trả. Không sửa trực tiếp số dư điểm trong thông tin khách.");
        them("Thanh toán tiền mặt",
                "MỞ Ở ĐÂU: Bán hàng POS → Tiền mặt · F9.",
                "1. Kiểm tra giỏ hàng, khách, khuyến mãi, điểm và tổng tiền trước khi bấm Tiền mặt · F9.",
                "2. Trong hộp Thanh toán, chọn Tiền mặt và nhập Tiền khách đưa, ví dụ 1000000. Kiểm tra thông báo còn thiếu hoặc tiền trả lại.",
                "3. Khi đã nhận đủ tiền, bấm Thanh toán & xuất hóa đơn. Một lần bấm hoàn tất giao dịch và tạo PDF.",
                "4. Trả tiền thừa cho khách theo số app hiển thị. PDF tự lưu tại D:\\HoaDonGreenSmart và được mở để xem.",
                "NẾU LỖI XUẤT PDF: Nếu thông báo đã thanh toán thành công, vào Hóa đơn để In hóa đơn lại. Không thực hiện thanh toán thêm lần nữa để lấy PDF.");
        them("Thanh toán bằng QR",
                "MỞ Ở ĐÂU: Bán hàng POS → QR · F10.",
                "1. Kiểm tra tổng tiền và mở hộp Thanh toán, chọn Chuyển khoản QR nếu cần.",
                "2. Bấm Hiện QR, mời khách quét mã và đối chiếu số tiền, ngân hàng, tài khoản nhận tiền của cửa hàng.",
                "3. Kiểm tra tiền đã vào tài khoản cửa hàng. Chỉ khi đã nhận tiền, bấm XÁC NHẬN: TÔI ĐÃ NHẬN TIỀN. App hoàn tất thanh toán và xuất PDF.",
                "4. Nếu dùng nút Thanh toán & xuất hóa đơn ở hộp thanh toán chính, phải đánh dấu Đã kiểm tra tiền chuyển khoản vào tài khoản cửa hàng sau khi kiểm tra thực tế.",
                "LƯU Ý: Hiện QR không tự xác minh giao dịch ngân hàng. Đóng hộp QR / Đóng / để sau thì chưa ghi nhận đã thanh toán.",
                "NẾU KHÔNG HIỆN QR: Kiểm tra Internet, thử lại hoặc nhờ Quản lý kiểm tra thông tin ngân hàng trong Sao lưu / thiết lập.");
        them("Tra cứu và in hóa đơn",
                "MỞ Ở ĐÂU: Menu Hóa đơn.",
                "1. Nhập mã hóa đơn hoặc SĐT khách, chỉnh Từ ngày / Đến ngày theo yyyy-MM-dd, chọn trạng thái hoặc hình thức thanh toán nếu cần, rồi bấm Tìm / lọc.",
                "2. Chọn hóa đơn trong bảng. Xem chi tiết mở nội dung các dòng sản phẩm; In hóa đơn xuất thẳng PDF của hóa đơn đã thanh toán.",
                "3. PDF tự lưu theo tên D:\\HoaDonGreenSmart\\HoaDon_<MaHoaDon>.pdf, không cần chọn nơi lưu. Nhấn Ctrl+P tại danh sách cũng xuất hóa đơn đang chọn.",
                "4. Muốn tiếp tục đơn Chưa thanh toán, chọn đơn → Mở đơn tại POS → chuyển menu Bán hàng POS. Chỉ Hủy đơn khi đơn còn chưa thanh toán và bạn có quyền thao tác.",
                "LƯU Ý: In lại không thu tiền hoặc trừ tồn thêm. Nếu PDF đang được ứng dụng khác giữ nên không ghi đè được, đóng PDF cũ rồi xuất lại. Khi cần in giấy, dùng chức năng in của ứng dụng đọc PDF.");
        them("Lập phiếu nhập hàng",
                "MỞ Ở ĐÂU: Menu Phiếu nhập hàng.",
                "1. Khi đã nhận và kiểm đếm hàng, bấm Phiếu mới. Điền nhà cung cấp, chứng từ, người giao theo thông tin thực tế.",
                "2. Chọn sản phẩm, nhập số lượng thực nhận và đơn giá nhập, bấm Thêm dòng. Lặp lại cho từng sản phẩm; dùng Bỏ dòng để sửa dòng nhập sai.",
                "3. Đối chiếu các dòng và tổng nhập rồi bấm Lưu chờ duyệt. Phiếu chuyển sang Chờ duyệt.",
                "4. Đề nghị Quản lý kiểm tra và duyệt hàng thực nhận. Dùng Tải lại / lọc, chọn phiếu rồi Mở sửa / chi tiết để theo dõi.",
                "KẾT QUẢ: Phiếu Chờ duyệt chưa tăng tồn. Khi được duyệt, trạng thái là Đã nhập và tồn tăng đúng một lần. Đây là ghi nhận hàng nhận thực tế, không phải đơn đặt mua.");
        them("Lập phiếu đổi trả",
                "MỞ Ở ĐÂU: Menu Đổi trả. Cần xác định hóa đơn mua đã thanh toán.",
                "1. Bấm Phiếu mới, nhập mã hóa đơn gốc và lý do. Bấm Lấy dòng hóa đơn.",
                "2. Chọn sản phẩm trong các dòng đã mua, số lượng trả và tình trạng Nguyên vẹn / Lỗi, bấm Thêm dòng trả. Không nhập vượt số còn được trả.",
                "3. Kiểm tra các dòng rồi bấm Lưu chờ duyệt. Quản lý xem xét để Duyệt hoặc Từ chối.",
                "4. Khi phiếu Đã duyệt, chọn phiếu và bấm Xác nhận hoàn tất. Đọc số tiền và hình thức hoàn được hiển thị; thực hiện / kiểm tra việc hoàn tiền trước khi xác nhận.",
                "KẾT QUẢ: Chỉ Hoàn tất mới cập nhật tiền hoàn, tồn và điểm theo giao dịch. Duyệt phiếu chưa phải là hoàn tiền. Chỉ được sửa / xóa phiếu chờ khi app cho phép.");
        them("Tra cứu bảo hành",
                "MỞ Ở ĐÂU: Hóa đơn → chọn hóa đơn đã thanh toán → Tra bảo hành.",
                "1. Tìm đúng hóa đơn của khách bằng mã hóa đơn hoặc SĐT và khoảng ngày.",
                "2. Bấm Tra bảo hành và chọn sản phẩm đã mua trong danh sách app hiển thị.",
                "3. Đọc ngày lập, thời hạn và hạn tham khảo. Sản phẩm có 0 tháng được báo không áp dụng bảo hành.",
                "LƯU Ý: Đối chiếu chứng từ và điều kiện bảo hành thực tế trước khi hướng dẫn khách liên hệ đơn vị phụ trách. App hỗ trợ tra cứu, không có quy trình lập phiếu sửa chữa.");
        them("Sản phẩm và tồn kho",
                "MỞ Ở ĐÂU: Menu Sản phẩm hoặc thẻ sản phẩm tại POS.",
                "1. Nhập mã / tên sản phẩm, chọn bộ lọc loại, thương hiệu, giá hoặc trạng thái rồi bấm Tìm.",
                "2. Chọn một dòng để đọc thông số, giá, bảo hành, tồn và ảnh. Bấm Tải lại / bỏ lọc để xem lại toàn bộ.",
                "3. Tại POS, kiểm tra số lượng còn trước khi thêm vào giỏ. Sản phẩm ngưng bán không được dùng như sản phẩm đang bán.",
                "LƯU Ý: Tài khoản Bán hàng xem danh mục; Quản lý cập nhật thông tin và ảnh. Tồn được cập nhật qua nhập hàng, thanh toán và đổi trả, không sửa tay ở form sản phẩm.");
        them("Đổi mật khẩu, đăng xuất",
                "MỞ Ở ĐÂU: Các nút cuối menu bên trái.",
                "1. Để đổi mật khẩu của mình, bấm Đổi mật khẩu, nhập mật khẩu mới vào hộp thoại rồi xác nhận.",
                "2. Trước khi Đăng xuất, lưu đơn đang làm nếu cần giữ lại và kiểm tra việc đối soát ca. Giỏ chưa lưu sẽ mất khi thoát phiên.",
                "3. Bấm Đăng xuất, đọc thông báo và xác nhận để trở về màn Đăng nhập.",
                "PHÍM TẮT: Ctrl+Shift+P đổi mật khẩu; Ctrl+L đăng xuất. Chỉ dùng tài khoản được cấp cho mình để giao dịch được ghi nhận đúng người.");
        them("Các phím tắt",
                "TOÀN ỨNG DỤNG: F1 mở Trợ giúp; Shift+F1 mở bảng phím tắt; Alt+Home về Dashboard.",
                "CHUYỂN MÀN: Ctrl+1 Bán hàng POS; Ctrl+2 Hóa đơn; Ctrl+3 Đổi trả; Ctrl+4 Khách hàng; Ctrl+5 Sản phẩm; Ctrl+6 Phiếu nhập hàng; Ctrl+8 Ca làm việc.",
                quanLy ? "DÀNH CHO QUẢN LÝ: Ctrl+7 Khuyến mãi; Ctrl+9 Danh mục ca; Ctrl+0 Nhân viên / tài khoản; Ctrl+B Báo cáo; Ctrl+G Sao lưu / thiết lập."
                        : "CÁC MỤC QUẢN LÝ: Nếu chức năng không xuất hiện trong menu của bạn, liên hệ Quản lý khi cần được hỗ trợ.",
                "TRONG POS: F2 tìm sản phẩm; F8 lưu đơn; F9 tiền mặt; F10 QR; Ctrl+N tạo đơn mới.",
                "HÓA ĐƠN: Ctrl+P xuất PDF của dòng đang chọn. THANH TOÁN: F2 hiện QR, Enter thanh toán & xuất hóa đơn, Esc để sau.",
                "MÀN DANH MỤC: Ctrl+N tạo mới, Ctrl+S lưu, Ctrl+D xóa, Ctrl+F tìm kiếm, F5 tải lại tại các màn có nút tương ứng. Trong Trợ giúp, Ctrl+F đưa con trỏ vào ô tìm hướng dẫn.");
        them("Khi gặp vướng mắc",
                "CHƯA BÁN ĐƯỢC: Kiểm tra đã mở ca của mình và giỏ có sản phẩm. Nếu chưa được phân công, nhờ Quản lý phân công trước.",
                "BÁO CÒN CA ĐANG MỞ: Vào Ca làm việc, bỏ từ khóa lọc nếu cần, lọc Đang mở rồi bấm Tải lại / lọc. Kiểm tra cả ca cũ của mình và đối soát / đóng ca đó trước.",
                "THIẾU HÀNG: Kiểm tra lại tồn, giảm số lượng trong giỏ hoặc liên hệ Quản lý. Không tăng tồn bằng cách sửa thông tin sản phẩm.",
                "KHÔNG HIỆN QR: Kiểm tra Internet, thử lại; nhờ Quản lý kiểm tra ngân hàng và tài khoản nhận tiền. Chỉ xác nhận khi đã nhận tiền thực tế.",
                "ĐÃ THANH TOÁN NHƯNG CHƯA CÓ PDF: Tìm hóa đơn ở menu Hóa đơn và bấm In hóa đơn. Máy cần có ổ D; đóng file PDF cũ nếu đang bị giữ rồi thử lại. Không thanh toán lại để lấy hóa đơn.",
                "KHÔNG TẢI ĐƯỢC DỮ LIỆU: Ghi lại thông báo lỗi và việc đang làm để báo Quản lý / người hỗ trợ. Dữ liệu chưa lưu nên được kiểm tra trước khi tải lại màn hình.");

        if (!quanLy) return;
        them("Nhân viên và tài khoản",
                "MỞ Ở ĐÂU: Nhân viên / tài khoản. Dành cho Quản lý.",
                "1. Bấm Tạo mới, điền mã, họ tên, vai trò và thông tin nhân viên; nhập ngày theo yyyy-MM-dd.",
                "2. Điền Tên đăng nhập mới và Mật khẩu mới cho hồ sơ mới. Chọn ảnh nếu cần, rồi bấm Lưu.",
                "3. Khi sửa, chọn nhân viên trong bảng, kiểm tra thông tin đang mở rồi Lưu. Dùng tìm mã / họ tên và lọc Vai trò để tra nhanh.",
                "LƯU Ý: Mỗi nhân viên gắn với một tài khoản. Hồ sơ đã có lịch sử có thể không xóa được; đọc thông báo trước khi đổi hoặc xóa dữ liệu.");
        them("Danh mục ca và phân công",
                "MỞ Ở ĐÂU: Danh mục ca; sau đó Ca làm việc. Dành cho Quản lý.",
                "1. Trong Danh mục ca, bấm Tạo mới, nhập mã ca chưa có trong danh sách, tên ca và giờ bắt đầu / kết thúc theo HH:mm rồi Lưu.",
                "2. Chuyển sang Ca làm việc. Chọn nhân viên có sẵn từ danh sách Mã nhân viên, bấm Chọn ngày để mở lịch, rồi chọn Ca làm việc.",
                "3. Điền ghi chú nếu có và bấm Phân công. Kiểm tra mã phân công, nhân viên, ngày, ca trong bảng.",
                "4. Hướng dẫn nhân viên mở đúng phân công của mình. Không phân công chồng giờ; chỉ Xóa phân công khi chưa mở và app cho phép.");
        them("Quản lý sản phẩm và ảnh",
                "MỞ Ở ĐÂU: Sản phẩm. Thao tác thêm / sửa dành cho Quản lý.",
                "1. Bấm Tạo mới. Mã sản phẩm được điền sẵn; nhập tên, loại, thương hiệu, giá, thông số, xuất xứ, số tháng bảo hành và trạng thái.",
                "2. Bấm Chọn ảnh, chọn JPG hoặc PNG không quá 10 MB, kiểm tra ảnh xem trước rồi Lưu. Muốn bỏ ảnh, bấm Bỏ ảnh rồi Lưu.",
                "3. Muốn sửa, chọn đúng dòng trong bảng trước khi cập nhật. Nếu chỉ ngừng kinh doanh, chọn trạng thái Ngưng bán rồi Lưu.",
                "4. Tạo Phiếu nhập hàng và duyệt hàng thực nhận để tăng tồn. Sau khi lưu ảnh, chuyển lại POS để đọc ảnh mới.",
                "LƯU Ý: Không điền tồn đầu vào form sửa sản phẩm; sản phẩm mới được bổ sung tồn qua nghiệp vụ nhập hàng.");
        them("Quản lý khuyến mãi",
                "MỞ Ở ĐÂU: Khuyến mãi. Dành cho Quản lý.",
                "1. Bấm Tạo mới, điền mã, tên chương trình, mức giảm (%), ngày bắt đầu / kết thúc và trạng thái.",
                "2. Kiểm tra ngày theo yyyy-MM-dd và giá trị giảm hợp lệ rồi bấm Lưu.",
                "3. Để cập nhật, chọn chương trình trong bảng rồi sửa và Lưu. Dùng Tìm hoặc Tải lại / bỏ lọc để tra cứu.",
                "LƯU Ý: POS chỉ áp dụng chương trình phù hợp hiệu lực. Chương trình đã có lịch sử sử dụng không được xóa tùy ý; dùng trạng thái phù hợp để dừng áp dụng mới.");
        them("Duyệt nhập hàng và đổi trả",
                "MỞ Ở ĐÂU: Phiếu nhập hàng hoặc Đổi trả; cũng có thể mở công việc cần xử lý từ Dashboard quản lý.",
                "NHẬP HÀNG: Lọc Chờ duyệt, chọn phiếu → Mở sửa / chi tiết, đối chiếu chứng từ và hàng thực nhận. Bấm Duyệt hàng thực nhận khi đã kiểm đếm; hoặc Từ chối và nhập lý do.",
                "KẾT QUẢ NHẬP: Đã nhập mới cộng tồn. Phiếu Chờ duyệt hoặc Từ chối chưa tăng tồn và không duyệt lại phiếu đã nhập.",
                "ĐỔI TRẢ: Mở Đổi trả, lọc Chờ duyệt, chọn phiếu → Mở sửa / xem. Kiểm tra hóa đơn gốc, số lượng và tình trạng hàng rồi Duyệt hoặc Từ chối có lý do.",
                "KẾT QUẢ ĐỔI TRẢ: Duyệt mới chuyển sang Đã duyệt. Người xử lý cần thực hiện / kiểm tra hoàn tiền rồi Xác nhận hoàn tất; khi đó mới cập nhật tiền, tồn và điểm.");
        them("Báo cáo và Dashboard quản lý",
                "MỞ Ở ĐÂU: Dashboard và Báo cáo. Dành cho Quản lý.",
                "1. Trên Dashboard, đọc các chỉ số, phiếu cần duyệt, cảnh báo tồn và ca cần xử lý để biết việc cần ưu tiên.",
                "2. Vào Báo cáo, chọn Từ ngày ca / Đến ngày ca theo yyyy-MM-dd rồi bấm Lập báo cáo.",
                "3. Xem các cột tiền thu, tiền hoàn, thu sau hoàn; chuyển bảng sản phẩm để xem tồn và lượng bán / trả trong kỳ.",
                "4. Bấm Xuất bảng CSV khi cần lưu bảng báo cáo. Chọn đúng kỳ trước khi xuất để file khớp dữ liệu đang xem.");
        them("Sao lưu và thiết lập QR",
                "MỞ Ở ĐÂU: Sao lưu / thiết lập. Dành cho Quản lý.",
                "SAO LƯU: Điền Đường dẫn .bak trên máy SQL Server, bấm Sao lưu và kiểm tra. Dùng Kiểm tra bản .bak để kiểm tra bản sao tại đường dẫn đó.",
                "TỰ ĐỘNG: Nhập Chu kỳ sao lưu (phút), ví dụ 60; 0 là tắt. Bấm Lưu cấu hình rồi đăng nhập lại để áp dụng lịch.",
                "ĐIỀU KIỆN CHẠY: Sao lưu tự động chỉ chạy khi ứng dụng còn mở và Quản lý đang đăng nhập. Đường dẫn .bak thuộc máy SQL Server và phải là nơi hệ thống được phép ghi.",
                "THIẾT LẬP QR: Điền BIN ngân hàng 6 số, số tài khoản nhận tiền thật của cửa hàng, tên chủ tài khoản và tên ngân hàng; bấm Lưu cấu hình. Đối chiếu thông tin trước khi cho khách chuyển khoản.",
                "LƯU Ý: Thiết lập QR không tự xác nhận tiền đã vào. Nhân viên vẫn phải kiểm tra giao dịch thực tế trước khi bấm xác nhận nhận tiền.");
    }

    private String khongDau(String text) {
        return Normalizer.normalize(text.toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "").replace('đ', 'd');
    }

    private void locChuDe() {
        String chon = danhMuc.getSelectedValue();
        String tuKhoa = khongDau(tim.getText().trim());
        model.clear();
        for (Map.Entry<String, String> muc : huongDan.entrySet())
            if (khongDau(muc.getKey() + " " + muc.getValue()).contains(tuKhoa))
                model.addElement(muc.getKey());
        lblSoMuc.setText(model.size() + " / " + huongDan.size() + " chủ đề");
        if (!model.isEmpty()) {
            if (chon != null && model.contains(chon)) danhMuc.setSelectedValue(chon, true);
            else danhMuc.setSelectedIndex(0);
        } else hienChuDe();
    }

    private void hienChuDe() {
        String ten = danhMuc.getSelectedValue();
        lblChuDe.setText(ten == null ? "Chưa tìm thấy hướng dẫn" : ten);
        noiDung.setText(ten == null
                ? "Thử từ khóa ngắn hơn như: ca, QR, hóa đơn.\n\nBấm Bỏ lọc để xem toàn bộ hướng dẫn phù hợp vai trò của bạn."
                : huongDan.get(ten));
        noiDung.setCaretPosition(0);
    }

    @Override public void insertUpdate(DocumentEvent e) { locChuDe(); }
    @Override public void removeUpdate(DocumentEvent e) { locChuDe(); }
    @Override public void changedUpdate(DocumentEvent e) { locChuDe(); }
}
