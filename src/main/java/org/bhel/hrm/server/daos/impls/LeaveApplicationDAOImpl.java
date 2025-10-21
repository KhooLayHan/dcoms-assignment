package org.bhel.hrm.server.daos.impls;

import org.bhel.hrm.common.dtos.JobOpeningDTO;
import org.bhel.hrm.server.DatabaseManager;
import org.bhel.hrm.server.daos.JobOpeningDAO;
import org.bhel.hrm.server.daos.LeaveApplicationDAO;
import org.bhel.hrm.server.domain.JobOpening;
import org.bhel.hrm.server.domain.LeaveApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class LeaveApplicationDAOImpl implements LeaveApplicationDAO {
    private static final Logger logger = LoggerFactory.getLogger(LeaveApplicationDAOImpl.class);

    private final DatabaseManager databaseManager;

    public LeaveApplicationDAOImpl(DatabaseManager dbManager) {
        this.databaseManager = dbManager;
    }

    @Override
    public List<LeaveApplication> findByEmployeeId(int employeeId) {
        return List.of();
    }

    @Override
    public Optional<LeaveApplication> findById(Integer integer) {
        return Optional.empty();
    }

    @Override
    public List<LeaveApplication> findAll() {
        return List.of();
    }

    @Override
    public void save(LeaveApplication entity) {
    }

    @Override
    public void deleteById(Integer integer) {}

    @Override
    public long count() {
        return 0;
    }
}
