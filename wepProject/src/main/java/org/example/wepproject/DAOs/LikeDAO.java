package org.example.wepproject.DAOs;


import org.example.wepproject.Models.Like;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import java.util.List;

public class LikeDAO extends AbstractDAO<Like, Long> {
    private static final String TABLE_NAME = "LIKES";
    private static final String INSERT_QUERY = "INSERT INTO LIKES (user_id, post_id) VALUES (?, ?)";
    private static final String FIND_BY_USER_AND_POST_QUERY = "SELECT id, user_id, post_id FROM LIKES WHERE user_id = ? AND post_id = ?";
    private static final String FIND_BY_POST_ID_QUERY = "SELECT id, user_id, post_id FROM LIKES WHERE post_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM LIKES WHERE id = ?";

    @Override
    protected Like mapResultSetToEntity(ResultSet rs) throws SQLException {
        return Like.builder()
                .id(rs.getLong("id"))
                .userId(rs.getLong("user_id"))
                .postId(rs.getLong("post_id"))
                .build();
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
        return null;
    }

    @Override
    protected Object[] getInsertParams(Like like) {
        return new Object[]{
                like.getUserId(),
                like.getPostId()
        };
    }

    @Override
    protected Object[] getUpdateParams(Like like) {
        return null;
    }

    @Override
    protected void setId(Like like, Long id) {
        like.setId(id);
    }

    @Override
    public Like save(Like entity) {
        try {
            Long generatedId = executeInsert(getInsertQuery(), getInsertParams(entity));
            if (generatedId != null) {
                setId(entity, generatedId);
            }
            return entity;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save like: " + entity, e);
        }
    }
    public List<Like> findByPostId(Long postId) {
        try {
            List<Like> likes = executeQuery(FIND_BY_POST_ID_QUERY, postId);
            return likes;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to find likes for postId: " + postId, e);
        }
    }
    public Like findByUserIdAndPostId(Long userId, Long postId) {
        try {
            List<Like> results = executeQuery(FIND_BY_USER_AND_POST_QUERY, userId, postId);
            if (!results.isEmpty()) {
                Like like = results.get(0);
                System.out.println("Found like: id=" + like.getId() + ", userId=" + like.getUserId() + ", postId=" + like.getPostId());
                return like;
            }
            return null;
        } catch (SQLException e) {
            System.out.println("SQLException in findByUserIdAndPostId: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to find like for userId: " + userId + ", postId: " + postId, e);
        }
    }

    @Override
    public Like findById(Long id) {
        return null; // Not implemented
    }

    @Override
    public List<Like> findAll() {
        return null; // Not implemented
    }

    @Override
    public void deleteById(Long id) {
        try {
            int rowsAffected = executeUpdate(DELETE_QUERY, id);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to delete like with id: " + id, e);
        }
    }

    @Override
    public void update(Like entity) {
        // Not implemented
    }
}