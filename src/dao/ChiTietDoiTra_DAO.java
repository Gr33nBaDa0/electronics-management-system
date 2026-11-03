package dao;

import connectDB.ConnectDB;

import entity.ChiTietDoiTra;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.PhieuDoiTra;
import entity.SanPham;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChiTietDoiTra_DAO {

    public List<ChiTietDoiTra> getTheoPhieu(String ma) throws SQLException {
        Connection con = ConnectDB.getConnection();
        return getTheoPhieu(con, ma);
    }

    List<ChiTietDoiTra> getTheoPhieu(Connection con, String ma) throws SQLException {
        List<ChiTietDoiTra> ds = new ArrayList<>();
        String sql = "SELECT * FROM dbo.ChiTietDoiTra WHERE MaPhieuDoiTra=? ORDER BY MaSanPham";
        try (PreparedStatement s = con.prepareStatement(sql)) {
            s.setString(1, ma);
            try (ResultSet r = s.executeQuery()) {
                while (r.next()) {
                    ChiTietDoiTra d = new ChiTietDoiTra();
                    d.setPhieuDoiTra(new PhieuDoiTra(ma));
                    ChiTietHoaDon g = new ChiTietHoaDon();
                    g.setHoaDon(new HoaDon(r.getString("MaHoaDon")));
                    g.setSanPham(new SanPham(r.getString("MaSanPham")));
                    d.setChiTietHoaDon(g);
                    d.setSoLuongTra(r.getInt("SoLuongTra"));
                    d.setTinhTrangHang(r.getString("TinhTrangHang"));
                    d.setTienHoan(r.getBigDecimal("TienHoan"));
                    ds.add(d);
                }
            }
        }
        return ds;
    }
}
