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
        if (nhanVien == null) {
            throw new IllegalArgumentException("Nhân viên không được rỗng");
        }
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
        if (caLamViec == null) {
            throw new IllegalArgumentException("Ca làm việc không được rỗng");
        }
        this.caLamViec = caLamViec;
    }

    public BigDecimal getTienQuyDauCa() {
        return tienQuyDauCa != null ? tienQuyDauCa : BigDecimal.ZERO;
    }
    public void setTienQuyDauCa(BigDecimal tienQuyDauCa) {
        if (tienQuyDauCa != null && tienQuyDauCa.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Tiền quỹ đầu ca phải lớn hơn hoặc bằng 0");
        }
        this.tienQuyDauCa = tienQuyDauCa != null ? tienQuyDauCa : BigDecimal.ZERO;
    }

    public void setTienQuyDauCa(double tien) {
        setTienQuyDauCa(BigDecimal.valueOf(tien));
    }
    public BigDecimal getTienMatThucDem() {
        return tienMatThucDem != null ? tienMatThucDem : BigDecimal.ZERO;
    }

    public void setTienMatThucDem(BigDecimal tienMatThucDem) {
        if (tienMatThucDem != null && tienMatThucDem.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Tiền mặt thực đếm phải lớn hơn hoặc bằng 0");
        }
        this.tienMatThucDem = tienMatThucDem != null ? tienMatThucDem : BigDecimal.ZERO;
    }
    public void setTienMatThucDem(double tien) {
        setTienMatThucDem(BigDecimal.valueOf(tien));
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

    public boolean xacNhanVaoCa() {
        if ("Chưa mở".equalsIgnoreCase(this.trangThai) || this.trangThai == null) {
            this.trangThai = "Đang mở";
            this.gioMoCa = LocalDateTime.now();
            return true;
        }
        return false;
    }

    public boolean xacNhanDongCa() {
        if ("Đang mở".equalsIgnoreCase(this.trangThai)) {
            this.trangThai = "Đã đóng";
            this.gioDongCa = LocalDateTime.now();
            return true;
        }
        return false;
    }

    public double tinhTienMatLyThuyet(double tongThu, double tongChi) {
        double dauCa = getTienQuyDauCa().doubleValue();
        return dauCa + tongThu - tongChi;
    }

    public BigDecimal tinhTienMatLyThuyet(BigDecimal tongThu, BigDecimal tongChi) {
        BigDecimal thu = tongThu != null ? tongThu : BigDecimal.ZERO;
        BigDecimal chi = tongChi != null ? tongChi : BigDecimal.ZERO;
        return getTienQuyDauCa().add(thu).subtract(chi);
    }

    public double tinhChenhLech(double tongThu, double tongChi) {
        double thucDem = getTienMatThucDem().doubleValue();
        return thucDem - tinhTienMatLyThuyet(tongThu, tongChi);
    }

    public BigDecimal tinhChenhLech(BigDecimal tongThu, BigDecimal tongChi) {
        return getTienMatThucDem().subtract(tinhTienMatLyThuyet(tongThu, tongChi));
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
        return "PhanCongCa [maPhanCong=" + maPhanCong + ", nhanVien=" + (nhanVien != null ? nhanVien.getHoTen() : "")
                + ", ngay=" + ngay + ", ca=" + (caLamViec != null ? caLamViec.getTenCa() : "")
                + ", trangThai=" + trangThai + "]";
    }
}