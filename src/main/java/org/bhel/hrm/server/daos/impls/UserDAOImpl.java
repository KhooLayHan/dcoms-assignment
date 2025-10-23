package org.bhel.hrm.server.daos.impls;

import org.bhel.hrm.common.dtos.UserDTO;
import org.bhel.hrm.server.DatabaseManager;
import org.bhel.hrm.server.daos.AbstractDAO;
import org.bhel.hrm.server.daos.UserDAO;
import org.bhel.hrm.server.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class UserDAOImpl extends AbstractDAO<User> implements UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);

    private final RowMapper<User> rowMapper = result -> new User(
        result.getInt("id"),
        result.getString("username"),
        result.getString("password_hash"),
        result.getInt("role_id") == 1
            ? UserDTO.Role.HR_STAFF
            : UserDTO.Role.EMPLOYEE
    );

    public UserDAOImpl(DatabaseManager dbManager) {
        super(dbManager);
    }

    @Override
    public Optional<User> findById(Integer id) {
        String sql = """
            SELECT
                id,
                username,
                password_hash,
                role_id
            FROM
                users
            WHERE
                id = ?
        """;

        try {
            return findOne(sql, stmt -> stmt.setInt(1, id), rowMapper);
        } catch (Exception e) {
            logger.error("Error finding user by ID: {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        String sql = """
            SELECT
                id,
                username,
                password_hash,
                role_id
            FROM
                users
            ORDER BY
                username ASC
        """;

        try {
            return findMany(sql, stmt -> {}, rowMapper);
        } catch (Exception e) {
            logger.error("Error finding all users", e);
            return List.of();
        }
    }

    @Override
    public void save(User user) {
        if (user.getId() == 0)
            insert(user);
        else
            update(user);
    }

    private void insert(User user) {
        String sql = """
            INSERT INTO
                users (
                    username,
                    password_hash,
                    role_id
                )
            VALUES (
                ?,
                ?,
                ?
            )
        """;

        try (
            Connection conn = dbManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            setSaveParameters(stmt, user);
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next())
                    user.setId(generatedKeys.getInt(1)); // Sets the new ID back on the object
            }
        } catch (SQLException e) {
            logger.error("Error inserting new user: {}", user.getUsername(), e);
        }
    }

    private void update(User user) {
        String sql = """
            UPDATE
                users
            SET
                username = ?,
                password_hash = ?,
                role_id = ?
            WHERE
                id = ?
        """;

        try {
            executeUpdate(sql, stmt -> {
                setSaveParameters(stmt, user);
                stmt.setInt(4, user.getId());
            });
        } catch (Exception e) {
            logger.error("Error updating existing user: {}", user.getUsername(), e);
        }
    }

    @Override
    protected void setSaveParameters(PreparedStatement stmt, User user) throws SQLException {
        stmt.setString(1, user.getUsername());
        stmt.setString(2, user.getPasswordHash());
        stmt.setInt(3, user.getRole() == UserDTO.Role.HR_STAFF ? 1 : 2);
    }

    @Override
    public void deleteById(Integer id) {
        String sql = """
            DELETE FROM
                users
            WHERE
                id = ?
        """;

        try {
            executeUpdate(sql, stmt -> stmt.setInt(1, id));
        } catch (Exception e) {
            logger.error("Error deleting user by ID: {}", id, e);
        }
    }

    @Override
    public long count() {
        String sql = """
            SELECT
                COUNT(*)
            FROM
                users
        """;

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
        String sql = """
            SELECT
                id,
                username,
                password_hash,
                role_id
            FROM
                users
            WHERE
                username = ?
        """;

        try {
            return findOne(sql, stmt -> stmt.setString(1, username), rowMapper);
        } catch (Exception e) {
            logger.error("Error finding user by username: {}", username, e);
            return Optional.empty();
        }
    }
}