package entity;

import java.time.LocalDateTime;

public class PhieuNhapHang {
    private String maPhieuNhan;
    private LocalDateTime ngayLap;
    private String soChungTu;
    private String nhaCungCap;
    private String nguoiGiao;
    private NhanVien nhanVienLap;
    private NhanVien quanLyDuyet;
    private String trangThai;
    private LocalDateTime ngayDuyet;
    private String lyDoTuChoi;
    private double tongTien;

    public PhieuNhapHang() {}

    public String getMaPhieuNhan() { return maPhieuNhan; }
    public void setMaPhieuNhan(String maPhieuNhan) {
        if (maPhieuNhan == null || !maPhieuNhan.matches("^PN\\d{3,}$")) {
            throw new IllegalArgumentException("Mã phiếu nhập không hợp lệ (VD: PN001)");
        }
        this.maPhieuNhan = maPhieuNhan;
    }

    public LocalDateTime getNgayLap() { return ngayLap; }
    public void setNgayLap(LocalDateTime ngayLap) {
        if (ngayLap == null) throw new IllegalArgumentException("Ngày lập không được rỗng");
        this.ngayLap = ngayLap;
    }

    public String getSoChungTu() { return soChungTu; }
    public void setSoChungTu(String soChungTu) {
        if (soChungTu == null || soChungTu.trim().isEmpty()) throw new IllegalArgumentException("Số chứng từ không được rỗng");
        this.soChungTu = soChungTu;
    }

    public String getNhaCungCap() { return nhaCungCap; }
    public void setNhaCungCap(String nhaCungCap) {
        if (nhaCungCap == null || nhaCungCap.trim().isEmpty()) throw new IllegalArgumentException("Nhà cung cấp không được rỗng");
        this.nhaCungCap = nhaCungCap;
    }

    public String getNguoiGiao() { return nguoiGiao; }
    public void setNguoiGiao(String nguoiGiao) {
        if (nguoiGiao == null || nguoiGiao.trim().isEmpty()) throw new IllegalArgumentException("Người giao không được rỗng");
        this.nguoiGiao = nguoiGiao;
    }

    public NhanVien getNhanVienLap() { return nhanVienLap; }
    public void setNhanVienLap(NhanVien nhanVienLap) {
        if (nhanVienLap == null) throw new IllegalArgumentException("Nhân viên lập không được rỗng");
        this.nhanVienLap = nhanVienLap;
    }

    public NhanVien getQuanLyDuyet() { return quanLyDuyet; }
    public void setQuanLyDuyet(NhanVien quanLyDuyet) { this.quanLyDuyet = quanLyDuyet; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) {
        if (!"Chờ duyệt".equals(trangThai) && !"Đã duyệt".equals(trangThai) && !"Từ chối".equals(trangThai)) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ");
        }
        this.trangThai = trangThai;
    }

    public LocalDateTime getNgayDuyet() { return ngayDuyet; }
    public void setNgayDuyet(LocalDateTime ngayDuyet) { this.ngayDuyet = ngayDuyet; }

    public String getLyDoTuChoi() { return lyDoTuChoi; }
    public void setLyDoTuChoi(String lyDoTuChoi) { this.lyDoTuChoi = lyDoTuChoi; }

    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) {
        if (tongTien < 0) throw new IllegalArgumentException("Tổng tiền không được âm");
        this.tongTien = tongTien;
    }

    @Override
    public String toString() {
        return maPhieuNhan + " - " + trangThai;
    }
}