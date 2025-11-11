package org.bhel.hrm.client.controllers;

import javafx.beans.Observable;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.bhel.hrm.client.MainClient;
import org.bhel.hrm.client.constants.FXMLPaths;
import org.bhel.hrm.client.services.ServiceManager;
import org.bhel.hrm.client.utils.DialogManager;
import org.bhel.hrm.common.dtos.EmployeeDTO;
import org.bhel.hrm.common.services.HRMService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;

/**
 * Controller for the Employee Management view.
 * Handles displaying, searching, adding, editing, and deleting employees.
 */
public class EmployeeManagementController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeManagementController.class);

    @FXML private TableView<EmployeeDTO> employeeTable;
    @FXML private TableColumn<EmployeeDTO, Integer> idColumn;
    @FXML private TableColumn<EmployeeDTO, String> firstNameColumn;
    @FXML private TableColumn<EmployeeDTO, String> lastNameColumn;
    @FXML private TableColumn<EmployeeDTO, String> icPassportColumn;

    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button clearSearchButton;
    @FXML private Button addNewButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button refreshButton;

    private HRMService hrmService;
    private ServiceManager serviceManager;
    private ExecutorService executorService;
    private ObservableList<EmployeeDTO> allEmployees;
    private ObservableList<EmployeeDTO> filteredEmployees;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Employee Management Controller initialized.");

        // Get dependencies from parent controller
        MainController mainController = getMainController();
        if (mainController != null) {
            this.serviceManager = mainController.getServiceManager();
            this.executorService = mainController.getExecutorService();

            if (serviceManager != null)
                this.hrmService = serviceManager.getHrmService();
        }

        if (hrmService == null) {
            logger.error("HRMService is null - cannot initialize employee management");
            DialogManager.showErrorDialog(
                "Connection Error",
                "Could not connect to the server. Please check your connection."
            );
            return;
        }

        initializeTableColumns();
        setupTableSelectionListener();
        setupSearchListener();
        loadEmployees();

        editButton.disableProperty().bind(
            employeeTable.getSelectionModel().selectedItemProperty().isNull());
        deleteButton.disableProperty().bind(
            employeeTable.getSelectionModel().selectedItemProperty().isNull());
    }

    /**
     * Gets the MainController from the scene graph.
     */
    private MainController getMainController() {
        try {
            if (
                employeeTable.getScene() != null &&
                employeeTable.getScene().getRoot() != null
            ) {
                return (MainController) employeeTable.getScene()
                    .getRoot().getProperties().get("mainController");
            }
        } catch (Exception e) {
            logger.error("Failed to get MainController.", e);
        }

        return null;
    }

    private void connectToRmiService() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            this.hrmService = (HRMService) registry.lookup(HRMService.SERVICE_NAME);
        } catch (Exception e) {
            logger.error("Client Error: Could not connect to the RMI service.", e);
            DialogManager.showErrorDialog(
                "Connection Error",
                "Could not connect to the server. Please ensure the server is running."
            );
        }
    }

    /**
     * Initializes the table columns with cell value factories.
     */
    private void initializeTableColumns() {
        idColumn.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().id()).asObject());
        firstNameColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().firstName()));
        lastNameColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().lastName()));
        icPassportColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().icPassport()));
    }

    /**
     * Sets up the table selection listener to enable/disable action buttons.
     */
    private void setupTableSelectionListener() {
        employeeTable.getSelectionModel().selectedItemProperty().addListener(
            (observable,
             oldValue,
             newValue
            ) -> {
                boolean hasSelection = newValue != null;
                editButton.setDisable(!hasSelection);
                deleteButton.setDisable(!hasSelection);
            }
        );
    }

    /**
     * Sets up the search field listener for real-time filtering
     */
    private void setupSearchListener() {
        searchField.textProperty().addListener(
            (
            observable,
            oldValue,
            newValue
            ) -> {
            filterEmployees(newValue);
        });
    }

    /**
     * Loads all employees from the server asynchronously.
     */
    private void loadEmployees() {
        if (hrmService == null) {
            logger.warn("Cannot load employees - HRMService is null");

            DialogManager.showErrorDialog(
                "Client Error",
                "Could not connect to the server."
            );
            return;
        }

        // Show loading indicator
        employeeTable.setPlaceholder(new Label("Loading employee..."));

        Task<List<EmployeeDTO>> employeeManagementTask = new Task<>() {
            @Override
            protected List<EmployeeDTO> call() throws Exception {
                return hrmService.getAllEmployees();
            }
        };

        employeeManagementTask.setOnSucceeded(event -> {
            logger.info("Successfully fetched employee data.");

            employeeTable.setItems(
                FXCollections.observableArrayList(employeeManagementTask.getValue()));
        });

        employeeManagementTask.setOnFailed(event -> {
            logger.error("Failed to fetch employees", employeeManagementTask.getException());

            DialogManager.showErrorDialog(
                "Load Error",
                "Could not fetch employee data."
            );
        });

         mainClient.getExecutorService().submit(employeeManagementTask);

    }

    @FXML
    private void handleAddNewEmployee() {
        showEmployeeFormDialog(null);
    }

    @FXML
    private void handleEditSelectedEmployee() {
        EmployeeDTO selectedEmployee =
            employeeTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) {
            DialogManager.showWarningDialog(
                "No selection",
                "Please select an employee from the table to edit."
            );
            return;
        }

        showEmployeeFormDialog(selectedEmployee);
    }

    private void showEmployeeFormDialog(EmployeeDTO employee) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource(FXMLPaths.Dialogs.EMPLOYEE_FORM));
            Stage dialogStage = new Stage();

            dialogStage.setTitle(employee == null ? "Add Employee" : "Edit Employee");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(loader.load()));

            EmployeeFormController controller = loader.getController();
            if (controller == null) {
                DialogManager.showErrorDialog(
                    "Initialization Error",
                    "Form controller could not be initialized."
                );
                return;
            }

            controller.setDialogStage(dialogStage);
            controller.setHrmService(this.hrmService);

            if (employee != null)
                controller.setEmployeeToEdit(employee);

            dialogStage.showAndWait();

            if (controller.isSaved())
                loadEmployees();
        } catch (IOException e) {
            logger.error("Failed to load employee form dialog", e);
            DialogManager.showErrorDialog(
                "UI Error",
                "Could not open the employee form. Please try again."
            );
        }
    }

    @FXML
    private void handleDeleteSelectedEmployee() {
        DialogManager.showWarningDialog(
            "Not Implemented",
            "Delete functionality not yet implemented"
        );
    }
}
