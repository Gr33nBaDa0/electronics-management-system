package entity;

import java.time.LocalDate;

public class ChuongTrinhKhuyenMai {
    private String maKhuyenMai;
    private String tenChuongTrinh;
    private double giaTriGiam;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private String trangThai;

    public ChuongTrinhKhuyenMai() {}

    public String getMaKhuyenMai() { return maKhuyenMai; }
    public void setMaKhuyenMai(String maKhuyenMai) {
        // KM + yyyy + 3 chữ số (VD: KM2026001)
        if (maKhuyenMai == null || !maKhuyenMai.matches("^KM\\d{7}$")) {
            throw new IllegalArgumentException("Mã khuyến mãi không hợp lệ (VD: KM2026001)");
        }
        this.maKhuyenMai = maKhuyenMai;
    }

    public String getTenChuongTrinh() { return tenChuongTrinh; }
    public void setTenChuongTrinh(String tenChuongTrinh) {
        if (tenChuongTrinh == null || tenChuongTrinh.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên chương trình không được rỗng");
        }
        this.tenChuongTrinh = tenChuongTrinh;
    }

    public double getGiaTriGiam() { return giaTriGiam; }
    public void setGiaTriGiam(double giaTriGiam) {
        if (giaTriGiam <= 0 || giaTriGiam > 50) {
            throw new IllegalArgumentException("Giá trị giảm phải > 0 và <= 50 (%)");
        }
        this.giaTriGiam = giaTriGiam;
    }

    public LocalDate getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(LocalDate ngayBatDau) {
        if (ngayBatDau == null) throw new IllegalArgumentException("Ngày bắt đầu không được rỗng");
        this.ngayBatDau = ngayBatDau;
    }

    public LocalDate getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        if (ngayKetThuc == null) throw new IllegalArgumentException("Ngày kết thúc không được rỗng");
        if (ngayBatDau != null && ngayKetThuc.isBefore(ngayBatDau)) {
            throw new IllegalArgumentException("Ngày kết thúc phải >= ngày bắt đầu");
        }
        this.ngayKetThuc = ngayKetThuc;
    }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) {
        if (!"Đang áp dụng".equals(trangThai) && !"Tạm ngừng".equals(trangThai) && !"Hết hạn".equals(trangThai)) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ");
        }
        this.trangThai = trangThai;
    }

    /** Kiểm tra còn hiệu lực không */
    public boolean kiemTraHieuLuc() {
        if (!"Đang áp dụng".equals(this.trangThai)) return false;
        LocalDate now = LocalDate.now();
        return !now.isBefore(this.ngayBatDau) && !now.isAfter(this.ngayKetThuc);
    }

    @Override
    public String toString() {
        return tenChuongTrinh + " (" + giaTriGiam + "%)";
    }
}