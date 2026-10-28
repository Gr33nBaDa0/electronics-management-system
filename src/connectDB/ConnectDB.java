package connectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
    // Đã cập nhật đúng tên DB theo script SQL mới nhất của bạn
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=GreenSmart_DB;encrypt=true;trustServerCertificate=true;";
    private static final String USER = "sa"; // Tên đăng nhập SQL của bạn
    private static final String PASSWORD = "123456"; // Mật khẩu SQL của bạn (hãy sửa lại)

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy thư viện JDBC. Hãy add file mssql-jdbc...jar vào Build Path!");
        } catch (SQLException e) {
            System.err.println("Kết nối CSDL thất bại! Kiểm tra lại URL, User hoặc Password.");
            e.printStackTrace();
        }
        return conn;
    }

    // Chạy hàm này để test kết nối
    public static void main(String[] args) {
        Connection conn = getConnection();
        if (conn != null) {
            System.out.println("KẾT NỐI CƠ SỞ DỮ LIỆU GREENSMART_DB THÀNH CÔNG!");
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("KẾT NỐI THẤT BẠI!");
        }
    }
}