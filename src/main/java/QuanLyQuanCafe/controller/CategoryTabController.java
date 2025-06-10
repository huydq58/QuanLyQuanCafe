package QuanLyQuanCafe.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import  QuanLyQuanCafe.*;
import  QuanLyQuanCafe.model.*;
import  QuanLyQuanCafe.database.*;

public class CategoryTabController {
    @FXML
    public TableView<Category> categoryTableView;

    @FXML
    private TableColumn<Category, Integer> categoryIdCol;

    @FXML
    private TableColumn<Category, String> categoryNameCol2;

    private DataProvider provider;

    @FXML
    private TextField categoryIdField;

    @FXML
    private TextField categoryNameField;

    @FXML
    private TextField searchCategoryField;

    @FXML
    private void initialize() {
        System.out.println("Category tab initialized");
        provider = new DataProvider();

        loadAllCategories();

        categoryIdCol
                .setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        categoryNameCol2
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        // Set table selection listener
        categoryTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateCategoryFields(newSelection);
            }
        });
    }

    public void loadAllCategories() {
        CategoryDAL categoryDAL = new CategoryDAL(provider);
        ObservableList<Category> categoryList = FXCollections.observableArrayList(categoryDAL.getAllCategories());

        categoryTableView.setItems(categoryList);

        categoryTableView.refresh();

    }

    private void populateCategoryFields(Category category) {
        categoryIdField.setText(String.valueOf(category.getId()));
        categoryNameField.setText(category.getName());
    }

    @FXML
    private void handleSearchCategory() {
        String query = searchCategoryField.getText().toLowerCase();
        ObservableList<Category> filteredList = FXCollections.observableArrayList();
        CategoryDAL categoryDAL = new CategoryDAL(provider);

        for (Category category : categoryDAL.getAllCategories()) {
            if (category.getName().toLowerCase().contains(query)) {
                filteredList.add(category);
            }
        }

        categoryTableView.setItems(filteredList);
    }

    @FXML
    private void handleAddCategory() {
        CategoryDAL categoryDAL = new CategoryDAL(provider);

        Category c = new Category();
        c.setId(categoryDAL.getCategoryCount() + 1);
        c.setName("Loại " + (categoryDAL.getCategoryCount() + 1));
        categoryDAL.addCategory(c);
        loadAllCategories();
    }

    @FXML
    private void handleDeleteCategory() {
        Category selectedCategory = categoryTableView.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            CategoryDAL categoryDAL = new CategoryDAL(provider);
            categoryDAL.deleteCategory(selectedCategory.getId());
            loadAllCategories();
        }
    }

    @FXML
    private void handleSaveCategory() {
        Category selectedCategory = categoryTableView.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            selectedCategory.setName(categoryNameField.getText());
            loadAllCategories();
        }
    }
}

