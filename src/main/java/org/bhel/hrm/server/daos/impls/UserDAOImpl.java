package org.bhel.hrm.server.daos.impls;

import org.bhel.hrm.common.dtos.UserDTO;
import org.bhel.hrm.server.DatabaseManager;
import org.bhel.hrm.server.daos.UserDAO;
import org.bhel.hrm.server.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);

    private final DatabaseManager dbManager;

    public UserDAOImpl(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public Optional<User> findById(Integer id) {
        String sql = "SELECT id, username, password_hash, role_id FROM users WHERE id = ?";

        try (
            Connection conn = dbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, id);

            try (ResultSet result = stmt.executeQuery()) {
                if (result.next())
                    return Optional.of(mapRowToUser(result));
            }
        } catch (SQLException e) {
            logger.error("Error finding user by ID: {}", id, e);
        }

        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, password_hash, role_id FROM users ORDER BY username ASC";

        try (
            Connection conn = dbManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(sql);
        ) {
            while (result.next())
                users.add(mapRowToUser(result));
        } catch (SQLException e) {
            logger.error("Error finding all users", e);
        }

        return users;
    }

    @Override
    public void save(User user) {
        String sql = (user.getId() == 0)
            ? "INSERT INTO users (username, password_hash, role_id) VALUES (?, ?, ?)"
            : "UPDATE users SET username = ?, password_hash = ?, role_id = ? WHERE id = ?";

        Connection conn;
        try {
            // NOTE: try-with-resources will automatically call the `.close()` method for any
            // resource declared within it when the block is exited. Thus, the connection
            // will fail and crash immediately. Ensure that the connection lifecycle is handled
            // entirely by DatabaseManager by calling it outside the try-with-resources block.
            conn = dbManager.getConnection();

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getPasswordHash());
                stmt.setInt(3, user.getRole() == UserDTO.Role.HR_STAFF ? 1 : 2);

                if (user.getId() != 0)
                    stmt.setInt(4, user.getId()); // Set ID for UPDATE clause

                stmt.executeUpdate();

                // If INSERT clause, get the generated ID
                if (user.getId() == 0) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next())
                            user.setId(generatedKeys.getInt(1)); // Sets the new ID back on the object
                    }
                }
            } catch (SQLException e) {
                logger.error("Error inserting new user: {}", user.getUsername(), e);
            }
        } catch (SQLException e) {
            logger.error("Error inserting new user: {}", user.getUsername(), e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (
            Connection conn = dbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error deleting user by ID: {}", id, e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM users";

        try (
            Connection conn = dbManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(sql)
        ) {
            if (result.next())
                return Math.toIntExact(result.getLong(1));
        } catch (SQLException e) {
            logger.error("Error counting users", e);
        }

        return 0;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT id, username, password_hash, role_id FROM users WHERE username = ?";

        try (Connection conn = dbManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);

            try (ResultSet result = stmt.executeQuery()) {
                if (result.next())
                    return Optional.of(mapRowToUser(result));
            } catch (SQLException e) {
                logger.error("Error finding user by username: {}", username, e);
            }
        } catch (SQLException e) {
            logger.error("Error finding user by username: {}", username, e);
        }

        return Optional.empty();
    }

    private User mapRowToUser(ResultSet result) throws SQLException {
        return new User(
            result.getInt("id"),
            result.getString("username"),
            result.getString("password_hash"),
            result.getInt("role_id") == 1 ? UserDTO.Role.HR_STAFF : UserDTO.Role.EMPLOYEE
        );
    }
}
