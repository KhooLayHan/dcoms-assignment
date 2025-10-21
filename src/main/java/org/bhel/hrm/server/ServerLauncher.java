package org.bhel.hrm.server;

import org.bhel.hrm.common.services.HRMService;
import org.bhel.hrm.server.config.Configuration;
import org.bhel.hrm.server.daos.EmployeeDAO;
import org.bhel.hrm.server.daos.UserDAO;
import org.bhel.hrm.server.daos.impls.EmployeeDAOImpl;
import org.bhel.hrm.server.daos.impls.UserDAOImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerLauncher {
    private static final Logger logger = LoggerFactory.getLogger(ServerLauncher.class);

    public static void main(String[] args) {
        try {
            logger.info("HRM Server is starting up...");

            // 1. Load the configuration
            Configuration configuration = new Configuration();
            logger.info("Application is starting in [{}] environment.", configuration.getAppEnvironment());

            // 4. Setup and start the RMI server
            HRMServer server = getHRMServer(configuration);
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind(HRMService.SERVICE_NAME, server);

            logger.info("Server is running and waiting for client connections...");
       } catch (Exception e) {
            logger.error("Server exception: {}", e.toString());
            logger.error("A fatal error occurred during startup {}", e.getMessage());
        }
    }

    private static HRMServer getHRMServer(Configuration configuration) throws RemoteException {
        DatabaseManager databaseManager = new DatabaseManager(configuration);

        // 2. Setup database and DAOs
        EmployeeDAO employeeDAO = new EmployeeDAOImpl(databaseManager);
        UserDAO userDAO = new UserDAOImpl(databaseManager);

        // 3. Conditionally seed the database
        if ("development".equalsIgnoreCase(configuration.getAppEnvironment())) {
            DatabaseSeeder seeder = new DatabaseSeeder(databaseManager, userDAO, employeeDAO);
            seeder.seedIfEmpty();
        }

        return new HRMServer(databaseManager, employeeDAO, userDAO);
    }
}
