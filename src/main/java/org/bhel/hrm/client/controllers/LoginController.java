package org.bhel.hrm.client.controllers;

import org.bhel.hrm.common.services.IService;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML private TextField usernameField;
    @FXML private TextField passwordField;

    private IService service;

    public LoginController() {
        // Connects to the RMI service when the controller is created.
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            this.service = (IService) registry.lookup(IService.SERVICE_NAME);
        } catch (Exception e) {
            logger.error("Client exception: Could not connect to the RMI service. {%s}", e);
        }
    }

    @FXML
    protected void handleLoginButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // TODO: Validate input parameters locally.

        try {
            if (service.isUserAuthenticated(username, password)) {
                logger.info("Login successful!");

                // TODO: Closes the login window and opens the main dashboard.
            } else {
                logger.info("Login failed!");
                // TODO: Shows a "Login failed" error message to the user.
            }
        } catch (Exception e) {
            logger.error("Error during the authentication: {}", e.getMessage());

            // TODO: Shows a connection error dialog.
        }
    }
}
