

USE [master];
GO
IF DB_ID(N'GreenSmart') IS NULL
    EXEC(N'CREATE DATABASE [GreenSmart]');
GO
USE [GreenSmart];
GO


-- 1. NhanVien
CREATE TABLE NhanVien (
    MaNhanVien VARCHAR(5) NOT NULL PRIMARY KEY,
    HoTen NVARCHAR(100) NOT NULL,
    VaiTro NVARCHAR(20) NOT NULL,
    SDT VARCHAR(10) NOT NULL,
    Email VARCHAR(255) NULL,
    DiaChi NVARCHAR(255) NULL,
    SoCCCD VARCHAR(12) NOT NULL UNIQUE,
    HinhTheNV VARBINARY(MAX) NULL,
    GioiTinh NVARCHAR(4) NOT NULL,
    NgaySinh DATE NOT NULL,
    NgayBatDauLV DATE NOT NULL
);

INSERT INTO NhanVien (
    MaNhanVien, HoTen, VaiTro, SDT, Email, DiaChi, SoCCCD, HinhTheNV, GioiTinh, NgaySinh, NgayBatDauLV
)
VALUES
('QL001', N'Lê Văn Quang', N'Quản lý', '0900000001', 'quang@example.com', N'TP. Hồ Chí Minh', '079000000001', NULL, 'Nam', '1990-05-12', '2020-01-02'),
('NV001', N'Nguyễn Văn An', N'Bán hàng', '0900000002', 'an@example.com', N'TP. Hồ Chí Minh', '079000000002', NULL, 'Nam', '2000-08-20', '2024-01-02'),
('NV002', N'Trần Thị Bình', N'Bán hàng', '0900000003', 'binh@example.com', N'TP. Hồ Chí Minh', '079000000003', NULL, N'Nữ', '2001-03-15', '2024-01-02'),
('NV003', N'Phạm Minh Châu', N'Bán hàng', '0900000004', 'chau@example.com', N'TP. Hồ Chí Minh', '079000000004', NULL, N'Khác', '2002-11-02', '2025-01-02');

-- 2. TaiKhoan
CREATE TABLE TaiKhoan (
    TenDangNhap VARCHAR(5) NOT NULL PRIMARY KEY,
    Quyen VARCHAR(10) NOT NULL,
    MatKhau VARBINARY(64) NOT NULL,
    MaNhanVien VARCHAR(5) NOT NULL UNIQUE,
    CONSTRAINT FK_TaiKhoan_NhanVien FOREIGN KEY(MaNhanVien) REFERENCES NhanVien(MaNhanVien)
);

INSERT INTO TaiKhoan (
    TenDangNhap, Quyen, MatKhau, MaNhanVien
)
VALUES
('QL001', 'ADMIN','Green@123' , 'QL001'),
('NV001', 'BAN_HANG','Green@123' , 'NV001'),
('NV002', 'BAN_HANG','Green@123', 'NV002'),
('NV003', 'BAN_HANG','Green@123' , 'NV003');

-- 3. CaLamViec
CREATE TABLE CaLamViec (
    MaCa VARCHAR(4) NOT NULL PRIMARY KEY,
    TenCa NVARCHAR(20) NOT NULL UNIQUE,
    GioBatDau TIME(0) NOT NULL,
    GioKetThuc TIME(0) NOT NULL
);

INSERT INTO CaLamViec (
    MaCa, TenCa, GioBatDau, GioKetThuc
)
VALUES
('CA01', N'Ca sáng', '08:00', '12:00'),
('CA02', N'Ca chiều', '12:00', '17:00'),
('CA03', N'Ca tối', '17:00', '21:30');

-- 4. PhanCongCa
CREATE TABLE PhanCongCa (
    MaPhanCong VARCHAR(20) NOT NULL PRIMARY KEY,
    MaNhanVien VARCHAR(5) NOT NULL FOREIGN KEY REFERENCES NhanVien(MaNhanVien),
    Ngay DATE NOT NULL,
    GhiChu NVARCHAR(255) NULL,
    MaCa VARCHAR(4) NOT NULL FOREIGN KEY REFERENCES CaLamViec(MaCa),
    TienQuyDauCa DECIMAL(18,2) NOT NULL DEFAULT 0,
    TienMatThucDem DECIMAL(18,2) NULL,
    TrangThai NVARCHAR(20) NOT NULL DEFAULT N'Chưa mở',
    GioMoCa DATETIME2(0) NULL,
    GioDongCa DATETIME2(0) NULL,
    UNIQUE(MaNhanVien,Ngay,MaCa),
    UNIQUE(MaPhanCong,MaNhanVien)
);

