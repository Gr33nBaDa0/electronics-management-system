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

    /** PC + ngày phân công yyyyMMdd + thứ tự 4 số trong ngày. Giữ nguyên mã cũ. */
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
        if (lonNhat >= 9999) throw new SQLException("Ngày này đã hết dải mã phân công 0001–9999.");
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
                throw new SQLException("Không phân công ngày quá khứ.");
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
                throw new SQLException("Nhân viên không tồn tại.");
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
                throw new SQLException("Ca đã trùng hoặc chồng giờ với phân công hiện có.");
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
            String sql = "DELETE FROM dbo.PhanCongCa WHERE MaPhanCong=? AND TrangThai=N'Chưa mở'";
            int ketQua3;
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, id);
                ketQua3 = stmt.executeUpdate();
            }
            if (ketQua3 != 1) {
                throw new SQLException("Chỉ xóa phân công chưa mở, chưa có chứng từ.");
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
                throw new SQLException("Quỹ đầu ca không được âm.");
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
                    "SELECT COUNT(*) FROM dbo.PhanCongCa WHERE MaNhanVien=? AND TrangThai=N'Đang"
                            + " mở'";
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
                throw new SQLException("Bạn còn ca đang mở. Đóng ca cũ trước.");
            }
            String sql3 =
                    "UPDATE dbo.PhanCongCa SET"
                            + " TienQuyDauCa=?,GioMoCa=SYSDATETIME(),TrangThai=N'Đang mở' WHERE"
                            + " MaPhanCong=? AND MaNhanVien=? AND Ngay=CAST(GETDATE() AS DATE) AND"
                            + " TrangThai=N'Chưa mở'";
            int ketQua11;
            try (PreparedStatement stmt4 = con.prepareStatement(sql3)) {
                stmt4.setDouble(1, quy);
                stmt4.setString(2, id);
                stmt4.setString(3, nv);
                ketQua11 = stmt4.executeUpdate();
            }
            if (ketQua11 != 1) {
                throw new SQLException("Chỉ mở ca của mình, đúng ngày và chưa mở.");
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
            throw new SQLException("Không được xem quỹ ca khác.");
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
                    + " WHERE h.MaPhanCong=? AND h.TrangThaiThanhToan=N'Đã thanh toán' AND"
                    + " h.HinhThucThanhToan=N'Tiền mặt' GROUP BY"
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
                        + " p.TrangThai=N'Hoàn tất' AND h.HinhThucThanhToan=N'Tiền mặt'";
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
        return until.InHoaDon.tron(quy + thu - hoan);
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
                            + " MaPhanCong=? AND TrangThai=N'Đang mở'";
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
                throw new SQLException("Chỉ đóng ca đang mở của mình.");
            }
            if (!Double.isFinite(dem) || dem < 0) {
                throw new SQLException("Tiền thực đếm không được âm.");
            }
            double lt = tienLyThuyet(con, id);
            if (until.InHoaDon.soSanhTien(dem, lt) != 0 && (lyDo == null || lyDo.trim().isEmpty())) {
                throw new SQLException("Có chênh lệch, cần nhập giải trình.");
            }
            String sql2 =
                    "UPDATE dbo.PhanCongCa SET"
                        + " TienMatThucDem=?,GhiChu=?,GioDongCa=SYSDATETIME(),TrangThai=N'Đã đóng'"
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
                        + " AND Ngay = CAST(GETDATE() AS DATE) AND TrangThai = N'Đang mở'";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, taiKhoan.getNhanVien().getMaNhanVien());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("MaPhanCong");
                }
            }
        }
        throw new SQLException("Bạn chưa mở ca hôm nay. Vào mục Ca làm việc để mở ca.");
    }
}
