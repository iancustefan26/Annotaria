CREATE OR REPLACE PACKAGE feed_formulas
AS
    -- variables used in functions

    -- alpha + beta + gamma = 1
    v_alpha FLOAT := 0.6;  -- social_affinity importance index
    v_beta FLOAT := 0.1;   -- post_impact importance index
    v_gamma FLOAT := 0.3;  -- category_relevance importance index
    --------------

    -- used for post_impact function
    v_delta_decay_rate FLOAT := 0.1; -- decay rate for recency function
    c_euler_number CONSTANT FLOAT  := 2.71828; -- euler number approximation
    ------------------

    -- used for popularity function
    -- a + b = 1
    v_a_like_importance FLOAT := 0.6; -- a likes importance index
    v_b_comment_importance FLOAT := 0.4; -- b comments importance index
    -------------------

    -- FUNCTIONS
    ----
    -- MAIN FORMULA:
    -- Score(u, p) = alpha * Social_Affinity(u, p) + beta * Post_Impact(p) + gamma * Category_Relevance(u, p)
    -- returns a number between [0, 1]
    -- alpha + beta + gamma = 1
    FUNCTION score (p_user_id NUMBER, p_post_id NUMBER) RETURN FLOAT;

    -- Popularity(p) = a * likes + b * comments
    FUNCTION popularity (p_post_id POST.id%TYPE) RETURN FLOAT;

    -- Recency(p) = e^(-delta * (now - post_timestamp))
    FUNCTION recency (p_post_id POST.id%TYPE) RETURN FLOAT;

    -- Post_Impact(p) = (Popualrity(p) + Recency(p)
    FUNCTION post_impact (p_post_id POST.id%TYPE) RETURN FLOAT;

    -- Social_Affinity(u, p) = 1 - 1 / FRIENDSHIP(user_id, Author(post_id)) (TABLE)
    FUNCTION social_affinity (p_user_id USERS.id%TYPE, p_post_id POST.id%TYPE) RETURN FLOAT;

    -- Category_Relevance(u, p) = 1 - 1 / CATEGORY_INTEREST(user_id, Category(post_id))
    FUNCTION category_relevance (p_user_id USERS.id%TYPE, p_post_id POST.id%TYPE) RETURN FLOAT; 

END feed_formulas;

/

CREATE OR REPLACE PACKAGE BODY feed_formulas
IS
    -- MAIN FORMULA:
    -- Score(u, p) = alpha * Social_Affinity(u, p) + beta * Post_Impact(p) + gamma * Category_Relevance(u, p)
    -- returns a number between [0, 1]
    -- alpha + beta + gamma = 1
    FUNCTION score (p_user_id NUMBER, p_post_id NUMBER) RETURN FLOAT
    AS
    BEGIN
        RETURN 
        v_alpha * social_affinity(p_user_id, p_post_id) +
        v_beta * post_impact(p_user_id) +
        v_gamma * category_relevance(p_user_id, p_post_id);
    EXCEPTION
        WHEN OTHERS THEN
            raise;
    END score;

    -- Recency(p) = 1 - 1 / (e ^ ( -delta * (now - post_timestamp)))
    FUNCTION recency (p_post_id POST.id%TYPE) RETURN FLOAT
AS
    v_post_timestamp POST.date_posted%TYPE;
BEGIN
    SELECT date_posted INTO v_post_timestamp FROM POST WHERE id = p_post_id;
    RETURN 1.0 - (1.0 / (
        1440 * EXTRACT(DAY FROM (SYSTIMESTAMP - v_post_timestamp)) +
        60 * EXTRACT(HOUR FROM (SYSTIMESTAMP - v_post_timestamp)) +
        EXTRACT(MINUTE FROM (SYSTIMESTAMP - v_post_timestamp))
    ));
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        raise post_exceptions.no_such_post;
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error Code    : ' || SQLCODE);
        DBMS_OUTPUT.PUT_LINE('Error Message : ' || SQLERRM);
        DBMS_OUTPUT.PUT_LINE('Backtrace feed     : ' || DBMS_UTILITY.FORMAT_ERROR_BACKTRACE);
        raise post_exceptions.unexpected_post_error;
END recency;

    -- Post_Impact(p) = (Popualrity(p) + Recency(p)) / 2.0
    FUNCTION post_impact (p_post_id POST.id%TYPE) RETURN FLOAT
    AS
    BEGIN
        RETURN (popularity(p_post_id) + recency(p_post_id)) / 2.0;
    EXCEPTION
        WHEN OTHERS THEN
            raise;
    END post_impact;

    -- Social_Affinity(u, p) = 1 - 1 / FRIENDSHIP(user_id, Author(post_id)) (TABLE)
    FUNCTION social_affinity (p_user_id USERS.id%TYPE, p_post_id POST.id%TYPE) RETURN FLOAT
    AS
        v_friendship_interest FRIENDSHIP.interest%TYPE;
    BEGIN
        SELECT interest INTO v_friendship_interest FROM FRIENDSHIP
        WHERE user1_id = p_user_id
        AND
        user2_id = (SELECT author_id FROM POST WHERE id = p_post_id);

        RETURN 1.0 - (1.0 / v_friendship_interest);
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN 0.1;
        WHEN OTHERS THEN
            raise post_exceptions.unexpected_post_error;
    END social_affinity;

    -- Category_Relevance(u, p) = 1 - 1 / CATEGORY_INTEREST(user_id, Category(post_id))
    FUNCTION category_relevance (p_user_id USERS.id%TYPE, p_post_id POST.id%TYPE) RETURN FLOAT
    AS
        v_relevance CATEGORY_INTEREST.interest%TYPE;
    BEGIN
        SELECT interest INTO v_relevance FROM CATEGORY_INTEREST
        WHERE user_id = p_user_id 
        AND 
        category_id = (SELECT category_id  FROM POST WHERE id = p_post_id);
        RETURN 1.0 - (1.0 / v_relevance);
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN 0.1;
        WHEN OTHERS THEN
            raise post_exceptions.unexpected_post_error;
    END category_relevance;

     -- Popularity(p) = a * likes + b * comments
    FUNCTION popularity (p_post_id POST.id%TYPE)
    RETURN FLOAT AS
        v_likes POST.likes_count%TYPE;
        v_comments POST.comments_count%TYPE;
    BEGIN
        SELECT likes_count INTO v_likes FROM POST WHERE id = p_post_id;
        SELECT comments_count INTO v_comments FROM POST WHERE id = p_post_id;
        RETURN v_a_like_importance * v_likes + v_b_comment_importance * v_comments;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN 0.1;
        WHEN OTHERS THEN
            raise post_exceptions.unexpected_post_error;
    END popularity;

END feed_formulas;

/

exit;


DECLARE
    v_graph graph;
BEGIN
    v_graph := graph_generator.generate(21, 5, 2, NULL, NULL, 1);
     FOR i in v_graph.first..v_graph.last LOOP
        FOR j in v_graph(i).first..v_graph(i).last LOOP
            DBMS_OUTPUT.PUT(v_graph(i)(j)|| ' ');
        END LOOP;
        DBMS_OUTPUT.PUT_LINE('');
    END LOOP;
END;



select * from user_source;
