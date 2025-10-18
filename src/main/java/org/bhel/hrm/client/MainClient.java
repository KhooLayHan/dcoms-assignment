package org.bhel.hrm.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainClient extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        // Loads the login view as the first screen.
//        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("view/LoginView.fxml")));
//        primaryStage.setTitle("BHEL HRM System – Login");
//        primaryStage.setScene(new Scene(root));
//        primaryStage.show();

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("view/DashboardView.fxml")));
        primaryStage.setTitle("BHEL – Human Resource Management");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
