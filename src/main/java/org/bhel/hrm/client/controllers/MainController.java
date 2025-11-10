package org.bhel.hrm.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.bhel.hrm.client.MainClient;
import org.bhel.hrm.client.constants.FXMLPaths;
import org.bhel.hrm.client.services.ServiceManager;
import org.bhel.hrm.client.utils.DialogManager;
import org.bhel.hrm.client.utils.ViewManager;
import org.bhel.hrm.common.dtos.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * The main controller for the application's primary view (MainView.fxml).
 * It manages the main layout, navigation, and content swapping.
 */
public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @FXML private VBox navigationVBox;
    @FXML private StackPane contentArea;
    @FXML private Label loggedInUserLabel;
    @FXML private BorderPane mainPane;

    private UserDTO currentUser;
    private ServiceManager serviceManager;
    private ExecutorService executorService;
    private Button activeButton = null;
    private MainClient mainClient;

    /**
     * This method is called by the MainClient after the FXML is loaded
     * to pass in the authenticated user.
     *
     * @param user The authenticated user from the login screen
     */
    public void initData(UserDTO user) {
        if (user == null || user.role() == null) {
            logger.error("Invalid user data received.");
            DialogManager.showErrorDialog(
                "Authentication Error",
                "Invalid user session. Please login again."
            );

            return;
        }

        this.currentUser = user;
        loggedInUserLabel.setText(currentUser.username());
        refreshNavigation();
    }

    public void refreshNavigation() {
        buildNavigationMenu();
        loadDashboardView();
    }

    private void buildNavigationMenu() {
        navigationVBox.getChildren().clear(); // Clear any existing buttons

        // Always add a Dashboard button
        addNavigationBtn("Dashboard", this::loadDashboardView);

        // Role-based navigation
        if (currentUser.role() == UserDTO.Role.HR_STAFF) {
            addNavigationBtn("Employees",
                () -> ViewManager.loadView(contentArea, "/org/bhel/hrm/client/view/EmployeeManagementView.fxml"));

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
        button.getStyleClass().add("nav-button");
        button.setOnAction(e -> {
            if (activeButton != null)
                activeButton.getStyleClass().remove("nav-button-active");

            button.getStyleClass().add("nav-button-active");
            activeButton = button;

            action.run();
        });
        navigationVBox.getChildren().add(button);
    }

    private void loadDashboardView() {
        // Uses the ViewManager to load the appropriate dashboard
        if (currentUser.role() == UserDTO.Role.HR_STAFF) {
            ViewManager.loadView(contentArea, FXMLPaths.EMPLOYEE_MANAGEMENT);
        } else {
            // Placeholder to load other employee-specific Dashboard views. For instance:
            // ViewManager.loadView(contentArea, "/org/bhel/hrm/client/view/EmployeeDashboardView.fxml");
        }
    }

    @FXML
    private void handleLogout() {
        logger.info("User {} logged out.", currentUser.username());

        // Optional: Confirm logout
        if (!DialogManager.showConfirmationDialog(
            "Logout",
            "Are you sure you want to logout?"
        ))
            return;

        this.currentUser = null;
        mainClient.showLoginView();
    }

    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    public void setServiceManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }
}
