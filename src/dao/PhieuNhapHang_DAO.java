package dao;

import connectDB.ConnectDB;

import entity.ChiTietPhieuNhapHang;
import entity.NhanVien;
import entity.PhieuNhapHang;
import entity.TaiKhoan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PhieuNhapHang_DAO {

    public List<PhieuNhapHang> getAllPhieuNhapHang() throws SQLException {
        List<PhieuNhapHang> ds = new ArrayList<>();
        Connection con = ConnectDB.getConnection();
        String sql =
                "SELECT p.*, nvLap.HoTen AS TenNguoiLap, nvDuyet.HoTen AS TenNguoiDuyet "
                        + "FROM dbo.PhieuNhapHang p "
                        + "LEFT JOIN dbo.NhanVien nvLap ON nvLap.MaNhanVien = p.MaNhanVienLap "
                        + "LEFT JOIN dbo.NhanVien nvDuyet ON nvDuyet.MaNhanVien = p.MaQuanLyDuyet "
                        + "ORDER BY p.NgayLap DESC";
        try (PreparedStatement s = con.prepareStatement(sql);
                ResultSet r = s.executeQuery()) {
            while (r.next()) {
                PhieuNhapHang p = new PhieuNhapHang(r.getString("MaPhieuNhap"));
                Timestamp thoiGian2 = r.getTimestamp("NgayLap");
                p.setNgayLap(thoiGian2 == null ? null : thoiGian2.toLocalDateTime());
                p.setSoChungTu(r.getString("SoChungTu"));
                p.setNhaCungCap(r.getString("NhaCungCap"));
                p.setNguoiGiao(r.getString("NguoiGiao"));

                NhanVien nvLap = new NhanVien(r.getString("MaNhanVienLap"));
                nvLap.setHoTen(r.getString("TenNguoiLap"));
                p.setNhanVienLap(nvLap);

                String q = r.getString("MaQuanLyDuyet");
                if (q != null) {
                    NhanVien nvDuyet = new NhanVien(q);
                    nvDuyet.setHoTen(r.getString("TenNguoiDuyet"));
                    p.setQuanLyDuyet(nvDuyet);
                } else {
                    p.setQuanLyDuyet(null);
                }

                p.setTrangThai(r.getString("TrangThai"));
                Timestamp thoiGian3 = r.getTimestamp("NgayDuyet");
                p.setNgayDuyet(thoiGian3 == null ? null : thoiGian3.toLocalDateTime());
                p.setLyDoTuChoi(r.getString("LyDoTuChoi"));
                ds.add(p);
            }
        }
        return ds;
    }

    public String luu(PhieuNhapHang p, List<ChiTietPhieuNhapHang> ds, TaiKhoan tk)
            throws SQLException {
        Connection con = ConnectDB.getConnection();
        boolean tuDongCommitCu = con.getAutoCommit();
        int mucCoLapCu = con.getTransactionIsolation();
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        try {
            new TaiKhoan_DAO().kiemTraQuyen(tk, false);
            if (ds.isEmpty() || p.getNhaCungCap().trim().isEmpty()) {
                throw new SQLException("Cần nhà cung cấp và dòng nhập.");
            }
            String id = p.getMaPhieuNhap();
            if (id == null) {
                id =
                        "PN"
                                + java.util
                                        .UUID
                                        .randomUUID()
                                        .toString()
                                        .replace("-", "")
                                        .substring(0, 16);
                String sql =
                        "INSERT INTO"
                            + " dbo.PhieuNhapHang(MaPhieuNhap,SoChungTu,NhaCungCap,NguoiGiao,MaNhanVienLap)"
                            + " VALUES(?,?,?,?,?)";
                try (PreparedStatement stmt = con.prepareStatement(sql)) {
                    stmt.setString(1, id);
                    stmt.setString(2, p.getSoChungTu());
                    stmt.setString(3, p.getNhaCungCap());
                    stmt.setString(4, p.getNguoiGiao());
                    stmt.setString(5, tk.getNhanVien().getMaNhanVien());
                    stmt.executeUpdate();
                }
            } else {
                String sql2 =
                        "UPDATE dbo.PhieuNhapHang SET SoChungTu=?,NhaCungCap=?,NguoiGiao=? WHERE"
                                + " MaPhieuNhap=? AND MaNhanVienLap=? AND TrangThai=N'Chờ duyệt'";
                int ketQua6;
                try (PreparedStatement stmt3 = con.prepareStatement(sql2)) {
                    stmt3.setString(1, p.getSoChungTu());
                    stmt3.setString(2, p.getNhaCungCap());
                    stmt3.setString(3, p.getNguoiGiao());
                    stmt3.setString(4, id);
                    stmt3.setString(5, tk.getNhanVien().getMaNhanVien());
                    ketQua6 = stmt3.executeUpdate();
                }
                if (ketQua6 != 1) {
                    throw new SQLException("Chỉ sửa phiếu chờ duyệt của mình.");
                }
                String sql3 = "DELETE FROM dbo.ChiTietPhieuNhapHang WHERE MaPhieuNhap=?";
                try (PreparedStatement stmt4 = con.prepareStatement(sql3)) {
                    stmt4.setString(1, id);
                    stmt4.executeUpdate();
                }
            }
            new ChiTietPhieuNhapHang_DAO().ghi(con, id, ds);
            con.commit();
            return id;
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
            new TaiKhoan_DAO().kiemTraQuyen(tk, false);
            String sql =
                    "SELECT MaPhieuNhap FROM dbo.PhieuNhapHang WITH (UPDLOCK,HOLDLOCK) WHERE"
                            + " MaPhieuNhap=? AND MaNhanVienLap=? AND TrangThai=N'Chờ duyệt'";
            String ketQua3 = null;
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, id);
                stmt.setString(2, tk.getNhanVien().getMaNhanVien());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        ketQua3 = rs.getString(1);
                    }
                }
            }
            if (ketQua3 == null) {
                throw new SQLException("Chỉ xóa phiếu chờ của mình.");
            }
            String sql2 = "DELETE FROM dbo.ChiTietPhieuNhapHang WHERE MaPhieuNhap=?";
            try (PreparedStatement stmt3 = con.prepareStatement(sql2)) {
                stmt3.setString(1, id);
                stmt3.executeUpdate();
            }
            String sql3 = "DELETE FROM dbo.PhieuNhapHang WHERE MaPhieuNhap=?";
            try (PreparedStatement stmt4 = con.prepareStatement(sql3)) {
                stmt4.setString(1, id);
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

    public void duyet(String id, boolean dongY, String lyDo, TaiKhoan tk) throws SQLException {
        Connection con = ConnectDB.getConnection();
        boolean tuDongCommitCu = con.getAutoCommit();
        int mucCoLapCu = con.getTransactionIsolation();
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        try {
            new TaiKhoan_DAO().kiemTraQuyen(tk, true);
            String sql =
                    "SELECT MaNhanVienLap FROM dbo.PhieuNhapHang WITH (UPDLOCK,HOLDLOCK) WHERE"
                            + " MaPhieuNhap=? AND TrangThai=N'Chờ duyệt'";
            String ketQua3 = null;
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        ketQua3 = rs.getString(1);
                    }
                }
            }
            String nguoi = ketQua3;
            if (nguoi == null) {
                throw new SQLException("Phiếu không còn chờ duyệt.");
            }
            if (nguoi.equals(tk.getNhanVien().getMaNhanVien())) {
                throw new SQLException("Người lập không tự duyệt phiếu nhập.");
            }
            if (!dongY && (lyDo == null || lyDo.trim().isEmpty())) {
                throw new SQLException("Từ chối phải có lý do.");
            }
            if (dongY) {
                String sql2 =
                        "SELECT * FROM dbo.ChiTietPhieuNhapHang WHERE MaPhieuNhap=? ORDER BY"
                                + " MaSanPham";
                try (PreparedStatement s = con.prepareStatement(sql2)) {
                    s.setString(1, id);
                    try (ResultSet r = s.executeQuery()) {
                        int n = 0;
                        while (r.next()) {
                            if (r.getInt("SoLuong") <= 0) {
                                throw new SQLException("Số lượng nhập không hợp lệ.");
                            }
                            SanPham_DAO.doiTon(con, r.getString("MaSanPham"), r.getInt("SoLuong"));
                            n++;
                        }
                        if (n <= 0) {
                            throw new SQLException("Phiếu nhập chưa có chi tiết.");
                        }
                    }
                }
            }
            String sql3 =
                    "UPDATE dbo.PhieuNhapHang SET"
                        + " TrangThai=?,MaQuanLyDuyet=?,NgayDuyet=SYSDATETIME(),LyDoTuChoi=? WHERE"
                        + " MaPhieuNhap=?";
            try (PreparedStatement stmt3 = con.prepareStatement(sql3)) {
                stmt3.setString(1, dongY ? "Đã nhập" : "Từ chối");
                stmt3.setString(2, tk.getNhanVien().getMaNhanVien());
                stmt3.setString(3, dongY ? null : lyDo);
                stmt3.setString(4, id);
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
}
