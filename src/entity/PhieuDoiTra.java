package entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class PhieuDoiTra {
    private String maPhieuDoiTra;
    private HoaDon hoaDonGoc;
    private NhanVien nhanVienLap;
    private NhanVien quanLyDuyet;
    private PhanCongCa phanCongHoanTien;
    private LocalDateTime ngayLap;
    private String lyDo;
    private String trangThai; 
    private LocalDateTime ngayDuyet;
    private String lyDoTuChoi;
    private int diemHoanLai;
    private int diemThuHoi;

    public PhieuDoiTra() {}

    public PhieuDoiTra(String maPhieuDoiTra) {
        this.maPhieuDoiTra = maPhieuDoiTra;
    }
    public PhieuDoiTra(String maPhieuDoiTra, HoaDon hoaDonGoc, NhanVien nhanVienLap, NhanVien quanLyDuyet,
			PhanCongCa phanCongHoanTien, LocalDateTime ngayLap, String lyDo, String trangThai, LocalDateTime ngayDuyet,
			String lyDoTuChoi, int diemHoanLai, int diemThuHoi) {
		this.maPhieuDoiTra = maPhieuDoiTra;
		this.hoaDonGoc = hoaDonGoc;
		this.nhanVienLap = nhanVienLap;
		this.quanLyDuyet = quanLyDuyet;
		this.phanCongHoanTien = phanCongHoanTien;
		this.ngayLap = ngayLap != null ? ngayLap : LocalDateTime.now();
		this.lyDo = lyDo;
		this.trangThai = trangThai;
		this.ngayDuyet = ngayDuyet;
		this.lyDoTuChoi = lyDoTuChoi;
		this.diemHoanLai = Math.max(0, diemHoanLai);
        this.diemThuHoi = Math.max(0, diemThuHoi);
	}
    
    public String getMaPhieuDoiTra() {
        return maPhieuDoiTra;
    }
    public void setMaPhieuDoiTra(String maPhieuDoiTra) {
        this.maPhieuDoiTra = maPhieuDoiTra != null ? maPhieuDoiTra.trim() : null;
    }

    public PhieuDoiTra(
            String maPhieuDoiTra,
            HoaDon hoaDonGoc,
            NhanVien nhanVienLap,
            NhanVien quanLyDuyet,
            PhanCongCa phanCongHoanTien,
            LocalDateTime ngayLap,
            String lyDo,
            String trangThai,
            LocalDateTime ngayDuyet,
            String lyDoTuChoi,
            int diemHoanLai,
            int diemThuHoi) {
        this.maPhieuDoiTra = maPhieuDoiTra;
        this.hoaDonGoc = hoaDonGoc;
        this.nhanVienLap = nhanVienLap;
        this.quanLyDuyet = quanLyDuyet;
        this.phanCongHoanTien = phanCongHoanTien;
        this.ngayLap = ngayLap;
        this.lyDo = lyDo;
        this.trangThai = trangThai;
        this.ngayDuyet = ngayDuyet;
        this.lyDoTuChoi = lyDoTuChoi;
        this.diemHoanLai = diemHoanLai;
        this.diemThuHoi = diemThuHoi;
    }

    public String getMaPhieuDoiTra() {
        return maPhieuDoiTra;
    }

    public void setMaPhieuDoiTra(String maPhieuDoiTra) {
        this.maPhieuDoiTra = maPhieuDoiTra;
    }

    public HoaDon getHoaDonGoc() {
        return hoaDonGoc;
    }

    public void setHoaDonGoc(HoaDon hoaDonGoc) {
        this.hoaDonGoc = hoaDonGoc;
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

    public PhanCongCa getPhanCongHoanTien() {
        return phanCongHoanTien;
    }

    public void setPhanCongHoanTien(PhanCongCa phanCongHoanTien) {
        this.phanCongHoanTien = phanCongHoanTien;
    }

    public LocalDateTime getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDateTime ngayLap) {
        this.ngayLap = ngayLap;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
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

    public int getDiemHoanLai() {
        return diemHoanLai;
    }

    public void setDiemHoanLai(int diemHoanLai) {
        this.diemHoanLai = diemHoanLai;
    }

    public int getDiemThuHoi() {
        return diemThuHoi;
    }

    public void setDiemThuHoi(int diemThuHoi) {
        this.diemThuHoi = diemThuHoi;
    }
    public void setDiemThuHoi(int diemThuHoi) {
        this.diemThuHoi = Math.max(0, diemThuHoi);
    }

    public double tinhTongTienHoan(List<ChiTietDoiTra> ds) {
        if (ds == null || ds.isEmpty()) return 0.0;
        double tong = 0.0;
        for (ChiTietDoiTra ct : ds) {
            tong += ct.tinhTienHoan();
        }
        return tong;
    }

    public BigDecimal tinhTongTienHoanBigDecimal(List<ChiTietDoiTra> ds) {
        if (ds == null || ds.isEmpty()) return BigDecimal.ZERO;
        BigDecimal tong = BigDecimal.ZERO;
        for (ChiTietDoiTra ct : ds) {
            tong = tong.add(ct.getTienHoan() != null ? ct.getTienHoan() : BigDecimal.ZERO);
        }
        return tong;
    }

    
    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PhieuDoiTra other = (PhieuDoiTra) obj;
		return Objects.equals(maPhieuDoiTra, other.maPhieuDoiTra);
	}
	@Override
	public int hashCode() {
		return Objects.hash(maPhieuDoiTra);
	}

    @Override
    public String toString() {
        return String.valueOf(maPhieuDoiTra);
    }
}
