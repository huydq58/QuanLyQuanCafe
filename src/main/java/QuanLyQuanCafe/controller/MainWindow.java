package QuanLyQuanCafe.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import QuanLyQuanCafe.App;
import QuanLyQuanCafe.database.DataProvider;
import QuanLyQuanCafe.model.Bill;
import QuanLyQuanCafe.model.BillDAL;
import QuanLyQuanCafe.model.Category;
import QuanLyQuanCafe.model.CategoryDAL;
import QuanLyQuanCafe.model.CurrentUserSession;
import QuanLyQuanCafe.model.DinhLuongMonAn;
import QuanLyQuanCafe.model.DinhLuongMonAnDAL;
import QuanLyQuanCafe.model.Food;
import QuanLyQuanCafe.model.FoodDAL;
import QuanLyQuanCafe.model.NguyenLieuDAL;
import QuanLyQuanCafe.model.Order;
import QuanLyQuanCafe.model.OrderDAL;
import QuanLyQuanCafe.model.OrderItemView;
import QuanLyQuanCafe.model.TableFood;
import QuanLyQuanCafe.model.TableFoodDAL;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.converter.LongStringConverter;

public class MainWindow implements Initializable {

    @FXML private FlowPane danhSachBan, danhSachMon;
    @FXML private Label messageTableLabel;
    @FXML private TableView<OrderItemView> bangHoaDon;
    @FXML private TableColumn<OrderItemView, String> foodNameCol;
    @FXML private TableColumn<OrderItemView, Long> quantityCol, pricePerItemCol, totalPriceCol;
    @FXML private ChoiceBox<Category> categoryChoiceBox;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private Button addFoodButton, gotoAdminButton, thanhToanButton;
    @FXML private TextField totalPrice, discountField;
    @FXML private TableColumn<OrderItemView, Void> deleteCol;

