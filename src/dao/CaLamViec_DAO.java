package dao;

import connectDB.ConnectDB;

import entity.CaLamViec;
import entity.TaiKhoan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class CaLamViec_DAO {

    public boolean insertCaLamViec(CaLamViec x, TaiKhoan tk) throws SQLException {
        Connection con = ConnectDB.getConnection();
        boolean tuDongCommitCu = con.getAutoCommit();
        int mucCoLapCu = con.getTransactionIsolation();
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        try {
            new TaiKhoan_DAO().kiemTraQuyen(tk, true);
            if (!(x.getGioBatDau().isBefore(x.getGioKetThuc()))) {
                throw new SQLException("Giờ kết thúc phải sau giờ bắt đầu trong cùng ngày.");
            }
            String sql =
                    "INSERT INTO dbo.CaLamViec(MaCa,TenCa,GioBatDau,GioKetThuc) VALUES(?,?,?,?)";
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, x.getMaCa());
                stmt.setString(2, x.getTenCa());
                stmt.setTime(3, Time.valueOf(x.getGioBatDau()));
                stmt.setTime(4, Time.valueOf(x.getGioKetThuc()));
                stmt.executeUpdate();
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

    public boolean deleteCaLamViec(String ma, TaiKhoan tk) throws SQLException {
        Connection con = ConnectDB.getConnection();
        boolean tuDongCommitCu = con.getAutoCommit();
        int mucCoLapCu = con.getTransactionIsolation();
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        try {
            new TaiKhoan_DAO().kiemTraQuyen(tk, true);
            String sql = "DELETE FROM dbo.CaLamViec WHERE MaCa=?";
            int ketQua3;
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, ma);
                ketQua3 = stmt.executeUpdate();
            }
            if (ketQua3 != 1) {
                throw new SQLException(
                        "Không tìm thấy ca. Ca đã có phân công được giữ bởi khóa ngoại.");
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

    public List<CaLamViec> getAllCaLamViec() throws SQLException {
        List<CaLamViec> ds = new ArrayList<>();
        String sql = "SELECT * FROM dbo.CaLamViec ORDER BY MaCa";
        Connection con = ConnectDB.getConnection();
        try (PreparedStatement stmt = con.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                CaLamViec caLamViec = new CaLamViec();
                caLamViec.setMaCa(rs.getString("MaCa"));
                caLamViec.setTenCa(rs.getString("TenCa"));
                caLamViec.setGioBatDau(rs.getTime("GioBatDau").toLocalTime());
                caLamViec.setGioKetThuc(rs.getTime("GioKetThuc").toLocalTime());
                ds.add(caLamViec);
            }
        }
        return ds;
    }

    static CaLamViec tim(Connection con, String ma) throws SQLException {
        String sql = "SELECT * FROM dbo.CaLamViec WHERE MaCa=?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, ma);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    CaLamViec caLamViec = new CaLamViec();
                    caLamViec.setMaCa(rs.getString("MaCa"));
                    caLamViec.setTenCa(rs.getString("TenCa"));
                    caLamViec.setGioBatDau(rs.getTime("GioBatDau").toLocalTime());
                    caLamViec.setGioKetThuc(rs.getTime("GioKetThuc").toLocalTime());
                    return caLamViec;
                } else {
                    return null;
                }
            }
        }
    }

    public boolean updateCaLamViec(CaLamViec x, TaiKhoan tk) throws SQLException {
        Connection con = ConnectDB.getConnection();
        boolean tuDongCommitCu = con.getAutoCommit();
        int mucCoLapCu = con.getTransactionIsolation();
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        try {
            new TaiKhoan_DAO().kiemTraQuyen(tk, true);
            if (!(x.getGioBatDau().isBefore(x.getGioKetThuc()))) {
                throw new SQLException("Giờ kết thúc phải sau giờ bắt đầu trong cùng ngày.");
            }
            String sql2 = "SELECT COUNT(*) FROM dbo.PhanCongCa WHERE MaCa=?";
            int ketQua6 = 0;
            try (PreparedStatement stmt3 = con.prepareStatement(sql2)) {
                stmt3.setString(1, x.getMaCa());
                try (ResultSet rs = stmt3.executeQuery()) {
                    if (rs.next()) {
                        ketQua6 = rs.getInt(1);
                    }
                }
            }
            if (ketQua6 != 0) {
                throw new SQLException("Ca đã có phân công: giữ lịch sử, tạo ca mới nếu đổi giờ.");
            }
            String sql3 = "UPDATE dbo.CaLamViec SET TenCa=?,GioBatDau=?,GioKetThuc=? WHERE MaCa=?";
            int ketQua10;
            try (PreparedStatement stmt4 = con.prepareStatement(sql3)) {
                stmt4.setString(1, x.getTenCa());
                stmt4.setTime(2, Time.valueOf(x.getGioBatDau()));
                stmt4.setTime(3, Time.valueOf(x.getGioKetThuc()));
                stmt4.setString(4, x.getMaCa());
                ketQua10 = stmt4.executeUpdate();
            }
            if (ketQua10 != 1) {
                throw new SQLException("Không tìm thấy ca.");
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
