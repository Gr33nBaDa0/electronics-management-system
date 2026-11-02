package entity;

import java.math.BigDecimal;

public class ChiTietHoaDon {
    private HoaDon hoaDon;
    private SanPham sanPham;
    private int soLuong;
    private ChuongTrinhKhuyenMai chuongTrinhKhuyenMai;
    private BigDecimal donGia;
    private BigDecimal giamGia;

    public ChiTietHoaDon() {}

    public ChiTietHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }



    public ChiTietHoaDon(HoaDon hoaDon, SanPham sanPham, int soLuong, ChuongTrinhKhuyenMai chuongTrinhKhuyenMai,
			BigDecimal donGia, BigDecimal giamGia) {
		this.hoaDon = hoaDon;
		this.sanPham = sanPham;
		this.soLuong = soLuong;
		this.chuongTrinhKhuyenMai = chuongTrinhKhuyenMai;
		this.donGia = donGia;
		this.giamGia = giamGia;
	}

	public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public ChuongTrinhKhuyenMai getChuongTrinhKhuyenMai() {
        return chuongTrinhKhuyenMai;
    }

    public void setChuongTrinhKhuyenMai(ChuongTrinhKhuyenMai chuongTrinhKhuyenMai) {
        this.chuongTrinhKhuyenMai = chuongTrinhKhuyenMai;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    public BigDecimal getGiamGia() {
        return giamGia;
    }

    public void setGiamGia(BigDecimal giamGia) {
        this.giamGia = giamGia;
    }

    public BigDecimal getThanhTienChiTiet() {
        return donGia.multiply(BigDecimal.valueOf(soLuong)).subtract(giamGia);
    }

    @Override
    public String toString() {
        return sanPham == null
                ? ""
                : sanPham.getMaSanPham() + " · " + sanPham.getTenSanPham() + " × " + soLuong;
    }
}
