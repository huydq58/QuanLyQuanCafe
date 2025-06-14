package QuanLyQuanCafe.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

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
import QuanLyQuanCafe.model.NguyenLieu; // <-- THÊM IMPORT NÀY
import QuanLyQuanCafe.model.NguyenLieuDAL;
import QuanLyQuanCafe.model.Order;
import QuanLyQuanCafe.model.OrderDAL;
import QuanLyQuanCafe.model.OrderItemView;
import QuanLyQuanCafe.model.TableFood;
import QuanLyQuanCafe.model.TableFoodDAL;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
import javafx.scene.layout.TilePane;
import javafx.stage.FileChooser;
import javafx.util.converter.LongStringConverter;


public class MainWindow {

    @FXML private FlowPane danhSachBan;
    @FXML private TilePane danhSachMon;
    @FXML private Label messageTableLabel;
    @FXML private TableView<OrderItemView> bangHoaDon;
    @FXML private TableColumn<OrderItemView, String> foodNameCol;
    @FXML private TableColumn<OrderItemView, Long> quantityCol, totalPriceCol;
    @FXML private ChoiceBox<Category> categoryChoiceBox;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private Button addFoodButton, gotoAdminButton, thanhToanButton,signoutButton;
    @FXML private TextField totalPrice, discountField;
    @FXML private TableColumn<OrderItemView, Void> deleteCol;

    private int choseTable;
    private int choseFood;
    private int chosenBill;
    private final ToggleGroup foodBtbGroup = new ToggleGroup();
    private List<OrderItemView> orderItems = new ArrayList<>();
    private static final DataProvider provider = new DataProvider();

    // --- BẮT ĐẦU THÊM MỚI ---
    private final NguyenLieuDAL nguyenLieuDAL;
    private final DinhLuongMonAnDAL dinhLuongDAL;
    // --- KẾT THÚC THÊM MỚI ---

    // --- BẮT ĐẦU SỬA ĐỔI CONSTRUCTOR ---
    public MainWindow() {
        this.nguyenLieuDAL = new NguyenLieuDAL(provider);
        this.dinhLuongDAL = new DinhLuongMonAnDAL(provider);
    }
    // --- KẾT THÚC SỬA ĐỔI CONSTRUCTOR ---
    
