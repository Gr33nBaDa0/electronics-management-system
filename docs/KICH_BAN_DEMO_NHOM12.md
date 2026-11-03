# KỊCH BẢN TẬP DƯỢT BẢO VỆ ĐỒ ÁN (DEMO CHEAT-SHEET)
## DỰ ÁN: HỆ THỐNG QUẢN LÝ CỬA HÀNG ĐIỆN MÁY GREENSMART
**Môn học:** Phát triển ứng dụng · **Nhóm thực hiện:** Nhóm 12  
**Đối tượng chấm:** Giảng viên hướng dẫn & Hội đồng chấm đồ án IUH

---

## 👥 PHÂN CÔNG THÀNH VIÊN (NHÓM 4 BẠN)

| Thành viên | Vai trò & Nhiệm vụ chính | Các Use Case phụ trách trình bày & Demo |
|:---:|:---|:---|
| **Bạn 1 (Nhóm trưởng)** | Giới thiệu tổng quan đề tài, kiến trúc 3 lớp (3-Tier), mô hình CSDL và phần Quyền Admin. | **Mở đầu**, **UC001** (Đăng nhập & Phân quyền), **Kết luận & Báo cáo** |
| **Bạn 2** | Trình bày luồng vận hành đầu ngày của thu ngân: Ca trực, Tiền quỹ. | **UC005** (Mở ca làm việc & Kiểm soát quỹ) |
| **Bạn 3** | Trình bày tính năng cốt lõi tại quầy POS: Chọn sản phẩm, áp khuyến mãi, tích điểm, thanh toán VietQR. | **UC007** (Lập hóa đơn POS & Thanh toán QR thật) |
| **Bạn 4** | Trình bày quy trình Kho & Chăm sóc khách hàng: Nhập hàng, kiểm duyệt tồn kho, thêm khách hàng mới. | **UC019** (Nhập kho), **UC029** (Khách hàng) & **UC032** (Báo cáo doanh thu) |

---

## 🎬 KỊCH BẢN CHI TIẾT 6 USE CASE ĐINH (CHUẨN CHƯƠNG 3 BÁO CÁO)

