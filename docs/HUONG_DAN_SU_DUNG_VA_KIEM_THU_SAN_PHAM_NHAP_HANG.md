# TÀI LIỆU ĐẶC TẢ NGHIỆP VỤ & HƯỚNG DẪN KIỂM THỬ
## PHÂN HỆ: QUẢN LÝ SẢN PHẨM & QUẢN LÝ NHẬP HÀNG
**Hệ thống Quản lý Cửa hàng Điện máy GreenSmart**  
**Học phần:** Phát triển ứng dụng (PTUD) - Học kỳ 1, Năm học 2026 - 2027  
**Sinh viên phụ trách:** Võ Văn Tuấn  
**Mã số sinh viên (MSSV):** 24694721  
**Nhóm:** 12  

---

## MỤC LỤC
1. [Giới thiệu tổng quan](#1-giới-thiệu-tổng-quan)
2. [Đặc tả nghiệp vụ Quản lý Sản phẩm](#2-đặc-tả-nghiệp-vụ-quản-lý-sản-phẩm)
3. [Đặc tả nghiệp vụ Quản lý Nhập hàng](#3-đặc-tả-nghiệp-vụ-quản-lý-nhập-hàng)
4. [Kiến trúc kỹ thuật và Cơ chế an toàn dữ liệu](#4-kiến-trúc-kỹ-thuật-và-cơ-chế-an-toàn-dữ-liệu)
5. [Quy trình kiểm thử chi tiết (Test Cases)](#5-quy-trình-kiểm-thử-chi-tiết-test-cases)
6. [Kịch bản Demo chuẩn phục vụ báo cáo bảo vệ](#6-kịch-bản-demo-chuẩn-phục-vụ-báo-cáo-bảo-vệ)

---

## 1. GIỚI THIỆU TỔNG QUAN

Trong hệ thống GreenSmart, hai phân hệ **Quản lý Sản phẩm** và **Quản lý Nhập hàng** đóng vai trò là xương sống vận hành:
- **Quản lý Sản phẩm (`SanPham_GUI.java`, `SanPham_DAO.java`):** Chịu trách nhiệm lưu trữ danh mục thiết bị điện tử, điện máy, đồ gia dụng; giá vốn, giá niêm yết, thông số kỹ thuật, tình trạng kinh doanh và hình ảnh đại diện của từng mặt hàng.
- **Quản lý Nhập hàng (`PhieuNhapHang_GUI.java`, `PhieuNhapHang_DAO.java`, `ChiTietPhieuNhapHang_DAO.java`):** Quản lý luân chuyển chứng từ nhập kho từ các nhà cung cấp, đảm bảo kiểm soát thực nhận và tự động cập nhật số lượng tồn kho chính xác.

### Danh mục tệp mã nguồn phụ trách:
- `src/gui/SanPham_GUI.java`: Giao diện quản lý danh mục sản phẩm và xem trước hình ảnh.
- `src/gui/PhieuNhapHang_GUI.java`: Giao diện lập phiếu, duyệt hàng thực nhận và quản lý dòng nhập.
- `src/dao/SanPham_DAO.java`: Xử lý giao dịch truy vấn, sinh mã tự động, thêm, sửa, xóa và biến động tồn kho sản phẩm.
- `src/dao/PhieuNhapHang_DAO.java`: Xử lý giao dịch lập phiếu, duyệt phiếu, hủy phiếu và ghi nhận kiểm soát nội bộ.
- `src/dao/ChiTietPhieuNhapHang_DAO.java`: Xử lý chi tiết các dòng hàng trong phiếu nhập.
- `src/entity/SanPham.java`, `src/entity/PhieuNhapHang.java`, `src/entity/ChiTietPhieuNhapHang.java`: Mô hình thực thể nghiệp vụ.
- `database/GreenSmart_sql.sql`: Script cấu trúc bảng CSDL, ràng buộc khóa ngoại, trigger và dữ liệu mẫu.
- `resource/hinhanh/`: Thư mục chứa tài nguyên ảnh sản phẩm thực tế (SP0001 - SP0017).

---

## 2. ĐẶC TẢ NGHIỆP VỤ QUẢN LÝ SẢN PHẨM

### 2.1. Quy tắc mã định danh (Product ID)
- Mã sản phẩm có định dạng chuẩn: `SPxxxx` với `xxxx` là số nguyên tự tăng từ `0001` đến `9999`.
- Mã được sinh hoàn toàn tự động thông qua phương thức `layMaTiepTheo()` và `taoMaTiepTheo()`.
- Để ngăn ngừa tranh chấp mã khi nhiều phiên làm việc cùng mở màn hình thêm mới, hệ thống áp dụng khóa phạm vi `WITH (UPDLOCK, HOLDLOCK)` ở mức cô lập `TRANSACTION_SERIALIZABLE`.

### 2.2. Quy tắc quản lý tồn kho (Stock Policy)
- Khi thêm mới một sản phẩm vào hệ thống, trường `SoLuongTon` bắt buộc khởi tạo bằng `0`.
- Hệ thống nghiêm cấm người dùng gõ tay hoặc chỉnh sửa trực tiếp số lượng tồn kho trên giao diện Sản phẩm.
- Tồn kho chỉ được phép tăng khi có **Phiếu Nhập Hàng** được phê duyệt thực tế, hoặc giảm khi phát sinh **Hóa đơn Bán hàng** thành công.

### 2.3. Quy tắc lưu trữ và hiển thị hình ảnh
- Ảnh sản phẩm được lưu trữ vật lý tại thư mục `resource/hinhanh/` với tên file chuẩn hóa theo mã: `SPxxxx.png` (hoặc `.jpg`).
- Trường `HinhAnh` trong bảng `SanPham` lưu đường dẫn tương đối để đảm bảo khả năng di động khi triển khai trên các máy tính khác nhau.
- Giao diện tích hợp bộ thu phóng ảnh tỷ lệ mượt (`Image.SCALE_SMOOTH`), tự động tính toán khung 140x140 pixel mà không gây biến dạng tỷ lệ khung hình gốc (aspect ratio).
- Trường hợp ảnh bị thất lạc hoặc đường dẫn sai lệch, hệ thống tự động kích hoạt cơ chế fallback tìm ảnh theo mã SP hoặc hiển thị nhãn thông báo thân thiện.

### 2.4. Phân quyền người dùng (Role-Based Access Control)
- **Quản lý (ADMIN):** Toàn quyền Tạo mới, Lưu thông tin, Xóa sản phẩm, Chọn ảnh và Thay đổi trạng thái kinh doanh.
- **Nhân viên (STAFF):** Chỉ được phép tra cứu, tìm kiếm, lọc theo khoảng giá, loại hàng, thương hiệu và xem chi tiết sản phẩm. Các nút chức năng chỉnh sửa tự động bị vô hiệu hóa.

---

## 3. ĐẶC TẢ NGHIỆP VỤ QUẢN LÝ NHẬP HÀNG

### 3.1. Quy trình phê duyệt hai bước (Two-Step Workflow)
1. **Bước 1 - Lập phiếu nhập (Nhân viên / Quản lý):**
   - Người lập chọn nhà cung cấp, nhập số chứng từ kèm theo và người giao hàng.
   - Thêm từng dòng sản phẩm kèm số lượng và đơn giá nhập thỏa thuận.
   - Khi bấm `Lưu chờ duyệt`, phiếu được tạo ở trạng thái `Chờ duyệt`. **Tồn kho chưa tăng** ở bước này.
2. **Bước 2 - Phê duyệt thực nhận (Chỉ dành cho Quản lý):**
   - Khi hàng hóa thực tế đã về tới kho và được kiểm đếm đủ số lượng, đạt chất lượng, Quản lý mở phiếu và bấm `Duyệt hàng thực nhận`.
   - Hệ thống tự động kích hoạt giao dịch cộng dồn số lượng tồn kho `SoLuongTon += SoLuong` cho từng sản phẩm trong phiếu.
   - Trạng thái phiếu chuyển thành `Đã nhập` và khóa vĩnh viễn mọi thao tác chỉnh sửa.

### 3.2. Nguyên tắc kiểm soát nội bộ 4 mắt (Four-Eyes Principle)
- Đây là nguyên tắc bảo mật và chống gian lận trọng yếu:
  - Nếu một Quản lý tự tay lập phiếu nhập, chính Quản lý đó **KHÔNG ĐƯỢC PHÉP TỰ DUYỆT** phiếu của mình (`!cuaMinh(p)`).
  - Nút `Duyệt hàng thực nhận` sẽ tự động bị mờ đi. Bắt buộc phải có một Quản lý khác vào kiểm đếm độc lập và bấm duyệt.

### 3.3. Quy định từ chối nhập hàng
- Trong trường hợp hàng hóa bị lỗi, hư hỏng trong quá trình vận chuyển hoặc sai quy cách so với hợp đồng:
  - Quản lý bấm nút `Từ chối`.
  - Hệ thống yêu cầu bắt buộc nhập rõ `Lý do từ chối` (không được để trống).
  - Trạng thái phiếu chuyển sang `Từ chối`, kho không tăng, phiếu bị khóa để lưu vết kiểm toán.

---

## 4. KIẾN TRÚC KỸ THUẬT VÀ CƠ CHẾ AN TOÀN DỮ LIỆU

### 4.1. Quản lý Giao dịch (Transaction Management)
Mọi thao tác thay đổi nhiều bảng liên quan đều được thực thi trong một Transaction duy nhất:
```java
Connection con = ConnectDB.getConnection();
boolean oldAutoCommit = con.getAutoCommit();
int oldIsolation = con.getTransactionIsolation();
try {
    con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
    con.setAutoCommit(false);

    // 1. Cập nhật trạng thái phiếu nhập
    // 2. Duyệt từng dòng chi tiết để tăng tồn kho qua SanPham_DAO.doiTon()
    
    con.commit();
} catch (Exception e) {
    con.rollback();
    throw e;
} finally {
    con.setAutoCommit(oldAutoCommit);
    con.setTransactionIsolation(oldIsolation);
}
```

### 4.2. Khóa chống xung đột đồng thời (Concurrency Control)
- Sử dụng gợi ý khóa SQL Server: `WITH (UPDLOCK, HOLDLOCK)`.
- Khi tính toán mã kế tiếp hoặc kiểm tra điều kiện xóa sản phẩm, khóa này ngăn ngừa hiện tượng đọc dữ liệu bẩn (Dirty Read) hoặc hiện tượng bóng ma (Phantom Read) khi nhiều máy trạm gửi lệnh cùng thời điểm.

---

## 5. QUY TRÌNH KIỂM THỬ CHI TIẾT (TEST CASES)

| Mã TC | Chức năng kiểm thử | Dữ liệu đầu vào | Kết quả mong đợi | Kết quả thực tế | Đánh giá |
|:---:|---|---|---|---|:---:|
| **TC01** | Tự sinh mã sản phẩm | Bấm nút `Tạo mới` | Mã hiển thị đúng định dạng `SPxxxx` kế tiếp, ô mã bị khóa không cho sửa | Mã tự tăng chính xác, ô mã read-only | **PASS** |
| **TC02** | Chọn ảnh đại diện từ đĩa cứng | Chọn file `SP0014.jpg` | Ảnh hiển thị thu phóng vừa vặn khung 140x140, giữ nguyên tỷ lệ | Ảnh sắc nét, không méo hình | **PASS** |
| **TC03** | Thêm sản phẩm mới | Nhập đủ tên, loại, hãng, giá bán, giá nhập | Lưu thành công, ảnh tự copy vào `resource/hinhanh/`, tồn kho = 0 | Đã lưu, tồn kho khởi tạo 0 | **PASS** |
| **TC04** | Kiểm tra dữ liệu rỗng | Để trống Tên sản phẩm, bấm `Lưu` | Hệ thống chặn lại, xuất cảnh báo: "Vui lòng nhập Tên sản phẩm" | Bắt lỗi chính xác | **PASS** |
| **TC05** | Bộ lọc tìm kiếm kết hợp | Lọc loại "Tivi" + Khoảng giá 5tr - 15tr | Bảng chỉ hiển thị đúng các dòng tivi có giá trong khoảng quy định | Lọc nhanh, chính xác 100% | **PASS** |
| **TC06** | Xóa sản phẩm có tồn kho > 0 | Chọn sản phẩm tồn 20 cái, bấm `Xóa` | Báo lỗi: "Chỉ xóa sản phẩm tồn 0, chưa có lịch sử" | Chặn xóa an toàn | **PASS** |
| **TC07** | Thêm chi tiết phiếu nhập trùng | Thêm 2 dòng cùng chọn mã `SP0001` | Cảnh báo: "Sản phẩm đã có, bỏ dòng cũ rồi thêm lại" | Ngăn chặn trùng lặp dòng | **PASS** |
| **TC08** | Lập phiếu nhập chờ duyệt | Nhập NCC, thêm 2 sản phẩm, bấm `Lưu` | Phiếu tạo thành công với trạng thái `Chờ duyệt`, tồn kho CHƯA tăng | Lưu đúng, tồn kho giữ nguyên | **PASS** |
| **TC09** | Tự duyệt phiếu của chính mình | Tài khoản Quản lý mở phiếu do chính mình lập | Nút `Duyệt hàng thực nhận` bị vô hiệu hóa (disabled) | Kiểm soát 4 mắt hoạt động chuẩn | **PASS** |
| **TC10** | Quản lý khác duyệt hàng thực nhận | Quản lý 2 mở phiếu của Quản lý 1 bấm `Duyệt` | Phiếu chuyển sang `Đã nhập`, tồn kho các mặt hàng lập tức tăng tương ứng | Tồn kho tăng chính xác theo số lượng nhập | **PASS** |
| **TC11** | Từ chối phiếu nhập | Bấm `Từ chối` và nhập lý do "Hàng móp méo" | Phiếu chuyển `Từ chối`, lưu lý do, tồn kho không tăng | Lưu vết từ chối đầy đủ | **PASS** |
| **TC12** | Khóa phiếu sau khi duyệt | Mở lại phiếu đã duyệt hoặc đã từ chối | Toàn bộ form và nút Thêm/Sửa/Xóa bị vô hiệu hóa | Bảo vệ toàn vẹn lịch sử | **PASS** |

---

## 6. KỊCH BẢN DEMO CHUẨN PHỤC VỤ BÁO CÁO BẢO VỆ

### Bước 1: Giới thiệu vai trò và tổng quan (30 giây)
- Kính thưa Thầy/Cô, em là **Võ Văn Tuấn (MSSV 24694721)**, chịu trách nhiệm thiết kế CSDL và lập trình hai phân hệ: Quản lý Sản phẩm và Quản lý Nhập hàng.
- Đây là hai phân hệ cung cấp nguồn hàng và hình ảnh phục vụ trực tiếp cho màn hình Bán hàng (POS) của bạn Trường và quy trình Đổi trả của bạn Tuyên.

### Bước 2: Trình diễn nghiệp vụ Sản phẩm (1 phút)
1. Mở màn hình Sản phẩm, đăng nhập quyền Quản lý.
2. Bấm `Tạo mới`: Chỉ vào ô mã sản phẩm để thấy mã tự động nhảy lên `SP0017`.
3. Nhập tên sản phẩm mẫu: `Máy lọc không khí Coway`, giá bán `7.500.000đ`, giá nhập `5.500.000đ`.
4. Bấm `Chọn ảnh...` và chọn file ảnh từ máy. Nhấn mạnh việc ảnh hiển thị vừa vặn không méo mó và được tự động copy vào thư mục dự án khi bấm `Lưu`.
5. Sau khi lưu, chỉ vào cột `Tồn kho = 0` trên bảng và giải thích: Sản phẩm mới tạo bắt buộc tồn kho bằng 0 để chống tồn ảo.

### Bước 3: Trình diễn luồng Nhập hàng và Phê duyệt 4 mắt (2 phút)
1. Chuyển sang màn hình `Phiếu Nhập Hàng`, bấm `Phiếu mới`.
2. Nhập nhà cung cấp `Công ty Cổ phần Coway Việt Nam`, số chứng từ `NK-CW-2026`.
3. Chọn sản phẩm `SP0017` vừa tạo, chọn số lượng `20 cái`, giá nhập `5.500.000đ`, bấm `Thêm dòng (F4)`.
4. Bấm `Lưu chờ duyệt`: Quay lại bảng sản phẩm để chứng minh tồn kho vẫn bằng 0 vì hàng chưa duyệt.
5. Chỉ vào nút `Duyệt hàng thực nhận` đang bị mờ: Giải thích nguyên tắc 4 mắt (em là người lập phiếu nên hệ thống khóa không cho tự duyệt).
6. Đăng nhập bằng tài khoản Quản lý thứ hai, mở lại phiếu này -> Nút `Duyệt hàng thực nhận` sáng lên.
7. Bấm `Duyệt hàng thực nhận` -> Phiếu chuyển sang `Đã nhập` và bị khóa.
8. Mở lại màn hình Sản phẩm: Tồn kho `SP0017` đã chính thức nhảy lên đúng `20 cái`.

---
*Tài liệu kỹ thuật được lập bởi sinh viên Võ Văn Tuấn - MSSV: 24694721.*


---

## 7. ĐẶC TẢ CẤU TRÚC DỮ LIỆU & RÀNG BUỘC TOÀN VẸN (DATABASE SCHEMA)

### 7.1. Bảng SanPham (Danh mục hàng hóa)
- `MaSanPham VARCHAR(6) PRIMARY KEY`: Mã sản phẩm chuẩn hóa `SPxxxx`.
- `TenSanPham NVARCHAR(100) NOT NULL`: Tên hiển thị đầy đủ của thiết bị.
- `LoaiHang NVARCHAR(50) NOT NULL`: Phân loại nhóm thiết bị (Tivi, Tủ lạnh, Máy giặt, Gia dụng, Điện lạnh...).
- `ThuongHieu NVARCHAR(50) NOT NULL`: Nhãn hàng sản xuất (Sony, LG, Samsung, Panasonic, Sharp, Toshiba...).
- `DonGiaBan FLOAT NOT NULL CHECK (DonGiaBan >= 0)`: Giá bán lẻ niêm yết đến khách hàng.
- `GiaNhap FLOAT NOT NULL CHECK (GiaNhap >= 0)`: Giá vốn nhập vào gần nhất để theo dõi biên lợi nhuận.
- `ThongSoKiThuat NVARCHAR(500)`: Tóm tắt thông số kỹ thuật (công suất, kích thước, công nghệ Inverter...).
- `XuatXu NVARCHAR(50) NOT NULL`: Nước sản xuất (Việt Nam, Thái Lan, Malaysia, Nhật Bản, Trung Quốc...).
- `TrangThai NVARCHAR(30) NOT NULL DEFAULT N'Đang bán'`: Tình trạng ('Đang bán' hoặc 'Ngừng bán').
- `ThoiHanBaoHanh INT NOT NULL DEFAULT 12 CHECK (ThoiHanBaoHanh >= 0)`: Thời gian bảo hành tính theo tháng.
- `SoLuongTon INT NOT NULL DEFAULT 0 CHECK (SoLuongTon >= 0)`: Tồn kho thực tế sẵn sàng xuất bán.
- `HinhAnh NVARCHAR(255)`: Đường dẫn tương đối lưu trữ ảnh đại diện `resource/hinhanh/SPxxxx.png`.

### 7.2. Bảng PhieuNhapHang (Chứng từ nhập kho)
- `MaPhieuNhap VARCHAR(20) PRIMARY KEY`: Mã chứng từ nhập hàng.
- `NgayLap DATETIME2(0) NOT NULL DEFAULT SYSDATETIME()`: Thời điểm nhân viên lập phiếu.
- `SoChungTu NVARCHAR(50)`: Mã số hóa đơn / phiếu giao hàng từ nhà cung cấp kèm theo kiện hàng.
- `NhaCungCap NVARCHAR(100) NOT NULL`: Tên đối tác / nhà phân phối cung cấp hàng hóa.
- `NguoiGiao NVARCHAR(100)`: Họ tên nhân viên giao nhận / tài xế của bên vận chuyển.
- `MaNhanVienLap VARCHAR(10) NOT NULL FOREIGN KEY REFERENCES NhanVien(MaNhanVien)`: Nhân viên chịu trách nhiệm kiểm đếm ban đầu.
- `MaQuanLyDuyet VARCHAR(10) FOREIGN KEY REFERENCES NhanVien(MaNhanVien)`: Quản lý thẩm định và ký duyệt thực nhận.
- `TrangThai NVARCHAR(30) NOT NULL DEFAULT N'Chờ duyệt'`: Ba trạng thái chuẩn ('Chờ duyệt', 'Đã nhập', 'Từ chối').
- `NgayDuyet DATETIME2(0)`: Thời điểm quản lý phê duyệt hoặc từ chối phiếu.
- `LyDoTuChoi NVARCHAR(255)`: Ghi chú bắt buộc khi từ chối nhận hàng để đối soát với nhà cung cấp.

### 7.3. Bảng ChiTietPhieuNhapHang (Dòng hàng nhập kho)
- `MaPhieuNhap VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES PhieuNhapHang(MaPhieuNhap)`: Khóa ngoại liên kết đầu phiếu.
- `MaSanPham VARCHAR(6) NOT NULL FOREIGN KEY REFERENCES SanPham(MaSanPham)`: Khóa ngoại trỏ đến danh mục hàng hóa.
- `SoLuong INT NOT NULL CHECK (SoLuong > 0)`: Số lượng kiện hàng thực nhận.
- `DonGiaNhap FLOAT NOT NULL CHECK (DonGiaNhap >= 0)`: Giá nhập thực tế trên từng đơn vị sản phẩm.
- `ThanhTien AS CONVERT(FLOAT(53), ROUND(SoLuong * DonGiaNhap, 2))`: Cột tính toán tự động giá trị thành tiền.
- `PRIMARY KEY (MaPhieuNhap, MaSanPham)`: Ràng buộc khóa chính phức hợp chống trùng sản phẩm trên cùng một phiếu.

---

## 8. HƯỚNG DẪN CÀI ĐẶT & PHỤC HỒI CƠ SỞ DỮ LIỆU THỰC HÀNH

### 8.1. Các bước chạy script CSDL GreenSmart
1. Mở phần mềm **Microsoft SQL Server Management Studio (SSMS)**.
2. Kết nối tới SQL Server Instance cục bộ (Ví dụ: `localhost` hoặc `.\SQLEXPRESS`).
3. Mở file `database/GreenSmart_sql.sql` trong thư mục dự án.
4. Bấm **Execute (F5)** để khởi tạo toàn bộ 13 bảng, ràng buộc toàn vẹn và nạp dữ liệu mẫu ban đầu.
5. Kiểm tra kết quả: Bảng `SanPham` có đủ 17 bản ghi và bảng `PhieuNhapHang` có đủ các phiếu mẫu.

### 8.2. Cấu hình kết nối JDBC trong mã nguồn
- Tệp cấu hình: `config.properties`
- Cấu hình chuẩn kết nối TLS/SSL tương thích JDK 17+:
  ```properties
  db.url=jdbc:sqlserver://localhost:1433;databaseName=GreenSmart;encrypt=true;trustServerCertificate=true;
  db.user=sa
  db.password=sapassword
  backup.file=C:\\DuAnPTUD\\GreenSmart_Backup.bak
  ```

---

## 9. DANH MỤC 20 TEST CASES NÂNG CAO CHO BUỔI BẢO VỆ

| ID | Nhóm chức năng | Tình huống kiểm thử | Dữ liệu kiểm thử | Kết quả kỳ vọng |
|:---:|---|---|---|---|
| TC13 | Tìm kiếm SP | Tìm kiếm theo tên không phân biệt hoa thường | Nhập "tivi sony" | Hiển thị đúng Smart TV Sony |
| TC14 | Tìm kiếm SP | Tìm kiếm theo thông số kỹ thuật | Nhập "Inverter" | Hiển thị toàn bộ tủ lạnh/máy giặt Inverter |
| TC15 | Lọc giá SP | Nhập giá từ > giá đến | Giá từ 10tr, Giá đến 5tr | Báo lỗi: "Giá từ không được lớn hơn giá đến" |
| TC16 | Sửa SP | Cập nhật giá bán lẻ của SP0001 | Sửa từ 14.990.000 thành 15.500.000 | Cập nhật thành công vào CSDL |
| TC17 | Sửa SP | Đổi trạng thái từ "Đang bán" sang "Ngừng bán" | Chọn trạng thái "Ngừng bán" | Sản phẩm không còn xuất hiện trên màn POS |
| TC18 | Xóa SP | Xóa sản phẩm chưa có giao dịch | Tạo SP mới SP0018, bấm Xóa | Xóa thành công khỏi CSDL |
| TC19 | Nhập hàng | Nhập số lượng âm hoặc bằng 0 | Gõ SL = 0 | Spinner chặn lại (giá trị tối thiểu là 1) |
| TC20 | Nhập hàng | Đơn giá nhập chứa chữ cái | Gõ "nam trieu" | Báo lỗi định dạng số không hợp lệ |
| TC21 | Nhập hàng | Bỏ bớt dòng hàng trước khi lưu | Bấm chọn dòng ấm siêu tốc -> bấm "Bỏ dòng" | Dòng hàng bị xóa khỏi bảng chi tiết |
| TC22 | Nhập hàng | Tính tổng tiền tự động | Thêm 2 SP: 10 cái x 1tr + 5 cái x 2tr | Tổng tiền hiển thị chính xác 20.000.000đ |
| TC23 | Phê duyệt | Quản lý kiểm tra tồn kho tăng | Ghi nhớ tồn SP0005 là 30, duyệt nhập 20 | Tồn kho SP0005 nhảy lên chính xác 50 |
| TC24 | Phân quyền | Nhân viên đăng nhập mở form Nhập hàng | Tài khoản nhân viên | Chỉ được lập phiếu chờ, không có nút duyệt |
| TC25 | Kiểm soát | Kiểm tra rollback khi lỗi kết nối giữa chừng | Ngắt mạng khi đang duyệt chi tiết | Toàn bộ transaction bị hủy, kho giữ nguyên |

---
*Tài liệu kỹ thuật chính thức phục vụ nghiệm thu môn học PTUD - GreenSmart.*
