package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.NhanVien;
import entity.TaiKhoan;

public class TaiKhoan_DAO {
	public List<TaiKhoan> getAllTaiKhoan() throws SQLException {
        List<TaiKhoan> danhSach = new ArrayList<>();
        String sql = "SELECT tk.*, nv.HoTen, nv.VaiTro, nv.SDT, nv.Email, nv.DiaChi, "
                + "nv.SoCCCD, nv.HinhTheNV, nv.GioiTinh, nv.NgaySinh, nv.NgayBatDauLV "
                + "FROM dbo.TaiKhoan tk JOIN dbo.NhanVien nv "
                + "ON tk.MaNhanVien = nv.MaNhanVien ORDER BY tk.TenDangNhap";
        Connection con = ConnectDB.getConnection();
        try (PreparedStatement stmt = con.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String user = rs.getString("TenDangNhap");
                String pass = rs.getString("MatKhau");
                String quyen = rs.getString("Quyen");
                String maNhanVien = rs.getString("MaNhanVien");
                NhanVien nv = new NhanVien(maNhanVien);
                nv.setHoTen(rs.getString("HoTen"));
                nv.setVaiTro(rs.getString("VaiTro"));
                nv.setSdt(rs.getString("SDT"));
                nv.setEmail(rs.getString("Email"));
                nv.setDiaChi(rs.getString("DiaChi"));
                nv.setSoCCCD(rs.getString("SoCCCD"));
                nv.setHinhTheNV(rs.getBytes("HinhTheNV"));
                nv.setGioiTinh(rs.getString("GioiTinh"));
                Date ngaySinh = rs.getDate("NgaySinh");
                Date ngayBatDau = rs.getDate("NgayBatDauLV");
                nv.setNgaySinh(ngaySinh == null ? null : ngaySinh.toLocalDate());
                nv.setNgayBatDauLV(ngayBatDau == null ? null : ngayBatDau.toLocalDate());
                danhSach.add(new TaiKhoan(user, quyen, pass, nv));
            }
        }
        return danhSach;
    }
	
    public TaiKhoan kiemTraDangNhap(String tenDangNhap, String matKhau) throws SQLException {
        String sql = "SELECT tk.*, nv.HoTen, nv.VaiTro, nv.SDT, nv.Email, nv.DiaChi, "
                + "nv.SoCCCD, nv.HinhTheNV, nv.GioiTinh, nv.NgaySinh, nv.NgayBatDauLV "
                + "FROM dbo.TaiKhoan tk JOIN dbo.NhanVien nv "
                + "ON tk.MaNhanVien = nv.MaNhanVien "
                + "WHERE tk.TenDangNhap = ? AND tk.MatKhau = ?";
        Connection con = ConnectDB.getConnection();
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, tenDangNhap.trim());
            stmt.setString(2, matKhau);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next() || !matKhau.equals(rs.getString("MatKhau"))) {
                    return null;
                }
                String user = rs.getString("TenDangNhap");
                String pass = rs.getString("MatKhau");
                String quyen = rs.getString("Quyen");
                String maNhanVien = rs.getString("MaNhanVien");
                NhanVien nv = new NhanVien(maNhanVien);
                nv.setHoTen(rs.getString("HoTen"));
                nv.setVaiTro(rs.getString("VaiTro"));
                nv.setSdt(rs.getString("SDT"));
                nv.setEmail(rs.getString("Email"));
                nv.setDiaChi(rs.getString("DiaChi"));
                nv.setSoCCCD(rs.getString("SoCCCD"));
                nv.setHinhTheNV(rs.getBytes("HinhTheNV"));
                nv.setGioiTinh(rs.getString("GioiTinh"));
                Date ngaySinh = rs.getDate("NgaySinh");
                Date ngayBatDau = rs.getDate("NgayBatDauLV");
                nv.setNgaySinh(ngaySinh == null ? null : ngaySinh.toLocalDate());
                nv.setNgayBatDauLV(ngayBatDau == null ? null : ngayBatDau.toLocalDate());
                TaiKhoan tk = new TaiKhoan(user, quyen, pass, nv);
                String vaiTro = tk.getNhanVien().getVaiTro();
                boolean hopLe = ("ADMIN".equals(tk.getQuyen()) && "Quản lý".equals(vaiTro))
                        || ("BAN_HANG".equals(tk.getQuyen()) && "Bán hàng".equals(vaiTro));
                if (!hopLe) {
                    throw new SQLException("Quyền tài khoản chưa khớp vai trò nhân viên.");
                }
                return tk;
            }
        }
    }


    public boolean insertTaiKhoan(TaiKhoan tk) throws SQLException {
        String sql = "INSERT INTO dbo.TaiKhoan (TenDangNhap, MatKhau, Quyen, MaNhanVien) "
                + "VALUES (?, ?, ?, ?)";
        Connection con = ConnectDB.getConnection();
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, tk.getTenDangNhap().trim());
            stmt.setString(2, tk.getMatKhau());
            stmt.setString(3, tk.getQuyen());
            stmt.setString(4, tk.getNhanVien().getMaNhanVien().trim());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateMatKhau(String tenDangNhap, String matKhauMoi) throws SQLException {
        String sql = "UPDATE dbo.TaiKhoan SET MatKhau = ? WHERE TenDangNhap = ?";
        Connection con = ConnectDB.getConnection();
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, matKhauMoi);
            stmt.setString(2, tenDangNhap.trim());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateQuyen(String tenDangNhap, String quyenMoi) throws SQLException {
        String sql = "UPDATE dbo.TaiKhoan SET Quyen = ? WHERE TenDangNhap = ?";
        Connection con = ConnectDB.getConnection();
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, quyenMoi);
            stmt.setString(2, tenDangNhap.trim());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteTaiKhoan(String tenDangNhap) throws SQLException {
        String sql = "DELETE FROM dbo.TaiKhoan WHERE TenDangNhap = ?";
        Connection con = ConnectDB.getConnection();
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, tenDangNhap.trim());
            return stmt.executeUpdate() > 0;
        }
    }


    public boolean kiemTraTrungTenDangNhap(String tenDangNhap) throws SQLException {
        Connection con = ConnectDB.getConnection();
        return kiemTraTrungTenDangNhap(con, tenDangNhap);
    }

    public boolean kiemTraTrungTenDangNhap(Connection con, String tenDangNhap) throws SQLException {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) return false;
        String sql = "SELECT COUNT(*) FROM dbo.TaiKhoan WHERE TenDangNhap = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, tenDangNhap.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean kiemTraThongTinKhoiPhuc(String tenDangNhap, String sdt, String soCCCD) throws SQLException {
        Connection con = ConnectDB.getConnection();
        String sql = "SELECT COUNT(*) FROM dbo.TaiKhoan tk "
                + "JOIN dbo.NhanVien nv ON tk.MaNhanVien = nv.MaNhanVien "
                + "WHERE tk.TenDangNhap = ? AND nv.SDT = ? AND nv.SoCCCD = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, tenDangNhap.trim());
            stmt.setString(2, sdt.trim());
            stmt.setString(3, soCCCD.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean datLaiMatKhauQuen(String tenDangNhap, String sdt, String soCCCD, String matKhauMoi) throws SQLException {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            throw new SQLException("Vui lòng nhập tên đăng nhập.");
        }
        if (sdt == null || sdt.trim().isEmpty()) {
            throw new SQLException("Vui lòng nhập số điện thoại.");
        }
        if (soCCCD == null || soCCCD.trim().isEmpty()) {
            throw new SQLException("Vui lòng nhập số CCCD.");
        }
        if (!kiemTraThongTinKhoiPhuc(tenDangNhap, sdt, soCCCD)) {
            throw new SQLException("Thông tin xác minh không chính xác (Tên đăng nhập, SĐT hoặc CCCD không khớp).");
        }
        if (matKhauMoi == null || matKhauMoi.length() < 8) {
            throw new SQLException("Mật khẩu mới phải có ít nhất 8 ký tự.");
        }
        if (matKhauMoi.length() > 128) {
            throw new SQLException("Mật khẩu mới tối đa 128 ký tự.");
        }
        return updateMatKhau(tenDangNhap, matKhauMoi);
    }

    // Các màn hình mới dùng lại cách truy vấn ở các phương thức phía trên.
    public void kiemTraQuyen(TaiKhoan taiKhoan, boolean canQuanLy) throws SQLException {
        if (taiKhoan == null || taiKhoan.getNhanVien() == null) {
            throw new SQLException("Vui lòng đăng nhập trước.");
        }
        TaiKhoan hienTai = kiemTraDangNhap(taiKhoan.getTenDangNhap(), taiKhoan.getMatKhau());
        if (hienTai == null
                || !hienTai.getNhanVien().getMaNhanVien().equals(taiKhoan.getNhanVien().getMaNhanVien())
                || !hienTai.getQuyen().equals(taiKhoan.getQuyen())) {
            throw new SQLException("Thông tin tài khoản đã thay đổi. Vui lòng đăng nhập lại.");
        }
        if (canQuanLy && !"ADMIN".equals(hienTai.getQuyen())) {
            throw new SQLException("Chỉ quản lý được thực hiện chức năng này.");
        }
    }

    public List<TaiKhoan> getAllTaiKhoan(TaiKhoan taiKhoan) throws SQLException {
        kiemTraQuyen(taiKhoan, true);
        return getAllTaiKhoan();
    }

    public void updateMatKhau(String tenDangNhap, String matKhauMoi, TaiKhoan taiKhoan)
            throws SQLException {
        kiemTraQuyen(taiKhoan, !tenDangNhap.equals(taiKhoan.getTenDangNhap()));
        if (matKhauMoi == null || matKhauMoi.isEmpty() || matKhauMoi.length() > 128) {
            throw new SQLException("Mật khẩu từ 1 đến 128 ký tự.");
        }
        if (!updateMatKhau(tenDangNhap, matKhauMoi)) {
            throw new SQLException("Không tìm thấy tài khoản cần đổi mật khẩu.");
        }
        if (tenDangNhap.equals(taiKhoan.getTenDangNhap())) {
            taiKhoan.setMatKhau(matKhauMoi);
        }
    }
}

