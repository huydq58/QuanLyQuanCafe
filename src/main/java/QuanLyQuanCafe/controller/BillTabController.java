package QuanLyQuanCafe.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import  QuanLyQuanCafe.*;
import  QuanLyQuanCafe.model.*;
import  QuanLyQuanCafe.database.*;


public class BillTabController {

    // Doanh Thu Tab

    @FXML
    private DatePicker orderDatePicker;

    @FXML
    private DatePicker paidDatePicker;

    @FXML
    private TableView<Bill> billTableView;

    @FXML
    private TableColumn<Bill, String> tableNameCol;

    @FXML
    private TableColumn<Bill, Integer> discountCol;

    @FXML
    private TableColumn<Bill, Double> totalPriceCol;

    @FXML
    private TableColumn<Bill, String> orderDateCol;

    @FXML
    private TableColumn<Bill, String> paidDateCol;

    @FXML
    private Button viewButton;

    private static final DataProvider provider = new DataProvider();

    @FXML
    public void initialize() {
        System.out.println("BillTabController initialized");
        tableNameCol.setCellValueFactory(cellData -> {
            // Use a lambda to retrieve the table name based on tableID
            String tableName = getTableNameById(cellData.getValue().getTableID());
            return new SimpleStringProperty(tableName);
        });

        discountCol.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(cellData.getValue().getDisCount()).asObject());
        totalPriceCol.setCellValueFactory(
                cellData -> new SimpleDoubleProperty(cellData.getValue().getTotalPrice()).asObject());

        orderDateCol.setCellValueFactory(cellData -> {
            LocalDate orderDate = cellData.getValue().getOrderDate().toLocalDate();
            return new SimpleStringProperty(orderDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        });

        paidDateCol.setCellValueFactory(cellData -> {
            LocalDate paidDate = cellData.getValue().getPaidDate() != null
                    ? cellData.getValue().getPaidDate().toLocalDate()
                    : null;
            return new SimpleStringProperty(
                    paidDate != null ? paidDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "");
        });

    }

    @FXML
    public void handleViewButton() {
        LocalDate orderDate = orderDatePicker.getValue();
        LocalDate paidDate = paidDatePicker.getValue();

        // Validate dates
        if (orderDate == null || paidDate == null) {
            System.out.println("Please select both dates.");
            return;
        }

        // Clear previous data
        billTableView.getItems().clear();

        // Fetch bills based on the selected date range
        ObservableList<Bill> billsInRange = FXCollections.observableArrayList();

        for (Bill bill : new BillDAL(provider).getAllBills()) {
            LocalDate billOrderDate = bill.getOrderDate().toLocalDate();
            // LocalDate billPaidDate = bill.isPaid() ? bill.getPaidDate().toLocalDate() :
            // null;

            if (!billOrderDate.isBefore(orderDate) && !billOrderDate.isAfter(paidDate)) {
                billsInRange.add(bill);
            }
        }

        // Update TableView
        billTableView.setItems(billsInRange);
    }

    private String getTableNameById(int tableId) {

        for (TableFood table : new TableFoodDAL(provider).getAllTables()) {
            if (table.getId() == tableId) {
                return table.getName();
            }
        }
        return "Unknown Table"; // Fallback if table not found
    }
}