INSERT INTO PhanCongCa (
    MaPhanCong, MaNhanVien, Ngay, GhiChu, MaCa, TienQuyDauCa, TienMatThucDem, TrangThai, GioMoCa, GioDongCa
)
VALUES
('PC202610180001', 'NV001', '2026-10-18', N'Ca minh họa đã đối soát', 'CA01', 20000000, 168387800.00, N'Đã đóng', '2026-10-18T07:55:00', '2026-10-18T12:05:00'),
('PC202610190001', 'NV001', '2026-10-19', N'Ca minh họa có hoàn tiền', 'CA01', 20000000, 5159900, N'Đã đóng', '2026-10-19T07:55:00', '2026-10-19T12:05:00'),
('PC202610190002', 'NV002', '2026-10-19', N'Ca minh họa chênh 10.000: tiền lẻ chưa bàn giao', 'CA01', 1000000, 2308000.00, N'Đã đóng', '2026-10-19T07:58:00', '2026-10-19T12:05:00'),
('PC202610200001', 'NV001', '2026-10-20', N'Ca demo đang mở để thử POS và hoàn tiền', 'CA02', 20000000, NULL, N'Đang mở', '2026-10-20T12:10:00', NULL),
('PC202610200002', 'NV002', '2026-10-20', N'Đăng nhập NV002 để thử mở ca', 'CA02', 0, NULL, N'Chưa mở', NULL, NULL),
('PC202610200003', 'NV003', '2026-10-20', N'Đăng nhập NV003 để thử mở ca', 'CA02', 0, NULL, N'Chưa mở', NULL, NULL);

-- 5. KhachHang
CREATE TABLE KhachHang (
    MaKhachHang VARCHAR(5) NOT NULL PRIMARY KEY,
    HoTen NVARCHAR(100) NOT NULL,
    SoDienThoai VARCHAR(10) NOT NULL UNIQUE,
    DiaChi NVARCHAR(255) NULL,
    DiemTichLuy INT NOT NULL DEFAULT 0,
    Email VARCHAR(255) NULL
);

INSERT INTO KhachHang (
    MaKhachHang, HoTen, SoDienThoai, DiaChi, DiemTichLuy, Email
)
VALUES
('KH001', N'Nguyễn Thanh Hà', '0900000101', N'Quận 1, TP. Hồ Chí Minh', 1450, 'ha@example.com'),
('KH002', N'Trần Quốc Huy', '0900000102', N'Gò Vấp, TP. Hồ Chí Minh', 12, 'huy@example.com'),
('KH003', N'Lê Ngọc Mai', '0900000103', N'Bình Thạnh, TP. Hồ Chí Minh', 0, 'mai@example.com'),
('KH004', N'Phạm Hoàng Nam', '0900000104', N'Thủ Đức, TP. Hồ Chí Minh', 0, NULL),
('KH005', N'Võ Thị Lan', '0900000105', N'Tân Bình, TP. Hồ Chí Minh', 0, NULL),
('KH006', N'Đặng Minh Khang', '0900000106', N'Bình Tân, TP. Hồ Chí Minh', 0, NULL),
('KH007', N'Bùi Thu Trang', '0900000107', N'Quận 7, TP. Hồ Chí Minh', 0, NULL),
('KH008', N'Hồ Gia Bảo', '0900000108', N'Quận 3, TP. Hồ Chí Minh', 0, NULL),
('KH009', N'Đỗ Phương Anh', '0900000109', N'Nhà Bè, TP. Hồ Chí Minh', 0, NULL),
('KH010', N'Dương Tuấn Kiệt', '0900000110', N'Quận 8, TP. Hồ Chí Minh', 0, NULL);

-- 6. SanPham
CREATE TABLE SanPham (
    MaSanPham VARCHAR(6) NOT NULL PRIMARY KEY,
    TenSanPham NVARCHAR(150) NOT NULL,
    LoaiHang NVARCHAR(50) NOT NULL,
    ThuongHieu NVARCHAR(50) NOT NULL,
    DonGiaBan DECIMAL(18,2) NOT NULL,
    GiaNhap DECIMAL(18,2) NOT NULL,
    ThongSoKiThuat NVARCHAR(MAX) NULL,
    XuatXu NVARCHAR(50) NOT NULL,
    TrangThai NVARCHAR(20) NOT NULL DEFAULT N'Đang bán',
    ThoiHanBaoHanh INT NOT NULL,
    SoLuongTon INT NOT NULL DEFAULT 0
);

