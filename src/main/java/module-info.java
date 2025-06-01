module org.example.quanlyquancafe {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.sql;
    requires jbcrypt;


    opens QuanLyQuanCafe to javafx.fxml;
    exports QuanLyQuanCafe;
    exports QuanLyQuanCafe.controller;
    opens QuanLyQuanCafe.controller to javafx.fxml;
}