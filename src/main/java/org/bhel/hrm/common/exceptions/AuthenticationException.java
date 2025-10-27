package org.bhel.hrm.common.exceptions;

/**
 * Thrown when a user provides invalid credentials (e.g., unknown username or wrong password).
 * <p>
 * This exception indicates an authentication failure due to incorrect login details.
 */
public final class AuthenticationException extends HRMException {
    /**
     * Constructs an AuthenticationException for the specified username.
     *
     * @param username The username that failed authentication
     */
    public AuthenticationException(String username) {
        super("Invalid username or password.");
    }
}
