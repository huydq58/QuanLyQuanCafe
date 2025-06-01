package QuanLyQuanCafe.controller;

import QuanLyQuanCafe.App;
import java.net.URL;

import QuanLyQuanCafe.database.DataProvider;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


import QuanLyQuanCafe.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

import java.time.LocalDateTime;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;

public class MainWindow implements Initializable {

    // <editor-fold desc="Khoi tao giao dien">
    @FXML
    private FlowPane danhSachBan;

    @FXML
    private FlowPane danhSachMon;

    @FXML
    private Label messageTableLabel;

    private int choseTable;
    private int choseFood;
    private int chosenBill;

    @FXML
    private TableView<OrderItemView> bangHoaDon;

    @FXML
    private TableColumn<OrderItemView, String> foodNameCol;
    @FXML
    private TableColumn<OrderItemView, Long> quantityCol;
    @FXML
    private TableColumn<OrderItemView, Long> pricePerItemCol;
    @FXML
    private TableColumn<OrderItemView, Long> totalPriceCol;

    @FXML
    private ChoiceBox<Category> categoryChoiceBox;

    @FXML
    private Spinner<Integer> quantitySpinner;

    @FXML
    private Button addFoodButton; // Add this line for the button

    private ToggleGroup tableBtnGroup = new ToggleGroup();
    private ToggleGroup foodBtbGroup = new ToggleGroup();

    @FXML
    private TextField totalPrice;

    private List<OrderItemView> orderItems = new ArrayList<>();

    @FXML
    private Button gotoAdminButton;

    @FXML
    private TextField discountField;

    @FXML
    private Button thanhToanButton;
    // </editor-fold>

    private List<ImageView> imageViews = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        thanhToanButton.setDisable(true);

        loadTables();
        loadFoods();
        loadCategories();

        setupOrderTable();
        // populateOrderTable();
        addFoodButton.setOnAction(event -> addFood());

