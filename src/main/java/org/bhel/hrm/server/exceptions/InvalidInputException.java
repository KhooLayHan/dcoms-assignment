package org.bhel.hrm.server.exceptions;

/**
 * Thrown when the data provided by the client fails a business validation rule
 * (e.g., a leave application with a start date after the end date).
 * <p>
 * This exception indicates invalid input that violates domain constraints.
 */
public final class InvalidInputException extends HRMException {
    /**
     * Constructs an InvalidInputException with the specified detail message.
     *
     * @param message The detail message explaining the validation failure
     */
    public InvalidInputException(String message) {
        super(message);
    }
}
