USE [master];
GO
IF DB_ID(N'GreenSmart_DB') IS NULL
    EXEC(N'CREATE DATABASE [GreenSmart_DB]');
GO
USE [GreenSmart_DB];
GO

IF OBJECT_ID(N'dbo.ChiTietDoiTra', N'U') IS NOT NULL DROP TABLE dbo.ChiTietDoiTra;
IF OBJECT_ID(N'dbo.PhieuDoiTra', N'U') IS NOT NULL DROP TABLE dbo.PhieuDoiTra;
IF OBJECT_ID(N'dbo.ChiTietPhieuNhapHang', N'U') IS NOT NULL DROP TABLE dbo.ChiTietPhieuNhapHang;
IF OBJECT_ID(N'dbo.PhieuNhapHang', N'U') IS NOT NULL DROP TABLE dbo.PhieuNhapHang;
IF OBJECT_ID(N'dbo.ChiTietHoaDon', N'U') IS NOT NULL DROP TABLE dbo.ChiTietHoaDon;
IF OBJECT_ID(N'dbo.HoaDon', N'U') IS NOT NULL DROP TABLE dbo.HoaDon;
IF OBJECT_ID(N'dbo.ChuongTrinhKhuyenMai', N'U') IS NOT NULL DROP TABLE dbo.ChuongTrinhKhuyenMai;
IF OBJECT_ID(N'dbo.SanPham', N'U') IS NOT NULL DROP TABLE dbo.SanPham;
IF OBJECT_ID(N'dbo.KhachHang', N'U') IS NOT NULL DROP TABLE dbo.KhachHang;
IF OBJECT_ID(N'dbo.PhanCongCa', N'U') IS NOT NULL DROP TABLE dbo.PhanCongCa;
IF OBJECT_ID(N'dbo.CaLamViec', N'U') IS NOT NULL DROP TABLE dbo.CaLamViec;
IF OBJECT_ID(N'dbo.TaiKhoan', N'U') IS NOT NULL DROP TABLE dbo.TaiKhoan;
IF OBJECT_ID(N'dbo.NhanVien', N'U') IS NOT NULL DROP TABLE dbo.NhanVien;
GO

-- 1. Nhân viên
CREATE TABLE NhanVien (
    MaNhanVien VARCHAR(5) NOT NULL PRIMARY KEY CHECK (MaNhanVien LIKE 'NV[0-9][0-9][0-9]' OR MaNhanVien LIKE 'QL[0-9][0-9][0-9]'),
    HoTen NVARCHAR(100) NOT NULL,
    VaiTro NVARCHAR(20) NOT NULL CHECK (VaiTro IN (N'Bán hàng', N'Quản lý')),
    SDT VARCHAR(10) NOT NULL CHECK (LEN(SDT) = 10 AND SDT LIKE '0[35789]%' AND SDT NOT LIKE '%[^0-9]%'),
    Email VARCHAR(255) NULL CHECK (Email IS NULL OR Email LIKE '%@%._%'),
    DiaChi NVARCHAR(255) NULL,
    SoCCCD VARCHAR(12) NOT NULL UNIQUE CHECK (LEN(SoCCCD) = 12 AND SoCCCD NOT LIKE '%[^0-9]%'),
    HinhTheNV VARBINARY(MAX) NULL,
    GioiTinh NVARCHAR(4) NOT NULL CHECK (GioiTinh IN (N'Nam', N'Nữ', N'Khác')),
    NgaySinh DATE NOT NULL CHECK (NgaySinh <= DATEADD(YEAR, -18, GETDATE()) AND NgaySinh >= DATEADD(YEAR, -65, GETDATE())),
    NgayBatDauLV DATE NOT NULL CHECK (NgayBatDauLV <= GETDATE())
);
INSERT INTO NhanVien VALUES
('QL001', N'Lê Văn Quang', N'Quản lý', '0900000001', 'quang@example.com', N'TP. Hồ Chí Minh', '079000000001', NULL, N'Nam', '1990-05-12', '2020-01-02'),
('NV001', N'Nguyễn Văn An', N'Bán hàng', '0900000002', 'an@example.com', N'TP. Hồ Chí Minh', '079000000002', NULL, N'Nam', '2000-08-20', '2024-01-02'),
('NV002', N'Trần Thị Bình', N'Bán hàng', '0900000003', 'binh@example.com', N'TP. Hồ Chí Minh', '079000000003', NULL, N'Nữ', '2001-03-15', '2024-01-02');

