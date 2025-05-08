package com.easychat.enums;

import org.apache.commons.lang3.StringUtils;

public enum MessageStatusEnum {
    SENDING(0, "发送中"),
    SENT(1, "已发送");

    private Integer status;
    private String desc;

    MessageStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    public static MessageStatusEnum getByStatus(String status) {
        try {
            if (StringUtils.isEmpty(status)) {
                return null;
            }
            return MessageStatusEnum.valueOf(status.toUpperCase());
        }catch (Exception e){
            return null;
        }
    }
    public static MessageStatusEnum getByStatus(Integer status) {
        try {
            for (MessageStatusEnum userContactStatueEnum : MessageStatusEnum.values()) {
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