    private int choseTable, choseFood, chosenBill;
    private final ToggleGroup tableBtnGroup = new ToggleGroup(), foodBtbGroup = new ToggleGroup();
    private List<OrderItemView> orderItems = new ArrayList<>();
    private final List<ImageView> imageViews = new ArrayList<>();
    private static final DataProvider provider = new DataProvider();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        thanhToanButton.setDisable(true);
        totalPrice.setText("");
        loadTables(); loadAllFoods(); loadCategories();
        setupOrderTable();
        setupDeleteColumn();
        addFoodButton.setOnAction(event -> addFood());
        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
        categoryChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateFoodDisplay(newVal));
        gotoAdminButton.setVisible(CurrentUserSession.getInstance().isQuanLy());
    }

    @FXML private void gotoAdminScreen() throws IOException { App.setRoot("Admin"); }
    @FXML private void gotoLoginScreen() throws IOException { App.setRoot("login"); }

    //Load Table lên Flowpane
    private void loadTables() {
        TableFoodDAL dal = new TableFoodDAL(provider);
        for (TableFood table : dal.getAllTables()) {
            ToggleButton btn = createToggleButton(table.getName(), table.getId(), tableBtnGroup);
            btn.setOnAction(this::chonTable);
            danhSachBan.getChildren().add(btn);
        }
    }
    //Load món ăn lên Flowpane
    private void loadAllFoods() {
        for (Food food : new FoodDAL(provider).getAllFood()) loadFoodItem(food);
    }
    //Load danh mục vào choicebox
    private void loadCategories() {
        List<Category> categories = new CategoryDAL(provider).getAllCategories();
        categoryChoiceBox.setItems(FXCollections.observableArrayList(categories));
    }
    private void updateFoodDisplay(Category selectedCategory) {
        danhSachMon.getChildren().clear();

        List<Food> foods = new FoodDAL(provider).getAllFood();

        // Lọc theo category nếu được chọn
        if (selectedCategory != null) {
            foods = foods.stream()
                    .filter(f -> f.getCategoryId() == selectedCategory.getId())
                    .collect(Collectors.toList());
        }

        // Load các món ăn hợp lệ vào giao diện
        for (Food food : foods) {
            loadFoodItem(food); // Gọi phiên bản nhận Food làm tham số
        }
    }

    //Xử lý khi chọn vào món ăn
    private void chonFood(javafx.event.ActionEvent event, ImageView imageView) {
        imageViews.forEach(img -> img.setOpacity(1));
        imageView.setOpacity(0.5);
        choseFood = (int) ((ToggleButton) event.getSource()).getUserData();
    }
    //Xử lý khi chọn vào bàn
    private void chonTable(javafx.event.ActionEvent event) {
        totalPrice.setText("0");
        foodBtbGroup.getToggles().forEach(t -> t.setSelected(false));
        choseFood = 0;
        choseTable = (int) ((ToggleButton) event.getSource()).getUserData();
        chosenBill = getBillIdByTableId(choseTable);
        updateOrderTable(choseTable);
    }
    //Kiểm tra bàn đang có khách nào không
    private int getBillIdByTableId(int tableId) {
        TableFood table = new TableFoodDAL(provider).getTableById(tableId);
        messageTableLabel.setText(table.isAvailable() ? "Bàn đang trống" : "Bàn " + tableId + " có khách!");
        return new BillDAL(provider).getAllBills().stream()
                .filter(b -> b.getTableID() == tableId && !b.isPaid())
                .map(Bill::getId).findFirst().orElse(0);
    }
    //Cập nhật món ăn đã gọi của bàn vào hóa đơn
    private double updateOrderTable(int tableId) {
        Optional<Bill> billOpt = new BillDAL(provider).getAllBills().stream()
                .filter(b -> b.getTableID() == tableId && !b.isPaid()).findFirst();
        totalPrice.setText("");
        if (billOpt.isPresent()) {
            orderItems = getOrderItemsForBill(billOpt.get().getId());
            thanhToanButton.setDisable(false);
            bangHoaDon.setItems(FXCollections.observableArrayList(orderItems));
        } else bangHoaDon.getItems().clear();

        double total = orderItems.stream().mapToDouble(OrderItemView::getTotalPrice).sum();
        totalPrice.setText(convertToVND(total));
        return total;
    }
    //Lấy danh sách món ăn nằm trong Bill
    private List<OrderItemView> getOrderItemsForBill(int billId) {
        List<Order> orders = new OrderDAL(provider).getOrdersByBillID(billId);
        Map<Integer, Food> foodMap = new FoodDAL(provider).getAllFood().stream().collect(Collectors.toMap(Food::getId, f -> f));
        return orders.stream().map(o -> {
            Food f = foodMap.get(o.getFoodID());
            return new OrderItemView(f.getName(),o.getId(), o.getCount(), f.getPrice(), f.getId());
        }).collect(Collectors.toList());
    }
    //Tạo danh sách món đã gọi vào hóa đơn
    private void setupOrderTable() {
        foodNameCol.setCellValueFactory(new PropertyValueFactory<>("foodName"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        pricePerItemCol.setCellValueFactory(new PropertyValueFactory<>("pricePerItem"));
        totalPriceCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        quantityCol.setCellFactory(TextFieldTableCell.forTableColumn(new LongStringConverter()));

        quantityCol.setOnEditCommit(e -> {
            OrderItemView item = e.getRowValue();
            long newQty = e.getNewValue();
            if (newQty <= 0) { showAlert("Số lượng không hợp lệ", "Phải > 0"); bangHoaDon.refresh(); return; }
            item.setQuantity(newQty); item.setTotalPrice(newQty * item.getPricePerItem());
            new OrderDAL(provider).updateOrder(new Order(chosenBill, item.getFoodID(), newQty));
            bangHoaDon.refresh();
        });
    }
    //Xử lý logic nút thêm món
    @FXML public void addFood() {
        int quantity = quantitySpinner.getValue();
        if (choseTable == 0 || choseFood == 0) { showAlert("Thiếu thông tin", "Chọn bàn và món trước"); return; }

        BillDAL billDAL = new BillDAL(provider);
        OrderDAL orderDAL = new OrderDAL(provider);

        try {
            if ((chosenBill = getBillIdByTableId(choseTable)) == 0) {
                Bill newBill = new Bill(choseTable, 0, 0, false);
                chosenBill = billDAL.insertBill(newBill);
                String tableName= "Bàn "+choseTable;
                new TableFoodDAL(provider).updateTable(new TableFood(choseTable,tableName, false));
                messageTableLabel.setText("Bàn " + choseTable + " có khách!");
            }
            if (orderDAL.existsOrder(chosenBill, choseFood))
                orderDAL.increaseQuantity(chosenBill, choseFood, quantity);
            else orderDAL.insertOrder(new Order(chosenBill, choseFood, quantity));
            thanhToanButton.setDisable(false);
            updateOrderTable(choseTable);
        } catch (SQLException e) {
            showAlert("Lỗi CSDL", e.getMessage());
        }
    }
    //Xử lý logic nút thanh toán
    @FXML
    public void thanhToan() {
        BillDAL billDAL = new BillDAL(provider);
        TableFoodDAL tableDAL = new TableFoodDAL(provider);
        // 1. Cập nhật trạng thái bàn
        TableFood table = tableDAL.getTableById(choseTable);
        Bill bill = billDAL.getBillById(chosenBill);
        if (bill == null) {return;}
        if (table != null) {
            table.setAvailable(true);
            tableDAL.updateTable(table); // Cập nhật vào DB
        }

        // 2. Cập nhật hóa đơn
        if (bill != null) {
            double totalPriceLocal = 0;

            if (bill.getDisCount() > 0) {
                totalPriceLocal = bill.getTotalPrice();
            } else {
                for (OrderItemView orderItem : orderItems) {
                    totalPriceLocal += orderItem.getTotalPrice();
                }
            }

            bill.setPaid(true);
            bill.setPaidDate(LocalDateTime.now());
            bill.setTotalPrice(totalPriceLocal);

            billDAL.updateBill(bill); // Cập nhật vào DB

            // === TỰ ĐỘNG TRỪ KHO NGUYÊN LIỆU ===
            truKhoNguyenLieu(orderItems); 
        }

        // 3. Reset giao diện
        discountField.setText("0");
        totalPrice.setText("0");
        bangHoaDon.getItems().clear();
        thanhToanButton.setDisable(true);
    }

    /**
     * Tự động trừ kho nguyên liệu dựa trên các món trong hóa đơn đã thanh toán.
     * Phục vụ Use Case [UC011].
     * @param orderItems Danh sách các món hàng đã được bán.
     */
    private void truKhoNguyenLieu(List<OrderItemView> orderItems) {
        DinhLuongMonAnDAL dinhLuongDAL = new DinhLuongMonAnDAL(provider);
        NguyenLieuDAL nguyenLieuDAL = new NguyenLieuDAL(provider);

        // Lặp qua từng món ăn trong hóa đơn
        for (OrderItemView item : orderItems) {
            int idMonAn = item.getFoodID();
            long soLuongMon = item.getQuantity();

            // Với mỗi món, lấy công thức (danh sách nguyên liệu và định lượng) của nó
            List<DinhLuongMonAn> congThuc = dinhLuongDAL.getDinhLuongByMonAnId(idMonAn);

            // Nếu món ăn có công thức, lặp qua từng nguyên liệu trong công thức
            if (!congThuc.isEmpty()) {
                for (DinhLuongMonAn nguyenLieuCan : congThuc) {
                    // Tính toán tổng số lượng nguyên liệu cần trừ
                    double luongGiam = nguyenLieuCan.getSoLuongCan() * soLuongMon;
                    
                    // Gọi DAL để cập nhật (trừ) số lượng trong CSDL
                    nguyenLieuDAL.capNhatSoLuongTon(nguyenLieuCan.getIdNguyenLieu(), luongGiam);

                    // In ra console để kiểm tra (bạn có thể xóa sau này)
                    System.out.println("Tru kho: Mon ID " + idMonAn + ", Nguyen Lieu ID " + nguyenLieuCan.getIdNguyenLieu() + ", So luong giam: " + luongGiam);
                }
            }
        }
        System.out.println("Hoan tat cap nhat kho cho hoa don.");
    }


    private void setupDeleteColumn() {
        deleteCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Xóa");

            {
                btn.setStyle("-fx-background-color: #FF6666; -fx-text-fill: white; -fx-font-size: 10;");
                btn.setOnAction(event -> {
                    OrderItemView item = getTableView().getItems().get(getIndex());
                    xoaItem(item);
                });
            }
            private void xoaItem(OrderItemView item) {
                OrderDAL orderDAL = new OrderDAL(provider);
                boolean daXoa = orderDAL.deleteOrder(item.getOrderId());
                if (daXoa) {
                    bangHoaDon.getItems().remove(item);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Lỗi");
                    alert.setHeaderText(null);
                    alert.setContentText(String.valueOf(item.getOrderId()));
                    alert.showAndWait();
                }
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
    }
    //Xử lý logic nút khuyến mãi
    @FXML public void apKhuyenMai() {
        int discount = Integer.parseInt(discountField.getText());
        double total = orderItems.stream().mapToDouble(OrderItemView::getTotalPrice).sum();
        double finalPrice = total - (total * discount / 100.0);
        new BillDAL(provider).getAllBills().stream()
                .filter(b -> b.getId() == chosenBill)
                .findFirst().ifPresent(b -> { b.setDisCount(discount); b.setTotalPrice(finalPrice); });
        totalPrice.setText(convertToVND(finalPrice));
    }

    private String convertToVND(double money) {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN")).format(money);
    }

    private void loadFoodItem(Food food) {
        VBox vBox = new VBox();

        vBox.setPrefWidth(100);
        vBox.setPrefHeight(100);
        vBox.setAlignment(Pos.CENTER);

        StackPane stackPane = new StackPane();

        ImageView imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);

        imageViews.add(imageView);


        File imageFile = new File("images/" + food.getImgName());

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

    private ToggleButton createToggleButton(String text, int id, ToggleGroup group) {
        ToggleButton btn = new ToggleButton(text);
        btn.setToggleGroup(group);
        btn.setPrefSize(100, 100);
        btn.setUserData(id);
        return btn;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(message);
        alert.showAndWait();
    }
}