-- 2. Tài khoản
CREATE TABLE TaiKhoan (
    TenDangNhap VARCHAR(5) NOT NULL PRIMARY KEY,
    Quyen VARCHAR(10) NOT NULL CHECK (Quyen IN ('ADMIN', 'BAN_HANG')),
    MatKhau NVARCHAR(128) NOT NULL,
    MaNhanVien VARCHAR(5) NOT NULL UNIQUE FOREIGN KEY REFERENCES NhanVien(MaNhanVien) ON DELETE CASCADE
);
INSERT INTO TaiKhoan VALUES
('QL001', 'ADMIN', 'Green@123', 'QL001'),
('NV001', 'BAN_HANG', 'Green@123', 'NV001'),
('NV002', 'BAN_HANG', 'Green@123', 'NV002');

-- 3. Ca làm việc
CREATE TABLE CaLamViec (
    MaCa VARCHAR(4) NOT NULL PRIMARY KEY CHECK (MaCa IN ('CA01', 'CA02', 'CA03')),
    TenCa NVARCHAR(20) NOT NULL UNIQUE CHECK (TenCa IN (N'Ca sáng', N'Ca chiều', N'Ca tối')),
    GioBatDau TIME NOT NULL,
    GioKetThuc TIME NOT NULL,
    CONSTRAINT CK_CaLamViec_Gio CHECK (GioKetThuc > GioBatDau)
);
INSERT INTO CaLamViec VALUES
('CA01', N'Ca sáng', '08:00', '12:00'),
('CA02', N'Ca chiều', '12:00', '17:00'),
('CA03', N'Ca tối', '17:00', '21:30');

-- 4. Phân công ca
CREATE TABLE PhanCongCa (
    MaPhanCong VARCHAR(20) NOT NULL PRIMARY KEY,
    MaCa VARCHAR(4) NOT NULL FOREIGN KEY REFERENCES CaLamViec(MaCa),
    MaNhanVien VARCHAR(5) NOT NULL FOREIGN KEY REFERENCES NhanVien(MaNhanVien),
    Ngay DATE NOT NULL,
    GhiChu NVARCHAR(255) NULL,
    TienQuyDauCa DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK (TienQuyDauCa >= 0),
    TienMatThucDem DECIMAL(18,2) NULL CHECK (TienMatThucDem >= 0),
    TrangThai NVARCHAR(20) NOT NULL DEFAULT N'Chưa mở' CHECK (TrangThai IN (N'Chưa mở', N'Đang mở', N'Đã đóng')),
    GioMoCa DATETIME NULL,
    GioDongCa DATETIME NULL,
    TienMatLyThuyet DECIMAL(18,2) NULL CHECK (TienMatLyThuyet >= 0),
    ChenhLech DECIMAL(18,2) NULL,
    UNIQUE(MaNhanVien, Ngay, MaCa)
);
INSERT INTO PhanCongCa VALUES
('PC001', 'CA01', 'NV001', '2026-10-18', N'Ca minh họa', 20000000, 168387800, N'Đã đóng', '2026-10-18 07:55:00', '2026-10-18 12:05:00', 168387800, 0),
('PC002', 'CA01', 'NV001', '2026-10-19', N'Ca minh họa hoàn tiền', 20000000, 5159900, N'Đã đóng', '2026-10-19 07:55:00', '2026-10-19 12:05:00', 5159900, 0),
('PC003', 'CA02', 'NV001', '2026-10-20', N'Ca demo đang mở', 20000000, NULL, N'Đang mở', '2026-10-20 12:10:00', NULL, NULL, NULL);

