package com.easychat.controller;

import com.easychat.constants.Constants;
import com.easychat.entity.vo.ResponseVo;
import com.easychat.exception.BusinessException;
import com.easychat.redis.RedisUtils;
import com.easychat.service.UserInfoService;
import com.wf.captcha.ArithmeticCaptcha;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/account")
@Validated
public class AccountController extends ABaseController {

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private UserInfoService userInfoService;

    @RequestMapping("/checkCode")
    public ResponseVo checkCode() {
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(100, 42);
        String code = captcha.text();
        String checkCodeKey = UUID.randomUUID().toString();
        String checkCodeBase64 = captcha.toBase64();
        redisUtils.setex(Constants.REDIS_KEY_CHECK_CODE+checkCodeKey,code, 60*10);
        Map<String, String> result = new HashMap<>();
        result.put("code", checkCodeBase64);
        result.put("checkCodeKey", checkCodeKey);
        return getSuccessResponseVo(result);
    }

    @RequestMapping("/register")
    public ResponseVo register(@NotEmpty String checkCodeKey,
                               @NotEmpty @Email String email,
                               @NotEmpty String password,
                               @NotEmpty String nickName,
                               @NotEmpty String checkCode) throws BusinessException {
        try {
            if (checkCode.equalsIgnoreCase((String) redisUtils.get(Constants.REDIS_KEY_CHECK_CODE+checkCodeKey))) {
                throw new BusinessException("验证码不正确");
            }
            userInfoService.register(email, password, nickName);
            return getSuccessResponseVo(null);
        }finally {
            redisUtils.delete(Constants.REDIS_KEY_CHECK_CODE+checkCodeKey);
        }
    }
}
