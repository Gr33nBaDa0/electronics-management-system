<a id="readme-top"></a>

<!-- PROJECT SHIELDS -->

[![Java][Java-shield]][Java-url]
[![Swing][Swing-shield]][Swing-url]
[![SQLServer][SQLServer-shield]][SQLServer-url]
[![JDBC][JDBC-shield]][JDBC-url]
[![Eclipse][Eclipse-shield]][Eclipse-url]
[![UML][UML-shield]][UML-url]

[Java-shield]: https://img.shields.io/badge/Java-JDK_17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white
[Java-url]: https://www.oracle.com/java/
[Swing-shield]: https://img.shields.io/badge/Java_Swing-GUI_&_AWT-007396?style=for-the-badge&logo=java&logoColor=white
[Swing-url]: https://docs.oracle.com/javase/tutorial/uiswing/
[SQLServer-shield]: https://img.shields.io/badge/SQL_Server-CC2927?style=for-the-badge&logo=microsoft-sql-server&logoColor=white
[SQLServer-url]: https://www.microsoft.com/en-us/sql-server
[JDBC-shield]: https://img.shields.io/badge/JDBC-sqljdbc42-4EA94B?style=for-the-badge&logo=databricks&logoColor=white
[JDBC-url]: https://learn.microsoft.com/en-us/sql/connect/jdbc/microsoft-jdbc-driver-for-sql-server
[Eclipse-shield]: https://img.shields.io/badge/Eclipse_IDE-2C2255?style=for-the-badge&logo=eclipse-ide&logoColor=white
[Eclipse-url]: https://eclipseide.org/
[UML-shield]: https://img.shields.io/badge/Visual_Paradigm-UML_2.5-CC0000?style=for-the-badge&logo=diagramsdotnet&logoColor=white
[UML-url]: https://www.visual-paradigm.com/

<!-- PROJECT LOGO -->
<br />
<div align="center">
  <h1 align="center">🛒 HỆ THỐNG QUẢN LÝ SIÊU THỊ ĐIỆN MÁY GREENSMART</h1>
  <p align="center">
    Ứng dụng Desktop quản lý bán lẻ điện máy toàn diện — Bán hàng POS, Thanh toán VietQR, Quản lý Tồn kho, Đổi trả & Đối soát Két tiền Ca trực
    <br />
    <em>Bài tập lớn môn Phát Triển Ứng Dụng (Application Development) — Nhóm 12 (IUH)</em>
  </p>
</div>

---

<!-- TABLE OF CONTENTS -->
<details>
  <summary>📋 Mục lục</summary>
  <ol>
    <li><a href="#giới-thiệu-dự-án">Giới thiệu dự án</a></li>
    <li><a href="#công-nghệ-sử-dụng">Công nghệ sử dụng</a></li>
    <li><a href="#cấu-trúc-dự-án">Cấu trúc dự án</a></li>
    <li><a href="#kiến-trúc-phần-mềm--giao-diện">Kiến trúc phần mềm & Giao diện</a></li>
    <li><a href="#kiến-trúc-cơ-sở-dữ-liệu--dao">Kiến trúc Cơ sở dữ liệu & DAO</a></li>
    <li><a href="#các-màn-hình-chức-năng-gui">Các màn hình chức năng (GUI)</a></li>
    <li><a href="#design-tokens-uithemejava">Design Tokens (UITheme.java)</a></li>
    <li><a href="#quy-ước-đặt-tên">Quy ước đặt tên</a></li>
    <li><a href="#bắt-đầu">Bắt đầu</a></li>
    <li><a href="#lưu-ý-kỹ-thuật--quy-tắc-nghiệp-vụ">Lưu ý kỹ thuật & Quy tắc nghiệp vụ</a></li>
    <li><a href="#thành-viên">Thành viên thực hiện</a></li>
  </ol>
</details>

---

## Giới thiệu dự án

