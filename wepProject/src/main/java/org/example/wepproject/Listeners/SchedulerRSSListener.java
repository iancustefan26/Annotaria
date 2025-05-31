package org.example.wepproject.Listeners;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.example.wepproject.DAOs.RssDAO;
import org.example.wepproject.Helpers.Exporters.RssExporter;
import org.example.wepproject.Models.RssRecord;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class SchedulerRSSListener implements ServletContextListener {
    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        RssDAO rssDAO = new RssDAO() {
            @Override
            protected String getTableName() {
                return "";
            }

            @Override
            protected String getInsertQuery() {
                return "";
            }

            @Override
            protected String getUpdateQuery() {
                return "";
            }

            @Override
            protected Object[] getInsertParams(RssRecord entity) {
                return new Object[0];
            }

            @Override
            protected Object[] getUpdateParams(RssRecord entity) {
                return new Object[0];
            }

            @Override
            protected void setId(RssRecord entity, Long aLong) {

            }

            @Override
            public RssRecord findById(Long aLong) {
                return null;
            }

            @Override
            public List<RssRecord> findAll() {
                return List.of();
            }

            @Override
            public RssRecord save(RssRecord entity) {
                return null;
            }

            @Override
            public void update(RssRecord entity) {

            }

            @Override
            public void deleteById(Long aLong) {

            }
        };
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Rss Scheduler is running");
            try {
                List<RssRecord> records = rssDAO.exportFromFunction();
                RssExporter.exportRss(records);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                throw new RuntimeException(e);
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
}
