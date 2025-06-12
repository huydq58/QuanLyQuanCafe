package QuanLyQuanCafe.controller;

import QuanLyQuanCafe.crypto.Hashing;
import QuanLyQuanCafe.database.DataProvider;
import QuanLyQuanCafe.model.CurrentUserSession;
import QuanLyQuanCafe.model.TaiKhoan;
import QuanLyQuanCafe.model.TaiKhoanDAL;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Objects;

public class AccountTabController {

    @FXML private TableView<TaiKhoan> accountTableView;
    @FXML private TableColumn<TaiKhoan, String> usernameCol;
    @FXML private TableColumn<TaiKhoan, Integer> staffIdCol;
    @FXML private TableColumn<TaiKhoan, String> roleCol;

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField staffIdField;
    @FXML private ChoiceBox<String> roleChoiceBox;
    @FXML private Button saveAccountButton;

    private TaiKhoanDAL taiKhoanDAL;
    private TaiKhoan currentTaiKhoan;
    private TaiKhoan selectedAccount;
    private boolean isNewAccount = false;

    @FXML
    public void initialize() {
        DataProvider provider = new DataProvider();
        taiKhoanDAL = new TaiKhoanDAL(provider);
        currentTaiKhoan =CurrentUserSession.getInstance().getLoggedInUser();
        selectedAccount = new TaiKhoan();
        // Cấu hình bảng
        usernameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTenDangNhap()));
        staffIdCol.setCellValueFactory(cellData -> {
            Integer maNV = cellData.getValue().getMaNV();
            return new SimpleIntegerProperty(maNV == null ? 0 : maNV).asObject();
        });
        roleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));

        // Cấu hình ChoiceBox
        roleChoiceBox.setItems(FXCollections.observableArrayList("NHANVIEN", "QUANLY"));

        // Listener cho bảng
        accountTableView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> populateForm(newVal)
        );

        loadAccounts();
    }

    private void loadAccounts() {
        ObservableList<TaiKhoan> accounts = FXCollections.observableArrayList(taiKhoanDAL.getAllTaiKhoan());
        accountTableView.setItems(accounts);
    }

    private void populateForm(TaiKhoan account) {
        isNewAccount = false;
        selectedAccount = account;
        if (account == null) {
            clearForm();
        } else {
            usernameField.setText(account.getTenDangNhap());
            usernameField.setDisable(true); // Không cho sửa tên đăng nhập
            passwordField.clear(); // Luôn xóa password để bảo mật
            staffIdField.setText(account.getMaNV() != null ? String.valueOf(account.getMaNV()) : "");
            roleChoiceBox.setValue(account.getRole());
        }
    }
    
    @FXML
    private void handleAddNewAccount() {
        isNewAccount = true;
        selectedAccount = new TaiKhoan();
        clearForm();
        usernameField.setDisable(false); // Cho phép nhập tên đăng nhập mới
    }

    @FXML
    private void handleDeleteAccount() {
        if (selectedAccount == null || isNewAccount) {
            return;
        }

        if (Objects.equals(selectedAccount.getMaNV(), currentTaiKhoan.getMaNV())) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo");
            alert.setHeaderText("Không thể xóa tài khoản");
            alert.setContentText("Bạn không thể xóa chính bạn.");
            alert.showAndWait();
            return;
        }

        // Nếu không phải chính mình, tiến hành xóa
        taiKhoanDAL.deleteTaiKhoan(selectedAccount.getTenDangNhap());
        loadAccounts();
        clearForm();
    }


    @FXML
    private void handleSaveAccount() {
        // Lấy dữ liệu từ form
        selectedAccount.setTenDangNhap(usernameField.getText());
        selectedAccount.setRole(roleChoiceBox.getValue());
        try {
            selectedAccount.setMaNV(Integer.parseInt(staffIdField.getText()));
        } catch (NumberFormatException e) {
            selectedAccount.setMaNV(null); // Nếu không nhập hoặc nhập sai thì là null
        }

        // Xử lý mật khẩu: chỉ mã hóa và cập nhật nếu người dùng nhập mật khẩu mới
        String newPassword = passwordField.getText();
        if (newPassword != null && !newPassword.isEmpty()) {
            String hashedPassword = Hashing.hashPassword(newPassword);
            selectedAccount.setMatKhauHash(hashedPassword);
        }

        if (isNewAccount) {
            // Thêm mới
            if (selectedAccount.getMatKhauHash() == null) {
                // Hiển thị cảnh báo yêu cầu nhập mật khẩu cho tài khoản mới
                return;
            }
            taiKhoanDAL.addTaiKhoan(selectedAccount);
        } else {
            // Cập nhật
            taiKhoanDAL.updateTaiKhoan(selectedAccount);
        }

        loadAccounts();
        clearForm();
        usernameField.setDisable(true);
    }

    private void clearForm() {
        usernameField.clear();
        passwordField.clear();
        staffIdField.clear();
        roleChoiceBox.setValue(null);
        accountTableView.getSelectionModel().clearSelection();
    }
}