package QuanLyQuanCafe.controller;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import QuanLyQuanCafe.App;
import QuanLyQuanCafe.database.DataProvider;
import QuanLyQuanCafe.model.Category;
import QuanLyQuanCafe.model.CategoryDAL;
import QuanLyQuanCafe.model.DinhLuongMonAn;
import QuanLyQuanCafe.model.DinhLuongMonAnDAL;
import QuanLyQuanCafe.model.Food;
import QuanLyQuanCafe.model.FoodDAL;
import QuanLyQuanCafe.model.NguyenLieu;
import QuanLyQuanCafe.model.NguyenLieuDAL;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FoodTabController {

    @FXML private TableView<Food> foodTableView;
    @FXML private TableColumn<Food, Integer> foodIdCol;
    @FXML private TableColumn<Food, String> foodNameCol;
    @FXML private TableColumn<Food, String> categoryNameCol;
    @FXML private TableColumn<Food, Long> priceCol;
    @FXML private TextField searchField;

    @FXML private TextField foodIdField;
    @FXML private TextField foodNameField;
    @FXML private ChoiceBox<Category> categoryChoiceBox;
    @FXML private Spinner<Double> priceSpinner;
    @FXML private TextField foodImgPathField;
    @FXML private ImageView foodImg;
    @FXML private Button saveButton;
    
    @FXML private TableView<DinhLuongMonAn> recipeTableView;
    @FXML private TableColumn<DinhLuongMonAn, String> recipeIngredientNameCol;
    @FXML private TableColumn<DinhLuongMonAn, Double> recipeQuantityCol;
    @FXML private TableColumn<DinhLuongMonAn, Void> recipeDeleteCol;
    @FXML private ChoiceBox<NguyenLieu> addIngredientChoiceBox;
    @FXML private TextField addIngredientQuantityField;

    private DataProvider provider;
    private FoodDAL foodDAL;
    private CategoryDAL categoryDAL;
    private DinhLuongMonAnDAL dinhLuongDAL;
    private NguyenLieuDAL nguyenLieuDAL;

    private Map<Integer, String> categoryMap = new HashMap<>();
    private Map<Integer, String> ingredientMap = new HashMap<>();
    private ObservableList<DinhLuongMonAn> currentRecipeList = FXCollections.observableArrayList();
    private Food selectedFood;

    @FXML
    public void initialize() {
        provider = new DataProvider();
        foodDAL = new FoodDAL(provider);
        categoryDAL = new CategoryDAL(provider);
        dinhLuongDAL = new DinhLuongMonAnDAL(provider);
        nguyenLieuDAL = new NguyenLieuDAL(provider);

        setupFoodTable();
        setupRecipeTable();
        
        loadCategories();
        loadIngredients();
        loadFoodItems();
        
        foodTableView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldFood, newFood) -> populateForm(newFood)
        );
        
        setupDoubleSpinner(priceSpinner, 0.0, 10000000.0, 0.0, 1000.0);
        
        priceSpinner.getEditor().setPromptText("Giá bán");
        
    
    }

    private void setupFoodTable() {
        foodIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        foodNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        categoryNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(categoryMap.get(cellData.getValue().getCategoryId())));
    }

    private void setupRecipeTable() {
        recipeIngredientNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(ingredientMap.get(cellData.getValue().getIdNguyenLieu())));
        recipeQuantityCol.setCellValueFactory(new PropertyValueFactory<>("soLuongCan"));
        
        recipeDeleteCol.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Xóa");
            {
                deleteButton.setStyle("-fx-background-color: #FF6666; -fx-text-fill: white; -fx-font-size: 10;");
                deleteButton.setOnAction(event -> {
                    DinhLuongMonAn item = getTableView().getItems().get(getIndex());
                    currentRecipeList.remove(item);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        });
    }

    private void loadFoodItems() {
        ObservableList<Food> foodList = FXCollections.observableArrayList(foodDAL.getAllFood());
        foodTableView.setItems(foodList);
    }

    private void loadCategories() {
        List<Category> categories = categoryDAL.getAllCategories();
        categoryMap = categories.stream().collect(Collectors.toMap(Category::getId, Category::getName));
        categoryChoiceBox.setItems(FXCollections.observableArrayList(categories));
    }

    private void loadIngredients() {
        List<NguyenLieu> ingredients = nguyenLieuDAL.getAllNguyenLieu();
        ingredientMap = ingredients.stream().collect(Collectors.toMap(NguyenLieu::getId, NguyenLieu::getTen));
        addIngredientChoiceBox.setItems(FXCollections.observableArrayList(ingredients));
    }

    private void populateForm(Food food) {
        selectedFood = food;
        if (food == null) {
            clearForm();
            return;
        }

        foodIdField.setText(String.valueOf(food.getId()));
        foodNameField.setText(food.getName());
        priceSpinner.getValueFactory().setValue((double) food.getPrice());
        
        for (Category c : categoryChoiceBox.getItems()) {
            if (c.getId() == food.getCategoryId()) {
                categoryChoiceBox.getSelectionModel().select(c);
                break;
            }
        }
        
        foodImgPathField.setText(food.getImgName());
        if (food.getImgName() != null && !food.getImgName().isEmpty()) {
            InputStream is = App.class.getResourceAsStream("/images/" + food.getImgName());
            if (is != null) {
                foodImg.setImage(new Image(is));
            } else {
                foodImg.setImage(null);
            }
        } else {
            foodImg.setImage(null);
        }

        currentRecipeList.setAll(dinhLuongDAL.getDinhLuongByMonAnId(food.getId()));
        recipeTableView.setItems(currentRecipeList);
    }

    @FXML
    private void handleAddNewFood() {
        foodTableView.getSelectionModel().clearSelection();
        clearForm();
        foodIdField.setText("0");
    }
    
    @FXML
    private void handleDeleteFood() {
        if (selectedFood != null) {
            foodDAL.deleteFood(selectedFood.getId());
            loadFoodItems();
            clearForm();
        }
    }

    @FXML
    private void handleSaveFood() {
        int foodId;
        try {
            foodId = Integer.parseInt(foodIdField.getText());
        } catch (NumberFormatException e) {
            foodId = 0;
        }
        
        Food foodToSave = (foodId == 0) ? new Food() : selectedFood;
        if (foodToSave == null) return;
        
        foodToSave.setName(foodNameField.getText());
        foodToSave.setPrice(priceSpinner.getValue().longValue());
        foodToSave.setCategoryId(categoryChoiceBox.getValue().getId());
        foodToSave.setImageName(foodImgPathField.getText());
        foodToSave.setAvailable(true);

        if (foodId == 0) {
            int newId = foodDAL.addFood(foodToSave);
            foodToSave.setId(newId);
        } else {
            foodDAL.updateFood(foodToSave);
        }
        
        dinhLuongDAL.overwriteDinhLuongForMonAn(foodToSave.getId(), currentRecipeList);
        
        loadFoodItems();
        foodTableView.getSelectionModel().select(foodToSave);
    }
    
    @FXML
    private void handleAddIngredientToRecipe() {
        NguyenLieu selected = addIngredientChoiceBox.getValue();
        if (selected == null) return;

        double quantity;
        try {
            quantity = Double.parseDouble(addIngredientQuantityField.getText());
            if (quantity <= 0) return;
        } catch (NumberFormatException e) {
            return;
        }

        for(DinhLuongMonAn item : currentRecipeList) {
            if (item.getIdNguyenLieu() == selected.getId()) {
                item.setSoLuongCan(quantity);
                recipeTableView.refresh();
                return;
            }
        }
        
        int foodId = (selectedFood != null) ? selectedFood.getId() : 0;
        currentRecipeList.add(new DinhLuongMonAn(foodId, selected.getId(), quantity));
    }

    private void clearForm() {
        selectedFood = null;
        foodIdField.setText("");
        foodNameField.clear();
        priceSpinner.getValueFactory().setValue(0.0);
        categoryChoiceBox.getSelectionModel().clearSelection();
        foodImgPathField.clear();
        foodImg.setImage(null);
        currentRecipeList.clear();
        addIngredientChoiceBox.getSelectionModel().clearSelection();
        addIngredientQuantityField.clear();
    }
    
    private void setupDoubleSpinner(Spinner<Double> spinner, double min, double max, double initialValue, double amountToStepBy) {
        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(min, max, initialValue, amountToStepBy);
        spinner.setValueFactory(valueFactory);
    }
    
    @FXML private void handleSearchFood() { /* Tạm thời để trống */ }
}