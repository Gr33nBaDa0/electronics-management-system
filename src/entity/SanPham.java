package entity;

import java.math.BigDecimal;

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

    public SanPham() {
    }

    public String getMaSanPham() { return maSanPham; }
    public void setMaSanPham(String maSanPham) { this.maSanPham = maSanPham; }
    public String getTenSanPham() { return tenSanPham; }
    public void setTenSanPham(String tenSanPham) { this.tenSanPham = tenSanPham; }
    public String getLoaiHang() { return loaiHang; }
    public void setLoaiHang(String loaiHang) { this.loaiHang = loaiHang; }
    public String getThuongHieu() { return thuongHieu; }
    public void setThuongHieu(String thuongHieu) { this.thuongHieu = thuongHieu; }
    public BigDecimal getDonGiaBan() { return donGiaBan; }
    public void setDonGiaBan(BigDecimal donGiaBan) { this.donGiaBan = donGiaBan; }
    public BigDecimal getGiaNhap() { return giaNhap; }
    public void setGiaNhap(BigDecimal giaNhap) { this.giaNhap = giaNhap; }
    public String getThongSoKiThuat() { return thongSoKiThuat; }
    public void setThongSoKiThuat(String thongSoKiThuat) { this.thongSoKiThuat = thongSoKiThuat; }
    public String getXuatXu() { return xuatXu; }
    public void setXuatXu(String xuatXu) { this.xuatXu = xuatXu; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public int getThoiHanBaoHanh() { return thoiHanBaoHanh; }
    public void setThoiHanBaoHanh(int thoiHanBaoHanh) { this.thoiHanBaoHanh = thoiHanBaoHanh; }
    public int getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(int soLuongTon) { this.soLuongTon = soLuongTon; }
}
