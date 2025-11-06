package org.bhel.hrm.common.utils;

import org.bhel.hrm.common.error.*;
import org.bhel.hrm.common.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.sql.SQLException;

public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ExceptionMappingConfig mappingConfig;
    private final ErrorMessageProvider messageProvider;

//    private GlobalExceptionHandler() {
//        throw new UnsupportedOperationException("This class GlobalExceptionHandler is a utility class; it should not be instantiated.");
//    }

    public GlobalExceptionHandler(
        ExceptionMappingConfig mappingConfig,
        ErrorMessageProvider messageProvider
    ) {
        this.mappingConfig = mappingConfig;
        this.messageProvider = messageProvider;
    }

    public GlobalExceptionHandler() {
        this(new ExceptionMappingConfig(), new ErrorMessageProvider());
    }

    public void handle(Exception e, ErrorContext context) throws RemoteException, HRMException {
        if (e == null) {
            logger.error("Null exception passed to handler with context: {}", context);
            throw new RemoteException("An unexpected error occurred");
        }

        String errorId = context.getErrorId();
        String operation = context.getOperation();

        logger.debug("Handling exception [errorId={}] for operation: {}",
            errorId, operation);

        // 1. Handle specific and known business exceptions that were thrown.
        if (e instanceof HRMException hrmException) {
            handleBusinessException(hrmException, context);
            throw hrmException;
        }

        // 2. Handle specific and expected database errors and translate them into business exceptions.
        if (e instanceof SQLException) {
            handleSQLException((SQLException) e, context);
            throw (SQLException) e;
        }

        // 3. Handle all other unexpected, unrecoverable exceptions
        handleUnexpectedException(e, context);
    }

    /**
     * Convenience method with operation context
     */
    public void handle(Exception e, String operation) throws RemoteException, HRMException {
        ErrorContext context = ErrorContext.forOperation(operation);
        handle(e, context);
    }

    /**
     * Convenience method with user context
     */
    public void handle(Exception e, String operation, String userId) throws RemoteException, HRMException {
        ErrorContext context = ErrorContext.forUser(operation, userId);
        handle(e, context);
    }

    private void handleBusinessException(HRMException e, ErrorContext context) {
        String errorId = context.getErrorId();
        ErrorCode errorCode = e.getErrorCode();

        logger.warn(
            "Business exception [errorId={}, code={}, operation={}]: {}",
            errorId,
            errorCode.getCode(),
            context.getOperation(),
            e.getMessage()
        );

        if (!context.getAdditionalData().isEmpty()) {
            logger.debug("Additional context [errorId={}]: {}",
                errorId, context.getAdditionalData());
        }
    }

    private void handleSQLException(SQLException e, ErrorContext context) throws HRMException {
        String errorId = context.getErrorId();
        String operation = context.getOperation();

        logger.warn(
            "SQL exception [errorId={}, sqlCode={}, sqlState={}, operation={}]: {}",
            errorId,
            e.getErrorCode(),
            e.getSQLState(),
            operation,
            e.getMessage()
        );

        DataAccessException translated = mappingConfig.translate(e, context);

        logger.debug(
            "Translated to [errorId={}, code={}], {}",
            errorId,
            translated.getErrorCode().getCode(),
            translated.getMessage()
        );

        throw translated;
    }

    private void handleUnexpectedException(Exception e, ErrorContext context) throws RemoteException {
        String errorId = context.getErrorId();
        String operation = context.getOperation();

        logger.warn(
            "Unrecoverable error [errorId={}, operation={}, exceptionType={}]",
            errorId,
            operation,
            e.getClass().getName(),
            e
        );

        throw new RemoteException(
            String.format(
                "A critical server error occurred [Error ID: %s]. Please contact support.",
                errorId
            ),
            e
        );
    }

    /**
     * Creates a user-safe error response using injected message provider
     */
    public ErrorResponse createErrorResponse(Exception e, ErrorContext context) {
        if (e instanceof HRMException hrmException) {
            return new ErrorResponse(
                context.getErrorId(),
                hrmException.getErrorCode(),
                messageProvider.getMessage(hrmException.getErrorCode()),
                context.getTimestamp()
            );
        }

        return new ErrorResponse(
            context.getErrorId(),
            ErrorCode.SYSTEM_ERROR,
            messageProvider.getMessage(ErrorCode.SYSTEM_ERROR),
            context.getTimestamp()
        );
    }

    /**
     * Checks if an Exception is retryable.
     */
    public boolean isRetryable(Exception e) {
        if (e instanceof TransientDataAccessException)
            return true;

        if (e instanceof HRMException hrmException) {
            ErrorCode errorCode = hrmException.getErrorCode();

            return
                errorCode == ErrorCode.DB_DEADLOCK ||
                errorCode == ErrorCode.DB_CONNECTION_FAILED ||
                errorCode == ErrorCode.DB_LOCK_TIMEOUT
            ;
        }

        return false;
    }
}
