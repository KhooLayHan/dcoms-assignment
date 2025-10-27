package org.bhel.hrm.common.exceptions;

/**
 * Thrown when a user provides invalid credentials (e.g., unknown username or wrong password).
 * <p>
 * This exception indicates an authentication failure due to incorrect login details.
 * It is recommended not to disclose whether the username exists or which specific
 * credential was incorrect, to prevent user enumeration attacks.
 */
public final class AuthenticationException extends HRMException {
    private final String username;

    /**
     * Constructs an AuthenticationException for the specified username.
     *
     * @param username The username that failed authentication; may be null
     */
    public AuthenticationException(String username) {
        super("Invalid username or password.");
        this.username = username;
    }

    /**
     * Constructs an AuthenticationException with the specified username and cause.
     *
     * @param username The username that failed authentication; may be null
     * @param cause    The underlying cause of the authentication failure
     */
    public AuthenticationException(String username, Throwable cause) {
        super("Invalid username or password.", cause);
        this.username = username;
    }

    /**
     * Returns the username associated with this authentication failure.
     *
     * @return the username that failed authentication, or {@code null} if not specified
     */
    public String getUsername() {
        return username;
    }
}
