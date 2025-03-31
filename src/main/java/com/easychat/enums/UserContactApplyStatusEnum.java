package com.easychat.enums;

import org.apache.commons.lang3.StringUtils;

public enum UserContactApplyStatusEnum {
    INIT(1, "待处理"),
    PASS(1, "已同意"),
    REJECT(2, "拒绝"),
    BLACKLIST(3, "已拉黑");
    private Integer status;
    private String desc;

    UserContactApplyStatusEnum(Integer status,String desc) {
        this.status = status;
        this.desc = desc;
    }
    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    public static UserContactApplyStatusEnum getByStatus(String status) {
        try {
            if (StringUtils.isEmpty(status)) {
                return null;
            }
            return UserContactApplyStatusEnum.valueOf(status.toUpperCase());
        }catch (Exception e){
            return null;
        }
    }
    public static UserContactApplyStatusEnum getByStatus(Integer status) {
        try {
            for (UserContactApplyStatusEnum userContactStatueEnum : UserContactApplyStatusEnum.values()) {
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
