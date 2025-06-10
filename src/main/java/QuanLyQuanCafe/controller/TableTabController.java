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

public class TableTabController {
    @FXML
    private TableView<TableFood> tableView;

    @FXML
    private TableColumn<TableFood, Integer> tableIdCol;

    @FXML
    private TableColumn<TableFood, String> tableNameCol2;

    @FXML
    private TableColumn<TableFood, String> tableStatusCol;

    @FXML
    private TextField tableIdField;

    @FXML
    private TextField tableNameField;

    private DataProvider dataProvider;

    @FXML
    private void initialize() {
        System.out.println("Table tab initialized");
        dataProvider = new DataProvider();
        loadTables();

        // Table code
        tableIdCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        tableNameCol2
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        tableStatusCol
                .setCellValueFactory(cellData -> {
                    String statusText;
                    boolean myBoolean = cellData.getValue().isAvailable();
                    int myInt = myBoolean ? 1 : 0;
                    switch (myInt) {
                        case 0:
                            statusText = "Đang trống";
                            break;
                        case 1:
                            statusText = "Đang sử dụng";
                            break;
                        default:
                            statusText = "";
                            break;
                    }

                    return new SimpleStringProperty(statusText);
                });

        // Set table selection listener
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateTableFields(newSelection);
            }
        });
    }

    private void loadTables() {

        ObservableList<TableFood> tables = FXCollections.observableArrayList(new TableFoodDAL(dataProvider).getAllTables());

        tableView.setItems(tables);

        // Refresh the table view
        tableView.refresh();

        tableView.getItems().clear();
        tableView.getItems().addAll(new TableFoodDAL(dataProvider).getAllTables());
    }

    private void populateTableFields(TableFood table) {
        tableIdField.setText(String.valueOf(table.getId()));
        tableNameField.setText(table.getName());
    }

    @FXML
    private void handleAddTable() {
        TableFood t = new TableFood();
        TableFoodDAL tableFoodDAL = new TableFoodDAL(dataProvider);
        t.setId(tableFoodDAL.getTableCount() + 1);
        t.setName("Bàn " + (tableFoodDAL.getTableCount() + 1));
        tableFoodDAL.addTable(t);
        loadTables();
    }

    @FXML
    private void handleDeleteTable() {
        TableFood selectedTable = tableView.getSelectionModel().getSelectedItem();
        TableFoodDAL tableFoodDAL = new TableFoodDAL(dataProvider);
        if (selectedTable != null) {
            tableFoodDAL.deleteTable(selectedTable.getId());
            loadTables();
        }
    }

    @FXML
    private void handleSaveTable() {
        TableFood selectedTable = tableView.getSelectionModel().getSelectedItem();
        if (selectedTable != null) {
            selectedTable.setName(tableNameField.getText());
            loadTables();
        }
    }
}

