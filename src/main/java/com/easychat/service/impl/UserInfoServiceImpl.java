package com.easychat.service.impl;
import com.easychat.entity.config.AppConfig;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.vo.UserInfoVo;
import com.easychat.enums.JoinTypeEnum;
import com.easychat.enums.UserContactTypeEnum;
import com.easychat.enums.UserStatusEnum;
import com.easychat.exception.BusinessException;
import com.easychat.redis.RedisComponent;
import com.easychat.service.UserInfoService;
import com.easychat.entity.po.UserInfo;
import com.easychat.entity.query.UserInfoQuery;
import com.easychat.entity.query.SimplePage;
import com.easychat.mappers.UserInfoMapper;
import com.easychat.enums.PageSize;
import com.easychat.entity.vo.PaginationResultVo;

import java.util.*;
import javax.annotation.Resource;;
import com.easychat.utils.CopyTools;
import com.easychat.utils.StringTools;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *@Description: Service
 *@date: 2025/03/28
 */
@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {
	@Resource
	private UserInfoMapper<UserInfo,UserInfoQuery> userInfoMapper;
    @Autowired
    private AppConfig appConfig;

	@Resource
	private RedisComponent redisComponent;
	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<UserInfo> findListByParam(UserInfoQuery param){
		return this.userInfoMapper.selectList(param);
	}

	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(UserInfoQuery param){
		return this.userInfoMapper.selectCount(param);
	}

	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVo<UserInfo> findListByPage(UserInfoQuery query){
		int count = this.findCountByParam(query);
		int pageSize = query.getPageSize()==null?PageSize.SIZE15.getSize():query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<UserInfo> list = this.findListByParam(query);
		PaginationResultVo<UserInfo> result = new PaginationResultVo(count,page.getPageSize(),page.getPageNo(),page.getTotalPage(),list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(UserInfo bean){
		return this.userInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<UserInfo> listBean){
		if(listBean==null||listBean.isEmpty()){
			return 0;
		}
		return this.userInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增/修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<UserInfo> listBean){
		if(listBean==null||listBean.isEmpty()){
			return 0;
		}
		return this.userInfoMapper.insertOrUpdateBatch(listBean);
	}


	/**
	 * 根据UserId查询
	 */
	@Override
	public UserInfo getUserInfoByUserId(String userId){
		return this.userInfoMapper.selectByUserId(userId);
	}


	/**
	 * 根据UserId更新
	 */
	@Override
	public Integer updateUserInfoByUserId(UserInfo bean, String userId){
		return this.userInfoMapper.updateByUserId(bean,userId);
	}


	/**
	 * 根据UserId删除
	 */
	@Override
	public Integer deleteUserInfoByUserId(String userId){
		return this.userInfoMapper.deleteByUserId(userId);
	}



	@Override
	public void register(String email, String nickname, String password) throws BusinessException {
		UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
		if(userInfo != null){
			throw new BusinessException("邮箱账号已存在");
		}

			String userId = StringTools.getUserId();
			userInfo = new UserInfo();
			userInfo.setUserId(userId);
			userInfo.setEmail(email);
			userInfo.setNickName(nickname);
			userInfo.setPassword(StringTools.encodeMd5(password));
			userInfo.setCreateTime(new Date());
			userInfo.setJoinType(JoinTypeEnum.APPLY.getType());
			userInfo.setStatus(UserStatusEnum.ENABLE.getStatus());
			this.userInfoMapper.insert(userInfo);

	}


	@Override
	public UserInfoVo login(String email, String password) throws BusinessException {
		UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
		if (null==userInfo || !userInfo.getPassword().equals(StringTools.encodeMd5(password))) {
			throw new BusinessException("账号或密码不存在");
		}
		if (UserStatusEnum.DISABLE.getStatus().equals(userInfo.getStatus())) {
			throw new BusinessException("账号已禁用");
		}
		TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(userInfo);
		Long userHeartBeat = redisComponent.getUserHeartBeat(userInfo.getUserId());
		if(userHeartBeat==null){
			throw new BusinessException("此账号已在别处登录，请退出后再登录");
		}
		// 保存登录信息到redis中
		String token = StringTools.encodeMd5(userInfo.getPassword() + StringTools.getRandomString(20));
		tokenUserInfoDto.setToken(token);
		redisComponent.saveTokenUserInfoDto(tokenUserInfoDto);
		UserInfoVo userInfoVo = CopyTools.copy(userInfo, UserInfoVo.class);
		userInfoVo.setAdmin(tokenUserInfoDto.getAdmin());
		userInfoVo.setToken(token);

		return userInfoVo;
	}

	private TokenUserInfoDto getTokenUserInfoDto(UserInfo userInfo){
		TokenUserInfoDto tokenUserInfoDto = new TokenUserInfoDto();
		tokenUserInfoDto.setUserId(userInfo.getUserId());
		tokenUserInfoDto.setNickname(userInfo.getNickName());
		String adminEmails = appConfig.getAdminEmails();
		if (!StringUtils.isEmpty(adminEmails) && ArrayUtils.contains(adminEmails.split(","), userInfo.getEmail())) {
			tokenUserInfoDto.setAdmin(true);
		}else {
			tokenUserInfoDto.setAdmin(false);
		}
		return tokenUserInfoDto;
	}
}