package org.bhel.hrm.server.daos.impls;

import org.bhel.hrm.server.DatabaseManager;
import org.bhel.hrm.server.daos.EmployeeDAO;
import org.bhel.hrm.server.domain.Employee;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeDAOImpl implements EmployeeDAO {

    public EmployeeDAO(DatabaseManager dbManager) {
    }

    @Override
    public Optional<Employee> findById(int id) {
        // Logic to select an employee by ID.
        return Optional.empty();
    }

    @Override
    public List<Employee> findAll() {
        // Logic to query the DB and builds a List<Employee>. It returns a list of internal Domain objects.
        return new ArrayList<>();
    }

    @Override
    public void save(Employee employee) {
        // Logic to insert the employee into the DB.
    }
}
