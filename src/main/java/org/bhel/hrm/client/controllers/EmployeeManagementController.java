package org.bhel.hrm.client.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
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

public class EmployeeManagementController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeManagementController.class);

    @FXML private TableView<EmployeeDTO> employeeTable;
    @FXML private TableColumn<EmployeeDTO, Integer> idColumn;
    @FXML private TableColumn<EmployeeDTO, String> firstNameColumn;
    @FXML private TableColumn<EmployeeDTO, String> lastNameColumn;
    @FXML private TableColumn<EmployeeDTO, String> icPassportColumn;


    private HRMService hrmService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("HR Dashboard Controller initialized.");

        connectToRmiService();
        initializeTableColumns();
        loadEmployees();
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

    private void initializeTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        icPassportColumn.setCellValueFactory(new PropertyValueFactory<>("icPassport"));
    }

    private void loadEmployees() {
        if (hrmService == null)
            return;

        try {
            logger.debug("Fetching all employees...");

            List<EmployeeDTO> employees = hrmService.getAllEmployees();
            employeeTable.setItems(FXCollections.observableArrayList(employees));
        } catch (Exception e) {
            logger.error("Failed to fetch employee data.", e);
            DialogManager.showErrorDialog(
                "Load Data Error",
                "Could not fetch employee data from the server."
            );
        }
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
                getClass().getResource("/org/bhel/hrm/client/view/dialogs/EmployeeFormView.fxml"));
            Stage dialogStage = new Stage();

            dialogStage.setTitle(employee == null ? "Add Employee" : "Edit Employee");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(loader.load()));

            EmployeeFormController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setHrmService(this.hrmService);

            if (employee != null)
                controller.setEmployeeToEdit(employee);

            dialogStage.showAndWait();

            if (controller.isSaved())
                loadEmployees();
        } catch (IOException e) {
            e.printStackTrace();
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
