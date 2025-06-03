-- Creating function to retrieve user by username with input validation
CREATE OR REPLACE FUNCTION get_user_by_username(p_username IN VARCHAR2)
RETURN SYS_REFCURSOR
AS
    l_cursor SYS_REFCURSOR;
    l_count NUMBER;
BEGIN
    -- Validate input length and format
    IF p_username IS NULL OR LENGTH(TRIM(p_username)) = 0 OR LENGTH(p_username) > 50 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Invalid username length or format');
    END IF;

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

-- Creating function to retrieve user by ID with input validation
CREATE OR REPLACE FUNCTION get_user_by_id(p_id IN NUMBER)
RETURN SYS_REFCURSOR
AS
    l_cursor SYS_REFCURSOR;
    l_count NUMBER;
BEGIN
    -- Validate input
    IF p_id IS NULL OR p_id <= 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Invalid user ID');
    END IF;

    SELECT COUNT(*) INTO l_count
    FROM USERS
    WHERE id = p_id;

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

-- Creating function to retrieve user by email with input validation
CREATE OR REPLACE FUNCTION get_user_by_email(p_email IN VARCHAR2)
RETURN SYS_REFCURSOR
AS
    l_cursor SYS_REFCURSOR;
    l_count NUMBER;
BEGIN
    -- Validate input length and format
    IF p_email IS NULL OR LENGTH(TRIM(p_email)) = 0 OR LENGTH(p_email) > 100 OR NOT REGEXP_LIKE(p_email, '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$') THEN
        RAISE_APPLICATION_ERROR(-20001, 'Invalid email format');
    END IF;

    SELECT COUNT(*) INTO l_count
    FROM USERS
    WHERE email = p_email;

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

-- Creating procedure to delete user by ID with input validation
CREATE OR REPLACE PROCEDURE delete_user_by_id(p_id IN NUMBER)
AS
    l_count NUMBER;
BEGIN
    -- Validate input
    IF p_id IS NULL OR p_id <= 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Invalid user ID');
    END IF;

    SELECT COUNT(*) INTO l_count
    FROM USERS
    WHERE id = p_id;

    IF l_count = 0 THEN
        RAISE auth_exceptions.user_not_found;
    END IF;

    DELETE FROM USERS
    WHERE id = p_id;

EXCEPTION
    WHEN auth_exceptions.user_not_found THEN
        RAISE_APPLICATION_ERROR(-20002, 'User with ID ' || p_id || ' not found');
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20003, 'Error deleting user by ID: ' || SQLERRM);
END delete_user_by_id;
/

exit;