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
    private int baoHanhThang;
    private int soLuongTon;

    public SanPham() {}

    public SanPham(String maSanPham, String tenSanPham, String loaiHang, String thuongHieu, double donGiaBan, double giaNhap, String thongSoKiThuat, String xuatXu, String trangThai, int baoHanhThang, int soLuongTon) {
        this.setMaSanPham(maSanPham);
        this.setTenSanPham(tenSanPham);
        this.setLoaiHang(loaiHang);
        this.setThuongHieu(thuongHieu);
        this.setGiaNhap(giaNhap);
        this.setDonGiaBan(donGiaBan);
        this.setThongSoKiThuat(thongSoKiThuat);
        this.setXuatXu(xuatXu);
        this.setTrangThai(trangThai);
        this.setBaoHanhThang(baoHanhThang);
        this.setSoLuongTon(soLuongTon);
    }
    
    // Getters
    public String getMaSanPham() { return maSanPham; }
    public String getTenSanPham() { return tenSanPham; }
    public String getLoaiHang() { return loaiHang; }
    public String getThuongHieu() { return thuongHieu; }
    public double getDonGiaBan() { return donGiaBan; }
    public double getGiaNhap() { return giaNhap; }
    public String getThongSoKiThuat() { return thongSoKiThuat; }
    public String getXuatXu() { return xuatXu; }
    public String getTrangThai() { return trangThai; }
    public int getBaoHanhThang() { return baoHanhThang; }
    public int getSoLuongTon() { return soLuongTon; }

    // Setters có Validation (Regex)
    public void setMaSanPham(String maSanPham) {
        if (maSanPham == null || !maSanPham.matches("^SP\\d{4}$")) {
            throw new IllegalArgumentException("Mã sản phẩm không hợp lệ (VD: SP0001)");
        }
        this.maSanPham = maSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        if (tenSanPham == null || tenSanPham.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được rỗng");
        }
        this.tenSanPham = tenSanPham;
    }

    public void setLoaiHang(String loaiHang) {
        if (loaiHang == null || loaiHang.trim().isEmpty()) throw new IllegalArgumentException("Loại hàng không được rỗng");
        this.loaiHang = loaiHang;
    }

    public void setThuongHieu(String thuongHieu) {
        if (thuongHieu == null || thuongHieu.trim().isEmpty()) throw new IllegalArgumentException("Thương hiệu không được rỗng");
        this.thuongHieu = thuongHieu;
    }

    public void setGiaNhap(double giaNhap) {
        if (giaNhap <= 0) throw new IllegalArgumentException("Giá nhập phải > 0");
        this.giaNhap = giaNhap;
    }

    public void setDonGiaBan(double donGiaBan) {
        if (donGiaBan <= 0 || donGiaBan < this.giaNhap) {
            throw new IllegalArgumentException("Đơn giá bán phải > 0 và >= giá nhập");
        }
        this.donGiaBan = donGiaBan;
    }

    public void setThongSoKiThuat(String thongSoKiThuat) { this.thongSoKiThuat = thongSoKiThuat; }
    
    public void setXuatXu(String xuatXu) {
        if (xuatXu == null || xuatXu.trim().isEmpty()) throw new IllegalArgumentException("Xuất xứ không được rỗng");
        this.xuatXu = xuatXu;
    }

    public void setTrangThai(String trangThai) {
        if (!"Đang bán".equals(trangThai) && !"Ngưng bán".equals(trangThai)) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ");
        }
        this.trangThai = trangThai;
    }

    public void setBaoHanhThang(int baoHanhThang) {
        if (baoHanhThang < 0) throw new IllegalArgumentException("Thời hạn bảo hành không hợp lệ");
        this.baoHanhThang = baoHanhThang;
    }

    public void setSoLuongTon(int soLuongTon) {
        if (soLuongTon < 0) throw new IllegalArgumentException("Số lượng tồn không được âm");
        this.soLuongTon = soLuongTon;
    }

    @Override
    public String toString() {
        return tenSanPham;
    }
}