package org.bhel.hrm.server.daos.impls;

import org.bhel.hrm.common.dtos.UserDTO;
import org.bhel.hrm.server.DatabaseManager;
import org.bhel.hrm.server.daos.UserDAO;
import org.bhel.hrm.server.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);

    private final DatabaseManager databaseManager;

    public UserDAOImpl(DatabaseManager dbManager) {
        this.databaseManager = dbManager;
    }

    @Override
    public Optional<User> findById(Integer integer) {
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public User save(User entity) {
        return null;
    }

    @Override
    public void deleteById(Integer integer) {

    }

    @Override
    public int count() {
        return 0;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

//        try (Connection conn = databaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setString(1, username);
//
//            try (ResultSet result = pstmt.executeQuery()) {
//                if (result.next())
//                    return Optional.of(mapRowToUser(result));
//            } catch (SQLException e) {
//                logger.error("Error finding user by username: {}", username, e);
//            }
//        }

        return Optional.empty();
    }

    private User mapRowToUser(ResultSet result) throws SQLException {
        return new User(
            result.getInt("id"),
            result.getString("username"),
            result.getString("passwordHash"),
            UserDTO.Role.valueOf(result.getString("role"))
        );
    }
}
