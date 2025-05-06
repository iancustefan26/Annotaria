package org.example.webp.Helpers;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

// singleton for creating the connection of the db

public class JpaUtil {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPU");

    private JpaUtil() {}

    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    public static void shutdown() {
        if (emf != null) {
            emf.close();
        }
    }
}

