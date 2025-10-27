package org.bhel.hrm.server.exceptions;

/**
 * A runtime exception thrown when there is a critical, unrecoverable error
 * during a database operation (e.g., connection failure, SQL syntax error).
 * <p>
 * This exception should be used to wrap underlying data access failures
 * and propagate them as unchecked exceptions.
 */
public class DataAccessLayerException extends RuntimeException {
    /**
     * Constructs a new DataAccessLayerException with the specified detail message and cause.
     *
     * @param message The detail message explaining the data access error
     * @param cause The underlying cause of the failure (typically a SQLException)
     */
    public DataAccessLayerException(String message, Throwable cause) {
        super(message, cause);
    }
}
