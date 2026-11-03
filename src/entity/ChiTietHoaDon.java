package entity;

import java.math.BigDecimal;
import java.util.Objects;

public class ChiTietHoaDon {
    private HoaDon hoaDon;
    private SanPham sanPham;
    private int soLuong;
    private ChuongTrinhKhuyenMai chuongTrinhKhuyenMai;
    private BigDecimal donGia;
    private BigDecimal giamGia;

    public ChiTietHoaDon() {
        this.donGia = BigDecimal.ZERO;
        this.giamGia = BigDecimal.ZERO;
    }
    public ChiTietHoaDon(HoaDon hoaDon) {
        this();
        this.hoaDon = hoaDon;
    }
    public ChiTietHoaDon(HoaDon hoaDon, SanPham sanPham, int soLuong, ChuongTrinhKhuyenMai chuongTrinhKhuyenMai,
			BigDecimal donGia, BigDecimal giamGia) {
		super();
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
        if (hoaDon == null) {
            throw new IllegalArgumentException("Hóa đơn không được rỗng");
        }
        this.hoaDon = hoaDon;
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

    public ChuongTrinhKhuyenMai getChuongTrinhKhuyenMai() {
        return chuongTrinhKhuyenMai;
    }
    public void setChuongTrinhKhuyenMai(ChuongTrinhKhuyenMai chuongTrinhKhuyenMai) {
        this.chuongTrinhKhuyenMai = chuongTrinhKhuyenMai;
    }

    public BigDecimal getDonGia() {
        return donGia != null ? donGia : BigDecimal.ZERO;
    }
    public void setDonGia(BigDecimal donGia) {
        if (donGia == null || donGia.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Đơn giá phải > 0");
        }
        this.donGia = donGia;
    }

    public void setDonGia(double gia) {
        setDonGia(BigDecimal.valueOf(gia));
    }
    public BigDecimal getGiamGia() {
        return giamGia != null ? giamGia : BigDecimal.ZERO;
    }

    public void setGiamGia(BigDecimal giamGia) {
        if (giamGia != null && giamGia.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giảm giá không hợp lệ (Không được âm)");
        }
        this.giamGia = giamGia != null ? giamGia : BigDecimal.ZERO;
    }
    public void setGiamGia(double giam) {
        setGiamGia(BigDecimal.valueOf(giam));
    }

    public BigDecimal getThanhTienChiTiet() {
        BigDecimal tienHang = getDonGia().multiply(BigDecimal.valueOf(soLuong));
        BigDecimal conLai = tienHang.subtract(getGiamGia());
        return conLai.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : conLai;
    }
    public double tinhThanhTienChiTiet() {
        return getThanhTienChiTiet().doubleValue();
    }

    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChiTietHoaDon other = (ChiTietHoaDon) obj;
		return Objects.equals(hoaDon, other.hoaDon) && Objects.equals(sanPham, other.sanPham);
	}
	@Override
	public int hashCode() {
		return Objects.hash(hoaDon, sanPham);
	}

    @Override
    public String toString() {
        return sanPham == null
                ? ""
                : sanPham.getMaSanPham() + " · " + sanPham.getTenSanPham() + " × " + soLuong;
    }
}