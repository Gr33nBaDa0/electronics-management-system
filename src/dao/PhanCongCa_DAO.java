package dao;

import connectDB.ConnectDB;

import entity.CaLamViec;
import entity.NhanVien;
import entity.PhanCongCa;
import entity.TaiKhoan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PhanCongCa_DAO {

    public List<PhanCongCa> getAllPhanCongCa(TaiKhoan tk) throws SQLException {
        List<PhanCongCa> ds = new ArrayList<>();
        Connection con = ConnectDB.getConnection();
        new TaiKhoan_DAO().kiemTraQuyen(tk, false);
        String sql =
                "SELECT * FROM dbo.PhanCongCa WHERE (?='ADMIN' OR MaNhanVien=?) ORDER BY Ngay"
                        + " DESC,MaCa,MaNhanVien";
        try (PreparedStatement s = con.prepareStatement(sql)) {
            s.setString(1, tk.getQuyen());
            s.setString(2, tk.getNhanVien().getMaNhanVien());
            try (ResultSet r = s.executeQuery()) {
                while (r.next()) {
                    PhanCongCa x = new PhanCongCa(r.getString("MaPhanCong"));
                    x.setNhanVien(new NhanVien(r.getString("MaNhanVien")));
                    x.setNgay(r.getDate("Ngay").toLocalDate());
                    x.setGhiChu(r.getString("GhiChu"));
                    x.setCaLamViec(new CaLamViec(r.getString("MaCa")));
                    x.setTienQuyDauCa(r.getDouble("TienQuyDauCa"));
                    x.setTienMatThucDem(r.getDouble("TienMatThucDem"));
                    x.setTrangThai(r.getString("TrangThai"));
                    Timestamp thoiGian2 = r.getTimestamp("GioMoCa");
                    x.setGioMoCa(thoiGian2 == null ? null : thoiGian2.toLocalDateTime());
                    Timestamp thoiGian3 = r.getTimestamp("GioDongCa");
                    x.setGioDongCa(thoiGian3 == null ? null : thoiGian3.toLocalDateTime());
                    ds.add(x);
                }
            }
        }
        return ds;
    }

    /** PC + ngÃ y phÃ¢n cÃ´ng yyyyMMdd + thá»© tá»± 4 sá»‘ trong ngÃ y. Giá»¯ nguyÃªn mÃ£ cÅ©. */
    private String taoMaPhanCong(Connection con, LocalDate ngay) throws SQLException {
        String dau = "PC" + ngay.format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
        String sql =
                "SELECT MaPhanCong FROM dbo.PhanCongCa WITH (UPDLOCK,HOLDLOCK)"
                        + " WHERE MaPhanCong LIKE ? ORDER BY MaPhanCong DESC";
        int lonNhat = 0;
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, dau + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String ma = rs.getString("MaPhanCong");
                    if (ma != null && ma.matches("(?i)" + dau + "[0-9]{4}"))
                        lonNhat = Math.max(lonNhat, Integer.parseInt(ma.substring(dau.length())));
                }
            }
        }
        if (lonNhat >= 9999) throw new SQLException("NgÃ y nÃ y Ä‘Ã£ háº¿t dáº£i mÃ£ phÃ¢n cÃ´ng 0001â€“9999.");
        return dau + String.format(java.util.Locale.ROOT, "%04d", lonNhat + 1);
    }

    public void phanCong(String nv, String ca, LocalDate ngay, String ghiChu, TaiKhoan tk)
            throws SQLException {
        Connection con = ConnectDB.getConnection();
        boolean tuDongCommitCu = con.getAutoCommit();
        int mucCoLapCu = con.getTransactionIsolation();
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        try {
            new TaiKhoan_DAO().kiemTraQuyen(tk, true);
            if (ngay.isBefore(LocalDate.now())) {
                throw new SQLException("KhÃ´ng phÃ¢n cÃ´ng ngÃ y quÃ¡ khá»©.");
            }
            String maPhanCong = taoMaPhanCong(con, ngay);
            String sql2 =
                    "SELECT MaNhanVien FROM dbo.NhanVien WITH (UPDLOCK,HOLDLOCK) WHERE"
                            + " MaNhanVien=?";
            String ketQua3 = null;
            try (PreparedStatement stmt = con.prepareStatement(sql2)) {
                stmt.setString(1, nv);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        ketQua3 = rs.getString(1);
                    }
                }
            }
            if (ketQua3 == null) {
                throw new SQLException("NhÃ¢n viÃªn khÃ´ng tá»“n táº¡i.");
            }
            String sql =
                    "SELECT COUNT(*) FROM dbo.PhanCongCa p JOIN dbo.CaLamViec a ON a.MaCa=p.MaCa"
                        + " JOIN dbo.CaLamViec b ON b.MaCa=? WHERE p.MaNhanVien=? AND p.Ngay=? AND"
                        + " a.GioBatDau<b.GioKetThuc AND a.GioKetThuc>b.GioBatDau";
            int ketQua7 = 0;
            try (PreparedStatement stmt3 = con.prepareStatement(sql)) {
                stmt3.setString(1, ca);
                stmt3.setString(2, nv);
                stmt3.setDate(3, java.sql.Date.valueOf(ngay));
                try (ResultSet rs2 = stmt3.executeQuery()) {
                    if (rs2.next()) {
                        ketQua7 = rs2.getInt(1);
                    }
                }
            }
            if (ketQua7 != 0) {
                throw new SQLException("Ca Ä‘Ã£ trÃ¹ng hoáº·c chá»“ng giá» vá»›i phÃ¢n cÃ´ng hiá»‡n cÃ³.");
            }
            String sql4 =
                    "INSERT INTO dbo.PhanCongCa(MaPhanCong,MaNhanVien,Ngay,MaCa,GhiChu)"
                            + " VALUES(?,?,?,?,?)";
            try (PreparedStatement stmt4 = con.prepareStatement(sql4)) {
                stmt4.setString(1, maPhanCong);
                stmt4.setString(2, nv);
                stmt4.setDate(3, java.sql.Date.valueOf(ngay));
                stmt4.setString(4, ca);
                stmt4.setString(5, ghiChu);
                stmt4.executeUpdate();
            }
            con.commit();
        } catch (SQLException | RuntimeException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(tuDongCommitCu);
            con.setTransactionIsolation(mucCoLapCu);
        }
    }

    public void xoa(String id, TaiKhoan tk) throws SQLException {
        Connection con = ConnectDB.getConnection();
        boolean tuDongCommitCu = con.getAutoCommit();
        int mucCoLapCu = con.getTransactionIsolation();
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        try {
            new TaiKhoan_DAO().kiemTraQuyen(tk, true);
            String sql = "DELETE FROM dbo.PhanCongCa WHERE MaPhanCong=? AND TrangThai=N'ChÆ°a má»Ÿ'";
            int ketQua3;
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, id);
                ketQua3 = stmt.executeUpdate();
            }
            if (ketQua3 != 1) {
                throw new SQLException("Chá»‰ xÃ³a phÃ¢n cÃ´ng chÆ°a má»Ÿ, chÆ°a cÃ³ chá»©ng tá»«.");
            }
            con.commit();
        } catch (SQLException | RuntimeException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(tuDongCommitCu);
            con.setTransactionIsolation(mucCoLapCu);
        }
    }

    public void moCa(String id, double quy, TaiKhoan tk) throws SQLException {
        Connection con = ConnectDB.getConnection();
        boolean tuDongCommitCu = con.getAutoCommit();
        int mucCoLapCu = con.getTransactionIsolation();
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        try {
            new TaiKhoan_DAO().kiemTraQuyen(tk, false);
            if (!Double.isFinite(quy) || quy < 0) {
                throw new SQLException("Quá»¹ Ä‘áº§u ca khÃ´ng Ä‘Æ°á»£c Ã¢m.");
            }
            String nv = tk.getNhanVien().getMaNhanVien();
            String sql =
                    "SELECT MaNhanVien FROM dbo.NhanVien WITH (UPDLOCK,HOLDLOCK) WHERE"
                            + " MaNhanVien=?";
            String ketQua3 = null;
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, nv);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        ketQua3 = rs.getString(1);
                    }
                }
            }
            String sql2 =
                    "SELECT COUNT(*) FROM dbo.PhanCongCa WHERE MaNhanVien=? AND TrangThai=N'Äang"
                            + " má»Ÿ'";
            int ketQua7 = 0;
            try (PreparedStatement stmt3 = con.prepareStatement(sql2)) {
                stmt3.setString(1, nv);
                try (ResultSet rs2 = stmt3.executeQuery()) {
                    if (rs2.next()) {
                        ketQua7 = rs2.getInt(1);
                    }
                }
            }
            if (ketQua7 != 0) {
                throw new SQLException("Báº¡n cÃ²n ca Ä‘ang má»Ÿ. ÄÃ³ng ca cÅ© trÆ°á»›c.");
            }
            String sql3 =
                    "UPDATE dbo.PhanCongCa SET"
                            + " TienQuyDauCa=?,GioMoCa=SYSDATETIME(),TrangThai=N'Äang má»Ÿ' WHERE"
                            + " MaPhanCong=? AND MaNhanVien=? AND Ngay=CAST(GETDATE() AS DATE) AND"
                            + " TrangThai=N'ChÆ°a má»Ÿ'";
            int ketQua11;
            try (PreparedStatement stmt4 = con.prepareStatement(sql3)) {
                stmt4.setDouble(1, quy);
                stmt4.setString(2, id);
                stmt4.setString(3, nv);
                ketQua11 = stmt4.executeUpdate();
            }
            if (ketQua11 != 1) {
                throw new SQLException("Chá»‰ má»Ÿ ca cá»§a mÃ¬nh, Ä‘Ãºng ngÃ y vÃ  chÆ°a má»Ÿ.");
            }
            con.commit();
        } catch (SQLException | RuntimeException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(tuDongCommitCu);
            con.setTransactionIsolation(mucCoLapCu);
        }
    }

    public double tienLyThuyet(String pc, TaiKhoan tk) throws SQLException {
        Connection con = ConnectDB.getConnection();
        new TaiKhoan_DAO().kiemTraQuyen(tk, false);
        String sql = "SELECT MaNhanVien FROM dbo.PhanCongCa WHERE MaPhanCong=?";
        String ketQua3 = null;
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, pc);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ketQua3 = rs.getString(1);
                }
            }
        }
        String nv = ketQua3;
        if (!("ADMIN".equals(tk.getQuyen())) && !(tk.getNhanVien().getMaNhanVien().equals(nv))) {
            throw new SQLException("KhÃ´ng Ä‘Æ°á»£c xem quá»¹ ca khÃ¡c.");
        }
        return tienLyThuyet(con, pc);
    }

    static double tienLyThuyet(Connection con, String pc) throws SQLException {
        String sql2 = "SELECT TienQuyDauCa FROM dbo.PhanCongCa WHERE MaPhanCong=?";
        double ketQua3 = 0.0;
        try (PreparedStatement stmt = con.prepareStatement(sql2)) {
            stmt.setString(1, pc);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ketQua3 = rs.getDouble(1);
                }
            }
        }
        double quy = ketQua3;
        String sql =
                "SELECT COALESCE(SUM(v.Tien),0) FROM (SELECT"
                    + " h.MaHoaDon,ROUND((SUM(d.SoLuong*d.DonGia-d.GiamGia)-h.DiemSuDung*1000)*(1+h.ThueSuatVAT),2)"
                    + " AS Tien FROM dbo.HoaDon h JOIN dbo.ChiTietHoaDon d ON d.MaHoaDon=h.MaHoaDon"
                    + " WHERE h.MaPhanCong=? AND h.TrangThaiThanhToan=N'ÄÃ£ thanh toÃ¡n' AND"
                    + " h.HinhThucThanhToan=N'Tiá»n máº·t' GROUP BY"
                    + " h.MaHoaDon,h.DiemSuDung,h.ThueSuatVAT) v";
        double ketQua7 = 0.0;
        try (PreparedStatement stmt3 = con.prepareStatement(sql)) {
            stmt3.setString(1, pc);
            try (ResultSet rs2 = stmt3.executeQuery()) {
                if (rs2.next()) {
                    ketQua7 = rs2.getDouble(1);
                }
            }
        }
        double thu = ketQua7;
        String sql4 =
                "SELECT COALESCE(SUM(d.TienHoan),0) FROM dbo.PhieuDoiTra p JOIN dbo.ChiTietDoiTra d"
                        + " ON d.MaPhieuDoiTra=p.MaPhieuDoiTra JOIN dbo.HoaDon h ON"
                        + " h.MaHoaDon=p.MaHoaDonGoc WHERE p.MaPhanCongHoanTien=? AND"
                        + " p.TrangThai=N'HoÃ n táº¥t' AND h.HinhThucThanhToan=N'Tiá»n máº·t'";
        double ketQua11 = 0.0;
        try (PreparedStatement stmt4 = con.prepareStatement(sql4)) {
            stmt4.setString(1, pc);
            try (ResultSet rs3 = stmt4.executeQuery()) {
                if (rs3.next()) {
                    ketQua11 = rs3.getDouble(1);
                }
            }
        }
        double hoan = ketQua11;
        return util.InHoaDon.tron(quy + thu - hoan);
    }

    public void dongCa(String id, double dem, String lyDo, TaiKhoan tk) throws SQLException {
        Connection con = ConnectDB.getConnection();
        boolean tuDongCommitCu = con.getAutoCommit();
        int mucCoLapCu = con.getTransactionIsolation();
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        try {
            new TaiKhoan_DAO().kiemTraQuyen(tk, false);
            String sql =
                    "SELECT MaNhanVien FROM dbo.PhanCongCa WITH (UPDLOCK,HOLDLOCK) WHERE"
                            + " MaPhanCong=? AND TrangThai=N'Äang má»Ÿ'";
            String ketQua3 = null;
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        ketQua3 = rs.getString(1);
                    }
                }
            }
            String nv = ketQua3;
            if (!(tk.getNhanVien().getMaNhanVien().equals(nv))) {
                throw new SQLException("Chá»‰ Ä‘Ã³ng ca Ä‘ang má»Ÿ cá»§a mÃ¬nh.");
            }
            if (!Double.isFinite(dem) || dem < 0) {
                throw new SQLException("Tiá»n thá»±c Ä‘áº¿m khÃ´ng Ä‘Æ°á»£c Ã¢m.");
            }
            double lt = tienLyThuyet(con, id);
            if (util.InHoaDon.soSanhTien(dem, lt) != 0 && (lyDo == null || lyDo.trim().isEmpty())) {
                throw new SQLException("CÃ³ chÃªnh lá»‡ch, cáº§n nháº­p giáº£i trÃ¬nh.");
            }
            String sql2 =
                    "UPDATE dbo.PhanCongCa SET"
                        + " TienMatThucDem=?,GhiChu=?,GioDongCa=SYSDATETIME(),TrangThai=N'ÄÃ£ Ä‘Ã³ng'"
                        + " WHERE MaPhanCong=?";
            try (PreparedStatement stmt3 = con.prepareStatement(sql2)) {
                stmt3.setDouble(1, dem);
                stmt3.setString(2, lyDo);
                stmt3.setString(3, id);
                stmt3.executeUpdate();
            }
            con.commit();
        } catch (SQLException | RuntimeException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(tuDongCommitCu);
            con.setTransactionIsolation(mucCoLapCu);
        }
    }

    public String layMaCaDangMo(Connection con, TaiKhoan taiKhoan) throws SQLException {
        String sql =
                "SELECT MaPhanCong FROM dbo.PhanCongCa WITH (UPDLOCK,HOLDLOCK) WHERE MaNhanVien = ?"
                        + " AND Ngay = CAST(GETDATE() AS DATE) AND TrangThai = N'Äang má»Ÿ'";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, taiKhoan.getNhanVien().getMaNhanVien());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("MaPhanCong");
                }
            }
        }
        throw new SQLException("Báº¡n chÆ°a má»Ÿ ca hÃ´m nay. VÃ o má»¥c Ca lÃ m viá»‡c Ä‘á»ƒ má»Ÿ ca.");
    }
}
