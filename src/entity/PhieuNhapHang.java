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

    public PhieuNhapHang() {
        this.trangThai = "Chờ duyệt";
        this.ngayLap = LocalDateTime.now();
    }

    public PhieuNhapHang(String maPhieuNhap) {
        this();
        setMaPhieuNhap(maPhieuNhap);
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
        setMaPhieuNhap(maPhieuNhap);
        this.ngayLap = ngayLap != null ? ngayLap : LocalDateTime.now();
        this.soChungTu = soChungTu;
        setNhaCungCap(nhaCungCap);
        setNguoiGiao(nguoiGiao);
        setNhanVienLap(nhanVienLap);
        this.quanLyDuyet = quanLyDuyet;
        this.trangThai = trangThai != null ? trangThai : "Chờ duyệt";
        this.ngayDuyet = ngayDuyet;
        this.lyDoTuChoi = lyDoTuChoi;
    }

    public String getMaPhieuNhap() {
        return maPhieuNhap;
    }

    public void setMaPhieuNhap(String maPhieuNhap) {
        this.maPhieuNhap = maPhieuNhap != null ? maPhieuNhap.trim() : null;
    }

    // Alias cho đặc tả Bảng 4.8 (maPhieuNhan)
    public String getMaPhieuNhan() {
        return getMaPhieuNhap();
    }

    public void setMaPhieuNhan(String maPhieuNhan) {
        setMaPhieuNhap(maPhieuNhan);
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
        if (nhaCungCap == null || nhaCungCap.trim().isEmpty()) {
            throw new IllegalArgumentException("Nhà cung cấp không được rỗng");
        }
        this.nhaCungCap = nhaCungCap.trim();
    }

    public String getNguoiGiao() {
        return nguoiGiao;
    }

    public void setNguoiGiao(String nguoiGiao) {
        if (nguoiGiao == null || nguoiGiao.trim().isEmpty()) {
            throw new IllegalArgumentException("Người giao không được rỗng");
        }
        this.nguoiGiao = nguoiGiao.trim();
    }

    public NhanVien getNhanVienLap() {
        return nhanVienLap;
    }

    public void setNhanVienLap(NhanVien nhanVienLap) {
        if (nhanVienLap == null) {
            throw new IllegalArgumentException("Nhân viên lập không được rỗng");
        }
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

    /**
     * Tính tổng tiền phiếu nhập hàng = ∑ thanhTien các chi tiết
     */
    public double tinhTongTien(List<ChiTietPhieuNhapHang> ds) {
        if (ds == null || ds.isEmpty()) return 0.0;
        double tong = 0.0;
        for (ChiTietPhieuNhapHang ct : ds) {
            tong += ct.tinhThanhTienChiTiet();
        }
        return tong;
    }

    public BigDecimal tinhTongTienBigDecimal(List<ChiTietPhieuNhapHang> ds) {
        if (ds == null || ds.isEmpty()) return BigDecimal.ZERO;
        BigDecimal tong = BigDecimal.ZERO;
        for (ChiTietPhieuNhapHang ct : ds) {
            tong = tong.add(ct.getThanhTien());
        }
        return tong;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhieuNhapHang)) return false;
        PhieuNhapHang that = (PhieuNhapHang) o;
        return Objects.equals(maPhieuNhap, that.maPhieuNhap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPhieuNhap);
    }

    @Override
    public String toString() {
        return "PhieuNhapHang [ma=" + maPhieuNhap + ", ngayLap=" + ngayLap + ", ncc=" + nhaCungCap
                + ", nguoiGiao=" + nguoiGiao + ", trangThai=" + trangThai + "]";
    }
}
