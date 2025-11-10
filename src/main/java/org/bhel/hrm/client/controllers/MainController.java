package org.bhel.hrm.client.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.bhel.hrm.client.MainClient;
import org.bhel.hrm.client.constants.FXMLPaths;
import org.bhel.hrm.client.services.ServiceManager;
import org.bhel.hrm.client.utils.DialogManager;
import org.bhel.hrm.client.utils.ViewManager;
import org.bhel.hrm.common.dtos.UserDTO;
import org.bhel.hrm.server.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.concurrent.ExecutorService;

/**
 * The main controller for the application's primary view (MainView.fxml).
 * It manages the main layout, navigation, and content swapping.
 */
public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    private static final Duration SESSION_TIMEOUT = Duration.minutes(30);

    @FXML private BorderPane mainPane;
    @FXML private VBox sidebar;
    @FXML private VBox navigationVBox;
    @FXML private StackPane contentArea;
    @FXML private Label loggedInUserLabel;
    @FXML private Label currentTimeLabel;
    @FXML private Label connectionStatusIcon;
    @FXML private Label connectionStatusLabel;
    @FXML private Label currentViewLabel;
    @FXML private HBox connectionStatusBox;
    @FXML private Button logoutButton;

    private UserDTO currentUser;
    private ServiceManager serviceManager;
    private ExecutorService executorService;
    private MainClient mainClient;
    private Button activeButton = null;

    private Timeline clockTimeline;
    private Timeline sessionTimer;
    private Timeline connectionCheckTimer;

    /**
     * JavaFX initialize method - called after FXML is loaded
     */
    @FXML
    public void initialize() {
        // Will be fully initialized when initData is called
        logger.debug("MainController initialized.");
    }

    public void setServiceManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void setMainClient(MainClient mainClient) {
        this.mainClient = mainClient;
    }

    /**
     * This method is called by the MainClient after the FXML is loaded
     * to pass in the authenticated user and initialize the view.
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

            handleLogout();
            return;
        }

        this.currentUser = user;
        logger.info("Initializing main view for user: {} with role: {}",
            user.username(), user.role());

        // Update UI with user information
        loggedInUserLabel.setText(currentUser.username());

        // Build navigation menu based on user role
        buildNavigationMenu();

        // Load initial dashboard view
        loadDashboardView();

        // Start background services
        startClock();
        startConnectionMonitoring();
        // startSessionTimer();

        // Setup keyboard shortcuts
        setupKeyboardShortcuts();

        // Store controller reference for child views
        mainPane.getProperties().put("mainController", this);
    }

    public void refreshNavigation() {
        buildNavigationMenu();
        loadDashboardView();
    }

    /**
     * Builds the navigation menu based on the current user's role.
     */
    private void buildNavigationMenu() {
        navigationVBox.getChildren().clear(); // Clear any existing buttons

        // Always add a Dashboard button
        addNavigationBtn("Dashboard", this::loadDashboardView);

        // Role-based navigation
        if (currentUser.role() == UserDTO.Role.HR_STAFF) {
            addNavigationBtn("Employees", this::loadEmployeeManagementView);
            addNavigationBtn("Recruitment", this::loadRecruitmentView);
            addNavigationBtn("Training Admin", this::loadTrainingAdminView);
        }

        if (
            currentUser.role() == UserDTO.Role.EMPLOYEE ||
            currentUser.role() == UserDTO.Role.HR_STAFF
        ) {
            addNavigationBtn("My Profile", this::loadProfileView);
            addNavigationBtn("Leave Applications", this::loadLeaveApplicationsView);
            addNavigationBtn("Training Catalog", this::loadTrainingCatalogView);
        }
    }

    /** Helper method to create and add a styled navigation button. */
    private void addNavigationBtn(String text, Runnable action) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.getStyleClass().add("nav-button");

        button.setOnAction(e -> {
            // Removes active state from previous button
            if (activeButton != null)
                activeButton.getStyleClass().remove("nav-button-active");

            // Sets active state on current button
            button.getStyleClass().add("nav-button-active");
            activeButton = button;

            // Update current view label
            currentViewLabel.setText(text);

            // Reset session timer on user activity
            // resetSessionTimer();

            // Execute the navigation action
            action.run();
        });

        navigationVBox.getChildren().add(button);

        // Sets first button as active by default
        if (activeButton == null) {
            button.getStyleClass().add("nav-button-active");
            activeButton = button;
        }
    }

    /**
     * Loads the dashboard view based on user role.
     */
    private void loadDashboardView() {
        currentViewLabel.setText("Dashboard");

        // Uses the ViewManager to load the appropriate dashboard
        if (currentUser.role() == UserDTO.Role.HR_STAFF) {
            ViewManager.loadView(contentArea, FXMLPaths.EMPLOYEE_MANAGEMENT);
        } else {
            // Placeholder to load other employee-specific Dashboard views. For instance:
            // ViewManager.loadView(contentArea, "/org/bhel/hrm/client/view/EmployeeDashboardView.fxml");
        }
    }

    /**
     * Loads the employee management view
     */
    private void loadEmployeeManagementView() {
        logger.info("Loading Employee Management View...");

        ViewManager.loadView(contentArea,
            FXMLPaths.EMPLOYEE_MANAGEMENT);
    }

    /**
     * Loads the recruitment view
     */
    private void loadRecruitmentView() {
        logger.info("Loading Recruitment View...");
    }

    /**
     * Loads the training admin view
     */
    private void loadTrainingAdminView() {
        logger.info("Loading Training Admin View...");
    }

    /**
     * Loads the profile view
     */
    private void loadProfileView() {
        logger.info("Loading Profile View...");

    }

    /**
     * Loads the leave applications view
     */
    private void loadLeaveApplicationsView() {
        logger.info("Loading Leave Applications View...");
    }

    /**
     * Loads the training catalog view
     */
    private void loadTrainingCatalogView() {
        logger.info("Loading Training Catalog View...");
    }

    /**
     * Starts the clock that updates the current time display.
     */
    private void startClock() {
        clockTimeline = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> {
                currentTimeLabel.setText(
                    LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                );
            })
        );
        clockTimeline.setCycleCount(Animation.INDEFINITE);
        clockTimeline.play();
    }

    /**
     * Monitors the connection status to the server.
     */
    private void startConnectionMonitoring() {
        updateConnectionStatus();

        connectionCheckTimer = new Timeline(
            new KeyFrame(Duration.seconds(30), e -> {
                updateConnectionStatus();
            })
        );
        connectionCheckTimer.setCycleCount(Animation.INDEFINITE);
        connectionCheckTimer.play();
    }

    /**
     * Updates the connection status indicator.
     */
    private void updateConnectionStatus() {
        boolean connected =
            serviceManager != null && serviceManager.isConnected();

        Platform.runLater(() -> {
            if (connected) {
                connectionStatusIcon.getStyleClass().removeAll("connection-status-disconnected");
                connectionStatusIcon.getStyleClass().add("connection-status-connected");
                connectionStatusLabel.setText("Connected");
            } else {
                connectionStatusIcon.getStyleClass().removeAll("connection-status-connected");
                connectionStatusIcon.getStyleClass().add("connection-status-disconnected");
                connectionStatusLabel.setText("Disconnected");

                logger.warn("Connection to server lost");
            }
        });
    }

    /**
     * Starts the session timeout timer
     */
    private void startSessionTimer() {
        sessionTimer = new Timeline(
            new KeyFrame(SESSION_TIMEOUT, e -> {
                logger.info("Session timeout for user: {}", currentUser.username());
                Platform.runLater(() -> {
                    DialogManager.showWarningDialog(
                        "Session Expired",
                        "Your session has expired due to inactivity. Please login again."
                    );
                    handleLogout();
                });
            })
        );
        sessionTimer.setCycleCount(1);
        sessionTimer.play();
    }

    /**
     * Resets the session timer (called on user activity)
     */
    private void resetSessionTimer() {
        if (sessionTimer != null) {
            sessionTimer.stop();
            sessionTimer.playFromStart();
        }
    }

    /**
     * Sets up keyboard shortcuts.
     */
    private void setupKeyboardShortcuts() {
        Platform.runLater(() -> {
            if (mainPane.getScene() != null) {
                mainPane.getScene().getAccelerators().put(
                    new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN),
                    this::handleLogout
                );
            }
        });
    }

    /**
     * Handles logout action.
     */
    @FXML
    private void handleLogout() {
        logger.info("User {} logged out.",
            currentUser != null
                ? currentUser.username()
                : "Unknown"
        );

        // Confirm logout
        if (!DialogManager.showConfirmationDialog(
            "Logout",
            "Are you sure you want to logout?"
        ))
            return;

        // Stop all timers
        if (clockTimeline != null)
            clockTimeline.stop();
        if (sessionTimer != null)
            sessionTimer.stop();
        if (connectionCheckTimer != null)
            connectionCheckTimer.stop();

        // Clears the current user data
        this.currentUser = null;

        // Return to log-in screen
        if (mainClient != null) {
            mainClient.showLoginView();
        } else {
            logger.error("MainClient reference is null, cannot return to login.");
        }
    }

    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public UserDTO getCurrentUser() {
        return currentUser;
    }
}
