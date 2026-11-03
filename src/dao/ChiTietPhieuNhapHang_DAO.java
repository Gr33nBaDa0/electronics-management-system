package dao;

import connectDB.ConnectDB;

import entity.ChiTietPhieuNhapHang;
import entity.PhieuNhapHang;
import entity.SanPham;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Lớp xử lý dữ liệu Chi Tiết Phiếu Nhập Hàng (Data Access Object)
 * Quản lý các dòng hàng, đơn giá nhập và số lượng chi tiết từng sản phẩm.
 * @author Võ Văn Tuấn - MSSV: 24694721 (Nhóm 12)
 */
public class ChiTietPhieuNhapHang_DAO {

    public List<ChiTietPhieuNhapHang> getTheoPhieu(String ma) throws SQLException {
        List<ChiTietPhieuNhapHang> ds = new ArrayList<>();
        Connection con = ConnectDB.getConnection();
        String sql =
                "SELECT d.*,sp.* FROM dbo.ChiTietPhieuNhapHang d JOIN dbo.SanPham sp ON"
                        + " sp.MaSanPham=d.MaSanPham WHERE MaPhieuNhap=? ORDER BY d.MaSanPham";
        try (PreparedStatement s = con.prepareStatement(sql)) {
            s.setString(1, ma);
            try (ResultSet r = s.executeQuery()) {
                while (r.next()) {
                    ChiTietPhieuNhapHang d = new ChiTietPhieuNhapHang();
                    d.setPhieuNhapHang(new PhieuNhapHang(ma));
                    SanPham sanPham = new SanPham();
                    sanPham.setMaSanPham(r.getString("MaSanPham"));
                    sanPham.setTenSanPham(r.getString("TenSanPham"));
                    sanPham.setLoaiHang(r.getString("LoaiHang"));
                    sanPham.setThuongHieu(r.getString("ThuongHieu"));
                    sanPham.setDonGiaBan(r.getDouble("DonGiaBan"));
                    sanPham.setGiaNhap(r.getDouble("GiaNhap"));
                    sanPham.setThongSoKiThuat(r.getString("ThongSoKiThuat"));
                    sanPham.setXuatXu(r.getString("XuatXu"));
                    sanPham.setTrangThai(r.getString("TrangThai"));
                    sanPham.setThoiHanBaoHanh(r.getInt("ThoiHanBaoHanh"));
                    sanPham.setSoLuongTon(r.getInt("SoLuongTon"));
                    d.setSanPham(sanPham);
                    d.setSoLuong(r.getInt("SoLuong"));
                    d.setDonGiaNhap(r.getDouble("DonGiaNhap"));
                    ds.add(d);
                }
            }
        }
        return ds;
    }

    public void ghi(Connection con, String ma, List<ChiTietPhieuNhapHang> ds) throws SQLException {
        Set<String> seen = new HashSet<>();
        for (ChiTietPhieuNhapHang d : ds) {
            if (d.getSoLuong() <= 0
                    || !Double.isFinite(d.getDonGiaNhap()) || d.getDonGiaNhap() < 0
                    || !(seen.add(d.getSanPham().getMaSanPham()))) {
                throw new SQLException("Dòng nhập không hợp lệ hoặc trùng sản phẩm.");
            }
            String sql =
                    "INSERT INTO dbo.ChiTietPhieuNhapHang(MaPhieuNhap,MaSanPham,SoLuong,DonGiaNhap)"
                            + " VALUES(?,?,?,?)";
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, ma);
                stmt.setString(2, d.getSanPham().getMaSanPham());
                stmt.setInt(3, d.getSoLuong());
                stmt.setDouble(4, d.getDonGiaNhap());
                stmt.executeUpdate();
            }
        }
    }
}
