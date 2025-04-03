package com.easychat.enums;

import org.apache.commons.lang3.StringUtils;

public enum AppUpdateFileTypeEnum {
    LOCAL(0, "本地"),
    OUTER_LINK(1, "外链");

    private Integer type;
    private String desc;

    AppUpdateFileTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public static AppUpdateFileTypeEnum getByType(String type) {
        try {
            if (StringUtils.isEmpty(type)) {
                return null;
            }
            return AppUpdateFileTypeEnum.valueOf(type.toUpperCase());
        }catch (Exception e){
            return null;
        }
    }
    public static AppUpdateFileTypeEnum getByType(Integer type) {
        try {
            for (AppUpdateFileTypeEnum userContactStatueEnum : AppUpdateFileTypeEnum.values()) {
                if (userContactStatueEnum.getType().equals(type)) {
                    return userContactStatueEnum;
                }
            }
            return null;
        }catch (Exception e){
            return null;
        }
    }
}
