package com.easychat.controller;

import com.easychat.constants.Constants;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.exception.BusinessException;
import com.easychat.entity.vo.ResponseVo;
import com.easychat.enums.ResponseCodeEnum;
import com.easychat.redis.RedisUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

public class ABaseController {

    @Resource
    private RedisUtils redisUtils;

    protected static final String STATUS_SUCCESS = "success";

    protected static final String STATUS_ERROR = "error";

    protected <T> ResponseVo getSuccessResponseVo(T t) {
        ResponseVo<T> responseVo = new ResponseVo<>();
        responseVo.setStatus(STATUS_SUCCESS);
        responseVo.setCode(ResponseCodeEnum.CODE_200.getCode());
        responseVo.setInfo(ResponseCodeEnum.CODE_200.getMsg());
        responseVo.setData(t);
        return responseVo;
    }
    protected <T> ResponseVo getBusinessErrorResponseVo(BusinessException e, T t) {
        ResponseVo responseVo = new ResponseVo();
        responseVo.setStatus(STATUS_ERROR);
        if (e.getCode() == null) {
            responseVo.setCode(ResponseCodeEnum.CODE_600.getCode());
        }else {
            responseVo.setCode(e.getCode());
        }
        responseVo.setInfo(e.getMessage());
        responseVo.setData(t);
        return responseVo;
    }
    protected <T> ResponseVo getServerErrorResponseVo(T t) {
        ResponseVo<T> responseVo = new ResponseVo<>();
        responseVo.setStatus(STATUS_ERROR);
        responseVo.setCode(ResponseCodeEnum.CODE_500.getCode());
        responseVo.setInfo(ResponseCodeEnum.CODE_500.getMsg());
        responseVo.setData(t);
        return responseVo;
    }


    protected TokenUserInfoDto getTokenUserInfo(HttpServletRequest request) {
        String token = request.getHeader("token");

        TokenUserInfoDto tokenUserInfoDto = (TokenUserInfoDto) redisUtils.get(Constants.REDIS_KEY_WS_TOKEN+token);
        return tokenUserInfoDto;
    }
}
