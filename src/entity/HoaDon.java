package entity;

import java.time.LocalDateTime;

public class HoaDon {
    private String maHoaDon;
    private KhachHang khachHang;
    private NhanVien nhanVien;
    private LocalDateTime ngayLap;
    private String hinhThucThanhToan;
    private String trangThaiThanhToan;
    private double thueSuatVAT;
    private PhanCongCa phanCongCa;
    private int diemSuDung;

    public HoaDon() {}

    public HoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public HoaDon(String maHoaDon, KhachHang khachHang, NhanVien nhanVien, LocalDateTime ngayLap,
			String hinhThucThanhToan, String trangThaiThanhToan, double thueSuatVAT, PhanCongCa phanCongCa,
			int diemSuDung) {
		this.maHoaDon = maHoaDon;
		this.khachHang = khachHang;
		this.nhanVien = nhanVien;
		this.ngayLap = ngayLap;
		this.hinhThucThanhToan = hinhThucThanhToan;
		this.trangThaiThanhToan = trangThaiThanhToan;
		this.thueSuatVAT = thueSuatVAT;
		this.phanCongCa = phanCongCa;
		this.diemSuDung = diemSuDung;
	}

	public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public LocalDateTime getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDateTime ngayLap) {
        this.ngayLap = ngayLap;
    }

    public String getHinhThucThanhToan() {
        return hinhThucThanhToan;
    }

    public void setHinhThucThanhToan(String hinhThucThanhToan) {
        this.hinhThucThanhToan = hinhThucThanhToan;
    }

    public String getTrangThaiThanhToan() {
        return trangThaiThanhToan;
    }

    public void setTrangThaiThanhToan(String trangThaiThanhToan) {
        this.trangThaiThanhToan = trangThaiThanhToan;
    }

    public double getThueSuatVAT() {
        return thueSuatVAT;
    }

    public void setThueSuatVAT(double thueSuatVAT) {
        this.thueSuatVAT = thueSuatVAT;
    }

    public PhanCongCa getPhanCongCa() {
        return phanCongCa;
    }

    public void setPhanCongCa(PhanCongCa phanCongCa) {
        this.phanCongCa = phanCongCa;
    }

    public int getDiemSuDung() {
        return diemSuDung;
    }

    public void setDiemSuDung(int diemSuDung) {
        this.diemSuDung = diemSuDung;
    }

    @Override
    public String toString() {
        return String.valueOf(maHoaDon);
    }
}
