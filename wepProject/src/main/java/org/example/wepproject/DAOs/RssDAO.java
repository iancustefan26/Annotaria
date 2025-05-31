package org.example.wepproject.DAOs;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.example.wepproject.Helpers.Exporters.RssExporter;
import org.example.wepproject.Models.RssRecord;
import org.example.wepproject.Models.StatisticRecord;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class RssDAO extends AbstractDAO<RssRecord, Long> {

    private static final String exportStatistics = "{? = call export_rss(?, ?)}";
    @Override
    protected RssRecord mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new RssRecord(
                rs.getString("title"),
                rs.getString("link"),
                rs.getString("description"),
                rs.getTimestamp("pub_date"),
                rs.getInt("guid")
        );
    }

    public List<RssRecord> exportFromFunction() throws SQLException {
        return executePlsqlFunction(exportStatistics, RssExporter.latestPosts, RssExporter.latestComments);
    }
}
