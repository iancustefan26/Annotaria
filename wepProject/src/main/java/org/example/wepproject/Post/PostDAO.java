package org.example.wepproject.Post;


import org.example.wepproject.Helpers.AbstractDAO;

import java.sql.ResultSet;
import java.sql.SQLException;
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
    private static final String CALL_DELETE_BY_ID = "{ ? = call delete_post_by_id(?) }";
    private static final String CALL_GET_BY_ID = "{ ? = call get_post_by_id(?) }";


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

    @Override
    public List<Post> findAll() {
        return List.of();
    }

    @Override
    public Post save(Post entity) {
        return null;
    }

    @Override
    public void update(Post entity) {

    }

    @Override
    public void deleteById(Long aLong) {

    }
}

