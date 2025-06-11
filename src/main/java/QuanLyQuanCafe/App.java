package QuanLyQuanCafe;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application{

    private static Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        scene = new Scene(loadFXML("login"));
        
        scene.getStylesheets().add(App.class.getResource("styles.css").toExternalForm());
        
        stage.setScene(scene);
        stage.setTitle("Coffee Management");
        stage.show();
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
    
    public static void setRoot(String fxml) throws IOException {
        Parent root = loadFXML(fxml);
        scene.setRoot(root);

        // Cập nhật kích thước cửa sổ theo từng màn hình
        switch (fxml) {
            case "login":
                // Kích thước cho màn hình đăng nhập
                scene.getWindow().setWidth(800);
                scene.getWindow().setHeight(500);
                scene.getWindow().centerOnScreen();
                break;

            case "MainWindow":
            case "Admin":
                // Kích thước chuẩn cho các màn hình chính (tương đương tablet)
                scene.getWindow().setWidth(1100);
                scene.getWindow().setHeight(700);
                scene.getWindow().centerOnScreen();
                break;
                
            default:
                // Kích thước mặc định cho các trường hợp khác
                scene.getWindow().setWidth(800);
                scene.getWindow().setHeight(600);
                scene.getWindow().centerOnScreen();
                break;
        }
    }
}