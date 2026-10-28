package entity;

public class KhachHang {
    private String maKhachHang;
    private String hoTen;
    private String soDienThoai;
    private String diaChi;
    private int diemTichLuy;
    private String loaiThanhVien;
    private String email;

    public KhachHang() {}

    public KhachHang(String maKhachHang, String hoTen, String soDienThoai, String diaChi, int diemTichLuy, String loaiThanhVien, String email) {
        this.setMaKhachHang(maKhachHang);
        this.setHoTen(hoTen);
        this.setSoDienThoai(soDienThoai);
        this.setDiaChi(diaChi);
        this.setDiemTichLuy(diemTichLuy);
        this.setLoaiThanhVien(loaiThanhVien);
        this.setEmail(email);
    }

    public String getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(String maKhachHang) {
        if (maKhachHang == null || !maKhachHang.matches("^KH\\d{3}$")) {
            throw new IllegalArgumentException("Mã khách hàng không hợp lệ (VD: KH001)");
        }
        this.maKhachHang = maKhachHang;
    }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) {
        if (hoTen == null || hoTen.trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên khách hàng không được rỗng");
        }
        this.hoTen = hoTen;
    }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) {
        if (soDienThoai == null || !soDienThoai.matches("^0[35789]\\d{8}$")) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ (10 số, đầu 03/05/07/08/09)");
        }
        this.soDienThoai = soDienThoai;
    }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public int getDiemTichLuy() { return diemTichLuy; }
    public void setDiemTichLuy(int diemTichLuy) {
        if (diemTichLuy < 0) throw new IllegalArgumentException("Điểm tích lũy không được âm");
        this.diemTichLuy = diemTichLuy;
    }

    public String getLoaiThanhVien() { return loaiThanhVien; }
    public void setLoaiThanhVien(String loaiThanhVien) {
        if (loaiThanhVien == null) { this.loaiThanhVien = "Chuẩn"; return; }
        if (!"Chuẩn".equals(loaiThanhVien) && !"Thân thiết".equals(loaiThanhVien) && !"VIP".equals(loaiThanhVien)) {
            throw new IllegalArgumentException("Loại thành viên không hợp lệ");
        }
        this.loaiThanhVien = loaiThanhVien;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        if (email != null && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Email không hợp lệ");
        }
        this.email = email;
    }

    // --- Business methods ---
    /** Tích điểm: 100.000 VNĐ = 1 điểm */
    public int tichDiem(double thanhTien) {
        if (thanhTien < 0) throw new IllegalArgumentException("Thành tiền không hợp lệ");
        int diemMoi = (int) (thanhTien / 100_000);
        this.diemTichLuy += diemMoi;
        capNhatHangThanhVien();
        return diemMoi;
    }

    /** Cập nhật hạng thành viên dựa trên điểm */
    public String capNhatHangThanhVien() {
        if (this.diemTichLuy >= 1000) this.loaiThanhVien = "VIP";
        else if (this.diemTichLuy >= 100) this.loaiThanhVien = "Thân thiết";
        else this.loaiThanhVien = "Chuẩn";
        return this.loaiThanhVien;
    }

    /** Sử dụng điểm: 1 điểm = 1.000 VNĐ, giảm tối đa 50% hóa đơn */
    public int suDungDiem(int soDiemMuonDung, double tongTienHang) {
        if (soDiemMuonDung < 0) throw new IllegalArgumentException("Số điểm không hợp lệ");
        if (soDiemMuonDung > this.diemTichLuy) {
            throw new IllegalArgumentException("Số điểm sử dụng vượt quá điểm hiện có");
        }
        double tienGiam = soDiemMuonDung * 1000.0;
        double toiDa = tongTienHang * 0.5;
        if (tienGiam > toiDa) {
            throw new IllegalArgumentException("Tiền giảm từ điểm không được vượt quá 50% hóa đơn");
        }
        this.diemTichLuy -= soDiemMuonDung;
        return soDiemMuonDung;
    }

    @Override
    public String toString() {
        return hoTen + " - " + soDienThoai;
    }
}