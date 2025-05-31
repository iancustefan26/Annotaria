package org.example.wepproject.DAOs;

import org.example.wepproject.Models.NamedTag;
import org.example.wepproject.Models.NamedTag;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class NamedTagDAO extends AbstractDAO<NamedTag,Long> {
    private static final String TABLE_NAME = "NAMED_TAGS";
    private static final String INSERT_QUERY = "INSERT INTO NAMED_TAGS (name) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE NAMED_TAGS SET name = ? WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT id, name FROM NAMED_TAGS ORDER BY name";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM NAMED_TAGS WHERE id = ?";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM NAMED_TAGS WHERE id = ?";

    @Override
    protected NamedTag mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new NamedTag(
                rs.getLong("id"),
                rs.getString("name")
        );
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected String getInsertQuery() {
        return INSERT_QUERY;
    }

    @Override
    protected String getUpdateQuery() {
        return UPDATE_QUERY;
    }

    @Override
    protected Object[] getInsertParams(NamedTag category) {
        return new Object[] {
                category.getName()
        };
    }

    @Override
    protected Object[] getUpdateParams(NamedTag category) {
        return new Object[] {
                category.getName(),
                category.getId()
        };
    }

    @Override
    protected void setId(NamedTag category, Long id) {
        category.setId(id);
    }

    public NamedTag findById(Long id) {
        try {
            List<NamedTag> NAMED_TAGS = executeQuery(GET_BY_ID_QUERY, id);
            return NAMED_TAGS.isEmpty() ? null : NAMED_TAGS.getFirst();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find category by ID: " + id, e);
        }
    }

    @Override
    public List<NamedTag> findAll() {
        try {
            return executeQuery(FIND_ALL_QUERY);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find NAMED_TAGS", e);
        }
    }

    @Override
    public NamedTag save(NamedTag entity) {
        try {
            Long generatedId = executeInsert(getInsertQuery(), getInsertParams(entity));
            if (generatedId != null) {
                setId(entity, generatedId);
            }
            return entity;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save category: " + entity, e);
        }
    }

    @Override
    public void update(NamedTag entity) {
        try {
            executeUpdate(getUpdateQuery(), getUpdateParams(entity));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update category: " + entity, e);
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            executeUpdate(DELETE_BY_ID_QUERY, id);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete category by ID: " + id, e);
        }
    }
}
