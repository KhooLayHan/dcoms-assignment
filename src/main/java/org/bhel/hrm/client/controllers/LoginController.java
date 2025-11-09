package org.bhel.hrm.client.controllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.bhel.hrm.client.MainClient;
import org.bhel.hrm.client.utils.DialogManager;
import org.bhel.hrm.common.dtos.UserDTO;
import org.bhel.hrm.common.exceptions.HRMException;
import org.bhel.hrm.common.services.HRMService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private HRMService hrmService;
    private MainClient mainClient; // References the main application class

    @FXML
    public void initialize() {
        // Clears any previous error messages
        errorLabel.setText("");
        connectToRMIService();
    }

    public void setMainApp(MainClient client) {
        this.mainClient = client;
    }

    private void connectToRMIService() {
        // Connects to the RMI service when the controller is created.
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            this.hrmService = (HRMService) registry.lookup(HRMService.SERVICE_NAME);

            logger.info("Successfully connected to the RMI service.");
        } catch (Exception e) {
            logger.error("Client Error: Could not connect to the RMI service.", e);
            DialogManager.showErrorDialog(
                "Connection Error",
                "Could not connect to the server. Please ensure the server is running."
            );
        }
    }

    @FXML
    protected void handleLoginButtonAction() {
        if (hrmService == null) {
            DialogManager.showErrorDialog(
                "Connection Error",
                "Not connected to the server. Please restart the application."
            );

            return;
        }

        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isBlank() || password.isBlank()) {
            errorLabel.setText("Username and password cannot be empty.");
            return;
        }

        errorLabel.setText("Authenticating...");
        usernameField.setDisable(true);
        passwordField.setDisable(true);

        Task<UserDTO> loginTask = new Task<UserDTO>() {
            @Override
            protected UserDTO call() throws Exception {
                return hrmService.authenticateUser(username, password);
            }
        };

        loginTask.setOnSucceeded(event -> {
            usernameField.setDisable(false);
            passwordField.setDisable(false);

            UserDTO authenticatedUser = loginTask.getValue();
            if (authenticatedUser != null) {
                logger.info("Login successful for user: {}", username);

                // On success, tell the main application to switch to the main view
                mainClient.showMainView(authenticatedUser);
            } else {
                errorLabel.setText("Invalid username or password.");
            }
        });

        loginTask.setOnFailed(event -> {
            usernameField.setDisable(false);
            passwordField.setDisable(false);

            Throwable error = loginTask.getException();

            switch (error) {
                case HRMException hrmException -> {
                    logger.warn("Authentication failed for user '{}': {}",
                        username, hrmException.getMessage());
                    errorLabel.setText("Invalid username or password.");
                }
                case RemoteException remoteException -> {
                    logger.error("RMI error during authentication.", remoteException);
                    DialogManager.showErrorDialog(
                        "Server Error",
                        "An error occurred while communicating with the server. Please try again."
                    );
                }
                default -> {
                    logger.error("Unexpected error during login.", error);
                    DialogManager.showErrorDialog(
                        "Login Error",
                        "Unexpected error during login. Please try again."
                    );
                }
            }
        });

        mainClient.getExecutorService().submit(loginTask);
    }
}
