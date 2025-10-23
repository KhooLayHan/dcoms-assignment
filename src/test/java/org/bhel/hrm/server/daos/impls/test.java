package org.bhel.hrm.server.daos.impls;

import org.bhel.hrm.common.dtos.UserDTO;
import org.bhel.hrm.server.DatabaseManager;
import org.bhel.hrm.server.domain.User;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for UserDAOImpl using an in-memory H2 database.
 * These tests verify the actual database interactions and SQL queries.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDAOImplIntegrationTest {

    private DatabaseManager dbManager;
    private UserDAOImpl userDAO;

    @BeforeAll
    void setUpDatabase() throws Exception {
        // Create an in-memory H2 database for testing
        dbManager = new DatabaseManager("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        userDAO = new UserDAOImpl(dbManager);

        // Create the schema
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(255) NOT NULL UNIQUE,
                    password_hash VARCHAR(255) NOT NULL,
                    role_id INT NOT NULL
                )
            """);
        }
    }

    @BeforeEach
    void cleanDatabase() throws Exception {
        // Clear all data before each test
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM users");
            stmt.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
        }
    }

    @AfterAll
    void tearDownDatabase() throws Exception {
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS users");
        }
    }

    @Nested
    @DisplayName("Insert and FindById Integration Tests")
    class InsertAndFindByIdTests {

        @Test
        @DisplayName("should insert and retrieve user with HR_STAFF role")
        void shouldInsertAndRetrieveHRStaff() {
            // Given
            User newUser = new User(0, "hr.admin", "hashed_pass_123", UserDTO.Role.HR_STAFF);

            // When
            userDAO.save(newUser);

            // Then
            assertThat(newUser.getId()).isNotZero();

            Optional<User> retrieved = userDAO.findById(newUser.getId());
            assertThat(retrieved).isPresent();
            assertThat(retrieved.get())
                    .extracting(User::getId, User::getUsername, User::getPasswordHash, User::getRole)
                    .containsExactly(newUser.getId(), "hr.admin", "hashed_pass_123", UserDTO.Role.HR_STAFF);
        }

        @Test
        @DisplayName("should insert and retrieve user with EMPLOYEE role")
        void shouldInsertAndRetrieveEmployee() {
            // Given
            User newUser = new User(0, "employee.user", "hashed_pass_456", UserDTO.Role.EMPLOYEE);

            // When
            userDAO.save(newUser);

            // Then
            assertThat(newUser.getId()).isNotZero();

            Optional<User> retrieved = userDAO.findById(newUser.getId());
            assertThat(retrieved).isPresent();
            assertThat(retrieved.get().getRole()).isEqualTo(UserDTO.Role.EMPLOYEE);
        }

        @Test
        @DisplayName("should return empty optional when user does not exist")
        void shouldReturnEmptyWhenUserNotFound() {
            // When
            Optional<User> result = userDAO.findById(9999);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should generate sequential IDs for multiple inserts")
        void shouldGenerateSequentialIds() {
            // Given
            User user1 = new User(0, "user1", "pass1", UserDTO.Role.EMPLOYEE);
            User user2 = new User(0, "user2", "pass2", UserDTO.Role.EMPLOYEE);
            User user3 = new User(0, "user3", "pass3", UserDTO.Role.HR_STAFF);

            // When
            userDAO.save(user1);
            userDAO.save(user2);
            userDAO.save(user3);

            // Then
            assertThat(user1.getId()).isEqualTo(1);
            assertThat(user2.getId()).isEqualTo(2);
            assertThat(user3.getId()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("Update Integration Tests")
    class UpdateTests {

        @Test
        @DisplayName("should update existing user's username")
        void shouldUpdateUsername() {
            // Given
            User user = new User(0, "original.name", "password", UserDTO.Role.EMPLOYEE);
            userDAO.save(user);
            int userId = user.getId();

            // When
            user.setUsername("updated.name");
            userDAO.save(user);

            // Then
            Optional<User> updated = userDAO.findById(userId);
            assertThat(updated).isPresent();
            assertThat(updated.get().getUsername()).isEqualTo("updated.name");
        }

        @Test
        @DisplayName("should update existing user's password")
        void shouldUpdatePassword() {
            // Given
            User user = new User(0, "user.test", "old_password", UserDTO.Role.EMPLOYEE);
            userDAO.save(user);
            int userId = user.getId();

            // When
            user.setPasswordHash("new_password_hash");
            userDAO.save(user);

            // Then
            Optional<User> updated = userDAO.findById(userId);
            assertThat(updated).isPresent();
            assertThat(updated.get().getPasswordHash()).isEqualTo("new_password_hash");
        }

        @Test
        @DisplayName("should update existing user's role")
        void shouldUpdateRole() {
            // Given
            User user = new User(0, "user.test", "password", UserDTO.Role.EMPLOYEE);
            userDAO.save(user);
            int userId = user.getId();

            // When
            user.setRole(UserDTO.Role.HR_STAFF);
            userDAO.save(user);

            // Then
            Optional<User> updated = userDAO.findById(userId);
            assertThat(updated).isPresent();
            assertThat(updated.get().getRole()).isEqualTo(UserDTO.Role.HR_STAFF);
        }

        @Test
        @DisplayName("should not change ID when updating")
        void shouldNotChangeIdWhenUpdating() {
            // Given
            User user = new User(0, "user.test", "password", UserDTO.Role.EMPLOYEE);
            userDAO.save(user);
            int originalId = user.getId();

            // When
            user.setUsername("new.username");
            userDAO.save(user);

            // Then
            assertThat(user.getId()).isEqualTo(originalId);
        }
    }

    @Nested
    @DisplayName("FindByUsername Integration Tests")
    class FindByUsernameTests {

        @Test
        @DisplayName("should find user by username")
        void shouldFindUserByUsername() {
            // Given
            User user = new User(0, "john.doe", "password123", UserDTO.Role.EMPLOYEE);
            userDAO.save(user);

            // When
            Optional<User> found = userDAO.findByUsername("john.doe");

            // Then
            assertThat(found).isPresent();
            assertThat(found.get())
                    .extracting(User::getUsername, User::getPasswordHash, User::getRole)
                    .containsExactly("john.doe", "password123", UserDTO.Role.EMPLOYEE);
        }

        @Test
        @DisplayName("should return empty optional when username does not exist")
        void shouldReturnEmptyWhenUsernameNotFound() {
            // When
            Optional<User> found = userDAO.findByUsername("nonexistent.user");

            // Then
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("should find correct user among multiple users")
        void shouldFindCorrectUserAmongMultiple() {
            // Given
            userDAO.save(new User(0, "alice", "pass1", UserDTO.Role.EMPLOYEE));
            userDAO.save(new User(0, "bob", "pass2", UserDTO.Role.HR_STAFF));
            userDAO.save(new User(0, "charlie", "pass3", UserDTO.Role.EMPLOYEE));

            // When
            Optional<User> found = userDAO.findByUsername("bob");

            // Then
            assertThat(found).isPresent();
            assertThat(found.get())
                    .extracting(User::getUsername, User::getRole)
                    .containsExactly("bob", UserDTO.Role.HR_STAFF);
        }
    }

    @Nested
    @DisplayName("FindAll Integration Tests")
    class FindAllTests {

        @Test
        @DisplayName("should return empty list when no users exist")
        void shouldReturnEmptyListWhenNoUsers() {
            // When
            List<User> users = userDAO.findAll();

            // Then
            assertThat(users).isEmpty();
        }

        @Test
        @DisplayName("should return all users ordered by username")
        void shouldReturnAllUsersOrderedByUsername() {
            // Given
            userDAO.save(new User(0, "charlie", "pass3", UserDTO.Role.EMPLOYEE));
            userDAO.save(new User(0, "alice", "pass1", UserDTO.Role.HR_STAFF));
            userDAO.save(new User(0, "bob", "pass2", UserDTO.Role.EMPLOYEE));

            // When
            List<User> users = userDAO.findAll();

            // Then
            assertThat(users)
                    .hasSize(3)
                    .extracting(User::getUsername)
                    .containsExactly("alice", "bob", "charlie");
        }

        @Test
        @DisplayName("should return users with correct roles")
        void shouldReturnUsersWithCorrectRoles() {
            // Given
            userDAO.save(new User(0, "hr1", "pass1", UserDTO.Role.HR_STAFF));
            userDAO.save(new User(0, "emp1", "pass2", UserDTO.Role.EMPLOYEE));

            // When
            List<User> users = userDAO.findAll();

            // Then
            assertThat(users)
                    .extracting(User::getUsername, User::getRole)
                    .containsExactly(
                            tuple("emp1", UserDTO.Role.EMPLOYEE),
                            tuple("hr1", UserDTO.Role.HR_STAFF)
                    );
        }
    }

    @Nested
    @DisplayName("Delete Integration Tests")
    class DeleteTests {

        @Test
        @DisplayName("should delete user by id")
        void shouldDeleteUserById() {
            // Given
            User user = new User(0, "to.delete", "password", UserDTO.Role.EMPLOYEE);
            userDAO.save(user);
            int userId = user.getId();

            // When
            userDAO.deleteById(userId);

            // Then
            Optional<User> deleted = userDAO.findById(userId);
            assertThat(deleted).isEmpty();
        }

        @Test
        @DisplayName("should not affect other users when deleting")
        void shouldNotAffectOtherUsersWhenDeleting() {
            // Given
            User user1 = new User(0, "user1", "pass1", UserDTO.Role.EMPLOYEE);
            User user2 = new User(0, "user2", "pass2", UserDTO.Role.EMPLOYEE);
            User user3 = new User(0, "user3", "pass3", UserDTO.Role.EMPLOYEE);
            userDAO.save(user1);
            userDAO.save(user2);
            userDAO.save(user3);

            // When
            userDAO.deleteById(user2.getId());

            // Then
            assertThat(userDAO.findById(user1.getId())).isPresent();
            assertThat(userDAO.findById(user2.getId())).isEmpty();
            assertThat(userDAO.findById(user3.getId())).isPresent();
        }

        @Test
        @DisplayName("should handle deleting non-existent user gracefully")
        void shouldHandleDeletingNonExistentUser() {
            // When/Then - should not throw exception
            assertThatCode(() -> userDAO.deleteById(9999))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Count Integration Tests")
    class CountTests {

        @Test
        @DisplayName("should return 0 when no users exist")
        void shouldReturnZeroWhenNoUsers() {
            // When
            long count = userDAO.count();

            // Then
            assertThat(count).isZero();
        }

        @Test
        @DisplayName("should return correct count of users")
        void shouldReturnCorrectCount() {
            // Given
            userDAO.save(new User(0, "user1", "pass1", UserDTO.Role.EMPLOYEE));
            userDAO.save(new User(0, "user2", "pass2", UserDTO.Role.HR_STAFF));
            userDAO.save(new User(0, "user3", "pass3", UserDTO.Role.EMPLOYEE));

            // When
            long count = userDAO.count();

            // Then
            assertThat(count).isEqualTo(3);
        }

        @Test
        @DisplayName("should update count after insertions and deletions")
        void shouldUpdateCountAfterOperations() {
            // Given
            User user1 = new User(0, "user1", "pass1", UserDTO.Role.EMPLOYEE);
            User user2 = new User(0, "user2", "pass2", UserDTO.Role.EMPLOYEE);
            userDAO.save(user1);
            userDAO.save(user2);

            assertThat(userDAO.count()).isEqualTo(2);

            // When
            userDAO.deleteById(user1.getId());

            // Then
            assertThat(userDAO.count()).isEqualTo(1);

            // When
            userDAO.save(new User(0, "user3", "pass3", UserDTO.Role.EMPLOYEE));

            // Then
            assertThat(userDAO.count()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("Complete Workflow Integration Tests")
    class CompleteWorkflowTests {

        @Test
        @DisplayName("should support complete CRUD workflow")
        void shouldSupportCompleteCRUDWorkflow() {
            // Create
            User user = new User(0, "workflow.user", "initial_password", UserDTO.Role.EMPLOYEE);
            userDAO.save(user);
            int userId = user.getId();
            assertThat(userId).isPositive();

            // Read
            Optional<User> retrieved = userDAO.findById(userId);
            assertThat(retrieved).isPresent();
            assertThat(retrieved.get().getUsername()).isEqualTo("workflow.user");

            // Update
            user.setUsername("updated.user");
            user.setPasswordHash("new_password");
            user.setRole(UserDTO.Role.HR_STAFF);
            userDAO.save(user);

            retrieved = userDAO.findById(userId);
            assertThat(retrieved).isPresent();
            assertThat(retrieved.get())
                    .extracting(User::getUsername, User::getPasswordHash, User::getRole)
                    .containsExactly("updated.user", "new_password", UserDTO.Role.HR_STAFF);

            // Delete
            userDAO.deleteById(userId);
            assertThat(userDAO.findById(userId)).isEmpty();
        }
    }
}