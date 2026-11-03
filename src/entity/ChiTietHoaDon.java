package entity;


public class ChiTietHoaDon {
    private HoaDon hoaDon;
    private SanPham sanPham;
    private int soLuong;
    private ChuongTrinhKhuyenMai chuongTrinhKhuyenMai;
    private double donGia;

    public ChiTietHoaDon() {}

    public ChiTietHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }



    public ChiTietHoaDon(HoaDon hoaDon, SanPham sanPham, int soLuong, ChuongTrinhKhuyenMai chuongTrinhKhuyenMai,
			double donGia) {
		this.hoaDon = hoaDon;
		this.sanPham = sanPham;
		this.soLuong = soLuong;
		this.chuongTrinhKhuyenMai = chuongTrinhKhuyenMai;
		this.donGia = donGia;
	}

	public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public ChuongTrinhKhuyenMai getChuongTrinhKhuyenMai() {
        return chuongTrinhKhuyenMai;
    }

    public void setChuongTrinhKhuyenMai(ChuongTrinhKhuyenMai chuongTrinhKhuyenMai) {
        this.chuongTrinhKhuyenMai = chuongTrinhKhuyenMai;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    public double getGiamGia() {
        if (chuongTrinhKhuyenMai == null) return 0.0;
        return HoaDon.lamTronTien(
                donGia * soLuong * chuongTrinhKhuyenMai.getGiaTriGiam() / 100.0);
    }

    public double getThanhTienChiTiet() {
        return getTienHang() - getGiamGia();
    }

    public double getTienHang() { return donGia * soLuong; }
    public double getTienThucTraPhanBo(java.util.List<ChiTietHoaDon> ds, HoaDon h) {
        for (Object[] dong : h.phanBoTienThucTra(ds)) {
            if (sanPham.getMaSanPham().equals(dong[0])) {
                return ((Number) dong[1]).doubleValue();
            }
        }
        throw new IllegalArgumentException("Dòng hàng không thuộc hóa đơn.");
    }
    public int getSoLuongConDuocTra(java.util.List<ChiTietDoiTra> cacLanTra) {
        int daTra = 0;
        for (ChiTietDoiTra d : cacLanTra) {
            ChiTietHoaDon goc = d.getChiTietHoaDon();
            PhieuDoiTra p = d.getPhieuDoiTra();
            if (p != null && !"Từ chối".equals(p.getTrangThai())
                    && hoaDon.getMaHoaDon().equals(goc.getHoaDon().getMaHoaDon())
                    && sanPham.getMaSanPham().equals(goc.getSanPham().getMaSanPham())) daTra += d.getSoLuong();
        }
        return soLuong - daTra;
    }
    public java.time.LocalDate getHanBaoHanhThamKhao() {
        if (hoaDon == null || hoaDon.getNgayLap() == null || sanPham == null
                || sanPham.getBaoHanhThang() <= 0) return null;
        return hoaDon.getNgayLap().toLocalDate().plusMonths(sanPham.getBaoHanhThang());
    }

    @Override
    public String toString() {
        return sanPham == null
                ? ""
                : sanPham.getMaSanPham() + " · " + sanPham.getTenSanPham() + " × " + soLuong;
    }
}