    @FXML
    public void initialize() {
        thanhToanButton.setDisable(true);
        totalPrice.setText("");
        loadTables();
        loadAllFoods();
        loadCategories();
        setupOrderTable();
        setupDeleteColumn();
        addFoodButton.setOnAction(event -> addFood());
        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
        categoryChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateFoodDisplay(newVal));
        gotoAdminButton.setVisible(CurrentUserSession.getInstance().isQuanLy());
    }
    
    // --- BẮT ĐẦU THÊM MỚI: PHƯƠNG THỨC KIỂM TRA NGUYÊN LIỆU ---
    /**
     * Kiểm tra xem các nguyên liệu có đủ cho một món ăn với số lượng cho trước không.
     * @param foodId ID của món ăn cần kiểm tra.
     * @param quantityToAdd Số lượng món ăn sẽ được thêm.
     * @return null nếu đủ nguyên liệu, ngược lại trả về chuỗi thông báo lỗi.
     */
    private String kiemTraNguyenLieu(int foodId, int quantityToAdd) {
        List<DinhLuongMonAn> congThuc = dinhLuongDAL.getDinhLuongByMonAnId(foodId);
    
        // Nếu món không có công thức, coi như luôn đủ
        if (congThuc == null || congThuc.isEmpty()) {
            return null;
        }
    
        for (DinhLuongMonAn dinhLuong : congThuc) {
            NguyenLieu nguyenLieuTrongKho = nguyenLieuDAL.getNguyenLieuById(dinhLuong.getIdNguyenLieu());
    
            if (nguyenLieuTrongKho == null) {
                return "Lỗi dữ liệu: Không tìm thấy nguyên liệu ID " + dinhLuong.getIdNguyenLieu() + " trong kho.";
            }
    
            double luongCan = dinhLuong.getSoLuongCan() * quantityToAdd;
    
            if (nguyenLieuTrongKho.getSoLuongTon() < luongCan) {
                return String.format("Không đủ '%s'.\nCần %.2f %s, chỉ còn %.2f %s.",
                        nguyenLieuTrongKho.getTen(),
                        luongCan, nguyenLieuTrongKho.getDonViTinh(),
                        nguyenLieuTrongKho.getSoLuongTon(), nguyenLieuTrongKho.getDonViTinh());
            }
        }
    
        // Nếu vòng lặp kết thúc mà không có lỗi, tức là đủ nguyên liệu
        return null;
    }
    // --- KẾT THÚC THÊM MỚI ---

    @FXML
    public void addFood() {
        if (choseTable == 0) { showAlert("Thiếu thông tin", "Vui lòng chọn bàn trước!"); return; }
        if (choseFood == 0) { showAlert("Thiếu thông tin", "Vui lòng chọn món ăn!"); return; }
        int quantity = quantitySpinner.getValue();

        // --- BẮT ĐẦU THÊM MỚI: LOGIC KIỂM TRA KHO ---
        String errorMessage = kiemTraNguyenLieu(choseFood, quantity);
        if (errorMessage != null) {
            showAlert("Không đủ nguyên liệu", errorMessage);
            return; // Dừng lại, không thêm món ăn
        }
        // --- KẾT THÚC THÊM MỚI ---

        BillDAL billDAL = new BillDAL(provider);
        OrderDAL orderDAL = new OrderDAL(provider);

        try {
            if ((chosenBill = getBillIdByTableId(choseTable)) == 0) {
                Bill newBill = new Bill(choseTable, 0, 0, false);
                chosenBill = billDAL.insertBill(newBill);
                TableFood table = new TableFoodDAL(provider).getTableById(choseTable);
                if (table != null) {
                    table.setAvailable(false);
                    new TableFoodDAL(provider).updateTable(table);
                    loadTables();
                }
                messageTableLabel.setText("Bàn " + choseTable + " có khách!");
            }
            if (orderDAL.existsOrder(chosenBill, choseFood)) {
                orderDAL.increaseQuantity(chosenBill, choseFood, quantity);
            } else {
                orderDAL.insertOrder(new Order(chosenBill, choseFood, quantity));
            }
            updateOrderTable(choseTable);
        } catch (SQLException e) {
            showAlert("Lỗi CSDL", e.getMessage());
        }
    }
    
    // ... (Các phương thức khác giữ nguyên không thay đổi)
    // ... (gotoAdminScreen, signOut, loadTables, loadAllFoods, loadCategories, v.v...)
    // ... (Bạn chỉ cần sao chép và thay thế toàn bộ file là được)

    @FXML
    private void gotoAdminScreen() throws IOException {
        App.setRoot("Admin");
    }
    @FXML
    private void signOut() throws IOException {

        App.setRoot("Login");
    }

    private void loadTables() {
        danhSachBan.getChildren().clear();
        TableFoodDAL dal = new TableFoodDAL(provider);
        List<TableFood> tables = dal.getAllTables();
        final ToggleGroup tableGroup = new ToggleGroup();

        for (TableFood table : tables) {
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource("TableCard.fxml"));
                StackPane tableCardNode = loader.load();
                
                Label tableNameLabel = (Label) tableCardNode.lookup("#tableNameLabel");
                ToggleButton selectTableToggle = (ToggleButton) tableCardNode.lookup("#selectTableToggle");
                
                tableNameLabel.setText(table.getName());
                selectTableToggle.setUserData(table.getId());
                selectTableToggle.setToggleGroup(tableGroup);
                
                if (!table.isAvailable()) {
                    tableCardNode.getStyleClass().add("table-card-unavailable");
                }

                selectTableToggle.setOnAction(this::chonTable);
                
                tableGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
                    danhSachBan.getChildren().forEach(node -> node.getStyleClass().remove("selected"));
                    if (newToggle != null) {
                        ((Node) newToggle).getParent().getStyleClass().add("selected");
                    }
                });

                danhSachBan.getChildren().add(tableCardNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadAllFoods() {
        danhSachMon.getChildren().clear();
        List<Food> allFoods = new FoodDAL(provider).getAllFood();
        for (Food food : allFoods) {
            loadFoodItem(food);
        }
    }

    private void loadCategories() {
    List<Category> categories = new CategoryDAL(provider).getAllCategories();

    // --- BẮT ĐẦU THAY ĐỔI ---
    // 1. Tạo một đối tượng Category ảo cho "Tất cả"
    Category allCategory = new Category();
    allCategory.setId(0); // Dùng ID 0 hoặc một số âm để nhận biết
    allCategory.setName("Tất cả");

    // 2. Thêm "Tất cả" vào đầu danh sách
    List<Category> displayCategories = new ArrayList<>();
    displayCategories.add(allCategory);
    displayCategories.addAll(categories);
    // --- KẾT THÚC THAY ĐỔI ---

    categoryChoiceBox.setItems(FXCollections.observableArrayList(displayCategories));
    // Tùy chọn: Mặc định chọn "Tất cả" khi khởi động
    categoryChoiceBox.getSelectionModel().selectFirst();
}

private void updateFoodDisplay(Category selectedCategory) {
    danhSachMon.getChildren().clear();
    List<Food> allFoods = new FoodDAL(provider).getAllFood();
    List<Food> foodsToDisplay; // Danh sách món ăn sẽ được hiển thị

    // --- BẮT ĐẦU THAY ĐỔI ---
    // Kiểm tra xem người dùng có chọn "Tất cả" hay không
    if (selectedCategory != null && selectedCategory.getId() != 0) {
        // Nếu chọn một danh mục cụ thể -> Lọc như cũ
        foodsToDisplay = allFoods.stream()
                .filter(f -> f.getCategoryId() == selectedCategory.getId())
                .collect(Collectors.toList());
    } else {
        // Nếu không chọn gì hoặc chọn "Tất cả" (ID = 0) -> Hiển thị tất cả món ăn
        foodsToDisplay = allFoods;
    }
    // --- KẾT THÚC THAY ĐỔI ---

    for (Food food : foodsToDisplay) {
        loadFoodItem(food);
    }
}


    private void loadFoodItem(Food food) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("FoodCard.fxml"));
            StackPane foodCardNode = loader.load();

            ImageView foodImageView = (ImageView) foodCardNode.lookup("#foodImageView");
            Label foodNameLabel = (Label) foodCardNode.lookup("#foodNameLabel");
            Label foodPriceLabel = (Label) foodCardNode.lookup("#foodPriceLabel");
            ToggleButton selectFoodToggle = (ToggleButton) foodCardNode.lookup("#selectFoodToggle");

            foodNameLabel.setText(food.getName());
            foodPriceLabel.setText(convertToVND(food.getPrice()));

            String imagePathFromDB = food.getImgName();
            if (imagePathFromDB != null && !imagePathFromDB.isEmpty()) {
                String filename;
                if (imagePathFromDB.contains("\\")) {
                    filename = imagePathFromDB.substring(imagePathFromDB.lastIndexOf("\\") + 1);
                } else if (imagePathFromDB.contains("/")) {
                    filename = imagePathFromDB.substring(imagePathFromDB.lastIndexOf("/") + 1);
                } else {
                    filename = imagePathFromDB;
                }
                
                String classpath = "/images/" + filename;
                InputStream imageStream = App.class.getResourceAsStream(classpath);
                
                if (imageStream != null) {
                    foodImageView.setImage(new Image(imageStream));
                } else {
                    System.err.println("Không thể tìm thấy tài nguyên ảnh: " + classpath);
                }
            }
            
            selectFoodToggle.setUserData(food.getId());
            selectFoodToggle.setToggleGroup(foodBtbGroup);
            
            selectFoodToggle.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) {
                    foodCardNode.getStyleClass().add("selected");
                } else {
                    foodCardNode.getStyleClass().remove("selected");
                }
            });
            
            selectFoodToggle.setOnAction(event -> choseFood = (int) ((ToggleButton) event.getSource()).getUserData());
            
            danhSachMon.getChildren().add(foodCardNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void chonTable(javafx.event.ActionEvent event) {
        totalPrice.setText("0");
        foodBtbGroup.getToggles().forEach(toggle -> {
            toggle.setSelected(false);
        });
        choseFood = 0;
        choseTable = (int) ((ToggleButton) event.getSource()).getUserData();
        chosenBill = getBillIdByTableId(choseTable);
        updateOrderTable(choseTable);
    }

    private int getBillIdByTableId(int tableId) {
        TableFood table = new TableFoodDAL(provider).getTableById(tableId);
        messageTableLabel.setText(table.isAvailable() ? "Bàn đang trống" : "Bàn " + tableId + " có khách!");
        return new BillDAL(provider).getAllBills().stream()
                .filter(b -> b.getTableID() == tableId && !b.isPaid())
                .map(Bill::getId).findFirst().orElse(0);
    }

    private double updateOrderTable(int tableId) {
        Optional<Bill> billOpt = new BillDAL(provider).getAllBills().stream()
                .filter(b -> b.getTableID() == tableId && !b.isPaid()).findFirst();
        totalPrice.setText("0");
        List<OrderItemView> items = (billOpt.isPresent()) ? getOrderItemsForBill(billOpt.get().getId()) : new ArrayList<>();
        orderItems = items;

        if (!orderItems.isEmpty()) {
            thanhToanButton.setDisable(false);
            bangHoaDon.setItems(FXCollections.observableArrayList(orderItems));
        } else {
            bangHoaDon.getItems().clear();
            thanhToanButton.setDisable(true);
        }

        double total = orderItems.stream().mapToDouble(OrderItemView::getTotalPrice).sum();
        totalPrice.setText(convertToVND(total));
        apKhuyenMai(); 
        return total;
    }

    private List<OrderItemView> getOrderItemsForBill(int billId) {
        List<Order> orders = new OrderDAL(provider).getOrdersByBillID(billId);
        Map<Integer, Food> foodMap = new FoodDAL(provider).getAllFood().stream().collect(Collectors.toMap(Food::getId, f -> f));
        return orders.stream().map(o -> {
            Food f = foodMap.get(o.getFoodID());
            return new OrderItemView(f.getName(), o.getId(), o.getCount(), f.getPrice(), f.getId());
        }).collect(Collectors.toList());
    }

    private void setupOrderTable() {
        foodNameCol.setCellValueFactory(new PropertyValueFactory<>("foodName"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        totalPriceCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        quantityCol.setCellFactory(TextFieldTableCell.forTableColumn(new LongStringConverter()));

        quantityCol.setOnEditCommit(e -> {
            OrderItemView item = e.getRowValue();
            long newQty = e.getNewValue();
            if (newQty <= 0) {
                showAlert("Số lượng không hợp lệ", "Phải > 0");
                bangHoaDon.refresh();
                return;
            }
            item.setQuantity(newQty);
            item.setTotalPrice(newQty * item.getPricePerItem());
            new OrderDAL(provider).updateOrder(new Order(chosenBill, item.getFoodID(), newQty));
            updateOrderTable(choseTable);
        });
    }

    @FXML
    public void thanhToan() {
        if (chosenBill == 0) return;

        BillDAL billDAL = new BillDAL(provider);
        TableFoodDAL tableDAL = new TableFoodDAL(provider);
        
        Bill bill = billDAL.getBillById(chosenBill);
        if (bill != null) {
            double finalPrice = calculateFinalTotal();
            int discount = Integer.parseInt(discountField.getText());

            bill.setPaid(true);
            bill.setPaidDate(LocalDateTime.now());
            bill.setTotalPrice(finalPrice);
            bill.setDisCount(discount);
            billDAL.updateBill(bill);
            
            // KÍCH HOẠT LẠI TÍNH NĂNG TRỪ KHO
            truKhoNguyenLieu(orderItems);
        }
        
        TableFood table = tableDAL.getTableById(choseTable);
        if (table != null) {
            table.setAvailable(true);
            tableDAL.updateTable(table);
            loadTables();
        }
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận");
        confirmAlert.setHeaderText("In hóa đơn");
        confirmAlert.setContentText("Bạn có muốn in hóa đơn ra file PDF không?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            handlePrintBillToPDF();
        } else {
            showAlert("Thông báo", "Hủy in hóa đơn.");
        }

        // Reset UI
        discountField.setText("0");
        updateOrderTable(choseTable);
        messageTableLabel.setText("Vui lòng chọn bàn");
    }

    private void truKhoNguyenLieu(List<OrderItemView> orderItems) {
        DinhLuongMonAnDAL dinhLuongDAL = new DinhLuongMonAnDAL(provider);
        NguyenLieuDAL nguyenLieuDAL = new NguyenLieuDAL(provider);

        for (OrderItemView item : orderItems) {
            int idMonAn = item.getFoodID();
            long soLuongMon = item.getQuantity();

            List<DinhLuongMonAn> congThuc = dinhLuongDAL.getDinhLuongByMonAnId(idMonAn);

            if (!congThuc.isEmpty()) {
                for (DinhLuongMonAn nguyenLieuCan : congThuc) {
                    double luongGiam = nguyenLieuCan.getSoLuongCan() * soLuongMon;
                    nguyenLieuDAL.capNhatSoLuongTon(nguyenLieuCan.getIdNguyenLieu(), luongGiam);
                }
            }
        }
        System.out.println("Đã cập nhật kho sau khi thanh toán.");
    }

    private void setupDeleteColumn() {
        deleteCol.setCellFactory(col -> {
            Button btn = new Button("Xóa");
            TableCell<OrderItemView, Void> cell = new TableCell<>() {
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : btn);
                }
            };
            btn.setStyle("-fx-background-color: #FF6666; -fx-text-fill: white; -fx-font-size: 10;");
            btn.setOnAction(event -> {
                OrderItemView item = (OrderItemView) cell.getTableRow().getItem();
                if (item != null) xoaItem(item);
            });
            return cell;
        });
    }
    
    private void xoaItem(OrderItemView item) {
        OrderDAL orderDAL = new OrderDAL(provider);
        if (orderDAL.deleteOrder(item.getOrderId())) {
            updateOrderTable(choseTable);
        } else {
            showAlert("Lỗi", "Không thể xóa món ăn khỏi hóa đơn.");
        }
    }

    private double calculateFinalTotal() {
        double subTotal = orderItems.stream().mapToDouble(OrderItemView::getTotalPrice).sum();
        int discount = 0;
        try {
            discount = Integer.parseInt(discountField.getText());
            if (discount < 0 || discount > 100) discount = 0;
        } catch (NumberFormatException e) { discount = 0; }
        return subTotal - (subTotal * discount / 100.0);
    }
    
    @FXML
    public void apKhuyenMai() {
        totalPrice.setText(convertToVND(calculateFinalTotal()));
    }

    private String convertToVND(double money) {
        String formatted = NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(money);
        return formatted.replace("₫", "d");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void handlePrintBillToPDF() {
        if (orderItems.isEmpty()) {
            showAlert("Thông báo", "Không có gì để in.");
            return;
        }
        File currentDir = new File(System.getProperty("user.dir"));

        File hoaDonDir = new File(currentDir, "HoaDon");
        if (!hoaDonDir.exists()) {
            hoaDonDir.mkdirs();
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu Hóa Đơn PDF");
        fileChooser.setInitialDirectory(hoaDonDir); // ← thiết lập thư mục khởi chạy
        fileChooser.setInitialFileName("HoaDon_Ban" + choseTable + "_" + chosenBill + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));


        File file = fileChooser.showSaveDialog(thanhToanButton.getScene().getWindow());

        if (file != null) {
            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage();
                document.addPage(page);

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 22);
                    contentStream.setLeading(14.5f);
                    contentStream.newLineAtOffset(50, 750);

                    contentStream.showText(removeDiacritics("HÓA ĐƠN THANH TOÁN"));
                    contentStream.newLine();
                    contentStream.newLine();

                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    TableFood table = new TableFoodDAL(provider).getTableById(choseTable);
                    contentStream.showText(removeDiacritics("Bàn: " + (table != null ? table.getName() : "N/A")));
                    contentStream.newLine();
                    contentStream.showText("Hoa don so: " + chosenBill);
                    contentStream.newLine();
                    contentStream.showText(removeDiacritics("Thời gian: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"))));
                    contentStream.newLine();
                    contentStream.showText("-------------------------------------------------------------------");
                    contentStream.newLine();

                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.showText(String.format("%-25s %5s %15s", removeDiacritics("Tên Món"), "SL", removeDiacritics("Thành Tiền")));
                    contentStream.newLine();
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    contentStream.showText("-------------------------------------------------------------------");
                    contentStream.newLine();

                    for (OrderItemView item : orderItems) {
                        String line = String.format("%-25.25s %5d %15s",
                                removeDiacritics(item.getFoodName()),
                                item.getQuantity(),
                                convertToVND(item.getTotalPrice()));
                        contentStream.showText(line);
                        contentStream.newLine();
                    }

                    contentStream.showText("-------------------------------------------------------------------");
                    contentStream.newLine();

                    double finalTotal = calculateFinalTotal();
                    double subTotal = orderItems.stream().mapToDouble(OrderItemView::getTotalPrice).sum();
                    int discount = Integer.parseInt(discountField.getText());

                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                    contentStream.showText(String.format("%32s %15s", removeDiacritics("Tổng Cộng:"), convertToVND(subTotal)));
                    contentStream.newLine();
                    if (discount > 0) {
                        contentStream.showText(String.format("%32s %15s", removeDiacritics("Giảm giá (" + discount + "%):"), "- " + convertToVND(subTotal - finalTotal)));
                        contentStream.newLine();
                    }
                    contentStream.showText(String.format("%32s %15s", removeDiacritics("THANH TOÁN:"), convertToVND(finalTotal)));
                    contentStream.newLine();
                    contentStream.newLine();
                    contentStream.newLine();

                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                    contentStream.showText(removeDiacritics("Cảm ơn quý khách và hẹn gặp lại!"));

                    contentStream.endText();
                }
                document.save(file);
                showAlert("Thành công", "Đã lưu hóa đơn thành công tại:\n" + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Lỗi", "Đã xảy ra lỗi khi tạo file PDF.");
            }
        }
    }
    
    private String removeDiacritics(String str) {
        if (str == null) return null;
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String temp = pattern.matcher(nfdNormalizedString).replaceAll("");
        return temp.replace('đ', 'd').replace('Đ', 'D');
    }
}