package org.bhel.hrm.server.daos;

import java.util.List;
import java.util.Optional;

public interface DAO<T, ID> {
    Optional<T> findById(ID id);
    List<T> findAll();

    T save(T t);
    void saveAll(Iterable<T> entities);

    int count();

    void update(T entity);

    void delete(T entity);
    void deleteById(ID id);

    boolean existsById(ID id);
}
