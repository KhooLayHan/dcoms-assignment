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

        @SuppressWarnings("java:S2095")
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
                logger.debug("Transaction committed for Thread [{}]", Thread.currentThread().getName());
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

    public void releaseConnection(Connection conn) {
        Connection tx = transactionConnection.get();

        if (conn != null && conn != tx) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error("Error closing connection.", e);
            }
        }
    }

    public boolean isTransactionActive() {
        return transactionConnection.get() != null;
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
            createHRMTables(stmt);
            logger.info("Database connection successful. Database schema initialized successfully.");
        } catch (SQLException e) {
            logger.error("FATAL: Database schema initialization failed!");
            logger.error(e.toString());
        }
    }

    private void createHRMTables(Statement stmt) throws SQLException {
        // 1. Users and UserRoles table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS user_roles (
                id TINYINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(20) NOT NULL UNIQUE,
                sort_order TINYINT NOT NULL DEFAULT 0
            )
        """);

        stmt.execute("""
            INSERT IGNORE INTO user_roles (id, name, sort_order) VALUES
            (1, 'hr_staff', 1),
            (2, 'employee', 2)
        """);

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS users (
                id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(255) NOT NULL UNIQUE,
                password_hash VARCHAR(255) NOT NULL,
                role_id TINYINT UNSIGNED NOT NULL,

                CONSTRAINT fk_users_role_id
                    FOREIGN KEY (role_id) REFERENCES user_roles(id)
                    ON UPDATE CASCADE
                    ON DELETE RESTRICT
            );
        """);

        // 2. Employees Table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS employees (
                id INT AUTO_INCREMENT PRIMARY KEY,
                user_id INT NOT NULL,
                first_name VARCHAR(255) NOT NULL,
                last_name VARCHAR(255) NOT NULL,
                ic_passport VARCHAR(255) NOT NULL UNIQUE,

                CONSTRAINT fk_employees_employee_id
                    FOREIGN KEY (user_id) REFERENCES users(id)
                    ON UPDATE CASCADE
                    ON DELETE RESTRICT
            )
        """);

        // 3. LeaveApplications, LeaveApplicationTypes, and LeaveApplicationStatuses Table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS leave_application_types (
                id TINYINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(20) NOT NULL UNIQUE,
                sort_order TINYINT NOT NULL DEFAULT 0
            )
        """);

        stmt.execute("""
            INSERT IGNORE INTO leave_application_types (id, name, sort_order) VALUES
            (1, 'annual', 1),
            (2, 'sick', 2),
            (3, 'unpaid', 3)
        """);

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS leave_application_statuses (
                id TINYINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(20) NOT NULL UNIQUE,
                sort_order TINYINT NOT NULL DEFAULT 0
            )
        """);

        stmt.execute("""
            INSERT IGNORE INTO leave_application_statuses (id, name, sort_order) VALUES
            (1, 'pending', 1),
            (2, 'approved', 2),
            (3, 'rejected', 3)
        """);

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS leave_applications (
                id INT AUTO_INCREMENT PRIMARY KEY,
                employee_id INT NOT NULL,
                start_date_time DATETIME NOT NULL,
                end_date_time DATETIME NOT NULL,
                type_id TINYINT UNSIGNED NOT NULL,
                status_id TINYINT UNSIGNED NOT NULL,
                reason TEXT,
        
                CONSTRAINT fk_leave_applications_employee_id
                    FOREIGN KEY (employee_id) REFERENCES employees(id)
                    ON UPDATE CASCADE
                    ON DELETE CASCADE,

                CONSTRAINT fk_leave_applications_type_id
                    FOREIGN KEY (type_id) REFERENCES leave_application_types(id)
                    ON UPDATE CASCADE
                    ON DELETE RESTRICT,

                CONSTRAINT fk_leave_applications_status_id
                    FOREIGN KEY (status_id) REFERENCES leave_application_statuses(id)
                    ON UPDATE CASCADE
                    ON DELETE RESTRICT
            )
        """);

        // 4. TrainingCourses Table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS training_courses (
                id INT AUTO_INCREMENT PRIMARY KEY,
                title VARCHAR(255) NOT NULL,
                description TEXT,
                duration_in_hours INT,
                department VARCHAR(255)
            )
        """);

        // 5. BenefitPlans Table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS benefit_plans (
                id INT AUTO_INCREMENT PRIMARY KEY,
                plan_name VARCHAR(255) NOT NULL,
                provider VARCHAR(255),
                description TEXT,
                cost_per_month DECIMAL(10, 2)
            )
        """);

        // 6. JobOpenings, and JobOpeningStatuses Table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS job_opening_statuses (
                id TINYINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(20) NOT NULL UNIQUE,
                sort_order TINYINT NOT NULL DEFAULT 0
            )
        """);

        stmt.execute("""
            INSERT IGNORE INTO job_opening_statuses (id, name, sort_order) VALUES
            (1, 'open', 1),
            (2, 'closed', 2),
            (3, 'on_hold', 3)
        """);

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS job_openings (
                id INT AUTO_INCREMENT PRIMARY KEY,
                title VARCHAR(255) NOT NULL,
                description TEXT,
                department VARCHAR(255),
                status_id TINYINT UNSIGNED NOT NULL,

                CONSTRAINT fk_job_openings_status_id
                    FOREIGN KEY (status_id) REFERENCES job_opening_statuses(id)
                    ON UPDATE CASCADE
                    ON DELETE RESTRICT
            )
        """);

        // 7. Applicants, and ApplicantStatuses Table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS applicant_statuses (
                id TINYINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(20) NOT NULL UNIQUE,
                sort_order TINYINT NOT NULL DEFAULT 0
            )
        """);

        stmt.execute("""
            INSERT IGNORE INTO applicant_statuses (id, name, sort_order) VALUES
            (1, 'new', 1),
            (2, 'screening', 2),
            (3, 'interviewing', 3),
            (4, 'offered', 4),
            (5, 'hired', 5),
            (6, 'rejected', 6)
        """);

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS applicants (
                id INT AUTO_INCREMENT PRIMARY KEY,
                job_opening_id INT NOT NULL,
                full_name VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL,
                phone VARCHAR(50),
                status_id TINYINT UNSIGNED NOT NULL,

                CONSTRAINT fk_applicants_job_opening_id
                    FOREIGN KEY (job_opening_id) REFERENCES job_openings(id)
                    ON UPDATE CASCADE
                    ON DELETE CASCADE,

                CONSTRAINT fk_applicants_status_id
                    FOREIGN KEY (status_id) REFERENCES applicant_statuses(id)
                    ON UPDATE CASCADE
                    ON DELETE RESTRICT
            )
        """);
    }
}
