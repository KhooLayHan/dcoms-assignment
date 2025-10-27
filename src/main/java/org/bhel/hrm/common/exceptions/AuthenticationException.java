package org.bhel.hrm.common.exceptions;

/**
 * Thrown when a user provides invalid credentials (e.g., unknown username or wrong password).
 * <p>
 * This exception indicates an authentication failure due to incorrect login details.
 */
public final class AuthenticationException extends HRMException {
    private final String username;

    /**
     * Constructs an AuthenticationException for the specified username.
     *
     * @param username The username that failed authentication
     */
    public AuthenticationException(String username) {
        super("Invalid username or password.");
        this.username = username;
    }

    public AuthenticationException(String username, Throwable cause) {
        super("Invalid username or password.", cause);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