-- 5. Khách hàng
CREATE TABLE KhachHang (
    MaKhachHang VARCHAR(5) NOT NULL PRIMARY KEY CHECK (MaKhachHang LIKE 'KH[0-9][0-9][0-9]'),
    HoTen NVARCHAR(100) NOT NULL,
    SoDienThoai VARCHAR(10) NOT NULL UNIQUE CHECK (LEN(SoDienThoai) = 10 AND SoDienThoai LIKE '0[35789]%' AND SoDienThoai NOT LIKE '%[^0-9]%'),
    DiaChi NVARCHAR(255) NULL,
    DiemTichLuy INT NOT NULL DEFAULT 0 CHECK (DiemTichLuy >= 0),
    LoaiThanhVien NVARCHAR(20) NOT NULL DEFAULT N'Chuẩn' CHECK (LoaiThanhVien IN (N'Chuẩn', N'Thân thiết', N'VIP')),
    Email VARCHAR(255) NULL CHECK (Email IS NULL OR Email LIKE '%@%._%')
);
INSERT INTO KhachHang VALUES
('KH001', N'Nguyễn Thanh Hà', '0900000101', N'Quận 1', 1450, N'VIP', 'ha@example.com'),
('KH002', N'Trần Quốc Huy', '0900000102', N'Gò Vấp', 12, N'Chuẩn', 'huy@example.com');

-- 6. Khuyến mãi
CREATE TABLE ChuongTrinhKhuyenMai (
    MaKhuyenMai VARCHAR(9) NOT NULL PRIMARY KEY CHECK (MaKhuyenMai LIKE 'KM[0-9][0-9][0-9][0-9][0-9][0-9][0-9]'),
    TenChuongTrinh NVARCHAR(100) NOT NULL,
    GiaTriGiam DECIMAL(5,2) NOT NULL CHECK (GiaTriGiam > 0 AND GiaTriGiam <= 50),
    NgayBatDau DATE NOT NULL,
    NgayKetThuc DATE NOT NULL,
    TrangThai NVARCHAR(20) NOT NULL DEFAULT N'Đang áp dụng' CHECK (TrangThai IN (N'Đang áp dụng', N'Tạm ngưng', N'Hết hạn')),
    CONSTRAINT CK_CTKM_Ngay CHECK (NgayKetThuc >= NgayBatDau)
);
INSERT INTO ChuongTrinhKhuyenMai VALUES
('KM2026001', N'Ưu đãi điện máy 10%', 10.00, '2026-09-20', '2026-11-19', N'Đang áp dụng');

-- 7. Sản phẩm
CREATE TABLE SanPham (
    MaSanPham VARCHAR(6) NOT NULL PRIMARY KEY CHECK (MaSanPham LIKE 'SP[0-9][0-9][0-9][0-9]'),
    TenSanPham NVARCHAR(150) NOT NULL,
    LoaiHang NVARCHAR(50) NOT NULL,
    ThuongHieu NVARCHAR(50) NOT NULL,
    DonGiaBan DECIMAL(18,2) NOT NULL CHECK (DonGiaBan > 0),
    GiaNhap DECIMAL(18,2) NOT NULL CHECK (GiaNhap > 0),
    ThongSoKyThuat NVARCHAR(MAX) NULL,
    XuatXu NVARCHAR(50) NOT NULL,
    TrangThai NVARCHAR(20) NOT NULL DEFAULT N'Đang bán' CHECK (TrangThai IN (N'Đang bán', N'Ngưng bán')),
    BaoHanhThang INT NOT NULL CHECK (BaoHanhThang >= 0),
    SoLuongTon INT NOT NULL DEFAULT 0 CHECK (SoLuongTon >= 0),
    CONSTRAINT CK_SanPham_Gia CHECK (DonGiaBan >= GiaNhap)
);
INSERT INTO SanPham VALUES
('SP0001', N'Smart TV Sony 4K 55 inch', N'Tivi', 'Sony', 14990000, 12500000, N'55 inch', N'Malaysia', N'Đang bán', 24, 23),
('SP0002', N'Tủ lạnh LG Inverter', N'Tủ lạnh', 'LG', 8990000, 7200000, N'315 lít', N'Việt Nam', N'Đang bán', 24, 27);

