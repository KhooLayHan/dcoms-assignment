package org.bhel.hrm.server.daos.impls;

import org.bhel.hrm.server.DatabaseManager;
import org.bhel.hrm.server.daos.ApplicantDAO;
import org.bhel.hrm.server.domain.Applicant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class ApplicantDAOImpl implements ApplicantDAO {
    private static final Logger logger = LoggerFactory.getLogger(ApplicantDAOImpl.class);

    private final DatabaseManager databaseManager;

    public ApplicantDAOImpl(DatabaseManager dbManager) {
        this.databaseManager = dbManager;
    }

    @Override
    public List<Applicant> findByJobOpeningId(int jobOpeningId) {
        return List.of();
    }

    @Override
    public Optional<Applicant> findById(Integer integer) {
        return Optional.empty();
    }

    @Override
    public List<Applicant> findAll() {
        return List.of();
    }

    @Override
    public Applicant save(Applicant entity) {
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
