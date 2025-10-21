package org.bhel.hrm.server.daos.impls;

import org.bhel.hrm.common.dtos.UserDTO;
import org.bhel.hrm.server.DatabaseManager;
import org.bhel.hrm.server.daos.EmployeeDAO;
import org.bhel.hrm.server.domain.Employee;
import org.bhel.hrm.server.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeDAOImpl implements EmployeeDAO {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeDAOImpl.class);

    private final DatabaseManager dbManager;

    public EmployeeDAOImpl(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public Optional<Employee> findById(Integer integer) {
        return Optional.empty();
    }

    @Override
    public List<Employee> findAll() {
        return List.of();
    }

    @Override
    public void save(Employee employee) {
        String sql = (employee.getId() == 0)
                ? "INSERT INTO employees (user_id, first_name, last_name, ic_passport) VALUES (?, ?, ?, ?)"
                : "UPDATE employees SET user_id = ?, first_name = ?, last_name = ?, ic_passport = ? WHERE id = ?";

        Connection conn;
        try {
            // NOTE: try-with-resources will automatically call the `.close()` method for any
            // resource declared within it when the block is exited. Thus, the connection
            // will fail and crash immediately. Ensure that the connection lifecycle is handled
            // entirely by DatabaseManager by calling it outside the try-with-resources block.
            conn = dbManager.getConnection();

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, employee.getUserId());
                stmt.setString(2, employee.getFirstName());
                stmt.setString(3, employee.getLastName());
                stmt.setString(4, employee.getIcPassport());

                if (employee.getId() != 0)
                    stmt.setInt(5, employee.getId()); // Set ID for UPDATE clause

                stmt.executeUpdate();

                // If INSERT clause, get the generated ID
                if (employee.getId() == 0) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next())
                            employee.setId(generatedKeys.getInt(1)); // Sets the new ID back on the object
                    }
                }
            } catch (SQLException e) {
                logger.error("Error inserting new employee: {} {}", employee.getFirstName(), employee.getLastName(), e);
            } finally {
                // IMPORTANT: Only close the connection IF IT'S NOT PART OF A TRANSACTION.
                // Our DatabaseManager's transaction methods will handle closing.
                // For simplicity in this structure, we can assume the service layer will close it.
                // A more advanced implementation would have the DatabaseManager track this.
                // For now, the key is NOT to auto-close it here.
            }
        } catch (SQLException e) {
            logger.error("Error inserting new employee: {} {}", employee.getFirstName(), employee.getLastName(), e);
        }
    }

    @Override
    public void deleteById(Integer integer) {

    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM employees";

        try (
            Connection conn = dbManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(sql)
        ) {
            if (result.next())
                return Math.toIntExact(result.getLong(1));
        } catch (SQLException e) {
            logger.error("Error counting employees", e);
        }

        return 0;
    }

    private Employee mapRowToEmployee(ResultSet result) throws SQLException {
        return new Employee(
            result.getInt("id"),
            result.getInt("user_id"),
            result.getString("first_name"),
            result.getString("last_name"),
            result.getString("ic_passport")
        );
    }
}
