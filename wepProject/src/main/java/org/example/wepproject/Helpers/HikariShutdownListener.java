package org.example.wepproject.Helpers;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class HikariShutdownListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("WebApp started: Connection pool will be initialized on first use");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DatabaseConnection.closePool();
        System.out.println("WebApp shutting down: Connection pool closed");
    }
}