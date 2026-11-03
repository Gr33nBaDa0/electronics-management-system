package dao;

import connectDB.ConnectDB;

import entity.ChuongTrinhKhuyenMai;
import entity.TaiKhoan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ChuongTrinhKhuyenMai_DAO {

    public String layMaTiepTheo() throws SQLException {
        return taoMaTiepTheo(ConnectDB.getConnection(), false);
    }

    private String taoMaTiepTheo(Connection con, boolean khoa) throws SQLException {
        if (con == null || con.isClosed()) throw new SQLException("Chưa kết nối SQL.");
        String tienTo = "KM" + LocalDate.now().getYear();
        String sql = "SELECT MaKhuyenMai FROM dbo.ChuongTrinhKhuyenMai"
                + (khoa ? " WITH (UPDLOCK,HOLDLOCK)" : "")
                + " WHERE MaKhuyenMai LIKE ? ORDER BY MaKhuyenMai DESC";
        int lonNhat = 0;
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, tienTo + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String ma = rs.getString("MaKhuyenMai");
                    if (ma != null && ma.matches("(?i)" + tienTo + "[0-9]{3}")) {
                        lonNhat = Math.max(lonNhat, Integer.parseInt(ma.substring(tienTo.length())));
                    }
                }
            }
        }
        if (lonNhat >= 999) throw new SQLException("Đã hết dải mã khuyến mãi năm " + LocalDate.now().getYear() + ".");
        return tienTo + String.format(java.util.Locale.ROOT, "%03d", lonNhat + 1);
    }

    public List<ChuongTrinhKhuyenMai> getAllChuongTrinhKhuyenMai() throws SQLException {
        List<ChuongTrinhKhuyenMai> ds = new ArrayList<>();
        String sql = "SELECT * FROM dbo.ChuongTrinhKhuyenMai ORDER BY MaKhuyenMai";
        Connection con = ConnectDB.getConnection();
        try (PreparedStatement stmt = con.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ChuongTrinhKhuyenMai khuyenMai = new ChuongTrinhKhuyenMai();
                khuyenMai.setMaKhuyenMai(rs.getString("MaKhuyenMai"));
                khuyenMai.setTenChuongTrinh(rs.getString("TenChuongTrinh"));
                khuyenMai.setGiaTriGiam(rs.getDouble("GiaTriGiam"));
                khuyenMai.setNgayBatDau(
                        rs.getDate("NgayBatDau") == null
                                ? null
                                : rs.getDate("NgayBatDau").toLocalDate());
                khuyenMai.setNgayKetThuc(
                        rs.getDate("NgayKetThuc") == null
                                ? null
                                : rs.getDate("NgayKetThuc").toLocalDate());
                khuyenMai.setTrangThai(rs.getString("TrangThai"));
                ds.add(khuyenMai);
            }
        }
        return ds;
    }

    static ChuongTrinhKhuyenMai tim(Connection con, String ma) throws SQLException {
        String sql = "SELECT * FROM dbo.ChuongTrinhKhuyenMai WHERE MaKhuyenMai=?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, ma);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ChuongTrinhKhuyenMai khuyenMai = new ChuongTrinhKhuyenMai();
                    khuyenMai.setMaKhuyenMai(rs.getString("MaKhuyenMai"));
                    khuyenMai.setTenChuongTrinh(rs.getString("TenChuongTrinh"));
                    khuyenMai.setGiaTriGiam(rs.getDouble("GiaTriGiam"));
                    khuyenMai.setNgayBatDau(
                            rs.getDate("NgayBatDau") == null
                                    ? null
                                    : rs.getDate("NgayBatDau").toLocalDate());
                    khuyenMai.setNgayKetThuc(
                            rs.getDate("NgayKetThuc") == null
                                    ? null
                                    : rs.getDate("NgayKetThuc").toLocalDate());
                    khuyenMai.setTrangThai(rs.getString("TrangThai"));
                    return khuyenMai;
                } else {
                    return null;
                }
            }
        }
    }

    public boolean insertChuongTrinhKhuyenMai(ChuongTrinhKhuyenMai x, TaiKhoan tk)
            throws SQLException {
        Connection con = ConnectDB.getConnection();
        boolean tuDongCommitCu = con.getAutoCommit();
        int mucCoLapCu = con.getTransactionIsolation();
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        try {
            new TaiKhoan_DAO().kiemTraQuyen(tk, true);
            if (x.getNgayKetThuc().isBefore(x.getNgayBatDau())
                    || !Double.isFinite(x.getGiaTriGiam()) || x.getGiaTriGiam() < 0
                    || x.getGiaTriGiam() > 50.0) {
                throw new SQLException("Ngày hoặc % giảm không hợp lệ (0 đến 50%).");
            }
            String maMoi = taoMaTiepTheo(con, true);
            String sql =
                    "INSERT INTO dbo.ChuongTrinhKhuyenMai"
                        + " (MaKhuyenMai,TenChuongTrinh,GiaTriGiam,NgayBatDau,NgayKetThuc,TrangThai)"
                        + " VALUES (?,?,?,?,?,?)";
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, maMoi);
                stmt.setString(2, x.getTenChuongTrinh());
                stmt.setDouble(3, x.getGiaTriGiam());
                stmt.setDate(4, java.sql.Date.valueOf(x.getNgayBatDau()));
                stmt.setDate(5, java.sql.Date.valueOf(x.getNgayKetThuc()));
                stmt.setString(6, x.getTrangThai());
                stmt.executeUpdate();
            }
            con.commit();
            x.setMaKhuyenMai(maMoi);
        } catch (SQLException | RuntimeException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(tuDongCommitCu);
            con.setTransactionIsolation(mucCoLapCu);
        }
        return true;
    }

    public boolean deleteChuongTrinhKhuyenMai(String ma, TaiKhoan tk) throws SQLException {
        Connection con = ConnectDB.getConnection();
        boolean tuDongCommitCu = con.getAutoCommit();
        int mucCoLapCu = con.getTransactionIsolation();
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        try {
            new TaiKhoan_DAO().kiemTraQuyen(tk, true);
            String sql = "DELETE FROM dbo.ChuongTrinhKhuyenMai WHERE MaKhuyenMai=?";
            int ketQua3;
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, ma);
                ketQua3 = stmt.executeUpdate();
            }
            if (ketQua3 != 1) {
                throw new SQLException(
                        "Không tìm thấy dữ liệu. Chứng từ liên quan được giữ bởi khóa ngoại.");
            }
            con.commit();
        } catch (SQLException | RuntimeException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(tuDongCommitCu);
            con.setTransactionIsolation(mucCoLapCu);
        }
        return true;
    }

    static boolean hieuLuc(ChuongTrinhKhuyenMai x, LocalDate ngay) {
        return x != null && x.isDangHieuLuc(ngay);
    }

    public boolean updateChuongTrinhKhuyenMai(ChuongTrinhKhuyenMai x, TaiKhoan tk)
            throws SQLException {
        Connection con = ConnectDB.getConnection();
        boolean tuDongCommitCu = con.getAutoCommit();
        int mucCoLapCu = con.getTransactionIsolation();
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        try {
            new TaiKhoan_DAO().kiemTraQuyen(tk, true);
            if (x.getNgayKetThuc().isBefore(x.getNgayBatDau())
                    || !Double.isFinite(x.getGiaTriGiam()) || x.getGiaTriGiam() < 0
                    || x.getGiaTriGiam() > 50.0) {
                throw new SQLException("Ngày hoặc % giảm không hợp lệ (0 đến 50%).");
            }
            ChuongTrinhKhuyenMai cu = tim(con, x.getMaKhuyenMai());
            if (cu != null && Double.compare(cu.getGiaTriGiam(), x.getGiaTriGiam()) != 0) {
                String sqlDaDung = "SELECT MaHoaDon FROM dbo.ChiTietHoaDon"
                        + " WITH (UPDLOCK,HOLDLOCK) WHERE MaKhuyenMai=?";
                try (PreparedStatement stmt = con.prepareStatement(sqlDaDung)) {
                    stmt.setString(1, x.getMaKhuyenMai());
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            throw new SQLException("Khuyến mãi đã dùng trong hóa đơn."
                                    + " Tạo chương trình mới nếu muốn đổi % giảm.");
                        }
                    }
                }
            }
            String sql2 =
                    "UPDATE dbo.ChuongTrinhKhuyenMai SET"
                        + " TenChuongTrinh=?,GiaTriGiam=?,NgayBatDau=?,NgayKetThuc=?,TrangThai=?"
                        + " WHERE MaKhuyenMai=?";
            int ketQua6;
            try (PreparedStatement stmt3 = con.prepareStatement(sql2)) {
                stmt3.setString(1, x.getTenChuongTrinh());
                stmt3.setDouble(2, x.getGiaTriGiam());
                stmt3.setDate(3, java.sql.Date.valueOf(x.getNgayBatDau()));
                stmt3.setDate(4, java.sql.Date.valueOf(x.getNgayKetThuc()));
                stmt3.setString(5, x.getTrangThai());
                stmt3.setString(6, x.getMaKhuyenMai());
                ketQua6 = stmt3.executeUpdate();
            }
            if (ketQua6 != 1) {
                throw new SQLException("Không tìm thấy dữ liệu để sửa.");
            }
            con.commit();
        } catch (SQLException | RuntimeException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(tuDongCommitCu);
            con.setTransactionIsolation(mucCoLapCu);
        }
        return true;
    }
}
