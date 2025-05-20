package org.example.wepproject.Helpers;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;

public class DatabaseConnection {
    private static HikariDataSource pool;

    private DatabaseConnection() {
    }

    public static synchronized HikariDataSource getPool() throws SQLException {
        if (pool == null || pool.isClosed()) {
            HikariConfig config = new HikariConfig();
            config.setDriverClassName("oracle.jdbc.OracleDriver");
            config.setJdbcUrl("jdbc:oracle:thin:@//localhost:1521/FREE");
            config.setUsername("api_test");
            config.setPassword("api_test");

            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setMaxLifetime(1800000);
            config.setConnectionTimeout(30000);
            config.setLeakDetectionThreshold(60000);

            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            pool = new HikariDataSource(config);
        }
        return pool;
    }

    public static void closePool() {
        if (pool != null && !pool.isClosed()) {
            pool.close();
        }
    }
}