        // xử lý rs tại đây...


        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));

        categoryChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldCategory, newCategory) -> {
            updateFoodDisplay(newCategory);
        });
    }

    @FXML
    private void gotoAdminScreen() throws IOException {

        App.setRoot("AdminScreen");
    }
    //Load danh sach ban
    private void loadTables() {
        DataProvider provider = new DataProvider();
        TableFoodDAL tableDAL = new TableFoodDAL(provider);

        List<TableFood> tables = tableDAL.getAllTables();

        // Duyệt danh sách bàn
        for (TableFood table : tables) {
            ToggleButton b = new ToggleButton();
            b.setToggleGroup(tableBtnGroup);
            b.setText(table.getName());
            b.setPrefWidth(80);
            b.setPrefHeight(80);
            b.setUserData(table.getId());
            b.setOnAction(event -> chonTable(event));

            danhSachBan.getChildren().add(b);
        }

    }
    //Load tất cả món ăn
    private void loadFoods() {
        DataProvider provider = new DataProvider();
        FoodDAL foodDAL = new FoodDAL(provider);
        List<Food> foods = foodDAL.getAllFood();

        for (Food food : foods) {
            loadFoodItem(food);
        }
        System.out.println("So luong mon: " + danhSachMon.getChildren().size());

    }
    //Load tất cả danh mục
    private void loadCategories() {
        DataProvider provider = new DataProvider();
        CategoryDAL categoryDAL = new CategoryDAL(provider);
        List<Category> categories = categoryDAL.getAllCategories();
        categoryChoiceBox.getItems().clear();
        categoryChoiceBox.getItems().addAll(categories);
    }
    //Update list thức ăn khi chọn danh mục
    private void updateFoodDisplay(Category selectedCategory) {
        // Clear the FlowPane first
        danhSachMon.getChildren().clear();
        DataProvider provider = new DataProvider();
        FoodDAL foodDAL = new FoodDAL(provider);
        List<Food> foods = foodDAL.getAllFood();
        // Get the foods based on selected category
        List<Food> foodsToDisplay = selectedCategory == null
                ? foods
                : foods.stream()
                .filter(food -> food.getCategoryId() == selectedCategory.getId())
                .collect(Collectors.toList());

        imageViews.clear();
        for (Food food : foodsToDisplay) {
            loadFoodItem(food);
        }

    }

    @FXML
    public void chonFood(javafx.event.ActionEvent event, ImageView imageView) {
        // Lấy button được clicked
        ToggleButton clickedButton = (ToggleButton) event.getSource();

        for (ImageView imgV : imageViews) {
            imgV.setOpacity(1);
        }

        imageView.setOpacity(0.5);

        // Lấy data (food id) từ button được click
        int foodID = (int) clickedButton.getUserData();

        choseFood = foodID;
        System.out.println("Selected Food ID: " + choseFood);
    }

    @FXML
    public void chonTable(javafx.event.ActionEvent event) {
        for (Toggle btn : foodBtbGroup.getToggles()) {
            btn.setSelected(false);
        }

        choseFood = 0;

        // Lấy button được clicked
        ToggleButton clickedButton = (ToggleButton) event.getSource();

        // Lấy data (table id) từ button được click
        int tableId = (int) clickedButton.getUserData();

        choseTable = tableId;
        chosenBill = getBillIdByTableId(choseTable);
        System.out.println("Selected Table ID: " + choseTable);

        updateOrderTable(choseTable);

    }

    private int getBillIdByTableId(int tableId) {
        TableFoodDAL tableDAL = new TableFoodDAL(new DataProvider());
        List<TableFood> tables = tableDAL.getAllTables();

        for (TableFood table : tables) {
            if (table.getId() == tableId) {
                if (!table.isAvailable()) {
                    messageTableLabel.setText("Bàn " + tableId + " có khách!");
                } else {
                    messageTableLabel.setText("Bàn đang trống");
                }
            }
        }
        BillDAL billDAL = new BillDAL(new DataProvider());
        List<Bill> bills = billDAL.getAllBills();
        // Check co hoa don hay khong
        for (Bill bill : bills) {
            if (bill.getTableID() == tableId && !bill.isPaid()) {
                return bill.getId();
            }
        }

        return 0;
    }

    private double updateOrderTable(int tableId) {
        BillDAL billDAL = new BillDAL(new DataProvider());
        List<Bill> bills = billDAL.getAllBills();

        for (Bill bill : bills) {
            if (bill.getTableID() == tableId && !bill.isPaid()) {
                // Get the order items related to this bill ID
                orderItems = getOrderItemsForBill(bill.getId());
                ObservableList<OrderItemView> observableList = FXCollections.observableArrayList(orderItems);

                // Update the TableView with the filtered order items
                bangHoaDon.setItems(observableList);

                bangHoaDon.refresh();

                // Break the loop since we've found the matching bill
                break;
            } else {
                bangHoaDon.getItems().clear();
            }
        }

        double totalPriceLocal = 0;

        for (OrderItemView orderItem : orderItems) {
            totalPriceLocal += orderItem.getTotalPrice();
        }

        totalPrice.setText(convertToVND(totalPriceLocal));

        return 0;
    }

    private List<OrderItemView> getOrderItemsForBill(int billId) {
        List<OrderItemView> orderItemViews = new ArrayList<>();

        OrderDAL orderDAL = new OrderDAL(new DataProvider());
        List<Order> orders = orderDAL.getOrdersByBillID(billId); // ✅ Truy vấn theo billId

        // Tạo Map để tìm Food theo ID nhanh hơn O(1)
        List<Food> foods = new FoodDAL(new DataProvider()).getAllFood();
        Map<Integer, Food> foodMap = new HashMap<>();
        for (Food food : foods) {
            foodMap.put(food.getId(), food);
        }

        for (Order order : orders) {
            Food foundFood = foodMap.get(order.getFoodID());

            if (foundFood != null) {
                OrderItemView itemView = new OrderItemView(
                        foundFood.getName(),
                        order.getCount(),
                        foundFood.getPrice(),
                        foundFood.getId()
                );
                orderItemViews.add(itemView);
            }
        }

        return orderItemViews;
    }

    // Setup TableView columns
    private void setupOrderTable() {
        foodNameCol.setCellValueFactory(new PropertyValueFactory<>("foodName"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        pricePerItemCol.setCellValueFactory(new PropertyValueFactory<>("pricePerItem"));
        totalPriceCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        quantityCol.setCellFactory(TextFieldTableCell.forTableColumn(new javafx.util.converter.LongStringConverter()));

        quantityCol.setOnEditCommit(event -> {
            OrderItemView item = event.getRowValue();
            long newQuantity = event.getNewValue();

            if (newQuantity <= 0) {
                // Không cho phép số lượng <= 0
                showAlert("Số lượng không hợp lệ", "Vui lòng nhập số lượng lớn hơn 0.");
                bangHoaDon.refresh();
                return;
            }

            item.setQuantity(newQuantity);
            long pricePerItem = item.getPricePerItem();
            item.setTotalPrice(pricePerItem * newQuantity);

            // Cập nhật trực tiếp vào CSDL thông qua OrderDAL
            OrderDAL orderDAL = new OrderDAL(new DataProvider());
            Order updatedOrder = new Order();
            updatedOrder.setBillID(chosenBill);
            updatedOrder.setFoodID(item.getFoodID());
            updatedOrder.setCount(newQuantity);

            boolean success = orderDAL.updateOrder(updatedOrder);
            if (!success) {
                showAlert("Lỗi cập nhật", "Không thể cập nhật số lượng món ăn.");
            }

            bangHoaDon.refresh();
        });
    }


    // Tao list hoa don view tu Orders va Foods
    private List<OrderItemView> getOrderItems() {
        List<OrderItemView> orderItemViews = new ArrayList<>();
        OrderDAL orderDAL = new OrderDAL(new DataProvider());
        List<Order> orders = orderDAL.getAllOrders();

        FoodDAL foodDAL = new FoodDAL(new DataProvider());
        List<Food> foods = foodDAL.getAllFood();

        for (Order order : orders) {
            Food foundFood = null;
            // Find the food by matching the ID in the foods list
            for (Food food : foods) {
                if (food.getId() == order.getFoodID()) {
                    foundFood = food;
                    break;
                }
            }

            // If food is found, create and add OrderItemView to the list
            if (foundFood != null) {
                OrderItemView itemView = new OrderItemView(foundFood.getName(),
                        order.getCount(), foundFood.getPrice(),
                        foundFood.getId());
                orderItemViews.add(itemView);
            }
        }

        return orderItemViews; // Return the list of order item views
    }

    @FXML
    public void addFood() {
        Integer quantity = quantitySpinner.getValue();
        if (choseTable == 0) {
            showAlert("Chưa chọn bàn", "Vui lòng chọn bàn.");
            return;
        }
        if (choseFood == 0) {
            showAlert("Chưa chọn món", "Vui lòng chọn món ăn.");
            return;
        }

        BillDAL billDAL = new BillDAL(new DataProvider());
        TableFoodDAL tableDAL = new TableFoodDAL(new DataProvider());
        OrderDAL orderDAL = new OrderDAL(new DataProvider());

        try {
            chosenBill = getBillIdByTableId(choseTable);
            thanhToanButton.setDisable(false);
            System.out.println("chosenBill: " + chosenBill);
            System.out.println("chosenTable: " + choseTable);

            if (chosenBill == 0) {
                Bill newBill = new Bill();
                newBill.setTableID(choseTable);
                newBill.setDisCount(0);
                newBill.setTotalPrice(0);
                newBill.setPaid(false);

                int generatedBillId = billDAL.insertBill(newBill);
                if (generatedBillId == -1) {
                    showAlert("Lỗi", "Không thể tạo hóa đơn mới.");
                    return;
                }

                chosenBill = generatedBillId;

                TableFood tableFood = tableDAL.getTableById(choseTable);
                tableFood.setAvailable(false);
                tableDAL.updateTable(tableFood);  // Cập nhật trạng thái trong DB

                messageTableLabel.setText("Bàn " + choseTable + " có khách!");
                System.out.println("Tạo hóa đơn mới ID: " + chosenBill);
            }

            Order newOrder = new Order();
            newOrder.setBillID(chosenBill);
            newOrder.setFoodID(choseFood);
            newOrder.setCount(quantity);

            if (orderDAL.existsOrder(chosenBill, choseFood)) {
                orderDAL.increaseQuantity(chosenBill, choseFood, quantity);
            } else {
                orderDAL.insertOrder(newOrder);
            }

            double totalPriceLocal = updateOrderTable(choseTable);
            totalPrice.setText(convertToVND(totalPriceLocal));
            quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));

            System.out.println("Thêm món ID: " + choseFood + ", SL: " + quantity + " vào bàn ID: " + choseTable);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi CSDL", "Có lỗi xảy ra khi thao tác với cơ sở dữ liệu: " + e.getMessage());
        }
    }




    @FXML
    public void thanhToan() {
//        for (Table table : ShopDB.tables) {
//            if (table.getId() == choseTable) {
//                table.setStatus(TableStatus.NO_USE);
//
//                for (Bill bill : OrderBillDB.bills) {
//                    if (bill.getId() == chosenBill) {
//
//                        double totalPriceLocal = 0;
//
//                        if (bill.getDisCount() > 0) {
//                            totalPriceLocal = bill.getTotalPrice();
//                        } else {
//                            for (OrderItemView orderItem : orderItems) {
//                                totalPriceLocal += orderItem.getTotalPrice();
//                            }
//                        }
//
//                        bill.setPaid(true);
//                        bill.setPaidDate(LocalDateTime.now());
//                        bill.setTotalPrice(totalPriceLocal);
//                    }
//                }
//
//                discountField.setText("0");
//                totalPrice.setText("0");
//
//                bangHoaDon.getItems().clear();
//
//                thanhToanButton.setDisable(true);
//
//                ShopDB.saveShopDB();
//                OrderBillDB.saveBill();
//            }
//        }
    }

    @FXML
    public void apKhuyenMai() {
        BillDAL billDAL = new BillDAL(new DataProvider());
        List<Bill> bills = billDAL.getAllBills();
        for (Bill bill : bills) {
            if (bill.getId() == chosenBill) {

                int discount = Integer.parseInt(discountField.getText());

                bill.setDisCount(discount);

                double totalPriceLocal = 0;

                for (OrderItemView orderItem : orderItems) {
                    totalPriceLocal += orderItem.getTotalPrice();
                }

                double newTotalPrice = totalPriceLocal - (totalPriceLocal * (discount / 100.0));

                bill.setTotalPrice(newTotalPrice);

                totalPrice.setText(convertToVND(newTotalPrice));
            }
        }
    }

    private String convertToVND(double money) {
        // Create a NumberFormat instance for VND
        Locale vietnamLocale = Locale.forLanguageTag("vi-VN");

        // Create a NumberFormat instance for VND
        NumberFormat vndFormat = NumberFormat.getCurrencyInstance(vietnamLocale);

        // Display the formatted currency
        System.out.println("Formatted amount: " + vndFormat.format(money));
        return vndFormat.format(money);
    }

    @FXML
    private void gotoLoginScreen() throws IOException {
        App.setRoot("login");
    }
    private void loadFoodItem(Food food){

        VBox vBox = new VBox();
        vBox.setPrefWidth(100);
        vBox.setPrefHeight(100);
        vBox.setAlignment(Pos.CENTER);

        StackPane stackPane = new StackPane();

        ImageView imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);

        imageViews.add(imageView);

        File imageFile = new File(food.getImagePath());

        Image image = new Image(imageFile.toURI().toString());

        imageView.setImage(image);

        ToggleButton b = new ToggleButton();
        b.setToggleGroup(foodBtbGroup);
        b.setPrefHeight(100);
        b.setPrefWidth(100);
        b.setOpacity(0);
        b.setUserData(food.getId());
        b.setOnAction(event -> chonFood(event, imageView));


        stackPane.getChildren().add(imageView);
        stackPane.getChildren().add(b);

        vBox.getChildren().add(stackPane);

        Label label = new Label(food.getName());
        label.setWrapText(true);
        vBox.getChildren().add(label);

        danhSachMon.getChildren().add(vBox);
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
