package QuanLyQuanCafe.model;

public class OrderItemView {

    private String foodName;
    private int orderId;
    private long quantity;
    private long pricePerItem;
    private long totalPrice;
    private int foodID;

    public OrderItemView(String foodName, int orderId, long quantity, long pricePerItem, int foodID) {
        this.foodName = foodName;
        this.orderId = orderId;
        this.quantity = quantity;
        this.pricePerItem = pricePerItem;
        this.foodID = foodID;
        this.totalPrice = quantity * pricePerItem;
    }

    public int getFoodID() {
        return foodID;
    }

    public void setFoodID(int foodID) {
        this.foodID = foodID;
    }

    public int getOrderId() {return orderId;}

    public void setOrderId(int orderId) {this.orderId = orderId;}

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public long getPricePerItem() {
        return pricePerItem;
    }

    public void setPricePerItem(long pricePerItem) {
        this.pricePerItem = pricePerItem;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }


}