**GREENSMART MANAGEMENT SYSTEM** là phần mềm máy tính (Desktop Application) được xây dựng thuần bằng **Java (Swing/AWT)** kết hợp hệ quản trị cơ sở dữ liệu **Microsoft SQL Server**, nhằm thay thế quy trình ghi chép sổ sách và bảng tính rời rạc tại siêu thị điện máy bằng một giải pháp phần mềm đồng bộ. Trọng tâm của hệ thống bao gồm:

- **Bán hàng tại quầy (POS)** — Lập hóa đơn nhanh, kiểm tra tồn kho khả dụng thời gian thực, tự động tính thuế VAT (10%) và hỗ trợ thanh toán tiền mặt hoặc quét mã QR ngân hàng (VietQR).
- **Khách hàng & Tích điểm thành viên** — Tra cứu hội viên qua số điện thoại, tự động cộng điểm thưởng sau thanh toán và quy đổi giảm trừ trực tiếp trên hóa đơn.
- **Nhập hàng bổ sung & Đổi trả hàng** — Quy trình kiểm soát 2 bước chặt chẽ (Nhân viên lập phiếu tiếp nhận/đổi trả → Người quản lý phê duyệt cập nhật tồn kho & hoàn tiền).
- **Quản lý ca làm việc & Đối soát két tiền** — Vận hành theo 3 ca cố định trong ngày, nhận quỹ đầu ca và tự động đối soát tiền mặt thực tế cuối ca kèm giải trình chênh lệch.
- **Giao diện đồng bộ (UITheme)** — Thiết kế hiện đại, dùng chung bộ Design Tokens xuyên suốt và hỗ trợ điều hướng nhanh bằng phím tắt (F1–F12, Tab Order).

**Phân hệ nghiệp vụ chính:**
| Phân hệ | Mô tả |
|---|---|
| Hệ thống & Ca trực | Đăng nhập phân quyền (Quản lý / Bán hàng), đổi mật khẩu, phân công 3 ca cố định, mở/đóng ca & kiểm đếm két tiền |
| Bán hàng & Thanh toán (POS) | Giỏ hàng thông minh, áp mã khuyến mãi hợp lệ, quy đổi điểm tích lũy, thanh toán Tiền mặt/VietQR, in hóa đơn PDF |
| Danh mục & Nhập hàng | Quản lý sản phẩm điện máy, lập phiếu nhập hàng bổ sung và phê duyệt cộng dồn tồn kho khả dụng |
| Khách hàng & Đổi trả | Quản lý hồ sơ khách hàng, lịch sử giao dịch, lập phiếu đổi trả trong hạn (7–14 ngày) và duyệt hoàn tiền/thu hồi điểm |
| Thống kê & Báo cáo | Tổng hợp doanh thu theo ca/ngày/tháng/năm, top sản phẩm bán chạy, kiểm soát tồn kho và sao lưu/phục hồi CSDL |

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

## Công nghệ sử dụng

| Lớp | Công nghệ | Ghi chú |
| ---------- | -------------------------------- | ------------------------------------------------------------------------------------- |
| Language | Java SE (JDK 17+) | Lập trình hướng đối tượng (OOP), Collections, Date/Time API (`java.time`) |
| UI / GUI | Java Swing & AWT | Thiết kế giao diện Desktop, Custom Components, Event-Driven Programming |
| Database | Microsoft SQL Server | Hệ quản trị CSDL quan hệ (13 bảng chuẩn hóa 3NF), hỗ trợ ACID Transaction |
| DB Driver | Microsoft JDBC Driver (`sqljdbc42.jar`) | Kết nối trực tiếp giữa ứng dụng Java và SQL Server |
| QR Helper | VietQR / ZXing | Sinh mã QR thanh toán chuyển khoản động theo đúng số tiền hóa đơn |
| Modeling | Visual Paradigm (UML 2.5) | Thiết kế Use Case, Activity, Sequence, Class Diagram & EER Diagram |
| IDE | Eclipse IDE | Môi trường phát triển dự án chuẩn (`.project`, `.classpath`) |

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

## Cấu trúc dự án

