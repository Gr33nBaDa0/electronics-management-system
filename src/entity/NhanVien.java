package entity;

import java.util.Objects;
import java.time.LocalDate;

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
    
    public NhanVien() {
    }

    public NhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public NhanVien(String maNhanVien, String hoTen, String vaiTro, String sdt, String email, String diaChi, String soCCCD, byte[] hinhTheNV, String gioiTinh, LocalDate ngaySinh, LocalDate ngayBatDauLV) {
        this.maNhanVien = maNhanVien;
        this.hoTen = hoTen;
        this.vaiTro = vaiTro;
        this.sdt = sdt;
        this.email = email;
        this.diaChi = diaChi;
        this.soCCCD = soCCCD;
        this.hinhTheNV = hinhTheNV == null ? null : hinhTheNV.clone();
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.ngayBatDauLV = ngayBatDauLV;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getSoCCCD() {
        return soCCCD;
    }

    public void setSoCCCD(String soCCCD) {
        this.soCCCD = soCCCD;
    }

    public byte[] getHinhTheNV() {
        return hinhTheNV == null ? null : hinhTheNV.clone();
    }

    public void setHinhTheNV(byte[] hinhTheNV) {
        this.hinhTheNV = hinhTheNV == null ? null : hinhTheNV.clone();
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public LocalDate getNgayBatDauLV() {
        return ngayBatDauLV;
    }

    public void setNgayBatDauLV(LocalDate ngayBatDauLV) {
        this.ngayBatDauLV = ngayBatDauLV;
    }

    @Override

    public String toString() {
        return hoTen == null ? String.valueOf(maNhanVien) : hoTen + " (" + maNhanVien + ")";
    }

    @Override

    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof NhanVien)) return false;
        NhanVien item = (NhanVien) other;
        return maNhanVien != null && maNhanVien.equals(item.maNhanVien);
    }

    @Override

    public int hashCode() {
        return maNhanVien == null ? System.identityHashCode(this) : Objects.hash(maNhanVien);
    }
}

