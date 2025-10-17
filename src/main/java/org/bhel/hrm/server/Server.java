package org.bhel.hrm.server;

import org.bhel.hrm.common.models.Employee;
import org.bhel.hrm.common.services.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Server extends UnicastRemoteObject implements Service {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    protected Server() throws RemoteException {
        super();
    }

    @Override
    public String registerEmployee(String firstName, String lastName, String icPassport) throws RemoteException {
        logger.info("Client requested to register employees: {}", firstName);

        // TODO: Will need to implement calling the DatabaseManager when saving the employee.
        return "";
    }

    @Override
    public Employee getEmployeeDetails(String employeeId) throws RemoteException {
        return null;
    }

    @Override
    public boolean authenticateUser(String username, String password) throws RemoteException {
        return false;
    }
}
