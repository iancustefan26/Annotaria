package org.example.webp.auth.Repositories;


import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.example.webp.Interfaces.AbstractRepository;
import org.example.webp.auth.Models.User;

public class UserRepository extends AbstractRepository<User,Long>{
    public UserRepository(EntityManager em) {
        super(em, User.class);
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
}