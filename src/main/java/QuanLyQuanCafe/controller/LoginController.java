package QuanLyQuanCafe.controller;

import QuanLyQuanCafe.App;
import QuanLyQuanCafe.database.UserDB;
import QuanLyQuanCafe.enums.UserRole;
import QuanLyQuanCafe.model.User;
import java.io.IOException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import QuanLyQuanCafe.crypto.Hashing;
import QuanLyQuanCafe.database.sqlserver;

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
        String hashedPassword = Hashing.hashPassword(password);

    }



}
