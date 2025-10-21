package org.bhel.hrm.client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.bhel.hrm.client.utils.ViewManager;
import org.bhel.hrm.common.dtos.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The main controller for the application's primary view (MainView.fxml).
 * It manages the main layout, navigation, and content swapping.
 */
public class MainController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @FXML private VBox navigationVBox;
    @FXML private StackPane contentArea;
    @FXML private Label loggedInUserLabel;

    // Set up after a successful login screen.
    private UserDTO currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.currentUser = new UserDTO(1, "hr_admin", UserDTO.Role.HR_STAFF);

        loggedInUserLabel.setText(currentUser.username());
        buildNavigationMenu();

        loadDashboardView(); // Loads the initial dashboard view
    }

    private void buildNavigationMenu() {
        navigationVBox.getChildren().clear(); // Clear any existing buttons

        // Always add a Dashboard button
        addNavigationBtn("Dashboard", this::loadDashboardView);

        // Role-based navigation
        if (currentUser.role() == UserDTO.Role.HR_STAFF) {
            addNavigationBtn("Employees",
                () -> ViewManager.loadView(contentArea, "/org/bhel/hrm/client/view/HRDashboardView.fxml"));

            addNavigationBtn("Recruitment", () -> logger.info("Loading recruitment view..."));
            addNavigationBtn("Training Admin", () -> logger.info("Loading training admin view..."));
        }

        if (currentUser.role() == UserDTO.Role.EMPLOYEE || currentUser.role() == UserDTO.Role.HR_STAFF) {
            addNavigationBtn("My Profile", () -> logger.info("Loading profile view..."));
            addNavigationBtn("Leave Applications", () -> logger.info("Loading leave applications view..."));
            addNavigationBtn("Training Catalog", () -> logger.info("Loading training catalog view..."));
        }
    }

    /** Helper method to create and add a styled navigation button. */
    private void addNavigationBtn(String text, Runnable action) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.getStyleClass().add("nav-button"); // Adds a CSS class for styling
        button.setOnAction(e -> action.run());
        navigationVBox.getChildren().add(button);
    }

    private void loadDashboardView() {
        // Uses the ViewManager to load the appropriate dashboard
        if (currentUser.role() == UserDTO.Role.HR_STAFF) {
            ViewManager.loadView(contentArea, "/org/bhel/hrm/client/view/HRDashboardView.fxml");
        } else {
            // Placeholder to load other employee-specific Dashboard views. For instance:
            // ViewManager.loadView(contentArea, "/org/bhel/hrm/client/view/EmployeeDashboardView.fxml");
        }
    }

    @FXML
    private void handleLogout() {
        logger.info("User logged out.");
        // TODO: Add logic to close this window and show the login screen.
    }
}
