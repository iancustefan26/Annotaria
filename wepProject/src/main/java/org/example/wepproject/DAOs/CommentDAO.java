package org.example.wepproject.DAOs;


import org.example.wepproject.Models.Comment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO extends AbstractDAO<Comment, Long> {
    private static final String TABLE_NAME = "COMMENTS";
    private static final String INSERT_QUERY = "INSERT INTO COMMENTS (post_id, user_id, content, date_posted) VALUES (?, ?, ?, ?)";
    private static final String FIND_BY_POST_ID_QUERY = "SELECT id, post_id, user_id, content, date_posted FROM COMMENTS WHERE post_id = ? ORDER BY date_posted DESC";
    private static final String DELETE_QUERY = "DELETE FROM COMMENTS WHERE id = ?";
    private static final String FIND_USER_ID_FROM_COMMENT_QUERY = "SELECT user_id FROM COMMENTS WHERE id = ?";
    @Override
    protected Comment mapResultSetToEntity(ResultSet rs) throws SQLException {
        return Comment.builder()
                .id(rs.getLong("id"))
                .postId(rs.getLong("post_id"))
                .userId(rs.getLong("user_id"))
                .content(rs.getString("content"))
                .datePosted(rs.getTimestamp("date_posted"))
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
        return null; // Updates not implemented
    }

    @Override
    protected Object[] getInsertParams(Comment comment) {
        return new Object[]{
                comment.getPostId(),
                comment.getUserId(),
                comment.getContent(),
                comment.getDatePosted() != null ? comment.getDatePosted() : new Timestamp(System.currentTimeMillis())
        };
    }

    @Override
    protected Object[] getUpdateParams(Comment comment) {
        return null; // Updates not implemented
    }

    @Override
    protected void setId(Comment comment, Long id) {
        comment.setId(id);
    }

    @Override
    public Comment save(Comment entity) {
        try {
            Long generatedId = executeInsert(getInsertQuery(), getInsertParams(entity));
            if (generatedId != null) {
                setId(entity, generatedId);
            }
            return entity;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save comment: " + entity, e);
        }
    }

    public Long findUserIdFromComment(Long commentId) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_USER_ID_FROM_COMMENT_QUERY)) {

            stmt.setLong(1, commentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("user_id");
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user ID from comment: " + e.getMessage(), e);
        }
    }

    public List<Comment> findByPostId(Long postId) {
        try {
            List<Comment> comments = executeQuery(FIND_BY_POST_ID_QUERY, postId);
            return comments;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to find comments for postId: " + postId, e);
        }
    }

    @Override
    public Comment findById(Long id) {
        return null; // Not implemented
    }

    @Override
    public List<Comment> findAll() {
        return null; // Not implemented
    }

    @Override
    public void deleteById(Long id) {
        try {
            int rowsAffected = executeUpdate(DELETE_QUERY, id);
        } catch (SQLException e) {
            System.out.println("SQLException in deleteById: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to delete comment with id: " + id, e);
        }
    }



    @Override
    public void update(Comment entity) {
        // Not implemented
    }
}