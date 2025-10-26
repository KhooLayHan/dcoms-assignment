package org.bhel.hrm.server.exceptions;

/**
 * Thrown when a requested user cannot be found in the system.
 * <p>
 * This exception indicates that an operation requiring a specific user failed
 * because no user with the specified identifier exists.
 */
public final class UserNotFoundException extends HRMException {
    /**
     * Constructs a UserNotFoundException for the specified identifier.
     *
     * @param identifier The username that was not found
     */
    public UserNotFoundException(String identifier) {
        super("User with username " + identifier + " was not found.");
    }
}
