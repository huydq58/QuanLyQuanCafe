package QuanLyQuanCafe.controller;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.util.Duration;

import java.io.IOException;
import  QuanLyQuanCafe.*;
import  QuanLyQuanCafe.model.*;
import  QuanLyQuanCafe.database.*;

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

    private BillTabController billController;
    private FoodTabController foodController;
    private CategoryTabController categoryController;
    private TableTabController tableController;
    private AccountTabController accountController;

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

        foodTab.setOnSelectionChanged(event -> {
            if (foodTab.isSelected() && foodController == null) {
                try {
                    FXMLLoader foodLoader = new FXMLLoader(App.class.getResource("FoodTab.fxml"));
                    foodTab.setContent(foodLoader.load());
                    foodController = foodLoader.getController();
                    foodController.refreshData();
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

        accountTab.setOnSelectionChanged(event -> {
            if (accountTab.isSelected() && accountController == null) {
                try {
                    FXMLLoader accountLoader = new FXMLLoader(App.class.getResource("AccountTab.fxml"));
                    accountTab.setContent(accountLoader.load());
                    accountController = accountLoader.getController();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
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
        try {


            // Update UI to show success
            showSuccess("Database saved successfully");

            // Refresh all views to show updated data
            // refreshAllData();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error saving database", e.getMessage());
        }
    }

    private void showSuccess(String message) {
        messageUpdateDB.setText(message);
        messageUpdateDB.setStyle("-fx-text-fill: green;");

        // Clear message after delay
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

    // Getters for sub-controllers
    public BillTabController getbillController() {
        return billController;
    }

    public FoodTabController getFoodController() {
        return foodController;
    }

    public CategoryTabController getCategoryController() {
        return categoryController;
    }

    public TableTabController getTableController() {
        return tableController;
    }

    public AccountTabController getAccountController() {
        return accountController;
    }



    public void exportData() {
        // Implement data export functionality
    }

    public void importData() {
        // Implement data import functionality
    }
}

