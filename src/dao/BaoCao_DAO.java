package dao;

import connectDB.ConnectDB;

import entity.TaiKhoan;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BaoCao_DAO {

    /** Ngày báo cáo là ngày của ca thu/hoàn, vì mô hình chưa lưu ngày thanh toán/hoàn tất riêng. */
    public List<Object[]> doanhThu(LocalDate tu, LocalDate den, TaiKhoan tk) throws SQLException {
        if (den.isBefore(tu)) {
            throw new SQLException("Khoảng ngày không hợp lệ.");
        }
        List<Object[]> ds = new ArrayList<>();
        String sql =
                "SELECT p.Ngay,h.HinhThucThanhToan,COUNT(*) AS SoDon,SUM(v.Tien) AS TongThu FROM"
                    + " (SELECT"
                    + " h.MaHoaDon,ROUND((SUM(d.SoLuong*d.DonGia-d.GiamGia)-h.DiemSuDung*1000)*(1+h.ThueSuatVAT),2)"
                    + " AS Tien FROM dbo.HoaDon h JOIN dbo.ChiTietHoaDon d ON d.MaHoaDon=h.MaHoaDon"
                    + " WHERE h.TrangThaiThanhToan=N'Đã thanh toán' GROUP BY"
                    + " h.MaHoaDon,h.DiemSuDung,h.ThueSuatVAT) v JOIN dbo.HoaDon h ON"
                    + " h.MaHoaDon=v.MaHoaDon JOIN dbo.PhanCongCa p ON p.MaPhanCong=h.MaPhanCong"
                    + " WHERE p.Ngay>=? AND p.Ngay<=? GROUP BY p.Ngay,h.HinhThucThanhToan ORDER BY"
                    + " p.Ngay";
        Connection con = ConnectDB.getConnection();
        new TaiKhoan_DAO().kiemTraQuyen(tk, true);
        try (PreparedStatement s = con.prepareStatement(sql)) {
            s.setDate(1, java.sql.Date.valueOf(tu));
            s.setDate(2, java.sql.Date.valueOf(den));
            try (ResultSet r = s.executeQuery()) {
                while (r.next()) {
                    ds.add(
                            new Object[] {
                                r.getDate(1),
                                r.getString(2),
                                r.getInt(3),
                                r.getBigDecimal(4),
                                BigDecimal.ZERO,
                                r.getBigDecimal(4)
                            });
                }
            }
        }
        String refund =
                "SELECT ca.Ngay,h.HinhThucThanhToan,SUM(d.TienHoan) FROM dbo.PhieuDoiTra p JOIN"
                    + " dbo.ChiTietDoiTra d ON d.MaPhieuDoiTra=p.MaPhieuDoiTra JOIN dbo.HoaDon h ON"
                    + " h.MaHoaDon=p.MaHoaDonGoc JOIN dbo.PhanCongCa ca ON"
                    + " ca.MaPhanCong=p.MaPhanCongHoanTien WHERE p.TrangThai=N'Hoàn tất' AND"
                    + " ca.Ngay>=? AND ca.Ngay<=? GROUP BY ca.Ngay,h.HinhThucThanhToan";
        try (PreparedStatement s = con.prepareStatement(refund)) {
            s.setDate(1, java.sql.Date.valueOf(tu));
            s.setDate(2, java.sql.Date.valueOf(den));
            try (ResultSet r = s.executeQuery()) {
                while (r.next()) {
                    Object[] found = null;
                    for (Object[] a : ds) {
                        if (a[0].equals(r.getDate(1)) && a[1].equals(r.getString(2))) {
                            found = a;
                        }
                    }
                    if (found == null) {
                        found =
                                new Object[] {
                                    r.getDate(1),
                                    r.getString(2),
                                    0,
                                    BigDecimal.ZERO,
                                    BigDecimal.ZERO,
                                    BigDecimal.ZERO
                                };
                        ds.add(found);
                    }
                    found[4] = r.getBigDecimal(3);
                    found[5] = ((BigDecimal) found[3]).subtract((BigDecimal) found[4]);
                }
            }
        }
        ds.sort(Comparator.comparing(a -> a[0].toString() + a[1]));
        return ds;
    }

    public List<Object[]> sanPham(LocalDate tu, LocalDate den, TaiKhoan tk) throws SQLException {
        if (den.isBefore(tu)) {
            throw new SQLException("Khoảng ngày không hợp lệ.");
        }
        List<Object[]> ds = new ArrayList<>();
        String sql =
                "SELECT"
                    + " s.MaSanPham,s.TenSanPham,s.SoLuongTon,COALESCE(b.SoBan,0),COALESCE(t.SoTra,0)"
                    + " FROM dbo.SanPham s LEFT JOIN (SELECT d.MaSanPham,SUM(d.SoLuong) SoBan FROM"
                    + " dbo.ChiTietHoaDon d JOIN dbo.HoaDon h ON h.MaHoaDon=d.MaHoaDon JOIN"
                    + " dbo.PhanCongCa p ON p.MaPhanCong=h.MaPhanCong WHERE"
                    + " h.TrangThaiThanhToan=N'Đã thanh toán' AND p.Ngay>=? AND p.Ngay<=? GROUP BY"
                    + " d.MaSanPham) b ON b.MaSanPham=s.MaSanPham LEFT JOIN (SELECT"
                    + " d.MaSanPham,SUM(d.SoLuongTra) SoTra FROM dbo.ChiTietDoiTra d JOIN"
                    + " dbo.PhieuDoiTra p ON p.MaPhieuDoiTra=d.MaPhieuDoiTra JOIN dbo.PhanCongCa ca"
                    + " ON ca.MaPhanCong=p.MaPhanCongHoanTien WHERE p.TrangThai=N'Hoàn tất' AND"
                    + " ca.Ngay>=? AND ca.Ngay<=? GROUP BY d.MaSanPham) t ON"
                    + " t.MaSanPham=s.MaSanPham ORDER BY COALESCE(b.SoBan,0) DESC,s.MaSanPham";
        Connection con = ConnectDB.getConnection();
        new TaiKhoan_DAO().kiemTraQuyen(tk, true);
        try (PreparedStatement s = con.prepareStatement(sql)) {
            s.setDate(1, java.sql.Date.valueOf(tu));
            s.setDate(2, java.sql.Date.valueOf(den));
            s.setDate(3, java.sql.Date.valueOf(tu));
            s.setDate(4, java.sql.Date.valueOf(den));
            try (ResultSet r = s.executeQuery()) {
                while (r.next()) {
                    ds.add(
                            new Object[] {
                                r.getString(1),
                                r.getString(2),
                                r.getInt(3),
                                r.getInt(4),
                                r.getInt(5),
                                r.getInt(3) <= 5 ? "Tồn thấp" : "Còn hàng"
                            });
                }
            }
        }
        return ds;
    }
}