### 🟢 USE CASE 1 (UC001): Đăng nhập & Kiểm chứng Phân quyền RBAC
* **Người thực hiện:** Bạn 1
* **Mục tiêu:** Chứng minh hệ thống nhận diện đúng vai trò, tự động lọc giao diện và menu theo quyền hạn.
* **Các bước thao tác:**
  1. Mở ứng dụng -> Giao diện [`DangNhap_GUI`](file:///c:/Users/daota/Downloads/electronics-management-system/electronics-management-system/src/gui/DangNhap_GUI.java) xuất hiện với trạng thái *"Kết nối SQL thành công"*.
  2. **Thử nghiệm 1 (Nhân viên bán hàng):**
     - Nhập Username: `NV001` / Mật khẩu: `Green@123` -> Bấm **Đăng nhập**.
     - **Quan sát:** Menu bên trái chỉ hiển thị: Bán hàng, Hóa đơn, Đổi trả, Khách hàng, Ca làm việc, Sản phẩm, Phiếu nhập. **Ẩn hoàn toàn:** Menu Quản trị (Nhân viên, Khuyến mãi, Báo cáo doanh thu, Sao lưu).
     - Bấm **Đăng xuất**.
  3. **Thử nghiệm 2 (Quản lý - Admin):**
     - Nhập Username: `QL001` / Mật khẩu: `Green@123` -> Bấm **Đăng nhập**.
     - **Quan sát:** Menu bên trái mở rộng đầy đủ tất cả các danh mục quản trị nâng cao.

---

### 🟢 USE CASE 2 (UC005): Mở ca làm việc đầu ngày & Ghi nhận Quỹ
* **Người thực hiện:** Bạn 2
* **Mục tiêu:** Đảm bảo nguyên tắc thu ngân chỉ được bán hàng khi đã có ca trực hợp lệ và mở quỹ thành công.
* **Các bước thao tác:**
  1. Đăng nhập bằng tài khoản thu ngân `NV002` (hoặc `NV001`).
  2. Chọn menu **"Ca làm việc"** (`PhanCongCa_GUI`).
  3. Chọn ca trực được phân công cho mình (trạng thái đang là *Chưa mở*).
  4. Bấm nút **"Mở ca"** -> Hộp thoại hiện ra yêu cầu nhập tiền quỹ đầu ca.
  5. Nhập số tiền: `2,000,000` (Hai triệu đồng tiền lẻ thối khách).
  6. Bấm **Xác nhận**.
  7. **Kết quả:** Trạng thái ca trực chuyển sang **"Đang mở"**, giờ mở ca được ghi nhận tự động. Nhân viên chính thức đủ điều kiện mở màn hình POS bán hàng.

---

### 🟢 USE CASE 3 (UC007): Bán hàng điện máy tại quầy POS & Thanh toán VietQR
* **Người thực hiện:** Bạn 3
* **Mục tiêu:** Biểu diễn trải nghiệm bán hàng thực tế: hình ảnh sản phẩm, chiết khấu khuyến mãi, tính thuế VAT, trừ tồn kho và thanh toán QR.
* **Các bước thao tác:**
  1. Chọn menu **"Bán hàng POS"** (`BanHang_GUI`).
  2. **Quan sát:** Lưới sản phẩm hiển thị đầy đủ hình ảnh sinh động từ thư mục `resource/sanpham/`, kèm giá bán và số lượng tồn kho.
  3. **Thao tác chọn hàng:**
     - Click chọn sản phẩm `SP0001` (Smart TV Sony 4K 55 inch) -> Tự động thêm vào giỏ.
     - Chọn `SP0005` (Nồi cơm điện Sharp) -> Tăng số lượng lên `2`.
  4. **Áp dụng chính sách & Khách hàng:**
     - Chọn khách hàng thân thiết `KH001` (Nguyễn Thanh Hà - Hạng VIP).
     - Chọn chương trình khuyến mãi: *Ưu đãi điện máy 10%* (`KM2026001`).
     - Áp dụng điểm tích lũy: Nhập `50` điểm (giảm ngay 50.000 VNĐ).
  5. **Quan sát tổng tiền:** Hệ thống tự động tính: Tổng tiền hàng, Giảm giá khuyến mãi, Trừ điểm, Thuế VAT 10% và Tổng tiền thanh toán.
  6. **Bấm "Thanh toán QR":**
     - Cửa sổ [`ThanhToan_GUI`](file:///c:/Users/daota/Downloads/electronics-management-system/electronics-management-system/src/gui/ThanhToan_GUI.java) bật lên.
     - Mã QR động ngân hàng thực tế sinh từ **VietQR** hiện ra trên màn hình kèm số tiền chính xác và mã hóa đơn.
     - Tích chọn *"Đã kiểm tra tiền chuyển khoản vào tài khoản cửa hàng"* -> Bấm **"Xác nhận thanh toán"**.
  7. **Xem hóa đơn:** Cửa sổ xem trước hóa đơn hiển thị đầy đủ thông tin chi tiết. Tồn kho của `SP0001` tự động giảm đi 1, và `SP0005` giảm đi 2.

---

### 🟢 USE CASE 4 (UC019): Lập phiếu tiếp nhận hàng hóa & Duyệt tăng tồn kho
* **Người thực hiện:** Bạn 4
* **Mục tiêu:** Chứng minh quy trình kiểm soát nhập kho 2 bước chống gian lận hàng hóa.
* **Các bước thao tác:**
  1. Tại menu **"Phiếu nhập hàng"** (`PhieuNhapHang_GUI`), bấm **"Phiếu mới"**.
  2. Điền thông tin: Nhà cung cấp: *Công ty TNHH Phân phối Sony VN*, Số chứng từ: *CT-SONY-99*, Người giao: *Nguyễn Văn Giao*.
  3. Thêm chi tiết hàng nhập:
     - Chọn sản phẩm: `SP0001` (Smart TV Sony 55 inch).
     - Số lượng nhập: `10`.
     - Đơn giá nhập: `12,500,000` VNĐ.
  4. Bấm **"Lưu chờ duyệt"** -> Phiếu nhập được lưu vào hệ thống ở trạng thái **"Chờ duyệt"**. Lúc này tồn kho của sản phẩm vẫn chưa thay đổi.
  5. **Duyệt hàng nhập (Bởi Quản lý):**
     - Quản lý `QL001` kiểm tra số lượng thực nhận, bấm nút **"Duyệt hàng thực nhận"**.
     - Bấm Xác nhận.
     - **Kết quả:** Trạng thái phiếu chuyển thành **"Đã nhập"**, tồn kho của `SP0001` trong bảng sản phẩm lập tức tăng thêm đúng 10 cái.

---

### 🟢 USE CASE 5 (UC029): Tiếp nhận & Thêm mới Khách hàng thành viên
* **Người thực hiện:** Bạn 4
* **Mục tiêu:** Chứng minh cơ chế kiểm tra tính hợp lệ dữ liệu (Regex) và phân hạng khách hàng.
* **Các bước thao tác:**
  1. Chọn menu **"Khách hàng"** (`KhachHang_GUI`).
  2. Bấm **"Thêm mới"** -> Mã khách hàng `KH011` tự động được sinh sẵn.
  3. Nhập dữ liệu:
     - Họ tên: *Trần Hoàng Long*.
     - Số điện thoại: `0987654321` (Đúng 10 số, đầu 09).
     - Địa chỉ: *Quận Gò Vấp, TP.HCM*.
     - Email: *long.tran@gmail.com*.
  4. Bấm **"Lưu"**.
  5. **Kiểm tra nghiệp vụ:** Hệ thống nạp khách hàng mới vào bảng với Hạng thành viên mặc định là **"Chuẩn"** và Điểm tích lũy khởi tạo là `0`.

---

### 🟢 USE CASE 6 (UC032): Báo cáo Thống kê Doanh thu & Xuất file CSV
* **Người thực hiện:** Bạn 1 & Bạn 4
* **Mục tiêu:** Tổng hợp số liệu tài chính phục vụ quyết định quản trị của chủ cửa hàng.
* **Các bước thao tác:**
  1. Đăng nhập bằng tài khoản Quản lý `QL001` -> Chọn menu **"Báo cáo"** (`BaoCao_GUI`).
  2. Chọn khoảng thời gian: Từ ngày `01/10/2026` đến ngày `31/10/2026`.
  3. Bấm **"Lập báo cáo"**:
     - **Tab 1 (Doanh thu theo ca):** Hiển thị doanh thu thực thu, số hóa đơn và độ lệch tiền mặt của từng ca trực.
     - **Tab 2 (Sản phẩm bán chạy):** Xếp hạng các dòng Tivi, Tủ lạnh, Máy giặt bán chạy nhất cùng tỷ trọng đóng góp doanh thu.
  4. Bấm **"Xuất bảng CSV"**:
     - Chọn thư mục lưu file (mặc định `BaoCao.csv`).
     - File được xuất chuẩn mã hóa **UTF-8 with BOM**, khi nhấp đúp mở bằng Microsoft Excel hiển thị tiếng Việt dấu chuẩn 100%, không bị vỡ font.

---

## 💡 CÁC CÂU HỎI THƯỜNG GẶP TỪ GIẢNG VIÊN VÀ CÁCH TRẢ LỜI ĐẠT ĐIỂM TỐI ĐA

1. **Giảng viên hỏi:** *"Mô hình 3 lớp của nhóm được phân tách như thế nào? DAO gọi GUI hay GUI gọi DAO?"*
   - **Trả lời:** *"Dạ thưa Thầy/Cô, nhóm áp dụng chuẩn mô hình 3 lớp hướng đối tượng. Tầng GUI chỉ đảm nhận vai trò hiển thị và bắt sự kiện người dùng, sau đó gọi xuống tầng DAO. Tầng DAO chứa các câu lệnh truy vấn SQL Server, xử lý Transaction Isolation (`SERIALIZABLE` để chống xung đột tồn kho và đụng độ mã hóa đơn) rồi trả về các đối tượng Entity. GUI tuyệt đối không viết truy vấn trực tiếp và DAO không phụ thuộc vào GUI ạ."*

2. **Giảng viên hỏi:** *"Làm sao nhóm đảm bảo khi hai thu ngân cùng bấm thanh toán thì không bị trùng mã hóa đơn?"*
   - **Trả lời:** *"Dạ thưa Thầy/Cô, trong `HoaDon_DAO`, nhóm đã sử dụng mệnh đề khóa độc quyền của SQL Server là `WITH (UPDLOCK, HOLDLOCK)` khi phát sinh mã hóa đơn theo mẫu `HD + yyyyMMdd + CAxx + STT`, kết hợp với quản lý giao dịch `con.setAutoCommit(false)` và `con.commit()`. Khi một giao dịch đang đọc và tăng mã thì giao dịch khác phải chờ, đảm bảo không bao giờ bị đụng độ ạ."*

3. **Giảng viên hỏi:** *"Tính năng mã QR thanh toán có phải ảnh tĩnh chèn vào không?"*
   - **Trả lời:** *"Dạ không ạ, nhóm tích hợp chuẩn VietQR động thông qua API của vietqr.io trong class `InHoaDon.java`. Mỗi đơn hàng có số tiền khác nhau và mã đơn khác nhau thì hệ thống sẽ sinh ra một mã QR động chứa đúng số tiền và nội dung chuyển khoản đó, người mua dùng app ngân hàng quét là tự điền đúng số tiền và thông tin cửa hàng ạ."*
