package org.bhel.hrm.server;

import org.bhel.hrm.common.dtos.*;
import org.bhel.hrm.common.services.HRMService;
import org.bhel.hrm.server.daos.EmployeeDAO;
import org.bhel.hrm.server.daos.UserDAO;
import org.bhel.hrm.server.domain.Employee;
import org.bhel.hrm.server.domain.User;
import org.bhel.hrm.server.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Optional;

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
    public UserDTO authenticateUser(String username, String password) throws RemoteException {
        logger.info("Authentication attempt for user: {}.", username);

        Optional<User> userOpt = userDAO.findByUsername(username);
        if (userOpt.isEmpty()) {
            logger.warn("Authentication failed: User '{}' not found.", username);
            return null;
        }

        User user = userOpt.get();
        boolean passwordMatches = PasswordService.checkPassword(password, user.getPasswordHash());

        if (passwordMatches) {
            logger.info("User '{}' authenticated successfully.", username);
            return UserMapper.mapToDto(user);
        } else {
            logger.warn("Authentication failed: Invalid password for user '{}'.", username);
            return null;
        }
    }

    @Override
    public void registerNewEmployee(NewEmployeeRegistrationDTO registrationData) throws RemoteException {
        logger.info("Attempting to register new employee: {}.", registrationData.username());

        try {
            // 1. Starts the transaction
            dbManager.beginTransaction();

            // 2. Create and save the User domain object
            User newUser = new User(
                registrationData.username(),
                PasswordService.hashPassword(registrationData.initialPassword()),
                registrationData.role()
            );
            userDAO.save(newUser);

            // 3. Create and save the Employee domain object, linking it to the new User
            Employee newEmployee = new Employee(
                newUser.getId(), // Uses the ID generated from the new User saved
                registrationData.firstName(),
                registrationData.lastName(),
                registrationData.icPassport()
            );
            employeeDAO.save(newEmployee);

            // 4. If both operations succeed, commit the transaction
            dbManager.commitTransaction();
            logger.info("Successfully registered the new Employee {} with user ID {}.", newEmployee.getFirstName(), newEmployee.getId());
        } catch (Exception e) {
            // 5. If any exceptions occurred, roll back the entire transaction
            logger.error("Registration failed for user '{}'. Rolling back transaction.", registrationData.username());

            dbManager.rollbackTransaction();
            throw new RemoteException("Employee registration failed due to a server error.");
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
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public void applyForLeave(LeaveApplicationDTO leaveApplicationDTO) throws RemoteException {
        throw new UnsupportedOperationException("not yet implemented");
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
        throw new UnsupportedOperationException("not yet implemented");
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
        throw new UnsupportedOperationException("not yet implemented");
    }
}
