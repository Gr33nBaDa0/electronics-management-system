package entity;

public class TaiKhoan {
    private String tenDangNhap;
    private String quyen;
    private String matKhau;
    private NhanVien nhanVien;

    public TaiKhoan() {}

    public TaiKhoan(String tenDangNhap, String quyen, String matKhau, NhanVien nhanVien) {
        this.setTenDangNhap(tenDangNhap);
        this.setQuyen(quyen);
        this.setMatKhau(matKhau);
        this.setNhanVien(nhanVien);
    }

    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập không được rỗng");
        }
        this.tenDangNhap = tenDangNhap;
    }

    public String getQuyen() { return quyen; }
    public void setQuyen(String quyen) {
        if (!"ADMIN".equals(quyen) && !"BAN_HANG".equals(quyen)) {
            throw new IllegalArgumentException("Quyền không hợp lệ (Chỉ nhận ADMIN hoặc BAN_HANG)");
        }
        this.quyen = quyen;
    }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) {
        if (matKhau == null || matKhau.length() < 8) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 8 ký tự");
        }
        this.matKhau = matKhau;
    }

    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien nhanVien) {
        if (nhanVien == null) throw new IllegalArgumentException("Nhân viên không được rỗng");
        this.nhanVien = nhanVien;
    }

    /** Kiểm tra mật khẩu có khớp không */
    public boolean kiemTraDangNhap(String matKhauNhap) {
        return this.matKhau != null && this.matKhau.equals(matKhauNhap);
    }

    @Override
    public String toString() {
        return tenDangNhap + " - " + quyen;
    }
}