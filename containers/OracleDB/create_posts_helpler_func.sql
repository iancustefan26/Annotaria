

CREATE or REPLACE FUNCTION get_post_by_id(p_id in NUMBER)
RETURN SYS_REFCURSOR
AS
    l_cursor SYS_REFCURSOR;
    l_count NUMBER;
BEGIN
    select count(*) into l_count from post where id = p_id;
    if l_count = 0 then
        raise posts_exceptions.post_not_found; 
    end if;
    open l_cursor for 
        select * from post where id = p_id;
    return l_cursor;
END get_post_by_id;
/

create or replace PROCEDURE delete_post_by_id(p_id in NUMBER)
AS
    l_count NUMBER;
BEGIN
    select count(*) into l_count from post where id = p_id;
    if l_count = 0 then
        raise posts_exceptions.post_not_found; 
    end if;
    delete from post where id = p_id;
END delete_post_by_id;
/

create or replace function get_post_by_category(p_category_id in NUMBER)
RETURN SYS_REFCURSOR
AS
    l_cursor SYS_REFCURSOR;
    l_count NUMBER;
BEGIN
    select count(*) into l_count from post where category_id = p_category_id;
    if l_count = 0 then 
        raise posts_exceptions.post_not_found;
    end if;
    open l_cursor for 
        select * from post where category_id = p_category_id;
    return l_cursor;
end get_post_by_category;

/

create or replace function get_post_by_user_id(p_user_id in NUMBER)
return SYS_REFCURSOR
AS
    l_cursor SYS_REFCURSOR;
    l_count NUMBER;
BEGIN
    select count(*) into l_count from post where author_id = p_user_id;
    if l_count = 0 then
        raise posts_exceptions.post_not_found;
    end if;
    open l_cursor for  
        select * from post where author_id = p_user_id;
    return l_cursor;
end get_post_by_user_id;

/
