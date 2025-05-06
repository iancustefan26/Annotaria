package org.example.webp.Helpers;

import jakarta.persistence.EntityManager;
import org.example.webp.Interfaces.RepositoryFactory;
import org.example.webp.auth.Repositories.UserRepository;

public class JpaRepositoryFactory implements RepositoryFactory {
    private final EntityManager em;

    public JpaRepositoryFactory(EntityManager em) {
        this.em = em;
    }

    @Override
    public UserRepository getUserRepository() {
        return new UserRepository(em);
    }

    // here more getRepository
}
