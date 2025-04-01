package com.easychat.controller;
import com.easychat.annotation.GlobalInterceptor;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.po.UserContact;
import com.easychat.entity.query.UserContactQuery;
import com.easychat.entity.vo.GroupInfoVo;
import com.easychat.enums.GroupStatusEnum;
import com.easychat.enums.UserContactStatueEnum;
import com.easychat.exception.BusinessException;
import com.easychat.service.GroupInfoService;
import com.easychat.entity.po.GroupInfo;
import com.easychat.entity.query.GroupInfoQuery;
import com.easychat.entity.vo.ResponseVo;

import java.io.IOException;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.easychat.service.UserContactService;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

/**
 *@Description: Service
 *@date: 2025/03/31
 */
@RestController
@RequestMapping("group")
public class GroupInfoController extends ABaseController {
	@Resource
	private GroupInfoService groupInfoService;

	@Resource
	private UserContactService userContactService;

	@RequestMapping("/saveGroup")
	@GlobalInterceptor
	public ResponseVo saveGroup(HttpServletRequest request, String groupId,
								@NotEmpty String groupName,
								String groupNotice,
								@NotNull	Integer joinType,
								MultipartFile avatarFile,
								MultipartFile avatarCover) throws BusinessException, IOException {
		TokenUserInfoDto tokenUserInfo = getTokenUserInfo(request);
		GroupInfo groupInfo = new GroupInfo();
		groupInfo.setGroupId(groupId);
		groupInfo.setGroupOwnerId(tokenUserInfo.getUserId());
		groupInfo.setGroupName(groupName);
		groupInfo.setGroupNotice(groupNotice);
		groupInfo.setJoinType(joinType);
		groupInfoService.saveGroup(groupInfo,avatarFile,avatarCover);
		return getSuccessResponseVo(null);
	}

	@RequestMapping("/loadMyGroup")
	@GlobalInterceptor
	public ResponseVo loadMyGroup(HttpServletRequest request) {
		TokenUserInfoDto tokenUserInfo = getTokenUserInfo(request);
		GroupInfoQuery groupInfoQuery = new GroupInfoQuery();
		groupInfoQuery.setGroupOwnerId(tokenUserInfo.getUserId());
		groupInfoQuery.setOrderBy("create_time desc");

		List<GroupInfo> groupInfoList = this.groupInfoService.findListByParam(groupInfoQuery);
		return getSuccessResponseVo(groupInfoList);
	}

	@RequestMapping("/getGroupInfo")
	@GlobalInterceptor
	public ResponseVo getGroupInfo(HttpServletRequest request,@NotEmpty String groupId) throws BusinessException {
		GroupInfo groupInfo = getGroupDetailCommon(request, groupId);
		UserContactQuery userContactQuery = new UserContactQuery();
		userContactQuery.setContactId(groupId);
		Integer memberCount = this.userContactService.findCountByParam(userContactQuery);
		groupInfo.setMemberCount(memberCount);
		return getSuccessResponseVo(groupInfo);
	}

	public GroupInfo getGroupDetailCommon(HttpServletRequest request,String groupId) throws BusinessException {
		TokenUserInfoDto tokenUserInfo = getTokenUserInfo(request);
		UserContact userContact = this.userContactService.getUserContactByUserIdAndContactId(tokenUserInfo.getUserId(), groupId);

		if (null == userContact || !UserContactStatueEnum.FRIEND.equals(userContact.getStatus())) {
			throw new BusinessException("你不在群聊或者群聊不存在或已解散");
		}

		GroupInfo groupInfo = this.groupInfoService.getGroupInfoByGroupId(groupId);
		if (null == groupInfo || !GroupStatusEnum.NORMAL.equals(groupInfo.getStatus())) {
			throw new BusinessException("群聊不存在，或已解散");
		}
		return groupInfo;
	}

	@RequestMapping("/getGroupInfo4Chat")
	@GlobalInterceptor
	public ResponseVo getGroupInfo4Chat(HttpServletRequest request,@NotEmpty String groupId) throws BusinessException {
		GroupInfo groupInfo = getGroupDetailCommon(request, groupId);
		UserContactQuery userContactQuery = new UserContactQuery();
		userContactQuery.setContactId(groupId);
		userContactQuery.setQueryUserInfo(true);
		userContactQuery.setOrderBy("create_time asc");
		userContactQuery.setStatus(UserContactStatueEnum.FRIEND.getStatus());
		List<UserContact> userContactList = this.userContactService.findListByParam(userContactQuery);
		GroupInfoVo groupInfoVo = new GroupInfoVo();
		groupInfoVo.setGroupInfo(groupInfo);
		groupInfoVo.setUserContactList(userContactList);
		return getSuccessResponseVo(groupInfoVo);
	}

}