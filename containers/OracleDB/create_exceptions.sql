CREATE OR REPLACE PACKAGE tag_exceptions
AS
    over_50_user_tags EXCEPTION;
    named_tag_too_large EXCEPTION;
    PRAGMA EXCEPTION_INIT(over_50_user_tags, -20001);
    PRAGMA EXCEPTION_INIT(named_tag_too_large, -200020);
END tag_exceptions;
/

CREATE OR REPLACE PACKAGE statistics_exceptions
AS
    invalid_date_sorting EXCEPTION;
    PRAGMA EXCEPTION_INIT(invalid_date_sorting, -200030);
END statistics_exceptions;
/


CREATE OR REPLACE PACKAGE post_exceptions
AS
    no_such_post EXCEPTION;
    invalid_post_insert EXCEPTION;
    unexpected_post_error EXCEPTION;
    description_too_large EXCEPTION;
    media_blob_too_large EXCEPTION;
    invalid_creation_date EXCEPTION;
    no_existing_unlike_operation EXCEPTION;
    no_existing_category_interest_operation EXCEPTION;
    PRAGMA EXCEPTION_INIT(no_such_post, -20010);
    PRAGMA EXCEPTION_INIT(unexpected_post_error, -20011);
    PRAGMA EXCEPTION_INIT(description_too_large, -20012);
    PRAGMA EXCEPTION_INIT(media_blob_too_large, -20013);
    PRAGMA EXCEPTION_INIT(invalid_creation_date, -20014);
    PRAGMA EXCEPTION_INIT(invalid_post_insert, -20015);
    PRAGMA EXCEPTION_INIT(no_existing_unlike_operation, -20016);
    PRAGMA EXCEPTION_INIT(no_existing_category_interest_operation, -20017);
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
