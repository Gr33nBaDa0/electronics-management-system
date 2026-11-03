# GREENSMART — HỆ THỐNG QUẢN LÝ CỬA HÀNG ĐIỆN MÁY
> **Đồ án môn học:** Phát triển Ứng dụng (Application Development)  
> **Trường:** Đại học Công nghiệp TP. Hồ Chí Minh (IUH) — Khoa Công nghệ Thông tin  
> **Nhóm thực hiện:** Nhóm 12  
> **Nền tảng công nghệ:** Java Swing (Desktop App) · JDBC · Microsoft SQL Server · Kiến trúc 3 lớp (3-Tier Architecture)

---

## 📌 1. Giới thiệu Dự án

**GreenSmart** là phần mềm quản lý bán hàng và vận hành toàn diện cho chuỗi cửa hàng điện máy hiện đại (kinh doanh Tivi, Tủ lạnh, Máy giặt, Điều hòa, Thiết bị gia dụng...). Ứng dụng được thiết kế tối ưu cho mô hình bán lẻ tại quầy (POS), kiểm soát ca làm việc, đối soát tiền mặt, quản lý xuất nhập tồn kho, chăm sóc khách hàng tích điểm thành viên và báo cáo doanh thu trực quan.

---

## 🏛️ 2. Mô hình Kiến trúc 3 Lớp (3-Tier Architecture)

Dự án được xây dựng chuẩn mực theo mô hình 3 lớp phân tách rõ ràng trách nhiệm:

```
[ Giao diện người dùng (Presentation Layer) ]
        │  src/gui/  (17 màn hình Swing: POS, Hóa đơn, Nhập hàng, Đổi trả, Báo cáo...)
        │  src/util/ (Tiện ích giao diện, render bảng biểu, VietQR, In hóa đơn)
        ▼
[ Lớp Nghiệp vụ & Truy xuất Dữ liệu (Business & DAO Layer) ]
        │  src/entity/ (13 thực thể nghiệp vụ với ràng buộc dữ liệu & logic tính toán)
        │  src/dao/    (16 lớp DAO xử lý Transaction Isolation, Khóa chống trùng lập)
        │  src/connectDB/ (Kết nối Singleton, tự động cấu hình bảo mật TLS 1.0/1.2/1.3)
        ▼
[ Lớp Cơ sở Dữ liệu (Database Layer) ]
        └─ database/GreenSmart_sql.sql (13 bảng, UDF kiểm tra thời gian, Khóa ngoại CASCADE, CHECK ràng buộc toàn vẹn)
```

---

## 🌟 3. Danh sách Chức năng Chính (Use Cases)

### 🔹 Phân hệ Bán hàng & Thu ngân (POS)
- **Lưới sản phẩm trực quan:** Hiển thị danh mục theo nhóm hàng kèm hình ảnh từ `resource/sanpham/` và thông số kỹ thuật.
- **Giỏ hàng linh hoạt:** Thêm, bớt, sửa số lượng, tự động tính thuế VAT (10%), chiết khấu khuyến mãi theo chương trình.
- **Thanh toán đa phương thức:**
  - **Tiền mặt:** Nhập tiền khách đưa, tự động tính tiền thừa trả khách.
  - **Chuyển khoản QR (VietQR):** Tích hợp sinh mã QR động ngân hàng thực tế từ `vietqr.io` với số tiền và nội dung đơn hàng.
- **In hóa đơn:** Hỗ trợ xem trước và in phiếu tính tiền chi tiết.

### 🔹 Phân hệ Vận hành & Ca làm việc
- **Quản lý ca trực:** Phân công theo 3 ca: Ca sáng (CA01), Ca chiều (CA02), Ca tối (CA03).
- **Mở ca & Đóng ca đối soát:** Ghi nhận tiền quỹ đầu ca, chốt tiền mặt thực đếm cuối ca, tự động tính tiền mặt lý thuyết và độ lệch quỹ.

### 🔹 Phân hệ Kho & Sản phẩm
- **Quản lý danh mục:** Thêm mới, cập nhật giá bán, thông số kỹ thuật, hình ảnh và trạng thái kinh doanh.
- **Phiếu nhập hàng:** Quy trình 2 bước: Nhân viên lập phiếu tạm `Chờ duyệt` -> Quản lý kiểm duyệt và xác nhận nhập kho thực tế (tự động tăng tồn kho).

