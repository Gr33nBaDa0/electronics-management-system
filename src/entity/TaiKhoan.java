package entity;

import java.util.Objects;

public class TaiKhoan {
    private String tenDangNhap;
    private String quyen;
    private String matKhau;
    private NhanVien nhanVien;

    public TaiKhoan() {}
    public TaiKhoan(String tenDangNhap) {
		this.tenDangNhap = tenDangNhap;
	}
	public TaiKhoan(String tenDangNhap, String quyen, String matKhau, NhanVien nhanVien) {
		this.tenDangNhap = tenDangNhap;
        this.quyen = quyen;
        this.matKhau = matKhau;
        this.nhanVien = nhanVien;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }
    public String getQuyen() {
        return quyen;
    }
    public String getMatKhau() {
        return matKhau;
    }
    public NhanVien getNhanVien() {
        return nhanVien;
    }
    
    public void setTenDangNhap(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập không hợp lệ (Không được để trống)");
        }
        this.tenDangNhap = tenDangNhap.trim();
    }
    public void setQuyen(String quyen) {
        if (quyen == null || (!quyen.equalsIgnoreCase("ADMIN") && !quyen.equalsIgnoreCase("BAN_HANG"))) {
            throw new IllegalArgumentException("Quyền không hợp lệ (Chỉ nhận 'ADMIN' hoặc 'BAN_HANG')");
        }
        this.quyen = quyen.toUpperCase();
    }
    public void setMatKhau(String matKhau) {
        if (matKhau == null || matKhau.trim().length() < 8) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 8 ký tự");
        }
        this.matKhau = matKhau;
    }
    public void setNhanVien(NhanVien nhanVien) {
        if (nhanVien == null) {
            throw new IllegalArgumentException("Nhân viên không được rỗng");
        }
        this.nhanVien = nhanVien;
    }

    public boolean kiemTraDangNhap(String mk) {
        return this.matKhau != null && this.matKhau.equals(mk);
    }

    @Override
	public int hashCode() {
		return Objects.hash(tenDangNhap);
	}
    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TaiKhoan other = (TaiKhoan) obj;
		return Objects.equals(tenDangNhap, other.tenDangNhap);
	}
    
	@Override
    public String toString() {
        return "TaiKhoan [tenDangNhap=" + tenDangNhap + ", quyen=" + quyen + ", nhanVien=" + (nhanVien != null ? nhanVien.getHoTen() : "") + "]";
    }
}