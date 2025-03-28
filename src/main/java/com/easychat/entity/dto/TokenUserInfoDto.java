package com.easychat.entity.dto;

import java.io.Serializable;

public class TokenUserInfoDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String token;
    private String userId;
    private String nickname;
    private Boolean admin;

    public void setToken(String token) {
        this.token = token;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}
