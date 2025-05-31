package org.example.wepproject.DAOs;

import org.example.wepproject.Models.MatrixCell;
import org.example.wepproject.Models.StatisticRecord;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class StatisticsDAO extends AbstractDAO<StatisticRecord, Long> {
    private static final String exportStatistics = "{? = call export_statistics(?)}";
    @Override
    protected StatisticRecord mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new StatisticRecord(
                rs.getString("title"),
                rs.getString("owner_name"),
                rs.getInt("score")
        );
    }

    public List<StatisticRecord> exportFromFunction(String latest) throws SQLException {
        return executePlsqlFunction(exportStatistics, latest);
    }
}
