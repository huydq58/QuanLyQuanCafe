package QuanLyQuanCafe.controller;

import QuanLyQuanCafe.database.DataProvider;
import QuanLyQuanCafe.model.NguyenLieu;
import QuanLyQuanCafe.model.NguyenLieuDAL;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class InventoryTabController {

    @FXML private TableView<NguyenLieu> inventoryTableView;
    @FXML private TableColumn<NguyenLieu, Integer> ingredientIdCol;
    @FXML private TableColumn<NguyenLieu, String> ingredientNameCol;
    @FXML private TableColumn<NguyenLieu, String> ingredientUnitCol;
    @FXML private TableColumn<NguyenLieu, Number> ingredientStockCol;
    @FXML private TableColumn<NguyenLieu, Number> ingredientThresholdCol;

    @FXML private TextField ingredientIdField;
    @FXML private TextField ingredientNameField;
    @FXML private TextField ingredientUnitField;
    @FXML private Spinner<Double> ingredientStockSpinner;
    @FXML private Spinner<Double> ingredientThresholdSpinner;
    @FXML private Button saveIngredientButton;

    private NguyenLieuDAL nguyenLieuDAL;
    private NguyenLieu selectedIngredient;

    @FXML
    public void initialize() {
        DataProvider provider = new DataProvider();
        nguyenLieuDAL = new NguyenLieuDAL(provider);

        // Cấu hình các cột cho bảng
        ingredientIdCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        ingredientNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTen()));
        ingredientUnitCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDonViTinh()));
        ingredientStockCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getSoLuongTon()));
        ingredientThresholdCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getNguongCanhBao()));

        // Cấu hình Spinner cho số thực
        setupDoubleSpinner(ingredientStockSpinner, 0, 100000, 0, 1);
        setupDoubleSpinner(ingredientThresholdSpinner, 0, 100000, 0, 1);
        
        // Thêm listener để khi chọn một dòng trong bảng, thông tin sẽ hiển thị lên form
        inventoryTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> populateForm(newValue)
        );

        loadInventory();
        setFormDisable(true);
    }

    private void loadInventory() {
        ObservableList<NguyenLieu> inventoryList = FXCollections.observableArrayList(nguyenLieuDAL.getAllNguyenLieu());
        inventoryTableView.setItems(inventoryList);
    }

    private void populateForm(NguyenLieu nguyenLieu) {
        selectedIngredient = nguyenLieu;
        if (nguyenLieu == null) {
            clearForm();
            setFormDisable(true);
        } else {
            ingredientIdField.setText(String.valueOf(nguyenLieu.getId()));
            ingredientNameField.setText(nguyenLieu.getTen());
            ingredientUnitField.setText(nguyenLieu.getDonViTinh());
            ingredientStockSpinner.getValueFactory().setValue(nguyenLieu.getSoLuongTon());
            ingredientThresholdSpinner.getValueFactory().setValue(nguyenLieu.getNguongCanhBao());
            setFormDisable(false);
        }
    }
    
    @FXML
    private void handleSaveIngredient() {
        if (selectedIngredient == null) return; // Không có gì để lưu

        // Lấy dữ liệu từ form
        selectedIngredient.setTen(ingredientNameField.getText());
        selectedIngredient.setDonViTinh(ingredientUnitField.getText());
        selectedIngredient.setSoLuongTon(ingredientStockSpinner.getValue());
        selectedIngredient.setNguongCanhBao(ingredientThresholdSpinner.getValue());

        if (selectedIngredient.getId() == 0) { // Trường hợp thêm mới
            nguyenLieuDAL.addNguyenLieu(selectedIngredient);
        } else { // Trường hợp cập nhật
            nguyenLieuDAL.updateNguyenLieu(selectedIngredient);
        }
        
        loadInventory(); // Tải lại bảng
        clearForm();
        setFormDisable(true);
    }

    @FXML
    private void handleAddNewIngredient() {
        // Tạo một đối tượng nguyên liệu mới, rỗng
        NguyenLieu newIngredient = new NguyenLieu();
        newIngredient.setId(0); // Đánh dấu là đối tượng mới
        inventoryTableView.getSelectionModel().clearSelection();
        populateForm(newIngredient);
    }

    @FXML
    private void handleDeleteIngredient() {
        if (selectedIngredient == null || selectedIngredient.getId() == 0) {
            // Hiển thị cảnh báo
            return;
        }
        nguyenLieuDAL.deleteNguyenLieu(selectedIngredient.getId());
        loadInventory();
        clearForm();
    }
    
    private void clearForm() {
        selectedIngredient = null;
        ingredientIdField.clear();
        ingredientNameField.clear();
        ingredientUnitField.clear();
        ingredientStockSpinner.getValueFactory().setValue(0.0);
        ingredientThresholdSpinner.getValueFactory().setValue(0.0);
    }

    private void setFormDisable(boolean disabled) {
        ingredientNameField.setDisable(disabled);
        ingredientUnitField.setDisable(disabled);
        ingredientStockSpinner.setDisable(disabled);
        ingredientThresholdSpinner.setDisable(disabled);
        saveIngredientButton.setDisable(disabled);
    }
    
    private void setupDoubleSpinner(Spinner<Double> spinner, double min, double max, double initialValue, double amountToStepBy) {
        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(min, max, initialValue, amountToStepBy);
        spinner.setValueFactory(valueFactory);
    }
}