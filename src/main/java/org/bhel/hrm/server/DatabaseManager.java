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

    private final Configuration config;
    private final ThreadLocal<Connection> transactionConnection = new ThreadLocal<>();

    public DatabaseManager(Configuration config) {
        this.config = config;
        initializeDatabase();
    }

    /**
     * Gets a connection. If a transaction is active on the current thread,
     * it returns the transaction's connection. Otherwise, it creates a new one.
     */
    public Connection getConnection() throws SQLException {
        Connection conn = transactionConnection.get();
        if (conn != null)
            return conn; // Returns an existing transaction connection

        // Returns a new connection for a single, non-transactional operation
        return DriverManager.getConnection(config.getDbUrl(), config.getDbUser(), config.getDbPassword());
    }

    public void beginTransaction() throws SQLException {
        if (transactionConnection.get() != null)
            throw new SQLException("Transaction is already active on this thread.");

        Connection conn = DriverManager.getConnection(config.getDbUrl(), config.getDbUser(), config.getDbPassword());
        conn.setAutoCommit(false);

        transactionConnection.set(conn);
        logger.debug("Transaction started for Thread [{}]", Thread.currentThread().getName());
    }

    public void commitTransaction() throws SQLException {
        Connection conn = transactionConnection.get();

        if (conn != null) {
            try {
                conn.commit();
                logger.debug("Transaction commited for Thread [{}]", Thread.currentThread().getName());
            } finally {
                closeTransactionConnection();
            }
        }
    }

    public void rollbackTransaction() {
        Connection conn = transactionConnection.get();

        if (conn != null) {
            try {
                conn.rollback();
                logger.warn("Transaction rolled back for Thread [{}]", Thread.currentThread().getName());
            } catch (SQLException e) {
                logger.error("Error during transaction rollback.", e);
            } finally {
                closeTransactionConnection();
            }
        }
    }

    private void closeTransactionConnection() {
        Connection conn = transactionConnection.get();

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error("Error closing transaction connection.", e);
            } finally {
                transactionConnection.remove(); // Cleans up the ThreadLocal
            }
        }
    }

    private void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            String sql = """
                CREATE TABLE IF NOT EXISTS employees (
                    id INT AUTO_INCREMENT PRIMARY KEY,
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
