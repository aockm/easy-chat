package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.entity.po.GroupInfo;
import com.easychat.entity.query.GroupInfoQuery;
import com.easychat.entity.vo.PaginationResultVo;
import com.easychat.entity.vo.ResponseVo;
import com.easychat.enums.ResponseCodeEnum;
import com.easychat.exception.BusinessException;
import com.easychat.service.GroupInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/admin")
public class AdminGroupController extends ABaseController {

    @Resource
    private GroupInfoService groupInfoService;

    @RequestMapping("/loadGroup")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVo loadGroup(GroupInfoQuery groupInfoQuery){
        groupInfoQuery.setOrderBy("create_time desc");
        groupInfoQuery.setQueryGroupOwnerName(true);
        groupInfoQuery.setQueryMemberCount(true);
        PaginationResultVo resultVo = groupInfoService.findListByPage(groupInfoQuery);
        return getSuccessResponseVo(resultVo);
    }

    @RequestMapping("/dissolutionGroup")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVo dissolutionGroup(@NotEmpty String groupId) throws BusinessException {
        GroupInfo groupInfo = groupInfoService.getGroupInfoByGroupId(groupId);
        if(groupInfo == null){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        groupInfoService.dissolutionGroup(groupInfo.getGroupOwnerId(),groupId);
        return getSuccessResponseVo(null);
    }
}
