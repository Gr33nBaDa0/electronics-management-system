package dao;

import connectDB.ConnectDB;
import entity.NhanVien;
import entity.TaiKhoan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class TaiKhoan_DAO {

    public TaiKhoan checkLogin(String tenDangNhap, String matKhau) {
        TaiKhoan taiKhoan = null;
        Connection conn = ConnectDB.getConnection();
        
        if (conn == null) return null;

        // Truy vấn JOIN 2 bảng để lấy thông tin tài khoản và nhân viên
        String sql = "SELECT tk.TenDangNhap, tk.Quyen, tk.MatKhau, " +
                     "nv.MaNhanVien, nv.HoTen, nv.VaiTro, nv.SDT, nv.Email, nv.DiaChi, " +
                     "nv.SoCCCD, nv.GioiTinh, nv.NgaySinh, nv.NgayBatDauLV " +
                     "FROM TaiKhoan tk " +
                     "JOIN NhanVien nv ON tk.MaNhanVien = nv.MaNhanVien " +
                     "WHERE tk.TenDangNhap = ? AND tk.MatKhau = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            ps.setString(2, matKhau);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Tạo đối tượng Nhân viên
                    NhanVien nv = new NhanVien();
                    nv.setMaNhanVien(rs.getString("MaNhanVien"));
                    nv.setHoTen(rs.getString("HoTen"));
                    nv.setVaiTro(rs.getString("VaiTro"));
                    nv.setSdt(rs.getString("SDT"));
                    nv.setEmail(rs.getString("Email"));
                    nv.setDiaChi(rs.getString("DiaChi"));
                    nv.setSoCCCD(rs.getString("SoCCCD"));
                    nv.setGioiTinh(rs.getString("GioiTinh"));
                    nv.setNgaySinh(rs.getDate("NgaySinh").toLocalDate());
                    nv.setNgayBatDauLV(rs.getDate("NgayBatDauLV").toLocalDate());

                    // Tạo đối tượng Tài khoản
                    taiKhoan = new TaiKhoan();
                    taiKhoan.setTenDangNhap(rs.getString("TenDangNhap"));
                    taiKhoan.setQuyen(rs.getString("Quyen"));
                    taiKhoan.setMatKhau(rs.getString("MatKhau"));
                    taiKhoan.setNhanVien(nv);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close(); // Đóng kết nối
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return taiKhoan;
    }
}