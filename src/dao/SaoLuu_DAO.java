package dao;

import connectDB.ConnectDB;

import entity.TaiKhoan;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SaoLuu_DAO {

    public String saoLuu(String duongDan, TaiKhoan tk) throws SQLException {
        if (duongDan == null || !(duongDan.endsWith(".bak")) || duongDan.contains("'")) {
            throw new SQLException("Nhập đường dẫn .bak trên máy SQL Server.");
        }
        Connection con = ConnectDB.getConnection();
        new TaiKhoan_DAO().kiemTraQuyen(tk, true);
        String db = con.getCatalog();
        if (db == null || !(db.matches("[A-Za-z0-9_]+"))) {
            throw new SQLException("Tên database không hợp lệ.");
        }
        try (Statement s = con.createStatement()) {
            s.setQueryTimeout(180);
            s.execute(
                    "BACKUP DATABASE ["
                            + db
                            + "] TO DISK=N'"
                            + duongDan
                            + "' WITH COPY_ONLY, CHECKSUM");
        }
        verify(con, duongDan);
        return duongDan;
    }

    public void kiemTra(String path, TaiKhoan tk) throws SQLException {
        if (path == null || !(path.endsWith(".bak")) || path.contains("'")) {
            throw new SQLException("Đường dẫn .bak không hợp lệ.");
        }
        Connection con = ConnectDB.getConnection();
        new TaiKhoan_DAO().kiemTraQuyen(tk, true);
        verify(con, path);
    }

    private void verify(Connection con, String path) throws SQLException {
        int last = 0;
        try (Statement s = con.createStatement()) {
            s.setQueryTimeout(180);
            try (ResultSet r = s.executeQuery("RESTORE HEADERONLY FROM DISK=N'" + path + "'")) {
                while (r.next()) {
                    last = Math.max(last, r.getInt("Position"));
                }
            }
            if (last <= 0) {
                throw new SQLException("File chưa có backup set.");
            }
            s.execute(
                    "RESTORE VERIFYONLY FROM DISK=N'" + path + "' WITH FILE=" + last + ",CHECKSUM");
        }
    }
}
