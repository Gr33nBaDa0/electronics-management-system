package util;

import entity.ChiTietHoaDon;
import entity.HoaDon;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;

/** Các hàm in hóa đơn, tính tiền, điểm tích lũy và tạo QR ngân hàng. */
public final class InHoaDon {
    public static String noiDung(HoaDon h, List<ChiTietHoaDon> ds) {
        StringBuilder s = new StringBuilder("GREENSMART — HÓA ĐƠN BÁN HÀNG\n\n");
        s.append("Mã: ")
                .append(h.getMaHoaDon())
                .append("\nNgày lập: ")
                .append(h.getNgayLap())
                .append("\nNhân viên: ")
                .append(h.getNhanVien().getMaNhanVien())
                .append("\nKhách: ")
                .append(h.getKhachHang() == null ? "Vãng lai" : h.getKhachHang().getMaKhachHang())
                .append("\nTrạng thái: ")
                .append(h.getTrangThaiThanhToan())
                .append("\nThanh toán: ")
                .append(h.getHinhThucThanhToan())
                .append("\n\n");
        for (ChiTietHoaDon d : ds)
            s.append(d.getSanPham().getMaSanPham())
                    .append(" — ")
                    .append(d.getSanPham().getTenSanPham())
                    .append("\n  ")
                    .append(d.getSoLuong())
                    .append(" x ")
                    .append(tien(d.getDonGia()))
                    .append("; giảm ")
                    .append(tien(d.getGiamGia()))
                    .append("; còn ")
                    .append(tien(d.getThanhTienChiTiet()))
                    .append("\n");
        s.append("\nTổng hàng: ")
                .append(tien(tongHang(ds)))
                .append("\nGiảm khuyến mãi: ")
                .append(tien(tongHang(ds).subtract(sauKhuyenMai(ds))))
                .append("\nĐiểm dùng: ")
                .append(h.getDiemSuDung())
                .append("\nVAT: ")
                .append(tien(vat(ds, h.getDiemSuDung(), h.getThueSuatVAT())))
                .append("\nPHẢI TRẢ: ")
                .append(tien(phaiTra(ds, h.getDiemSuDung(), h.getThueSuatVAT())))
                .append(" VND\n\nCảm ơn quý khách!");
        return s.toString();
    }

    public static final BigDecimal MOT_DIEM = new BigDecimal("1000");

