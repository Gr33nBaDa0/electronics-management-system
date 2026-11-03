package entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class HoaDon {
    private String maHoaDon;
    private KhachHang khachHang;
    private NhanVien nhanVien;
    private LocalDateTime ngayLap;
    private String hinhThucThanhToan;
    private String trangThaiThanhToan;
    private BigDecimal thueSuatVAT;
    private PhanCongCa phanCongCa;
    private int diemSuDung;

    public HoaDon() {
        this.ngayLap = LocalDateTime.now();
        this.trangThaiThanhToan = "Chưa thanh toán";
        this.thueSuatVAT = new BigDecimal("0.1");
        this.diemSuDung = 0;
    }
    public HoaDon(String maHoaDon) {
		this.maHoaDon = maHoaDon;
	}
    
    
	public HoaDon(String maHoaDon, KhachHang khachHang, NhanVien nhanVien, LocalDateTime ngayLap,
			String hinhThucThanhToan, String trangThaiThanhToan, BigDecimal thueSuatVAT, PhanCongCa phanCongCa,
			int diemSuDung) {
		super();
		this.maHoaDon = maHoaDon;
		this.khachHang = khachHang;
		this.nhanVien = nhanVien;
		this.ngayLap = ngayLap != null ? ngayLap : LocalDateTime.now();
		this.hinhThucThanhToan = hinhThucThanhToan;
		this.trangThaiThanhToan = trangThaiThanhToan != null ? trangThaiThanhToan : "Chưa thanh toán";
		this.thueSuatVAT = thueSuatVAT;
		this.phanCongCa = phanCongCa;
		this.diemSuDung = Math.max(0, diemSuDung);
	}

    public String getMaHoaDon() {
        return maHoaDon;
    }
    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon != null ? maHoaDon.trim() : null;
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
        if (nhanVien == null) {
            throw new IllegalArgumentException("Nhân viên không được rỗng");
        }
        this.nhanVien = nhanVien;
    }

    public LocalDateTime getNgayLap() {
        return ngayLap;
    }
    public void setNgayLap(LocalDateTime ngayLap) {
        if (ngayLap != null && ngayLap.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Ngày lập không hợp lệ (Phải nhỏ hơn hoặc bằng ngày giờ hiện tại)");
        }
        this.ngayLap = ngayLap;
    }

    public String getHinhThucThanhToan() {
        return hinhThucThanhToan;
    }
    public void setHinhThucThanhToan(String hinhThucThanhToan) {
        if (hinhThucThanhToan != null && !hinhThucThanhToan.trim().isEmpty()) {
            if (!hinhThucThanhToan.equalsIgnoreCase("Tiền mặt") && !hinhThucThanhToan.equalsIgnoreCase("Chuyển khoản QR")) {
                throw new IllegalArgumentException("Hình thức thanh toán không hợp lệ (Tiền mặt hoặc Chuyển khoản QR)");
            }
        }
        this.hinhThucThanhToan = hinhThucThanhToan;
    }

    public String getTrangThaiThanhToan() {
        return trangThaiThanhToan;
    }
    public void setTrangThaiThanhToan(String trangThaiThanhToan) {
        if (trangThaiThanhToan != null && !trangThaiThanhToan.trim().isEmpty()) {
            if (!trangThaiThanhToan.equalsIgnoreCase("Chưa thanh toán")
                    && !trangThaiThanhToan.equalsIgnoreCase("Đã thanh toán")
                    && !trangThaiThanhToan.equalsIgnoreCase("Đã hủy")) {
                throw new IllegalArgumentException("Trạng thái thanh toán không hợp lệ (Chưa thanh toán, Đã thanh toán, Đã hủy)");
            }
        }
        this.trangThaiThanhToan = trangThaiThanhToan;
    }

    public BigDecimal getThueSuatVAT() {
        return thueSuatVAT != null ? thueSuatVAT : new BigDecimal("0.1");
    }
    public void setThueSuatVAT(BigDecimal thueSuatVAT) {
        if (thueSuatVAT != null) {
            if (thueSuatVAT.compareTo(BigDecimal.ZERO) < 0 || thueSuatVAT.compareTo(BigDecimal.ONE) >= 0) {
                throw new IllegalArgumentException("Thuế suất VAT không hợp lệ (0 <= thue < 1)");
            }
            this.thueSuatVAT = thueSuatVAT;
        } else {
            this.thueSuatVAT = new BigDecimal("0.1");
        }
    }
    public void setThueSuatVAT(double thue) {
        setThueSuatVAT(BigDecimal.valueOf(thue));
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
        this.diemSuDung = Math.max(0, diemSuDung);
    }

    public double tinhTongTienHang(List<ChiTietHoaDon> ds) {
        if (ds == null || ds.isEmpty()) return 0.0;
        double sum = 0.0;
        for (ChiTietHoaDon ct : ds) {
            sum += ct.getDonGia().doubleValue() * ct.getSoLuong();
        }
        return sum;
    }

    public double tinhTongGiamGia(List<ChiTietHoaDon> ds) {
        if (ds == null || ds.isEmpty()) return this.diemSuDung * 1000.0;
        double sumGiam = 0.0;
        for (ChiTietHoaDon ct : ds) {
            sumGiam += ct.getGiamGia().doubleValue();
        }
        return sumGiam + (this.diemSuDung * 1000.0);
    }

    public double tinhTienThueVat(List<ChiTietHoaDon> ds) {
        double sauGiam = Math.max(0.0, tinhTongTienHang(ds) - tinhTongGiamGia(ds));
        return sauGiam * getThueSuatVAT().doubleValue();
    }

    public double tinhThanhTien(List<ChiTietHoaDon> ds) {
        double sauGiam = Math.max(0.0, tinhTongTienHang(ds) - tinhTongGiamGia(ds));
        return sauGiam + tinhTienThueVat(ds);
    }

    
    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HoaDon other = (HoaDon) obj;
		return Objects.equals(maHoaDon, other.maHoaDon);
	}
	@Override
	public int hashCode() {
		return Objects.hash(maHoaDon);
	}

    @Override
    public String toString() {
        return "HoaDon [maHoaDon=" + maHoaDon + ", ngayLap=" + ngayLap + ", nv="
                + (nhanVien != null ? nhanVien.getHoTen() : "")
                + ", kh=" + (khachHang != null ? khachHang.getHoTen() : "Vãng lai")
                + ", tt=" + trangThaiThanhToan + "]";
    }
}