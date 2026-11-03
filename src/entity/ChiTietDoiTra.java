package entity;

import java.math.BigDecimal;
import java.util.Objects;

public class ChiTietDoiTra {
    private PhieuDoiTra phieuDoiTra;
    private ChiTietHoaDon chiTietHoaDon;
    private int soLuongTra;
    private String tinhTrangHang;
    private BigDecimal tienHoan;

    public ChiTietDoiTra() {
        this.soLuongTra = 1;
        this.tienHoan = BigDecimal.ZERO;
    }

    public ChiTietDoiTra(PhieuDoiTra phieuDoiTra) {
    	this();
        this.phieuDoiTra = phieuDoiTra;
    }
    public ChiTietDoiTra(PhieuDoiTra phieuDoiTra, ChiTietHoaDon chiTietHoaDon, int soLuongTra, String tinhTrangHang,
			BigDecimal tienHoan) {
		this.phieuDoiTra = phieuDoiTra;
		this.chiTietHoaDon = chiTietHoaDon;
		this.soLuongTra = soLuongTra;
		this.tinhTrangHang = tinhTrangHang;
		this.tienHoan = tienHoan != null ? tienHoan : BigDecimal.ZERO;
	}

    public PhieuDoiTra getPhieuDoiTra() {
        return phieuDoiTra;
    }
    public void setPhieuDoiTra(PhieuDoiTra phieuDoiTra) {
        if (phieuDoiTra == null) {
            throw new IllegalArgumentException("Phiếu đổi trả không được rỗng");
        }
        this.phieuDoiTra = phieuDoiTra;
    }

    public ChiTietHoaDon getChiTietHoaDon() {
        return chiTietHoaDon;
    }
    public void setChiTietHoaDon(ChiTietHoaDon chiTietHoaDon) {
        if (chiTietHoaDon == null) {
            throw new IllegalArgumentException("Chi tiết hóa đơn không được rỗng");
        }
        this.chiTietHoaDon = chiTietHoaDon;
    }

    public int getSoLuongTra() {
        return soLuongTra;
    }
    public void setSoLuongTra(int soLuongTra) {
        if (soLuongTra <= 0) {
            throw new IllegalArgumentException("Số lượng trả phải > 0");
        }
        this.soLuongTra = soLuongTra;
    }

    public int getSoLuong() {
        return getSoLuongTra();
    }
    public void setSoLuong(int soLuong) {
        setSoLuongTra(soLuong);
    }

    public String getTinhTrangHang() {
        return tinhTrangHang;
    }
    public void setTinhTrangHang(String tinhTrangHang) {
        if (tinhTrangHang == null || tinhTrangHang.trim().isEmpty()) {
            throw new IllegalArgumentException("Tình trạng hàng không được rỗng");
        }
        this.tinhTrangHang = tinhTrangHang.trim();
    }

    public BigDecimal getTienHoan() {
        return tienHoan != null ? tienHoan : BigDecimal.ZERO;
    }
    public void setTienHoan(BigDecimal tienHoan) {
        this.tienHoan = tienHoan != null ? tienHoan : BigDecimal.ZERO;
    }

    public void setTienHoan(double tien) {
        setTienHoan(BigDecimal.valueOf(tien));
    }
    public double tinhTienHoan() {
        if (chiTietHoaDon != null && chiTietHoaDon.getDonGia() != null) {
            BigDecimal dg = chiTietHoaDon.getDonGia();
            BigDecimal th = dg.multiply(BigDecimal.valueOf(soLuongTra));
            this.tienHoan = th;
            return th.doubleValue();
        }
        return tienHoan != null ? tienHoan.doubleValue() : 0.0;
    }

    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChiTietDoiTra other = (ChiTietDoiTra) obj;
		return Objects.equals(chiTietHoaDon, other.chiTietHoaDon) && Objects.equals(phieuDoiTra, other.phieuDoiTra);
	}
	@Override
	public int hashCode() {
		return Objects.hash(chiTietHoaDon, phieuDoiTra);
	}

    @Override
    public String toString() {
        return "ChiTietDoiTra [soLuongTra=" + soLuongTra + ", tinhTrang=" + tinhTrangHang + ", tienHoan=" + tienHoan + "]";
    }
}
