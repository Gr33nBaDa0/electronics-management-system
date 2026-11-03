package dao;

import connectDB.ConnectDB;

import entity.TaiKhoan;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Dashboard chỉ đọc dữ liệu. Không thêm bảng hoặc thay đổi chứng từ. */
public class Dashboard_DAO {
    public static final int NGUONG_TON_THAP = 5;

    /**
     * [thu, hoàn, số HĐ đã thanh toán, đơn chưa TT, nhập chờ, trả chờ, trả đã duyệt, SP tồn thấp].
     */
    public Object[] tongHopNgay(TaiKhoan tk, LocalDate ngay, boolean caNhan) throws SQLException {
        Connection con = ConnectDB.getConnection();
        new TaiKhoan_DAO().kiemTraQuyen(tk, false);
        boolean tatCa = "ADMIN".equals(tk.getQuyen()) && !caNhan;
        String nv = tk.getNhanVien().getMaNhanVien();
        Object[] kq = {0.0, 0.0, 0, 0, 0, 0, 0, 0};
        String sql =
                "SELECT COUNT(*),COALESCE(SUM(x.Tien),0) FROM (SELECT"
                    + " h.MaHoaDon,ROUND((SUM(d.SoLuong*d.DonGia-d.GiamGia)-h.DiemSuDung*1000)*(1+h.ThueSuatVAT),2)"
                    + " Tien FROM dbo.HoaDon h JOIN dbo.ChiTietHoaDon d ON d.MaHoaDon=h.MaHoaDon"
                    + " JOIN dbo.PhanCongCa ca ON ca.MaPhanCong=h.MaPhanCong WHERE"
                    + " h.TrangThaiThanhToan=N'Đã thanh toán' AND ca.Ngay=? AND (?=1 OR"
                    + " h.MaNhanVien=?) GROUP BY h.MaHoaDon,h.DiemSuDung,h.ThueSuatVAT) x";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(ngay));
            stmt.setInt(2, tatCa ? 1 : 0);
            stmt.setString(3, nv);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    kq[2] = rs.getInt(1);
                    kq[0] = rs.getDouble(2);
                }
            }
        }
        String hoan =
                "SELECT COALESCE(SUM(d.TienHoan),0) FROM dbo.PhieuDoiTra p JOIN dbo.ChiTietDoiTra d"
                    + " ON d.MaPhieuDoiTra=p.MaPhieuDoiTra JOIN dbo.PhanCongCa ca ON"
                    + " ca.MaPhanCong=p.MaPhanCongHoanTien WHERE p.TrangThai=N'Hoàn tất' AND"
                    + " ca.Ngay=? AND (?=1 OR ca.MaNhanVien=?)";
        try (PreparedStatement stmt = con.prepareStatement(hoan)) {
            stmt.setDate(1, Date.valueOf(ngay));
            stmt.setInt(2, tatCa ? 1 : 0);
            stmt.setString(3, nv);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) kq[1] = rs.getDouble(1);
            }
        }
        kq[3] =
                dem(
                        con,
                        "SELECT COUNT(*) FROM dbo.HoaDon WHERE TrangThaiThanhToan=N'Chưa thanh"
                            + " toán' AND (?=1 OR MaNhanVien=?)",
                        tatCa,
                        nv);
        kq[4] =
                dem(
                        con,
                        "SELECT COUNT(*) FROM dbo.PhieuNhapHang WHERE TrangThai=N'Chờ duyệt' AND"
                            + " (?=1 OR MaNhanVienLap=?)",
                        tatCa,
                        nv);
        kq[5] =
                dem(
                        con,
                        "SELECT COUNT(*) FROM dbo.PhieuDoiTra WHERE TrangThai=N'Chờ duyệt' AND (?=1"
                            + " OR MaNhanVienLap=?)",
                        tatCa,
                        nv);
        kq[6] =
                dem(
                        con,
                        "SELECT COUNT(*) FROM dbo.PhieuDoiTra WHERE TrangThai=N'Đã duyệt' AND (?=1"
                            + " OR MaNhanVienLap=?)",
                        tatCa,
                        nv);
        String ton =
                "SELECT COUNT(*) FROM dbo.SanPham WHERE TrangThai=N'Đang bán' AND SoLuongTon<=?";
        try (PreparedStatement stmt = con.prepareStatement(ton)) {
            stmt.setInt(1, NGUONG_TON_THAP);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) kq[7] = rs.getInt(1);
            }
        }
        return kq;
    }

    private int dem(Connection con, String sql, boolean tatCa, String nv) throws SQLException {
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, tatCa ? 1 : 0);
            stmt.setString(2, nv);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /** [loại, mã, NV lập, ngày lập, giá trị dự kiến]. */
    public List<Object[]> phieuChoDuyet(TaiKhoan tk) throws SQLException {
        Connection con = ConnectDB.getConnection();
        new TaiKhoan_DAO().kiemTraQuyen(tk, true);
        List<Object[]> ds = new ArrayList<>();
        String sql =
                "SELECT N'Nhập hàng' Loai,p.MaPhieuNhap Ma,p.MaNhanVienLap NV,p.NgayLap"
                    + " Ngay,COALESCE(SUM(d.SoLuong*d.DonGiaNhap),0) Tien FROM dbo.PhieuNhapHang p"
                    + " LEFT JOIN dbo.ChiTietPhieuNhapHang d ON d.MaPhieuNhap=p.MaPhieuNhap WHERE"
                    + " p.TrangThai=N'Chờ duyệt' GROUP BY p.MaPhieuNhap,p.MaNhanVienLap,p.NgayLap"
                    + " UNION ALL SELECT N'Đổi"
                    + " trả',p.MaPhieuDoiTra,p.MaNhanVienLap,p.NgayLap,COALESCE(SUM(d.TienHoan),0)"
                    + " FROM dbo.PhieuDoiTra p LEFT JOIN dbo.ChiTietDoiTra d ON"
                    + " d.MaPhieuDoiTra=p.MaPhieuDoiTra WHERE p.TrangThai=N'Chờ duyệt' GROUP BY"
                    + " p.MaPhieuDoiTra,p.MaNhanVienLap,p.NgayLap ORDER BY Ngay";
        try (PreparedStatement stmt = con.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next())
                ds.add(
                        new Object[] {
                            rs.getString(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getTimestamp(4).toLocalDateTime(),
                            rs.getDouble(5)
                        });
        }
        return ds;
    }

    /** Chỉ đơn nháp và phiếu trả đã duyệt do chính nhân viên lập. */
    public List<Object[]> congViecCuaToi(TaiKhoan tk) throws SQLException {
        Connection con = ConnectDB.getConnection();
        new TaiKhoan_DAO().kiemTraQuyen(tk, false);
        List<Object[]> ds = new ArrayList<>();
        String sql =
                "SELECT N'Hóa đơn' Loai,h.MaHoaDon Ma,h.NgayLap Ngay,N'Chưa thanh toán'"
                    + " TrangThai,ROUND((COALESCE(SUM(d.SoLuong*d.DonGia-d.GiamGia),0)-h.DiemSuDung*1000)*(1+h.ThueSuatVAT),2)"
                    + " Tien FROM dbo.HoaDon h LEFT JOIN dbo.ChiTietHoaDon d ON"
                    + " d.MaHoaDon=h.MaHoaDon WHERE h.MaNhanVien=? AND h.TrangThaiThanhToan=N'Chưa"
                    + " thanh toán' GROUP BY h.MaHoaDon,h.NgayLap,h.DiemSuDung,h.ThueSuatVAT UNION"
                    + " ALL SELECT N'Đổi trả',p.MaPhieuDoiTra,p.NgayLap,N'Đã"
                    + " duyệt',COALESCE(SUM(d.TienHoan),0) FROM dbo.PhieuDoiTra p LEFT JOIN"
                    + " dbo.ChiTietDoiTra d ON d.MaPhieuDoiTra=p.MaPhieuDoiTra WHERE"
                    + " p.MaNhanVienLap=? AND p.TrangThai=N'Đã duyệt' GROUP BY"
                    + " p.MaPhieuDoiTra,p.NgayLap ORDER BY Ngay";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, tk.getNhanVien().getMaNhanVien());
            stmt.setString(2, tk.getNhanVien().getMaNhanVien());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    ds.add(
                            new Object[] {
                                rs.getString(1),
                                rs.getString(2),
                                rs.getTimestamp(3).toLocalDateTime(),
                                rs.getString(4),
                                rs.getDouble(5)
                            });
            }
        }
        return ds;
    }

    public List<Object[]> sanPhamCanXem(TaiKhoan tk) throws SQLException {
        Connection con = ConnectDB.getConnection();
        new TaiKhoan_DAO().kiemTraQuyen(tk, false);
        List<Object[]> ds = new ArrayList<>();
        String sql =
                "SELECT MaSanPham,TenSanPham,SoLuongTon FROM dbo.SanPham WHERE TrangThai=N'Đang"
                    + " bán' AND SoLuongTon<=? ORDER BY SoLuongTon,MaSanPham";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, NGUONG_TON_THAP);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    ds.add(
                            new Object[] {
                                rs.getString(1),
                                rs.getString(2),
                                rs.getInt(3),
                                rs.getInt(3) == 0 ? "Hết hàng" : "Tồn thấp"
                            });
            }
        }
        return ds;
    }

    /** Lịch 7 ngày của chính tài khoản, không đọc lịch người khác. */
    public List<Object[]> lichCuaToi(TaiKhoan tk) throws SQLException {
        Connection con = ConnectDB.getConnection();
        new TaiKhoan_DAO().kiemTraQuyen(tk, false);
        List<Object[]> ds = new ArrayList<>();
        String sql =
                "SELECT"
                    + " p.MaPhanCong,p.Ngay,c.TenCa,c.GioBatDau,c.GioKetThuc,p.TrangThai,p.TienQuyDauCa"
                    + " FROM dbo.PhanCongCa p JOIN dbo.CaLamViec c ON c.MaCa=p.MaCa WHERE"
                    + " p.MaNhanVien=? AND p.Ngay>=? AND p.Ngay<=? ORDER BY p.Ngay,c.GioBatDau";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, tk.getNhanVien().getMaNhanVien());
            stmt.setDate(2, Date.valueOf(LocalDate.now()));
            stmt.setDate(3, Date.valueOf(LocalDate.now().plusDays(6)));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    ds.add(
                            new Object[] {
                                rs.getString(1),
                                rs.getDate(2).toLocalDate(),
                                rs.getString(3),
                                rs.getTime(4).toLocalTime(),
                                rs.getTime(5).toLocalTime(),
                                rs.getString(6),
                                rs.getDouble(7)
                            });
            }
        }
        return ds;
    }
}
