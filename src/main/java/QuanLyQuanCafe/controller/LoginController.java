package QuanLyQuanCafe.controller;

import  org.mindrot.jbcrypt.BCrypt;

import  QuanLyQuanCafe.App;
import  QuanLyQuanCafe.database.DataProvider;
import QuanLyQuanCafe.model.CurrentUserSession;
import QuanLyQuanCafe.model.TaiKhoan;
import QuanLyQuanCafe.model.TaiKhoanDAL;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isBlank() || password.isBlank()) {
            showAlert("Lỗi", "Vui lòng nhập tên đăng nhập và mật khẩu.");
            return;
        }

        try {
            DataProvider dp = new DataProvider();
            TaiKhoanDAL taiKhoanDAL = new TaiKhoanDAL(dp);

            TaiKhoan tk = taiKhoanDAL.getTaiKhoanByUsername(username);

            if (tk == null) {
                showAlert("Đăng nhập thất bại", "Tên đăng nhập không tồn tại.");
                return;
            }

            boolean verified = BCrypt.checkpw(password, tk.getMatKhauHash());

            if (!verified) {
                showAlert("Đăng nhập thất bại", "Mật khẩu không chính xác.");
                return;
            }
            
            // === THAY ĐỔI QUAN TRỌNG: LƯU PHIÊN ĐĂNG NHẬP ===
            CurrentUserSession.getInstance().login(tk);
            System.out.println("User " + tk.getTenDangNhap() + " with role " + tk.getRole() + " logged in.");
            // ===============================================

            showAlert("Thành công", "Đăng nhập thành công!");
            App.setRoot("MainWindow");

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