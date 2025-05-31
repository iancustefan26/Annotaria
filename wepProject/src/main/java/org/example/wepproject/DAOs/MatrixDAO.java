package org.example.wepproject.DAOs;

import org.example.wepproject.Models.MatrixCell;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MatrixDAO extends AbstractDAO<MatrixCell, Long> {
    private static final String generateGraph = "{? = call graph_to_cursor(?, ?, ?, ?, ?, ?)}";
    @Override
    protected MatrixCell mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new MatrixCell(
                rs.getInt("row_index"),
                rs.getInt("col_index"),
                rs.getFloat("value")
        );
    }
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
    protected Object[] getInsertParams(MatrixCell entity) {
        return new Object[0];
    }

    @Override
    protected Object[] getUpdateParams(MatrixCell entity) {
        return new Object[0];
    }

    @Override
    protected void setId(MatrixCell entity, Long aLong) {

    }

    @Override
    public MatrixCell findById(Long aLong) {
        return null;
    }

    @Override
    public List<MatrixCell> findAll() {
        return List.of();
    }

    @Override
    public MatrixCell save(MatrixCell entity) {
        return null;
    }

    @Override
    public void update(MatrixCell entity) {

    }

    @Override
    public void deleteById(Long aLong) {

    }
    public List<MatrixCell> getMatrixFromFunction(Long userId, int bestFriends, int randomFriends, Integer category_id, Integer creationYear, Integer tagId) throws SQLException {
        return executePlsqlFunction(generateGraph, userId, bestFriends, randomFriends, category_id, creationYear, tagId);
    }
}
