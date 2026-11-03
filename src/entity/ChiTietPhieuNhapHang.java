package entity;


public class ChiTietPhieuNhapHang {
    private PhieuNhapHang phieuNhapHang;
    private SanPham sanPham;
    private int soLuong;
    private double donGiaNhap;

    public ChiTietPhieuNhapHang() {}

    public ChiTietPhieuNhapHang(PhieuNhapHang phieuNhapHang) {
        this.phieuNhapHang = phieuNhapHang;
    }

    public ChiTietPhieuNhapHang(
            PhieuNhapHang phieuNhapHang, SanPham sanPham, int soLuong, double donGiaNhap) {
        this.phieuNhapHang = phieuNhapHang;
        this.sanPham = sanPham;
        this.soLuong = soLuong;
        this.donGiaNhap = donGiaNhap;
    }

    public PhieuNhapHang getPhieuNhapHang() {
        return phieuNhapHang;
    }

    public void setPhieuNhapHang(PhieuNhapHang phieuNhapHang) {
        this.phieuNhapHang = phieuNhapHang;
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

    public double getDonGiaNhap() {
        return donGiaNhap;
    }

    public void setDonGiaNhap(double donGiaNhap) {
        this.donGiaNhap = donGiaNhap;
    }

    public double getThanhTien() {
        return donGiaNhap * soLuong;
    }

    @Override
    public String toString() {
        return String.valueOf(phieuNhapHang);
    }
}
