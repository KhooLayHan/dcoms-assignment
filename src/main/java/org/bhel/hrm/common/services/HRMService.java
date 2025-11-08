package org.bhel.hrm.common.services;

import org.bhel.hrm.common.dtos.*;
import org.bhel.hrm.common.exceptions.AuthenticationException;
import org.bhel.hrm.common.exceptions.HRMException;
import org.bhel.hrm.common.exceptions.ResourceNotFoundException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * The remote service interface for the BHEL Human Resource Management system.
 * This interface defines the contract between the client and the server, specifying
 * all available remote operations. All data transfers are handled via DTOs.
 */
public interface HRMService extends Remote {
    // A unique name to reference the service from the RMI registry.
    String SERVICE_NAME = "HRMService";

    // --- 1. Authentication & User Management ---

    /**
     * Authenticates a user with their credentials.
     *
     * @param username The user's username.
     * @param password The user's raw password.
     * @return A UserDTO if authentication is successful
     * @throws AuthenticationException on authentication failure.
     * @throws RemoteException if a communication-related error occurs.
     */
    UserDTO authenticateUser(String username, String password) throws RemoteException, HRMException;

    // --- 2. Employee Management (Primarily for HR Staff) ---

    /**
     * Registers a new employee, creating their user account and profile in one atomic operation.
     *
     * @param registrationData A DTO containing all required details for the new user and employee.
     * @throws RemoteException if registration fails (e.g., username already exists) or a communication error occurs.
     */
    void registerNewEmployee(NewEmployeeRegistrationDTO registrationData) throws RemoteException, HRMException;

    /**
     * Retrieves a list of all employees in the system.
     *
     * @return A List of EmployeeDTOs.
     * @throws RemoteException if a communication-related error occurs.
     */
    List<EmployeeDTO> getAllEmployees() throws RemoteException, HRMException;

    /**
     * Retrieves the full profile details for a single employee.
     *
     * @param employeeId The ID of the employee to fetch.
     * @return An EmployeeDTO containing the employee's details.
     * @throws RemoteException if the employee is not found or a communication error occurs.
     * @throws ResourceNotFoundException if the employee resource is not found.
     */
    EmployeeDTO getEmployeeById(int employeeId) throws RemoteException, HRMException;

    /**
     * Updates the profile information for an existing employee.
     *
     * @param employeeDTO The DTO containing the updated information. The ID must be valid.
     * @throws RemoteException if the update fails or a communication error occurs.
     */
    void updateEmployeeProfile(EmployeeDTO employeeDTO) throws RemoteException, HRMException;

    // --- 3. Leave Management (For Employees and HR) ---

    /**
     * Submits a new leave application for an employee.
     *
     * @param leaveApplicationDTO The DTO containing the details of the leave request.
     * @throws RemoteException if the application is invalid or a communication error occurs.
     */
    void applyForLeave(LeaveApplicationDTO leaveApplicationDTO) throws RemoteException;

    /**
     * Retrieves the leave history for a specific employee.
     *
     * @param employeeId The ID of the employee whose leave history is being requested.
     * @return A List of the employee's LeaveApplicationDTOs.
     * @throws RemoteException if a communication-related error occurs.
     */
    List<LeaveApplicationDTO> getLeaveHistoryForEmployees(int employeeId) throws RemoteException;

    // --- 4. Training Management (For Employees and HR) ---

    /**
     * Retrieves a list of all available training courses.
     *
     * @return A List of TrainingCourseDTOs.
     * @throws RemoteException if a communication-related error occurs.
     */
    List<TrainingCourseDTO> getAllTrainingCourses() throws RemoteException;

    /**
     * Enrolls an employee in a specific training course.
     *
     * @param employeeId The ID of the employee to enroll.
     * @param courseId The ID of the course to enroll in.
     * @throws RemoteException if enrollment fails (e.g., course is full) or a communication error occurs.
     */
    void enrollInTraining(int employeeId, int courseId) throws RemoteException;

    // --- 5. Recruitment Management (Primarily for HR Staff) ---

    /**
     * Retrieves a list of all open job positions.
     *
     * @return A List of JobOpeningDTOs.
     * @throws RemoteException if a communication-related error occurs.
     */
    List<JobOpeningDTO> getAllJobOpenings() throws RemoteException;

    /**
     * Retrieves all applicants for a specific job opening.
     *
     * @param jobOpeningId The ID of the job opening.
     * @return A List of ApplicantDTOs for that job.
     * @throws RemoteException if a communication-related error occurs.
     */
    List<ApplicantDTO> getApplicantsForJob(int jobOpeningId) throws RemoteException;

    // --- 6. Benefits Management (For Employees and HR) ---
    /**
     * Retrieves a list of all benefit plans offered by the company.
     *
     * @return A List of BenefitPlanDTOs.
     * @throws RemoteException if a communication-related error occurs.
     */
    List<BenefitPlanDTO> getAllBenefitPlans() throws RemoteException;

    /**
     * Enrolls an employee in a specific benefit plan.
     *
     * @param employeeId The ID of the employee to enroll.
     * @param planId The ID of the benefit plan to enroll in.
     * @throws RemoteException if enrollment fails or a communication error occurs.
     */
    void enrollInBenefitPlan(int employeeId, int planId) throws RemoteException;
}
