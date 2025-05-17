package org.example.wepproject.Helpers;

import oracle.jdbc.OracleTypes;
import org.example.wepproject.Interfaces.CrudDAO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public abstract class AbstractDAO<T, ID> implements CrudDAO<T, ID> {
    protected Connection getConnection() throws SQLException {
        return DatabaseConnection.getPool().getConnection();
    }

    protected int executeUpdate(String query, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            setParameters(stmt, params);
            return stmt.executeUpdate();
        }
    }

    protected ID executeInsert(String query, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, new String[]{"ID"})) {
            setParameters(stmt, params);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                Object idObj = rs.getObject(1);
                if (idObj instanceof BigDecimal) {
                    return (ID) Long.valueOf(((BigDecimal) idObj).longValue());
                }
                return (ID) idObj;
            }
            return null;
        }
    }

    protected List<T> executeQuery(String query, Object... params) throws SQLException {
        List<T> results = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            setParameters(stmt, params);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(mapResultSetToEntity(rs));
            }
        }
        return results;
    }

    protected List<T> executePlsqlFunction(String call, Object... params) throws SQLException {
        List<T> results = new ArrayList<>();
        try (Connection conn = getConnection();
             CallableStatement stmt = conn.prepareCall(call)) {
            // Register the first parameter as the output cursor
            stmt.registerOutParameter(1, OracleTypes.CURSOR);
            // Set input parameters starting from index 2
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 2, params[i]);
            }
            stmt.execute();
            ResultSet rs = (ResultSet) stmt.getObject(1);
            while (rs != null && rs.next()) {
                results.add(mapResultSetToEntity(rs));
            }
        }
        return results;
    }

    private void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }

    protected abstract T mapResultSetToEntity(ResultSet rs) throws SQLException;
    protected abstract String getTableName();
    protected abstract String getInsertQuery();
    protected abstract String getUpdateQuery();
    protected abstract Object[] getInsertParams(T entity);
    protected abstract Object[] getUpdateParams(T entity);
    protected abstract void setId(T entity, ID id);
}