    public static BigDecimal tron(BigDecimal n) {
        return n.setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal tongHang(List<ChiTietHoaDon> ds) {
        BigDecimal t = BigDecimal.ZERO;
        for (ChiTietHoaDon d : ds)
            t = t.add(d.getDonGia().multiply(BigDecimal.valueOf(d.getSoLuong())));
        return tron(t);
    }

    public static BigDecimal sauKhuyenMai(List<ChiTietHoaDon> ds) {
        BigDecimal t = BigDecimal.ZERO;
        for (ChiTietHoaDon d : ds) t = t.add(d.getThanhTienChiTiet());
        return tron(t);
    }

    public static int diemToiDa(BigDecimal sauGiam, int soDu) {
        return Math.min(
                soDu, sauGiam.divide(new BigDecimal("2000"), 0, RoundingMode.DOWN).intValueExact());
    }

    public static BigDecimal vat(List<ChiTietHoaDon> ds, int diem, BigDecimal thue) {
        return tron(
                sauKhuyenMai(ds)
                        .subtract(MOT_DIEM.multiply(BigDecimal.valueOf(diem)))
                        .multiply(thue));
    }

    public static BigDecimal phaiTra(List<ChiTietHoaDon> ds, int diem, BigDecimal thue) {
        return tron(
                sauKhuyenMai(ds)
                        .subtract(MOT_DIEM.multiply(BigDecimal.valueOf(diem)))
                        .add(vat(ds, diem, thue)));
    }

    public static int diemCong(BigDecimal thanhToan) {
        return thanhToan.divide(new BigDecimal("100000"), 0, RoundingMode.DOWN).intValueExact();
    }

    // Làm tròn lũy kế: lần trả cuối nhận phần chênh còn lại, không vượt tổng gốc.
    public static BigDecimal luyKe(BigDecimal giaTri, int daTra, int tongSoLuong) {
        return giaTri.multiply(BigDecimal.valueOf(daTra))
                .divide(BigDecimal.valueOf(tongSoLuong), 2, RoundingMode.HALF_UP);
    }

    public static Map<String, BigDecimal> phanBo(
            List<ChiTietHoaDon> ds, int diem, BigDecimal thue) {
        Map<String, BigDecimal> kq = new LinkedHashMap<>();
        BigDecimal a = sauKhuyenMai(ds),
                total = phaiTra(ds, diem, thue),
                da = BigDecimal.ZERO,
                luyKeHang = BigDecimal.ZERO;
        for (int i = 0; i < ds.size(); i++) {
            ChiTietHoaDon d = ds.get(i);
            luyKeHang = luyKeHang.add(d.getThanhTienChiTiet());
            BigDecimal muc =
                    i == ds.size() - 1
                            ? total
                            : (a.signum() == 0
                                    ? BigDecimal.ZERO
                                    : total.multiply(luyKeHang).divide(a, 2, RoundingMode.HALF_UP));
            BigDecimal v = muc.subtract(da);
            kq.put(d.getSanPham().getMaSanPham(), v);
            da = da.add(v);
        }
        return kq;
    }

    public static String tien(BigDecimal n) {
        return n == null
                ? ""
                : new DecimalFormat("#,##0.##", DecimalFormatSymbols.getInstance(Locale.US))
                        .format(n);
    }

    // Ô nhập tiền dùng số không phân cách hoặc dấu phẩy hàng nghìn, dấu chấm thập phân.
    public static BigDecimal soTien(String s) {
        return new BigDecimal(s.trim().replace(",", "")).setScale(2, RoundingMode.HALF_UP);
    }

    public static void kiemTraCauHinhQR() throws SQLException {
        if (!docThietLap("payment.bankBin", "").matches("\\d{6}")
                || !docThietLap("payment.account", "").matches("\\d{4,30}")
                || docThietLap("payment.accountName", "").isEmpty())
            throw new SQLException(
                    "Chưa điền đúng BIN ngân hàng, tài khoản và tên chủ tài khoản tại màn Sao lưu /"
                        + " thiết lập.");
    }

    public static String urlQR(String ma, BigDecimal tien) throws Exception {
        kiemTraCauHinhQR();
        if (tien.signum() <= 0
                || tien.stripTrailingZeros().scale() > 0
                || tien.toBigInteger().toString().length() > 13)
            throw new SQLException(
                    "QR ngân hàng yêu cầu số tiền nguyên VND dương, tối đa 13 chữ số. Chọn tiền mặt"
                            + " nếu hóa đơn có phần lẻ.");
        return "https://img.vietqr.io/image/"
                + docThietLap("payment.bankBin", "")
                + "-"
                + docThietLap("payment.account", "")
                + "-compact2.png?amount="
                + tien.toPlainString()
                + "&addInfo="
                + URLEncoder.encode(ma, "UTF-8")
                + "&accountName="
                + URLEncoder.encode(docThietLap("payment.accountName", ""), "UTF-8");
    }

    public static BufferedImage taoQR(String ma, BigDecimal tien) throws Exception {
        HttpURLConnection c = (HttpURLConnection) new URL(urlQR(ma, tien)).openConnection();
        c.setConnectTimeout(7000);
        c.setReadTimeout(7000);
        try (InputStream in = c.getInputStream()) {
            BufferedImage img = ImageIO.read(in);
            if (img == null) throw new IOException("Dịch vụ QR không trả ảnh.");
            return img;
        } finally {
            c.disconnect();
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

    private InHoaDon() {}
}
