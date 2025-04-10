package com.easychat.enums;

import org.apache.commons.lang3.StringUtils;

public enum UserContactTypeEnum {
    USER(0, "U", "好友"),
    GROUP(1, "G", "群");

    private Integer type;
    private String prefix;
    private String desc;

    UserContactTypeEnum(Integer type, String prefix, String desc) {
        this.type = type;
        this.prefix = prefix;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getDesc() {
        return desc;
    }

    public static UserContactTypeEnum getByName(String name) {
        try {
            if (StringUtils.isEmpty(name)) {
                return null;
            }
            return UserContactTypeEnum.valueOf(name.toUpperCase());
        }catch (Exception e){
            return null;
        }
    }
    public static UserContactTypeEnum getByPrefix(String prefix) {
        try {
            if (StringUtils.isEmpty(prefix) || prefix.trim().length() == 0) {
                return null;
            }
            prefix = prefix.substring(0, 1);
            for (UserContactTypeEnum userContactTypeEnum : UserContactTypeEnum.values()) {
                if (userContactTypeEnum.getPrefix().equals(prefix)) {
                    return userContactTypeEnum;
                }
            }
            return null;
        }catch (Exception e){
            return null;
        }
    }
}
