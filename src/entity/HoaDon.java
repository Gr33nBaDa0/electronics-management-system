package entity;

import java.time.LocalDateTime;

public class HoaDon {
    private String maHoaDon;
    private KhachHang khachHang;
    private NhanVien nhanVien;
    private LocalDateTime ngayLap;
    private String hinhThucThanhToan;
    private String trangThaiThanhToan;
    private double thueSuatVAT;
    private PhanCongCa phanCongCa;
    private int diemSuDung;

    public HoaDon() {}

    public HoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public HoaDon(String maHoaDon, KhachHang khachHang, NhanVien nhanVien, LocalDateTime ngayLap,
			String hinhThucThanhToan, String trangThaiThanhToan, double thueSuatVAT, PhanCongCa phanCongCa,
			int diemSuDung) {
		this.maHoaDon = maHoaDon;
		this.khachHang = khachHang;
		this.nhanVien = nhanVien;
		this.ngayLap = ngayLap;
		this.hinhThucThanhToan = hinhThucThanhToan;
		this.trangThaiThanhToan = trangThaiThanhToan;
		this.thueSuatVAT = thueSuatVAT;
		this.phanCongCa = phanCongCa;
		this.diemSuDung = diemSuDung;
	}

	public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public LocalDateTime getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDateTime ngayLap) {
        this.ngayLap = ngayLap;
    }

    public String getHinhThucThanhToan() {
        return hinhThucThanhToan;
    }

    public void setHinhThucThanhToan(String hinhThucThanhToan) {
        this.hinhThucThanhToan = hinhThucThanhToan;
    }

    public String getTrangThaiThanhToan() {
        return trangThaiThanhToan;
    }

    public void setTrangThaiThanhToan(String trangThaiThanhToan) {
        this.trangThaiThanhToan = trangThaiThanhToan;
    }

    public double getThueSuatVAT() {
        return thueSuatVAT;
    }

    public void setThueSuatVAT(double thueSuatVAT) {
        this.thueSuatVAT = thueSuatVAT;
    }

    public PhanCongCa getPhanCongCa() {
        return phanCongCa;
    }

    public void setPhanCongCa(PhanCongCa phanCongCa) {
        this.phanCongCa = phanCongCa;
    }

    public int getDiemSuDung() {
        return diemSuDung;
    }

    public void setDiemSuDung(int diemSuDung) {
        this.diemSuDung = diemSuDung;
    }

    public double getTongTienHang(java.util.List<ChiTietHoaDon> ds) {
        double tong = 0;
        for (ChiTietHoaDon d : ds) tong += d.getTienHang();
        return lamTronTien(tong);
    }
    public double getTongTienSauKhuyenMai(java.util.List<ChiTietHoaDon> ds) {
        double tong = 0;
        for (ChiTietHoaDon d : ds) tong += d.getThanhTienChiTiet();
        return lamTronTien(tong);
    }
    public double getTongGiamKhuyenMai(java.util.List<ChiTietHoaDon> ds) {
        return lamTronTien(getTongTienHang(ds) - getTongTienSauKhuyenMai(ds));
    }
    public double getTienGiamTuDiem() { return diemSuDung * 1000.0; }
    public double getTongTruocVAT(java.util.List<ChiTietHoaDon> ds) {
        return lamTronTien(getTongTienSauKhuyenMai(ds) - getTienGiamTuDiem());
    }
    public double getTienVAT(java.util.List<ChiTietHoaDon> ds) {
        return lamTronTien(getTongTruocVAT(ds) * thueSuatVAT);
    }
    public double getTongThanhToan(java.util.List<ChiTietHoaDon> ds) {
        return lamTronTien(getTongTruocVAT(ds) + getTienVAT(ds));
    }
    public int getDiemDuocCong(java.util.List<ChiTietHoaDon> ds) {
        return tinhDiemDuocCong(getTongThanhToan(ds));
    }
    public int getDiemSuDungToiDa(java.util.List<ChiTietHoaDon> ds, int soDuDiem) {
        return tinhDiemSuDungToiDa(getTongTienSauKhuyenMai(ds), soDuDiem);
    }
    public double getTienThua(java.util.List<ChiTietHoaDon> ds, double tienKhachDua) {
        return tinhTienThua(tienKhachDua, getTongThanhToan(ds));
    }
    public static double tinhTienThua(double tienKhachDua, double tongThanhToan) {
        return lamTronTien(tienKhachDua - tongThanhToan);
    }

    public java.util.List<Object[]> phanBoTienThucTra(java.util.List<ChiTietHoaDon> chiTiet) {
        java.util.List<ChiTietHoaDon> ds = new java.util.ArrayList<>(chiTiet);
        ds.sort(java.util.Comparator.comparing(d -> d.getSanPham().getMaSanPham()));
        java.util.List<Object[]> kq = new java.util.ArrayList<>();
        double hang = getTongTienSauKhuyenMai(ds), tong = getTongThanhToan(ds);
        double daPhanBo = 0, luyKeHang = 0;
        for (int i = 0; i < ds.size(); i++) {
            ChiTietHoaDon d = ds.get(i);
            luyKeHang = lamTronTien(luyKeHang + d.getThanhTienChiTiet());
            double muc = i == ds.size() - 1 ? tong
                    : (hang == 0 ? 0 : lamTronTien(tong * luyKeHang / hang));
            kq.add(new Object[] {d.getSanPham().getMaSanPham(), lamTronTien(muc - daPhanBo)});
            daPhanBo = lamTronTien(muc);
        }
        return kq;
    }
    public static int tinhDiemDuocCong(double thanhToan) {
        double diem = lamTronTien(thanhToan) / 100000.0;
        diem = diem < 0 ? Math.ceil(diem) : Math.floor(diem);
        if (diem > Integer.MAX_VALUE || diem < Integer.MIN_VALUE)
            throw new ArithmeticException("Số điểm vượt giới hạn cho phép.");
        return (int) diem;
    }
    public static int tinhDiemSuDungToiDa(double sauGiam, int soDuDiem) {
        return (int) Math.min(soDuDiem, Math.floor(lamTronTien(sauGiam) / 2000.0));
    }
    public static double lamTronTien(double n) {
        if (!Double.isFinite(n) || !Double.isFinite(n * 100.0))
            throw new IllegalArgumentException("Số tiền không hợp lệ.");
        double giaTri = Math.abs(n) * 100.0;
        double ketQua = giaTri < 4503599627370496.0
                ? Math.floor(Math.nextUp(giaTri) + 0.5) : giaTri;
        double tien = Math.copySign(ketQua / 100.0, n);
        return tien == 0.0 ? 0.0 : tien;
    }
    public static int soSanhTien(double a, double b) {
        return Double.compare(lamTronTien(a), lamTronTien(b));
    }

    @Override
    public String toString() {
        return String.valueOf(maHoaDon);
    }
}
