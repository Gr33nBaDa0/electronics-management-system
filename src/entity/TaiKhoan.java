package entity;

import java.util.Objects;

public class TaiKhoan {
    private String tenDangNhap;
    private String quyen;
    private String matKhau;
    private NhanVien nhanVien;

    public TaiKhoan() {
    }

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

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getQuyen() {
        return quyen;
    }

    public void setQuyen(String quyen) {
        this.quyen = quyen;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    @Override

    public String toString() {
        return String.valueOf(tenDangNhap);
    }

    @Override

    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof TaiKhoan)) return false;
        TaiKhoan item = (TaiKhoan) other;
        return tenDangNhap != null && tenDangNhap.equals(item.tenDangNhap);
    }

    @Override

    public int hashCode() {
        return tenDangNhap == null ? System.identityHashCode(this) : Objects.hash(tenDangNhap);
    }
}

