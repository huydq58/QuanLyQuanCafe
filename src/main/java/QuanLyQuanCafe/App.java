package QuanLyQuanCafe;

import java.io.IOException;

import QuanLyQuanCafe.model.TaiKhoan;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image; // <-- Import thêm lớp Image
import javafx.stage.Stage;

public class App extends Application{

    private static Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        scene = new Scene(loadFXML("login"));
        scene.getStylesheets().add(App.class.getResource("styles.css").toExternalForm());
        
        // === THÊM ĐOẠN CODE ĐỂ TẢI VÀ ĐẶT ICON CHO ỨNG DỤNG ===
        try {
            // Tải icon từ thư mục resources/images
            Image appIcon = new Image(App.class.getResourceAsStream("/images/logo.png"));
            stage.getIcons().add(appIcon);
        } catch (Exception e) {
            System.err.println("Không thể tải file icon ứng dụng: /images/logo.png");
            e.printStackTrace();
        }
        // =======================================================
        
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

        switch (fxml) {
            case "login":
                scene.getWindow().setWidth(800);
                scene.getWindow().setHeight(500);
                scene.getWindow().centerOnScreen();
                break;

            case "MainWindow":
            case "Admin":
                scene.getWindow().setWidth(1100);
                scene.getWindow().setHeight(700);
                scene.getWindow().centerOnScreen();
                break;
                
            default:
                scene.getWindow().setWidth(800);
                scene.getWindow().setHeight(600);
                scene.getWindow().centerOnScreen();
                break;
        }
    }
}