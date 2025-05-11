package org.example.wepproject.Helpers;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.SQLException;

public class DatabaseConnection{
    private static HikariDataSource pool;

    private DatabaseConnection(){}

    public static HikariDataSource getPool() throws SQLException {
        if(pool == null){
            HikariConfig config = new HikariConfig();
            config.setDriverClassName("oracle.jdbc.OracleDriver");
            config.setJdbcUrl("jdbc:oracle:thin:@//localhost:1521/FREE");
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
