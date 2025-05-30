CREATE OR REPLACE VIEW posts_view AS SELECT id FROM POST;

/

CREATE OR REPLACE TRIGGER prevent_mutating_post_cascades
INSTEAD OF DELETE ON posts_view
BEGIN
    DELETE FROM COMMENTS WHERE post_id = :OLD.id;
    DELETE FROM LIKES WHERE post_id = :OLD.id;
    DELETE FROM post WHERE id = :OLD.id;
EXCEPTION
    WHEN OTHERS THEN
        RAISE;
END;

/
CREATE OR REPLACE VIEW users_view AS SELECT id FROM users;

/

CREATE OR REPLACE TRIGGER prevent_mutating_user_cascades
INSTEAD OF DELETE ON users_view
BEGIN
    DELETE FROM COMMENTS WHERE user_id = :OLD.id;
    DELETE FROM LIKES WHERE user_id = :OLD.id;
    DELETE FROM posts_view WHERE id IN (select id FROM POST WHERE author_id = :OLD.id);
    DELETE FROM users WHERE id = :OLD.id;
EXCEPTION
    WHEN OTHERS THEN
        RAISE;
END;

/

commit;

exit;