package entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class PhanCongCa {
    private String maPhanCong;
    private NhanVien nhanVien;
    private LocalDate ngay;
    private String ghiChu;
    private CaLamViec caLamViec;
    private BigDecimal tienQuyDauCa;
    private BigDecimal tienMatThucDem;
    private String trangThai;
    private LocalDateTime gioMoCa;
    private LocalDateTime gioDongCa;

    public PhanCongCa() {
        this.tienQuyDauCa = BigDecimal.ZERO;
        this.tienMatThucDem = BigDecimal.ZERO;
        this.trangThai = "Chưa mở";
    }
    public PhanCongCa(String maPhanCong) {
        this.maPhanCong = maPhanCong;
    }
    public PhanCongCa(String maPhanCong, NhanVien nhanVien, LocalDate ngay, String ghiChu, CaLamViec caLamViec,
			BigDecimal tienQuyDauCa, BigDecimal tienMatThucDem, String trangThai, LocalDateTime gioMoCa,
			LocalDateTime gioDongCa) {
		this.maPhanCong = maPhanCong;
		this.nhanVien = nhanVien;
		this.ngay = ngay != null ? ngay : LocalDate.now();
		this.ghiChu = ghiChu;
		this.caLamViec = caLamViec;
		this.tienQuyDauCa = tienQuyDauCa;
		this.tienMatThucDem = tienMatThucDem;
		this.trangThai = trangThai != null ? trangThai : "Chưa mở";
		this.gioMoCa = gioMoCa;
		this.gioDongCa = gioDongCa;
	}

    public PhanCongCa(String maPhanCong) {
        this.maPhanCong = maPhanCong;
    }

    public PhanCongCa(
            String maPhanCong,
            NhanVien nhanVien,
            LocalDate ngay,
            String ghiChu,
            CaLamViec caLamViec,
            double tienQuyDauCa,
            double tienMatThucDem,
            String trangThai,
            LocalDateTime gioMoCa,
            LocalDateTime gioDongCa) {
        this.maPhanCong = maPhanCong;
        this.nhanVien = nhanVien;
        this.ngay = ngay;
        this.ghiChu = ghiChu;
        this.caLamViec = caLamViec;
        this.tienQuyDauCa = tienQuyDauCa;
        this.tienMatThucDem = tienMatThucDem;
        this.trangThai = trangThai;
        this.gioMoCa = gioMoCa;
        this.gioDongCa = gioDongCa;
    }

    public String getMaPhanCong() {
        return maPhanCong;
    }

    public void setMaPhanCong(String maPhanCong) {
        this.maPhanCong = maPhanCong;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public LocalDate getNgay() {
        return ngay;
    }

    public void setNgay(LocalDate ngay) {
        this.ngay = ngay;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public CaLamViec getCaLamViec() {
        return caLamViec;
    }

    public void setCaLamViec(CaLamViec caLamViec) {
        this.caLamViec = caLamViec;
    }

    public double getTienQuyDauCa() {
        return tienQuyDauCa;
    }

    public void setTienQuyDauCa(double tienQuyDauCa) {
        this.tienQuyDauCa = tienQuyDauCa;
    }

    public double getTienMatThucDem() {
        return tienMatThucDem;
    }

    public void setTienMatThucDem(double tienMatThucDem) {
        this.tienMatThucDem = tienMatThucDem;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
    
    public LocalDateTime getGioMoCa() {
        return gioMoCa;
    }
    public void setGioMoCa(LocalDateTime gioMoCa) {
        this.gioMoCa = gioMoCa;
    }
    
    public LocalDateTime getGioDongCa() {
        return gioDongCa;
    }
    public void setGioDongCa(LocalDateTime gioDongCa) {
        this.gioDongCa = gioDongCa;
    }

    public LocalDateTime getGioMoCa() {
        return gioMoCa;
    }

    public void setGioMoCa(LocalDateTime gioMoCa) {
        this.gioMoCa = gioMoCa;
    }

    public LocalDateTime getGioDongCa() {
        return gioDongCa;
    }

    public void setGioDongCa(LocalDateTime gioDongCa) {
        this.gioDongCa = gioDongCa;
    }

    @Override
	public int hashCode() {
		return Objects.hash(maPhanCong);
	}
    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PhanCongCa other = (PhanCongCa) obj;
		return Objects.equals(maPhanCong, other.maPhanCong);
	}
    
	@Override
    public String toString() {
        return String.valueOf(maPhanCong);
    }
}
