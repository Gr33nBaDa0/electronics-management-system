package entity;

import java.time.LocalDate;
import java.time.Period;

public class NhanVien {
    private String maNhanVien;
    private String hoTen;
    private String vaiTro;
    private String sdt;
    private String email;
    private String diaChi;
    private String soCCCD;
    private byte[] hinhTheNV; // Ảnh thẻ
    private String gioiTinh;
    private LocalDate ngaySinh;
    private LocalDate ngayBatDauLV;

    // Constructor không tham số
    public NhanVien() {}

    // Constructor đầy đủ tham số (Dùng this theo slide OOP)
    public NhanVien(String maNhanVien, String hoTen, String vaiTro, String sdt, String email, String diaChi, String soCCCD, byte[] hinhTheNV, String gioiTinh, LocalDate ngaySinh, LocalDate ngayBatDauLV) {
        this.setMaNhanVien(maNhanVien);
        this.setHoTen(hoTen);
        this.setVaiTro(vaiTro);
        this.setSdt(sdt);
        this.setEmail(email);
        this.setDiaChi(diaChi);
        this.setSoCCCD(soCCCD);
        this.setHinhTheNV(hinhTheNV);
        this.setGioiTinh(gioiTinh);
        this.setNgaySinh(ngaySinh);
        this.setNgayBatDauLV(ngayBatDauLV);
    }

    // --- GETTERS ---
    public String getMaNhanVien() { return maNhanVien; }
    public String getHoTen() { return hoTen; }
    public String getVaiTro() { return vaiTro; }
    public String getSdt() { return sdt; }
    public String getEmail() { return email; }
    public String getDiaChi() { return diaChi; }
    public String getSoCCCD() { return soCCCD; }
    public byte[] getHinhTheNV() { return hinhTheNV; }
    public String getGioiTinh() { return gioiTinh; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public LocalDate getNgayBatDauLV() { return ngayBatDauLV; }

    // --- SETTERS (Áp dụng Regular Expression - Slide 07) ---
    
    public void setMaNhanVien(String maNhanVien) {
        // Phải bắt đầu bằng NV hoặc QL, theo sau là 3 chữ số
        if (maNhanVien == null || !maNhanVien.matches("^(NV|QL)\\d{3}$")) {
            throw new IllegalArgumentException("Mã nhân viên không hợp lệ (VD: NV001, QL001)");
        }
        this.maNhanVien = maNhanVien;
    }

    public void setHoTen(String hoTen) {
        // Không rỗng, không chứa chữ số (theo mục 1.2 báo cáo)
        if (hoTen == null || hoTen.trim().isEmpty() || hoTen.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Họ tên không hợp lệ (Không được rỗng và không chứa số)");
        }
        this.hoTen = hoTen;
    }

    public void setVaiTro(String vaiTro) {
        if (!"Bán hàng".equals(vaiTro) && !"Quản lý".equals(vaiTro)) {
            throw new IllegalArgumentException("Vai trò không hợp lệ (Chỉ nhận 'Bán hàng' hoặc 'Quản lý')");
        }
        this.vaiTro = vaiTro;
    }

    public void setSdt(String sdt) {
        // 10 chữ số, bắt đầu bằng 03, 05, 07, 08, 09 (Theo slide Regex và CSDL)
        if (sdt == null || !sdt.matches("^0[35789]\\d{8}$")) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ (10 số, đầu 03/05/07/08/09)");
        }
        this.sdt = sdt;
    }

    public void setEmail(String email) {
        // Đúng định dạng email hoặc null (Theo slide Regex)
        if (email != null && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Email không hợp lệ");
        }
        this.email = email;
    }

    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public void setSoCCCD(String soCCCD) {
        // Đúng 12 chữ số (Theo slide Regex)
        if (soCCCD == null || !soCCCD.matches("^\\d{12}$")) {
            throw new IllegalArgumentException("Số CCCD không hợp lệ (Phải bao gồm đúng 12 chữ số)");
        }
        this.soCCCD = soCCCD;
    }

    public void setHinhTheNV(byte[] hinhTheNV) { this.hinhTheNV = hinhTheNV; }

    public void setGioiTinh(String gioiTinh) {
        if (!"Nam".equals(gioiTinh) && !"Nữ".equals(gioiTinh) && !"Khác".equals(gioiTinh)) {
            throw new IllegalArgumentException("Giới tính không hợp lệ");
        }
        this.gioiTinh = gioiTinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        if (ngaySinh == null) throw new IllegalArgumentException("Ngày sinh không được rỗng");
        int age = Period.between(ngaySinh, LocalDate.now()).getYears();
        if (age < 18 || age > 65) {
            throw new IllegalArgumentException("Tuổi phải từ 18 đến 65");
        }
        this.ngaySinh = ngaySinh;
    }

    public void setNgayBatDauLV(LocalDate ngayBatDauLV) {
        if (ngayBatDauLV == null || ngayBatDauLV.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày bắt đầu làm việc không hợp lệ (Không được lớn hơn ngày hiện tại)");
        }
        this.ngayBatDauLV = ngayBatDauLV;
    }

    // Ghi đè phương thức toString (Slide OOP)
    @Override
    public String toString() {
        return "NhanVien{" + "maNhanVien=" + maNhanVien + ", hoTen=" + hoTen + ", vaiTro=" + vaiTro + '}';
    }
}