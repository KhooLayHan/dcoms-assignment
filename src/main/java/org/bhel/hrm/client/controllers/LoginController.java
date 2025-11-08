package org.bhel.hrm.client.controllers;

import com.sun.tools.javac.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.bhel.hrm.client.MainClient;
import org.bhel.hrm.client.utils.DialogManager;
import org.bhel.hrm.common.dtos.UserDTO;
import org.bhel.hrm.common.exceptions.AuthenticationException;
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
    @FXML private TextField passwordField;
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

    public LoginController() {
        // Connects to the RMI service when the controller is created.
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            this.hrmService = (HRMService) registry.lookup(HRMService.SERVICE_NAME);
        } catch (Exception e) {
            logger.error("Client exception: Could not connect to the RMI service. ", e);
        }
    }

    private void connectToRMIService() {
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

        try {
            // Calls the remote authentication method
            UserDTO authenticatedUser = hrmService.authenticateUser(username, password);

            if (authenticatedUser != null) {
                logger.info("Login successful for user: {}", username);

                // On success, tell the main application to switch to the main view
                mainClient.showMainView(authenticatedUser);
            } else {
                // This case should not be hit if the server throws exceptions correctly, but is a good fallback.
                errorLabel.setText("Invalid username or password.");
            }
        } catch (HRMException e) {
            logger.warn("Authentication failed for user '{}': {}", username, e.getMessage());
            errorLabel.setText("Invalid username or password.");
        } catch (RemoteException e) {
            logger.error("RMI error during authentication.", e);
            DialogManager.showErrorDialog(
                "Server Error", "An error occurred while communicating with the server. Please try again.");
        }
    }
}
