package org.bhel.hrm.server.daos.impls;

import org.bhel.hrm.server.DatabaseManager;
import org.bhel.hrm.server.daos.LeaveApplicationDAO;
import org.bhel.hrm.server.daos.TrainingCourseDAO;
import org.bhel.hrm.server.domain.LeaveApplication;
import org.bhel.hrm.server.domain.TrainingCourse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class TrainingCourseDAOImpl implements TrainingCourseDAO {
    private static final Logger logger = LoggerFactory.getLogger(TrainingCourseDAOImpl.class);

    private final DatabaseManager databaseManager;

    public TrainingCourseDAOImpl(DatabaseManager dbManager) {
        this.databaseManager = dbManager;
    }

    @Override
    public Optional<TrainingCourse> findById(Integer integer) {
        return Optional.empty();
    }

    @Override
    public List<TrainingCourse> findAll() {
        return List.of();
    }

    @Override
    public TrainingCourse save(TrainingCourse entity) {
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
