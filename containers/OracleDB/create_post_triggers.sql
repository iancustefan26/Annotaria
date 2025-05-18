
CREATE or replace TRIGGER no_more_than_50_tags
FOR INSERT ON USER_TAG_FRAMES
COMPOUND TRIGGER

    v_tag_count NUMBER := 0;

    AFTER EACH ROW IS
        v_rows NUMBER;
    BEGIN
        v_tag_count := v_tag_count + 1;
        IF v_tag_count > 50 THEN
            ROLLBACK;
            DBMS_OUTPUT.PUT_LINE('You cannot tag more than 50 users on a post (spam violation reasons)');
            raise tag_exceptions.over_50_user_tags;
        END IF;

        -- increase friendship after tagging someone
        SELECT COUNT(*) INTO v_rows FROM FRIENDSHIP WHERE user1_id = :NEW.user_author_id AND user2_id = :NEW.user_tagged_id;
        IF v_rows = 0 THEN
            INSERT INTO FRIENDSHIP VALUES(
                :NEW.user_author_id,
                :NEW.user_tagged_id,
                1
            );
        ELSE 
            UPDATE FRIENDSHIP SET interest = interest + 1 WHERE user1_id = :NEW.user_author_id AND user2_id = :NEW.user_tagged_id;
        END IF;

    EXCEPTION
    WHEN NO_DATA_FOUND THEN
        raise post_exceptions.no_such_post;
    WHEN OTHERS THEN
        raise post_exceptions.unexpected_post_error;
    
    END AFTER EACH ROW;

END no_more_than_50_tags;

/

CREATE OR REPLACE TRIGGER valid_post_trigger
BEFORE INSERT ON POST
FOR EACH ROW
BEGIN
    IF :NEW.description IS NOT NULL
       AND LENGTH(:NEW.description) > 1000
    THEN
        RAISE post_exceptions.description_too_large;
    ELSE RAISE post_exceptions.unexpected_post_error;
    END IF;

    -- media BLOB larger than 50 MB?
    -- (50 MB = 50 × 1024 × 1024 = 52 428 800 bytes)
    IF :NEW.media_blob IS NOT NULL
       AND DBMS_LOB.GETLENGTH(:NEW.media_blob) > 50*1024*1024
    THEN
        raise post_exceptions.media_blob_too_large;
    ELSE RAISE post_exceptions.unexpected_post_error;
    END IF;

    IF :NEW.creation_year IS NOT NULL
    THEN
        IF ( 
             (   REGEXP_LIKE(TO_CHAR(:NEW.creation_year),'^\d{4}$')
                 AND :NEW.creation_year > EXTRACT(YEAR FROM SYSDATE)
             )
           )
        THEN
            raise post_exceptions.invalid_creation_date;
        ELSE RAISE post_exceptions.unexpected_post_error;
        END IF;
    END IF;


EXCEPTION
    WHEN NO_DATA_FOUND THEN
        raise post_exceptions.no_such_post;
    WHEN OTHERS THEN
        raise post_exceptions.unexpected_post_error;

END valid_post_trigger;

/

CREATE OR REPLACE TRIGGER update_likes_interest
AFTER INSERT OR DELETE ON LIKES
FOR EACH ROW
DECLARE
    v_rows_friendship NUMBER;
    v_rows_category NUMBER;
    v_post_author_id POST.author_id%TYPE;
    v_category_id POST.CATEGORY_ID%TYPE;
    v_interest NUMBER;   
BEGIN
    SELECT author_id INTO v_post_author_id FROM POST WHERE id = :OLD.post_id;
    SELECT category_id INTO v_category_id FROM POST WHERE id = :OLD.post_id;
    SELECT COUNT(*) INTO v_rows_friendship FROM FRIENDSHIP WHERE user1_id = :OLD.user_id AND user2_id = v_post_author_id;
    SELECT COUNT(*) INTO v_rows_category FROM CATEGORY_INTEREST WHERE user_id = :OLD.user_id AND category_id = v_category_id;
    CASE
        WHEN INSERTING THEN
            -- increasing friendship interest
            IF v_rows_friendship = 0 THEN
                INSERT INTO FRIENDSHIP VALUES(
                    :NEW.user_id,
                    v_post_author_id,
                    1
                );
            ELSE
                UPDATE FRIENDSHIP SET interest = interest + 1 WHERE user1_id = :NEW.user_id AND user2_id = v_post_author_id;
            END IF;

            -- increasing category interest
            IF v_rows_category = 0 THEN
                INSERT INTO CATEGORY_INTEREST VALUES(
                    :NEW.user_id,
                    v_category_id,
                    1
                );
            ELSE
                UPDATE CATEGORY_INTEREST SET interest = interest + 1 WHERE user_id = :NEW.user_id AND category_id = v_category_id;
            END IF;

        WHEN DELETING THEN
            -- decreasing friendship interest
            IF v_rows_friendship = 0 THEN
                raise post_exceptions.no_existing_unlike_operation;
            ELSE
                SELECT interest INTO v_interest FROM FRIENDSHIP WHERE user1_id = :OLD.user_id AND user2_id = v_post_author_id;
                IF v_interest = 0 THEN
                    DELETE FROM FRIENDSHIP WHERE user1_id = :OLD.user_id AND user2_id = v_post_author_id;
                ELSE 
                    UPDATE FRIENDSHIP SET interest = interest - 1 WHERE user1_id = :OLD.user_id AND user2_id = v_post_author_id;
                END IF;
            END IF;
            
            -- decreasing category interest
            IF v_rows_category = 0 THEN
                raise post_exceptions.no_existing_category_interest_operation;
            ELSE
                SELECT interest INTO v_interest FROM CATEGORY_INTEREST WHERE user_id = :OLD.user_id AND category_id = v_category_id;
                IF v_interest = 0 THEN
                    DELETE FROM CATEGORY_INTEREST WHERE user_id = :OLD.user_id AND category_id = v_category_id;
                ELSE
                    UPDATE CATEGORY_INTEREST SET interest = interest - 1 WHERE user_id = :OLD.user_id AND category_id = v_category_id;
                END IF;
            END IF;
    END CASE;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        raise post_exceptions.no_such_post;
    WHEN OTHERS THEN
        raise post_exceptions.unexpected_post_error;

END update_likes_interest;

/

commit;

exit;