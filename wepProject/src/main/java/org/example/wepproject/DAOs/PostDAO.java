package org.example.wepproject.DAOs;


import org.example.wepproject.Models.Post;
import org.example.wepproject.Exceptions.PostNotFoundException;

import javax.sql.rowset.serial.SerialBlob;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class PostDAO extends AbstractDAO<Post,Long> {
    private static final String TABLE_NAME = "POST";
    private static final String INSERT_QUERY = "INSERT INTO POST " +
            "(author_id, category_id, media_blob, external_media_url, " +
            "creation_year, date_posted, description, likes_count, comments_count) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE POST SET author_id = ?, category_id = ?, media_blob = ?," +
            " external_media_url = ?, creation_year = ?, date_posted = ?, description = ?, " +
            "likes_count = ?, comments_count = ? WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT id, author_id, category_id, media_blob, external_media_url," +
            " creation_year, date_posted, description, " +
            "likes_count, comments_count FROM POST";
    private static final String CALL_DELETE_BY_ID = "{call delete_post_by_id(?) }";
    private static final String CALL_GET_BY_ID = "{ ? = call get_post_by_id(?) }";
    private static final String CALL_GET_BY_CATEGORY_ID = "{ ? = call get_post_by_category_id(?) }";
    private static final String CALL_GET_BY_USER_ID = "{ ? = call get_post_by_user_id(?) }";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM POST WHERE id = ?";
    private static final String CALL_GET_SAVED_POSTS_BY_USER_ID = "{ ? = call get_saved_posts_by_user_id(?) }";
    private static final String IS_SAVED_POST_BY_USER_ID = "SELECT COUNT(*) FROM saved_posts WHERE user_id = ? AND post_id = ?";



    @Override
    protected Post mapResultSetToEntity(ResultSet rs) throws SQLException {
        return Post.builder()
                .id(rs.getLong("id"))
                .authorId(rs.getLong("author_id"))
                .categoryId(rs.getLong("category_id") != 0 ? rs.getLong("category_id") : null)
                .mediaBlob(rs.getBlob("media_blob"))
                .externalMediaUrl(rs.getString("external_media_url"))
                .creationYear(rs.getInt("creation_year") != 0 ? rs.getInt("creation_year") : null)
                .datePosted(rs.getTimestamp("date_posted"))
                .description(rs.getString("description"))
                .likesCount(rs.getInt("likes_count"))
                .commentsCount(rs.getInt("comments_count"))
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
        return UPDATE_QUERY;
    }

    @Override
    protected Object[] getInsertParams(Post post) {
        return new Object[]{
                post.getAuthorId(),
                post.getCategoryId(),
                post.getMediaBlob(),
                post.getExternalMediaUrl(),
                post.getCreationYear(),
                post.getDatePosted(),
                post.getDescription(),
                post.getLikesCount(),
                post.getCommentsCount()
        };
    }

    @Override
    protected Object[] getUpdateParams(Post post) {
        return new Object[]{
                post.getAuthorId(),
                post.getCategoryId(),
                post.getMediaBlob(),
                post.getExternalMediaUrl(),
                post.getCreationYear(),
                post.getDatePosted(),
                post.getDescription(),
                post.getLikesCount(),
                post.getCommentsCount(),
                post.getId()
        };
    }


    @Override
    protected void setId(Post post, Long id) {
        post.setId(id);
    }

    public Post findById(Long id) {
        try {
            List<Post> posts = executePlsqlFunction(CALL_GET_BY_ID, id);
            return posts.isEmpty() ? null : posts.getFirst();
        } catch (SQLException e) {
            if (e.getErrorCode() == 20003) {
                throw new PostNotFoundException("Post with ID " + id + " not found", e);
            } else {
                throw new RuntimeException("Failed to find post by ID: " + id, e);
            }
        }
    }
    public boolean isSavedPostById(Long userId, Long postId) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(IS_SAVED_POST_BY_USER_ID)) {
            stmt.setLong(1, userId);
            stmt.setLong(2, postId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        }catch (SQLException e){
           throw new RuntimeException("Failed to find post by ID: " + postId, e);
        }
    }

    public boolean isPostLiked(Long userId, Long postId) {
        String query = "SELECT COUNT(*) AS count FROM likes WHERE user_id = ? AND post_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, userId);
            stmt.setLong(2, postId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
                return false;
            }
        }catch (SQLException e){
            throw new RuntimeException("Failed to find post by ID: " + postId, e);
        }
    }

    public void deleteByIdWithQuerry(Long id){
        try{
            int rowNum = executeUpdate(DELETE_BY_ID_QUERY, id);
        }catch (SQLException e){
            throw new RuntimeException("Failed to delete post with ID: " + id, e);
        }
    }

    public List<Post> findByCategoryId(Long categoryId) {
        try{
            List<Post> posts = executePlsqlFunction(CALL_GET_BY_CATEGORY_ID, categoryId);
            return posts.isEmpty() ? null : posts;
        }catch (SQLException e){
            if(e.getErrorCode() == 20003) {
                throw new PostNotFoundException("Post with ID " + categoryId + " not found", e);
            }else{
                throw new RuntimeException("Failed to find post by ID: " + categoryId, e);
            }
        }
    }

    public List<Post> findByUserId(Long userId) {
        try{
            List<Post> posts = executePlsqlFunction(CALL_GET_BY_USER_ID, userId);
            return posts.isEmpty() ? null : posts;
        }catch (SQLException e){
            System.out.println("SqlException: " + e.getErrorCode());
            if(e.getErrorCode() == 20003) {
                throw new PostNotFoundException("Post with ID " + userId + " not found", e);
            }else{
                throw new RuntimeException("Failed to find post by ID: " + userId, e);
            }
        }
    }

    @Override
    public void deleteById(Long id) {
       try{
           executeSqlFunctionNoReturn(CALL_DELETE_BY_ID, id);
       }catch (SQLException e){
           if(e.getErrorCode() == 20003) {
               throw new PostNotFoundException("Post with ID " + id + " not found", e);
           }else{
               System.out.println(e.getMessage());
               throw new RuntimeException("Failed to delete post by ID: " + id, e);
           }
       }
    }

    @Override
    public List<Post> findAll() {
        try{
           return executeQuery(FIND_ALL_QUERY);
        }catch (SQLException e){
            throw new RuntimeException("Failed to find posts", e);
        }
    }

    public List<Post> findAllSavedPostsByUserId(Long userId) {
        try{
            List<Post> posts = executePlsqlFunction(CALL_GET_SAVED_POSTS_BY_USER_ID, userId);
            return posts.isEmpty() ? null : posts;
        }catch (SQLException e){
           if(e.getErrorCode() == 20003) {
               throw new PostNotFoundException("No posts found", e);
           }else{
               System.out.println(e.getMessage());
               throw new RuntimeException("Failed to retrieve saved photos of the user " + userId , e);
           }
        }
    }

    @Override
    public Post save(Post entity) {
        try {
            Long generatedId = executeInsert(getInsertQuery(), getInsertParams(entity));
            if (generatedId != null) {
                setId(entity, generatedId);
            }
            return entity;
        } catch (SQLException e) {
            System.out.println("SQLException in save: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to save post: " + entity, e);
        }
    }

    @Override
    public void update(Post entity) {
        try{
            executeUpdate(getUpdateQuery(),getUpdateParams(entity));
        }catch (SQLException e){
            throw new RuntimeException("Failed to update post: " + entity, e);
        }
    }
    @Override
    protected Long executeInsert(String query, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, new String[]{"ID"})) {
            stmt.setLong(1, (Long) params[0]); // author_id
            if (params[1] != null) {
                stmt.setLong(2, (Long) params[1]); // category_id
            } else {
                stmt.setNull(2, Types.NUMERIC);
            }
            if (params[2] != null) {
                SerialBlob blob = (SerialBlob) params[2];
                stmt.setBinaryStream(3, blob.getBinaryStream(), blob.length());
            } else {
                stmt.setNull(3, Types.BLOB);
            }
            if (params[3] != null) {
                stmt.setString(4, (String) params[3]); // external_media_url
            } else {
                stmt.setNull(4, Types.VARCHAR);
            }
            if (params[4] != null) {
                stmt.setInt(5, (Integer) params[4]); // creation_year
            } else {
                stmt.setNull(5, Types.NUMERIC);
            }
            stmt.setTimestamp(6, (Timestamp) params[5]); // date_posted
            if (params[6] != null) {
                stmt.setString(7, (String) params[6]); // description
            } else {
                stmt.setNull(7, Types.VARCHAR);
            }
            stmt.setInt(8, (Integer) params[7]); // likes_count
            stmt.setInt(9, (Integer) params[8]); // comments_count

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                Object idObj = rs.getObject(1);
                if (idObj instanceof BigDecimal) {
                    return ((BigDecimal) idObj).longValue();
                }
                return (Long) idObj;
            }
            return null;
        }
    }

    public void savePost(Long userId, Long postId) throws SQLException {
        String query = "INSERT INTO saved_posts (user_id, post_id) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, userId);
            stmt.setLong(2, postId);
            stmt.executeUpdate();
        }
    }

    public void unsavePost(Long userId, Long postId) throws SQLException {
        String query = "DELETE FROM saved_posts WHERE user_id = ? AND post_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, userId);
            stmt.setLong(2, postId);
            stmt.executeUpdate();
        }
    }

    public boolean existsById(Long postId) throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM post WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, postId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
                return false;
            }
        }
    }
}

