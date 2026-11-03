package dao;

import connectDB.ConnectDB;

import entity.SanPham;
import entity.TaiKhoan;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SanPham_DAO {

    /** Mã hiển thị trên form; không giữ chỗ trước khi người dùng Lưu. */
    public String layMaTiepTheo() throws SQLException {
        Connection con = ConnectDB.getConnection();
        return taoMaTiepTheo(con, false);
    }

    /** Khi thêm, gọi trong transaction và khóa phạm vi mã trên SQL Server. */
    private String taoMaTiepTheo(Connection con, boolean khoa) throws SQLException {
        if (con == null || con.isClosed()) throw new SQLException("Chưa kết nối SQL.");
        String sql =
                "SELECT MaSanPham FROM dbo.SanPham"
                        + (khoa ? " WITH (UPDLOCK,HOLDLOCK)" : "")
                        + " WHERE MaSanPham LIKE 'SP%' ORDER BY MaSanPham DESC";
        int lonNhat = 0;
        try (PreparedStatement stmt = con.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String ma = rs.getString("MaSanPham");
                if (ma != null && ma.matches("(?i)SP[0-9]{4}"))
                    lonNhat = Math.max(lonNhat, Integer.parseInt(ma.substring(2)));
            }
        }
        if (lonNhat >= 9999) throw new SQLException("Đã hết dải mã sản phẩm (tối đa SP9999).");
        return String.format(java.util.Locale.ROOT, "SP%04d", lonNhat + 1);
    }

    public List<SanPham> getAllSanPham() throws SQLException {
        List<SanPham> ds = new ArrayList<>();
        String sql = "SELECT * FROM dbo.SanPham ORDER BY MaSanPham";
        Connection con = ConnectDB.getConnection();
        try (PreparedStatement stmt = con.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                SanPham sanPham = new SanPham();
                sanPham.setMaSanPham(rs.getString("MaSanPham"));
                sanPham.setTenSanPham(rs.getString("TenSanPham"));
                sanPham.setLoaiHang(rs.getString("LoaiHang"));
                sanPham.setThuongHieu(rs.getString("ThuongHieu"));
                sanPham.setDonGiaBan(rs.getBigDecimal("DonGiaBan"));
                sanPham.setGiaNhap(rs.getBigDecimal("GiaNhap"));
                sanPham.setThongSoKiThuat(rs.getString("ThongSoKiThuat"));
                sanPham.setXuatXu(rs.getString("XuatXu"));
                sanPham.setTrangThai(rs.getString("TrangThai"));
                sanPham.setThoiHanBaoHanh(rs.getInt("ThoiHanBaoHanh"));
                sanPham.setSoLuongTon(rs.getInt("SoLuongTon"));
                sanPham.setHinhAnh(rs.getString("HinhAnh"));
                ds.add(sanPham);
            }
        }
        return ds;
    }

    static SanPham tim(Connection con, String ma) throws SQLException {
        String sql = "SELECT * FROM dbo.SanPham WHERE MaSanPham=?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, ma);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    SanPham sanPham = new SanPham();
                    sanPham.setMaSanPham(rs.getString("MaSanPham"));
                    sanPham.setTenSanPham(rs.getString("TenSanPham"));
                    sanPham.setLoaiHang(rs.getString("LoaiHang"));
                    sanPham.setThuongHieu(rs.getString("ThuongHieu"));
                    sanPham.setDonGiaBan(rs.getBigDecimal("DonGiaBan"));
                    sanPham.setGiaNhap(rs.getBigDecimal("GiaNhap"));
                    sanPham.setThongSoKiThuat(rs.getString("ThongSoKiThuat"));
                    sanPham.setXuatXu(rs.getString("XuatXu"));
                    sanPham.setTrangThai(rs.getString("TrangThai"));
                    sanPham.setThoiHanBaoHanh(rs.getInt("ThoiHanBaoHanh"));
                    sanPham.setSoLuongTon(rs.getInt("SoLuongTon"));
                    sanPham.setHinhAnh(rs.getString("HinhAnh"));
                    return sanPham;
                } else {
                    return null;
                }
            }
        }
    }

    public boolean insertSanPham(SanPham x, TaiKhoan tk) throws SQLException {
        Connection con = ConnectDB.getConnection();
        boolean tuDongCommitCu = con.getAutoCommit();
        int mucCoLapCu = con.getTransactionIsolation();
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        try {
            new TaiKhoan_DAO().kiemTraQuyen(tk, true);
            String maMoi = taoMaTiepTheo(con, true);
            if (x.getDonGiaBan().signum() < 0
                    || x.getGiaNhap().signum() < 0
                    || x.getThoiHanBaoHanh() < 0) {
                throw new SQLException("Giá / bảo hành không được âm.");
            }
            String sql =
                    "INSERT INTO dbo.SanPham"
                        + " (MaSanPham,TenSanPham,LoaiHang,ThuongHieu,DonGiaBan,GiaNhap,ThongSoKiThuat,XuatXu,TrangThai,ThoiHanBaoHanh,SoLuongTon,HinhAnh)"
                        + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, maMoi);
                stmt.setString(2, x.getTenSanPham());
                stmt.setString(3, x.getLoaiHang());
                stmt.setString(4, x.getThuongHieu());
                stmt.setBigDecimal(5, x.getDonGiaBan());
                stmt.setBigDecimal(6, x.getGiaNhap());
                stmt.setString(7, x.getThongSoKiThuat());
                stmt.setString(8, x.getXuatXu());
                stmt.setString(9, x.getTrangThai());
                stmt.setInt(10, x.getThoiHanBaoHanh());
                stmt.setInt(11, 0);
                stmt.setString(12, x.getHinhAnh());
                stmt.executeUpdate();
            }
            con.commit();
            x.setMaSanPham(maMoi);
        } catch (SQLException | RuntimeException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(tuDongCommitCu);
            con.setTransactionIsolation(mucCoLapCu);
        }
        return true;
    }

    public boolean deleteSanPham(String ma, TaiKhoan tk) throws SQLException {
        Connection con = ConnectDB.getConnection();
        boolean tuDongCommitCu = con.getAutoCommit();
        int mucCoLapCu = con.getTransactionIsolation();
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        try {
            new TaiKhoan_DAO().kiemTraQuyen(tk, true);
            String sql =
                    "SELECT SoLuongTon FROM dbo.SanPham WITH (UPDLOCK,HOLDLOCK) WHERE MaSanPham=?";
            BigDecimal ketQua3 = BigDecimal.ZERO;
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, ma);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        ketQua3 = rs.getBigDecimal(1);
                        if (ketQua3 == null) ketQua3 = BigDecimal.ZERO;
                    }
                }
            }
            if (ketQua3.signum() != 0) {
                throw new SQLException("Chỉ xóa sản phẩm tồn 0, chưa có lịch sử.");
            }
            String sql2 = "DELETE FROM dbo.SanPham WHERE MaSanPham=?";
            int ketQua7;
            try (PreparedStatement stmt3 = con.prepareStatement(sql2)) {
                stmt3.setString(1, ma);
                ketQua7 = stmt3.executeUpdate();
            }
            if (ketQua7 != 1) {
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

    public static void doiTon(Connection con, String ma, int thayDoi) throws SQLException {
        String sql =
                "UPDATE dbo.SanPham SET SoLuongTon=SoLuongTon+? WHERE MaSanPham=? AND"
                        + " SoLuongTon+?>=0";
        int ketQua3;
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, thayDoi);
            stmt.setString(2, ma);
            stmt.setInt(3, thayDoi);
            ketQua3 = stmt.executeUpdate();
        }
        if (ketQua3 != 1) {
            throw new SQLException("Không đủ tồn kho hoặc sản phẩm không tồn tại: " + ma);
        }
    }

    public boolean updateSanPham(SanPham x, TaiKhoan tk) throws SQLException {
        Connection con = ConnectDB.getConnection();
        boolean tuDongCommitCu = con.getAutoCommit();
        int mucCoLapCu = con.getTransactionIsolation();
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        try {
            new TaiKhoan_DAO().kiemTraQuyen(tk, true);
            if (x.getDonGiaBan().signum() < 0
                    || x.getGiaNhap().signum() < 0
                    || x.getThoiHanBaoHanh() < 0) {
                throw new SQLException("Giá / bảo hành không được âm.");
            }
            String sql2 =
                    "UPDATE dbo.SanPham SET"
                        + " TenSanPham=?,LoaiHang=?,ThuongHieu=?,DonGiaBan=?,GiaNhap=?,ThongSoKiThuat=?,XuatXu=?,TrangThai=?,ThoiHanBaoHanh=?,HinhAnh=?"
                        + " WHERE MaSanPham=?";
            int ketQua6;
            try (PreparedStatement stmt3 = con.prepareStatement(sql2)) {
                stmt3.setString(1, x.getTenSanPham());
                stmt3.setString(2, x.getLoaiHang());
                stmt3.setString(3, x.getThuongHieu());
                stmt3.setBigDecimal(4, x.getDonGiaBan());
                stmt3.setBigDecimal(5, x.getGiaNhap());
                stmt3.setString(6, x.getThongSoKiThuat());
                stmt3.setString(7, x.getXuatXu());
                stmt3.setString(8, x.getTrangThai());
                stmt3.setInt(9, x.getThoiHanBaoHanh());
                stmt3.setString(10, x.getHinhAnh());
                stmt3.setString(11, x.getMaSanPham());
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
