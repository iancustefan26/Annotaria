package org.example.wepproject.Listeners;
import com.rometools.rome.feed.synd.*;
import com.rometools.rome.io.SyndFeedOutput;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.wepproject.DAOs.StatisticsDAO;
import org.example.wepproject.Helpers.Exporters.StatisticsExporter;
import org.example.wepproject.Models.StatisticRecord;
import org.example.wepproject.Models.StatisticsExportFormat;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class SchedulerStatisticsListener implements ServletContextListener {

    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        StatisticsDAO statisticsDAO = new StatisticsDAO() {
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
            protected Object[] getInsertParams(StatisticRecord entity) {
                return new Object[0];
            }

            @Override
            protected Object[] getUpdateParams(StatisticRecord entity) {
                return new Object[0];
            }

            @Override
            protected void setId(StatisticRecord entity, Long aLong) {

            }

            @Override
            public StatisticRecord findById(Long aLong) {
                return null;
            }

            @Override
            public List<StatisticRecord> findAll() {
                return List.of();
            }

            @Override
            public StatisticRecord save(StatisticRecord entity) {
                return null;
            }

            @Override
            public void update(StatisticRecord entity) {

            }

            @Override
            public void deleteById(Long aLong) {

            }
        };
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Scheduler is running");
            try {
                List<StatisticRecord> records = statisticsDAO.exportFromFunction(null);
                StatisticsExporter.export(StatisticsExportFormat.CSV, records);
                StatisticsExporter.export(StatisticsExportFormat.SVG, records);
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
