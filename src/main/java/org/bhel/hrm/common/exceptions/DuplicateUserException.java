package org.bhel.hrm.common.exceptions;

/**
 * Thrown when attempting to create a user with a username that already exists.
 * <p>
 * This exception indicates a violation of the unique username constraint in the system.
 */
public final class DuplicateUserException extends HRMException {
    /**
     * Constructs a DuplicateUserException for the specified username.
     *
     * @param username The username that already exists
     */
    public DuplicateUserException(String username) {
        super("A user with the username " + username + " already exists.");
    }
}
