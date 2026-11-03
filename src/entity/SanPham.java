package entity;

import java.math.BigDecimal;
import java.util.Objects;

public class SanPham {
    private String maSanPham;
    private String tenSanPham;
    private String loaiHang;
    private String thuongHieu;
    private BigDecimal donGiaBan;
    private BigDecimal giaNhap;
    private String thongSoKiThuat;
    private String xuatXu;
    private String trangThai;
    private int thoiHanBaoHanh;
    private int soLuongTon;
    private String hinhAnh;

    public SanPham() {
        this.trangThai = "Đang bán";
        this.soLuongTon = 0;
        this.donGiaBan = BigDecimal.ZERO;
        this.giaNhap = BigDecimal.ZERO;
    }
    public SanPham(String maSanPham) {
    	this.maSanPham = maSanPham;
    }
    public SanPham(String maSanPham, String tenSanPham, String loaiHang, String thuongHieu, BigDecimal donGiaBan,
			BigDecimal giaNhap, String thongSoKiThuat, String xuatXu, String trangThai, int thoiHanBaoHanh,
			int soLuongTon, String hinhAnh) {
		super();
		this.maSanPham = maSanPham;
		this.tenSanPham = tenSanPham;
		this.loaiHang = loaiHang;
		this.thuongHieu = thuongHieu;
		this.donGiaBan = donGiaBan;
		this.giaNhap = giaNhap;
		this.thongSoKiThuat = thongSoKiThuat;
		this.xuatXu = xuatXu;
		this.trangThai = trangThai;
		this.thoiHanBaoHanh = thoiHanBaoHanh;
		this.soLuongTon = soLuongTon;
		this.hinhAnh = hinhAnh;
	}

    public String getMaSanPham() {
        return maSanPham;
    }
    public void setMaSanPham(String maSanPham) {
        if (maSanPham == null || !maSanPham.trim().matches("^SP\\d{4,}$")) {
            throw new IllegalArgumentException("Mã sản phẩm không hợp lệ (Dạng SPxxxx, ví dụ SP0001)");
        }
        this.maSanPham = maSanPham.trim();
    }

    public String getTenSanPham() {
        return tenSanPham;
    }
    public void setTenSanPham(String tenSanPham) {
        if (tenSanPham == null || tenSanPham.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được rỗng");
        }
        this.tenSanPham = tenSanPham.trim();
    }

    public String getLoaiHang() {
        return loaiHang;
    }
    public void setLoaiHang(String loaiHang) {
        if (loaiHang == null || loaiHang.trim().isEmpty()) {
            throw new IllegalArgumentException("Loại hàng không được rỗng");
        }
        this.loaiHang = loaiHang.trim();
    }

    public String getThuongHieu() {
        return thuongHieu;
    }
    public void setThuongHieu(String thuongHieu) {
        if (thuongHieu == null || thuongHieu.trim().isEmpty()) {
            throw new IllegalArgumentException("Thương hiệu không được rỗng");
        }
        this.thuongHieu = thuongHieu.trim();
    }

    public BigDecimal getDonGiaBan() {
        return donGiaBan != null ? donGiaBan : BigDecimal.ZERO;
    }
    public void setDonGiaBan(BigDecimal donGiaBan) {
        if (donGiaBan == null || donGiaBan.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Đơn giá bán không hợp lệ (Phải lớn hơn 0)");
        }
        if (this.giaNhap != null && this.giaNhap.compareTo(BigDecimal.ZERO) > 0 && donGiaBan.compareTo(this.giaNhap) < 0) {
            throw new IllegalArgumentException("Đơn giá bán không hợp lệ (Phải lớn hơn hoặc bằng giá nhập)");
        }
        this.donGiaBan = donGiaBan;
    }
    public void setDonGiaBan(double gia) {
        setDonGiaBan(BigDecimal.valueOf(gia));
    }
    
    public BigDecimal getGiaNhap() {
        return giaNhap != null ? giaNhap : BigDecimal.ZERO;
    }
    public void setGiaNhap(BigDecimal giaNhap) {
        if (giaNhap == null || giaNhap.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Giá nhập phải > 0");
        }
        this.giaNhap = giaNhap;
    }
    public void setGiaNhap(double gia) {
        setGiaNhap(BigDecimal.valueOf(gia));
    }

    public String getThongSoKiThuat() {
        return thongSoKiThuat;
    }
    public void setThongSoKiThuat(String thongSoKiThuat) {
        this.thongSoKiThuat = thongSoKiThuat;
    }

    public String getXuatXu() {
        return xuatXu;
    }
    public void setXuatXu(String xuatXu) {
        if (xuatXu == null || xuatXu.trim().isEmpty()) {
            throw new IllegalArgumentException("Xuất xứ không được rỗng");
        }
        this.xuatXu = xuatXu.trim();
    }

    public String getTrangThai() {
        return trangThai;
    }
    public void setTrangThai(String trangThai) {
        if (trangThai == null || (!trangThai.equalsIgnoreCase("Đang bán") && !trangThai.equalsIgnoreCase("Ngưng bán"))) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ (Chỉ nhận 'Đang bán' hoặc 'Ngưng bán')");
        }
        this.trangThai = trangThai;
    }

    public int getThoiHanBaoHanh() {
        return thoiHanBaoHanh;
    }
    public void setThoiHanBaoHanh(int thoiHanBaoHanh) {
        if (thoiHanBaoHanh < 0) {
            throw new IllegalArgumentException("Thời hạn bảo hành không hợp lệ (Phải >= 0)");
        }
        this.thoiHanBaoHanh = thoiHanBaoHanh;
    }
    public void setBaoHanhThang(int thang) {
        setThoiHanBaoHanh(thang);
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }
    public void setSoLuongTon(int soLuongTon) {
        if (soLuongTon < 0) {
            throw new IllegalArgumentException("Số lượng tồn không được âm");
        }
        this.soLuongTon = soLuongTon;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }
    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public double capNhatGiaBan(double giaMoi) {
        setDonGiaBan(giaMoi);
        return giaMoi;
    }

    public BigDecimal capNhatGiaBan(BigDecimal giaMoi) {
        setDonGiaBan(giaMoi);
        return giaMoi;
    }

    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SanPham other = (SanPham) obj;
		return Objects.equals(maSanPham, other.maSanPham);
	}
	@Override
	public int hashCode() {
		return Objects.hash(maSanPham);
	}

    @Override
    public String toString() {
        return maSanPham + " · " + tenSanPham;
    }
}