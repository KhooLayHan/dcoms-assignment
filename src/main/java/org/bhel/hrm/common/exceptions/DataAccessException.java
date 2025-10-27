package org.bhel.hrm.common.exceptions;

/**
 * A runtime exception thrown when there is a critical, unrecoverable error
 * during a database operation (e.g., connection failure, SQL syntax error).
 * <p>
 * This exception serves as a base class for more specific data access exceptions
 * and is used to wrap underlying data access failures, propagating them as unchecked exceptions.
 */
public sealed class DataAccessException extends RuntimeException permits
    CannotAcquireLockException,
    DataAccessResourceFailureException,
    DataIntegrityViolationException,
    IncorrectSqlGrammarException {
    /**
     * Constructs a new DataAccessException with the specified detail message and cause.
     *
     * @param message The detail message explaining the data access error
     * @param cause The underlying cause of the failure (typically a SQLException)
     */
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
