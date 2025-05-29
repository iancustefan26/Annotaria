package org.example.wepproject.DAOs;
import org.example.wepproject.Exceptions.UserNotFoundException;
import org.example.wepproject.Models.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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

    public User findByUsername(String username) {
        try {
            List<User> users = executePlsqlFunction(CALL_GET_USER_BY_USERNAME, username);
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
        try {
            List<User> users = executePlsqlFunction(CALL_GET_USER_BY_EMAIL, email);
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
        try{
            int rownum = executeUpdate(DELETE_BY_ID_QUERY, id);
            //executeSqlFunctionNoReturn(CALL_DELETE_USER_BY_ID, id);
        }catch (SQLException e){
            System.out.println(e.getErrorCode() + " " + e.getMessage()) ;
            if (e.getErrorCode() == 20002) {
                throw new UserNotFoundException("User with ID " + id + " not found", e);
            }else{
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
}

