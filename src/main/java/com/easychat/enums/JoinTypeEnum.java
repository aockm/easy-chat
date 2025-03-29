package com.easychat.enums;

import org.apache.commons.lang3.StringUtils;

public enum JoinTypeEnum {
    JOIN(0, "直接加如"),
    APPLY(1, "审核");

    private Integer type;
    private String desc;

    JoinTypeEnum(Integer status, String desc) {
        this.type = status;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
    public static JoinTypeEnum getByName(String name) {
        try {
            if (StringUtils.isEmpty(name)) {
                return null;
            }
            return JoinTypeEnum.valueOf(name.toUpperCase());
        }catch (Exception e){
            return null;
        }
    }

    public static JoinTypeEnum getByType(Integer joinType) {
        for (JoinTypeEnum joinTypeEnum : JoinTypeEnum.values()) {
            if (joinTypeEnum.getType().equals(joinType)) {
                return joinTypeEnum;
            }
        }
        return null;
    }

}
