package org.bhel.hrm.common.error;

import org.bhel.hrm.common.exceptions.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Configuration-driven mapping of database errors to application exceptions.
 * At the moment, only supports MySQL database vendor and custom mapping rules.
 */
public class ExceptionMappingConfig {
    public enum DatabaseVendor { MYSQL, POSTGRESQL }

    private final Map<Integer, ExceptionMapping> errorMappings;
    private final Map<String, ContextBasedMapping> contextMappings;
    private final DatabaseVendor vendor;

    public ExceptionMappingConfig() {
        this(DatabaseVendor.MYSQL);
    }

    public ExceptionMappingConfig(DatabaseVendor vendor) {
        this.vendor = vendor;
        this.errorMappings = new HashMap<>();
        this.contextMappings = new HashMap<>();

        initializeMappings();
    }

    private void initializeMappings() {
        switch (vendor) {
            case MYSQL -> initializeMySQLMappings();
            case POSTGRESQL -> initializePostGreSQLMappings();
        }
    }

    private void initializeMySQLMappings() {
        // --- 1. Connection / Resource Failure Errors ---

        // Access denied error
        addMapping(
            1045,
            ErrorCode.AUTH_INVALID_CREDENTIALS,
            DataAccessResourceFailureException::new
        );

        // Connection failure error
        addMapping(
            2013,
            ErrorCode.DB_CONNECTION_FAILED,
            DataAccessResourceFailureException::new
        );

        // --- 2. Data Integrity / Constraint Violation Errors ---

        // Duplicate entry
        addMapping(
            1062,
            ErrorCode.DB_DUPLICATE_ENTRY,
            DataIntegrityViolationException::new
        );

        // Foreign key constraint fails
        addMapping(
            1451,
            ErrorCode.DB_FOREIGN_KEY_VIOLATION,
            DataIntegrityViolationException::new
        );

        addMapping(
            1452,
            ErrorCode.DB_FOREIGN_KEY_VIOLATION,
            DataIntegrityViolationException::new
        );

        // Column cannot be null / missing defaults
        addMapping(
            1048,
            ErrorCode.DB_COLUMN_IS_NULL,
            DataIntegrityViolationException::new
        );

        addMapping(
            1364,
            ErrorCode.DB_COLUMN_IS_NULL,
            DataIntegrityViolationException::new
        );

        // --- 3. MySQL Grammar Errors ---

        // Syntax error
        addMapping(
            1064,
            ErrorCode.DB_QUERY_ERROR,
            IncorrectSqlGrammarException::new
        );

        // --- 4. Locking Errors ---

        // Deadlock found
        addMapping(
            1213,
            ErrorCode.DB_DEADLOCK,
            CannotAcquireLockException::new
        );

        // Lock wait timeout
        addMapping(
            1205,
            ErrorCode.DB_LOCK_TIMEOUT,
            CannotAcquireLockException::new
        );

        // --- 5. Context-specific Errors ---
        addContextMapping(
            "registration",
            1062,
            ErrorCode.USER_ALREADY_EXISTS,
            DuplicateUserException::new
        );

        addContextMapping(
            "employee.create",
            1062,
            ErrorCode.EMPLOYEE_DUPLICATE_ID,
            DuplicateUserException::new
        );

        addContextMapping(
            "employee.delete",
            1451,
            ErrorCode.EMPLOYEE_HAS_DEPENDENCIES,
            DataIntegrityViolationException::new
        );
    }

    private void initializePostGreSQLMappings() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public DataAccessException translate(SQLException ex, ErrorContext context) {
        int errorCode = ex.getErrorCode();
        String operation = context.getOperation();

        // First try context-specific first
        ContextBasedMapping contextMapping = findContextMapping(operation, errorCode);
        if (contextMapping != null) {
            return contextMapping.createException(
                contextMapping.errorCode.getDefaultMessage(), ex
            );
        }

        // Fallback to general error code mapping
        ExceptionMapping mapping = errorMappings.get(errorCode);
        if (mapping != null) {
            return mapping.createException(
                mapping.errorCode.getDefaultMessage() + " during " + operation,
                ex
            );
        }

        // Default fallback
        return new DataAccessException(
            ErrorCode.SYSTEM_ERROR.getDefaultMessage() + " during " + operation,
            ex
        );
    }

    // Adds a custom mapping for a specific error code
    public void addMapping(
        int errorCode,
        ErrorCode appErrorCode,
        BiFunction<String, SQLException, DataAccessException> factory
    ) {
        errorMappings.put(
            errorCode,
            new ExceptionMapping(appErrorCode, factory)
        );
    }

    // Adds a context-specific error code
    public void addContextMapping(
        String context,
        int mysqlErrorCode,
        ErrorCode appErrorCode,
        BiFunction<String, SQLException, DataAccessException> factory
    ) {
        contextMappings.put(
            context.toLowerCase(),
            new ContextBasedMapping(mysqlErrorCode, appErrorCode, factory)
        );
    }

    private ContextBasedMapping findContextMapping(String operation, int errorCode) {
        if (operation == null)
            return null;

        String key = operation.toLowerCase();
        ContextBasedMapping exactMatch = contextMappings.get(key);
        if (exactMatch != null && exactMatch.mysqlErrorCode == errorCode)
            return exactMatch;

        for (Map.Entry<String, ContextBasedMapping> entry : contextMappings.entrySet()) {
            if (
                key.contains(entry.getKey()) &&
                entry.getValue().mysqlErrorCode == errorCode
            )
                return entry.getValue();
        }

        return null;
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
