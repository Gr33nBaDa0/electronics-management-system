package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.HoaDon;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhanCongCa;

public class HoaDon_DAO {
    private final ChiTietHoaDon_DAO ct = new ChiTietHoaDon_DAO();

    public HoaDon tim(String ma) throws SQLException {
        Connection con = ConnectDB.getConnection();
        return tim(con, ma, false);
    }

    static HoaDon tim(Connection con, String ma, boolean lock) throws SQLException {
        String sql =
                "SELECT * FROM dbo.HoaDon "
                        + (lock ? "WITH (UPDLOCK,HOLDLOCK) " : "")
                        + "WHERE MaHoaDon=?";
        try (PreparedStatement s = con.prepareStatement(sql)) {
            s.setString(1, ma);
            try (ResultSet r = s.executeQuery()) {
                if (r.next()) {
                    HoaDon hoaDon = new HoaDon(r.getString("MaHoaDon"));
                    String ma3 = r.getString("MaKhachHang");
                    hoaDon.setKhachHang(ma3 == null ? null : new KhachHang(ma3));
                    hoaDon.setNhanVien(new NhanVien(r.getString("MaNhanVien")));
                    Timestamp thoiGian4 = r.getTimestamp("NgayLap");
                    hoaDon.setNgayLap(thoiGian4 == null ? null : thoiGian4.toLocalDateTime());
                    hoaDon.setHinhThucThanhToan(r.getString("HinhThucThanhToan"));
                    hoaDon.setTrangThaiThanhToan(r.getString("TrangThaiThanhToan"));
                    hoaDon.setThueSuatVAT(r.getDouble("ThueSuatVAT"));
                    hoaDon.setPhanCongCa(new PhanCongCa(r.getString("MaPhanCong")));
                    hoaDon.setDiemSuDung(r.getInt("DiemSuDung"));
                    return hoaDon;
                } else {
                    return null;
                }
            }
        }
    }

    public List<HoaDon> timKiem(
            String tuKhoa, LocalDate tu, LocalDate den, String trangThai, String hinhThuc)
            throws SQLException {
        if (den.isBefore(tu)) {
            throw new SQLException("Khoảng ngày không hợp lệ.");
        }
        List<HoaDon> ds = new ArrayList<>();
        String sql =
                "SELECT h.* FROM dbo.HoaDon h LEFT JOIN dbo.KhachHang k ON"
                    + " k.MaKhachHang=h.MaKhachHang WHERE (h.MaHoaDon LIKE ? OR k.SoDienThoai LIKE"
                    + " ?) AND h.NgayLap>=? AND h.NgayLap<? AND (?='' OR h.TrangThaiThanhToan=?)"
                    + " AND (?='' OR h.HinhThucThanhToan=?) ORDER BY h.NgayLap DESC,h.MaHoaDon"
                    + " DESC";
        Connection con = ConnectDB.getConnection();
        try (PreparedStatement s = con.prepareStatement(sql)) {
            s.setString(1, "%" + tuKhoa + "%");
            s.setString(2, "%" + tuKhoa + "%");
            s.setDate(3, java.sql.Date.valueOf(tu));
            s.setDate(4, java.sql.Date.valueOf(den.plusDays(1)));
            s.setString(5, trangThai);
            s.setString(6, trangThai);
            s.setString(7, hinhThuc);
            s.setString(8, hinhThuc);
            try (ResultSet r = s.executeQuery()) {
                while (r.next()) {
                    HoaDon hoaDon = new HoaDon(r.getString("MaHoaDon"));
                    String ma3 = r.getString("MaKhachHang");
                    hoaDon.setKhachHang(ma3 == null ? null : new KhachHang(ma3));
                    hoaDon.setNhanVien(new NhanVien(r.getString("MaNhanVien")));
                    Timestamp thoiGian4 = r.getTimestamp("NgayLap");
                    hoaDon.setNgayLap(thoiGian4 == null ? null : thoiGian4.toLocalDateTime());
                    hoaDon.setHinhThucThanhToan(r.getString("HinhThucThanhToan"));
                    hoaDon.setTrangThaiThanhToan(r.getString("TrangThaiThanhToan"));
                    hoaDon.setThueSuatVAT(r.getDouble("ThueSuatVAT"));
                    hoaDon.setPhanCongCa(new PhanCongCa(r.getString("MaPhanCong")));
                    hoaDon.setDiemSuDung(r.getInt("DiemSuDung"));
                    ds.add(hoaDon);
                }
            }
        }
        return ds;
    }
}
