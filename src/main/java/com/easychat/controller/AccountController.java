package com.easychat.controller;

import com.easychat.entity.vo.ResponseVo;
import com.wf.captcha.ArithmeticCaptcha;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/account")
@Validated
public class AccountController extends ABaseController {

    @RequestMapping("/checkCode")
    public ResponseVo checkCode() {
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(100, 43);
        String code = captcha.text();
        String checkCodeKey = UUID.randomUUID().toString();
        String checkCodeBase64 = captcha.toBase64();
        Map<String, String> result = new HashMap<>();
        result.put("code", checkCodeBase64);
        result.put("checkCodeKey", checkCodeKey);
        return getSuccessResponseVo(result);
    }
}
