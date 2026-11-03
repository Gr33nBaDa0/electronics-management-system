package dao;

import connectDB.ConnectDB;

import entity.NhanVien;
import entity.TaiKhoan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NhanVien_DAO {

    public List<NhanVien> getAllNhanVien() throws SQLException {
        List<NhanVien> ds = new ArrayList<>();
        String sql = "SELECT * FROM dbo.NhanVien ORDER BY MaNhanVien";
        Connection con = ConnectDB.getConnection();
        try (PreparedStatement stmt = con.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                NhanVien nhanVien = new NhanVien();
                nhanVien.setMaNhanVien(rs.getString("MaNhanVien"));
                nhanVien.setHoTen(rs.getString("HoTen"));
                nhanVien.setVaiTro(rs.getString("VaiTro"));
                nhanVien.setSdt(rs.getString("SDT"));
                nhanVien.setEmail(rs.getString("Email"));
                nhanVien.setDiaChi(rs.getString("DiaChi"));
                nhanVien.setSoCCCD(rs.getString("SoCCCD"));
                nhanVien.setHinhTheNV(rs.getBytes("HinhTheNV"));
                nhanVien.setGioiTinh(rs.getString("GioiTinh"));
                nhanVien.setNgaySinh(
                        rs.getDate("NgaySinh") == null
                                ? null
                                : rs.getDate("NgaySinh").toLocalDate());
                nhanVien.setNgayBatDauLV(
                        rs.getDate("NgayBatDauLV") == null
                                ? null
                                : rs.getDate("NgayBatDauLV").toLocalDate());
                ds.add(nhanVien);
            }
        }
        return ds;
    }

    static NhanVien tim(Connection con, String ma) throws SQLException {
        String sql = "SELECT * FROM dbo.NhanVien WHERE MaNhanVien=?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, ma);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    NhanVien nhanVien = new NhanVien();
                    nhanVien.setMaNhanVien(rs.getString("MaNhanVien"));
                    nhanVien.setHoTen(rs.getString("HoTen"));
                    nhanVien.setVaiTro(rs.getString("VaiTro"));
                    nhanVien.setSdt(rs.getString("SDT"));
                    nhanVien.setEmail(rs.getString("Email"));
                    nhanVien.setDiaChi(rs.getString("DiaChi"));
                    nhanVien.setSoCCCD(rs.getString("SoCCCD"));
                    nhanVien.setHinhTheNV(rs.getBytes("HinhTheNV"));
                    nhanVien.setGioiTinh(rs.getString("GioiTinh"));
                    nhanVien.setNgaySinh(
                            rs.getDate("NgaySinh") == null
                                    ? null
                                    : rs.getDate("NgaySinh").toLocalDate());
                    nhanVien.setNgayBatDauLV(
                            rs.getDate("NgayBatDauLV") == null
                                    ? null
                                    : rs.getDate("NgayBatDauLV").toLocalDate());
                    return nhanVien;
                } else {
                    return null;
                }
            }
        }
    }

    public boolean insertNhanVien(NhanVien x, TaiKhoan taiKhoan, TaiKhoan nguoiDung)
            throws SQLException {
        Connection con = ConnectDB.getConnection();
        boolean tuDongCommitCu = con.getAutoCommit();
        int mucCoLapCu = con.getTransactionIsolation();
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        try {
            new TaiKhoan_DAO().kiemTraQuyen(nguoiDung, true);
            if (x.getNgaySinh() == null
                    || x.getNgaySinh().isAfter(LocalDate.now().minusYears(18))) {
                throw new SQLException("Nhân viên phải đủ 18 tuổi.");
            }
            if (x.getNgayBatDauLV() == null || x.getNgayBatDauLV().isAfter(LocalDate.now())) {
                throw new SQLException("Ngày bắt đầu làm việc không hợp lệ.");
            }
            String quyen = "Quản lý".equals(x.getVaiTro()) ? "ADMIN" : "BAN_HANG";
            String sql =
                    "INSERT INTO"
                        + " dbo.NhanVien(MaNhanVien,HoTen,VaiTro,SDT,Email,DiaChi,SoCCCD,HinhTheNV,GioiTinh,NgaySinh,NgayBatDauLV)"
                        + " VALUES(?,?,?,?,?,?,?,?,?,?,?)";
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, x.getMaNhanVien());
                stmt.setString(2, x.getHoTen());
                stmt.setString(3, x.getVaiTro());
                stmt.setString(4, x.getSdt());
                stmt.setString(5, x.getEmail());
                stmt.setString(6, x.getDiaChi());
                stmt.setString(7, x.getSoCCCD());
                stmt.setBytes(8, x.getHinhTheNV());
                stmt.setString(9, x.getGioiTinh());
                stmt.setDate(10, java.sql.Date.valueOf(x.getNgaySinh()));
                stmt.setDate(11, java.sql.Date.valueOf(x.getNgayBatDauLV()));
                stmt.executeUpdate();
            }
            String sql2 =
                    "INSERT INTO dbo.TaiKhoan(TenDangNhap,Quyen,MatKhau,MaNhanVien)"
                            + " VALUES(?,?,?,?)";
            try (PreparedStatement stmt3 = con.prepareStatement(sql2)) {
                stmt3.setString(1, taiKhoan.getTenDangNhap());
                stmt3.setString(2, quyen);
                stmt3.setString(3, taiKhoan.getMatKhau());
                stmt3.setString(4, x.getMaNhanVien());
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
        return true;
    }

    public boolean deleteNhanVien(String ma, TaiKhoan tk) throws SQLException {
        Connection con = ConnectDB.getConnection();
        boolean tuDongCommitCu = con.getAutoCommit();
        int mucCoLapCu = con.getTransactionIsolation();
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        try {
            new TaiKhoan_DAO().kiemTraQuyen(tk, true);
            if (ma.equals(tk.getNhanVien().getMaNhanVien())) {
                throw new SQLException("Không xóa tài khoản đang đăng nhập.");
            }
            String sql = "DELETE FROM dbo.TaiKhoan WHERE MaNhanVien=?";
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, ma);
                stmt.executeUpdate();
            }
            String sql2 = "DELETE FROM dbo.NhanVien WHERE MaNhanVien=?";
            int ketQua6;
            try (PreparedStatement stmt3 = con.prepareStatement(sql2)) {
                stmt3.setString(1, ma);
                ketQua6 = stmt3.executeUpdate();
            }
            if (ketQua6 != 1) {
                throw new SQLException("Nhân viên không tồn tại.");
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

    public boolean updateNhanVien(NhanVien x, TaiKhoan nguoiDung) throws SQLException {
        Connection con = ConnectDB.getConnection();
        boolean tuDongCommitCu = con.getAutoCommit();
        int mucCoLapCu = con.getTransactionIsolation();
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        try {
            new TaiKhoan_DAO().kiemTraQuyen(nguoiDung, true);
            if (x.getNgaySinh() == null
                    || x.getNgaySinh().isAfter(LocalDate.now().minusYears(18))) {
                throw new SQLException("Nhân viên phải đủ 18 tuổi.");
            }
            if (x.getNgayBatDauLV() == null || x.getNgayBatDauLV().isAfter(LocalDate.now())) {
                throw new SQLException("Ngày bắt đầu làm việc không hợp lệ.");
            }
            String quyen = "Quản lý".equals(x.getVaiTro()) ? "ADMIN" : "BAN_HANG";
            if (x.getMaNhanVien().equals(nguoiDung.getNhanVien().getMaNhanVien())) {
                if (!(quyen.equals(nguoiDung.getQuyen()))) {
                    throw new SQLException("Không tự đổi quyền của mình.");
                }
            }
            String sql3 =
                    "UPDATE dbo.NhanVien SET"
                        + " HoTen=?,VaiTro=?,SDT=?,Email=?,DiaChi=?,SoCCCD=?,HinhTheNV=?,GioiTinh=?,NgaySinh=?,NgayBatDauLV=?"
                        + " WHERE MaNhanVien=?";
            int ketQua9;
            try (PreparedStatement stmt4 = con.prepareStatement(sql3)) {
                stmt4.setString(1, x.getHoTen());
                stmt4.setString(2, x.getVaiTro());
                stmt4.setString(3, x.getSdt());
                stmt4.setString(4, x.getEmail());
                stmt4.setString(5, x.getDiaChi());
                stmt4.setString(6, x.getSoCCCD());
                stmt4.setBytes(7, x.getHinhTheNV());
                stmt4.setString(8, x.getGioiTinh());
                stmt4.setDate(9, java.sql.Date.valueOf(x.getNgaySinh()));
                stmt4.setDate(10, java.sql.Date.valueOf(x.getNgayBatDauLV()));
                stmt4.setString(11, x.getMaNhanVien());
                ketQua9 = stmt4.executeUpdate();
            }
            if (ketQua9 != 1) {
                throw new SQLException("Nhân viên không tồn tại.");
            }
            String sql5 = "UPDATE dbo.TaiKhoan SET Quyen=? WHERE MaNhanVien=?";
            int ketQua12;
            try (PreparedStatement stmt6 = con.prepareStatement(sql5)) {
                stmt6.setString(1, quyen);
                stmt6.setString(2, x.getMaNhanVien());
                ketQua12 = stmt6.executeUpdate();
            }
            if (ketQua12 != 1) {
                throw new SQLException("Nhân viên chưa có tài khoản.");
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
