package entity;


public class ChiTietHoaDon {
    private HoaDon hoaDon;
    private SanPham sanPham;
    private int soLuong;
    private ChuongTrinhKhuyenMai chuongTrinhKhuyenMai;
    private double donGia;
    private double giamGia;

    public ChiTietHoaDon() {}

    public ChiTietHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }



    public ChiTietHoaDon(HoaDon hoaDon, SanPham sanPham, int soLuong, ChuongTrinhKhuyenMai chuongTrinhKhuyenMai,
			double donGia, double giamGia) {
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

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    public double getGiamGia() {
        return giamGia;
    }

    public void setGiamGia(double giamGia) {
        this.giamGia = giamGia;
    }

    public double getThanhTienChiTiet() {
        return donGia * soLuong - giamGia;
    }

    @Override
    public String toString() {
        return sanPham == null
                ? ""
                : sanPham.getMaSanPham() + " · " + sanPham.getTenSanPham() + " × " + soLuong;
    }
}
