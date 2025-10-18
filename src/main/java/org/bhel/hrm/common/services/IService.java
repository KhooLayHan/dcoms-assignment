package org.bhel.hrm.common.services;

import org.bhel.hrm.common.dtos.EmployeeDTO;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IService extends Remote {
    // Must be a unique name to reference the service from the RMI registry.
    String SERVICE_NAME = "HRMService";

    // TODO: Add any additional remote methods you need here.

    boolean isUserAuthenticated(String username, String password) throws RemoteException;
    List<EmployeeDTO> getAllEmployees() throws RemoteException;
    void registerEmployee(EmployeeDTO employeeDTO) throws RemoteException;
}
