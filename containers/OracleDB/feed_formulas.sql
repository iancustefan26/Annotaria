CREATE OR REPLACE PACKAGE feed_fromulas
AS
    -- variables used in functions

    -- number of graph nodes
    v_best_friend_nodes NUMBER := 0;  -- number of most relevant chosen users for the graph
    v_random_friend_nodes NUMBER := 0;  -- number of random chosen users for the graph
    ------------------------

    -- alpha + beta + gamma = 1
    v_alpha NUMBER := 0;  -- social_affinity importance index
    v_beta NUMBER := 0;   -- post_impact importance index
    v_gamma NUMBER := 0;  -- category_relevance importance index
    --------------

    -- used for post_impact function
    v_delta_decay_rate NUMBER := 0; -- decay rate for recency function
    c_euler_number CONSTANT NUMBER  := 2.71828; -- euler number approximation
    ------------------

    -- used for popularity function
    -- a + b = 1
    v_a_like_importance NUMBER := 0; -- a likes importance index
    v_b_comment_importance NUMBER := 0; -- b comments importance index
    -------------------

    -- FUNCTIONS
    ----
    -- MAIN FORMULA:
    -- Score(u, p) = alpha * Social_Affinity(u, p) + beta * Post_Impact(p) + gamma * Category_Relevance(u, p)
    -- returns a number between [0, 1]
    -- alpha + beta + gamma = 1
    FUNCTION score (p_user_id USERS.id%TYPE, p_post_id POST.id%TYPE) RETURN NUMBER;

    -- Popularity(p) = a * likes + b * comments
    FUNCTION popularity (p_post_id POST.id%TYPE) RETURN NUMBER;

    -- Recency(p) = e^(-delta * (now - post_timestamp))
    FUNCTION recency (p_post_id POST.id%TYPE) RETURN NUMBER;

    -- Post_Impact(p) = (Popualrity(p) + Recency(p)
    FUNCTION post_impact (p_post_id POST.id%TYPE) RETURN NUMBER;

    -- Social_Affinity(u, p) = 1 - 1 / FRIENDSHIP(user_id, Author(post_id)) (TABLE)
    FUNCTION social_affinity (p_user_id USERS.id%TYPE, p_post_id POST.id%TYPE) RETURN NUMBER;

    -- Category_Relevance(u, p) = 1 - 1 / CATEGORY_INTEREST(user_id, Category(post_id))
    FUNCTION category_relevance (p_user_id USERS.id%TYPE, p_post_id POST.id%TYPE) RETURN NUMBER; 

END feed_fromulas;

/

CREATE OR REPLACE PACKAGE BODY feed_fromulas
IS
    -- MAIN FORMULA:
    -- Score(u, p) = alpha * Social_Affinity(u, p) + beta * Post_Impact(p) + gamma * Category_Relevance(u, p)
    -- returns a number between [0, 1]
    -- alpha + beta + gamma = 1
    FUNCTION score (p_user_id USERS.id%TYPE, p_post_id POST.id%TYPE) RETURN NUMBER
    AS
    BEGIN
        RETURN 
        v_alpha * social_affinity(p_user_id, p_post_id) +
        v_beta * post_impact(p_user_id) +
        v_gamma * category_relevance(p_user_id, p_post_id);
    EXCEPTION
        WHEN post_exceptions.no_such_post THEN
            raise post_exceptions.no_such_post;
        WHEN OTHERS THEN
            raise post_exceptions.unexpected_post_error;
    END score;

    -- Recency(p) = 1 - 1 / (e ^ ( -delta * (now - post_timestamp)))
    FUNCTION recency (p_post_id POST.id%TYPE) RETURN NUMBER
    AS
        v_post_timestamp POST.date_posted%TYPE;
    BEGIN
        SELECT date_posted INTO v_post_timestamp FROM POST WHERE id = p_post_id;
        RETURN 1.0 - (1.0 / POWER(c_euler_number, -1 * v_delta_decay_rate * 
                         EXTRACT(SECOND FROM (SYSTIMESTAMP - v_post_timestamp)) +
                         60 * EXTRACT(MINUTE FROM (SYSTIMESTAMP - v_post_timestamp)) +
                         3600 * EXTRACT(HOUR FROM (SYSTIMESTAMP - v_post_timestamp)) +
                         86400 * EXTRACT(DAY FROM (SYSTIMESTAMP - v_post_timestamp))
                        ));
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            raise post_exceptions.no_such_post;
        WHEN OTHERS THEN
            raise post_exceptions.unexpected_post_error;
    END recency;

    -- Post_Impact(p) = (Popualrity(p) + Recency(p)) / 2.0
    FUNCTION post_impact (p_post_id POST.id%TYPE) RETURN NUMBER
    AS
    BEGIN
        RETURN (popularity(p_post_id) + recency(p_post_id)) / 2.0;
    EXCEPTION
        WHEN post_exceptions.no_such_post THEN
            raise post_exceptions.no_such_post;
        WHEN OTHERS THEN
            raise post_exceptions.unexpected_post_error;
    END post_impact;

    -- Social_Affinity(u, p) = 1 - 1 / FRIENDSHIP(user_id, Author(post_id)) (TABLE)
    FUNCTION social_affinity (p_user_id USERS.id%TYPE, p_post_id POST.id%TYPE) RETURN NUMBER
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
            raise post_exceptions.no_such_post;
        WHEN OTHERS THEN
            raise post_exceptions.unexpected_post_error;
    END social_affinity;

    -- Category_Relevance(u, p) = 1 - 1 / CATEGORY_INTEREST(user_id, Category(post_id))
    FUNCTION category_relevance (p_user_id USERS.id%TYPE, p_post_id POST.id%TYPE) RETURN NUMBER
    AS
        v_relevance CATEGORY_INTEREST.interest%TYPE;
    BEGIN
        SELECT interest INTO v_relevance FROM CATEGORY_INTEREST
        WHERE user_id = p_user_id 
        AND 
        category_id = (SELECT id FROM CATEGORY WHERE id = (SELECT category_id  FROM POST WHERE id = p_post_id));
        RETURN 1.0 - (1.0 / v_relevance);
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            raise post_exceptions.no_such_post;
        WHEN OTHERS THEN
            raise post_exceptions.unexpected_post_error;
    END category_relevance;

     -- Popularity(p) = a * likes + b * comments
    FUNCTION popularity (p_post_id POST.id%TYPE)
    RETURN NUMBER AS
        v_likes POST.likes_count%TYPE;
        v_comments POST.comments_count%TYPE;
    BEGIN
        SELECT likes_count INTO v_likes FROM POST WHERE id = p_post_id;
        SELECT comments_count INTO v_comments FROM POST WHERE id = p_post_id;
        RETURN v_a_like_importance * v_likes + v_b_comment_importance * v_comments;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            raise post_exceptions.no_such_post;
        WHEN OTHERS THEN
            raise post_exceptions.unexpected_post_error;
    END popularity;

END feed_fromulas;

/

exit;