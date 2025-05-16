CREATE OR REPLACE PACKAGE tag_exceptions
AS
    over_50_user_tags EXCEPTION;
    PRAGMA EXCEPTION_INIT(over_50_user_tags, -20001);
END tag_exceptions;
/

commit;

exit;


EXIT;