package connectDB;

import java.security.Security;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
	public static Connection con = null;
	private static ConnectDB instance = new ConnectDB();
	
	public static ConnectDB getInstance() {
		return instance;
	}
	
	public void connect() throws SQLException {
	   
	    String url = "jdbc:sqlserver://localhost:1433;databaseName=GreenSmart";
	    String user = "sa";
	    String password = "Sa@123456"; 
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