INSERT INTO SanPham (
    MaSanPham, TenSanPham, LoaiHang, ThuongHieu, DonGiaBan, GiaNhap, ThongSoKiThuat, XuatXu, TrangThai, ThoiHanBaoHanh, SoLuongTon
)
VALUES
('SP0001', 'Smart TV Sony 4K 55 inch', 'Tivi', 'Sony', 14990000, 12500000, '55 inch; 4K; Google TV; Wi-Fi; HDMI', 'Malaysia', N'Đang bán', 24, 23),
('SP0002', N'Tủ lạnh LG Inverter 315 lít', N'Tủ lạnh', 'LG', 8990000, 7200000, N'315 lít; Inverter; ngăn đá trên', N'Việt Nam', N'Đang bán', 24, 27),
('SP0003', N'Máy giặt Samsung 10 kg', N'Máy giặt', 'Samsung', 11990000, 9500000, N'10 kg; cửa trước; Digital Inverter', N'Việt Nam', N'Đang bán', 24, 29),
('SP0004', N'Điều hòa Panasonic 1 HP', N'Điều hòa', 'Panasonic', 9990000, 8000000, N'1 HP; Inverter; lọc bụi; tiết kiệm điện', N'Thái Lan', N'Đang bán', 12, 30),
('SP0005', N'Nồi cơm điện Sharp 1.8 lít', N'Gia dụng', 'Sharp', 1190000, 850000, N'1.8 lít; chống dính; giữ ấm', N'Thái Lan', N'Đang bán', 12, 30),
('SP0006', N'Máy hút bụi Electrolux 1800 W', N'Gia dụng', 'Electrolux', 2490000, 1800000, N'1800 W; hộp bụi 1.5 lít; lọc HEPA', N'Trung Quốc', N'Đang bán', 24, 30),
('SP0007', N'Lò vi sóng Toshiba 23 lít', N'Gia dụng', 'Toshiba', 2290000, 1700000, N'23 lít; 800 W; rã đông nhanh', N'Thái Lan', N'Đang bán', 12, 30),
('SP0008', N'Quạt đứng Senko 65 W', N'Gia dụng', 'Senko', 590000, 400000, N'65 W; 3 tốc độ; điều chỉnh độ cao', N'Việt Nam', N'Đang bán', 12, 28),
('SP0009', N'Máy lọc không khí Xiaomi 4 Lite', N'Gia dụng', 'Xiaomi', 2990000, 2200000, N'Lọc HEPA; diện tích 43 m²; cảm biến bụi', N'Trung Quốc', N'Đang bán', 12, 30),
('SP0010', N'Bếp điện từ Philips 2100 W', N'Gia dụng', 'Philips', 1790000, 1250000, N'2100 W; mặt kính; điều khiển cảm ứng', N'Trung Quốc', N'Đang bán', 24, 30),
('SP0011', 'Smart TV Samsung 4K 43 inch', 'Tivi', 'Samsung', 8490000, 6700000, '43 inch; 4K; Tizen OS; Bluetooth', N'Việt Nam', N'Đang bán', 24, 30),
('SP0012', N'Tủ lạnh Panasonic 234 lít', N'Tủ lạnh', 'Panasonic', 6990000, 5400000, N'234 lít; Inverter; kháng khuẩn', N'Việt Nam', N'Đang bán', 24, 30),
('SP0013', N'Máy giặt LG 9 kg', N'Máy giặt', 'LG', 8990000, 7100000, N'9 kg; cửa trước; giặt hơi nước', N'Việt Nam', N'Đang bán', 24, 30),
('SP0014', N'Ấm siêu tốc Sunhouse 1.8 lít', N'Gia dụng', 'Sunhouse', 390000, 250000, N'1.8 lít; inox; tự ngắt khi sôi', N'Việt Nam', N'Đang bán', 12, 3),
('SP0015', N'Máy sấy tóc Panasonic 1600 W', N'Gia dụng', 'Panasonic', 490000, 320000, N'1600 W; gấp gọn; 3 chế độ sấy', N'Thái Lan', N'Đang bán', 12, 3),
('SP0016', N'Bàn ủi hơi nước Philips 2000 W', N'Gia dụng', 'Philips', 990000, 700000, N'2000 W; chống dính; phun hơi liên tục', N'Trung Quốc', N'Đang bán', 24, 30);

