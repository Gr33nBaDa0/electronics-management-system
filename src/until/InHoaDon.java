package until;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.parser.LoaderContext;
import com.github.weisj.jsvg.parser.SVGLoader;
import com.github.weisj.jsvg.parser.resources.ResourcePolicy;
import com.github.weisj.jsvg.view.FloatSize;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import entity.ChiTietHoaDon;
import entity.HoaDon;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;

/** Các hàm in hóa đơn, tính tiền, điểm tích lũy và tạo QR ngân hàng. */
public final class InHoaDon {
    // Thông tin cửa hàng dùng chung trên hóa đơn.
    private static final String TEN_CUA_HANG = "GREENSMART", DIA_CHI_CUA_HANG = "12 Nguyễn Văn Bảo", SDT_CUA_HANG = "0343519459";

    public static String noiDung(HoaDon h, List<ChiTietHoaDon> ds) {
        StringBuilder s = new StringBuilder("GREENSMART — HÓA ĐƠN BÁN HÀNG\n\n");
        s.append("Mã: ").append(h.getMaHoaDon()).append("\nNgày lập: ").append(h.getNgayLap()).append("\nNhân viên: ")
                .append(h.getNhanVien().getMaNhanVien()).append("\nKhách: ")
                .append(h.getKhachHang() == null ? "Vãng lai" : h.getKhachHang().getMaKhachHang()).append("\nTrạng thái: ")
                .append(h.getTrangThaiThanhToan()).append("\nThanh toán: ").append(h.getHinhThucThanhToan()).append("\n\n");
        for (ChiTietHoaDon d : ds)
            s.append(d.getSanPham().getMaSanPham()).append(" — ").append(d.getSanPham().getTenSanPham()).append("\n  ")
                    .append(d.getSoLuong()).append(" x ").append(tien(d.getDonGia())).append("; giảm ").append(tien(d.getGiamGia()))
                    .append("; còn ").append(tien(d.getThanhTienChiTiet())).append("\n");
        s.append("\nTổng hàng: ").append(tien(tongHang(ds))).append("\nGiảm khuyến mãi: ")
                .append(tien(tron(tongHang(ds) - sauKhuyenMai(ds)))).append("\nĐiểm dùng: ").append(h.getDiemSuDung()).append("\nVAT: ")
                .append(tien(vat(ds, h.getDiemSuDung(), h.getThueSuatVAT()))).append("\nPHẢI TRẢ: ")
                .append(tien(phaiTra(ds, h.getDiemSuDung(), h.getThueSuatVAT()))).append(" VND\n\nCảm ơn quý khách!");
        return s.toString();
    }

    public static final double MOT_DIEM = 1000.0;

    // Làm tròn tiền đến 2 chữ số, đối xứng cho cả tiền âm.
    public static double tron(double n) {
        if (!Double.isFinite(n) || !Double.isFinite(n * 100.0))
            throw new IllegalArgumentException("Số tiền không hợp lệ.");
        double giaTri = Math.abs(n) * 100.0;
        // Bù sai số nhị phân ngay sát mốc nửa xu; số lớn đã không còn phần lẻ xu.
        double ketQua = giaTri < 4503599627370496.0
                ? Math.floor(Math.nextUp(giaTri) + 0.5) : giaTri;
        double tien = Math.copySign(ketQua / 100.0, n);
        return tien == 0.0 ? 0.0 : tien;
    }

    public static int soSanhTien(double a, double b) {
        return Double.compare(tron(a), tron(b));
    }

    public static double tongHang(List<ChiTietHoaDon> ds) {
        double tong = 0.0;
        for (ChiTietHoaDon d : ds) tong += d.getDonGia() * d.getSoLuong();
        return tron(tong);
    }

    public static double sauKhuyenMai(List<ChiTietHoaDon> ds) {
        double tong = 0.0;
        for (ChiTietHoaDon d : ds) tong += d.getThanhTienChiTiet();
        return tron(tong);
    }

    public static int diemToiDa(double sauGiam, int soDu) {
        double diem = Math.floor(tron(sauGiam) / 2000.0);
        return (int) Math.min(soDu, diem);
    }

