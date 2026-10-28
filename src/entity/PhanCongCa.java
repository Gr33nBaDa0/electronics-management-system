package entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PhanCongCa {
    private String maPhanCong;
    private NhanVien nhanVien;
    private LocalDate ngay;
    private String ghiChu;
    private CaLamViec caLamViec;
    private double tienQuyDauCa;
    private double tienMatThucDem;
    private String trangThai;
    private LocalDateTime gioMoCa;
    private LocalDateTime gioDongCa;
    private double tienMatLyThuyet;
    private double chenhLech;

    public PhanCongCa() {}

    public String getMaPhanCong() { return maPhanCong; }
    public void setMaPhanCong(String maPhanCong) {
    	if (maPhanCong == null || !maPhanCong.matches("^PC\\d{3}$")) {
            throw new IllegalArgumentException("Mã phân công không được rỗng");
        }
        this.maPhanCong = maPhanCong;
    }

    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien nhanVien) {
        if (nhanVien == null) throw new IllegalArgumentException("Nhân viên không được rỗng");
        this.nhanVien = nhanVien;
    }

    public LocalDate getNgay() { return ngay; }
    public void setNgay(LocalDate ngay) {
        if (ngay == null) throw new IllegalArgumentException("Ngày làm việc không được rỗng");
        this.ngay = ngay;
    }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public CaLamViec getCaLamViec() { return caLamViec; }
    public void setCaLamViec(CaLamViec caLamViec) {
        if (caLamViec == null) throw new IllegalArgumentException("Ca làm việc không được rỗng");
        this.caLamViec = caLamViec;
    }

    public double getTienQuyDauCa() { return tienQuyDauCa; }
    public void setTienQuyDauCa(double tienQuyDauCa) {
        if (tienQuyDauCa < 0) throw new IllegalArgumentException("Tiền quỹ đầu ca không được âm");
        this.tienQuyDauCa = tienQuyDauCa;
    }

    public double getTienMatThucDem() { return tienMatThucDem; }
    public void setTienMatThucDem(double tienMatThucDem) {
        if (tienMatThucDem < 0) throw new IllegalArgumentException("Tiền mặt thực đếm không được âm");
        this.tienMatThucDem = tienMatThucDem;
    }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) {
        if (!"Chưa mở".equals(trangThai) && !"Đang mở".equals(trangThai) && !"Đã đóng".equals(trangThai)) {
            throw new IllegalArgumentException("Trạng thái ca không hợp lệ");
        }
        this.trangThai = trangThai;
    }

    public LocalDateTime getGioMoCa() { return gioMoCa; }
    public void setGioMoCa(LocalDateTime gioMoCa) { this.gioMoCa = gioMoCa; }

    public LocalDateTime getGioDongCa() { return gioDongCa; }
    public void setGioDongCa(LocalDateTime gioDongCa) { this.gioDongCa = gioDongCa; }

    public double getTienMatLyThuyet() { return tienMatLyThuyet; }
    public void setTienMatLyThuyet(double tienMatLyThuyet) { this.tienMatLyThuyet = tienMatLyThuyet; }

    public double getChenhLech() { return chenhLech; }
    public void setChenhLech(double chenhLech) { this.chenhLech = chenhLech; }

    // --- Business methods ---
    public boolean xacNhanVaoCa() {
        return "Chưa mở".equals(this.trangThai) && this.tienQuyDauCa >= 0;
    }

    public boolean xacNhanDongCa() {
        return "Đang mở".equals(this.trangThai);
    }

    /** Tính tiền mặt lý thuyết = Đầu ca + Thu - Chi (truyền vào) */
    public double tinhTienMatLyThuyet(double thuTienMat, double chiHoanTien) {
        this.tienMatLyThuyet = this.tienQuyDauCa + thuTienMat - chiHoanTien;
        return this.tienMatLyThuyet;
    }

    /** Chênh lệch = Thực đếm - Lý thuyết */
    public double tinhChenhLech() {
        this.chenhLech = this.tienMatThucDem - this.tienMatLyThuyet;
        return this.chenhLech;
    }

    @Override
    public String toString() {
        return maPhanCong + " - " + (nhanVien != null ? nhanVien.getHoTen() : "");
    }
}