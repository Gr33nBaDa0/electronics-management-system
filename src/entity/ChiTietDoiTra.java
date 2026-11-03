package entity;


public class ChiTietDoiTra {
    private PhieuDoiTra phieuDoiTra;
    private ChiTietHoaDon chiTietHoaDon;
    private int soLuongTra;
    private String tinhTrangHang;
    private BigDecimal tienHoan;

    public ChiTietDoiTra() {}

    public ChiTietDoiTra(PhieuDoiTra phieuDoiTra) {
        this.phieuDoiTra = phieuDoiTra;
    }

    public ChiTietDoiTra(
            PhieuDoiTra phieuDoiTra,
            ChiTietHoaDon chiTietHoaDon,
            int soLuongTra,
            String tinhTrangHang,
            double tienHoan) {
        this.phieuDoiTra = phieuDoiTra;
        this.chiTietHoaDon = chiTietHoaDon;
        this.soLuongTra = soLuongTra;
        this.tinhTrangHang = tinhTrangHang;
        this.tienHoan = tienHoan;
    }

    public PhieuDoiTra getPhieuDoiTra() {
        return phieuDoiTra;
    }

    public void setPhieuDoiTra(PhieuDoiTra phieuDoiTra) {
        this.phieuDoiTra = phieuDoiTra;
    }

    public ChiTietHoaDon getChiTietHoaDon() {
        return chiTietHoaDon;
    }

    public void setChiTietHoaDon(ChiTietHoaDon chiTietHoaDon) {
        this.chiTietHoaDon = chiTietHoaDon;
    }

    public int getSoLuongTra() {
        return soLuongTra;
    }

    public void setSoLuongTra(int soLuongTra) {
        this.soLuongTra = soLuongTra;
    }

    public String getTinhTrangHang() {
        return tinhTrangHang;
    }

    public void setTinhTrangHang(String tinhTrangHang) {
        this.tinhTrangHang = tinhTrangHang;
    }

    public double getTienHoan() {
        return tienHoan;
    }

    public void setTienHoan(double tienHoan) {
        this.tienHoan = tienHoan;
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
        return String.valueOf(phieuDoiTra);
    }
}
