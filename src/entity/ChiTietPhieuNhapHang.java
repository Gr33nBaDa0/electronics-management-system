package entity;

public class ChiTietPhieuNhapHang {
    private PhieuNhapHang phieuNhapHang;
    private SanPham sanPham;
    private int soLuong;
    private double donGia;
    private double thanhTienChiTiet;

    public ChiTietPhieuNhapHang() {}

    public ChiTietPhieuNhapHang(PhieuNhapHang phieuNhapHang, SanPham sanPham, int soLuong, double donGia) {
        this.setPhieuNhapHang(phieuNhapHang);
        this.setSanPham(sanPham);
        this.setSoLuong(soLuong);
        this.setDonGia(donGia);
    }

    public PhieuNhapHang getPhieuNhapHang() { return phieuNhapHang; }
    public void setPhieuNhapHang(PhieuNhapHang phieuNhapHang) {
        if (phieuNhapHang == null) throw new IllegalArgumentException("Phiếu nhập không được rỗng");
        this.phieuNhapHang = phieuNhapHang;
    }

    public SanPham getSanPham() { return sanPham; }
    public void setSanPham(SanPham sanPham) {
        if (sanPham == null) throw new IllegalArgumentException("Sản phẩm không được rỗng");
        this.sanPham = sanPham;
    }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) {
        if (soLuong <= 0) throw new IllegalArgumentException("Số lượng phải > 0");
        this.soLuong = soLuong;
    }

    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) {
        if (donGia <= 0) throw new IllegalArgumentException("Đơn giá phải > 0");
        this.donGia = donGia;
    }

    public double getThanhTienChiTiet() { return thanhTienChiTiet; }

    public double tinhThanhTienChiTiet() {
        this.thanhTienChiTiet = this.soLuong * this.donGia;
        return this.thanhTienChiTiet;
    }

    @Override
    public String toString() {
        return (sanPham != null ? sanPham.getTenSanPham() : "") + " x " + soLuong;
    }
}