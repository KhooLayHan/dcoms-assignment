package org.bhel.hrm.common.services;

import org.bhel.hrm.common.models.Employee;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Service extends Remote {
    // Must be a unique name to reference the service from the RMI registry.
    String SERVICE_NAME = "HRMService";

    // TODO: Add any additional remote methods you need here.

    String registerEmployee(String firstName, String lastName, String icPassport) throws RemoteException;
    Employee getEmployeeDetails(String employeeId) throws RemoteException;

    boolean authenticateUser(String username, String password) throws RemoteException;
}