```
electronics-management-system/
│
├── .settings/                         # Cấu hình thiết lập workspace của Eclipse IDE
│   └── .gitkeep
│
├── database/                          # Lưu trữ kịch bản cơ sở dữ liệu (SQL Scripts)
│   ├── .gitkeep
│   └── GreenSmart_sql.sql             # ★ Script khởi tạo CSDL: 13 bảng, ràng buộc FK/Check & Seed data
│
├── docs/                              # Tài liệu báo cáo, đặc tả kỹ thuật & nhật ký AI
│   └── .gitkeep
│
├── lib/                               # Thư viện bên ngoài (External JARs)
│   ├── .gitkeep
│   └── sqljdbc42.jar                  # ★ Thư viện JDBC Driver kết nối Microsoft SQL Server
│
├── resource/                          # Tài nguyên tĩnh (Hình ảnh sản phẩm, Icon, Logo, Fonts)
│   └── .gitkeep
│
├── src/                               # Mã nguồn chính của ứng dụng Java
│   ├── .gitkeep
│   │
│   ├── common/                        # Khởi chạy ứng dụng & Quản lý phiên dùng chung
│   │   ├── .gitkeep
│   │   ├── App.java                   # ★ Điểm khởi chạy chính (Main entry point)
│   │   └── UserSession.java           # Lưu trữ thông tin nhân viên & ca làm việc đang đăng nhập
│   │
│   ├── connectDB/                     # Quản lý kết nối Cơ sở dữ liệu
│   │   ├── .gitkeep
│   │   └── ConnectDB.java             # ★ Singleton Pattern quản lý đối tượng Connection tới SQL Server
│   │
│   ├── entity/                        # Các lớp Thực thể nghiệp vụ (13 POJO / Entity Classes)
│   │   ├── .gitkeep
│   │   ├── NhanVien.java              # Thực thể Nhân viên
│   │   ├── TaiKhoan.java              # Thực thể Tài khoản đăng nhập
│   │   ├── CaLamViec.java             # Thực thể Ca làm việc (Sáng, Chiều, Tối)
│   │   ├── PhanCongCa.java            # Thực thể Phân công ca & Đối soát két tiền
│   │   ├── KhachHang.java             # Thực thể Khách hàng thành viên & Điểm tích lũy
│   │   ├── ChuongTrinhKhuyenMai.java  # Thực thể Chương trình khuyến mãi
│   │   ├── SanPham.java               # Thực thể Sản phẩm điện máy & Tồn kho
│   │   ├── PhieuNhapHang.java         # Thực thể Phiếu nhập hàng bổ sung
│   │   ├── ChiTietPhieuNhapHang.java  # Thực thể Chi tiết dòng hàng nhập
│   │   ├── HoaDon.java                # Thực thể Hóa đơn bán hàng (POS)
│   │   ├── ChiTietHoaDon.java         # Thực thể Chi tiết dòng hóa đơn bán
│   │   ├── PhieuDoiTra.java           # Thực thể Phiếu đổi trả & Hoàn tiền
│   │   └── ChiTietDoiTra.java         # Thực thể Chi tiết sản phẩm đổi trả
│   │
│   ├── dao/                           # Tầng Truy cập dữ liệu & Nghiệp vụ (Data Access Objects)
│   │   ├── .gitkeep
│   │   ├── NhanVien_DAO.java          # Truy vấn & quản lý hồ sơ nhân viên
│   │   ├── TaiKhoan_DAO.java          # Xác thực đăng nhập, phân quyền & đổi mật khẩu
│   │   ├── CaLamViec_DAO.java         # Truy vấn thông tin khung giờ 3 ca cố định
│   │   ├── PhanCongCa_DAO.java        # Xử lý mở ca, nhận quỹ đầu ca, đóng ca & đối soát két
│   │   ├── KhachHang_DAO.java         # Quản lý khách hàng, cộng/trừ điểm thưởng
│   │   ├── KhuyenMai_DAO.java         # Quản lý khuyến mãi & kiểm tra hiệu lực áp mã
│   │   ├── SanPham_DAO.java           # CRUD sản phẩm, tìm kiếm, lọc & kiểm tra tồn kho
│   │   ├── PhieuNhapHang_DAO.java     # Lập phiếu nhập & Transaction phê duyệt cộng tồn kho
│   │   ├── HoaDon_DAO.java            # Transaction lập hóa đơn POS, trừ tồn kho & tích điểm
│   │   ├── PhieuDoiTra_DAO.java       # Lập phiếu đổi trả & duyệt hoàn tiền/cộng lại tồn
│   │   └── ThongKe_DAO.java           # Tổng hợp số liệu doanh thu, hàng bán chạy & tồn kho
│   │
│   ├── gui/                           # Tầng Giao diện người dùng (Java Swing Frames & Panels)
│   │   ├── .gitkeep
│   │   ├── FrmDangNhap.java           # Màn hình Đăng nhập hệ thống
│   │   ├── FrmChinh.java              # Khung màn hình chính (MainFrame) điều hướng theo quyền
│   │   ├── PnlBanHangPOS.java         # Màn hình Bán hàng tại quầy (POS) & Thanh toán
│   │   ├── PnlSanPham.java            # Màn hình Quản lý danh mục sản phẩm & Tồn kho
│   │   ├── PnlNhapHang.java           # Màn hình Lập & Phê duyệt phiếu nhập hàng
│   │   ├── PnlKhachHang.java          # Màn hình Quản lý khách hàng thành viên
│   │   ├── PnlDoiTra.java             # Màn hình Tiếp nhận & Phê duyệt đổi trả hàng
│   │   ├── PnlKhuyenMai.java          # Màn hình Thiết lập chương trình khuyến mãi
│   │   ├── PnlNhanVien.java           # Màn hình Quản lý nhân viên & Tài khoản
│   │   ├── PnlCaLamViec.java          # Màn hình Phân ca, Mở ca & Đóng ca đối soát két
│   │   └── PnlThongKe.java            # Màn hình Báo cáo thống kê doanh thu & Sao lưu CSDL
│   │
│   ├── ui/                            # Thành phần định dạng giao diện dùng chung
│   │   ├── .gitkeep
│   │   └── UITheme.java               # ★ Design tokens: Bảng màu, Font chữ, Button, Table style
│   │
│   └── until/                         # Các lớp tiện ích bổ trợ (Utilities & Helpers)
│       ├── .gitkeep
│       ├── Validator.java             # Kiểm tra biểu thức chính quy (Regex) & dữ liệu đầu vào
│       ├── CurrencyFormatter.java     # Định dạng tiền tệ VNĐ, ngày giờ chuẩn Việt Nam
│       └── VietQRGenerator.java       # Tiện ích sinh mã QR thanh toán ngân hàng
│
├── .classpath                         # Cấu hình đường dẫn thư viện Java Build Path (Eclipse)
├── .gitignore                         # Danh sách loại trừ các file biên dịch (/bin, .class)
├── .project                           # Thông tin định danh dự án Eclipse
└── README.md                          # Tài liệu hướng dẫn và giới thiệu dự án
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

## Kiến trúc phần mềm & Giao diện

### Nguyên tắc thiết kế 3 lớp (3-Tier Architecture)

- **Presentation Layer (`src/gui`, `src/ui`)**: Chịu trách nhiệm hiển thị giao diện người dùng bằng Java Swing, bắt sự kiện bàn phím/chuột và kiểm tra định dạng cơ bản trước khi gọi xuống tầng DAO.
- **Business Logic & Data Access Layer (`src/dao`, `src/until`)**: Đóng gói toàn bộ quy tắc nghiệp vụ của siêu thị (kiểm tra tồn kho khả dụng, tính thuế VAT 10%, giới hạn giảm trừ điểm 50%, đối soát két tiền) và trực tiếp thực thi các câu lệnh `PreparedStatement` hoặc `Transaction` xuống CSDL.
- **Database & Entity Layer (`src/entity`, `src/connectDB`, `database/`)**: Biểu diễn 13 thực thể hướng đối tượng có kiểm tra ràng buộc dữ liệu chặt chẽ trong các hàm `setter` (throw `IllegalArgumentException` nếu vi phạm) và ánh xạ 1-1 với 13 bảng trong SQL Server.

### Luồng khởi tạo Giao diện

```text
common/App.java (Main)
  └── connectDB/ConnectDB.getInstance().connect() → Khởi tạo kết nối SQL Server
        └── ui/UITheme.setupLookAndFeel()          → Nạp bảng màu & Font chuẩn
              └── gui/FrmDangNhap                  → Xác thực tài khoản & phân quyền
                    └── gui/FrmChinh (MainFrame)   → Nạp các Pnl* tương ứng vai trò
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

