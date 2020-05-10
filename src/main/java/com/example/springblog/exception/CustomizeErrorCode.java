package com.example.springblog.exception;

public enum CustomizeErrorCode implements ICustomizeErrorCode{
    QUESTION_NOT_FOUND( "Question not found.",2001),
    TARGET_PARENT_NOT_FOUND("the question or comment does not exist",2002),
    USER_NOT_LOGIN("Please login first to comment", 2003),
    SYSTEM_ERROR("system error.",2004),
    TYPE_PARAM_WRONG("comment type is wrong",2005),
    COMMENT_NOT_FOUND("the comment does not exist",2006),
    COMMENT_IS_EMPTY("comment content cannot be empty",2007),
    NOTIFICATION_NOT_FOUND("notification not found",2008),
    READ_NOTIFICATION_FAIL("you don't have this notification",2009),
    ILLEGAL_IMAGE_FORMAT("The file format is not allowed",2010),
    GET_CONTAINER_FAILURE("cannot connect to the container.",2011),
    NOT_A_PIC("The file upload is not an image",2012),
    UPLOAD_FAILURE("The upload process has failed.try again later",2013),
    DOWNLOAD_FAILURE("The download process has failed.",2014),
    DELETE_FAILURE("Cannot delete the blob.",2015);



    private Integer code;
    private String message;


    CustomizeErrorCode(String message,Integer code) {

        this.code = code;
        this.message = message;

    }

    public String getMessage() {
        return message;
    }
    public Integer getCode() { return code; }
}
