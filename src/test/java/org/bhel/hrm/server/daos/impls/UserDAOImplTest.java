package org.bhel.hrm.server.daos.impls;

import org.bhel.hrm.server.DatabaseManager;
import org.bhel.hrm.server.config.Configuration;
import org.bhel.hrm.server.config.ConfigurationException;
import org.bhel.hrm.server.daos.UserDAO;
import org.bhel.hrm.server.domain.User;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Allows @BeforeAll and @AfterAll to be non-static
class UserDAOImplTest {

    private DatabaseManager dbManager;
    private UserDAO userDAO;
    private Connection transactionConnection; // Connection is managed per test

    @BeforeAll
    void setupClass() throws ConfigurationException {
        // This setup runs once. It initializes the DAO we are testing
        Configuration config = new Configuration();
        dbManager = new DatabaseManager(config);
        userDAO = new UserDAOImpl(dbManager);
    }

    @BeforeEach
    void setUp() throws SQLException {
        // Before each test, get a fresh connection and start a transaction
        // Thus ensuring each test is isolated
        transactionConnection = dbManager.getConnection();
        transactionConnection.setAutoCommit(false);
    }

    @AfterEach
    void tearDown() throws SQLException {
        // After each test, roll back any changes made
        // Thus leaving the database in a clean state for the next test
        if (transactionConnection == null)
            setUp();

        transactionConnection.rollback();
        transactionConnection.close();
    }

    @Test
    void findById_() {
    }

    @Test
    void findAll() {
    }

    @Test
    void save() {
    }

    @Test
    void setSaveParameters() {
    }

    @Test
    void deleteById() {
    }

    @Test
    void count() {
    }

    @Test
    void findByUsername() {
    }
}
