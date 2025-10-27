package org.bhel.hrm.server.exceptions;

/**
 * A base, checked exception for the HRM application, used for recoverable errors
 * that the caller should be forced to handle.
 * <p>
 * This exception should be extended
 * for specific error conditions within the HRM system. It ensures that
 * exceptional conditions are explicitly handled by client code.
 */
public sealed class HRMException extends Exception permits
    AuthenticationException,
    DuplicateUserException,
    EnrollmentException,
    InvalidInputException,
    LeaveManagementException,
    ResourceNotFoundException,
    UserNotFoundException {
    /**
     * Constructs a new HRM exception with the specified detail message.
     *
     * @param message The detail message explaining the exception
     */
    public HRMException(String message) {
        super(message);
    }

    /**
     * Constructs a new HRM exception with the specified detail message and cause.
     *
     * @param message The detail message explaining the exception
     * @param cause The underlying cause of the exception
     */
    public HRMException(String message, Throwable cause) {
        super(message, cause);
    }
}