-- 7. ChuongTrinhKhuyenMai
CREATE TABLE ChuongTrinhKhuyenMai (
    MaKhuyenMai VARCHAR(9) NOT NULL PRIMARY KEY,
    TenChuongTrinh NVARCHAR(100) NOT NULL,
    GiaTriGiam DECIMAL(5,2) NOT NULL,
    NgayBatDau DATE NOT NULL,
    NgayKetThuc DATE NOT NULL,
    TrangThai NVARCHAR(20) NOT NULL DEFAULT N'Đang áp dụng'
);

INSERT INTO ChuongTrinhKhuyenMai (
    MaKhuyenMai, TenChuongTrinh, GiaTriGiam, NgayBatDau, NgayKetThuc, TrangThai
)
VALUES
('KM2026001', N'Ưu đãi điện máy 10%', 10, '2026-09-20', '2026-11-19', N'Đang áp dụng'),
('KM2026002', N'Tiết kiệm gia dụng 5%', 5, '2026-09-20', '2026-11-04', N'Đang áp dụng'),
('KM2026003', N'Chương trình đã kết thúc', 15, '2026-08-21', '2026-09-10', N'Hết hạn'),
('KM2026004', N'Ưu đãi tạm ngưng', 8, '2026-10-10', '2026-10-30', N'Tạm ngưng');

-- 8. HoaDon
CREATE TABLE HoaDon (
    MaHoaDon VARCHAR(17) NOT NULL PRIMARY KEY,
    MaKhachHang VARCHAR(5) NULL FOREIGN KEY REFERENCES KhachHang(MaKhachHang),
    MaNhanVien VARCHAR(5) NOT NULL FOREIGN KEY REFERENCES NhanVien(MaNhanVien),
    NgayLap DATETIME2(0) NOT NULL DEFAULT SYSDATETIME(),
    HinhThucThanhToan NVARCHAR(30) NULL,
    TrangThaiThanhToan NVARCHAR(20) NOT NULL DEFAULT N'Chưa thanh toán',
    ThueSuatVAT DECIMAL(3,2) NOT NULL DEFAULT 0.10,
    MaPhanCong VARCHAR(20) NOT NULL,
    DiemSuDung INT NOT NULL DEFAULT 0,
    CONSTRAINT FK_HoaDon_PhanCong FOREIGN KEY(MaPhanCong,MaNhanVien) REFERENCES PhanCongCa(MaPhanCong,MaNhanVien)
);

INSERT INTO HoaDon (
    MaHoaDon, MaKhachHang, MaNhanVien, NgayLap, HinhThucThanhToan, TrangThaiThanhToan, ThueSuatVAT, MaPhanCong, DiemSuDung
)
VALUES
('HD20261018CA01001', 'KH001', 'NV001', '2026-10-18T10:00:00', N'Tiền mặt', N'Đã thanh toán', 0.10, 'PC202610180001', 0),
('HD20261019CA01001', 'KH001', 'NV001', '2026-10-19T10:00:00', N'Chuyển khoản QR', N'Đã thanh toán', 0.10, 'PC202610190001', 10),
('HD20261019CA01002', 'KH002', 'NV002', '2026-10-19T10:00:00', N'Tiền mặt', N'Đã thanh toán', 0.10, 'PC202610190002', 0),
('HD20261020CA02001', 'KH003', 'NV001', '2026-10-20T12:10:00', NULL, N'Chưa thanh toán', 0.10, 'PC202610200001', 0),
('HD20261019CA01003', NULL, 'NV001', '2026-10-19T11:00:00', NULL, N'Đã hủy', 0.10, 'PC202610190001', 0);

-- 9. ChiTietHoaDon
CREATE TABLE ChiTietHoaDon (
    MaHoaDon VARCHAR(17) NOT NULL FOREIGN KEY REFERENCES HoaDon(MaHoaDon),
    MaSanPham VARCHAR(6) NOT NULL FOREIGN KEY REFERENCES SanPham(MaSanPham),
    SoLuong INT NOT NULL,
    MaKhuyenMai VARCHAR(9) NULL FOREIGN KEY REFERENCES ChuongTrinhKhuyenMai(MaKhuyenMai),
    DonGia DECIMAL(18,2) NOT NULL,
    GiamGia DECIMAL(18,2) NOT NULL DEFAULT 0,
    ThanhTienChiTiet AS CONVERT(DECIMAL(18,2), SoLuong*DonGia-GiamGia),
    PRIMARY KEY(MaHoaDon,MaSanPham)
);

