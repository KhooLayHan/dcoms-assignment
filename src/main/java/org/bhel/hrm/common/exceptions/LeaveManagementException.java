package org.bhel.hrm.common.exceptions;

/**
 * Thrown for any business rule violation related to leave management,
 * such as insufficient leave balance or overlapping leave dates.
 * <p>
 * This exception covers domain-specific errors in leave processing.
 */
public final class LeaveManagementException extends HRMException {
    /**
     * Constructs a LeaveManagementException with the specified detail message.
     *
     * @param message The detail message explaining the leave-related error
     */
    public LeaveManagementException(String message) {
        super(message);
    }
}
