package org.bhel.hrm.common.error;

import org.bhel.hrm.common.exceptions.*;

import java.sql.SQLException;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Configuration-driven mapping of database errors to application exceptions.
 * At the moment, only supports MySQL database vendor and custom mapping rules.
 */
public class ExceptionMappingConfig {
    private final Map<Integer, ExceptionMapping> mysqlMappings;
    private final Map<Integer, ContextBasedMapping> contextMappings;

//    private static final ExceptionMappingConfig

    private void initializeMySQLMappings() {
        // --- 1. Connection / Resource Failure Errors ---

        // Access denied error
        mysqlMappings.put(1045, new ExceptionMapping(
            ErrorCode.AUTH_INVALID_CREDENTIALS,
            DataAccessResourceFailureException::new
        ));

        // Connection failure error
        mysqlMappings.put(2013, new ExceptionMapping(
            ErrorCode.DB_CONNECTION_FAILED,
            DataAccessResourceFailureException::new
        ));

        // --- 2. Data Integrity / Constraint Violation Errors ---

        // Duplicate entry
        mysqlMappings.put(1062, new ExceptionMapping(
            ErrorCode.DB_DUPLICATE_ENTRY,
            DataIntegrityViolationException::new
        ));

        // Foreign key constraint fails
        mysqlMappings.put(1451, new ExceptionMapping(
            ErrorCode.DB_FOREIGN_KEY_VIOLATION,
            DataIntegrityViolationException::new
        ));

        mysqlMappings.put(1452, new ExceptionMapping(
            ErrorCode.DB_FOREIGN_KEY_VIOLATION,
            DataIntegrityViolationException::new
        ));

        // --- 3. MySQL Grammar Errors ---

        // Syntax error
        mysqlMappings.put(1064, new ExceptionMapping(
            ErrorCode.DB_QUERY_ERROR,
            IncorrectSqlGrammarException::new
        ));

        // --- 4. Locking Errors ---

        // Deadlock found
        mysqlMappings.put(1213, new ExceptionMapping(
            ErrorCode.DB_DEADLOCK,
            CannotAcquireLockException::new
        ));

        // Lock wait timeout
        mysqlMappings.put(1205, new ExceptionMapping(
            ErrorCode.DB_LOCK_TIMEOUT,
            CannotAcquireLockException::new
        ));
    }

    private void initializeContextMappings() {
        // Registration operations
        contextMappings.put("registration", new ContextBasedMapping(
           1062,
           ErrorCode.USER_ALREADY_EXISTS,
           DuplicateUserException::new
        ));

        contextMappings.put("employee.create", new ContextBasedMapping(
                1062,
                ErrorCode.EMPLOYEE_DUPLICATE_ID,
                DuplicateUserException::new
        ));

        contextMappings.put("employee.delete", new ContextBasedMapping(
                1062,
                ErrorCode.EMPLOYEE_HAS_DEPENDENCIES,
                DataIntegrityViolationException::new
        ));

        contextMappings.put("update", new ContextBasedMapping(
                1062,
                ErrorCode.DB_DUPLICATE_ENTRY,
                InvalidInputException::new
        ));
    }


    private record ExceptionMapping(
        ErrorCode errorCode,
        BiFunction<String, SQLException, DataAccessException> exceptionFactory
    ) {
        DataAccessException createException(String message, SQLException ex) {
            return exceptionFactory.apply(message, ex);
        }
    }

    private record ContextBasedMapping(
        int mysqlErrorCode,
        ErrorCode errorCode,
        BiFunction<String, SQLException, DataAccessException> exceptionFactory
    ) {
        DataAccessException createException(String message, SQLException ex) {
            return exceptionFactory.apply(message, ex);
        }
    }
}