## Kiến trúc Cơ sở dữ liệu & DAO

### Pattern chính: Singleton Connection + DAO-per-Entity + SQL Transaction

Mọi kết nối tới SQL Server đều đi qua lớp duy nhất `ConnectDB.java`. Các nghiệp vụ làm thay đổi nhiều bảng dữ liệu cùng lúc bắt buộc sử dụng cơ chế quản lý giao dịch (`setAutoCommit(false)` → `commit()` / `rollback()`).

```text
ConnectDB.java          → Quản lý kết nối JDBC duy nhất tới database GreenSmart
UserSession.java        → Lưu trạng thái NhanVien hiện hành và CaLamViec đang mở
HoaDon_DAO.java         → Transaction POS: Lưu HoaDon + ChiTietHoaDon + Trừ tồn SanPham + Cộng/Trừ điểm KhachHang
PhieuNhapHang_DAO.java  → Transaction Duyệt nhập: Đổi trạng thái "Đã duyệt" + Cộng dồn soLuongTon vào SanPham
PhieuDoiTra_DAO.java    → Transaction Duyệt trả: Đổi trạng thái "Đã duyệt" + Cộng lại tồn kho + Thu hồi điểm tích lũy
PhanCongCa_DAO.java     → Mở ca (ghi nhận tiền quỹ đầu ca) & Đóng ca (tính toán chênh lệch két tiền mặt)
```

