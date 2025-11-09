package org.bhel.hrm.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.bhel.hrm.common.dtos.EmployeeDTO;
import org.bhel.hrm.common.dtos.NewEmployeeRegistrationDTO;
import org.bhel.hrm.common.dtos.UserDTO;
import org.bhel.hrm.common.exceptions.HRMException;
import org.bhel.hrm.common.services.HRMService;

import java.rmi.RemoteException;

public class EmployeeFormController {
    @FXML private Label titleLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<UserDTO.Role> roleComboBox;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField icPassportField;

    private HRMService hrmService;
    private Stage dialogStage;
    private EmployeeDTO employeeToEdit;
    private boolean isSaved = false;

    @FXML
    public void initialize() {
        roleComboBox.getItems().setAll(UserDTO.Role.values());
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setHrmService(HRMService hrmService) {
        this.hrmService = hrmService;
    }

    // Turns the dialog into "Edit" mode
    public void setEmployeeToEdit(EmployeeDTO employee) {
        this.employeeToEdit = employee;
        titleLabel.setText("Edit Employee");

        // Disable user creation fields in Edit mode
        usernameField.setDisable(true);
        passwordField.setDisable(true);
        roleComboBox.setDisable(true);

        // Populate fields with existing data
        firstNameField.setText(employee.firstName());
        lastNameField.setText(employee.lastName());
        icPassportField.setText(employee.icPassport());
    }

    public boolean isSaved() {
        return isSaved;
    }

    private void saveNewEmployee() throws RemoteException, HRMException {
        NewEmployeeRegistrationDTO registrationDTO = new NewEmployeeRegistrationDTO(
            usernameField.getText(),
            passwordField.getText(),
            roleComboBox.getValue(),
            firstNameField.getText(),
            lastNameField.getText(),
            icPassportField.getText()
        );

        hrmService.registerNewEmployee(registrationDTO);
        isSaved = true;
        dialogStage.close();
    }

    private void updateExistingEmployee() throws HRMException, RemoteException {
        EmployeeDTO updatedDTO = new EmployeeDTO(
            employeeToEdit.id(),
            employeeToEdit.userId(),
            firstNameField.getText(),
            lastNameField.getText(),
            icPassportField.getText()
        );

        hrmService.updateEmployeeProfile(updatedDTO);
        isSaved = true;
        dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}