    public static double vat(List<ChiTietHoaDon> ds, int diem, double thue) {
        return tron((sauKhuyenMai(ds) - MOT_DIEM * diem) * thue);
    }

    public static double phaiTra(List<ChiTietHoaDon> ds, int diem, double thue) {
        return tron(sauKhuyenMai(ds) - MOT_DIEM * diem + vat(ds, diem, thue));
    }

    public static int diemCong(double thanhToan) {
        double diem = tron(thanhToan) / 100000.0;
        diem = diem < 0 ? Math.ceil(diem) : Math.floor(diem);
        if (diem > Integer.MAX_VALUE || diem < Integer.MIN_VALUE)
            throw new ArithmeticException("Số điểm vượt giới hạn cho phép.");
        return (int) diem;
    }

    // Làm tròn lũy kế để lần trả cuối nhận đúng phần tiền còn lại.
    public static double luyKe(double giaTri, int daTra, int tongSoLuong) {
        if (tongSoLuong <= 0) throw new IllegalArgumentException("Số lượng gốc phải lớn hơn 0.");
        return tron(giaTri * daTra / tongSoLuong);
    }

    public static Map<String, Double> phanBo(List<ChiTietHoaDon> ds, int diem, double thue) {
        Map<String, Double> kq = new LinkedHashMap<>();
        double hang = sauKhuyenMai(ds), tong = phaiTra(ds, diem, thue);
        double daPhanBo = 0.0, luyKeHang = 0.0;
        for (int i = 0; i < ds.size(); i++) {
            ChiTietHoaDon d = ds.get(i);
            luyKeHang = tron(luyKeHang + d.getThanhTienChiTiet());
            double muc = i == ds.size() - 1 ? tong
                    : (hang == 0.0 ? 0.0 : tron(tong * luyKeHang / hang));
            double tien = tron(muc - daPhanBo);
            kq.put(d.getSanPham().getMaSanPham(), tien);
            daPhanBo = tron(daPhanBo + tien);
        }
        return kq;
    }

    public static String tien(double n) {
        return new DecimalFormat("#,##0.##", DecimalFormatSymbols.getInstance(Locale.US)).format(tron(n));
    }

    // Ô nhập tiền dùng dấu phẩy hàng nghìn và dấu chấm thập phân.
    public static double soTien(String s) {
        double giaTri = Double.parseDouble(s.trim().replace(",", ""));
        if (!Double.isFinite(giaTri) || !Double.isFinite(giaTri * 100.0))
            throw new NumberFormatException("Số tiền không hợp lệ.");
        return tron(giaTri);
    }

    public static void kiemTraCauHinhQR() throws SQLException {
        if (!docThietLap("payment.bankBin", "").matches("\\d{6}") || !docThietLap("payment.account", "").matches("\\d{4,30}")
                || docThietLap("payment.accountName", "").isEmpty())
            throw new SQLException( "Chưa điền đúng BIN ngân hàng, tài khoản và tên chủ tài khoản tại màn Sao lưu /" + " thiết lập.");
    }

    public static String urlQR(String ma, double tien) throws Exception {
        kiemTraCauHinhQR();
        if (!Double.isFinite(tien) || tien <= 0 || tien != Math.rint(tien) || tien > 9999999999999.0)
            throw new SQLException( "QR ngân hàng yêu cầu số tiền nguyên VND dương, tối đa 13 chữ số. Chọn tiền mặt"
                            + " nếu hóa đơn có phần lẻ.");
        return "https://img.vietqr.io/image/" + docThietLap("payment.bankBin", "") + "-" + docThietLap("payment.account", "")
                + "-compact2.png?amount=" + Long.toString((long) tien) + "&addInfo="
                + URLEncoder.encode(ma, "UTF-8") + "&accountName=" + URLEncoder.encode(docThietLap("payment.accountName", ""), "UTF-8");
    }

