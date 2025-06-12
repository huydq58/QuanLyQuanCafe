package QuanLyQuanCafe.controller;

import QuanLyQuanCafe.database.DataProvider;
import QuanLyQuanCafe.model.Category;
import QuanLyQuanCafe.model.CategoryDAL;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class CategoryTabController {
    @FXML private TableView<Category> categoryTableView;
    @FXML private TableColumn<Category, Integer> categoryIdCol;
    @FXML private TableColumn<Category, String> categoryNameCol2;
    @FXML private TextField categoryIdField;
    @FXML private TextField categoryNameField;
    @FXML private TextField searchCategoryField;

    private CategoryDAL categoryDAL;
    private Category selectedCategory;

    @FXML
    private void initialize() {
        DataProvider provider = new DataProvider();
        categoryDAL = new CategoryDAL(provider);

        categoryIdCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        categoryNameCol2.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        categoryTableView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> populateCategoryFields(newSelection)
        );

        loadAllCategories();
    }

    public void loadAllCategories() {
        ObservableList<Category> categoryList = FXCollections.observableArrayList(categoryDAL.getAllCategories());
        categoryTableView.setItems(categoryList);
    }

    private void populateCategoryFields(Category category) {
        selectedCategory = category;
        if (category == null) {
            clearForm();
        } else {
            categoryIdField.setText(String.valueOf(category.getId()));
            categoryNameField.setText(category.getName());
        }
    }

    @FXML
    private void handleSearchCategory() {
        String query = searchCategoryField.getText().toLowerCase();
        ObservableList<Category> filteredList = FXCollections.observableArrayList();
        for (Category category : categoryDAL.getAllCategories()) {
            if (category.getName().toLowerCase().contains(query)) {
                filteredList.add(category);
            }
        }
        categoryTableView.setItems(filteredList);
    }

    @FXML
    private void handleAddCategory() {
        clearForm();
        categoryTableView.getSelectionModel().clearSelection();
        selectedCategory = new Category(0, ""); // Tạo category mới rỗng
    }

    @FXML
    private void handleDeleteCategory() {
        if (selectedCategory != null && selectedCategory.getId() != 0) {
            categoryDAL.deleteCategory(selectedCategory.getId());
            loadAllCategories();
            clearForm();
        }
    }

    @FXML
    private void handleSaveCategory() {
        if (selectedCategory != null) {
            selectedCategory.setName(categoryNameField.getText());
            if (selectedCategory.getId() == 0) { // Thêm mới
                categoryDAL.addCategory(selectedCategory);
            } else { // Cập nhật
                categoryDAL.updateCategory(selectedCategory);
            }
            loadAllCategories();
        }
    }
    
    private void clearForm() {
        categoryIdField.clear();
        categoryNameField.clear();
        selectedCategory = null;
    }
}