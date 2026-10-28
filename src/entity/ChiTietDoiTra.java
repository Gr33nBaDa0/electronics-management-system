package entity;

public class ChiTietDoiTra {
    private PhieuDoiTra phieuDoiTra;
    private ChiTietHoaDon chiTietHoaDon;
    private int soLuong;
    private String tinhTrangHang;
    private double tienHoan;

    public ChiTietDoiTra() {}

    public ChiTietDoiTra(PhieuDoiTra phieuDoiTra, ChiTietHoaDon chiTietHoaDon, int soLuong, String tinhTrangHang) {
        this.setPhieuDoiTra(phieuDoiTra);
        this.setChiTietHoaDon(chiTietHoaDon);
        this.setSoLuong(soLuong);
        this.setTinhTrangHang(tinhTrangHang);
    }

    public PhieuDoiTra getPhieuDoiTra() { return phieuDoiTra; }
    public void setPhieuDoiTra(PhieuDoiTra phieuDoiTra) {
        if (phieuDoiTra == null) throw new IllegalArgumentException("Phiếu đổi trả không được rỗng");
        this.phieuDoiTra = phieuDoiTra;
    }

    public ChiTietHoaDon getChiTietHoaDon() { return chiTietHoaDon; }
    public void setChiTietHoaDon(ChiTietHoaDon chiTietHoaDon) {
        if (chiTietHoaDon == null) throw new IllegalArgumentException("Chi tiết hóa đơn không được rỗng");
        this.chiTietHoaDon = chiTietHoaDon;
    }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) {
        if (soLuong <= 0) throw new IllegalArgumentException("Số lượng phải > 0");
        if (chiTietHoaDon != null && soLuong > chiTietHoaDon.getSoLuong()) {
            throw new IllegalArgumentException("Số lượng trả vượt quá số lượng đã mua");
        }
        this.soLuong = soLuong;
    }

    public String getTinhTrangHang() { return tinhTrangHang; }
    public void setTinhTrangHang(String tinhTrangHang) {
        if (!"Nguyên vẹn".equals(tinhTrangHang) && !"Lỗi".equals(tinhTrangHang)) {
            throw new IllegalArgumentException("Tình trạng hàng không hợp lệ");
        }
        this.tinhTrangHang = tinhTrangHang;
    }

    public double getTienHoan() { return tienHoan; }

    /** Tiền hoàn = Số lượng trả * Đơn giá (từ chi tiết hóa đơn gốc) */
    public double tinhTienHoan() {
        if (chiTietHoaDon == null) return 0;
        this.tienHoan = this.soLuong * this.chiTietHoaDon.getDonGia();
        return this.tienHoan;
    }

    @Override
    public String toString() {
        return "Trả " + soLuong + " - " + tinhTrangHang;
    }
}