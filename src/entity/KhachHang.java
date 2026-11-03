package entity;

import java.util.Objects;

public class KhachHang {
    private String maKhachHang;
    private String hoTen;
    private String soDienThoai;
    private String diaChi;
    private int diemTichLuy;
    private String email;

    public KhachHang() {
        this.diemTichLuy = 0;
    }
    public KhachHang(String maKhachHang) {
    	this.maKhachHang = maKhachHang;
    }
    public KhachHang(String maKhachHang, String hoTen, String soDienThoai, String diaChi, int diemTichLuy,
			String email) {
		this.maKhachHang = maKhachHang;
		this.hoTen = hoTen;
		this.soDienThoai = soDienThoai;
		this.diaChi = diaChi;
		this.diemTichLuy = diemTichLuy;
		this.email = email;
	}
    
	public String getMaKhachHang() {
        return maKhachHang;
    }
    public void setMaKhachHang(String maKhachHang) {
        if (maKhachHang == null || !maKhachHang.trim().matches("^KH\\d{3,}$")) {
            throw new IllegalArgumentException("Mã khách hàng không hợp lệ (Phải bắt đầu bằng KH theo sau là ít nhất 3 chữ số)");
        }
        this.maKhachHang = maKhachHang.trim();
    }

    public String getHoTen() {
        return hoTen;
    }
    public void setHoTen(String hoTen) {
        if (hoTen == null || hoTen.trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên không được rỗng");
        }
        this.hoTen = hoTen.trim();
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }
    public void setSoDienThoai(String soDienThoai) {
        if (soDienThoai == null || !soDienThoai.trim().matches("^(03|05|07|08|09)\\d{8}$")) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ (Phải gồm 10 chữ số, đầu số 03, 05, 07, 08, 09)");
        }
        this.soDienThoai = soDienThoai.trim();
    }

    public String getDiaChi() {
        return diaChi;
    }
    public void setDiaChi(String diaChi) {
        this.diaChi = (diaChi == null || diaChi.trim().isEmpty()) ? null : diaChi.trim();
    }

    public int getDiemTichLuy() {
        return diemTichLuy;
    }
    public void setDiemTichLuy(int diemTichLuy) {
        if (diemTichLuy < 0) {
            throw new IllegalArgumentException("Điểm tích lũy không được âm");
        }
        this.diemTichLuy = diemTichLuy;
    }

    public String getEmail() {
        return email;
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

    public String capNhatHangThanhVien() {
        if (diemTichLuy >= 2000) return "VIP";
        if (diemTichLuy >= 500) return "Thân thiết";
        return "Chuẩn";
    }

    public String getLoaiThanhVien() {
        return capNhatHangThanhVien();
    }

    public int tichDiem(double thanhTien) {
        if (thanhTien <= 0) return 0;
        int diemMoi = (int) (thanhTien / 100000.0);
        this.diemTichLuy += diemMoi;
        return diemMoi;
    }

    public int suDungDiem(int soDiemMuonDung, double tongTienHoaDon) {
        if (soDiemMuonDung <= 0 || tongTienHoaDon <= 0) return 0;
        int diemHopLe = Math.min(soDiemMuonDung, this.diemTichLuy);
        int diemToiDaChoPhep = (int) ((tongTienHoaDon * 0.5) / 1000.0);
        diemHopLe = Math.min(diemHopLe, diemToiDaChoPhep);
        this.diemTichLuy -= diemHopLe;
        return diemHopLe;
    }

    @Override
	public int hashCode() {
		return Objects.hash(maKhachHang);
	}
    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KhachHang other = (KhachHang) obj;
		return Objects.equals(maKhachHang, other.maKhachHang);
	}
	@Override
    public String toString() {
        return "KhachHang [maKhachHang=" + maKhachHang + ", hoTen=" + hoTen + ", soDienThoai=" + soDienThoai
                + ", diemTichLuy=" + diemTichLuy + ", hang=" + capNhatHangThanhVien() + "]";
    }
}
