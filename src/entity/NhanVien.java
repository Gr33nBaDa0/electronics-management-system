package entity;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

public class NhanVien {
    private String maNhanVien;
    private String hoTen;
    private String vaiTro;
    private String sdt;
    private String email;
    private String diaChi;
    private String soCCCD;
    private byte[] hinhTheNV;
    private String gioiTinh;
    private LocalDate ngaySinh;
    private LocalDate ngayBatDauLV;

    public NhanVien() {}
    public NhanVien(String maNhanVien) {
		this.maNhanVien = maNhanVien;
	}
    public NhanVien(String maNhanVien, String hoTen, String vaiTro, String sdt, String email, String diaChi,
			String soCCCD, byte[] hinhTheNV, String gioiTinh, LocalDate ngaySinh, LocalDate ngayBatDauLV) {
		this.maNhanVien = maNhanVien;
		this.hoTen = hoTen;
		this.vaiTro = vaiTro;
		this.sdt = sdt;
		this.email = email;
		this.diaChi = diaChi;
		this.soCCCD = soCCCD;
		this.hinhTheNV = hinhTheNV;
		this.gioiTinh = gioiTinh;
		this.ngaySinh = ngaySinh;
		this.ngayBatDauLV = ngayBatDauLV;
	}
    
	public String getMaNhanVien() {
        return maNhanVien;
    }
    public String getHoTen() {
        return hoTen;
    }
    public String getVaiTro() {
        return vaiTro;
    }
    public String getSdt() {
        return sdt;
    }
    public String getEmail() {
        return email;
    }
    public String getDiaChi() {
        return diaChi;
    }
    public String getSoCCCD() {
        return soCCCD;
    }
    public byte[] getHinhTheNV() {
        return hinhTheNV;
    }
    public LocalDate getNgaySinh() {
        return ngaySinh;
    }
    public String getGioiTinh() {
        return gioiTinh;
    }
    public LocalDate getNgayBatDauLV() {
        return ngayBatDauLV;
    }
    
    public void setMaNhanVien(String maNhanVien) {
        if (maNhanVien == null || !maNhanVien.trim().matches("^(NV|QL)\\d{3,}$")) {
            throw new IllegalArgumentException("Mã nhân viên không hợp lệ (Phải bắt đầu bằng NV hoặc QL theo sau là ít nhất 3 chữ số)");
        }
        this.maNhanVien = maNhanVien.trim();
    }
    public void setHoTen(String hoTen) {
        if (hoTen == null || hoTen.trim().isEmpty() || hoTen.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Họ tên không hợp lệ (Không được rỗng và không chứa chữ số)");
        }
        this.hoTen = hoTen.trim();
    }
    public void setVaiTro(String vaiTro) {
        if (vaiTro == null || (!vaiTro.equalsIgnoreCase("Bán hàng") && !vaiTro.equalsIgnoreCase("Quản lý"))) {
            throw new IllegalArgumentException("Vai trò không hợp lệ (Chỉ nhận 'Bán hàng' hoặc 'Quản lý')");
        }
        this.vaiTro = vaiTro;
    }
    public void setSdt(String sdt) {
        if (sdt == null || !sdt.trim().matches("^(03|05|07|08|09)\\d{8}$")) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ (Phải gồm 10 chữ số, đầu số 03, 05, 07, 08, 09)");
        }
        this.sdt = sdt.trim();
    }
    public void setEmail(String email) {
        if (email != null && !email.trim().isEmpty()) {
            if (!email.trim().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
                throw new IllegalArgumentException("Email không hợp lệ");
            }
            this.email = email.trim();
        } else {
            this.email = null;
        }
    }
    public void setDiaChi(String diaChi) {
        this.diaChi = (diaChi == null || diaChi.trim().isEmpty()) ? null : diaChi.trim();
    }
    public void setSoCCCD(String soCCCD) {
        if (soCCCD == null || !soCCCD.trim().matches("^\\d{12}$")) {
            throw new IllegalArgumentException("Số CCCD không hợp lệ (Chuỗi đúng 12 chữ số)");
        }
        this.soCCCD = soCCCD.trim();
    }
    public void setHinhTheNV(byte[] hinhTheNV) {
        this.hinhTheNV = hinhTheNV;
    }
    public void setGioiTinh(String gioiTinh) {
        if (gioiTinh == null || (!gioiTinh.equalsIgnoreCase("Nam") && !gioiTinh.equalsIgnoreCase("Nữ") && !gioiTinh.equalsIgnoreCase("Khác"))) {
            throw new IllegalArgumentException("Giới tính không hợp lệ (Nam, Nữ hoặc Khác)");
        }
        this.gioiTinh = gioiTinh;
    }
    public void setNgaySinh(LocalDate ngaySinh) {
        if (ngaySinh == null) {
            this.ngaySinh = null;
            return;
        }
        int tuoi = Period.between(ngaySinh, LocalDate.now()).getYears();
        if (tuoi < 18 || tuoi > 65) {
            throw new IllegalArgumentException("Tuổi không hợp lệ (Nhân viên phải từ 18 đến 65 tuổi)");
        }
        this.ngaySinh = ngaySinh;
    }
    public void setNgayBatDauLV(LocalDate ngayBatDauLV) {
        if (ngayBatDauLV == null || ngayBatDauLV.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày bắt đầu làm việc không hợp lệ (Không null và phải nhỏ hơn hoặc bằng ngày hiện tại)");
        }
        this.ngayBatDauLV = ngayBatDauLV;
    }

    @Override
	public int hashCode() {
		return Objects.hash(maNhanVien);
	}
    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NhanVien other = (NhanVien) obj;
		return Objects.equals(maNhanVien, other.maNhanVien);
	}
    
	@Override
    public String toString() {
        return "NhanVien [maNhanVien=" + maNhanVien + ", hoTen=" + hoTen + ", vaiTro=" + vaiTro + ", sdt=" + sdt
                + ", email=" + email + ", soCCCD=" + soCCCD + ", gioiTinh=" + gioiTinh + ", ngaySinh=" + ngaySinh
                + ", ngayBatDauLV=" + ngayBatDauLV + "]";
    }
}