    public static BufferedImage taoQR(String ma, double tien) throws Exception {
        HttpURLConnection c = (HttpURLConnection) new URL(urlQR(ma, tien)).openConnection();
        c.setConnectTimeout(7000);
        c.setReadTimeout(7000);
        c.setRequestProperty("Accept", "image/png,image/svg+xml;q=0.9");
        c.setRequestProperty("User-Agent", "GreenSmart/1.0");
        try {
            int code = c.getResponseCode();
            if (code == 429 || code >= 500)
                throw new IOException( "Dịch vụ QR tạm thời chưa đáp ứng (HTTP " + code + "). Hãy thử lại sau.");
            if (code != HttpURLConnection.HTTP_OK)
                throw new IOException( "Không tạo được QR (HTTP " + code + "). Kiểm tra BIN ngân hàng và số tài khoản ở Sao lưu / thiết"
                                + " lập.");
            try (InputStream in = c.getInputStream()) {
                byte[] data = in.readNBytes(2_000_001);
                if (data.length > 2_000_000) throw new IOException("Ảnh QR trả về quá lớn.");
                return docAnhQR(data);
            }
        } catch (java.net.SocketTimeoutException e) {
            throw new IOException( "Dịch vụ QR phản hồi quá thời gian. Kiểm tra mạng rồi thử lại.", e);
        } catch (java.net.UnknownHostException e) {
            throw new IOException("Không kết nối được dịch vụ QR. Kiểm tra Internet.", e);
        } finally {
            c.disconnect();
        }
    }