-- 8. Hóa đơn
CREATE TABLE HoaDon (
    MaHoaDon VARCHAR(17) NOT NULL PRIMARY KEY,
    MaKhachHang VARCHAR(5) NULL FOREIGN KEY REFERENCES KhachHang(MaKhachHang),
    MaNhanVien VARCHAR(5) NOT NULL FOREIGN KEY REFERENCES NhanVien(MaNhanVien),
    MaPhanCong VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES PhanCongCa(MaPhanCong),
    NgayLap DATE NOT NULL DEFAULT CAST(GETDATE() AS DATE) CHECK (NgayLap <= CAST(GETDATE() AS DATE)),
    HinhThucThanhToan NVARCHAR(30) CHECK (HinhThucThanhToan IN (N'Tiền mặt', N'Chuyển khoản QR')),
    TrangThaiThanhToan NVARCHAR(20) NOT NULL DEFAULT N'Chưa thanh toán' CHECK (TrangThaiThanhToan IN (N'Chưa thanh toán', N'Đã thanh toán', N'Đã hủy')),
    TongTienHang DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK (TongTienHang >= 0),
    TongGiamGia DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK (TongGiamGia >= 0),
    ThanhTien DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK (ThanhTien >= 0),
    TienVAT DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK (TienVAT >= 0),
    ThueSuatVAT DECIMAL(3,2) NOT NULL DEFAULT 0.10 CHECK (ThueSuatVAT >= 0 AND ThueSuatVAT < 1),
    DiemCong INT NOT NULL DEFAULT 0 CHECK (DiemCong >= 0),
    TienGiamTuDiem DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK (TienGiamTuDiem >= 0)
);
INSERT INTO HoaDon VALUES
('HD20261018CA01001', 'KH001', 'NV001', 'PC001', '2026-10-18', N'Tiền mặt', N'Đã thanh toán', 14990000, 1499000, 14840100, 1349100, 0.10, 148, 0);

-- 9. Chi tiết hóa đơn (ĐÃ SỬA LỖI TẠI ĐÂY)
CREATE TABLE ChiTietHoaDon (
    MaHoaDon VARCHAR(17) NOT NULL FOREIGN KEY REFERENCES HoaDon(MaHoaDon) ON DELETE CASCADE,
    MaSanPham VARCHAR(6) NOT NULL FOREIGN KEY REFERENCES SanPham(MaSanPham),
    SoLuong INT NOT NULL CHECK (SoLuong > 0),
    MaKhuyenMai VARCHAR(9) NULL FOREIGN KEY REFERENCES ChuongTrinhKhuyenMai(MaKhuyenMai),
    DonGia DECIMAL(18,2) NOT NULL CHECK (DonGia > 0),
    GiamGia DECIMAL(18,2) NOT NULL DEFAULT 0,
    PRIMARY KEY(MaHoaDon, MaSanPham),
    CONSTRAINT CK_CTHD_GiamGia CHECK (GiamGia >= 0 AND GiamGia <= SoLuong * DonGia) -- Tách ra thành Constraint
);
INSERT INTO ChiTietHoaDon VALUES
('HD20261018CA01001', 'SP0001', 1, 'KM2026001', 14990000, 1499000);

-- 10. Phiếu nhập hàng
CREATE TABLE PhieuNhapHang (
    MaPhieuNhap VARCHAR(20) NOT NULL PRIMARY KEY,
    MaNhanVienLap VARCHAR(5) NOT NULL FOREIGN KEY REFERENCES NhanVien(MaNhanVien),
    MaQuanLyDuyet VARCHAR(5) NULL FOREIGN KEY REFERENCES NhanVien(MaNhanVien),
    NgayLap DATETIME NOT NULL DEFAULT GETDATE(),
    SoChungTu NVARCHAR(50) NOT NULL,
    NhaCungCap NVARCHAR(150) NOT NULL,
    NguoiGiao NVARCHAR(100) NOT NULL,
    TrangThai NVARCHAR(20) NOT NULL DEFAULT N'Chờ duyệt' CHECK (TrangThai IN (N'Chờ duyệt', N'Đã duyệt', N'Từ chối')),
    NgayDuyet DATETIME NULL,
    LyDoTuChoi NVARCHAR(255) NULL,
    TongTien DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK (TongTien >= 0)
);
INSERT INTO PhieuNhapHang VALUES
('PN001', 'NV002', 'QL001', '2026-10-17 09:00:00', 'CT-001', N'Minh Phát', N'Nguyễn Tài', N'Đã duyệt', '2026-10-17 10:00:00', NULL, 375000000);

