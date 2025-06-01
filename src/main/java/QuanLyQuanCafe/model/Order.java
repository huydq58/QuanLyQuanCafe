package QuanLyQuanCafe.model;


public class Order {
    private int id;
    private int billID;
    private int foodID;
    private long count;

    public Order() {}

    public Order(int billid,int foodid,long newqty) {
        this.billID = billid;
        this.foodID = foodid;
        this.count = newqty;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBillID() {
        return billID;
    }

    public void setBillID(int billID) {
        this.billID = billID;
    }

    public int getFoodID() {
        return foodID;
    }

    public void setFoodID(int foodID) {
        this.foodID = foodID;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

}
