package entity;


public class ChiTietDoiTra {
    private PhieuDoiTra phieuDoiTra;
    private ChiTietHoaDon chiTietHoaDon;
    private int soLuongTra;
    private String tinhTrangHang;
    private double tienHoan;

    public ChiTietDoiTra() {}

    public ChiTietDoiTra(PhieuDoiTra phieuDoiTra) {
        this.phieuDoiTra = phieuDoiTra;
    }

    public ChiTietDoiTra(
            PhieuDoiTra phieuDoiTra,
            ChiTietHoaDon chiTietHoaDon,
            int soLuongTra,
            String tinhTrangHang,
            double tienHoan) {
        this.phieuDoiTra = phieuDoiTra;
        this.chiTietHoaDon = chiTietHoaDon;
        this.soLuongTra = soLuongTra;
        this.tinhTrangHang = tinhTrangHang;
        this.tienHoan = tienHoan;
    }

    public PhieuDoiTra getPhieuDoiTra() {
        return phieuDoiTra;
    }

    public void setPhieuDoiTra(PhieuDoiTra phieuDoiTra) {
        this.phieuDoiTra = phieuDoiTra;
    }

    public ChiTietHoaDon getChiTietHoaDon() {
        return chiTietHoaDon;
    }

    public void setChiTietHoaDon(ChiTietHoaDon chiTietHoaDon) {
        this.chiTietHoaDon = chiTietHoaDon;
    }

    public int getSoLuongTra() {
        return soLuongTra;
    }

    public void setSoLuongTra(int soLuongTra) {
        this.soLuongTra = soLuongTra;
    }

    public String getTinhTrangHang() {
        return tinhTrangHang;
    }

    public void setTinhTrangHang(String tinhTrangHang) {
        this.tinhTrangHang = tinhTrangHang;
    }

    public double getTienHoan() {
        return tienHoan;
    }

    public void setTienHoan(double tienHoan) {
        this.tienHoan = tienHoan;
    }

    @Override
    public String toString() {
        return String.valueOf(phieuDoiTra);
    }
}