### 🔹 Phân hệ Khách hàng & Khuyến mãi
- **Tích điểm tự động:** Tích lũy điểm sau mỗi đơn hàng (1 điểm = 1.000 VNĐ giảm giá).
- **Phân hạng hội viên:** Tự động thăng hạng: *Chuẩn* -> *Thân thiết* -> *VIP*.
- **Chương trình khuyến mãi:** Thiết lập mức giảm giá (%) theo thời hạn áp dụng cụ thể.

### 🔹 Phân hệ Đổi trả Hàng hóa
- Tiếp nhận yêu cầu đổi trả theo hóa đơn gốc trong thời hạn quy định (7 - 14 ngày).
- Kiểm tra tình trạng nguyên vẹn/lỗi, quản lý phê duyệt và hoàn tiền kèm điều chỉnh điểm thưởng.

### 🔹 Phân hệ Báo cáo & Thống kê
- Thống kê doanh thu theo ngày, khoảng thời gian hoặc theo từng ca làm việc.
- Bảng xếp hạng sản phẩm bán chạy nhất.
- Xuất dữ liệu bảng biểu ra file định dạng **CSV (UTF-8 BOM)** mở được trực tiếp trên Excel không lỗi font.

---

## 👥 4. Phân quyền Người dùng (Role-Based Access Control)

| Tính năng | Quản lý (`ADMIN`) | Nhân viên Bán hàng (`BAN_HANG`) |
|---|:---:|:---:|
| Bán hàng POS & Lập hóa đơn | ✅ | ✅ |
| Khách hàng & Tra cứu sản phẩm | ✅ | ✅ |
| Mở ca / Đóng ca trực của mình | ✅ | ✅ |
| Lập phiếu nhập hàng & Đổi trả | ✅ | ✅ |
| **Duyệt phiếu nhập kho & Phê duyệt đổi trả** | ✅ | ❌ |
| **Quản lý nhân viên & Cấp tài khoản** | ✅ | ❌ |
| **Thiết lập Khuyến mãi & Phân công ca** | ✅ | ❌ |
| **Báo cáo doanh thu & Xuất CSV** | ✅ | ❌ |
| **Sao lưu cơ sở dữ liệu** | ✅ | ❌ |

---

## 🚀 5. Hướng dẫn Cài đặt & Chạy Ứng dụng

### Yêu cầu môi trường
- **Java Development Kit (JDK):** JDK 17 hoặc JDK 21 trở lên.
- **Hệ quản trị CSDL:** Microsoft SQL Server 2014 / 2016 / 2019 / 2022.
- **IDE khuyên dùng:** Eclipse IDE for Java Developers, IntelliJ IDEA, hoặc NetBeans.

### Các bước thực hiện

#### Bước 1: Nạp Cơ sở Dữ liệu
1. Mở **SQL Server Management Studio (SSMS)** và kết nối tới SQL Server của bạn.
2. Mở file script `database/GreenSmart_sql.sql`.
3. Bấm **Execute (F5)** để tự động tạo cơ sở dữ liệu `GreenSmart`, toàn bộ 13 bảng ràng buộc toàn vẹn và nạp dữ liệu mẫu ban đầu.

#### Bước 2: Cấu hình Kết nối CSDL
Mở file `src/connectDB/ConnectDB.java` và kiểm tra tài khoản SQL Server của máy bạn:
```java
String url = "jdbc:sqlserver://localhost:1433;databaseName=GreenSmart;trustServerCertificate=true;encrypt=false;";
String user = "sa";
String password = "your_sql_password"; // Thay bằng mật khẩu sa trên máy bạn
```

#### Bước 3: Chạy Ứng dụng
1. Mở project trong Eclipse / IntelliJ.
2. Tìm tới file `src/main/Main.java`.
3. Nhấp chuột phải chọn **Run As -> Java Application**.

### Tài khoản đăng nhập thử nghiệm:
- **Tài khoản Quản lý:** `QL001` / Mật khẩu: `Green@123`
- **Tài khoản Bán hàng:** `NV001` / Mật khẩu: `Green@123`
- **Tài khoản Bán hàng 2:** `NV002` / Mật khẩu: `Green@123`
