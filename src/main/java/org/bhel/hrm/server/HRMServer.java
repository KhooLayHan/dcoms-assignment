package org.bhel.hrm.server;

import org.bhel.hrm.common.dtos.*;
import org.bhel.hrm.common.services.HRMService;
import org.bhel.hrm.server.daos.EmployeeDAO;
import org.bhel.hrm.server.daos.UserDAO;
import org.bhel.hrm.server.domain.Employee;
import org.bhel.hrm.server.domain.User;
import org.bhel.hrm.common.exceptions.AuthenticationException;
import org.bhel.hrm.common.exceptions.DataAccessLayerException;
import org.bhel.hrm.common.exceptions.DuplicateUserException;
import org.bhel.hrm.common.exceptions.UserNotFoundException;
import org.bhel.hrm.server.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.List;

public class HRMServer extends UnicastRemoteObject implements HRMService {
    private static final Logger logger = LoggerFactory.getLogger(HRMServer.class);

    private final transient DatabaseManager dbManager;
    private final transient EmployeeDAO employeeDAO;
    private final transient UserDAO userDAO;

    public HRMServer(DatabaseManager databaseManager, EmployeeDAO employeeDAO, UserDAO userDAO) throws RemoteException {
        this.dbManager = databaseManager;
        this.employeeDAO = employeeDAO;
        this.userDAO = userDAO;
    }

    @Override
    public UserDTO authenticateUser(String username, String password) throws RemoteException, AuthenticationException {
        logger.info("Authentication attempt for user: {}.", username);

        try {
            User user = userDAO.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

            boolean passwordMatches = PasswordService.checkPassword(password, user.getPasswordHash());
            if (!passwordMatches)
                throw new AuthenticationException(username);

            logger.info("User '{}' authenticated successfully.", username);
            return UserMapper.mapToDto(user);

        } catch (UserNotFoundException | AuthenticationException e) {
            logger.warn("Authentication failed for user '{}'.", username);
            throw new AuthenticationException(username);
        } catch (DataAccessLayerException e) {
            logger.error("A database error occurred during authentication for user '{}'.", username);
            throw new RemoteException("Server error during authentication.", e);
        }
    }

    @Override
    public void registerNewEmployee(NewEmployeeRegistrationDTO registrationData) throws RemoteException, DuplicateUserException {
        logger.info("Attempting to register new employee: {}.", registrationData.username());

        try {
            dbManager.beginTransaction();

            // Business rule check: Does the user already exist?
            if (userDAO.findByUsername(registrationData.username()).isPresent())
                throw new DuplicateUserException(registrationData.username());

            User newUser = new User(
                    registrationData.username(),
                    PasswordService.hashPassword(registrationData.initialPassword()),
                    registrationData.role()
            );
            userDAO.save(newUser);

            Employee newEmployee = new Employee(
                    newUser.getId(),
                    registrationData.firstName(),
                    registrationData.lastName(),
                    registrationData.icPassport()
            );
            employeeDAO.save(newEmployee);

            dbManager.commitTransaction();
            logger.info("Successfully registered the new Employee {} with user ID {}.", newEmployee.getFirstName(), newEmployee.getId());

        } catch (DuplicateUserException e) {
            logger.info("Registration failed: username '{}' already exists. Rolling back transaction.", registrationData.username());

            dbManager.rollbackTransaction();
            throw e;
        } catch (DataAccessLayerException e) {
            logger.error("Registration failed for user '{}'. Rolling back transaction.", registrationData.username());

            dbManager.rollbackTransaction();
            throw new RemoteException("Employee registration failed due to a server-side error.", e);
        } catch (SQLException e) {
            logger.error("SQL error occurred during the registration transaction for user {}. Rolling back transaction.", registrationData.username(), e);

            try {
                dbManager.rollbackTransaction();
            } catch (Exception rollbackException) {
                logger.error("Rollback failed during exception handling.", rollbackException);
            }

            throw new RemoteException("Server transaction failed during registration.", e);
        } finally {
            // Ensure transaction is closed even if an unexpected exception occurs.
            if (dbManager.isTransactionActive())
                dbManager.rollbackTransaction();
        }
    }

    @Override
    public List<EmployeeDTO> getAllEmployees() throws RemoteException {
        return List.of();
    }

    @Override
    public EmployeeDTO getEmployeeById(int employeeId) throws RemoteException {
        return null;
    }

    @Override
    public void updateEmployeeProfile(EmployeeDTO employeeDTO) throws RemoteException {
        throw new RemoteException("not yet implemented");
    }

    @Override
    public void applyForLeave(LeaveApplicationDTO leaveApplicationDTO) throws RemoteException {
        throw new RemoteException("not yet implemented");
    }

    @Override
    public List<LeaveApplicationDTO> getLeaveHistoryForEmployees(int employeeId) throws RemoteException {
        return List.of();
    }

    @Override
    public List<TrainingCourseDTO> getAllTrainingCourses() throws RemoteException {
        return List.of();
    }

    @Override
    public void enrollInTraining(int employeeId, int courseId) throws RemoteException {
        throw new RemoteException("not yet implemented");
    }

    @Override
    public List<JobOpeningDTO> getAllJobOpenings() throws RemoteException {
        return List.of();
    }

    @Override
    public List<ApplicantDTO> getApplicantsForJob(int jobOpeningId) throws RemoteException {
        return List.of();
    }

    @Override
    public List<BenefitPlanDTO> getAllBenefitPlans() throws RemoteException {
        return List.of();
    }

    @Override
    public void enrollInBenefitPlan(int employeeId, int planId) throws RemoteException {
        throw new RemoteException("not yet implemented");
    }
}
