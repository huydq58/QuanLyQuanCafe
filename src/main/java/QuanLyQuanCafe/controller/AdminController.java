package QuanLyQuanCafe.controller;

import java.io.IOException;
import java.util.List;

import QuanLyQuanCafe.App;
import QuanLyQuanCafe.database.DataProvider;
import QuanLyQuanCafe.model.NguyenLieu;
import QuanLyQuanCafe.model.NguyenLieuDAL;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.util.Duration;

public class AdminController {
    @FXML
    private Tab billTab;
    @FXML
    private Tab foodTab;
    @FXML
    private Tab categoryTab;
    @FXML
    private Tab tableTab;
    @FXML
    private Tab accountTab;
    @FXML 
    private Tab inventoryTab;

    private BillTabController billController;
    private FoodTabController foodController;
    private CategoryTabController categoryController;
    private TableTabController tableController;
    private InventoryTabController inventoryController;

    @FXML
    public Label messageUpdateDB;

    @FXML
    public void initialize() {
        try {
            FXMLLoader billLoader = new FXMLLoader(App.class.getResource("BillTab.fxml"));
            billTab.setContent(billLoader.load());
            billController = billLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setupTabHandlers();
        
        // KIỂM TRA TỒN KHO KHI KHỞI TẠO
        checkInventoryWarnings();
    }

    private void setupTabHandlers() {
        billTab.setOnSelectionChanged(event -> {
            if (billTab.isSelected() && billController == null) {
                try {
                    FXMLLoader billLoader = new FXMLLoader(App.class.getResource("BillTab.fxml"));
                    billTab.setContent(billLoader.load());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        inventoryTab.setOnSelectionChanged(event -> {
            if (inventoryTab.isSelected()) {
                if (inventoryController == null) {
                    try {
                        FXMLLoader loader = new FXMLLoader(App.class.getResource("InventoryTab.fxml"));
                        inventoryTab.setContent(loader.load());
                        inventoryController = loader.getController();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // Xóa cảnh báo khi người dùng đã xem
                inventoryTab.getStyleClass().remove("tab-warning");
                inventoryTab.setText("Quản lý Kho");
            }
        });

        foodTab.setOnSelectionChanged(event -> {
            if (foodTab.isSelected() && foodController == null) {
                try {
                    FXMLLoader foodLoader = new FXMLLoader(App.class.getResource("FoodTab.fxml"));
                    foodTab.setContent(foodLoader.load());
                    foodController = foodLoader.getController();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        categoryTab.setOnSelectionChanged(event -> {
            if (categoryTab.isSelected() && categoryController == null) {
                try {
                    FXMLLoader categoryLoader = new FXMLLoader(App.class.getResource("CategoryTab.fxml"));
                    categoryTab.setContent(categoryLoader.load());
                    categoryController = categoryLoader.getController();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        tableTab.setOnSelectionChanged(event -> {
            if (tableTab.isSelected() && tableController == null) {
                try {
                    FXMLLoader tableLoader = new FXMLLoader(App.class.getResource("TableTab.fxml"));
                    tableTab.setContent(tableLoader.load());
                    tableController = tableLoader.getController();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        
        // Vô hiệu hóa tab chưa có chức năng
        accountTab.setDisable(true);
        accountTab.setContent(new Label("Chức năng 'Quản lý Tài khoản' đang được phát triển."));
    }
    
    /**
     * Phương thức kiểm tra các nguyên liệu dưới ngưỡng và cập nhật giao diện tab
     */
    private void checkInventoryWarnings() {
        DataProvider provider = new DataProvider();
        NguyenLieuDAL nguyenLieuDAL = new NguyenLieuDAL(provider);
        List<NguyenLieu> lowStockItems = nguyenLieuDAL.getNguyenLieuDuoiNguong();

        if (lowStockItems != null && !lowStockItems.isEmpty()) {
            inventoryTab.getStyleClass().add("tab-warning");
            inventoryTab.setText("Quản lý Kho (" + lowStockItems.size() + ")");
        } else {
            inventoryTab.getStyleClass().remove("tab-warning");
            inventoryTab.setText("Quản lý Kho");
        }
    }

    @FXML
    private void backtoMain() throws IOException {
        try {
            App.setRoot("MainWindow");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error returning to main screen", e.getMessage());
        }
    }

    @FXML
    private void saveDB() {
        // Chưa có logic
    }

    private void showSuccess(String message) {
        messageUpdateDB.setText(message);
        messageUpdateDB.setStyle("-fx-text-fill: green;");

        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> messageUpdateDB.setText(""));
        pause.play();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}