package org.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.SQLException;

public class DatabaseConnectionPool{
    private static HikariDataSource pool;

    private DatabaseConnectionPool(){}

    public static HikariDataSource getPool() throws SQLException {
        if(pool == null){
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:oracle:thin:@//localhost:1523/FREEPDB1");
            config.setUsername("api_test");
            config.setPassword("api_test");
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            pool = new  HikariDataSource(config);
        }
        return pool;
    }
}