INSERT INTO ChiTietHoaDon (
    MaHoaDon, MaSanPham, SoLuong, MaKhuyenMai, DonGia, GiamGia
)
VALUES
('HD20261018CA01001', 'SP0001', 8, 'KM2026001', 14990000, 11992000),
('HD20261018CA01001', 'SP0002', 3, NULL, 8990000, 0),
('HD20261019CA01001', 'SP0003', 1, 'KM2026002', 11990000, 599500),
('HD20261019CA01002', 'SP0008', 2, NULL, 590000, 0),
('HD20261020CA02001', 'SP0005', 1, NULL, 1190000, 0),
('HD20261019CA01003', 'SP0007', 1, NULL, 2290000, 0);

-- 10. PhieuNhapHang
CREATE TABLE PhieuNhapHang (
    MaPhieuNhap VARCHAR(20) NOT NULL PRIMARY KEY,
    NgayLap DATETIME2(0) NOT NULL DEFAULT SYSDATETIME(),
    SoChungTu NVARCHAR(50) NULL,
    NhaCungCap NVARCHAR(150) NOT NULL,
    NguoiGiao NVARCHAR(100) NULL,
    MaNhanVienLap VARCHAR(5) NOT NULL FOREIGN KEY REFERENCES NhanVien(MaNhanVien),
    MaQuanLyDuyet VARCHAR(5) NULL FOREIGN KEY REFERENCES NhanVien(MaNhanVien),
    TrangThai NVARCHAR(20) NOT NULL DEFAULT N'Chờ duyệt',
    NgayDuyet DATETIME2(0) NULL,
    LyDoTuChoi NVARCHAR(255) NULL
);

INSERT INTO PhieuNhapHang (
    MaPhieuNhap, NgayLap, SoChungTu, NhaCungCap, NguoiGiao, MaNhanVienLap, MaQuanLyDuyet, TrangThai, NgayDuyet, LyDoTuChoi
)
VALUES
('PN001', '2026-10-17T09:00:00', 'CT-001', N'Nhà phân phối Minh Phát', N'Nguyễn Tài', 'NV002', 'QL001', N'Đã nhập', '2026-10-17T10:00:00', NULL),
('PN002', '2026-10-20T12:10:00', 'CT-002', N'Nhà phân phối An Khang', N'Trần Long', 'NV001', NULL, N'Chờ duyệt', NULL, NULL),
('PN003', '2026-10-19T09:00:00', 'CT-003', N'Nhà phân phối Minh Phát', N'Nguyễn Tài', 'NV002', 'QL001', N'Từ chối', '2026-10-19T10:00:00', N'Số lượng chứng từ không khớp hàng giao');

-- 11. ChiTietPhieuNhapHang
CREATE TABLE ChiTietPhieuNhapHang (
    MaPhieuNhap VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES PhieuNhapHang(MaPhieuNhap),
    MaSanPham VARCHAR(6) NOT NULL FOREIGN KEY REFERENCES SanPham(MaSanPham),
    SoLuong INT NOT NULL,
    DonGiaNhap DECIMAL(18,2) NOT NULL,
    ThanhTien AS CONVERT(DECIMAL(18,2),SoLuong*DonGiaNhap),
    PRIMARY KEY(MaPhieuNhap,MaSanPham)
);

INSERT INTO ChiTietPhieuNhapHang (
    MaPhieuNhap, MaSanPham, SoLuong, DonGiaNhap
)
VALUES
('PN001', 'SP0001', 30, 12500000),
('PN001', 'SP0002', 30, 7200000),
('PN001', 'SP0003', 30, 9500000),
('PN001', 'SP0004', 30, 8000000),
('PN001', 'SP0005', 30, 850000),
('PN001', 'SP0006', 30, 1800000),
('PN001', 'SP0007', 30, 1700000),
('PN001', 'SP0008', 30, 400000),
('PN001', 'SP0009', 30, 2200000),
('PN001', 'SP0010', 30, 1250000),
('PN001', 'SP0011', 30, 6700000),
('PN001', 'SP0012', 30, 5400000),
('PN001', 'SP0013', 30, 7100000),
('PN001', 'SP0014', 3, 250000),
('PN001', 'SP0015', 3, 320000),
('PN001', 'SP0016', 30, 700000),
('PN002', 'SP0001', 2, 12500000),
('PN002', 'SP0014', 5, 250000),
('PN003', 'SP0002', 2, 7200000);

