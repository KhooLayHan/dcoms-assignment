package org.bhel.hrm.common.utils;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import org.bhel.hrm.common.exceptions.*;

import java.sql.SQLException;

public class ExceptionTranslator {
    private ExceptionTranslator() {
        throw new UnsupportedOperationException("This class ExceptionTranslator is a utility class; it should not be instantiated.");
    }

    public static DataAccessException translate(String message, SQLException ex) {
        // MySQL Vendor Error Code: https://dev.mysql.com/doc/mysql-errors/8.0/en/server-error-reference.html
        int errorCode = ex.getErrorCode();

        if (ex instanceof CommunicationsException)
            return errorCodeMapper(message, ex, errorCode);

        return new DataAccessException(message, ex);
    }

    private static DataAccessException errorCodeMapper(String message, SQLException ex, Integer errorCode) {
        if (errorCode == null)
            throw new IllegalStateException("MySQL's vendor error code is NULL.");

        return switch (errorCode) {
            // --- 1. Connection / Resource Failure Errors ---

            // Access denied error
            case 1045 -> new DataAccessResourceFailureException(message, ex);

            // --- 2. Data Integrity / Constraint Violation Errors ---

            // Duplicate entry for UNIQUE constraint
            case 1062 -> new DataIntegrityViolationException("Duplicate entry violation: " + message, ex);

            // Foreign key constraint fails
            case 1451, 1452 -> new DataIntegrityViolationException("Foreign key constraint violation: " + message, ex);

            // --- 3. SQL Grammar Errors ---

            // Syntax error
            case 1064 -> new IncorrectSqlGrammarException("Incorrect SQL grammar: " + message, ex);

            // --- 4. Locking Errors ---

            // Lock wait timeout or deadlock found
            case 1205, 1213 -> new CannotAcquireLockException("Could not acquire database lock: " + message, ex);

            // Default fallback for unknown errors
            default -> new DataAccessException(message, ex);
        };
    }
}
