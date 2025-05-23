package com.easychat.constants;

import com.easychat.enums.UserContactTypeEnum;

public class Constants {
    // token失效时间
    public static final Integer REDIS_KEY_TOKEN_EXPIRES = 60*60*24;
    public static final String REDIS_KEY_CHECK_CODE = "easychat:checkcode:";
    public static final String REDIS_KEY_WS_USER_HEART_BEAT = "easychat:ws:user:heartbeat:";
    public static final String REDIS_KEY_WS_TOKEN = "easychat:ws:token:";
    public static final String REDIS_KEY_WS_TOKEN_USERID = "easychat:ws:token:userid:";
    public static final String REDIS_KEY_SYS_SETTING = "easychat:sys:setting:";


    public static final String FILE_FOLDER_FILE = "file/";
    public static final String FILE_FOLDER_AVATAR_NAME = "avatar/";
    public static final String IMAGE_SUFFIX = ".png";
    public static final String COVER_IMAGE_SUFFIX = "_cover.png";
    public static final String APPLY_INFO_TEMPLATE = "我是%s";

    public static final String REGEX_PASSWORD = "^(?![a-zA-Z]+$)(?!\\d+$)(?![^\\da-zA-Z\\s]+$).{8,}$";
    public static final String APP_UPDATE_FOLDER = "app/";
    public static final String APP_TYPE_SUFFIX = ".exe";
    public static final String APP_NAME = "EasyChatSetup";

    // 用户联系人列表
    public static final String REDIS_KEY_USER_CONTACT = "easychat:ws:user:contact:";

    public static final String ROBOT_UID = UserContactTypeEnum.USER.getPrefix()+"robot";
}