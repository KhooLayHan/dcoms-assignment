package org.bhel.hrm.server;

import org.bhel.hrm.common.services.Service;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerLauncher {
    private static final Logger logger = LoggerFactory.getLogger(ServerLauncher.class);

    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();

        logger.info("HRM Server is starting up...");

        try {
            Server server = new Server();

            Registry registry = LocateRegistry.createRegistry(1099);

            registry.rebind(Service.SERVICE_NAME, server);

            logger.info("Server is running and waiting for client connections...");
       } catch (Exception e) {
            logger.error("Server exception: {}", e.toString());
            logger.error("A fatal error occurred during startup {}", e.getMessage());
        }
    }
}
