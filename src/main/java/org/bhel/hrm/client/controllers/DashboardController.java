package org.bhel.hrm.client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import org.bhel.hrm.common.dtos.EmployeeDTO;
import org.bhel.hrm.common.services.IService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @FXML
    private TableView<EmployeeDTO> employeeDTOTableView;

    private IService service;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("HR Dashboard Controller initialized.");

        connectToRmiService();

        // TODO: Set up table columns and link them to EmployeeDTO properties.
        loadEmployeeData();
    }

    private void connectToRmiService() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            this.service = (IService) registry.lookup(IService.SERVICE_NAME);
        } catch (Exception e) {
            showErrorDialog("Connection Error", "Could not connect to the server.");
            logger.error("Error: {%s}", e);
        }
    }

    private void loadEmployeeData() {
        // TODO: Populate the table view with data from the server.
    }

    @FXML
    private void handleAddEmployeeAction() {
        // TODO: Opens a new dialog window for adding an employee.
        logger.info("Added Employee button clicked!");
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
