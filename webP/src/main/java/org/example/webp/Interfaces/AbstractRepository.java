package org.example.webp.Interfaces;

import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public abstract class AbstractRepository<T, ID> implements CRUDRepository<T, ID> {

    protected final EntityManager em;
    private final Class<T> entityClass;

    public EntityManager getEm() {
        return em;
    }

    public AbstractRepository(EntityManager em, Class<T> entityClass) {
        this.em = em;
        this.entityClass = entityClass;
    }

    @Override
    public T save(T entity) {
        em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();
        return entity;
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(em.find(entityClass, id));
    }

    @Override
    public List<T> findAll() {
        return em.createQuery("FROM " + entityClass.getSimpleName(), entityClass).getResultList();
    }

    @Override
    public T update(T entity) {
        em.getTransaction().begin();
        T merged = em.merge(entity);
        em.getTransaction().commit();
        return merged;
    }

    @Override
    public void deleteById(ID id) {
        em.getTransaction().begin();
        findById(id).ifPresent(em::remove);
        em.getTransaction().commit();
    }
}

