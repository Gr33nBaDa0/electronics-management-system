package common;

import entity.NhanVien;
import entity.TaiKhoan;

public class UserSession {
    // Biến static lưu tài khoản đang đăng nhập
    public static TaiKhoan currentAccount = null;
    public static NhanVien currentUser = null;
    
    // Hàm đăng xuất
    public static void logout() {
        currentAccount = null;
        currentUser = null;
    }
}