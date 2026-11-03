package dao;

import connectDB.ConnectDB;

import entity.ChiTietHoaDon;
import entity.ChuongTrinhKhuyenMai;
import entity.HoaDon;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhanCongCa;
import entity.SanPham;
import entity.TaiKhoan;

import util.InHoaDon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HoaDon_DAO {

    private final ChiTietHoaDon_DAO ct = new ChiTietHoaDon_DAO();

    public HoaDon tim(String ma) throws SQLException {
        Connection con = ConnectDB.getConnection();
        return tim(con, ma, false);
    }

    static HoaDon tim(Connection con, String ma, boolean lock) throws SQLException {
        String sql =
                "SELECT * FROM dbo.HoaDon "
                        + (lock ? "WITH (UPDLOCK,HOLDLOCK) " : "")
                        + "WHERE MaHoaDon=?";
        try (PreparedStatement s = con.prepareStatement(sql)) {
            s.setString(1, ma);
            try (ResultSet r = s.executeQuery()) {
                if (r.next()) {
                    HoaDon hoaDon = new HoaDon(r.getString("MaHoaDon"));
                    String ma3 = r.getString("MaKhachHang");
                    hoaDon.setKhachHang(ma3 == null ? null : new KhachHang(ma3));
                    hoaDon.setNhanVien(new NhanVien(r.getString("MaNhanVien")));
                    Timestamp thoiGian4 = r.getTimestamp("NgayLap");
                    hoaDon.setNgayLap(thoiGian4 == null ? null : thoiGian4.toLocalDateTime());
                    hoaDon.setHinhThucThanhToan(r.getString("HinhThucThanhToan"));
                    hoaDon.setTrangThaiThanhToan(r.getString("TrangThaiThanhToan"));
                    hoaDon.setThueSuatVAT(r.getDouble("ThueSuatVAT"));
                    hoaDon.setPhanCongCa(new PhanCongCa(r.getString("MaPhanCong")));
                    hoaDon.setDiemSuDung(r.getInt("DiemSuDung"));
                    return hoaDon;
                } else {
                    return null;
                }
            }
        }
    }

    public List<HoaDon> timKiem(
            String tuKhoa, LocalDate tu, LocalDate den, String trangThai, String hinhThuc)
            throws SQLException {
        if (den.isBefore(tu)) {
            throw new SQLException("Khoáº£ng ngÃ y khÃ´ng há»£p lá»‡.");
        }
        List<HoaDon> ds = new ArrayList<>();
        String sql =
                "SELECT h.* FROM dbo.HoaDon h LEFT JOIN dbo.KhachHang k ON"
                    + " k.MaKhachHang=h.MaKhachHang WHERE (h.MaHoaDon LIKE ? OR k.SoDienThoai LIKE"
                    + " ?) AND h.NgayLap>=? AND h.NgayLap<? AND (?='' OR h.TrangThaiThanhToan=?)"
                    + " AND (?='' OR h.HinhThucThanhToan=?) ORDER BY h.NgayLap DESC,h.MaHoaDon"
                    + " DESC";
        Connection con = ConnectDB.getConnection();
        try (PreparedStatement s = con.prepareStatement(sql)) {
            s.setString(1, "%" + tuKhoa + "%");
            s.setString(2, "%" + tuKhoa + "%");
            s.setDate(3, java.sql.Date.valueOf(tu));
            s.setDate(4, java.sql.Date.valueOf(den.plusDays(1)));
            s.setString(5, trangThai);
            s.setString(6, trangThai);
            s.setString(7, hinhThuc);
            s.setString(8, hinhThuc);
            try (ResultSet r = s.executeQuery()) {
                while (r.next()) {
                    HoaDon hoaDon = new HoaDon(r.getString("MaHoaDon"));
                    String ma3 = r.getString("MaKhachHang");
                    hoaDon.setKhachHang(ma3 == null ? null : new KhachHang(ma3));
                    hoaDon.setNhanVien(new NhanVien(r.getString("MaNhanVien")));
                    Timestamp thoiGian4 = r.getTimestamp("NgayLap");
                    hoaDon.setNgayLap(thoiGian4 == null ? null : thoiGian4.toLocalDateTime());
                    hoaDon.setHinhThucThanhToan(r.getString("HinhThucThanhToan"));
                    hoaDon.setTrangThaiThanhToan(r.getString("TrangThaiThanhToan"));
                    hoaDon.setThueSuatVAT(r.getDouble("ThueSuatVAT"));
                    hoaDon.setPhanCongCa(new PhanCongCa(r.getString("MaPhanCong")));
                    hoaDon.setDiemSuDung(r.getInt("DiemSuDung"));
                    ds.add(hoaDon);
                }
            }
        }
        return ds;
    }

    private void kiemTraKhach(Connection con, HoaDon h, List<ChiTietHoaDon> ds)
            throws SQLException {
        if (h.getDiemSuDung() < 0) {
            throw new SQLException("Äiá»ƒm sá»­ dá»¥ng khÃ´ng Ä‘Æ°á»£c Ã¢m.");
        }
        int soDu = 0;
        if (h.getKhachHang() != null) {
            String sql =
                    "SELECT DiemTichLuy FROM dbo.KhachHang WITH (UPDLOCK,HOLDLOCK) WHERE"
                            + " MaKhachHang=?";
            String ketQua3 = null;
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, h.getKhachHang().getMaKhachHang());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        ketQua3 = rs.getString(1);
                    }
                }
            }
            String diem = ketQua3;
            if (diem == null) {
                throw new SQLException("KhÃ¡ch hÃ ng khÃ´ng tá»“n táº¡i.");
            }
            soDu = Integer.parseInt(diem);
        }
        if (h.getDiemSuDung() > InHoaDon.diemToiDa(InHoaDon.sauKhuyenMai(ds), soDu)) {
            throw new SQLException("Äiá»ƒm vÆ°á»£t sá»‘ dÆ° hoáº·c má»©c giáº£m 50% trÆ°á»›c VAT.");
        }
        if (!Double.isFinite(h.getThueSuatVAT())
                || h.getThueSuatVAT() < 0 || h.getThueSuatVAT() >= 1) {
            throw new SQLException("Thuáº¿ suáº¥t VAT khÃ´ng há»£p lá»‡.");
        }
    }

    private void kiemTraDong(Connection con, List<ChiTietHoaDon> ds, boolean luu)
            throws SQLException {
        if (ds == null || ds.isEmpty()) {
            throw new SQLException("HÃ³a Ä‘Æ¡n pháº£i cÃ³ sáº£n pháº©m.");
        }
        Set<String> ids = new HashSet<>();
        ds.sort(Comparator.comparing(d -> d.getSanPham().getMaSanPham()));
        for (ChiTietHoaDon d : ds) {
            String ma = d.getSanPham().getMaSanPham();
            if (!(ids.add(ma)) || d.getSoLuong() <= 0) {
                throw new SQLException("Má»—i sáº£n pháº©m má»™t dÃ²ng, sá»‘ lÆ°á»£ng pháº£i > 0.");
            }
            String sql =
                    "SELECT MaSanPham FROM dbo.SanPham WITH (UPDLOCK,HOLDLOCK) WHERE MaSanPham=?";
            String ketQua3 = null;
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, ma);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        ketQua3 = rs.getString(1);
                    }
                }
            }
            SanPham sp = SanPham_DAO.tim(con, ma);
            if (sp == null || !("Äang bÃ¡n".equals(sp.getTrangThai()))) {
                throw new SQLException("Sáº£n pháº©m khÃ´ng cÃ²n bÃ¡n: " + ma);
            }
            if (d.getSoLuong() > sp.getSoLuongTon()) {
                throw new SQLException("KhÃ´ng Ä‘á»§ tá»“n: " + sp.getTenSanPham());
            }
            if (luu) {
                d.setDonGia(sp.getDonGiaBan());
            } else {
                if (InHoaDon.soSanhTien(d.getDonGia(), sp.getDonGiaBan()) != 0) {
                    throw new SQLException("GiÃ¡ Ä‘Ã£ Ä‘á»•i. Má»Ÿ láº¡i Ä‘Æ¡n, lÆ°u láº¡i trÆ°á»›c khi thanh toÃ¡n.");
                }
            }
            double giam = 0.0;
            if (d.getChuongTrinhKhuyenMai() != null) {
                ChuongTrinhKhuyenMai km =
                        ChuongTrinhKhuyenMai_DAO.tim(
                                con, d.getChuongTrinhKhuyenMai().getMaKhuyenMai());
                if (!(ChuongTrinhKhuyenMai_DAO.hieuLuc(km, LocalDate.now()))) {
                    throw new SQLException("Khuyáº¿n mÃ£i háº¿t hiá»‡u lá»±c. Má»Ÿ láº¡i vÃ  sá»­a Ä‘Æ¡n.");
                }
                giam =
                        InHoaDon.tron(
                                d.getDonGia() * d.getSoLuong() * km.getGiaTriGiam() / 100.0);
            }
            if (luu) {
                d.setGiamGia(giam);
            } else {
                if (InHoaDon.soSanhTien(d.getGiamGia(), giam) != 0) {
                    throw new SQLException("Má»©c giáº£m Ä‘Ã£ Ä‘á»•i. LÆ°u láº¡i Ä‘Æ¡n trÆ°á»›c khi thanh toÃ¡n.");
                }
            }
        }
    }

    public String luuNhap(HoaDon h, List<ChiTietHoaDon> ds, TaiKhoan tk) throws SQLException {
        Connection con = ConnectDB.getConnection();
        boolean tuDongCommitCu = con.getAutoCommit();
        int mucCoLapCu = con.getTransactionIsolation();
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        try {
            new TaiKhoan_DAO().kiemTraQuyen(tk, false);
            String pc = new PhanCongCa_DAO().layMaCaDangMo(con, tk);
            kiemTraDong(con, ds, true);
            kiemTraKhach(con, h, ds);
            if (h.getMaHoaDon() == null) {
                String sql = "SELECT MaCa FROM dbo.PhanCongCa WHERE MaPhanCong=?";
                String ketQua3 = null;
                try (PreparedStatement stmt = con.prepareStatement(sql)) {
                    stmt.setString(1, pc);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            ketQua3 = rs.getString(1);
                        }
                    }
                }
                String ca = ketQua3;
                String prefix =
                        "HD" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ca;
                String sql2 =
                        "SELECT MAX(MaHoaDon) FROM dbo.HoaDon WITH (UPDLOCK,HOLDLOCK) WHERE"
                                + " MaHoaDon LIKE ?";
                String ketQua7 = null;
                try (PreparedStatement stmt3 = con.prepareStatement(sql2)) {
                    stmt3.setString(1, prefix + "%");
                    try (ResultSet rs2 = stmt3.executeQuery()) {
                        if (rs2.next()) {
                            ketQua7 = rs2.getString(1);
                        }
                    }
                }
                String max = ketQua7;
                int stt = max == null ? 1 : Integer.parseInt(max.substring(14)) + 1;
                if (stt > 999) {
                    throw new SQLException("Ca Ä‘Ã£ Ä‘á»§ 999 hÃ³a Ä‘Æ¡n trong ngÃ y.");
                }
                String id = prefix + String.format("%03d", stt);
                String sql3 =
                        "INSERT INTO"
                            + " dbo.HoaDon(MaHoaDon,MaKhachHang,MaNhanVien,NgayLap,ThueSuatVAT,MaPhanCong,DiemSuDung)"
                            + " VALUES(?,?,?,SYSDATETIME(),?,?,?)";
                try (PreparedStatement stmt4 = con.prepareStatement(sql3)) {
                    stmt4.setString(1, id);
                    stmt4.setString(
                            2, h.getKhachHang() == null ? null : h.getKhachHang().getMaKhachHang());
                    stmt4.setString(3, tk.getNhanVien().getMaNhanVien());
                    stmt4.setDouble(4, h.getThueSuatVAT());
                    stmt4.setString(5, pc);
                    stmt4.setInt(6, h.getDiemSuDung());
                    stmt4.executeUpdate();
                }
                h.setMaHoaDon(id);
            } else {
                HoaDon cu = tim(con, h.getMaHoaDon(), true);
                if (cu == null || !("ChÆ°a thanh toÃ¡n".equals(cu.getTrangThaiThanhToan()))) {
                    throw new SQLException("Chá»‰ sá»­a Ä‘Æ¡n chÆ°a thanh toÃ¡n.");
                }
                if (!(cu.getNhanVien().getMaNhanVien().equals(tk.getNhanVien().getMaNhanVien()))) {
                    throw new SQLException("Chá»‰ sá»­a Ä‘Æ¡n do mÃ¬nh láº­p.");
                }
                String sql4 =
                        "UPDATE dbo.HoaDon SET"
                                + " MaKhachHang=?,ThueSuatVAT=?,MaPhanCong=?,DiemSuDung=? WHERE"
                                + " MaHoaDon=?";
                try (PreparedStatement stmt5 = con.prepareStatement(sql4)) {
                    stmt5.setString(
                            1, h.getKhachHang() == null ? null : h.getKhachHang().getMaKhachHang());
                    stmt5.setDouble(2, h.getThueSuatVAT());
                    stmt5.setString(3, pc);
                    stmt5.setInt(4, h.getDiemSuDung());
                    stmt5.setString(5, h.getMaHoaDon());
                    stmt5.executeUpdate();
                }
                String sql6 = "DELETE FROM dbo.ChiTietHoaDon WHERE MaHoaDon=?";
                try (PreparedStatement stmt7 = con.prepareStatement(sql6)) {
                    stmt7.setString(1, h.getMaHoaDon());
                    stmt7.executeUpdate();
                }
            }
            ct.ghi(con, h, ds);
            con.commit();
            return h.getMaHoaDon();
        } catch (SQLException | RuntimeException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(tuDongCommitCu);
            con.setTransactionIsolation(mucCoLapCu);
        }
    }

    public double thanhToan(
            String ma, String hinhThuc, double tienNhan, boolean xacNhanQR, TaiKhoan tk)
            throws SQLException {
        Connection con = ConnectDB.getConnection();
        boolean tuDongCommitCu = con.getAutoCommit();
        int mucCoLapCu = con.getTransactionIsolation();
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        try {
            new TaiKhoan_DAO().kiemTraQuyen(tk, false);
            HoaDon h = tim(con, ma, true);
            if (h == null || !("ChÆ°a thanh toÃ¡n".equals(h.getTrangThaiThanhToan()))) {
                throw new SQLException("HÃ³a Ä‘Æ¡n Ä‘Ã£ thanh toÃ¡n, Ä‘Ã£ há»§y hoáº·c khÃ´ng tá»“n táº¡i.");
            }
            if (!(h.getNhanVien().getMaNhanVien().equals(tk.getNhanVien().getMaNhanVien()))) {
                throw new SQLException("Chá»‰ thanh toÃ¡n Ä‘Æ¡n cá»§a mÃ¬nh.");
            }
            String pc = new PhanCongCa_DAO().layMaCaDangMo(con, tk);
            if (!(pc.equals(h.getPhanCongCa().getMaPhanCong()))) {
                throw new SQLException(
                        "ÄÆ¡n khÃ´ng thuá»™c ca hiá»‡n táº¡i. Má»Ÿ láº¡i vÃ  lÆ°u Ä‘Æ¡n vÃ o ca nÃ y.");
            }
            List<ChiTietHoaDon> ds = ct.getTheoHoaDon(con, ma);
            kiemTraDong(con, ds, false);
            kiemTraKhach(con, h, ds);
            double total = InHoaDon.phaiTra(ds, h.getDiemSuDung(), h.getThueSuatVAT());
            if (!("Tiá»n máº·t".equals(hinhThuc)) && !("Chuyá»ƒn khoáº£n QR".equals(hinhThuc))) {
                throw new SQLException("HÃ¬nh thá»©c khÃ´ng há»£p lá»‡.");
            }
            if ("Tiá»n máº·t".equals(hinhThuc)) {
                if (!Double.isFinite(tienNhan) || InHoaDon.soSanhTien(tienNhan, total) < 0) {
                    throw new SQLException("Tiá»n khÃ¡ch Ä‘Æ°a chÆ°a Ä‘á»§.");
                }
            } else {
                if (!(xacNhanQR)) {
                    throw new SQLException("ChÆ°a xÃ¡c nháº­n tiá»n chuyá»ƒn khoáº£n Ä‘Ã£ vÃ o.");
                }
                util.InHoaDon.kiemTraCauHinhQR();
                if (!Double.isFinite(total) || total <= 0
                        || total != Math.rint(total) || total > 9999999999999.0) {
                    throw new SQLException("QR cáº§n sá»‘ tiá»n nguyÃªn VND dÆ°Æ¡ng, tá»‘i Ä‘a 13 chá»¯ sá»‘.");
                }
            }
            for (ChiTietHoaDon d : ds) {
                SanPham_DAO.doiTon(con, d.getSanPham().getMaSanPham(), -d.getSoLuong());
            }
            if (h.getKhachHang() != null) {
                KhachHang_DAO.doiDiem(
                        con,
                        h.getKhachHang().getMaKhachHang(),
                        InHoaDon.diemCong(total) - h.getDiemSuDung());
            }
            String sql =
                    "UPDATE dbo.HoaDon SET TrangThaiThanhToan=N'ÄÃ£ thanh toÃ¡n',HinhThucThanhToan=?"
                            + " WHERE MaHoaDon=? AND TrangThaiThanhToan=N'ChÆ°a thanh toÃ¡n'";
            int ketQua3;
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, hinhThuc);
                stmt.setString(2, ma);
                ketQua3 = stmt.executeUpdate();
            }
            if (ketQua3 != 1) {
                throw new SQLException("Tráº¡ng thÃ¡i Ä‘Æ¡n Ä‘Ã£ thay Ä‘á»•i.");
            }
            con.commit();
            return "Tiá»n máº·t".equals(hinhThuc) ? InHoaDon.tron(tienNhan - total) : 0.0;
        } catch (SQLException | RuntimeException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(tuDongCommitCu);
            con.setTransactionIsolation(mucCoLapCu);
        }
    }

    public void huy(String ma, TaiKhoan tk) throws SQLException {
        Connection con = ConnectDB.getConnection();
        boolean tuDongCommitCu = con.getAutoCommit();
        int mucCoLapCu = con.getTransactionIsolation();
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        try {
            new TaiKhoan_DAO().kiemTraQuyen(tk, false);
            HoaDon h = tim(con, ma, true);
            if (h == null || !("ChÆ°a thanh toÃ¡n".equals(h.getTrangThaiThanhToan()))) {
                throw new SQLException("Chá»‰ há»§y hÃ³a Ä‘Æ¡n chÆ°a thanh toÃ¡n.");
            }
            if (!("ADMIN".equals(tk.getQuyen()))
                    && !(h.getNhanVien()
                            .getMaNhanVien()
                            .equals(tk.getNhanVien().getMaNhanVien()))) {
                throw new SQLException("KhÃ´ng Ä‘Æ°á»£c há»§y Ä‘Æ¡n cá»§a ngÆ°á»i khÃ¡c.");
            }
            String sql = "UPDATE dbo.HoaDon SET TrangThaiThanhToan=N'ÄÃ£ há»§y' WHERE MaHoaDon=?";
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, ma);
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
    }
}
