package entity;

public class ChiTietHoaDon {
    private HoaDon hoaDon;
    private SanPham sanPham;
    private int soLuong;
    private ChuongTrinhKhuyenMai chuongTrinhKhuyenMai;
    private double donGia;
    private double giamGia;
    private double thanhTienChiTiet;

    public ChiTietHoaDon() {}

    public ChiTietHoaDon(HoaDon hoaDon, SanPham sanPham, int soLuong, double donGia) {
        this.setHoaDon(hoaDon);
        this.setSanPham(sanPham);
        this.setSoLuong(soLuong);
        this.setDonGia(donGia);
    }

    public HoaDon getHoaDon() { return hoaDon; }
    public void setHoaDon(HoaDon hoaDon) {
        if (hoaDon == null) throw new IllegalArgumentException("Hóa đơn không được rỗng");
        this.hoaDon = hoaDon;
    }

    public SanPham getSanPham() { return sanPham; }
    public void setSanPham(SanPham sanPham) {
        if (sanPham == null) throw new IllegalArgumentException("Sản phẩm không được rỗng");
        this.sanPham = sanPham;
    }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) {
        if (soLuong <= 0) throw new IllegalArgumentException("Số lượng phải > 0");
        if (sanPham != null && soLuong > sanPham.getSoLuongTon()) {
            throw new IllegalArgumentException("Số lượng vượt quá tồn kho hiện có");
        }
        this.soLuong = soLuong;
    }

    public ChuongTrinhKhuyenMai getChuongTrinhKhuyenMai() { return chuongTrinhKhuyenMai; }
    public void setChuongTrinhKhuyenMai(ChuongTrinhKhuyenMai km) {
        if (km != null && !km.kiemTraHieuLuc()) {
            throw new IllegalArgumentException("Chương trình khuyến mãi không còn hiệu lực");
        }
        this.chuongTrinhKhuyenMai = km;
    }

    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) {
        if (donGia <= 0) throw new IllegalArgumentException("Đơn giá phải > 0");
        this.donGia = donGia;
    }

    public double getGiamGia() { return giamGia; }
    public void setGiamGia(double giamGia) {
        if (giamGia < 0) throw new IllegalArgumentException("Giảm giá không được âm");
        if (giamGia > this.soLuong * this.donGia) {
            throw new IllegalArgumentException("Giảm giá không được vượt quá thành tiền");
        }
        this.giamGia = giamGia;
    }

    public double getThanhTienChiTiet() { return thanhTienChiTiet; }

    /** Thành tiền = Số lượng * Đơn giá - Giảm giá */
    public double tinhThanhTienChiTiet() {
        this.thanhTienChiTiet = this.soLuong * this.donGia - this.giamGia;
        return this.thanhTienChiTiet;
    }

    @Override
    public String toString() {
        return (sanPham != null ? sanPham.getTenSanPham() : "") + " x " + soLuong;
    }
}