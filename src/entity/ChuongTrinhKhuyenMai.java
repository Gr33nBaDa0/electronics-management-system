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

    public ChuongTrinhKhuyenMai(String maKhuyenMai) {
        this.maKhuyenMai = maKhuyenMai;
    }

    

    public ChuongTrinhKhuyenMai(String maKhuyenMai, String tenChuongTrinh, double giaTriGiam, LocalDate ngayBatDau,
			LocalDate ngayKetThuc, String trangThai) {
		this.maKhuyenMai = maKhuyenMai;
		this.tenChuongTrinh = tenChuongTrinh;
		this.giaTriGiam = giaTriGiam;
		this.ngayBatDau = ngayBatDau;
		this.ngayKetThuc = ngayKetThuc;
		this.trangThai = trangThai;
	}

	public String getMaKhuyenMai() {
        return maKhuyenMai;
    }

    public void setMaKhuyenMai(String maKhuyenMai) {
        this.maKhuyenMai = maKhuyenMai;
    }

    public String getTenChuongTrinh() {
        return tenChuongTrinh;
    }

    public void setTenChuongTrinh(String tenChuongTrinh) {
        this.tenChuongTrinh = tenChuongTrinh;
    }

    public double getGiaTriGiam() {
        return giaTriGiam;
    }

    public void setGiaTriGiam(double giaTriGiam) {
        this.giaTriGiam = giaTriGiam;
    }

    public LocalDate getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(LocalDate ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public LocalDate getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public boolean isDangHieuLuc(java.time.LocalDate ngay) {
        return ngay != null && ngayBatDau != null && ngayKetThuc != null
                && "Đang áp dụng".equals(trangThai)
                && !ngay.isBefore(ngayBatDau) && !ngay.isAfter(ngayKetThuc)
                && Double.isFinite(giaTriGiam) && giaTriGiam >= 0 && giaTriGiam <= 50.0;
    }

    @Override
    public String toString() {
        return String.valueOf(tenChuongTrinh);
    }
}
