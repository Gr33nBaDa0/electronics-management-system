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

    public ChuongTrinhKhuyenMai() {
        this.trangThai = "Đang áp dụng";
    }
    public ChuongTrinhKhuyenMai(String maKhuyenMai) {
    	this.maKhuyenMai = maKhuyenMai;
    }
    public ChuongTrinhKhuyenMai(String maKhuyenMai, String tenChuongTrinh, BigDecimal giaTriGiam, LocalDate ngayBatDau,
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
        if (maKhuyenMai == null || !maKhuyenMai.trim().matches("^KM\\d{7}$")) {
            throw new IllegalArgumentException("Mã khuyến mãi không hợp lệ (Dạng KM + năm yyyy + 3 chữ số, ví dụ KM2026001)");
        }
        this.maKhuyenMai = maKhuyenMai.trim();
    }

    public String getTenChuongTrinh() {
        return tenChuongTrinh;
    }
    public void setTenChuongTrinh(String tenChuongTrinh) {
        if (tenChuongTrinh == null || tenChuongTrinh.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên chương trình không được rỗng");
        }
        this.tenChuongTrinh = tenChuongTrinh.trim();
    }
    public BigDecimal getGiaTriGiam() {
        return giaTriGiam != null ? giaTriGiam : BigDecimal.ZERO;
    }

    public void setGiaTriGiam(BigDecimal giaTriGiam) {
        if (giaTriGiam == null || giaTriGiam.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Giá trị giảm phải lớn hơn 0");
        }
        this.giaTriGiam = giaTriGiam;
    }
    public void setGiaTriGiam(double val) {
        setGiaTriGiam(BigDecimal.valueOf(val));
    }

    public LocalDate getNgayBatDau() {
        return ngayBatDau;
    }
    public void setNgayBatDau(LocalDate ngayBatDau) {
        if (ngayBatDau == null) {
            throw new IllegalArgumentException("Ngày bắt đầu không được để trống");
        }
        this.ngayBatDau = ngayBatDau;
    }

    public LocalDate getNgayKetThuc() {
        return ngayKetThuc;
    }
    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        if (ngayKetThuc == null) {
            throw new IllegalArgumentException("Ngày kết thúc không được để trống");
        }
        if (ngayBatDau != null && ngayKetThuc.isBefore(ngayBatDau)) {
            throw new IllegalArgumentException("Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu");
        }
        this.ngayKetThuc = ngayKetThuc;
    }

    public String getTrangThai() {
        return trangThai;
    }
    public void setTrangThai(String trangThai) {
        if (trangThai == null || (!trangThai.equalsIgnoreCase("Đang áp dụng")
                && !trangThai.equalsIgnoreCase("Tạm ngưng")
                && !trangThai.equalsIgnoreCase("Hết hạn"))) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ (Đang áp dụng, Tạm ngưng hoặc Hết hạn)");
        }
        this.trangThai = trangThai;
    }

    public boolean kiemTraHieuLuc() {
        if (!"Đang áp dụng".equalsIgnoreCase(this.trangThai)) {
            return false;
        }
        LocalDate homNay = LocalDate.now();
        if (ngayBatDau != null && homNay.isBefore(ngayBatDau)) {
            return false;
        }
        if (ngayKetThuc != null && homNay.isAfter(ngayKetThuc)) {
            return false;
        }
        return true;
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
        return "ChuongTrinhKhuyenMai [maKM=" + maKhuyenMai + ", ten=" + tenChuongTrinh + ", giam=" + giaTriGiam
                + ", tuNgay=" + ngayBatDau + ", denNgay=" + ngayKetThuc + ", trangThai=" + trangThai + "]";
    }
}
