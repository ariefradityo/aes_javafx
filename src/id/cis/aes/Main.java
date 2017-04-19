package id.cis.aes;

import id.cis.aes.main.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.security.Security;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Security.setProperty("crypto.policy", "unlimited");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main/main.fxml"));
        Parent root = loader.load();

        MainController controller = loader.getController();
        controller.setMainStage(primaryStage);

        primaryStage.setTitle("AES Project CIS");
        primaryStage.setScene(new Scene(root, 640, 480));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