### HoaDon_DAO.java — Luồng xử lý Thanh toán POS

```text
Input: HoaDon, List<ChiTietHoaDon>, KhachHang (nếu có), ChuongTrinhKhuyenMai (nếu có)

Validation & Transaction Flow:
  1. Kiểm tra soLuongMua <= SanPham.soLuongTon khả dụng theo thời gian thực
  2. Tính TongTienHang = Σ (soLuongMua * donGiaBan)
  3. Áp dụng Khuyến mãi hợp lệ (nếu giảm % thì tối đa <= 50% giá trị)
  4. Quy đổi Điểm tích lũy: 1 điểm = 1.000 VNĐ (Tối đa không vượt quá 50% hóa đơn)
  5. Tính thuế VAT = 10% sau giảm giá → Tính TongThanhToan cuối cùng
  6. Thực thi SQL Batch trong 1 Transaction:
       → INSERT bảng HoaDon & ChiTietHoaDon
       → UPDATE SanPham SET soLuongTon = soLuongTon - soLuongMua
       → UPDATE KhachHang SET diemTichLuy = diemTichLuy - diemDaDung + (TongThanhToan / 100.000)
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

## Các màn hình chức năng (GUI)

| File / Màn hình | Vai trò sử dụng | Mô tả chức năng chính | DAO được gọi |
| -------------------------------------------------------- | ----------------------------- | ------------------------------------------------------------------------------------------------------------------ | ---------------------------------- |
| `FrmDangNhap.java` | Tất cả | Đăng nhập hệ thống bằng mã nhân viên & mật khẩu, kiểm tra trạng thái hoạt động | `TaiKhoan_DAO`, `NhanVien_DAO` |
| `FrmChinh.java` | Tất cả | Màn hình khung chính chứa Sidebar điều hướng, thanh trạng thái ca trực và giữ giỏ hàng POS khi chuyển tab | `UserSession`, `PhanCongCa_DAO` |
| `PnlBanHangPOS.java` | Bán hàng, Quản lý | Quét/tìm sản phẩm, thêm vào giỏ, chọn khách hàng, áp mã khuyến mãi, trừ điểm, thanh toán Tiền mặt/QR và in hóa đơn | `SanPham_DAO`, `KhachHang_DAO`, `KhuyenMai_DAO`, `HoaDon_DAO` |
| `PnlSanPham.java` | Bán hàng, Quản lý | Tra cứu, lọc sản phẩm theo loại/hãng/giá; Quản lý được thêm mới, cập nhật thông tin sản phẩm | `SanPham_DAO` |
| `PnlNhapHang.java` | Bán hàng, Quản lý | Nhân viên lập phiếu nhận hàng bổ sung (Chờ duyệt); Quản lý kiểm tra và bấm Phê duyệt để cộng tồn kho | `PhieuNhapHang_DAO`, `SanPham_DAO` |
| `PnlKhachHang.java` | Bán hàng, Quản lý | Thêm mới khách hàng thành viên, cập nhật thông tin, tra cứu SĐT, xem điểm tích lũy & lịch sử mua hàng | `KhachHang_DAO`, `HoaDon_DAO` |
| `PnlDoiTra.java` | Bán hàng, Quản lý | Tra cứu hóa đơn cũ, kiểm tra hạn 7–14 ngày, lập phiếu đổi trả; Quản lý phê duyệt hoàn tiền & thu hồi điểm | `PhieuDoiTra_DAO`, `HoaDon_DAO`, `SanPham_DAO` |
| `PnlKhuyenMai.java` | Quản lý | Thiết lập chương trình khuyến mãi (% hoặc tiền mặt), cấu hình ngày bắt đầu/kết thúc, kích hoạt/dừng KM | `KhuyenMai_DAO` |
| `PnlNhanVien.java` | Quản lý | Quản lý hồ sơ nhân viên, cấp tài khoản mới đồng bộ, phân quyền và khóa/mở tài khoản | `NhanVien_DAO`, `TaiKhoan_DAO` |
| `PnlCaLamViec.java` | Bán hàng, Quản lý | Quản lý phân lịch 3 ca cố định; Nhân viên thực hiện Mở ca (nhận quỹ đầu) và Đóng ca (đối soát két tiền mặt) | `CaLamViec_DAO`, `PhanCongCa_DAO` |
| `PnlThongKe.java` | Quản lý | Thống kê doanh thu theo ca/ngày/tháng/năm, sản phẩm bán chạy, tồn kho thực tế và Sao lưu/Phục hồi CSDL (`.bak`) | `ThongKe_DAO` |

> **Phân quyền chặt chẽ**: Khi đăng nhập bằng tài khoản `BAN_HANG`, các nút phê duyệt chứng từ, thiết lập khuyến mãi, quản lý nhân sự và báo cáo doanh thu tổng sẽ tự động bị vô hiệu hóa hoặc ẩn đi.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

## Design Tokens (`UITheme.java`)

Toàn bộ màu sắc, font chữ, kích thước nút bấm và bảng biểu đều được định nghĩa tập trung trong **`src/ui/UITheme.java`** (do TV3 chủ trì) nhằm đảm bảo giao diện đồng nhất 100% giữa tất cả các thành viên:

```java
/* ── Brand Colors (GreenSmart Palette) ──────────────── */
public static final Color PRIMARY_GREEN   = new Color(22, 163, 74);   // #16A34A - Xanh lá chủ đạo
public static final Color PRIMARY_DARK    = new Color(21, 128, 61);   // #15803D - Sidebar & Header
public static final Color ACCENT_BLUE     = new Color(37, 99, 235);   // #2563EB - Nút hành động phụ / Làm mới
public static final Color BG_LIGHT        = new Color(248, 250, 252); // #F8FAFC - Nền tổng thể
public static final Color SURFACE_WHITE   = new Color(255, 255, 255); // #FFFFFF - Nền Card / Form / Bảng

