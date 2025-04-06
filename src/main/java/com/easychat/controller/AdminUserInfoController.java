package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.entity.query.UserInfoQuery;
import com.easychat.entity.vo.PaginationResultVo;
import com.easychat.entity.vo.ResponseVo;
import com.easychat.exception.BusinessException;
import com.easychat.service.UserInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/admin")
public class AdminUserInfoController extends ABaseController {

    @Resource
    private UserInfoService userInfoService;

    @RequestMapping("/loadUser")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVo getUserInfo(UserInfoQuery userInfoQuery){
        userInfoQuery.setOrderBy("create_time desc");
        PaginationResultVo resultVo = userInfoService.findListByPage(userInfoQuery);
        return getSuccessResponseVo(resultVo);
    }

    @RequestMapping("/updateUserStatus")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVo updateUserStatus(@NotNull Integer status,@NotNull String userId) throws BusinessException {
        userInfoService.updateUserStatus(status,userId);
        return getSuccessResponseVo(null);
    }

    @RequestMapping("/forceOffline")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVo forceOffline(@NotNull String userId) throws BusinessException {
        userInfoService.forceOffline(userId);
        return getSuccessResponseVo(null);
    }
}
