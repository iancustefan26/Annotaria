package org.example.wepproject.DAOs;
import org.example.wepproject.Exceptions.UserNotFoundException;
import org.example.wepproject.Models.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import java.util.regex.Pattern;

public class UserDAO extends AbstractDAO<User, Long> {
    private static final String TABLE_NAME = "USERS";
    private static final String INSERT_QUERY = "INSERT INTO USERS (username, password_hash, email) VALUES (?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE USERS SET username = ?, password_hash = ?, email = ? WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT id, username, password_hash, email FROM USERS";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM USERS WHERE id = ?";
    private static final String CALL_DELETE_USER_BY_ID = "{call delete_user_by_id(?)}";
    private static final String CALL_GET_USER_BY_USERNAME = "{? = call get_user_by_username(?)}";
    private static final String CALL_GET_USER_BY_ID = "{? = call get_user_by_id(?)}";
    private static final String CALL_GET_USER_BY_EMAIL = "{? = call get_user_by_email(?)}";

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{3,50}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final int MAX_USERNAME_LENGTH = 50;
    private static final int MAX_EMAIL_LENGTH = 100;

    public User findByUsername(String username) {
        if (username == null || username.trim().isEmpty() || username.length() > MAX_USERNAME_LENGTH || !USERNAME_PATTERN.matcher(username).matches()) {
            throw new IllegalArgumentException("Invalid username format");
        }
        try {
            List<User> users = executePlsqlFunction(CALL_GET_USER_BY_USERNAME, username.trim());
            return users.isEmpty() ? null : users.getFirst();
        } catch (SQLException e) {
            if (e.getErrorCode() == 20002) {
                throw new UserNotFoundException("User with username " + username + " not found", e);
            } else {
                throw new RuntimeException("Failed to find user by username: " + username, e);
            }
        }
    }

    @Override
    public User findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        try {
            List<User> users = executePlsqlFunction(CALL_GET_USER_BY_ID, id);
            return users.isEmpty() ? null : users.getFirst();
        } catch (SQLException e) {
            if (e.getErrorCode() == 20002) {
                throw new UserNotFoundException("User with ID " + id + " not found", e);
            } else {
                throw new RuntimeException("Failed to find user by ID: " + id, e);
            }
        }
    }

    public User findByEmail(String email) {
        if (email == null || email.trim().isEmpty() || email.length() > MAX_EMAIL_LENGTH || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        try {
            List<User> users = executePlsqlFunction(CALL_GET_USER_BY_EMAIL, email.trim());
            return users.isEmpty() ? null : users.getFirst();
        } catch (SQLException e) {
            if (e.getErrorCode() == 20002) {
                throw new UserNotFoundException("User with email " + email + " not found", e);
            } else {
                throw new RuntimeException("Failed to find user by email: " + email, e);
            }
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
        validateUser(user);
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
        validateUser(user);
        try {
            executeUpdate(getUpdateQuery(), getUpdateParams(user));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update user: " + user, e);
        }
    }

    @Override
    public void deleteById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        try {
            executeUpdate(DELETE_BY_ID_QUERY, id);
        } catch (SQLException e) {
            if (e.getErrorCode() == 20002) {
                throw new UserNotFoundException("User with ID " + id + " not found", e);
            } else {
                throw new RuntimeException("Failed to delete user with ID: " + id, e);
            }
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

    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getUsername() == null || user.getUsername().trim().isEmpty() || user.getUsername().length() > MAX_USERNAME_LENGTH || !USERNAME_PATTERN.matcher(user.getUsername()).matches()) {
            throw new IllegalArgumentException("Invalid username format");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty() || user.getEmail().length() > MAX_EMAIL_LENGTH || !EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
    }
}
