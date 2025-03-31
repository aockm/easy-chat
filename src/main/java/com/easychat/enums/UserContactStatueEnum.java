package com.easychat.enums;

import org.apache.commons.lang3.StringUtils;

public enum UserContactStatueEnum {
    NO_FRIEND(0, "非好友"),
    FRIEND(1, "好友"),
    DEL(2, "已删除好友"),
    DEL_BE(3, "被好友删除"),
    BLACKLIST(4, "已拉黑好友"),
    BLACKLIST_BE(5, "被好友拉黑");

    private Integer status;
    private String desc;

    UserContactStatueEnum(Integer status,String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    public static UserContactStatueEnum getByStatus(String status) {
        try {
            if (StringUtils.isEmpty(status)) {
                return null;
            }
            return UserContactStatueEnum.valueOf(status.toUpperCase());
        }catch (Exception e){
            return null;
        }
    }
    public static UserContactStatueEnum getByStatus(Integer status) {
        try {
            for (UserContactStatueEnum userContactStatueEnum : UserContactStatueEnum.values()) {
                if (userContactStatueEnum.getStatus().equals(status)) {
                    return userContactStatueEnum;
                }
            }
            return null;
        }catch (Exception e){
            return null;
        }
    }

}
