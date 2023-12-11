package App;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DictionaryApplication extends Application {
    private String answer;

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        DataStore dataStore = DataStore.getInstance();
        dataStore.Init();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DictionaryApplication.fxml"));
        Parent root = loader.load();
        DictionaryController controller = loader.getController();
        Scene scene = new Scene(root);

        scene.setOnKeyPressed(event -> {
            try {
                controller.handleKeyboardInput(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();
        return;
    }

    public static void runApplication() {

    }

    public static void main(String[] args) {
        launch(args);
    }
}
