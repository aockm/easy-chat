package com.easychat.utils;

import com.easychat.enums.UserContactTypeEnum;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.util.Arrays;

public class StringTools {

    public static String getUserId() {
        return UserContactTypeEnum.USER.getPrefix() + getRandomNumber(11);
    }
    public static String getGroupId() {
        return UserContactTypeEnum.GROUP.getPrefix() + getRandomNumber(11);
    }
    public static String getRandomNumber(Integer length) {
        return RandomStringUtils.random(length,false,true);
    }

    public static String getRandomString(Integer length) {
        return RandomStringUtils.random(length,true,true);
    }

    public static String encodeMd5(String origin) {
        return StringUtils.isEmpty(origin) ? null : DigestUtils.md5Hex(origin);
    }

    public static String cleanHtmlTag(String content) {
        if (StringUtils.isEmpty(content)) {
            return null;
        }
        content = content.replace("<", "&lt");
        content = content.replace("\r\n", "<br>");
        content = content.replace("\n", "<br>");
        return content;
    }

    public static final String getChatSessionId4User(String[] userIds) {
        Arrays.sort(userIds);
        return encodeMd5(StringUtils.join(userIds,""));
    }
}
