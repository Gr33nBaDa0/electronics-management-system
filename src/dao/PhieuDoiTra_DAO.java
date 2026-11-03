package dao;


import connectDB.ConnectDB;

import entity.ChiTietDoiTra;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.NhanVien;
import entity.PhanCongCa;
import entity.PhieuDoiTra;
import entity.TaiKhoan;

import util.InHoaDon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PhieuDoiTra_DAO {

    public List<PhieuDoiTra> getAllPhieuDoiTra() throws SQLException {
        List<PhieuDoiTra> ds = new ArrayList<>();
        Connection con = ConnectDB.getConnection();
        String sql = "SELECT * FROM dbo.PhieuDoiTra ORDER BY NgayLap DESC";
        try (PreparedStatement s = con.prepareStatement(sql);
                ResultSet r = s.executeQuery()) {
            while (r.next()) {
                PhieuDoiTra p = new PhieuDoiTra(r.getString("MaPhieuDoiTra"));
                p.setHoaDonGoc(new HoaDon(r.getString("MaHoaDonGoc")));
                p.setNhanVienLap(new NhanVien(r.getString("MaNhanVienLap")));
                String q = r.getString("MaQuanLyDuyet"), ca = r.getString("MaPhanCongHoanTien");
                p.setQuanLyDuyet(q == null ? null : new NhanVien(q));
                p.setPhanCongHoanTien(ca == null ? null : new PhanCongCa(ca));
                Timestamp thoiGian2 = r.getTimestamp("NgayLap");
                p.setNgayLap(thoiGian2 == null ? null : thoiGian2.toLocalDateTime());
                p.setLyDo(r.getString("LyDo"));
                p.setTrangThai(r.getString("TrangThai"));
                Timestamp thoiGian3 = r.getTimestamp("NgayDuyet");
                p.setNgayDuyet(thoiGian3 == null ? null : thoiGian3.toLocalDateTime());
                p.setLyDoTuChoi(r.getString("LyDoTuChoi"));
                p.setDiemHoanLai(r.getInt("DiemHoanLai"));
                p.setDiemThuHoi(r.getInt("DiemThuHoi"));
                ds.add(p);
            }
        }
        return ds;
    }

    public String luu(String id, String hd, String lyDo, List<ChiTietDoiTra> ds, TaiKhoan tk)
            throws SQLException {
        Connection con = ConnectDB.getConnection();
        boolean tuDongCommitCu = con.getAutoCommit();
        int mucCoLapCu = con.getTransactionIsolation();
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        try {
            new TaiKhoan_DAO().kiemTraQuyen(tk, false);
            HoaDon h = HoaDon_DAO.tim(con, hd, true);
            if (h == null || !("ÄÃ£ thanh toÃ¡n".equals(h.getTrangThaiThanhToan()))) {
                throw new SQLException("Chá»‰ tráº£ hÃ ng tá»« hÃ³a Ä‘Æ¡n Ä‘Ã£ thanh toÃ¡n.");
            }
            int ngay = docSoThietLap("returns.days", 14);
            if (ngay <= 0
                    || h.getNgayLap().toLocalDate().isBefore(LocalDate.now().minusDays(ngay))) {
                throw new SQLException(
                        "HÃ³a Ä‘Æ¡n Ä‘Ã£ quÃ¡ háº¡n tráº£ " + ngay + " ngÃ y tÃ­nh tá»« ngÃ y láº­p.");
            }
            if (ds.isEmpty() || lyDo == null || lyDo.trim().isEmpty()) {
                throw new SQLException("Cáº§n lÃ½ do vÃ  Ã­t nháº¥t má»™t dÃ²ng tráº£.");
            }
            if (id == null) {
                id =
                        "DT"
                                + java.util
                                        .UUID
                                        .randomUUID()
                                        .toString()
                                        .replace("-", "")
                                        .substring(0, 16);
                String sql =
                        "INSERT INTO dbo.PhieuDoiTra(MaPhieuDoiTra,MaHoaDonGoc,MaNhanVienLap,LyDo)"
                                + " VALUES(?,?,?,?)";
                try (PreparedStatement stmt = con.prepareStatement(sql)) {
                    stmt.setString(1, id);
                    stmt.setString(2, hd);
                    stmt.setString(3, tk.getNhanVien().getMaNhanVien());
                    stmt.setString(4, lyDo);
                    stmt.executeUpdate();
                }
            } else {
                String sql2 =
                        "UPDATE dbo.PhieuDoiTra SET LyDo=? WHERE MaPhieuDoiTra=? AND MaHoaDonGoc=?"
                                + " AND MaNhanVienLap=? AND TrangThai=N'Chá» duyá»‡t'";
                int ketQua6;
                try (PreparedStatement stmt3 = con.prepareStatement(sql2)) {
                    stmt3.setString(1, lyDo);
                    stmt3.setString(2, id);
                    stmt3.setString(3, hd);
                    stmt3.setString(4, tk.getNhanVien().getMaNhanVien());
                    ketQua6 = stmt3.executeUpdate();
                }
                if (ketQua6 != 1) {
                    throw new SQLException("Chá»‰ sá»­a phiáº¿u chá» duyá»‡t cá»§a mÃ¬nh, cÃ¹ng hÃ³a Ä‘Æ¡n gá»‘c.");
                }
                String sql3 = "DELETE FROM dbo.ChiTietDoiTra WHERE MaPhieuDoiTra=?";
                try (PreparedStatement stmt4 = con.prepareStatement(sql3)) {
                    stmt4.setString(1, id);
                    stmt4.executeUpdate();
                }
            }
            List<ChiTietHoaDon> goc = new ChiTietHoaDon_DAO().getTheoHoaDon(con, hd);
            Map<String, Double> phanBo =
                    InHoaDon.phanBo(goc, h.getDiemSuDung(), h.getThueSuatVAT());
            Set<String> seen = new HashSet<>();
            for (ChiTietDoiTra d : ds) {
                String sp = d.getChiTietHoaDon().getSanPham().getMaSanPham();
                ChiTietHoaDon g = null;
                for (ChiTietHoaDon a : goc) {
                    if (a.getSanPham().getMaSanPham().equals(sp)) {
                        g = a;
                    }
                }
                if (g == null || !(seen.add(sp)) || d.getSoLuongTra() <= 0) {
                    throw new SQLException(
                            "Sáº£n pháº©m ngoÃ i hÃ³a Ä‘Æ¡n, bá»‹ trÃ¹ng hoáº·c lÆ°á»£ng tráº£ khÃ´ng há»£p lá»‡.");
                }
                String sql5 =
                        "SELECT COALESCE(SUM(d.SoLuongTra),0) FROM dbo.ChiTietDoiTra d JOIN"
                                + " dbo.PhieuDoiTra p ON p.MaPhieuDoiTra=d.MaPhieuDoiTra WHERE"
                                + " d.MaHoaDon=? AND d.MaSanPham=? AND p.TrangThai<>N'Tá»« chá»‘i'";
                int ketQua12 = 0;
                try (PreparedStatement stmt6 = con.prepareStatement(sql5)) {
                    stmt6.setString(1, hd);
                    stmt6.setString(2, sp);
                    try (ResultSet rs = stmt6.executeQuery()) {
                        if (rs.next()) {
                            ketQua12 = rs.getInt(1);
                        }
                    }
                }
                int da = ketQua12;
                if (da + d.getSoLuongTra() > g.getSoLuong()) {
                    throw new SQLException(
                            "Sá»‘ tráº£ vÆ°á»£t sá»‘ cÃ²n láº¡i (Ä‘Ã£ tÃ­nh phiáº¿u chá» / Ä‘Ã£ duyá»‡t): " + sp);
                }
                if (!("NguyÃªn váº¹n".equals(d.getTinhTrangHang()))
                        && !("Lá»—i".equals(d.getTinhTrangHang()))) {
                    throw new SQLException("Chá»n tÃ¬nh tráº¡ng NguyÃªn váº¹n hoáº·c Lá»—i.");
                }
                double tien = InHoaDon.luyKe(phanBo.get(sp), d.getSoLuongTra(), g.getSoLuong());
                String sql6 =
                        "INSERT INTO"
                            + " dbo.ChiTietDoiTra(MaPhieuDoiTra,MaHoaDon,MaSanPham,SoLuongTra,TinhTrangHang,TienHoan)"
                            + " VALUES(?,?,?,?,?,?)";
                try (PreparedStatement stmt7 = con.prepareStatement(sql6)) {
                    stmt7.setString(1, id);
                    stmt7.setString(2, hd);
                    stmt7.setString(3, sp);
                    stmt7.setInt(4, d.getSoLuongTra());
                    stmt7.setString(5, d.getTinhTrangHang());
                    stmt7.setDouble(6, tien);
                    stmt7.executeUpdate();
                }
            }
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
                    "SELECT MaPhieuDoiTra FROM dbo.PhieuDoiTra WITH (UPDLOCK,HOLDLOCK) WHERE"
                            + " MaPhieuDoiTra=? AND MaNhanVienLap=? AND TrangThai=N'Chá» duyá»‡t'";
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
                throw new SQLException("Chá»‰ xÃ³a phiáº¿u chá» cá»§a mÃ¬nh.");
            }
            String sql2 = "DELETE FROM dbo.ChiTietDoiTra WHERE MaPhieuDoiTra=?";
            try (PreparedStatement stmt3 = con.prepareStatement(sql2)) {
                stmt3.setString(1, id);
                stmt3.executeUpdate();
            }
            String sql3 = "DELETE FROM dbo.PhieuDoiTra WHERE MaPhieuDoiTra=?";
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

    public void duyet(String id, boolean chapNhan, String lyDo, TaiKhoan tk) throws SQLException {
        Connection con = ConnectDB.getConnection();
        boolean tuDongCommitCu = con.getAutoCommit();
        int mucCoLapCu = con.getTransactionIsolation();
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        try {
            new TaiKhoan_DAO().kiemTraQuyen(tk, true);
            if (!chapNhan && (lyDo == null || lyDo.trim().isEmpty())) {
                throw new SQLException("Tá»« chá»‘i cáº§n lÃ½ do.");
            }
            String sql =
                    "UPDATE dbo.PhieuDoiTra SET"
                        + " TrangThai=?,MaQuanLyDuyet=?,NgayDuyet=SYSDATETIME(),LyDoTuChoi=? WHERE"
                        + " MaPhieuDoiTra=? AND TrangThai=N'Chá» duyá»‡t'";
            int ketQua3;
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, chapNhan ? "ÄÃ£ duyá»‡t" : "Tá»« chá»‘i");
                stmt.setString(2, tk.getNhanVien().getMaNhanVien());
                stmt.setString(3, chapNhan ? null : lyDo);
                stmt.setString(4, id);
                ketQua3 = stmt.executeUpdate();
            }
            if (ketQua3 != 1) {
                throw new SQLException("Phiáº¿u khÃ´ng cÃ²n chá» duyá»‡t.");
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

    public int soConTra(String hd, String sp) throws SQLException {
        Connection con = ConnectDB.getConnection();
        String sql = "SELECT SoLuong FROM dbo.ChiTietHoaDon WHERE MaHoaDon=? AND MaSanPham=?";
        int ketQua3 = 0;
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, hd);
            stmt.setString(2, sp);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ketQua3 = rs.getInt(1);
                }
            }
        }
        int mua = ketQua3;
        String sql2 =
                "SELECT COALESCE(SUM(d.SoLuongTra),0) FROM dbo.ChiTietDoiTra d JOIN dbo.PhieuDoiTra"
                    + " p ON p.MaPhieuDoiTra=d.MaPhieuDoiTra WHERE d.MaHoaDon=? AND d.MaSanPham=?"
                    + " AND p.TrangThai<>N'Tá»« chá»‘i'";
        int ketQua7 = 0;
        try (PreparedStatement stmt3 = con.prepareStatement(sql2)) {
            stmt3.setString(1, hd);
            stmt3.setString(2, sp);
            try (ResultSet rs2 = stmt3.executeQuery()) {
                if (rs2.next()) {
                    ketQua7 = rs2.getInt(1);
                }
            }
        }
        int da = ketQua7;
        return mua - da;
    }

    public double duTinh(String id, TaiKhoan tk) throws SQLException {
        return hoanTat(id, tk, false, 0.0);
    }

    public double hoanTat(String id, double daXacNhan, TaiKhoan tk) throws SQLException {
        return hoanTat(id, tk, true, daXacNhan);
    }

    private double hoanTat(String id, TaiKhoan tk, boolean ghi, double daXacNhan)
            throws SQLException {
        Connection con = ConnectDB.getConnection();
        boolean tuDongCommitCu = con.getAutoCommit();
        int mucCoLapCu = con.getTransactionIsolation();
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        try {
            new TaiKhoan_DAO().kiemTraQuyen(tk, false);
            String sql = "SELECT MaHoaDonGoc FROM dbo.PhieuDoiTra WHERE MaPhieuDoiTra=?";
            String ketQua3 = null;
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        ketQua3 = rs.getString(1);
                    }
                }
            }
            String hd = ketQua3;
            HoaDon h = HoaDon_DAO.tim(con, hd, true);
            if (h == null || !("ÄÃ£ thanh toÃ¡n".equals(h.getTrangThaiThanhToan()))) {
                throw new SQLException("HÃ³a Ä‘Æ¡n gá»‘c khÃ´ng há»£p lá»‡.");
            }
            String sql2 =
                    "SELECT TrangThai FROM dbo.PhieuDoiTra WITH (UPDLOCK,HOLDLOCK) WHERE"
                            + " MaPhieuDoiTra=?";
            String ketQua7 = null;
            try (PreparedStatement stmt3 = con.prepareStatement(sql2)) {
                stmt3.setString(1, id);
                try (ResultSet rs2 = stmt3.executeQuery()) {
                    if (rs2.next()) {
                        ketQua7 = rs2.getString(1);
                    }
                }
            }
            String trangThai = ketQua7;
            if (!("ÄÃ£ duyá»‡t".equals(trangThai))) {
                throw new SQLException("Chá»‰ hoÃ n táº¥t phiáº¿u Ä‘Ã£ duyá»‡t, chÆ°a hoÃ n táº¥t.");
            }
            String ca = new PhanCongCa_DAO().layMaCaDangMo(con, tk);
            List<ChiTietHoaDon> goc = new ChiTietHoaDon_DAO().getTheoHoaDon(con, hd);
            Map<String, Double> phanBo =
                    InHoaDon.phanBo(goc, h.getDiemSuDung(), h.getThueSuatVAT());
            double total = InHoaDon.phaiTra(goc, h.getDiemSuDung(), h.getThueSuatVAT());
            if (total <= 0) {
                throw new SQLException("HÃ³a Ä‘Æ¡n khÃ´ng cÃ³ giÃ¡ trá»‹ Ä‘á»ƒ hoÃ n.");
            }
            String sql3 =
                    "SELECT COALESCE(SUM(d.TienHoan),0) FROM dbo.ChiTietDoiTra d JOIN"
                            + " dbo.PhieuDoiTra p ON p.MaPhieuDoiTra=d.MaPhieuDoiTra WHERE"
                            + " p.MaHoaDonGoc=? AND p.TrangThai=N'HoÃ n táº¥t'";
            double ketQua11 = 0.0;
            try (PreparedStatement stmt4 = con.prepareStatement(sql3)) {
                stmt4.setString(1, hd);
                try (ResultSet rs3 = stmt4.executeQuery()) {
                    if (rs3.next()) {
                        ketQua11 = rs3.getDouble(1);
                    }
                }
            }
            double truoc = ketQua11, hoan = 0.0;
            List<ChiTietDoiTra> ds = new ChiTietDoiTra_DAO().getTheoPhieu(con, id);
            if (ds.isEmpty()) {
                throw new SQLException("Phiáº¿u chÆ°a cÃ³ dÃ²ng tráº£.");
            }
            for (ChiTietDoiTra d : ds) {
                String sp = d.getChiTietHoaDon().getSanPham().getMaSanPham();
                ChiTietHoaDon g = null;
                for (ChiTietHoaDon a : goc) {
                    if (a.getSanPham().getMaSanPham().equals(sp)) {
                        g = a;
                    }
                }
                if (g == null || d.getSoLuongTra() <= 0) {
                    throw new SQLException("DÃ²ng gá»‘c khÃ´ng há»£p lá»‡.");
                }
                String from =
                        " FROM dbo.ChiTietDoiTra d JOIN dbo.PhieuDoiTra p ON"
                                + " p.MaPhieuDoiTra=d.MaPhieuDoiTra WHERE d.MaHoaDon=? AND"
                                + " d.MaSanPham=? AND p.TrangThai=N'HoÃ n táº¥t'";
                String sql4 = "SELECT COALESCE(SUM(d.SoLuongTra),0)" + from;
                int ketQua15 = 0;
                try (PreparedStatement stmt5 = con.prepareStatement(sql4)) {
                    stmt5.setString(1, hd);
                    stmt5.setString(2, sp);
                    try (ResultSet rs5 = stmt5.executeQuery()) {
                        if (rs5.next()) {
                            ketQua15 = rs5.getInt(1);
                        }
                    }
                }
                int da = ketQua15;
                String sql6 = "SELECT COALESCE(SUM(d.TienHoan),0)" + from;
                double ketQua19 = 0.0;
                try (PreparedStatement stmt7 = con.prepareStatement(sql6)) {
                    stmt7.setString(1, hd);
                    stmt7.setString(2, sp);
                    try (ResultSet rs6 = stmt7.executeQuery()) {
                        if (rs6.next()) {
                            ketQua19 = rs6.getDouble(1);
                        }
                    }
                }
                double tienDa = ketQua19;
                if (da + d.getSoLuongTra() > g.getSoLuong()) {
                    throw new SQLException("ÄÃ£ tráº£ vÆ°á»£t sá»‘ lÆ°á»£ng mua.");
                }
                double tien =
                        InHoaDon.tron(InHoaDon.luyKe(phanBo.get(sp), da + d.getSoLuongTra(), g.getSoLuong()) - tienDa);
                if (tien < 0) {
                    throw new SQLException("Sá»‘ tiá»n hoÃ n gá»‘c khÃ´ng nháº¥t quÃ¡n.");
                }
                hoan = InHoaDon.tron(hoan + tien);
                String sql7 =
                        "UPDATE dbo.ChiTietDoiTra SET TienHoan=? WHERE MaPhieuDoiTra=? AND"
                                + " MaSanPham=?";
                try (PreparedStatement stmt8 = con.prepareStatement(sql7)) {
                    stmt8.setDouble(1, tien);
                    stmt8.setString(2, id);
                    stmt8.setString(3, sp);
                    stmt8.executeUpdate();
                }
                if ("NguyÃªn váº¹n".equals(d.getTinhTrangHang())) {
                    SanPham_DAO.doiTon(con, sp, d.getSoLuongTra());
                }
            }
            if (InHoaDon.soSanhTien(truoc + hoan, total) > 0) {
                throw new SQLException("Tá»•ng hoÃ n vÆ°á»£t tiá»n thá»±c tráº£.");
            }
            int diemHoan = 0, diemThu = 0;
            if (h.getKhachHang() != null) {
                String sql8 =
                        "SELECT COALESCE(SUM(DiemHoanLai),0) FROM dbo.PhieuDoiTra WHERE"
                                + " MaHoaDonGoc=? AND TrangThai=N'HoÃ n táº¥t'";
                int ketQua26 = 0;
                try (PreparedStatement stmt9 = con.prepareStatement(sql8)) {
                    stmt9.setString(1, hd);
                    try (ResultSet rs7 = stmt9.executeQuery()) {
                        if (rs7.next()) {
                            ketQua26 = rs7.getInt(1);
                        }
                    }
                }
                int daHoan = ketQua26;
                String sql10 =
                        "SELECT COALESCE(SUM(DiemThuHoi),0) FROM dbo.PhieuDoiTra WHERE"
                                + " MaHoaDonGoc=? AND TrangThai=N'HoÃ n táº¥t'";
                int ketQua30 = 0;
                try (PreparedStatement stmt11 = con.prepareStatement(sql10)) {
                    stmt11.setString(1, hd);
                    try (ResultSet rs9 = stmt11.executeQuery()) {
                        if (rs9.next()) {
                            ketQua30 = rs9.getInt(1);
                        }
                    }
                }
                int daThu = ketQua30;
                diemHoan =
                        (int) Math.floor(Math.nextUp(h.getDiemSuDung() * (InHoaDon.tron(truoc + hoan) / total)))
                                - daHoan;
                diemThu =
                        (int) Math.floor(Math.nextUp(InHoaDon.diemCong(total) * (InHoaDon.tron(truoc + hoan) / total)))
                                - daThu;
                if (diemHoan < 0 || diemThu < 0) {
                    throw new SQLException("Äiá»ƒm hoÃ n trÆ°á»›c Ä‘Ã¢y khÃ´ng nháº¥t quÃ¡n.");
                }
                KhachHang_DAO.doiDiem(con, h.getKhachHang().getMaKhachHang(), diemHoan - diemThu);
            }
            if (ghi && (!Double.isFinite(daXacNhan) || InHoaDon.soSanhTien(hoan, daXacNhan) != 0)) {
                throw new SQLException("Sá»‘ tiá»n hoÃ n Ä‘Ã£ thay Ä‘á»•i. Kiá»ƒm tra vÃ  xÃ¡c nháº­n láº¡i.");
            }
            String sql11 =
                    "UPDATE dbo.PhieuDoiTra SET TrangThai=N'HoÃ n"
                            + " táº¥t',MaPhanCongHoanTien=?,DiemHoanLai=?,DiemThuHoi=? WHERE"
                            + " MaPhieuDoiTra=?";
            try (PreparedStatement stmt12 = con.prepareStatement(sql11)) {
                stmt12.setString(1, ca);
                stmt12.setInt(2, diemHoan);
                stmt12.setInt(3, diemThu);
                stmt12.setString(4, id);
                stmt12.executeUpdate();
            }
            if (ghi) {
                con.commit();
            } else {
                con.rollback();
            }
            return hoan;
        } catch (SQLException | RuntimeException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(tuDongCommitCu);
            con.setTransactionIsolation(mucCoLapCu);
        }
    }

    private static String docThietLap(String key, String macDinh) {
        java.util.Properties p = new java.util.Properties();
        java.nio.file.Path file = java.nio.file.Paths.get("config.properties");
        if (!java.nio.file.Files.exists(file))
            file = java.nio.file.Paths.get("config.example.properties");
        if (!java.nio.file.Files.exists(file)) return macDinh;
        try (java.io.Reader r = java.nio.file.Files.newBufferedReader(file)) {
            p.load(r);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return macDinh;
        }
        String v = p.getProperty(key, macDinh);
        return v == null || v.trim().isEmpty() ? macDinh : v.trim();
    }

    private static int docSoThietLap(String key, int macDinh) {
        try {
            return Integer.parseInt(docThietLap(key, ""));
        } catch (NumberFormatException e) {
            return macDinh;
        }
    }
}
