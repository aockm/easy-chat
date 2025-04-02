package com.easychat.enums;

import org.apache.commons.lang3.StringUtils;

public enum UserStatusEnum {
    DISABLE(0, "禁用"),
    ENABLE(1, "启用");

    private Integer status;
    private String desc;

    UserStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    public static UserStatusEnum getByStatus(String status) {
        try {
            if (StringUtils.isEmpty(status)) {
                return null;
            }
            return UserStatusEnum.valueOf(status.toUpperCase());
        }catch (Exception e){
            return null;
        }
    }
    public static UserStatusEnum getByStatus(Integer status) {
        try {
            for (UserStatusEnum userContactStatueEnum : UserStatusEnum.values()) {
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
