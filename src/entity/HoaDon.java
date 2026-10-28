package entity;

import java.time.LocalDate;

public class HoaDon {
    private String maHoaDon;
    private KhachHang khachHang; // Có thể null (khách vãng lai)
    private NhanVien nhanVien;
    private PhanCongCa phanCongCa;
    private LocalDate ngayLap;
    private String hinhThucThanhToan;
    private String trangThaiThanhToan;
    private double tongTienHang;
    private double tongGiamGia;
    private double thanhTien;
    private double tienVAT;
    private double thueSuatVAT = 0.10; // Mặc định 10%
    private int diemCong;
    private double tienGiamTuDiem;

    public HoaDon() {}

    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) {
        // Regex: HD + 8 số (ngày) + CA + 2 số (ca) + 3 số (thứ tự)
        if (maHoaDon == null || !maHoaDon.matches("^HD\\d{8}CA\\d{2}\\d{3}$")) {
            throw new IllegalArgumentException("Mã hóa đơn sai định dạng (VD: HD20261018CA01001)");
        }
        this.maHoaDon = maHoaDon;
    }

    public KhachHang getKhachHang() { return khachHang; }
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }

    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien nhanVien) {
        if (nhanVien == null) throw new IllegalArgumentException("Nhân viên không được rỗng");
        this.nhanVien = nhanVien;
    }

    public PhanCongCa getPhanCongCa() { return phanCongCa; }
    public void setPhanCongCa(PhanCongCa phanCongCa) { this.phanCongCa = phanCongCa; }

    public LocalDate getNgayLap() { return ngayLap; }
    public void setNgayLap(LocalDate ngayLap) {
        if (ngayLap == null || ngayLap.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày lập không được lớn hơn ngày hiện tại");
        }
        this.ngayLap = ngayLap;
    }

    public String getHinhThucThanhToan() { return hinhThucThanhToan; }
    public void setHinhThucThanhToan(String hinhThucThanhToan) {
        if (!"Tiền mặt".equals(hinhThucThanhToan) && !"Chuyển khoản QR".equals(hinhThucThanhToan)) {
            throw new IllegalArgumentException("Hình thức thanh toán không hợp lệ");
        }
        this.hinhThucThanhToan = hinhThucThanhToan;
    }

    public String getTrangThaiThanhToan() { return trangThaiThanhToan; }
    public void setTrangThaiThanhToan(String trangThaiThanhToan) {
        if (!"Chưa thanh toán".equals(trangThaiThanhToan) && 
            !"Đã thanh toán".equals(trangThaiThanhToan) && 
            !"Đã hủy".equals(trangThaiThanhToan)) {
            throw new IllegalArgumentException("Trạng thái thanh toán không hợp lệ");
        }
        this.trangThaiThanhToan = trangThaiThanhToan;
    }

    public double getTongTienHang() { return tongTienHang; }
    public void setTongTienHang(double tongTienHang) { this.tongTienHang = tongTienHang; }

    public double getTongGiamGia() { return tongGiamGia; }
    public void setTongGiamGia(double tongGiamGia) { this.tongGiamGia = tongGiamGia; }

    public double getThanhTien() { return thanhTien; }
    public void setThanhTien(double thanhTien) { this.thanhTien = thanhTien; }

    public double getTienVAT() { return tienVAT; }
    public void setTienVAT(double tienVAT) { this.tienVAT = tienVAT; }

    public double getThueSuatVAT() { return thueSuatVAT; }
    public void setThueSuatVAT(double thueSuatVAT) {
        if (thueSuatVAT < 0 || thueSuatVAT >= 1) throw new IllegalArgumentException("Thuế suất VAT không hợp lệ");
        this.thueSuatVAT = thueSuatVAT;
    }

    public int getDiemCong() { return diemCong; }
    public void setDiemCong(int diemCong) { this.diemCong = diemCong; }

    public double getTienGiamTuDiem() { return tienGiamTuDiem; }
    public void setTienGiamTuDiem(double tienGiamTuDiem) { this.tienGiamTuDiem = tienGiamTuDiem; }
}