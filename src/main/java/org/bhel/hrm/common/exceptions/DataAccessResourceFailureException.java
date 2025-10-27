package org.bhel.hrm.common.exceptions;

/**
 * A {@link DataAccessException} thrown when a critical resource needed
 * for data access fails (e.g., a database connection cannot be established).
 */
public final class DataAccessResourceFailureException extends DataAccessException {
    /**
     * Constructs a new DataAccessResourceFailureException with the specified detail message and cause.
     *
     * @param message The detail message explaining the resource failure error when accessing the data
     * @param cause The underlying cause of the resource failure
     */
    public DataAccessResourceFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
