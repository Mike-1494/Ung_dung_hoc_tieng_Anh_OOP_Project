package App;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DictionaryApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("DictionaryApplication.fxml"));
        Scene MenuScene = new Scene(root);
        primaryStage.setScene(MenuScene);
        primaryStage.show();
    }

    public static void runApplication() {

    }

    public static void main(String[] args) {
        launch(args);
    }
}
