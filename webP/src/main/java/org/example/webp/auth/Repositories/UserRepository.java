package org.example.webp.auth.Repositories;


import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.example.webp.auth.Models.User;
import org.example.webp.Interfaces.CRUDRepository;

import java.util.List;
import java.util.Optional;

public class UserRepository implements CRUDRepository<User, Long> {
    private final EntityManager em;

    public UserRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public User save(User entity) {
        em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();
        return entity;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    @Override
    public List<User> findAll() {
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class);
        return query.getResultList();
    }

    @Override
    public User update(User entity) {
        em.getTransaction().begin();
        User merged = em.merge(entity);
        em.getTransaction().commit();
        return merged;
    }

    @Override
    public void deleteById(Long id) {
        em.getTransaction().begin();
        User u = em.find(User.class, id);
        if (u != null) em.remove(u);
        em.getTransaction().commit();
    }

    public boolean existsByUsername(String username) {
        try{
            em.getTransaction().begin();
            String checkSql = "SELECT COUNT(*) FROM users WHERE username = ?";
            Long count = em.createQuery("select count(u) from User u where u.username =: username", Long.class)
                    .setParameter("username", username)
                    .getSingleResult();
            em.getTransaction().commit();
            return count > 0;
        }catch (Exception e){
            if(em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
    public User findByUsername(String username) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public EntityManager getEm() {
        return em;
    }
}

