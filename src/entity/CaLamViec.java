package entity;

import java.time.LocalTime;

public class CaLamViec {
    private String maCa;
    private String tenCa;
    private LocalTime gioBatDau;
    private LocalTime gioKetThuc;

    public CaLamViec() {}

    public CaLamViec(String maCa, String tenCa, LocalTime gioBatDau, LocalTime gioKetThuc) {
        this.setMaCa(maCa);
        this.setTenCa(tenCa);
        this.setGioBatDau(gioBatDau);
        this.setGioKetThuc(gioKetThuc);
    }

    public String getMaCa() { return maCa; }
    public void setMaCa(String maCa) {
        if (maCa == null || !maCa.matches("^CA\\d{2}$")) {
            throw new IllegalArgumentException("Mã ca không hợp lệ (VD: CA01, CA02, CA03)");
        }
        this.maCa = maCa;
    }

    public String getTenCa() { return tenCa; }
    public void setTenCa(String tenCa) {
        if (tenCa == null || tenCa.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên ca không được rỗng");
        }
        this.tenCa = tenCa;
    }

    public LocalTime getGioBatDau() { return gioBatDau; }
    public void setGioBatDau(LocalTime gioBatDau) {
        if (gioBatDau == null) throw new IllegalArgumentException("Giờ bắt đầu không được rỗng");
        this.gioBatDau = gioBatDau;
    }

    public LocalTime getGioKetThuc() { return gioKetThuc; }
    public void setGioKetThuc(LocalTime gioKetThuc) {
        if (gioKetThuc == null) throw new IllegalArgumentException("Giờ kết thúc không được rỗng");
        if (gioBatDau != null && !gioKetThuc.isAfter(gioBatDau)) {
            throw new IllegalArgumentException("Giờ kết thúc phải sau giờ bắt đầu");
        }
        this.gioKetThuc = gioKetThuc;
    }

    @Override
    public String toString() {
        return maCa + " - " + tenCa;
    }
}