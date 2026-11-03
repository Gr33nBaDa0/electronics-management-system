package entity;

import java.math.BigDecimal;
import java.util.Objects;

public class ChiTietPhieuNhapHang {
    private PhieuNhapHang phieuNhapHang;
    private SanPham sanPham;
    private int soLuong;
    private BigDecimal donGiaNhap;

    public ChiTietPhieuNhapHang() {
    }
    public ChiTietPhieuNhapHang(PhieuNhapHang phieuNhapHang) {
        this.phieuNhapHang = phieuNhapHang;
    }
    public ChiTietPhieuNhapHang(PhieuNhapHang phieuNhapHang, SanPham sanPham, int soLuong, BigDecimal donGiaNhap) {
		this.phieuNhapHang = phieuNhapHang;
		this.sanPham = sanPham;
		this.soLuong = soLuong;
		this.donGiaNhap = donGiaNhap;
	}
    
	public PhieuNhapHang getPhieuNhapHang() {
        return phieuNhapHang;
    }
    public void setPhieuNhapHang(PhieuNhapHang phieuNhapHang) {
        if (phieuNhapHang == null) {
            throw new IllegalArgumentException("Phiếu nhập hàng không được rỗng");
        }
        this.phieuNhapHang = phieuNhapHang;
    }

    public SanPham getSanPham() {
        return sanPham;
    }
    public void setSanPham(SanPham sanPham) {
        if (sanPham == null) {
            throw new IllegalArgumentException("Sản phẩm không được rỗng");
        }
        this.sanPham = sanPham;
    }

    public int getSoLuong() {
        return soLuong;
    }
    public void setSoLuong(int soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng phải > 0");
        }
        this.soLuong = soLuong;
    }

    public BigDecimal getDonGiaNhap() {
        return donGiaNhap != null ? donGiaNhap : BigDecimal.ZERO;
    }
    public void setDonGiaNhap(BigDecimal donGiaNhap) {
        if (donGiaNhap == null || donGiaNhap.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Đơn giá nhập phải > 0");
        }
        this.donGiaNhap = donGiaNhap;
    }

    public void setDonGiaNhap(double gia) {
        setDonGiaNhap(BigDecimal.valueOf(gia));
    }
    public BigDecimal getDonGia() {
        return getDonGiaNhap();
    }

    public void setDonGia(BigDecimal gia) {
        setDonGiaNhap(gia);
    }
    public void setDonGia(double gia) {
        setDonGiaNhap(gia);
    }

    public BigDecimal getThanhTien() {
        return getDonGiaNhap().multiply(BigDecimal.valueOf(soLuong));
    }

    public double tinhThanhTienChiTiet() {
        return getThanhTien().doubleValue();
    }

    
    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChiTietPhieuNhapHang other = (ChiTietPhieuNhapHang) obj;
		return Objects.equals(phieuNhapHang, other.phieuNhapHang) && Objects.equals(sanPham, other.sanPham);
	}
	@Override
	public int hashCode() {
		return Objects.hash(phieuNhapHang, sanPham);
	}

    @Override
    public String toString() {
        return (sanPham != null ? sanPham.getTenSanPham() : "") + " x " + soLuong;
    }
}