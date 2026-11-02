package entity;


public class SanPham {
    private String maSanPham;
    private String tenSanPham;
    private String loaiHang;
    private String thuongHieu;
    private double donGiaBan;
    private double giaNhap;
    private String thongSoKiThuat;
    private String xuatXu;
    private String trangThai;
    private int thoiHanBaoHanh;
    private int soLuongTon;
    private String hinhAnh;

    public SanPham() {}

    public SanPham(String maSanPham) {
        this.maSanPham = maSanPham;
    }


  
    public SanPham(String maSanPham, String tenSanPham, String loaiHang, String thuongHieu, double donGiaBan,
			double giaNhap, String thongSoKiThuat, String xuatXu, String trangThai, int thoiHanBaoHanh, int soLuongTon,
			String hinhAnh) {
		this.maSanPham = maSanPham;
		this.tenSanPham = tenSanPham;
		this.loaiHang = loaiHang;
		this.thuongHieu = thuongHieu;
		this.donGiaBan = donGiaBan;
		this.giaNhap = giaNhap;
		this.thongSoKiThuat = thongSoKiThuat;
		this.xuatXu = xuatXu;
		this.trangThai = trangThai;
		this.thoiHanBaoHanh = thoiHanBaoHanh;
		this.soLuongTon = soLuongTon;
		this.hinhAnh = hinhAnh;
	}

	public String getMaSanPham() {
        return maSanPham;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public void setMaSanPham(String maSanPham) {
        this.maSanPham = maSanPham;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public String getLoaiHang() {
        return loaiHang;
    }

    public void setLoaiHang(String loaiHang) {
        this.loaiHang = loaiHang;
    }

    public String getThuongHieu() {
        return thuongHieu;
    }

    public void setThuongHieu(String thuongHieu) {
        this.thuongHieu = thuongHieu;
    }

    public double getDonGiaBan() {
        return donGiaBan;
    }

    public void setDonGiaBan(double donGiaBan) {
        this.donGiaBan = donGiaBan;
    }

    public double getGiaNhap() {
        return giaNhap;
    }

    public void setGiaNhap(double giaNhap) {
        this.giaNhap = giaNhap;
    }

    public String getThongSoKiThuat() {
        return thongSoKiThuat;
    }

    public void setThongSoKiThuat(String thongSoKiThuat) {
        this.thongSoKiThuat = thongSoKiThuat;
    }

    public String getXuatXu() {
        return xuatXu;
    }

    public void setXuatXu(String xuatXu) {
        this.xuatXu = xuatXu;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public int getThoiHanBaoHanh() {
        return thoiHanBaoHanh;
    }

    public void setThoiHanBaoHanh(int thoiHanBaoHanh) {
        this.thoiHanBaoHanh = thoiHanBaoHanh;
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(int soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    @Override
    public String toString() {
        return maSanPham + " · " + tenSanPham;
    }
}
