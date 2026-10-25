package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.SanPham;

public class SanPham_DAO {
    public List<SanPham> getAllSanPham() throws SQLException {
        List<SanPham> danhSach = new ArrayList<>();
        String sql = "SELECT MaSanPham, TenSanPham, LoaiHang, ThuongHieu, DonGiaBan, "
                + "GiaNhap, ThongSoKiThuat, XuatXu, TrangThai, ThoiHanBaoHanh, SoLuongTon "
                + "FROM dbo.SanPham ORDER BY MaSanPham";

        Connection con = ConnectDB.getConnection();
        try (PreparedStatement stmt = con.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                SanPham sp = new SanPham();
                sp.setMaSanPham(rs.getString("MaSanPham"));
                sp.setTenSanPham(rs.getString("TenSanPham"));
                sp.setLoaiHang(rs.getString("LoaiHang"));
                sp.setThuongHieu(rs.getString("ThuongHieu"));
                sp.setDonGiaBan(rs.getBigDecimal("DonGiaBan"));
                sp.setGiaNhap(rs.getBigDecimal("GiaNhap"));
                sp.setThongSoKiThuat(rs.getString("ThongSoKiThuat"));
                sp.setXuatXu(rs.getString("XuatXu"));
                sp.setTrangThai(rs.getString("TrangThai"));
                sp.setThoiHanBaoHanh(rs.getInt("ThoiHanBaoHanh"));
                sp.setSoLuongTon(rs.getInt("SoLuongTon"));
                danhSach.add(sp);
            }
        }
        return danhSach;
    }
}
