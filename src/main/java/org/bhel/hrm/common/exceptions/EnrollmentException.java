package org.bhel.hrm.common.exceptions;

/**
 * A generic exception for when an employee fails to enroll in something,
 * such as a training course or a benefit plan.
 * <p>
 * This exception is used to signal business-level enrollment failures.
 */
public final class EnrollmentException extends HRMException {
    /**
     * Constructs an EnrollmentException with the specified detail message.
     *
     * @param message The detail message explaining the enrollment failure
     */
    public EnrollmentException(String message) {
        super(message);
    }
}