/* ── Semantic Status Colors ─────────────────────────── */
public static final Color STATUS_SUCCESS  = new Color(34, 197, 94);   // Đã duyệt / Hoàn tất / Còn hàng
public static final Color STATUS_WARNING  = new Color(234, 179, 8);   // Chờ duyệt / Sắp hết hàng
public static final Color STATUS_DANGER   = new Color(220, 38, 38);   // Từ chối / Hết hàng / Lệch két / Xóa

/* ── Typography & Metrics ───────────────────────────── */
public static final Font FONT_TITLE       = new Font("Segoe UI", Font.BOLD, 20);
public static final Font FONT_SUBTITLE    = new Font("Segoe UI", Font.BOLD, 15);
public static final Font FONT_REGULAR     = new Font("Segoe UI", Font.PLAIN, 14);
public static final int  TABLE_ROW_HEIGHT = 32;
public static final int  BUTTON_HEIGHT    = 36;
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

## Quy ước đặt tên

### Định dạng Mã tự động (ID Regex Conventions)

| Thực thể | Định dạng Mã (Regex) | Ví dụ | Ý nghĩa |
| --------------------- | --------------------------------------- | -------------- | ------------------------------------ |
| `NhanVien` | `^(NV\|QL)\d{3}$` | `NV001`, `QL001` | Mã nhân viên bán hàng hoặc Quản lý |
| `CaLamViec` | `^CA0[1-3]$` | `CA01`, `CA02`, `CA03` | 3 ca cố định: Sáng, Chiều, Tối |
| `KhachHang` | `^KH\d{4,}$` | `KH0001` | Mã khách hàng thành viên |
| `SanPham` | `^SP\d{4,}$` | `SP0001` | Mã sản phẩm điện máy |
| `ChuongTrinhKhuyenMai`| `^KM\d{4,}$` | `KM0001` | Mã chương trình khuyến mãi |
| `HoaDon` | `^HD\d{6,}$` | `HD000001` | Mã hóa đơn bán hàng POS |
| `PhieuNhapHang` | `^PN\d{5,}$` | `PN00001` | Mã phiếu nhận hàng bổ sung |
| `PhieuDoiTra` | `^DT\d{5,}$` | `DT00001` | Mã phiếu đổi trả hàng hóa |

