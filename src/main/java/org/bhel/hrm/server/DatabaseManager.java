package org.bhel.hrm.server;

import org.bhel.hrm.server.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    private final Configuration configuration;

    public DatabaseManager(Configuration config) {
        this.configuration = config;
        initializeDatabase();
    }


    public Connection getConnection() throws SQLException {
        // jdbc:mysql://localhost:3306/hrm_db?useSSL=false&serverTimezone=UTC
        final String DATABASE_URL = String.format("{%s}:{%s}://{%s}:{%s}/{%s}?useSSL=false&serverTimezone=UTC",
            configuration.getDbDriver(),
            configuration.getDbConnection(),
            configuration.getDbHost(),
            configuration.getDbPort(),
            configuration.getDbName()
        );

        final String DB_USER = configuration.getDbUser();
        final String DB_PASSWORD = configuration.getDbPassword();

        return DriverManager.getConnection(DATABASE_URL, DB_USER, DB_PASSWORD);
    }

    private void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            String sql = """
                CREATE TABLE IF NOT EXISTS employees (
                    id INT AUTO_INCREMENT PRIMARY_KEY,
                    first_name VARCHAR(255) NOT NULL,
                    last_name VARCHAR(255) NOT NULL,
                    ic_passport VARCHAR(255) UNIQUE NOT NULL
                )
            """;

            stmt.execute(sql);
            logger.info("Database connection successful. 'employees' table is ready.");
        } catch (SQLException e) {
            logger.error("FATAL: Database initialization failed!");
            logger.error(e.toString());
        }
    }
}
