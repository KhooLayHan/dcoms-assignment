package org.bhel.hrm.server.daos;

import java.util.List;
import java.util.Optional;

public interface DAO<T> {
    Optional<T> find(long id);
    List<T> findAll();

    void save(T t);
    void saveAll();

    int count();

    void update(T t, String[] params);

    void delete(T t);
    void deleteAllById(Iterable<? extends T> ids);

    boolean existsById();
}
