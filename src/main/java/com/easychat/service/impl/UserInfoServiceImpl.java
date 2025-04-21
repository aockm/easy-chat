package com.easychat.service.impl;
import com.easychat.constants.Constants;
import com.easychat.entity.config.AppConfig;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.po.UserContact;
import com.easychat.entity.vo.UserInfoVo;
import com.easychat.enums.*;
import com.easychat.exception.BusinessException;
import com.easychat.mappers.UserContactMapper;
import com.easychat.redis.RedisComponent;
import com.easychat.service.UserInfoService;
import com.easychat.entity.po.UserInfo;
import com.easychat.entity.query.UserInfoQuery;
import com.easychat.entity.query.SimplePage;
import com.easychat.mappers.UserInfoMapper;
import com.easychat.entity.vo.PaginationResultVo;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;;
import com.easychat.utils.CopyTools;
import com.easychat.utils.StringTools;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 *@Description: Service
 *@date: 2025/03/28
 */
@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {
	@Resource
	private UserInfoMapper<UserInfo,UserInfoQuery> userInfoMapper;

    @Resource
    private AppConfig appConfig;

	@Resource
	private RedisComponent redisComponent;
    @Autowired
    private UserContactMapper userContactMapper;

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
		// 查询联系人
		UserInfoQuery contactQuery = new UserInfoQuery();
		contactQuery.setUserId(userInfo.getUserId());
		contactQuery.setStatus(UserContactStatueEnum.FRIEND.getStatus());
		List<UserContact> contactList = userContactMapper.selectList(contactQuery);
		List<String> contactIdList = contactList.stream().map(item -> item.getContactId()).collect(Collectors.toList());
		redisComponent.clearUserContact(userInfo.getUserId());
		if (!contactIdList.isEmpty()) {
			redisComponent.addUserContactBatch(userInfo.getUserId(), contactIdList);
		}

		TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(userInfo);
		Long userHeartBeat = redisComponent.getUserHeartBeat(userInfo.getUserId());
		if(userHeartBeat != null){
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

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateUserInfo(UserInfo userInfo, MultipartFile avatarFile, MultipartFile avatarCover) throws IOException {

		if (avatarFile != null) {
			String baseFolder = appConfig.getProjectFolder()+ Constants.FILE_FOLDER_FILE;
			File targetFileFolder = new File(baseFolder+Constants.FILE_FOLDER_AVATAR_NAME);
			if (!targetFileFolder.exists()) {
				targetFileFolder.mkdirs();
			}
			String filePath = targetFileFolder.getPath()+"/" +userInfo.getUserId()+Constants.IMAGE_SUFFIX;
			avatarFile.transferTo(new File(filePath));
			avatarCover.transferTo(new File(filePath+Constants.COVER_IMAGE_SUFFIX));
		}
		UserInfo dbInfo = this.userInfoMapper.selectByUserId(userInfo.getUserId());
		String contactName = null;
		this.userInfoMapper.updateByUserId(userInfo,userInfo.getUserId());
		if (dbInfo.getNickName().equals(userInfo.getNickName())) {
			contactName = userInfo.getNickName();
		}
		// TODO 更新会话信息中昵称信息


	}

	@Override
	public void updateUserStatus(Integer status, String userId) throws BusinessException {
		UserStatusEnum statusEnum = UserStatusEnum.getByStatus(status);
		if(statusEnum==null){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		UserInfo userInfo = new UserInfo();
		userInfo.setStatus(statusEnum.getStatus());
		this.userInfoMapper.updateByUserId(userInfo,userId);
	}

	@Override
	public void forceOffline(String userId) {
		// TODO 强制下线
	}
}