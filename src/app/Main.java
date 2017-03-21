package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        javafx.geometry.Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();

        Parent root = FXMLLoader.load(getClass().getResource("window.fxml"));
        Scene primaryScene = new Scene(root, visualBounds.getWidth()/1.2, visualBounds.getHeight()/1.2);
        primaryScene.getStylesheets().add("app/stylesheet.css");
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }
    public static void main(String[] args){
        launch(args);
    }
}
