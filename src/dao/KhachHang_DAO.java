package dao;

import connectDB.ConnectDB;

import entity.KhachHang;
import entity.TaiKhoan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KhachHang_DAO {

    /** Mã hiển thị trên form; không giữ chỗ trước khi người dùng Lưu. */
    public String layMaTiepTheo() throws SQLException {
        Connection con = ConnectDB.getConnection();
        return taoMaTiepTheo(con, false);
    }

    /** Khi thêm, gọi trong transaction và khóa phạm vi mã trên SQL Server. */
    private String taoMaTiepTheo(Connection con, boolean khoa) throws SQLException {
        if (con == null || con.isClosed()) throw new SQLException("Chưa kết nối SQL.");
        String sql =
                "SELECT MaKhachHang FROM dbo.KhachHang"
                        + (khoa ? " WITH (UPDLOCK,HOLDLOCK)" : "")
                        + " WHERE MaKhachHang LIKE 'KH%' ORDER BY MaKhachHang DESC";
        int lonNhat = 0;
        try (PreparedStatement stmt = con.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String ma = rs.getString("MaKhachHang");
                if (ma != null && ma.matches("(?i)KH[0-9]{3}"))
                    lonNhat = Math.max(lonNhat, Integer.parseInt(ma.substring(2)));
            }
        }
        if (lonNhat >= 999) throw new SQLException("Đã hết dải mã khách hàng (tối đa KH999).");
        return String.format(java.util.Locale.ROOT, "KH%03d", lonNhat + 1);
    }

    public List<KhachHang> getAllKhachHang() throws SQLException {
        List<KhachHang> ds = new ArrayList<>();
        String sql = "SELECT * FROM dbo.KhachHang ORDER BY MaKhachHang";
        Connection con = ConnectDB.getConnection();
        try (PreparedStatement stmt = con.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                KhachHang khachHang = new KhachHang();
                khachHang.setMaKhachHang(rs.getString("MaKhachHang"));
                khachHang.setHoTen(rs.getString("HoTen"));
                khachHang.setSoDienThoai(rs.getString("SoDienThoai"));
                khachHang.setDiaChi(rs.getString("DiaChi"));
                khachHang.setDiemTichLuy(rs.getInt("DiemTichLuy"));
                khachHang.setEmail(rs.getString("Email"));
                ds.add(khachHang);
            }
        }
        return ds;
    }

    static KhachHang tim(Connection con, String ma) throws SQLException {
        String sql = "SELECT * FROM dbo.KhachHang WHERE MaKhachHang=?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, ma);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    KhachHang khachHang = new KhachHang();
                    khachHang.setMaKhachHang(rs.getString("MaKhachHang"));
                    khachHang.setHoTen(rs.getString("HoTen"));
                    khachHang.setSoDienThoai(rs.getString("SoDienThoai"));
                    khachHang.setDiaChi(rs.getString("DiaChi"));
                    khachHang.setDiemTichLuy(rs.getInt("DiemTichLuy"));
                    khachHang.setEmail(rs.getString("Email"));
                    return khachHang;
                } else {
                    return null;
                }
            }
        }
    }

    public boolean insertKhachHang(KhachHang x, TaiKhoan tk) throws SQLException {
        Connection con = ConnectDB.getConnection();
        boolean tuDongCommitCu = con.getAutoCommit();
        int mucCoLapCu = con.getTransactionIsolation();
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        try {
            new TaiKhoan_DAO().kiemTraQuyen(tk, false);
            String maMoi = taoMaTiepTheo(con, true);
            String sql =
                    "INSERT INTO dbo.KhachHang"
                            + " (MaKhachHang,HoTen,SoDienThoai,DiaChi,DiemTichLuy,Email) VALUES"
                            + " (?,?,?,?,?,?)";
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, maMoi);
                stmt.setString(2, x.getHoTen());
                stmt.setString(3, x.getSoDienThoai());
                stmt.setString(4, x.getDiaChi());
                stmt.setInt(5, 0);
                stmt.setString(6, x.getEmail());
                stmt.executeUpdate();
            }
            con.commit();
            x.setMaKhachHang(maMoi);
        } catch (SQLException | RuntimeException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(tuDongCommitCu);
            con.setTransactionIsolation(mucCoLapCu);
        }
        return true;
    }

    public boolean deleteKhachHang(String ma, TaiKhoan tk) throws SQLException {
        Connection con = ConnectDB.getConnection();
        boolean tuDongCommitCu = con.getAutoCommit();
        int mucCoLapCu = con.getTransactionIsolation();
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        try {
            new TaiKhoan_DAO().kiemTraQuyen(tk, false);
            String sql = "DELETE FROM dbo.KhachHang WHERE MaKhachHang=?";
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

    public static void doiDiem(Connection con, String ma, int thayDoi) throws SQLException {
        String sql =
                "UPDATE dbo.KhachHang SET DiemTichLuy=DiemTichLuy+? WHERE MaKhachHang=? AND"
                        + " DiemTichLuy+?>=0";
        int ketQua3;
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, thayDoi);
            stmt.setString(2, ma);
            stmt.setInt(3, thayDoi);
            ketQua3 = stmt.executeUpdate();
        }
        if (ketQua3 != 1) {
            throw new SQLException(
                    "Không đủ điểm để sử dụng / thu hồi. Vui lòng xử lý điểm trước.");
        }
    }

    public boolean updateKhachHang(KhachHang x, TaiKhoan tk) throws SQLException {
        Connection con = ConnectDB.getConnection();
        boolean tuDongCommitCu = con.getAutoCommit();
        int mucCoLapCu = con.getTransactionIsolation();
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        try {
            new TaiKhoan_DAO().kiemTraQuyen(tk, false);
            String sql2 =
                    "UPDATE dbo.KhachHang SET HoTen=?,SoDienThoai=?,DiaChi=?,Email=? WHERE"
                            + " MaKhachHang=?";
            int ketQua6;
            try (PreparedStatement stmt3 = con.prepareStatement(sql2)) {
                stmt3.setString(1, x.getHoTen());
                stmt3.setString(2, x.getSoDienThoai());
                stmt3.setString(3, x.getDiaChi());
                stmt3.setString(4, x.getEmail());
                stmt3.setString(5, x.getMaKhachHang());
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
