package entity;

import java.time.LocalDateTime;

public class PhieuDoiTra {
    private String maPhieuDoiTra;
    private HoaDon hoaDonGoc;
    private NhanVien nhanVienLap;
    private NhanVien quanLyDuyet;
    private PhanCongCa phanCong;
    private LocalDateTime ngayLap;
    private String lyDo;
    private String trangThai;
    private LocalDateTime ngayDuyet;
    private String lyDoTuChoi;
    private double tongTienHoan;
    private int diemHoanLai;
    private int diemThuHoi;

    public PhieuDoiTra() {}

    public String getMaPhieuDoiTra() { return maPhieuDoiTra; }
    public void setMaPhieuDoiTra(String maPhieuDoiTra) {
        if (maPhieuDoiTra == null || !maPhieuDoiTra.matches("^DT\\d{3,}$")) {
            throw new IllegalArgumentException("Mã phiếu đổi trả không hợp lệ (VD: DT001)");
        }
        this.maPhieuDoiTra = maPhieuDoiTra;
    }

    public HoaDon getHoaDonGoc() { return hoaDonGoc; }
    public void setHoaDonGoc(HoaDon hoaDonGoc) {
        if (hoaDonGoc == null) throw new IllegalArgumentException("Hóa đơn gốc không được rỗng");
        this.hoaDonGoc = hoaDonGoc;
    }

    public NhanVien getNhanVienLap() { return nhanVienLap; }
    public void setNhanVienLap(NhanVien nhanVienLap) {
        if (nhanVienLap == null) throw new IllegalArgumentException("Nhân viên lập không được rỗng");
        this.nhanVienLap = nhanVienLap;
    }

    public NhanVien getQuanLyDuyet() { return quanLyDuyet; }
    public void setQuanLyDuyet(NhanVien quanLyDuyet) { this.quanLyDuyet = quanLyDuyet; }

    public PhanCongCa getPhanCong() { return phanCong; }
    public void setPhanCong(PhanCongCa phanCong) {
        if (phanCong == null) throw new IllegalArgumentException("Phân công ca không được rỗng");
        this.phanCong = phanCong;
    }

    public LocalDateTime getNgayLap() { return ngayLap; }
    public void setNgayLap(LocalDateTime ngayLap) {
        if (ngayLap == null) throw new IllegalArgumentException("Ngày lập không được rỗng");
        this.ngayLap = ngayLap;
    }

    public String getLyDo() { return lyDo; }
    public void setLyDo(String lyDo) {
        if (lyDo == null || lyDo.trim().isEmpty()) throw new IllegalArgumentException("Lý do không được rỗng");
        this.lyDo = lyDo;
    }

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

    public double getTongTienHoan() { return tongTienHoan; }
    public void setTongTienHoan(double tongTienHoan) {
        if (tongTienHoan < 0) throw new IllegalArgumentException("Tổng tiền hoàn không được âm");
        this.tongTienHoan = tongTienHoan;
    }

    public int getDiemHoanLai() { return diemHoanLai; }
    public void setDiemHoanLai(int diemHoanLai) {
        if (diemHoanLai < 0) throw new IllegalArgumentException("Điểm hoàn lại không được âm");
        this.diemHoanLai = diemHoanLai;
    }

    public int getDiemThuHoi() { return diemThuHoi; }
    public void setDiemThuHoi(int diemThuHoi) {
        if (diemThuHoi < 0) throw new IllegalArgumentException("Điểm thu hồi không được âm");
        this.diemThuHoi = diemThuHoi;
    }

    @Override
    public String toString() {
        return maPhieuDoiTra + " - " + trangThai;
    }
}