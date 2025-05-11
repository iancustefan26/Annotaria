package org.example.wepproject.Interfaces;

import java.util.List;

public interface CrudDAO<T, ID> {
    T findById(ID id);
    List<T> findAll();
    T save(T entity);
    void update(T entity);
    void deleteById(ID id);
}

