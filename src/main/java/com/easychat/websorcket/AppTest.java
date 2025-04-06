package com.easychat.websorcket;

import org.apache.commons.codec.digest.DigestUtils;

public class AppTest {

    public static void main(String[] args) {
        String s = DigestUtils.md5Hex("admin123456");
        System.out.println(s);
    }
}
