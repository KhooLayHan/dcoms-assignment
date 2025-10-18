package org.bhel.hrm.server.daos;

import org.bhel.hrm.server.domain.Employee;

import java.util.List;
import java.util.Optional;

public interface IEmployeeDAO {
    void save(Employee employee);

    Optional<Employee> findById(int id);

    List<Employee> findAll();
}
