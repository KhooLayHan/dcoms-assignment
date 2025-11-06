package org.bhel.hrm.server;

import org.bhel.hrm.common.dtos.*;
import org.bhel.hrm.common.exceptions.*;
import org.bhel.hrm.common.services.HRMService;
import org.bhel.hrm.server.daos.EmployeeDAO;
import org.bhel.hrm.server.daos.UserDAO;
import org.bhel.hrm.server.domain.Employee;
import org.bhel.hrm.server.domain.User;
import org.bhel.hrm.server.mapper.EmployeeMapper;
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
            throw new AuthenticationException(username, e);
        } catch (DataAccessException e) {
            logger.error("A database error occurred during authentication for user '{}'.", username, e);
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

            try {
                dbManager.rollbackTransaction();
            } catch (Exception rollbackException) {
                logger.error("Rollback failed during exception handling of duplicated user.", rollbackException);
            }

            throw e;
        } catch (DataAccessException e) {
            logger.error("Registration failed for user '{}'. Rolling back transaction.", registrationData.username());

            try {
                dbManager.rollbackTransaction();
            } catch (Exception rollbackException) {
                logger.error("Rollback failed during exception handling of data access layer.", rollbackException);
            }

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
        logger.debug("RMI Call: getAllEmployees() received.");

        try {
            // 1. Fetch all domain objects from DAO.
            List<Employee> employees = employeeDAO.findAll();

            // 2. Map the list of domain objects to a list of DTOs and return.
            return EmployeeMapper.mapToDtoList(employees);
        } catch (DataAccessException e) {
            logger.error("A database error has occurred while fetching all employees.");
            throw new RemoteException("Server error: Could not retrieve employee list.", e);
        }
    }

    @Override
    public EmployeeDTO getEmployeeById(int employeeId) throws RemoteException, ResourceNotFoundException {
        logger.debug("RMI Call: getEmployeeById() for ID: {}", employeeId);

        try {
            // 1. Find the employee by ID. The DAO returns an Optional.
            Employee employee = employeeDAO.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", employeeId));

            // 2. If found, map to a DTO and return.
            return EmployeeMapper.mapToDto(employee);
        } catch (DataAccessException e) {
            logger.error("A database error occurred while fetching employee with ID: {}", employeeId);
            throw new RemoteException("Server error: Could not retrieve employee details.", e);
        }
    }

    @Override
    public void updateEmployeeProfile(EmployeeDTO employeeDTO) throws RemoteException, ResourceNotFoundException, InvalidInputException {
        if (employeeDTO == null)
            throw new InvalidInputException("EmployeeDTO is null");

        logger.info("RMI Call: updateEmployeeProfile() for employee ID: {}", employeeDTO.id());

        try {
            dbManager.beginTransaction();

            // 1. Find the existing employee in the database.
            Employee existingEmployee = employeeDAO.findById(employeeDTO.id())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", employeeDTO.id()));

            // 2. Update the fields of the existing domain object with the new data from the DTO.
            existingEmployee.setFirstName(employeeDTO.firstName());
            existingEmployee.setLastName(employeeDTO.lastName());
            existingEmployee.setIcPassport(employeeDTO.icPassport());

            // 3. Save the modified domain object. The DAO's save() method should perform an UPDATE.
            employeeDAO.save(existingEmployee);

            dbManager.commitTransaction();
            logger.info("Successfully updated profile for employee ID: {}", employeeDTO.id());
        } catch (DataAccessException e) {
            dbManager.rollbackTransaction();

            // 4. Handle the specific case of a duplicate name violation.
            if (e.getCause() instanceof SQLException sqlException && sqlException.getErrorCode() == 1062) {
                logger.warn("Update failed for employee ID {}. An existing employee with the name already exists.", employeeDTO.id());
                throw new InvalidInputException("An employee with this first and last name already exists.");
            }

            // Re-throw as RemoteException to signal failure
            logger.error("Data-access error during profile update for ID: {}", employeeDTO.id(), e);
            throw new RemoteException("Server error: Could not update employee profile.", e);
        } catch (SQLException e) {
            dbManager.rollbackTransaction();

            logger.error("A transaction error occurred during an employee profile update for ID: {}", employeeDTO.id());
            throw new RemoteException("Server transaction failed during profile update.", e);
        } finally {
            if (dbManager.isTransactionActive())
                dbManager.rollbackTransaction();
        }
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
