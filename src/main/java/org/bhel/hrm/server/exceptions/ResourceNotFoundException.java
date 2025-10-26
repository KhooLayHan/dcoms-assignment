package org.bhel.hrm.server.exceptions;

/**
 * A generic exception for when any entity (e.g., Employee, Leave Application)
 * cannot be found by its ID.
 * <p>
 * This is a more general exception than UserNotFoundException and
 * applies to any resource lookup failure.
 */
public final class ResourceNotFoundException extends HRMException {
    /**
     * Constructs a ResourceNotFoundException for the specified resource type and ID.
     *
     * @param resourceType The type of the resource that was not found (e.g., "Employee")
     * @param id The identifier of the resource that was not found
     */
    public ResourceNotFoundException(String resourceType, Object id) {
        super(resourceType + " with ID " + id + " was not found.");
    }
}
