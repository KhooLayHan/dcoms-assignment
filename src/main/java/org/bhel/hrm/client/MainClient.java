package org.bhel.hrm.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.bhel.hrm.client.controllers.LoginController;
import org.bhel.hrm.client.controllers.MainController;
import org.bhel.hrm.common.dtos.UserDTO;

import java.io.IOException;

public class MainClient extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        showLoginView();
    }

    /**
     * Loads and displays the LoginView.
     */
    public void showLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/bhel/hrm/client/view/LoginView.fxml"));
            Parent root = loader.load();

            // Give the LoginController a reference back to this MainClient instance.
            LoginController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.setTitle("BHEL Human Resource Management – Login");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called by the LoginController upon successful authentication.
     * Switches the scene to the main application view.
     *
     * @param user The authenticated UserDTO
     */
    public void showMainView(UserDTO user) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/bhel/hrm/client/view/MainView.fxml"));
            Parent root = loader.load();

            // Gets the MainController and pass the authenticated user data to it.
            MainController controller = loader.getController();
            controller.initData(user);

            primaryStage.setTitle("BHEL – Human Resource Management");
            primaryStage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
