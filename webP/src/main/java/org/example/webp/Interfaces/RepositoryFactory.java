package org.example.webp.Interfaces;

import org.example.webp.auth.Repositories.UserRepository;

public interface RepositoryFactory {
    UserRepository getUserRepository();
}
