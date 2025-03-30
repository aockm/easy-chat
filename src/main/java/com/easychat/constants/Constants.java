package com.easychat.constants;

import com.easychat.enums.UserContactTypeEnum;

public class Constants {
    public static final String REDIS_KEY_CHECK_CODE = "easychat:checkcode:";
    public static final String REDIS_KEY_WS_USER_HEART_BEAT = "easychat:ws:user:heartbeat:";
    public static final String REDIS_KEY_WS_TOKEN = "easychat:ws:token:";
    public static final String REDIS_KEY_WS_TOKEN_USERID = "easychat:ws:token:userid:";
    public static final String REDIS_KEY_SYS_SETTING = "easychat:sys:setting:";


    public static final String ROBOT_UID = UserContactTypeEnum.USER.getPrefix()+"robot";
}