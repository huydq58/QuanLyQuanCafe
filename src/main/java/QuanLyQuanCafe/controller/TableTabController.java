package QuanLyQuanCafe.controller;

import QuanLyQuanCafe.database.DataProvider;
import QuanLyQuanCafe.model.TableFood;
import QuanLyQuanCafe.model.TableFoodDAL;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class TableTabController {
    @FXML private TableView<TableFood> tableView;
    @FXML private TableColumn<TableFood, Integer> tableIdCol;
    @FXML private TableColumn<TableFood, String> tableNameCol2;
    @FXML private TableColumn<TableFood, String> tableStatusCol;
    @FXML private TextField tableIdField;
    @FXML private TextField tableNameField;

    private TableFoodDAL tableFoodDAL;
    private TableFood selectedTable;

    @FXML
    private void initialize() {
        DataProvider provider = new DataProvider();
        tableFoodDAL = new TableFoodDAL(provider);

        tableIdCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        tableNameCol2.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        tableStatusCol.setCellValueFactory(cellData -> {
            boolean isAvailable = cellData.getValue().isAvailable();
            return new SimpleStringProperty(isAvailable ? "Đang trống" : "Đang sử dụng");
        });

        tableView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> populateTableFields(newSelection)
        );
        
        loadTables();
    }

    private void loadTables() {
        ObservableList<TableFood> tables = FXCollections.observableArrayList(tableFoodDAL.getAllTables());
        tableView.setItems(tables);
    }

    private void populateTableFields(TableFood table) {
        selectedTable = table;
        if (table == null) {
            clearForm();
        } else {
            tableIdField.setText(String.valueOf(table.getId()));
            tableNameField.setText(table.getName());
        }
    }

    @FXML
    private void handleAddTable() {
        int nextTableNumber = tableFoodDAL.getTableCount() + 1;
        TableFood newTable = new TableFood();
        newTable.setName("Bàn " + nextTableNumber);
        newTable.setAvailable(true);
        tableFoodDAL.addTable(newTable);
        loadTables();
    }

    @FXML
    private void handleDeleteTable() {
        if (selectedTable != null) {
            tableFoodDAL.deleteTable(selectedTable.getId());
            loadTables();
            clearForm();
        }
    }

    @FXML
    private void handleSaveTable() {
        if (selectedTable != null) {
            selectedTable.setName(tableNameField.getText());
            // Cập nhật trạng thái không đổi ở màn hình này
            tableFoodDAL.updateTable(selectedTable);
            loadTables();
        }
    }
    
    private void clearForm() {
        tableIdField.clear();
        tableNameField.clear();
        selectedTable = null;
    }
}