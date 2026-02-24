package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static final String STYLESHEET = "/view/styles.css";

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/login.fxml"));

        Scene scene = new Scene(loader.load(), 960, 540);

        // Apply modern stylesheet
        String css = getClass().getResource(STYLESHEET).toExternalForm();
        scene.getStylesheets().add(css);

        stage.setTitle("Group30 GreenGrocer - Login");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Get stylesheet URL for use by controllers.
     */
    public static String getStylesheet() {
        return Main.class.getResource(STYLESHEET).toExternalForm();
    }
}