### Quy ước đặt tên biến & lớp trong Java

- **Entity classes**: Danh từ số ít viết hoa chữ cái đầu (`PascalCase`) — ví dụ: `SanPham`, `HoaDon`, `PhieuDoiTra`.
- **DAO classes**: `<TênEntity>_DAO` — ví dụ: `SanPham_DAO`, `HoaDon_DAO`.
- **GUI classes**: Tiền tố `Frm` cho `JFrame` (`FrmDangNhap`, `FrmChinh`) và `Pnl` cho `JPanel` (`PnlBanHangPOS`, `PnlSanPham`).
- **Swing Components**: `txt<Tên>` (`JTextField`), `btn<Tên>` (`JButton`), `tbl<Tên>` (`JTable`), `cbo<Tên>` (`JComboBox`), `lbl<Tên>` (`JLabel`).

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

## Bắt đầu

### Yêu cầu hệ thống

- **Java Development Kit (JDK):** Phiên bản 17 trở lên.
- **Hệ quản trị CSDL:** Microsoft SQL Server (2019 / 2022) & SQL Server Management Studio (SSMS).
- **IDE:** Eclipse IDE for Java Developers (hoặc IntelliJ IDEA).
- **Cấu hình mạng SQL Server:** Đã bật giao thức **TCP/IP** tại cổng mặc định `1433` trong *SQL Server Configuration Manager*.

### Cài đặt & Chạy dự án

**Bước 1 — Khởi tạo Cơ sở dữ liệu:**

1. Mở **SQL Server Management Studio (SSMS)** và kết nối vào SQL Server.
2. Mở file kịch bản `database/GreenSmart_sql.sql`.
3. Nhấn **Execute (F5)** để tự động tạo Database `GreenSmart`, 13 bảng dữ liệu, các khóa ngoại và dữ liệu mẫu (Seed Data).

**Bước 2 — Import dự án vào Eclipse IDE:**

1. Mở **Eclipse IDE** → Chọn `File` → `Import...` → `General` → `Existing Projects into Workspace`.
2. Chọn thư mục gốc của dự án `electronics-management-system` → Nhấn **Finish**.
3. Kiểm tra `Build Path`: Đảm bảo file `lib/sqljdbc42.jar` đã nằm trong mục **Referenced Libraries**.

**Bước 3 — Kiểm tra cấu hình kết nối & Chạy ứng dụng:**

