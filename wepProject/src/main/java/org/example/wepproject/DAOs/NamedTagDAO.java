package org.example.wepproject.DAOs;

import org.example.wepproject.Models.NamedTag;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class NamedTagDAO extends AbstractDAO<NamedTag, Long> {
    private static final String TABLE_NAME = "NAMED_TAGS";
    private static final String INSERT_QUERY = "INSERT INTO NAMED_TAGS (name) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE NAMED_TAGS SET name = ? WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT id, name FROM NAMED_TAGS ORDER BY name";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM NAMED_TAGS WHERE id = ?";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM NAMED_TAGS WHERE id = ?";
    private static final String INSERT_NAMED_TAG_FRAME_QUERY = "INSERT INTO NAMED_TAG_FRAMES (named_tag_id, post_id) VALUES (?, ?)";
    private static final String INSERT_USER_TAG_FRAME_QUERY = "INSERT INTO USER_TAG_FRAMES (post_id, user_author_id, user_tagged_id) VALUES (?, ?, ?)";
    private static final String CHECK_NAMED_TAG_EXISTS_QUERY = "SELECT 1 FROM NAMED_TAGS WHERE id = ?";
    private static final String CHECK_POST_EXISTS_QUERY = "SELECT 1 FROM POST WHERE id = ?";
    private static final String CHECK_USER_EXISTS_QUERY = "SELECT 1 FROM USERS WHERE id = ?";
    private static final String CHECK_NAMED_TAG_FRAME_EXISTS_QUERY = "SELECT 1 FROM NAMED_TAG_FRAMES WHERE named_tag_id = ? AND post_id = ?";
    private static final String CHECK_USER_TAG_FRAME_EXISTS_QUERY = "SELECT 1 FROM USER_TAG_FRAMES WHERE post_id = ? AND user_author_id = ? AND user_tagged_id = ?";

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
            List<NamedTag> namedTags = executeQuery(GET_BY_ID_QUERY, id);
            return namedTags.isEmpty() ? null : namedTags.getFirst();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find named tag by ID: " + id, e);
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
            throw new RuntimeException("Failed to save named tag: " + entity, e);
        }
    }

    @Override
    public void update(NamedTag entity) {
        try {
            executeUpdate(getUpdateQuery(), getUpdateParams(entity));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update named tag: " + entity, e);
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            executeUpdate(DELETE_BY_ID_QUERY, id);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete named tag by ID: " + id, e);
        }
    }


    public void addNamedTagFrame(Long namedTagId, Long postId) throws SQLException {
        if (namedTagId == null || postId == null) {
            throw new IllegalArgumentException("namedTagId and postId cannot be null");
        }

        try (Connection conn = getConnection()) {
            // Check if named tag exists
            try (PreparedStatement stmt = conn.prepareStatement(CHECK_NAMED_TAG_EXISTS_QUERY)) {
                stmt.setLong(1, namedTagId);
                if (!stmt.executeQuery().next()) {
                    throw new IllegalArgumentException("Named tag ID " + namedTagId + " does not exist");
                }
            }

            // Check if post exists
            try (PreparedStatement stmt = conn.prepareStatement(CHECK_POST_EXISTS_QUERY)) {
                stmt.setLong(1, postId);
                if (!stmt.executeQuery().next()) {
                    throw new IllegalArgumentException("Post ID " + postId + " does not exist");
                }
            }

            // Check if frame already exists
            try (PreparedStatement stmt = conn.prepareStatement(CHECK_NAMED_TAG_FRAME_EXISTS_QUERY)) {
                stmt.setLong(1, namedTagId);
                stmt.setLong(2, postId);
                if (stmt.executeQuery().next()) {
                    return; // Frame already exists, skip insertion
                }
            }

            // Insert frame
            try (PreparedStatement stmt = conn.prepareStatement(INSERT_NAMED_TAG_FRAME_QUERY)) {
                stmt.setLong(1, namedTagId);
                stmt.setLong(2, postId);
                stmt.executeUpdate();
            }
        }
    }


    public void addUserTagFrame(Long postId, Long userAuthorId, Long userTaggedId) throws SQLException {
        if (postId == null || userAuthorId == null || userTaggedId == null) {
            throw new IllegalArgumentException("postId, userAuthorId, and userTaggedId cannot be null");
        }
        if (userAuthorId.equals(userTaggedId)) {
            throw new IllegalArgumentException("userAuthorId and userTaggedId cannot be the same");
        }

        try (Connection conn = getConnection()) {
            // Check if post exists
            try (PreparedStatement stmt = conn.prepareStatement(CHECK_POST_EXISTS_QUERY)) {
                stmt.setLong(1, postId);
                if (!stmt.executeQuery().next()) {
                    throw new IllegalArgumentException("Post ID " + postId + " does not exist");
                }
            }

            // Check if author user exists
            try (PreparedStatement stmt = conn.prepareStatement(CHECK_USER_EXISTS_QUERY)) {
                stmt.setLong(1, userAuthorId);
                if (!stmt.executeQuery().next()) {
                    throw new IllegalArgumentException("User author ID " + userAuthorId + " does not exist");
                }
            }

            // Check if tagged user exists
            try (PreparedStatement stmt = conn.prepareStatement(CHECK_USER_EXISTS_QUERY)) {
                stmt.setLong(1, userTaggedId);
                if (!stmt.executeQuery().next()) {
                    throw new IllegalArgumentException("User tagged ID " + userTaggedId + " does not exist");
                }
            }

            // Check if frame already exists
            try (PreparedStatement stmt = conn.prepareStatement(CHECK_USER_TAG_FRAME_EXISTS_QUERY)) {
                stmt.setLong(1, postId);
                stmt.setLong(2, userAuthorId);
                stmt.setLong(3, userTaggedId);
                if (stmt.executeQuery().next()) {
                    return; // Frame already exists, skip insertion
                }
            }

            // Insert frame
            try (PreparedStatement stmt = conn.prepareStatement(INSERT_USER_TAG_FRAME_QUERY)) {
                stmt.setLong(1, postId);
                stmt.setLong(2, userAuthorId);
                stmt.setLong(3, userTaggedId);
                stmt.executeUpdate();
            }
        }
    }
}
