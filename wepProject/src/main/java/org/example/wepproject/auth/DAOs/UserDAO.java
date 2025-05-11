package org.example.wepproject.auth.DAOs;
import org.example.wepproject.Helpers.AbstractDAO;
import org.example.wepproject.auth.Models.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDAO extends AbstractDAO<User, Long> {
    private static final String TABLE_NAME = "USERS";
    private static final String INSERT_QUERY = "INSERT INTO USERS (username, password_hash, email) VALUES (?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE USERS SET username = ?, password_hash = ?, email = ? WHERE id = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT id, username, password_hash, email FROM USERS WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT id, username, password_hash, email FROM USERS";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM USERS WHERE id = ?";
    private static final String FIND_BY_USERNAME_QUERY = "SELECT * FROM USERS WHERE username = ?";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM USERS WHERE email = ?";


    public User findByEmail(String email) {
        try {
            List<User> users = executeQuery(FIND_BY_EMAIL_QUERY, email);
            return users.isEmpty() ? null : users.getFirst();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user by username: " + email, e);
        }
    }

    public User findByUsername(String username) {
        try {
            List<User> users = executeQuery(FIND_BY_USERNAME_QUERY, username);
            return users.isEmpty() ? null : users.getFirst();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user by username: " + username, e);
        }
    }

    @Override
    public User findById(Long id) {
        try {
            List<User> users = executeQuery(FIND_BY_ID_QUERY, id);
            return users.isEmpty() ? null : users.getFirst();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user by ID: " + id, e);
        }
    }

    @Override
    public List<User> findAll() {
        try {
            return executeQuery(FIND_ALL_QUERY);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all users", e);
        }
    }

    @Override
    public User save(User user) {
        try {
            Long generatedId = executeInsert(getInsertQuery(), getInsertParams(user));
            if (generatedId != null) {
                setId(user, generatedId);
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save user: " + user, e);
        }
    }

    @Override
    public void update(User user) {
        try {
            executeUpdate(getUpdateQuery(), getUpdateParams(user));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update user: " + user, e);
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            executeUpdate(DELETE_BY_ID_QUERY, id);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user by ID: " + id, e);
        }
    }

    @Override
    protected User mapResultSetToEntity(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .username(rs.getString("username"))
                .password(rs.getString("password_hash"))
                .email(rs.getString("email"))
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
    protected Object[] getInsertParams(User user) {
        return new Object[]{user.getUsername(), user.getPassword(), user.getEmail()};
    }

    @Override
    protected Object[] getUpdateParams(User user) {
        return new Object[]{user.getUsername(), user.getPassword(), user.getEmail(), user.getId()};
    }

    @Override
    protected void setId(User user, Long id) {
        user.setId(id);
    }
}
