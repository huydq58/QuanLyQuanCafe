package QuanLyQuanCafe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.IOException;

public class App extends Application{

    private static Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        scene = new Scene(loadFXML("login"));
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
                scene.getWindow().setWidth(600);
                scene.getWindow().setHeight(400);
                scene.getWindow().centerOnScreen();
                break;
            case "MainWindow":
                scene.getWindow().setWidth(1050);
                scene.getWindow().setHeight(650);
                scene.getWindow().centerOnScreen();
                break;
            case "AdminScreen":
                scene.getWindow().setWidth(1000);
                scene.getWindow().setHeight(650);
                scene.getWindow().centerOnScreen();
                break;
            default:
                scene.getWindow().setWidth(600);
                scene.getWindow().setHeight(400);
                scene.getWindow().centerOnScreen();
                break;
        }

    }

}