1. Mở file `src/connectDB/ConnectDB.java` và kiểm tra thông số `url`, `user` (mặc định: `sa`), `password` khớp với máy cá nhân.
2. Nhấp chuột phải vào `src/common/App.java` → Chọn **Run As** → **Java Application**.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

## Lưu ý kỹ thuật & Quy tắc nghiệp vụ

> **Quy tắc 3 Ca làm việc & Đối soát Két tiền mặt (Quy định 3.1)**
>
> Siêu thị vận hành 3 ca cố định: **Ca Sáng (08:00–12:00)**, **Ca Chiều (12:00–17:00)**, **Ca Tối (17:00–21:30)**. Cuối ca, hệ thống tự động tính:
> `Tiền mặt lý thuyết trong két = Tiền quỹ đầu ca + Tổng thu tiền mặt hóa đơn - Tổng tiền mặt hoàn trả đổi trả`.
> Nếu số tiền kiểm đếm thực tế lệch so với lý thuyết, nhân viên bắt buộc phải nhập **Ghi chú giải trình** trước khi đóng ca.

> **Cơ chế Duyệt 2 bước đối với Nhập hàng & Đổi trả (Quy định 3.2 & 3.5)**
>
> - Nhân viên bán hàng không được phép tự ý sửa tay cột `soLuongTon` trên màn hình sản phẩm.
> - Khi lập `PhieuNhapHang` hoặc `PhieuDoiTra`, phiếu mang trạng thái **"Chờ duyệt"** (chưa làm thay đổi tồn kho). Chỉ khi **Người quản lý** bấm **"Phê duyệt"**, hệ thống mới thực thi Transaction cộng tồn kho hoặc hoàn tiền/thu hồi điểm.

> **Quy tắc Điểm tích lũy, Khuyến mãi & Thuế VAT (Quy định 3.3, 3.4 & 3.6)**
>
> - **Tích điểm:** Cứ mỗi `100.000 VNĐ` thanh toán thành công được cộng `+1 điểm`.
> - **Sử dụng điểm:** `1 điểm = 1.000 VNĐ` giảm trừ trực tiếp, nhưng tổng tiền giảm từ điểm **không vượt quá 50%** giá trị hóa đơn.
> - **Khuyến mãi:** Chỉ Quản lý được tạo khuyến mãi; mức giảm theo phần trăm tối đa **50%**; mức giảm tiền trực tiếp phải nhỏ hơn giá bán niêm yết.
> - **Thuế VAT:** Tự động tính **10%** trên tổng tiền sau khi đã trừ khuyến mãi và điểm thưởng.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

## Thành viên

**Nhóm 12 — Môn Phát Triển Ứng Dụng (Khoa CNTT — Đại học Công nghiệp TP.HCM - IUH)**

| Mã | Họ và tên | MSSV | Vai trò | Nhánh Git (`Branch`) | Phân hệ phụ trách chính |
| --- | ---------------------- | ---------- | ----------- | -------------------------- | ----------------------------------------------------------------------- |
| TV1 | **Huỳnh Vũ Trường** | `24689181` | Nhóm trưởng | `feature/pos-hoadon` | Bán hàng POS, Hóa đơn, Thanh toán Tiền mặt/VietQR, In hóa đơn & Khuyến mãi |
| TV2 | **Huỳnh Thái Trọng** | `24700991` | Time Keeper | `feature/taikhoan-ca` | Khung ứng dụng, Đăng nhập & Phân quyền, Nhân viên, Tài khoản & Ca làm việc |
| TV3 | **Đào Tấn Tuyên** | `24696741` | Reporter | `feature/khachhang-doitra` | Bộ giao diện `UITheme.java`, Quản lý Khách hàng, Điểm tích lũy & Đổi trả hàng |
| TV4 | **Võ Văn Tuấn** | `24694721` | Note Taker | `feature/hanghoa-baocao` | CSDL SQL Server tổng, Danh mục Sản phẩm & Tồn kho, Nhập hàng & Thống kê |

<p align="right">(<a href="#readme-top">back to top</a>)</p>