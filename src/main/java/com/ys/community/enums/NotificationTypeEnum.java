package com.ys.community.enums;

public enum NotificationTypeEnum {
    REPLY_QUESTION(1,"回复了问题"),
    REPLY_COMMENT(2,"回复了评论")
    ;
    private int type;
    private String name;

    NotificationTypeEnum(int status, String name) {
        this.type = status;
        this.name = name;
    }

    public static String nameOfType(Integer type) {
        for (NotificationTypeEnum notificationTypeEnum : NotificationTypeEnum.values()) {
            if (type == notificationTypeEnum.getType()) {
                return notificationTypeEnum.getName();
            }
        }
        return null;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
