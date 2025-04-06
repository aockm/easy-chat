package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.dto.UserContactSearchResultDto;
import com.easychat.entity.po.UserContact;
import com.easychat.entity.po.UserInfo;
import com.easychat.entity.query.UserContactApplyQuery;
import com.easychat.entity.query.UserContactQuery;
import com.easychat.entity.vo.PaginationResultVo;
import com.easychat.entity.vo.ResponseVo;
import com.easychat.entity.vo.UserInfoVo;
import com.easychat.enums.PageSize;
import com.easychat.enums.ResponseCodeEnum;
import com.easychat.enums.UserContactStatueEnum;
import com.easychat.enums.UserContactTypeEnum;
import com.easychat.exception.BusinessException;
import com.easychat.service.UserContactApplyService;
import com.easychat.service.UserContactService;
import com.easychat.service.UserInfoService;
import com.easychat.utils.CopyTools;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 *@Description: 联系人Service
 *@date: 2025/03/31
 */
@RestController
@RequestMapping("/contact")
public class UserContactController extends ABaseController {
	@Resource
	private UserContactService userContactService;

	@Resource
	private UserInfoService userInfoService;

	@Resource
	private UserContactApplyService userContactApplyService;

	@RequestMapping("/search")
	@GlobalInterceptor
	public ResponseVo search(HttpServletRequest request, @NotEmpty String contactId) {
		TokenUserInfoDto tokenUserInfo = getTokenUserInfo(request);
		UserContactSearchResultDto resultDto = userContactService.searchContact(tokenUserInfo.getUserId(), contactId);
		return getSuccessResponseVo(resultDto);
	}

	@RequestMapping("/applyAdd")
	@GlobalInterceptor
	public ResponseVo applyAdd(HttpServletRequest request, @NotEmpty String contactId,String applyInfo) throws BusinessException {
		TokenUserInfoDto tokenUserInfo = getTokenUserInfo(request);
		Integer joinType = userContactService.applyAdd(tokenUserInfo, contactId,applyInfo);
		return getSuccessResponseVo(joinType);
	}

	@RequestMapping("/loadApply")
	@GlobalInterceptor
	public ResponseVo loadApply(HttpServletRequest request, Integer pageNo){
		TokenUserInfoDto tokenUserInfo = getTokenUserInfo(request);
		UserContactApplyQuery applyQuery = new UserContactApplyQuery();
		applyQuery.setOrderBy("last_apply_time desc");
		applyQuery.setReceiveUserId(tokenUserInfo.getUserId());
		applyQuery.setPageNo(pageNo);
		applyQuery.setPageSize(PageSize.SIZE15.getSize());
		applyQuery.setQueryContactInfo(true);
		PaginationResultVo resultVo = userContactApplyService.findListByPage(applyQuery);
		return getSuccessResponseVo(resultVo);
	}

	@RequestMapping("/dealWithApply")
	@GlobalInterceptor
	public ResponseVo dealWithApply(HttpServletRequest request, @NotNull Integer applyId, @NotNull Integer status) throws BusinessException {
		TokenUserInfoDto tokenUserInfo = getTokenUserInfo(request);
		this.userContactApplyService.dealWithApply(tokenUserInfo.getUserId(), applyId, status);
		return getSuccessResponseVo(null);
	}
	@RequestMapping("/loadContact")
	@GlobalInterceptor
	public ResponseVo loadContact(HttpServletRequest request, @NotNull String contactType) throws BusinessException {
		UserContactTypeEnum contactEnum = UserContactTypeEnum.getByName(contactType);
		if (contactEnum == null) {
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		TokenUserInfoDto tokenUserInfo = getTokenUserInfo(request);
		UserContactQuery userContactQuery = new UserContactQuery();
		userContactQuery.setUserId(tokenUserInfo.getUserId());
		userContactQuery.setContactType(contactEnum.getType());
		if (contactEnum == UserContactTypeEnum.USER) {
			userContactQuery.setQueryUserInfo(true);
		}else if (contactEnum == UserContactTypeEnum.GROUP) {
			userContactQuery.setQueryGroupInfo(true);
			userContactQuery.setExcludeMyGroup(true);
		}
		userContactQuery.setOrderBy("last_update_time desc");
		List<UserContact> contactList = userContactService.findListByParam(userContactQuery);
		return getSuccessResponseVo(contactList);
	}

	@RequestMapping("/getContactInfo")
	@GlobalInterceptor
	public ResponseVo getContactInfo(HttpServletRequest request, @NotNull String contactId) {
		TokenUserInfoDto tokenUserInfo = getTokenUserInfo(request);

		UserInfo userInfo = userInfoService.getUserInfoByUserId(contactId);
		UserInfoVo userInfoVo = CopyTools.copy(userInfo, UserInfoVo.class);
		userInfoVo.setContactStatus(UserContactStatueEnum.NO_FRIEND.getStatus());
		UserContact userContact = userContactService.getUserContactByUserIdAndContactId(tokenUserInfo.getUserId(), contactId);
		if (userContact != null) {
			userInfoVo.setContactStatus(UserContactStatueEnum.FRIEND.getStatus());
		}
		return getSuccessResponseVo(userInfoVo);
	}

	@RequestMapping("/getContactUserInfo")
	@GlobalInterceptor
	public ResponseVo getContactUserInfo(HttpServletRequest request, @NotNull String contactId) throws BusinessException {
		TokenUserInfoDto tokenUserInfo = getTokenUserInfo(request);
		UserContact userContact = userContactService.getUserContactByUserIdAndContactId(tokenUserInfo.getUserId(), contactId);
		if (userContact == null || ArrayUtils.contains(new Integer[]{
				UserContactStatueEnum.FRIEND.getStatus(),
				UserContactStatueEnum.DEL_BE.getStatus(),
				UserContactStatueEnum.BLACKLIST_BE.getStatus()
		},userContact.getStatus())) {
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		UserInfo userInfo = userInfoService.getUserInfoByUserId(contactId);
		UserInfoVo userInfoVo = CopyTools.copy(userInfo, UserInfoVo.class);
		return getSuccessResponseVo(userInfoVo);
	}

	@RequestMapping("/delContact")
	@GlobalInterceptor
	public ResponseVo delContact(HttpServletRequest request, @NotNull String contactId) {
		TokenUserInfoDto tokenUserInfo = getTokenUserInfo(request);
		userContactService.removeUserContact(tokenUserInfo.getUserId(),contactId,UserContactStatueEnum.DEL);

		return getSuccessResponseVo(null);
	}
	@RequestMapping("/addContact2BlackList")
	@GlobalInterceptor
	public ResponseVo addContact2BlackList(HttpServletRequest request, @NotNull String contactId) {
		TokenUserInfoDto tokenUserInfo = getTokenUserInfo(request);
		userContactService.removeUserContact(tokenUserInfo.getUserId(),contactId,UserContactStatueEnum.BLACKLIST);

		return getSuccessResponseVo(null);
	}
}