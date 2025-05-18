CREATE OR REPLACE PACKAGE tag_exceptions
AS
    over_50_user_tags EXCEPTION;
    PRAGMA EXCEPTION_INIT(over_50_user_tags, -20001);
END tag_exceptions;
/


CREATE OR REPLACE PACKAGE post_exceptions
AS
    no_such_post EXCEPTION;
    unexpected_post_error EXCEPTION;
    PRAGMA EXCEPTION_INIT(no_such_post, -20010);
    PRAGMA EXCEPTION_INIT(unexpected_post_error, -20011);
END post_exceptions;
/

-- exceptions for auth module 
CREATE OR REPLACE PACKAGE auth_exceptions
AS
  user_not_found EXCEPTION;
  PRAGMA EXCEPTION_INIT(user_not_found, -20002);
END auth_exceptions;
/

commit;

exit;


EXIT;
