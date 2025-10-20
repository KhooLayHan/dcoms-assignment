package org.bhel.hrm.server.daos.impls;

import org.bhel.hrm.server.DatabaseManager;
import org.bhel.hrm.server.daos.ApplicantDAO;
import org.bhel.hrm.server.daos.BenefitPlanDAO;
import org.bhel.hrm.server.domain.Applicant;
import org.bhel.hrm.server.domain.BenefitPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class BenefitPlanDAOImpl implements BenefitPlanDAO {
    private static final Logger logger = LoggerFactory.getLogger(BenefitPlanDAOImpl.class);

    private final DatabaseManager databaseManager;

    public BenefitPlanDAOImpl(DatabaseManager dbManager) {
        this.databaseManager = dbManager;
    }

    @Override
    public Optional<BenefitPlan> findById(Integer integer) {
        return Optional.empty();
    }

    @Override
    public List<BenefitPlan> findAll() {
        return List.of();
    }

    @Override
    public BenefitPlan save(BenefitPlan entity) {
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
