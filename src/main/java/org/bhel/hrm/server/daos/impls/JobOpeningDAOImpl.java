package org.bhel.hrm.server.daos.impls;

import org.bhel.hrm.common.dtos.JobOpeningDTO;
import org.bhel.hrm.common.dtos.UserDTO;
import org.bhel.hrm.server.DatabaseManager;
import org.bhel.hrm.server.daos.JobOpeningDAO;
import org.bhel.hrm.server.daos.UserDAO;
import org.bhel.hrm.server.domain.JobOpening;
import org.bhel.hrm.server.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class JobOpeningDAOImpl implements JobOpeningDAO {
    private static final Logger logger = LoggerFactory.getLogger(JobOpeningDAOImpl.class);

    private final DatabaseManager databaseManager;

    public JobOpeningDAOImpl(DatabaseManager dbManager) {
        this.databaseManager = dbManager;
    }

    @Override
    public List<JobOpening> findAllByStatus(JobOpeningDTO.JobStatus status) {
        return List.of();
    }

    @Override
    public Optional<JobOpening> findById(Integer integer) {
        return Optional.empty();
    }

    @Override
    public List<JobOpening> findAll() {
        return List.of();
    }

    @Override
    public JobOpening save(JobOpening entity) {
        return null;
    }

    @Override
    public void deleteById(Integer integer) {

    }

    @Override
    public int count() {
        return 0;
    }
}
