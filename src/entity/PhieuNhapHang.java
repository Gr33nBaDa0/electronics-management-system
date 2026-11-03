package entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Thực thể PhieuNhapHang (PhieuNhanHang) - Bảng 4.8 Đặc tả Class Diagram
 */
public class PhieuNhapHang {
    private String maPhieuNhap;
    private LocalDateTime ngayLap;
    private String soChungTu;
    private String nhaCungCap;
    private String nguoiGiao;
    private NhanVien nhanVienLap;
    private NhanVien quanLyDuyet;
    private String trangThai; // Chờ duyệt, Đã duyệt, Từ chối
    private LocalDateTime ngayDuyet;
    private String lyDoTuChoi;

    public PhieuNhapHang() {}

    public PhieuNhapHang(String maPhieuNhap) {
        this.maPhieuNhap = maPhieuNhap;
    }

    public PhieuNhapHang(
            String maPhieuNhap,
            LocalDateTime ngayLap,
            String soChungTu,
            String nhaCungCap,
            String nguoiGiao,
            NhanVien nhanVienLap,
            NhanVien quanLyDuyet,
            String trangThai,
            LocalDateTime ngayDuyet,
            String lyDoTuChoi) {
        this.maPhieuNhap = maPhieuNhap;
        this.ngayLap = ngayLap;
        this.soChungTu = soChungTu;
        this.nhaCungCap = nhaCungCap;
        this.nguoiGiao = nguoiGiao;
        this.nhanVienLap = nhanVienLap;
        this.quanLyDuyet = quanLyDuyet;
        this.trangThai = trangThai;
        this.ngayDuyet = ngayDuyet;
        this.lyDoTuChoi = lyDoTuChoi;
    }

    public String getMaPhieuNhap() {
        return maPhieuNhap;
    }

    public void setMaPhieuNhap(String maPhieuNhap) {
        this.maPhieuNhap = maPhieuNhap;
    }

    public LocalDateTime getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDateTime ngayLap) {
        this.ngayLap = ngayLap;
    }

    public String getSoChungTu() {
        return soChungTu;
    }

    public void setSoChungTu(String soChungTu) {
        this.soChungTu = soChungTu;
    }

    public String getNhaCungCap() {
        return nhaCungCap;
    }

    public void setNhaCungCap(String nhaCungCap) {
        this.nhaCungCap = nhaCungCap;
    }

    public String getNguoiGiao() {
        return nguoiGiao;
    }

    public void setNguoiGiao(String nguoiGiao) {
        this.nguoiGiao = nguoiGiao;
    }

    public NhanVien getNhanVienLap() {
        return nhanVienLap;
    }

    public void setNhanVienLap(NhanVien nhanVienLap) {
        this.nhanVienLap = nhanVienLap;
    }

    public NhanVien getQuanLyDuyet() {
        return quanLyDuyet;
    }

    public void setQuanLyDuyet(NhanVien quanLyDuyet) {
        this.quanLyDuyet = quanLyDuyet;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getNgayDuyet() {
        return ngayDuyet;
    }

    public void setNgayDuyet(LocalDateTime ngayDuyet) {
        this.ngayDuyet = ngayDuyet;
    }

    public String getLyDoTuChoi() {
        return lyDoTuChoi;
    }

    public void setLyDoTuChoi(String lyDoTuChoi) {
        this.lyDoTuChoi = lyDoTuChoi;
    }

    @Override
    public String toString() {
        return String.valueOf(maPhieuNhap);
    }
}
