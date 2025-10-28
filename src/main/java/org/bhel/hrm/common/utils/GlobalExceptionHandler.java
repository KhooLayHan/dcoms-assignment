package org.bhel.hrm.common.utils;

import org.bhel.hrm.common.exceptions.DataIntegrityViolationException;
import org.bhel.hrm.common.exceptions.DuplicateUserException;
import org.bhel.hrm.common.exceptions.HRMException;
import org.bhel.hrm.common.exceptions.InvalidInputException;
import org.bhel.hrm.common.services.HRMService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

public class ExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    private ExceptionHandler() {
        throw new UnsupportedOperationException("This class ExceptionHandler is a utility class; it should not be instantiated.");
    }

    public static void handle(Exception e, String context) throws RemoteException, HRMException {
        // 1. Handle specific and known business exceptions that were thrown.
        if (e instanceof HRMException) {
            logger.warn("A business rule violation that occurred during {}: {}", context, e.getMessage());
            throw (HRMException) e;
        }

        // 2. Handle specific and expected database errors and translate them into business exceptions.
        if (e instanceof DataIntegrityViolationException) {
            logger.warn("A data integrity violation occurred during {}.", context, e);

            if (context.contains("registration"))
                throw new DuplicateUserException("An employee with the same details may already exist.");
            else if (context.contains("update"))
                throw new InvalidInputException("The update violates a data constraint");

            switch (context.contains()) {
                case context.contains("registration" -> throw new DuplicateUserException("An employee with the same details may already exist.");
                case "update"
            }
        }
    }
}
