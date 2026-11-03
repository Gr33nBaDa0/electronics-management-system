package connectDB;

import java.security.Security;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
    static {
        // Cho phép giao thức TLSv1 và TLSv1.1 để kết nối thành công với SQL Server trên Java 17+
        try {
            System.setProperty("jdk.tls.client.protocols", "TLSv1.2,TLSv1.1,TLSv1");
            String disabledAlgorithms = Security.getProperty("jdk.tls.disabledAlgorithms");
            if (disabledAlgorithms != null) {
                disabledAlgorithms = disabledAlgorithms
                        .replace("TLSv1.1,", "")
                        .replace("TLSv1,", "")
                        .replace(", TLSv1.1", "")
                        .replace(", TLSv1", "")
                        .replace("TLSv1.1", "")
                        .replace("TLSv1", "");
                Security.setProperty("jdk.tls.disabledAlgorithms", disabledAlgorithms);
            }
        } catch (Exception ignored) {
        }
    }

	public static Connection con = null;
	private static ConnectDB instance = new ConnectDB();
	
	public static ConnectDB getInstance() {
		return instance;
	}
	
	public void connect() throws SQLException {
	   
	    String url = "jdbc:sqlserver://localhost:1433;databaseName=GreenSmart;trustServerCertificate=true;encrypt=false;";
	    String user = "sa";
	    String password = "sapassword"; 
	    con = DriverManager.getConnection(url, user, password);
	}
	public static Connection getConnection() {
		try {
	       
	        if (con == null || con.isClosed()) {
	            getInstance().connect(); 
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return con;
	}
}
