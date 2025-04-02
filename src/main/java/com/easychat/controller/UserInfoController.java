package com.easychat.controller;
import com.easychat.annotation.GlobalInterceptor;
import com.easychat.constants.Constants;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.vo.UserInfoVo;
import com.easychat.service.UserInfoService;
import com.easychat.entity.po.UserInfo;
import com.easychat.entity.query.UserInfoQuery;
import com.easychat.entity.vo.ResponseVo;

import java.io.IOException;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.easychat.utils.CopyTools;
import com.easychat.utils.StringTools;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

/**
 *@Description: Service
 *@date: 2025/03/28
 */
@RestController
@RequestMapping("/userInfo")
public class UserInfoController extends ABaseController {
	@Resource
	private UserInfoService userInfoService;


	@RequestMapping("/getUserInfo")
	@GlobalInterceptor
	public ResponseVo getUserInfo(HttpServletRequest request){
		TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo(request);
		UserInfo userInfo = userInfoService.getUserInfoByUserId(tokenUserInfoDto.getUserId());
		UserInfoVo userInfoVo = CopyTools.copy(userInfo, UserInfoVo.class);
		userInfoVo.setAdmin(tokenUserInfoDto.getAdmin());
		return getSuccessResponseVo(userInfoVo);
	}

	@RequestMapping("/saveUserInfo")
	@GlobalInterceptor
	public ResponseVo saveUserInfo(HttpServletRequest request, UserInfo userInfo, MultipartFile avatarFile, MultipartFile avatarCover) throws IOException {
		TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo(request);
		userInfo.setUserId(tokenUserInfoDto.getUserId());
		userInfo.setPassword(null);
		userInfo.setStatus(null);
		userInfo.setCreateTime(null);
		this.userInfoService.updateUserInfo(userInfo,avatarFile,avatarCover);

		return getUserInfo(request);
	}

	@RequestMapping("/updatePassword")
	@GlobalInterceptor
	public ResponseVo updatePassword(HttpServletRequest request,
									 @NotNull @Pattern(regexp = Constants.REGEX_PASSWORD) String password) {
		TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo(request);
		UserInfo userInfo = new UserInfo();
		userInfo.setPassword(StringTools.encodeMd5(password));
		this.userInfoService.updateUserInfoByUserId(userInfo,tokenUserInfoDto.getUserId());

		// TODO 强制退出 重新登录
		return getSuccessResponseVo(null);
	}

	@RequestMapping("/logout")
	@GlobalInterceptor
	public ResponseVo logout(HttpServletRequest request) {
		TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo(request);

		// TODO 退出登录 关闭ws连接
		
		return getSuccessResponseVo(null);
	}
}