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
        if (employee.getId() == 0) {
            String sql = "INSERT INTO employees (user_id, first_name, last_name, ic_passport) VALUES (?, ?, ?, ?)";

            try (
                Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            ) {
                stmt.setInt(1, employee.getUserId());
                stmt.setString(2, employee.getFirstName());
                stmt.setString(3, employee.getLastName());
                stmt.setString(4, employee.getIcPassport());

                stmt.executeUpdate();

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next())
                        employee.setId(generatedKeys.getInt(1)); // Sets the new ID back on the object
                }
            } catch (SQLException e) {
                logger.error("Error inserting new employee: {}", employee.getFirstName(), e);
            }
        } else {
            String sql = "UPDATE employees SET user_id = ?, first_name = ?, last_name = ?, ic_passport = ? WHERE id = ?";

            try (
                Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
            ) {
                stmt.setInt(1, employee.getUserId());
                stmt.setString(2, employee.getFirstName());
                stmt.setString(3, employee.getLastName());
                stmt.setString(4, employee.getIcPassport());
                stmt.setInt(5, employee.getId());

                stmt.executeUpdate();
            } catch (SQLException e) {
                logger.error("Error updating employee: {}", employee.getId(), e);
            }
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
