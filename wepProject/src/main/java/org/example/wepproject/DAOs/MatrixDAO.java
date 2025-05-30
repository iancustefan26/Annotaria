package org.example.wepproject.DAOs;

import org.example.wepproject.Models.MatrixCell;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class MatrixDAO extends AbstractDAO<MatrixCell, Long> {
    private static final String generateGraph = "{? = call graph_to_cursor(?, ?, ?, ?)}";
    @Override
    protected MatrixCell mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new MatrixCell(
                rs.getInt("row_index"),
                rs.getInt("col_index"),
                rs.getFloat("value")
        );
    }

    public List<MatrixCell> getMatrixFromFunction(Long userId, int bestFriends, int randomFriends, Integer category_id) throws SQLException {
        return executePlsqlFunction(generateGraph, userId, bestFriends, randomFriends, category_id);
    }
}
