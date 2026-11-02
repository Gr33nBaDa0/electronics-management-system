package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.ChiTietHoaDon;
import entity.ChuongTrinhKhuyenMai;
import entity.HoaDon;
import entity.SanPham;
import until.InHoaDon;

public class ChiTietHoaDon_DAO {

	public List<ChiTietHoaDon> getTheoHoaDon(String ma) throws SQLException {
        Connection con = ConnectDB.getConnection();
        return getTheoHoaDon(con, ma);
    }

    public List<ChiTietHoaDon> getTheoHoaDon(Connection con, String ma) throws SQLException {
        List<ChiTietHoaDon> ds = new ArrayList<>();
        String sql =
                "SELECT d.*,s.*,km.TenChuongTrinh AS TenKhuyenMai,km.GiaTriGiam,"
                        + " km.NgayBatDau AS KMBatDau,km.NgayKetThuc AS KMKetThuc,"
                        + " km.TrangThai AS TrangThaiKM FROM dbo.ChiTietHoaDon d"
                        + " JOIN dbo.SanPham s ON s.MaSanPham=d.MaSanPham"
                        + " LEFT JOIN dbo.ChuongTrinhKhuyenMai km ON km.MaKhuyenMai=d.MaKhuyenMai"
                        + " WHERE d.MaHoaDon=? ORDER BY d.MaSanPham";
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
                    if (km != null) {
                        ChuongTrinhKhuyenMai khuyenMai = new ChuongTrinhKhuyenMai(km);
                        khuyenMai.setTenChuongTrinh(rs.getString("TenKhuyenMai"));
                        khuyenMai.setGiaTriGiam(rs.getDouble("GiaTriGiam"));
                        khuyenMai.setNgayBatDau(rs.getDate("KMBatDau").toLocalDate());
                        khuyenMai.setNgayKetThuc(rs.getDate("KMKetThuc").toLocalDate());
                        khuyenMai.setTrangThai(rs.getString("TrangThaiKM"));
                        d.setChuongTrinhKhuyenMai(khuyenMai);
                    }
                    d.setDonGia(rs.getDouble("DonGia"));
                   /* if (InHoaDon.soSanhTien(d.getGiamGia(), rs.getDouble("GiamGia")) != 0) {
                        throw new SQLException("Mức giảm của hóa đơn " + ma
                                + " không khớp khuyến mãi gốc. Kiểm tra dữ liệu lịch sử trước khi xử lý.");
                    }*/
                    ds.add(d);
                }
            }
        }
        return ds;
    }
    
}
