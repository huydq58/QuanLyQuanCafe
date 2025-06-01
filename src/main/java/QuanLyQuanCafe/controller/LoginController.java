package QuanLyQuanCafe.controller;

import  QuanLyQuanCafe.*;
import  QuanLyQuanCafe.model.*;
import  QuanLyQuanCafe.database.*;

import javafx.fxml.FXML;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import QuanLyQuanCafe.crypto.Hashing;
import org.mindrot.jbcrypt.BCrypt;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void gotoView() throws IOException {

        App.setRoot("mainScreen");
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isBlank() || password.isBlank()) {
            showAlert("Lỗi", "Vui lòng nhập tên đăng nhập và mật khẩu.");
            return;
        }

        try {
            // Tạo DataProvider và DAL
            DataProvider dp = new DataProvider();
            TaiKhoanDAL taiKhoanDAL = new TaiKhoanDAL(dp);

            // 1. Lấy tài khoản từ cơ sở dữ liệu
            TaiKhoan tk = taiKhoanDAL.getTaiKhoanByUsername(username);

            if (tk == null) {
                showAlert("Đăng nhập thất bại", "Tên đăng nhập không tồn tại.");
                return;
            }

            // 2. So sánh mật khẩu nhập với hash từ DB
            boolean verified = BCrypt.checkpw(password, tk.getMatKhauHash());

            if (!verified) {
                showAlert("Đăng nhập thất bại", "Mật khẩu không chính xác.");
                return;
            }

            // 3. Kiểm tra quyền nếu cần (ví dụ: admin)
            boolean isAdmin = false;
            if (tk.getMaNV() != null) {
                isAdmin = taiKhoanDAL.getIsAdminByMaNV(tk.getMaNV());
            }

            // ✅ Đăng nhập thành công
            showAlert("Thành công", "Đăng nhập thành công!\nQuyền: " + (isAdmin ? "Admin" : "Nhân viên"));

            // 👉 TODO: chuyển sang giao diện chính
            // loadMainScene(tk, isAdmin);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Đã xảy ra lỗi khi đăng nhập: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }




}
