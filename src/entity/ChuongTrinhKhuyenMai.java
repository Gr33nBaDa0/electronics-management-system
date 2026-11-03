package entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class ChuongTrinhKhuyenMai {
    private String maKhuyenMai;
    private String tenChuongTrinh;
    private BigDecimal giaTriGiam;
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
    public void setGiaTriGiam(double val) {
        setGiaTriGiam(BigDecimal.valueOf(val));
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

    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChuongTrinhKhuyenMai other = (ChuongTrinhKhuyenMai) obj;
		return Objects.equals(maKhuyenMai, other.maKhuyenMai);
	}
	@Override
	public int hashCode() {
		return Objects.hash(maKhuyenMai);
	}

    @Override
    public String toString() {
        return String.valueOf(tenChuongTrinh);
    }
}
