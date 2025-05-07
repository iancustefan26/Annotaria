package org.example.webp.Factories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.example.webp.auth.Repositories.UserRepository;

public class RepositoryFactory {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPU");
    private final EntityManager em;

    public RepositoryFactory() {
        this.em = emf.createEntityManager();
    }

    public UserRepository getUserRepository() {
        return new UserRepository(em);
    }

    public EntityManager getEntityManager() {
        return em;
    }

    public void close() {
        if (em.isOpen()) em.close();
        if (emf.isOpen()) emf.close();
    }
}