-- 12. PhieuDoiTra
CREATE TABLE PhieuDoiTra (
    MaPhieuDoiTra VARCHAR(20) NOT NULL PRIMARY KEY,
    MaHoaDonGoc VARCHAR(17) NOT NULL FOREIGN KEY REFERENCES HoaDon(MaHoaDon),
    MaNhanVienLap VARCHAR(5) NOT NULL FOREIGN KEY REFERENCES NhanVien(MaNhanVien),
    MaQuanLyDuyet VARCHAR(5) NULL FOREIGN KEY REFERENCES NhanVien(MaNhanVien),
    MaPhanCongHoanTien VARCHAR(20) NULL FOREIGN KEY REFERENCES PhanCongCa(MaPhanCong),
    NgayLap DATETIME2(0) NOT NULL DEFAULT SYSDATETIME(),
    LyDo NVARCHAR(255) NOT NULL,
    TrangThai NVARCHAR(20) NOT NULL DEFAULT N'Chờ duyệt',
    NgayDuyet DATETIME2(0) NULL,
    LyDoTuChoi NVARCHAR(255) NULL,
    DiemHoanLai INT NOT NULL DEFAULT 0,
    DiemThuHoi INT NOT NULL DEFAULT 0,
    UNIQUE(MaPhieuDoiTra,MaHoaDonGoc)
);

INSERT INTO PhieuDoiTra (
    MaPhieuDoiTra, MaHoaDonGoc, MaNhanVienLap, MaQuanLyDuyet, MaPhanCongHoanTien, NgayLap, LyDo, TrangThai, NgayDuyet, LyDoTuChoi, DiemHoanLai, DiemThuHoi
)
VALUES
('DT001', 'HD20261018CA01001', 'NV001', 'QL001', 'PC202610190001', '2026-10-19T08:00:00', N'Khách trả một sản phẩm', N'Hoàn tất', '2026-10-19T09:00:00', NULL, 0, 148),
('DT002', 'HD20261018CA01001', 'NV001', NULL, NULL, '2026-10-20T12:10:00', N'Khách yêu cầu trả hàng', N'Chờ duyệt', NULL, NULL, 0, 0),
('DT003', 'HD20261018CA01001', 'NV001', 'QL001', NULL, '2026-10-20T12:10:00', N'Khách thay đổi nhu cầu', N'Đã duyệt', '2026-10-20T12:10:00', NULL, 0, 0),
('DT004', 'HD20261018CA01001', 'NV001', 'QL001', NULL, '2026-10-20T12:10:00', N'Yêu cầu không phù hợp', N'Từ chối', '2026-10-20T12:10:00', N'Hàng không đủ điều kiện tiếp nhận', 0, 0);

-- 13. ChiTietDoiTra
CREATE TABLE ChiTietDoiTra (
    MaPhieuDoiTra VARCHAR(20) NOT NULL,
    MaHoaDon VARCHAR(17) NOT NULL,
    MaSanPham VARCHAR(6) NOT NULL,
    SoLuongTra INT NOT NULL,
    TinhTrangHang NVARCHAR(255) NOT NULL,
    TienHoan DECIMAL(18,2) NOT NULL DEFAULT 0,
    PRIMARY KEY(MaPhieuDoiTra,MaHoaDon,MaSanPham),
    CONSTRAINT FK_ChiTietDoiTra_DongGoc FOREIGN KEY(MaHoaDon,MaSanPham) REFERENCES ChiTietHoaDon(MaHoaDon,MaSanPham),
    CONSTRAINT FK_ChiTietDoiTra_PhieuGoc FOREIGN KEY(MaPhieuDoiTra,MaHoaDon) REFERENCES PhieuDoiTra(MaPhieuDoiTra,MaHoaDonGoc)
);

INSERT INTO ChiTietDoiTra (
    MaPhieuDoiTra, MaHoaDon, MaSanPham, SoLuongTra, TinhTrangHang, TienHoan
)
VALUES
('DT001', 'HD20261018CA01001', 'SP0001', 1, N'Đã kiểm tra và tiếp nhận', 14840100),
('DT002', 'HD20261018CA01001', 'SP0001', 1, N'Đủ phụ kiện, chờ kiểm tra duyệt', 14840100),
('DT003', 'HD20261018CA01001', 'SP0001', 1, N'Đã được quản lý kiểm tra', 14840100),
('DT004', 'HD20261018CA01001', 'SP0001', 1, N'Thiếu phụ kiện', 14840100);

GO
