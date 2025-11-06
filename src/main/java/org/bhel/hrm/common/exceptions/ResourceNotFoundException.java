package org.bhel.hrm.common.exceptions;

import org.bhel.hrm.common.error.ErrorCode;
import org.bhel.hrm.common.error.ErrorContext;

/**
 * A generic exception for when any entity (e.g., Employee, Leave Application)
 * cannot be found by its ID.
 * <p>
 * This is a more general exception than UserNotFoundException and
 * applies to any resource lookup failure.
 */
public final class ResourceNotFoundException extends HRMException {
    private final Object resourceId;
    private final String resourceType;

    /**
     * Constructs a ResourceNotFoundException with basic message.
     *
     * @param message The detail message
     */
    public ResourceNotFoundException(String message) {
        super(ErrorCode.EMPLOYEE_NOT_FOUND, message);
        this.resourceId = null;
        this.resourceType = null;
    }

    /**
     * Constructs a ResourceNotFoundException for a specific resource.
     *
     * @param resourceType The type of resource (e.g., "Employee", "Leave Request")
     * @param resourceId The identifier of the resource that was not found
     */
    public ResourceNotFoundException(String resourceType, Object resourceId) {
        super(
            ErrorCode.EMPLOYEE_NOT_FOUND,
            formatMessage(resourceType, resourceId)
        );
        this.resourceId = resourceId;
        this.resourceType = resourceType;
    }

    /**
     * Constructs a ResourceNotFoundException with specific error code.
     *
     * @param errorCode The specific not found error code
     * @param message The detail message
     */
    public ResourceNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
        this.resourceId = null;
        this.resourceType = null;
    }

    /**
     * Constructs a ResourceNotFoundException with error code and context.
     *
     * @param errorCode The specific not found error code
     * @param message The detail message
     * @param context The error context
     */
    public ResourceNotFoundException(ErrorCode errorCode, String message, ErrorContext context) {
        super(errorCode, message, context);
        this.resourceId = null;
        this.resourceType = null;
    }

    /**
     * Constructs a ResourceNotFoundException with resource details and context.
     *
     * @param resourceType The type of resource
     * @param resourceId The identifier of the resource
     * @param context The error context
     */
    public ResourceNotFoundException(String resourceType, Object resourceId, ErrorContext context) {
        super(
            ErrorCode.EMPLOYEE_NOT_FOUND,
            formatMessage(resourceType, resourceId),
            context
        );
        this.resourceId = resourceId;
        this.resourceType = resourceType;
    }

    private static String formatMessage(
        String resourceType,
        Object resourceId
    ) {
        return String.format("%s with ID '%s' was not found",
            resourceType, resourceId);
    }

    public String getResourceType() {
        return resourceType;
    }

    public Object getResourceId() {
        return resourceId;
    }
}
