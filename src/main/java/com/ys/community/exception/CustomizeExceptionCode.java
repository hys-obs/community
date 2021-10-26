package com.ys.community.exception;

public enum CustomizeExceptionCode implements ICustomizeExceptionCode {

    QUESTION_NOT_FOUND(2001, "你找的问题不存在，稍后再试~"),
    TARGET_PARAM_NOT_FOUND(2002, "未选中任何问题或者评论进行回复"),
    NO_LOGIN(2003, "未登录不能进行评论"),
    SYSTEM_ERROR(2004, "服务出错，稍后再试！"),
    TYPE_PARAM_WRONG(2005, "评论类型错误或不存在"),
    COMMENT_NOT_FOUND(2006, "回复的评论不存在了，要不换个试试？"),
    CONTENT_IS_EMPTY(2007, "输入的内容不能为空"),
    READ_NOTIFICATION_FAIL(2008, "兄弟，你这是读别人的信息呢？"),
    NOTIFICATION_NOT_FOUND(2009, "消息无啦~"),
    ;

    private Integer code;
    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    CustomizeExceptionCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
