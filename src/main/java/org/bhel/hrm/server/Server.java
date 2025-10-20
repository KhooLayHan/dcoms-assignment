package org.bhel.hrm.server;

import org.bhel.hrm.common.dtos.EmployeeDTO;
import org.bhel.hrm.common.services.IService;
import org.bhel.hrm.server.daos.EmployeeDAO;
import org.bhel.hrm.server.domain.Employee;
import org.bhel.hrm.server.mapper.EmployeeMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class Server extends UnicastRemoteObject implements IService {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    protected Server(EmployeeDAO employeeDAO) throws RemoteException {
        super();
        this.employeeDAO = employeeDAO;
    }

    private final EmployeeDAO employeeDAO;

    @Override
    public List<EmployeeDTO> getAllEmployees() throws RemoteException {
        logger.info("RMI Call: getAllEmployees received.");
        List<Employee> employees = employeeDAO.findAll();
        return EmployeeMapper.mapToDtoList(employees);
    }

    @Override
    public void registerEmployee(EmployeeDTO employeeDTO) {
        logger.info("RMI Call: registerEmployee for '{}' received.", employeeDTO.firstName());
        Employee employee = EmployeeMapper.mapToDomain(employeeDTO);
        employeeDAO.save(employee);
    }

    @Override
    public boolean isUserAuthenticated(String username, String password) throws RemoteException {
        return false;
    }
}