    /** Đọc cả PNG lẫn toàn bộ SVG (ảnh nền, chữ, logo và các ô QR dạng vector). */
    private static BufferedImage docAnhQR(byte[] data) throws IOException {
        BufferedImage anh = ImageIO.read(new ByteArrayInputStream(data));
        if (anh != null) return anh;
        try {
            LoaderContext context = LoaderContext.builder()
                    .externalResourcePolicy(ResourcePolicy.DENY_EXTERNAL).build();
            SVGDocument svg = new SVGLoader().load(new ByteArrayInputStream(data), null, context);
            if (svg == null) throw new IOException("Dịch vụ QR không trả ảnh PNG hoặc SVG hợp lệ.");
            FloatSize size = svg.size();
            if (!Float.isFinite(size.width) || !Float.isFinite(size.height) || size.width <= 0 || size.height <= 0 || size.width > 2000
                    || size.height > 2000) throw new IOException("Kích thước ảnh QR không hợp lệ.");
            anh = new BufferedImage((int) Math.ceil(size.width), (int) Math.ceil(size.height), BufferedImage.TYPE_INT_RGB);
            Graphics2D g = anh.createGraphics();
            try {
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, anh.getWidth(), anh.getHeight());
                svg.render(null, g);
            } finally {
                g.dispose();
            }
            return anh;
        } catch (IOException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new IOException("Không đọc được ảnh QR từ dịch vụ. Hãy thử lại sau.", e);
        }
    }

    /** Xuất PDF từ hóa đơn đã lưu; chỉ đọc dữ liệu, không ghi lại thanh toán. */
    public static File xuatFilePDF(String ma) throws Exception {
        HoaDon h = new dao.HoaDon_DAO().tim(ma);
        if (h == null || !"Đã thanh toán".equals(h.getTrangThaiThanhToan()))
            throw new IllegalArgumentException("Chỉ in hóa đơn đã thanh toán.");
        List<ChiTietHoaDon> ds = new dao.ChiTietHoaDon_DAO().getTheoHoaDon(ma);
        for (entity.NhanVien nv : new dao.NhanVien_DAO().getAllNhanVien())
            if (nv.getMaNhanVien().equals(h.getNhanVien().getMaNhanVien())) h.setNhanVien(nv);
        if (h.getKhachHang() != null)
            for (entity.KhachHang kh : new dao.KhachHang_DAO().getAllKhachHang())
                if (kh.getMaKhachHang().equals(h.getKhachHang().getMaKhachHang()))
                    h.setKhachHang(kh);
        return xuatFilePDF(h, ds);
    }

    public static File xuatFilePDF(HoaDon hd, List<ChiTietHoaDon> gioHang) throws Exception {
        if (hd == null || !"Đã thanh toán".equals(hd.getTrangThaiThanhToan()))
            throw new IllegalArgumentException("Chỉ in hóa đơn đã thanh toán.");
        File folder = new File("D:/HoaDonGreenSmart");
        Files.createDirectories(folder.toPath());
        String ma = hd.getMaHoaDon().replaceAll("[^A-Za-z0-9_-]", "_");
        File file = new File(folder, "HoaDon_" + ma + ".pdf");

        PdfWriter writer = new PdfWriter(file.getAbsolutePath());
        PdfDocument pdfDoc = new PdfDocument(writer);
        try (Document document = new Document(pdfDoc)) {
            String font = new File("C:/Windows/Fonts/arial.ttf").exists() ? "C:/Windows/Fonts/arial.ttf" : "resource/fonts/DejaVuSans.ttf";
            document.setFont(PdfFontFactory.createFont(font, PdfEncodings.IDENTITY_H)).setFontSize(11);
            document.add(new Paragraph(TEN_CUA_HANG).setBold().setFontSize(20).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Địa chỉ: " + DIA_CHI_CUA_HANG + " | ĐT: " + SDT_CUA_HANG)
                    .setFontSize(10).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("HÓA ĐƠN THANH TOÁN").setBold().setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER).setMarginTop(8));
            document.add(new LineSeparator(new SolidLine(1f)).setMarginTop(4).setMarginBottom(8));

            String tenKH = hd.getKhachHang() == null ? "Khách vãng lai" : hd.getKhachHang().getHoTen();
            if (tenKH == null || tenKH.isBlank()) tenKH = hd.getKhachHang().getMaKhachHang();
            String sdtKH = hd.getKhachHang() == null ? "" : hd.getKhachHang().getSoDienThoai();
            String tenNV = hd.getNhanVien() == null ? "---" : hd.getNhanVien().getHoTen();
            if (tenNV == null || tenNV.isBlank()) tenNV = hd.getNhanVien().getMaNhanVien();
            String pttt = hd.getHinhThucThanhToan();
            Map<String, String> cacKM = new LinkedHashMap<>();
            for (ChiTietHoaDon ct : gioHang) {
                entity.ChuongTrinhKhuyenMai km = ct.getChuongTrinhKhuyenMai();
                if (km != null) cacKM.put(km.getMaKhuyenMai(), km.getTenChuongTrinh() == null
                        || km.getTenChuongTrinh().isBlank() ? km.getMaKhuyenMai() : km.getTenChuongTrinh());
            }
            double giamGia = tron(tongHang(gioHang) - sauKhuyenMai(gioHang));
            String khuyenMai = cacKM.isEmpty() ? (giamGia == 0 ? "Không" : "Giảm theo sản phẩm")
                    : String.join(", ", cacKM.values());
            document.add(new Paragraph()
                    .add("Mã HĐ     : " + hd.getMaHoaDon() + "\n")
                    .add("Ngày      : " + (hd.getNgayLap() == null ? "" : hd.getNgayLap()
                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))) + "\n")
                    .add("Khách hàng: " + tenKH + (sdtKH == null || sdtKH.isBlank() ? "" : " | SĐT: " + sdtKH) + "\n")
                    .add("Nhân viên : " + tenNV + "\n")
                    .add("Thanh toán: " + pttt + "\n")
                    .add("Trạng thái: " + hd.getTrangThaiThanhToan() + "\n")
                    .add("Khuyến mãi: " + khuyenMai).setCharacterSpacing(0.3f));
            document.add(new LineSeparator(new SolidLine(1f)).setMarginTop(4).setMarginBottom(8));

            document.add(new Paragraph("CHI TIẾT ĐƠN HÀNG").setBold().setFontSize(12).setKeepWithNext(true));
            float[] colW = {.6f, 3.4f, .6f, 1.6f, 1.4f, 1.8f};
            Table table = new Table(UnitValue.createPercentArray(colW)).useAllAvailableWidth();
            String[] headers = {"STT", "Sản phẩm", "SL", "Đơn giá", "Giảm KM", "Thành tiền"};
            for (String h : headers)
                table.addHeaderCell(new Cell().add(new Paragraph(h).setBold()).setFontSize(9).setTextAlignment(TextAlignment.CENTER));
            for (int i = 0; i < gioHang.size(); i++) {
                ChiTietHoaDon ct = gioHang.get(i);
                String ten = ct.getSanPham().getTenSanPham() + "\n" + ct.getSanPham().getMaSanPham();
                table.addCell(new Cell().add(new Paragraph(String.valueOf(i + 1))).setKeepTogether(true).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(ten)).setKeepTogether(true));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(ct.getSoLuong()))).setKeepTogether(true).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(tien(ct.getDonGia()))).setKeepTogether(true).setTextAlignment(TextAlignment.RIGHT));
                table.addCell(new Cell().add(new Paragraph(tien(ct.getGiamGia()))).setKeepTogether(true).setTextAlignment(TextAlignment.RIGHT));
                table.addCell(new Cell().add(new Paragraph(tien(ct.getThanhTienChiTiet()))).setKeepTogether(true).setTextAlignment(TextAlignment.RIGHT));
            }
            document.add(table.setFontSize(10));

            double tienDiem = MOT_DIEM * hd.getDiemSuDung();
            double truocVAT = tron(sauKhuyenMai(gioHang) - tienDiem);
            double tienVAT = vat(gioHang, hd.getDiemSuDung(), hd.getThueSuatVAT());
            double thanhToan = phaiTra(gioHang, hd.getDiemSuDung(), hd.getThueSuatVAT());
            String thueSuat = tien(hd.getThueSuatVAT() * 100.0);
            Div tong = new Div().setKeepTogether(true).setMarginTop(18);
            tong.add(new Paragraph("Tổng tiền hàng : " + tien(tongHang(gioHang)) + " VND").setTextAlignment(TextAlignment.RIGHT));
            tong.add(new Paragraph("Giảm khuyến mãi : " + (giamGia == 0 ? "" : "-") + tien(giamGia) + " VND").setTextAlignment(TextAlignment.RIGHT));
            tong.add(new Paragraph("Điểm sử dụng (" + hd.getDiemSuDung() + " điểm) : " + (tienDiem == 0 ? "" : "-") + tien(tienDiem) + " VND").setTextAlignment(TextAlignment.RIGHT));
            tong.add(new Paragraph("Tổng trước VAT : " + tien(truocVAT) + " VND").setTextAlignment(TextAlignment.RIGHT));
            tong.add(new Paragraph("VAT (" + thueSuat + "%) : " + tien(tienVAT) + " VND").setTextAlignment(TextAlignment.RIGHT));
            tong.add(new LineSeparator(new SolidLine(1f)).setMarginTop(4).setMarginBottom(8));
            tong.add(new Paragraph("THÀNH TIỀN : " + tien(thanhToan) + " VND").setBold().setFontSize(15).setTextAlignment(TextAlignment.RIGHT));
            tong.add(new LineSeparator(new SolidLine(1f)).setMarginTop(4).setMarginBottom(8));
            document.add(tong);

            Div cuoi = new Div().setKeepTogether(true);
            if (pttt != null && pttt.toLowerCase(Locale.ROOT).contains("qr") && thanhToan > 0) {
                try {
                    BufferedImage anhQR = taoQR(hd.getMaHoaDon(), thanhToan);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    ImageIO.write(anhQR, "png", out);
                    Image qrImg = new Image(ImageDataFactory.create(out.toByteArray()));
                    qrImg.scaleToFit(140, 166).setHorizontalAlignment(HorizontalAlignment.CENTER);
                    cuoi.add(new Paragraph("QR tham chiếu giao dịch - hóa đơn đã thanh toán").setFontSize(8).setTextAlignment(TextAlignment.CENTER));
                    cuoi.add(qrImg);
                } catch (Exception e) {
                    cuoi.add(new Paragraph("Không tải được QR tham chiếu. Hóa đơn đã được thanh toán.").setFontSize(8).setTextAlignment(TextAlignment.CENTER));
                }
            }
            cuoi.add(new LineSeparator(new SolidLine(1f)).setMarginTop(4).setMarginBottom(8));
            cuoi.add(new Paragraph("Cảm ơn quý khách! Hẹn gặp lại!").setItalic().setFontSize(12).setTextAlignment(TextAlignment.CENTER));
            document.add(cuoi);
        }
        return file;
    }


    public static boolean moFilePDF(File file) throws IOException {
        if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.OPEN))
            return false;
        Desktop.getDesktop().open(file);
        return true;
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
