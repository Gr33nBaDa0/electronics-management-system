package common;

import java.time.LocalDate;

import entity.NhanVien;
import entity.SanPham;

public class TestEntity {
    public static void main(String[] args) {
        System.out.println("--- BẮT ĐẦU TEST ENTITY ---");

        // TEST 1: Tạo Nhân Viên HỢP LỆ
        try {
            NhanVien nv = new NhanVien();
            nv.setMaNhanVien("NV001");
            nv.setHoTen("Nguyễn Văn Test");
            nv.setVaiTro("Bán hàng");
            nv.setSdt("0912345678");
            nv.setSoCCCD("012345678901");
            nv.setGioiTinh("Nam");
            nv.setNgaySinh(LocalDate.of(2000, 1, 1));
            nv.setNgayBatDauLV(LocalDate.now());
            System.out.println("=> Tạo Nhân Viên HỢP LỆ thành công: " + nv.getHoTen());
        } catch (Exception e) {
            System.err.println("LỖI (Không được xảy ra): " + e.getMessage());
        }

        // TEST 2: Tạo Nhân Viên KHÔNG HỢP LỆ (Sai số điện thoại)
        try {
            NhanVien nv2 = new NhanVien();
            nv2.setSdt("12345"); // Cố tình nhập sai
            System.err.println("LỖI (Đáng lẽ phải báo lỗi ở đây)");
        } catch (Exception e) {
            System.out.println("=> Đã chặn thành công lỗi SĐT: " + e.getMessage());
        }

        // TEST 3: Tạo Sản Phẩm HỢP LỆ
        try {
            SanPham sp = new SanPham();
            sp.setMaSanPham("SP0001");
            sp.setTenSanPham("Tivi Sony");
            sp.setGiaNhap(10000000);
            sp.setDonGiaBan(15000000); // Giá bán > giá nhập
            System.out.println("=> Tạo Sản Phẩm HỢP LỆ thành công: " + sp.getTenSanPham());
        } catch (Exception e) {
            System.err.println("LỖI (Không được xảy ra): " + e.getMessage());
        }

        // TEST 4: Tạo Sản Phẩm KHÔNG HỢP LỆ (Giá bán < giá nhập)
        try {
            SanPham sp2 = new SanPham();
            sp2.setGiaNhap(10000000);
            sp2.setDonGiaBan(5000000); // Cố tình nhập giá bán rẻ hơn giá nhập
            System.err.println("LỖI (Đáng lẽ phải báo lỗi ở đây)");
        } catch (Exception e) {
            System.out.println("=> Đã chặn thành công lỗi Giá bán: " + e.getMessage());
        }

        System.out.println("--- KẾT THÚC TEST ---");
    }
}