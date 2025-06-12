package QuanLyQuanCafe.controller;

import java.io.IOException;
import java.util.List;

import QuanLyQuanCafe.App;
import QuanLyQuanCafe.database.DataProvider;
import QuanLyQuanCafe.model.NguyenLieu;
import QuanLyQuanCafe.model.NguyenLieuDAL;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;

public class AdminController {
    // FXML Fields for each Tab
    @FXML private Tab billTab;
    @FXML private Tab foodTab;
    @FXML private Tab categoryTab;
    @FXML private Tab tableTab;
    @FXML private Tab inventoryTab;
    @FXML private Tab accountTab;

    @FXML public Label messageUpdateDB;

    @FXML
    public void initialize() {
        // Tải sẵn tab đầu tiên (Doanh thu) để không bị trống khi mới mở
        loadTabContent("BillTab.fxml", billTab);
        
        // Cài đặt lazy-loading cho các tab còn lại
        setupTabHandlers();
        
        // Kiểm tra tồn kho ngay khi mở màn hình admin
        checkInventoryWarnings();
    }

    /**
     * Cài đặt các sự kiện để chỉ tải nội dung của Tab khi nó được chọn lần đầu tiên.
     * Điều này giúp ứng dụng khởi động nhanh hơn.
     */
    private void setupTabHandlers() {
        foodTab.setOnSelectionChanged(event -> {
            if (foodTab.isSelected() && foodTab.getContent() == null) {
                loadTabContent("FoodTab.fxml", foodTab);
            }
        });

        categoryTab.setOnSelectionChanged(event -> {
            if (categoryTab.isSelected() && categoryTab.getContent() == null) {
                loadTabContent("CategoryTab.fxml", categoryTab);
            }
        });

        tableTab.setOnSelectionChanged(event -> {
            if (tableTab.isSelected() && tableTab.getContent() == null) {
                loadTabContent("TableTab.fxml", tableTab);
            }
        });
        
        inventoryTab.setOnSelectionChanged(event -> {
            if (inventoryTab.isSelected()) {
                if(inventoryTab.getContent() == null) {
                    loadTabContent("InventoryTab.fxml", inventoryTab);
                }
                // Xóa cảnh báo khi người dùng đã xem
                inventoryTab.getStyleClass().remove("tab-warning");
                inventoryTab.setText("Quản lý Kho");
            }
        });
        
        accountTab.setOnSelectionChanged(event -> {
            if (accountTab.isSelected() && accountTab.getContent() == null) {
                loadTabContent("AccountTab.fxml", accountTab);
            }
        });
    }

    /**
     * Phương thức chung để tải một file FXML vào nội dung của một Tab.
     * Đây là phương thức bị thiếu trong file của bạn.
     * @param fxmlFile Tên của file FXML (ví dụ: "BillTab.fxml").
     * @param tab      Tab cần được đổ nội dung vào.
     */
    private void loadTabContent(String fxmlFile, Tab tab) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlFile));
            tab.setContent(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
            tab.setContent(new Label("Lỗi khi tải tab: " + fxmlFile));
        }
    }

    /**
     * Phương thức kiểm tra các nguyên liệu dưới ngưỡng và cập nhật giao diện tab.
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
            showError("Lỗi quay lại màn hình chính", e.getMessage());
        }
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}