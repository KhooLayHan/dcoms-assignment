package org.bhel.hrm.server;

import org.bhel.hrm.common.services.HRMService;
import org.bhel.hrm.server.config.Configuration;
import org.bhel.hrm.server.daos.EmployeeDAO;
import org.bhel.hrm.server.daos.UserDAO;
import org.bhel.hrm.server.daos.impls.EmployeeDAOImpl;

import org.bhel.hrm.server.daos.impls.UserDAOImpl;
import org.bhel.hrm.server.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerLauncher {
    private static final Logger logger = LoggerFactory.getLogger(ServerLauncher.class);

    public static void main(String[] args) {
        try {
            logger.info("HRM Server is starting up...");

            Configuration configuration = new Configuration();

            @SuppressWarnings("java:S2440")
            DatabaseManager databaseManager = new DatabaseManager(configuration);

            EmployeeDAO employeeDAO = new EmployeeDAOImpl(databaseManager);
            UserDAO userDAO = new UserDAOImpl(databaseManager);

            HRMServer server = new HRMServer(databaseManager, employeeDAO, userDAO);

            Registry registry = LocateRegistry.createRegistry(1099);

            registry.rebind(HRMService.SERVICE_NAME, server);

            logger.info("Server is running and waiting for client connections...");
       } catch (Exception e) {
            logger.error("Server exception: {}", e.toString());
            logger.error("A fatal error occurred during startup {}", e.getMessage());
        }
    }
}
