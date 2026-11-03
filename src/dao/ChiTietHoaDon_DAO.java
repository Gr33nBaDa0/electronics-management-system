package dao;

import connectDB.ConnectDB;

import entity.ChiTietHoaDon;
import entity.ChuongTrinhKhuyenMai;
import entity.HoaDon;
import entity.SanPham;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDon_DAO {

    public List<ChiTietHoaDon> getTheoHoaDon(String ma) throws SQLException {
        Connection con = ConnectDB.getConnection();
        return getTheoHoaDon(con, ma);
    }

    public List<ChiTietHoaDon> getTheoHoaDon(Connection con, String ma) throws SQLException {
        List<ChiTietHoaDon> ds = new ArrayList<>();
        String sql =
                "SELECT d.*,s.* FROM dbo.ChiTietHoaDon d JOIN dbo.SanPham s ON"
                        + " s.MaSanPham=d.MaSanPham WHERE d.MaHoaDon=? ORDER BY d.MaSanPham";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, ma);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ChiTietHoaDon d = new ChiTietHoaDon();
                    d.setHoaDon(new HoaDon(ma));
                    SanPham sanPham = new SanPham();
                    sanPham.setMaSanPham(rs.getString("MaSanPham"));
                    sanPham.setTenSanPham(rs.getString("TenSanPham"));
                    sanPham.setLoaiHang(rs.getString("LoaiHang"));
                    sanPham.setThuongHieu(rs.getString("ThuongHieu"));
                    sanPham.setDonGiaBan(rs.getDouble("DonGiaBan"));
                    sanPham.setGiaNhap(rs.getDouble("GiaNhap"));
                    sanPham.setThongSoKiThuat(rs.getString("ThongSoKiThuat"));
                    sanPham.setXuatXu(rs.getString("XuatXu"));
                    sanPham.setTrangThai(rs.getString("TrangThai"));
                    sanPham.setThoiHanBaoHanh(rs.getInt("ThoiHanBaoHanh"));
                    sanPham.setSoLuongTon(rs.getInt("SoLuongTon"));
                    d.setSanPham(sanPham);
                    d.setSoLuong(rs.getInt("SoLuong"));
                    String km = rs.getString("MaKhuyenMai");
                    d.setChuongTrinhKhuyenMai(km == null ? null : new ChuongTrinhKhuyenMai(km));
                    d.setDonGia(rs.getDouble("DonGia"));
                    d.setGiamGia(rs.getDouble("GiamGia"));
                    ds.add(d);
                }
            }
        }
        return ds;
    }

    public void ghi(Connection con, HoaDon hd, List<ChiTietHoaDon> ds) throws SQLException {
        for (ChiTietHoaDon d : ds) {
            String sql =
                    "INSERT INTO"
                        + " dbo.ChiTietHoaDon(MaHoaDon,MaSanPham,SoLuong,MaKhuyenMai,DonGia,GiamGia)"
                        + " VALUES(?,?,?,?,?,?)";
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, hd.getMaHoaDon());
                stmt.setString(2, d.getSanPham().getMaSanPham());
                stmt.setInt(3, d.getSoLuong());
                stmt.setString(
                        4,
                        d.getChuongTrinhKhuyenMai() == null
                                ? null
                                : d.getChuongTrinhKhuyenMai().getMaKhuyenMai());
                stmt.setDouble(5, d.getDonGia());
                stmt.setDouble(6, d.getGiamGia());
                stmt.executeUpdate();
            }
        }
    }
}
