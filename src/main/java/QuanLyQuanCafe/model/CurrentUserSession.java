package QuanLyQuanCafe.model;

// Sử dụng mẫu thiết kế Singleton để đảm bảo chỉ có một phiên đăng nhập tại một thời điểm
public final class CurrentUserSession {

    private static CurrentUserSession instance;

    private TaiKhoan loggedInUser;

    private CurrentUserSession() {}

    public static CurrentUserSession getInstance() {
        if (instance == null) {
            instance = new CurrentUserSession();
        }
        return instance;
    }

    public void login(TaiKhoan user) {
        this.loggedInUser = user;
    }

    public void logout() {
        this.loggedInUser = null;
    }

    public TaiKhoan getLoggedInUser() {
        return loggedInUser;
    }

    public boolean isLoggedIn() {
        return this.loggedInUser != null;
    }
    
    public boolean isQuanLy() {
        if (!isLoggedIn()) {
            return false;
        }
        // So sánh vai trò, "QUANLY" là chuỗi ta đã đặt trong CSDL
        return "QUANLY".equalsIgnoreCase(loggedInUser.getRole());
    }
}