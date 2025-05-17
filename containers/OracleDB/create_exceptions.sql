CREATE OR REPLACE PACKAGE tag_exceptions
AS
    over_50_user_tags EXCEPTION;
    PRAGMA EXCEPTION_INIT(over_50_user_tags, -20001);
END tag_exceptions;
/


-- exceptions for auth module 
CREATE OR REPLACE PACKAGE auth_exceptions
AS
  user_not_found EXCEPTION;
  PRAGMA EXCEPTION_INIT(user_not_found, -20002);
END auth_exceptions;
/


CREATE OR REPLACE PACKAGE posts_exceptions
AS
  post_not_found EXCEPTION;
  PRAGMA EXCEPTION_INIT(post_not_found, -20003);
end posts_exceptions;
/


commit;

exit;


EXIT;
