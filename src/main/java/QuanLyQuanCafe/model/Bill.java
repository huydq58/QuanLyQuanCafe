package QuanLyQuanCafe.model;

import java.time.LocalDateTime;

public class Bill {

    private int id;
    private int tableID;
    private int disCount;
    private boolean paid;
    private LocalDateTime orderDate;
    private LocalDateTime paidDate;
    private double totalPrice;

    public Bill() {
        orderDate = LocalDateTime.now();
        paid = false;

    }
    public Bill(int tableid, int discount, double totalPrice, boolean ispaid) {
        orderDate = LocalDateTime.now();
        paid = ispaid;
        disCount = discount;
        tableID = tableid;

    }

    public LocalDateTime getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(LocalDateTime paidDate) {
        this.paidDate = paidDate;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public int getTableID() {
        return tableID;
    }

    public void setTableID(int tableID) {
        this.tableID = tableID;
    }

    public int getDisCount() {
        return disCount;
    }

    public void setDisCount(int disCount) {
        this.disCount = disCount;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

}

