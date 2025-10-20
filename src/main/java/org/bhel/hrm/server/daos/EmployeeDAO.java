package org.bhel.hrm.server.daos;

import org.bhel.hrm.server.domain.Employee;

/**
 * Data Access Object interface for Employee entities.
 * Inherits all standard CRUD operations from the generic DAO interface.
 */
public interface EmployeeDAO extends DAO<Employee, Integer> {
    // No custom methods are required for the initial core functionality.
    // Future methods could include:
    // List<Employee> findByDepartment(int departmentId);
}
