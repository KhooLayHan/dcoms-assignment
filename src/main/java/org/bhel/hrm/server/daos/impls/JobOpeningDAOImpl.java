package org.bhel.hrm.server.daos.impls;

import org.bhel.hrm.common.dtos.JobOpeningDTO;
import org.bhel.hrm.server.DatabaseManager;
import org.bhel.hrm.server.daos.AbstractDAO;
import org.bhel.hrm.server.daos.JobOpeningDAO;
import org.bhel.hrm.server.domain.JobOpening;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class JobOpeningDAOImpl extends AbstractDAO<JobOpening> implements JobOpeningDAO {
    private static final Logger logger = LoggerFactory.getLogger(JobOpeningDAOImpl.class);

    public JobOpeningDAOImpl(DatabaseManager dbManager) {
        super(dbManager);
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
    public void save(JobOpening entity) {

    }

    @Override
    protected void insert(JobOpening entity) {

    }

    @Override
    protected void update(JobOpening entity) {

    }

    @Override
    protected void setSaveParameters(PreparedStatement stmt, JobOpening entity) throws SQLException {

    }

    @Override
    public void deleteById(Integer integer) {

    }

    @Override
    public long count() {
        return 0;
    }
}
