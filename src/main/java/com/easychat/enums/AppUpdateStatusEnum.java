package com.easychat.enums;

import org.apache.commons.lang3.StringUtils;

public enum AppUpdateStatusEnum {
    INIT(0, "未发布"),
    GRAYSCALE(1, "灰度发布"),
    ALL(2, "灰度发布");

    private Integer status;
    private String desc;

    AppUpdateStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    public static AppUpdateStatusEnum getByStatus(String status) {
        try {
            if (StringUtils.isEmpty(status)) {
                return null;
            }
            return AppUpdateStatusEnum.valueOf(status.toUpperCase());
        }catch (Exception e){
            return null;
        }
    }
    public static AppUpdateStatusEnum getByStatus(Integer status) {
        try {
            for (AppUpdateStatusEnum userContactStatueEnum : AppUpdateStatusEnum.values()) {
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
