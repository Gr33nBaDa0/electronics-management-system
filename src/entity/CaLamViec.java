package entity;

import java.time.LocalTime;
import java.util.Objects;

public class CaLamViec {
    private String maCa;
    private String tenCa;
    private LocalTime gioBatDau;
    private LocalTime gioKetThuc;

    public CaLamViec() {}
    public CaLamViec(String maCa) {
    	this.maCa = maCa;
    }
    public CaLamViec(String maCa, String tenCa, LocalTime gioBatDau, LocalTime gioKetThuc) {
		this.maCa = maCa;
		this.tenCa = tenCa;
		this.gioBatDau = gioBatDau;
		this.gioKetThuc = gioKetThuc;
	}
        
    public String getMaCa() {
		return maCa;
	}
	public String getTenCa() {
		return tenCa;
	}
	public LocalTime getGioBatDau() {
		return gioBatDau;
	}
	public LocalTime getGioKetThuc() {
		return gioKetThuc;
	}
	
    public void setMaCa(String maCa) {
    	if (maCa == null || !maCa.trim().matches("^CA\\d{2}$")) {
            throw new IllegalArgumentException("Mã ca không hợp lệ (Phải có định dạng CAxx, ví dụ CA01)");
        }
        this.maCa = maCa.trim();
	}
	public void setTenCa(String tenCa) {
		if (tenCa == null || tenCa.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên ca không được rỗng");
        }
        this.tenCa = tenCa.trim();
	}
	public void setGioBatDau(LocalTime gioBatDau) {
		if (gioBatDau == null) {
            throw new IllegalArgumentException("Giờ bắt đầu không được rỗng");
        }
		this.gioBatDau = gioBatDau;
	}
	public void setGioKetThuc(LocalTime gioKetThuc) {
		if (gioKetThuc == null) {
            throw new IllegalArgumentException("Giờ kết thúc không được rỗng");
        }
        if (gioBatDau != null && !gioKetThuc.isAfter(gioBatDau)) {
            throw new IllegalArgumentException("Giờ kết thúc không hợp lệ (Phải sau giờ bắt đầu)");
        }
		this.gioKetThuc = gioKetThuc;
	}
	
    @Override
	public int hashCode() {
		return Objects.hash(maCa);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CaLamViec other = (CaLamViec) obj;
		return Objects.equals(maCa, other.maCa);
	}
	@Override
    public String toString() {
        return maCa + " · " + tenCa + " (" + gioBatDau + "–" + gioKetThuc + ")";
    }
}