-- 11. Chi tiết phiếu nhập hàng
CREATE TABLE ChiTietPhieuNhapHang (
    MaPhieuNhap VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES PhieuNhapHang(MaPhieuNhap) ON DELETE CASCADE,
    MaSanPham VARCHAR(6) NOT NULL FOREIGN KEY REFERENCES SanPham(MaSanPham),
    SoLuong INT NOT NULL CHECK (SoLuong > 0),
    DonGiaNhap DECIMAL(18,2) NOT NULL CHECK (DonGiaNhap > 0),
    PRIMARY KEY(MaPhieuNhap, MaSanPham)
);
INSERT INTO ChiTietPhieuNhapHang VALUES
('PN001', 'SP0001', 30, 12500000);

-- 12. Phiếu đổi trả
CREATE TABLE PhieuDoiTra (
    MaPhieuDoiTra VARCHAR(20) NOT NULL PRIMARY KEY,
    MaHoaDonGoc VARCHAR(17) NOT NULL FOREIGN KEY REFERENCES HoaDon(MaHoaDon),
    MaNhanVienLap VARCHAR(5) NOT NULL FOREIGN KEY REFERENCES NhanVien(MaNhanVien),
    MaQuanLyDuyet VARCHAR(5) NULL FOREIGN KEY REFERENCES NhanVien(MaNhanVien),
    MaPhanCong VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES PhanCongCa(MaPhanCong),
    NgayLap DATETIME NOT NULL DEFAULT GETDATE(),
    LyDo NVARCHAR(255) NOT NULL,
    TrangThai NVARCHAR(20) NOT NULL DEFAULT N'Chờ duyệt' CHECK (TrangThai IN (N'Chờ duyệt', N'Đã duyệt', N'Từ chối')),
    NgayDuyet DATETIME NULL,
    LyDoTuChoi NVARCHAR(255) NULL,
    TongTienHoan DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK (TongTienHoan >= 0),
    DiemHoanLai INT NOT NULL DEFAULT 0 CHECK (DiemHoanLai >= 0),
    DiemThuHoi INT NOT NULL DEFAULT 0 CHECK (DiemThuHoi >= 0)
);
INSERT INTO PhieuDoiTra VALUES
('DT001', 'HD20261018CA01001', 'NV001', 'QL001', 'PC002', '2026-10-19 08:00:00', N'Khách trả sản phẩm lỗi', N'Đã duyệt', '2026-10-19 09:00:00', NULL, 14840100, 0, 148);

-- 13. Chi tiết đổi trả
CREATE TABLE ChiTietDoiTra (
    MaPhieuDoiTra VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES PhieuDoiTra(MaPhieuDoiTra) ON DELETE CASCADE,
    MaHoaDon VARCHAR(17) NOT NULL,
    MaSanPham VARCHAR(6) NOT NULL,
    SoLuongTra INT NOT NULL CHECK (SoLuongTra > 0),
    TinhTrangHang NVARCHAR(50) NOT NULL CHECK (TinhTrangHang IN (N'Nguyên vẹn', N'Lỗi')),
    TienHoan DECIMAL(18,2) NOT NULL CHECK (TienHoan >= 0),
    PRIMARY KEY(MaPhieuDoiTra, MaHoaDon, MaSanPham),
    CONSTRAINT FK_ChiTietDoiTra_HoaDon FOREIGN KEY(MaHoaDon, MaSanPham) REFERENCES ChiTietHoaDon(MaHoaDon, MaSanPham)
);
INSERT INTO ChiTietDoiTra VALUES
('DT001', 'HD20261018CA01001', 'SP0001', 1, N'Lỗi', 14840100);

GO