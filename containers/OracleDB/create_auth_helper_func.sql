-- get user by username
CREATE OR REPLACE FUNCTION get_user_by_username(p_username IN VARCHAR2)
RETURN SYS_REFCURSOR
AS
    l_cursor SYS_REFCURSOR;
    l_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO l_count
    FROM USERS
    WHERE username = p_username;

    IF l_count = 0 THEN
        RAISE auth_exceptions.user_not_found;
    END IF;

    OPEN l_cursor FOR
        SELECT id, username, password_hash, email
        FROM USERS
        WHERE username = p_username;

    RETURN l_cursor;

EXCEPTION
    WHEN auth_exceptions.user_not_found THEN
        RAISE_APPLICATION_ERROR(-20002, 'User with username ' || p_username || ' not found');
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20003, 'Error retrieving user by username: ' || SQLERRM);
END get_user_by_username;
/

-- get user by ID
CREATE OR REPLACE FUNCTION get_user_by_id(p_id IN NUMBER)
RETURN SYS_REFCURSOR
AS
    l_cursor SYS_REFCURSOR;
    l_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO l_count FROM USERS WHERE id = p_id;

    IF l_count = 0 THEN
        RAISE auth_exceptions.user_not_found;
    END IF;

    OPEN l_cursor FOR
        SELECT id, username, password_hash, email
        FROM USERS
        WHERE id = p_id;

    RETURN l_cursor;

EXCEPTION
    WHEN auth_exceptions.user_not_found THEN
        RAISE_APPLICATION_ERROR(-20002, 'User with ID ' || p_id || ' not found');
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20003, 'Error retrieving user by ID: ' || SQLERRM);
END get_user_by_id;
/

-- get user by email
CREATE OR REPLACE FUNCTION get_user_by_email(p_email IN VARCHAR2)
RETURN SYS_REFCURSOR
AS
    l_cursor SYS_REFCURSOR;
    l_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO l_count FROM USERS WHERE email = p_email;

    IF l_count = 0 THEN
        RAISE auth_exceptions.user_not_found;
    END IF;

    OPEN l_cursor FOR
        SELECT id, username, password_hash, email
        FROM USERS
        WHERE email = p_email;

    RETURN l_cursor;

EXCEPTION
    WHEN auth_exceptions.user_not_found THEN
        RAISE_APPLICATION_ERROR(-20002, 'User with email ' || p_email || ' not found');
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20003, 'Error retrieving user by email: ' || SQLERRM);
END get_user_by_email;
/


CREATE OR REPLACE PROCEDURE delete_user_by_id(p_id IN NUMBER)
AS
    l_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO l_count
    FROM USERS_VIEW
    WHERE id = p_id;

    IF l_count = 0 THEN
        RAISE auth_exceptions.user_not_found;
    END IF;

    DELETE FROM USERS_VIEW
    WHERE id = p_id;

EXCEPTION
    WHEN auth_exceptions.user_not_found THEN
        RAISE_APPLICATION_ERROR(-20002, 'User with ID ' || p_id || ' not found');
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20003, 'Error deleting user by ID: ' || SQLERRM);
END delete_user_by_id;
/


exit;
