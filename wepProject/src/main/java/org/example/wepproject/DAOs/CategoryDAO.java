package org.example.wepproject.DAOs;

import org.example.wepproject.Models.Category;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CategoryDAO extends AbstractDAO<Category, Long> {
    private static final String TABLE_NAME = "CATEGORY";
    private static final String INSERT_QUERY = "INSERT INTO CATEGORY (name) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE CATEGORY SET name = ? WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT id, name FROM CATEGORY ORDER BY name";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM CATEGORY WHERE id = ?";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM CATEGORY WHERE id = ?";

    @Override
    protected Category mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new Category(
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
    protected Object[] getInsertParams(Category category) {
        return new Object[] {
                category.getName()
        };
    }

    @Override
    protected Object[] getUpdateParams(Category category) {
        return new Object[] {
                category.getName(),
                category.getId()
        };
    }

    @Override
    protected void setId(Category category, Long id) {
        category.setId(id);
    }

    public Category findById(Long id) {
        try {
            List<Category> CATEGORY = executeQuery(GET_BY_ID_QUERY, id);
            return CATEGORY.isEmpty() ? null : CATEGORY.getFirst();
        } catch (SQLException e) {
                throw new RuntimeException("Failed to find category by ID: " + id, e);
        }
    }

    @Override
    public List<Category> findAll() {
        try {
            return executeQuery(FIND_ALL_QUERY);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find CATEGORY", e);
        }
    }

    @Override
    public Category save(Category entity) {
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
    public void update(Category entity) {
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

