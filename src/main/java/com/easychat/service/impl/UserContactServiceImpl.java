package com.easychat.service.impl;
import com.easychat.constants.Constants;
import com.easychat.entity.dto.SysSettingDto;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.dto.UserContactSearchResultDto;
import com.easychat.entity.po.GroupInfo;
import com.easychat.entity.po.UserContactApply;
import com.easychat.entity.po.UserInfo;
import com.easychat.entity.query.*;
import com.easychat.enums.*;
import com.easychat.exception.BusinessException;
import com.easychat.mappers.GroupInfoMapper;
import com.easychat.mappers.UserContactApplyMapper;
import com.easychat.mappers.UserInfoMapper;
import com.easychat.redis.RedisComponent;
import com.easychat.service.UserContactService;
import com.easychat.entity.po.UserContact;
import com.easychat.mappers.UserContactMapper;
import com.easychat.entity.vo.PaginationResultVo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;;
import com.easychat.utils.CopyTools;
import com.easychat.utils.StringTools;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *@Description: 联系人Service
 *@date: 2025/03/31
 */
@Service("userContactService")
public class UserContactServiceImpl implements UserContactService {
	@Resource
	private UserContactMapper<UserContact,UserContactQuery> userContactMapper;

	@Resource
	private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

	@Resource
	private GroupInfoMapper<GroupInfo, GroupInfoQuery> groupInfoMapper;

	@Resource
	private UserContactApplyMapper<UserContactApply, UserContactApplyQuery> userContactApplyMapper;
    @Autowired
    private UserContactApplyServiceImpl userContactApplyService;

	@Resource
	private RedisComponent redisComponent;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<UserContact> findListByParam(UserContactQuery param){
		return this.userContactMapper.selectList(param);
	}

	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(UserContactQuery param){
		return this.userContactMapper.selectCount(param);
	}

	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVo<UserContact> findListByPage(UserContactQuery query){
		int count = this.findCountByParam(query);
		int pageSize = query.getPageSize()==null?PageSize.SIZE15.getSize():query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<UserContact> list = this.findListByParam(query);
		PaginationResultVo<UserContact> result = new PaginationResultVo(count,page.getPageSize(),page.getPageNo(),page.getTotalPage(),list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(UserContact bean){
		return this.userContactMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<UserContact> listBean){
		if(listBean==null||listBean.isEmpty()){
			return 0;
		}
		return this.userContactMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增/修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<UserContact> listBean){
		if(listBean==null||listBean.isEmpty()){
			return 0;
		}
		return this.userContactMapper.insertOrUpdateBatch(listBean);
	}


	/**
	 * 根据UserIdAndContactId查询
	 */
	@Override
	public UserContact getUserContactByUserIdAndContactId(String userId, String contactId){
		return this.userContactMapper.selectByUserIdAndContactId(userId, contactId);
	}


	/**
	 * 根据UserIdAndContactId更新
	 */
	@Override
	public Integer updateUserContactByUserIdAndContactId(UserContact bean, String userId, String contactId){
		return this.userContactMapper.updateByUserIdAndContactId(bean,userId, contactId);
	}


	/**
	 * 根据UserIdAndContactId删除
	 */
	@Override
	public Integer deleteUserContactByUserIdAndContactId(String userId, String contactId){
		return this.userContactMapper.deleteByUserIdAndContactId(userId, contactId);
	}

	@Override
	public UserContactSearchResultDto searchContact(String userId, String contactId) {
		UserContactTypeEnum typeEnum = UserContactTypeEnum.getByPrefix(contactId);
		if(typeEnum==null){
			return null;
		}
		UserContactSearchResultDto userContactSearchResultDto = new UserContactSearchResultDto();
		switch (typeEnum){
			case USER:
				UserInfo userInfo = userInfoMapper.selectByUserId(contactId);
				if(userInfo==null){
					return null;
				}
				userContactSearchResultDto = CopyTools.copy(userInfo,UserContactSearchResultDto.class);
				break;
			case GROUP:
				GroupInfo groupInfo = groupInfoMapper.selectByGroupId(contactId);
				if(groupInfo==null){
					return null;
				}
				userContactSearchResultDto.setNickName(groupInfo.getGroupName());
				break;
		}
		userContactSearchResultDto.setContactType(typeEnum.toString());
		userContactSearchResultDto.setContactId(contactId);

		if(userId.equals(contactId)) {
			userContactSearchResultDto.setStatus(UserContactStatueEnum.FRIEND.getStatus());
			return userContactSearchResultDto;
		}
		// 查询是否是好友
		UserContact userContact = this.userContactMapper.selectByUserIdAndContactId(userId, contactId);
		userContactSearchResultDto.setStatus(userContact==null?null:userContact.getStatus());

		return userContactSearchResultDto;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer applyAdd(TokenUserInfoDto tokenUserInfoDto, String contactId, String applyInfo) throws BusinessException {

		UserContactTypeEnum typeEnum = UserContactTypeEnum.getByPrefix(contactId);
		if(typeEnum==null){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		String applyUserId = tokenUserInfoDto.getUserId();
		// 默认申请信息
		applyInfo = StringUtils.isEmpty(applyInfo)? String.format(Constants.APPLY_INFO_TEMPLATE,tokenUserInfoDto.getNickname()) :applyInfo;

		Long curTime = System.currentTimeMillis();
		Integer joinType = null;
		String receiveUserId = contactId;
		// 查询是否已添加好友
		UserContact userContact = userContactMapper.selectByUserIdAndContactId(applyUserId, contactId);
		if(userContact!=null && ArrayUtils.contains(new Integer[]{
				UserContactStatueEnum.BLACKLIST_BE.getStatus(),UserContactStatueEnum.BLACKLIST_BE_FIRST.getStatus()},
				userContact.getStatus())){
			throw new BusinessException("对方已将你拉黑");
		}
		if (UserContactTypeEnum.GROUP.equals(typeEnum)) {
			GroupInfo groupInfo = groupInfoMapper.selectByGroupId(contactId);
			if(groupInfo==null || GroupStatusEnum.DISSOLUTION.getStatus().equals(groupInfo.getStatus())){
				throw new BusinessException("群聊不存在或已解散");
			}
			receiveUserId = groupInfo.getGroupOwnerId();
			joinType = groupInfo.getJoinType();

		}else {
			UserInfo userInfo = userContactMapper.selectByUserId(contactId);
			if(userInfo==null){
				throw new BusinessException(ResponseCodeEnum.CODE_600);
			}
			joinType = userInfo.getJoinType();
		}
        // 直接加入
		if(JoinTypeEnum.JOIN.getType().equals(joinType)){

			this.addContact(applyUserId,receiveUserId,contactId,typeEnum.getType(),applyInfo);
			return joinType;
		}
		UserContactApply dbApply = this.userContactApplyMapper.selectByApplyUserIdAndReceiveUserIdAndContactId(applyUserId, receiveUserId, contactId);
        UserContactApply userContactApply = new UserContactApply();
        if (dbApply==null){
            userContactApply.setApplyUserId(applyUserId);
			userContactApply.setContactType(typeEnum.getType());
			userContactApply.setReceiveUserId(receiveUserId);
			userContactApply.setContactId(contactId);
			userContactApply.setLastApplyTime(curTime);
			userContactApply.setStatus(UserContactApplyStatusEnum.INIT.getStatus());
			userContactApply.setApplyInfo(applyInfo);
			this.userContactApplyMapper.insert(userContactApply);
		}else {
            userContactApply.setStatus(UserContactApplyStatusEnum.INIT.getStatus());
			userContactApply.setLastApplyTime(curTime);
			userContactApply.setApplyInfo(applyInfo);
			this.userContactApplyMapper.updateByApplyId(userContactApply,dbApply.getApplyId());
		}

		if (dbApply == null || !UserContactApplyStatusEnum.INIT.getStatus().equals(dbApply.getStatus())) {
			// 发送ws消息
		}
		return 0;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void removeUserContact(String userId, String contactId, UserContactStatueEnum statueEnum) {
		UserContact userContact = new UserContact();
		userContact.setStatus(statueEnum.getStatus());
		userContactMapper.updateByUserIdAndContactId(userContact,userId,contactId);
		// 将好友中移除自己
		UserContact friendContact = new UserContact();
		if (UserContactStatueEnum.DEL==statueEnum) {
			friendContact.setStatus(UserContactStatueEnum.DEL.getStatus());
		}else if (UserContactStatueEnum.BLACKLIST==statueEnum) {
			friendContact.setStatus(UserContactStatueEnum.BLACKLIST_BE.getStatus());
		}
		userContactMapper.updateByUserIdAndContactId(friendContact,contactId,userId);
		// TODO 从我的好友列表缓存中删除好友

		// TODO 从好友列表缓存中删除我
	}

	@Override
	public void addContact(String applyUserId, String receiveUserId, String contactId, Integer contactType, String applyInfo) throws BusinessException {
		// 群聊人数
		if (UserContactTypeEnum.GROUP.getType().equals(contactType)) {
			UserContactQuery userContactQuery = new UserContactQuery();
			userContactQuery.setContactId(contactId);
			userContactQuery.setStatus(UserContactStatueEnum.FRIEND.getStatus());
			Integer count = userContactMapper.selectCount(userContactQuery);
			SysSettingDto sysSetting = redisComponent.getSysSetting();
			if(count >= sysSetting.getMaxGroupCount()){
				throw new BusinessException("成员已满，无法加入");
			}
		}

		Date curDate = new Date();
		// 同意 双方添加好友
		List<UserContact> contactList = new ArrayList<>();
		// 申请人添加对方
		UserContact userContact = new UserContact();
		userContact.setUserId(applyUserId);
		userContact.setContactId(contactId);
		userContact.setContactType(contactType);
		userContact.setCreatTime(curDate);
		userContact.setLastUpdateTime(curDate);
		userContact.setStatus(UserContactStatueEnum.FRIEND.getStatus());
		contactList.add(userContact);
		// 如果申请好友 接受人添加申请人 ，群组不用添加对方好友
		if (UserContactTypeEnum.USER.getType().equals(contactType)) {
			userContact = new UserContact();
			userContact.setUserId(receiveUserId);
			userContact.setContactId(applyUserId);
			userContact.setContactType(contactType);
			userContact.setCreatTime(curDate);
			userContact.setLastUpdateTime(curDate);
			userContact.setStatus(UserContactStatueEnum.FRIEND.getStatus());
			contactList.add(userContact);
		}

		// 批量插入
		userContactMapper.insertOrUpdate(contactList);

		// TODO 如果是好友，接受人也添加申请人为好友 添加缓存

		// TODO 创建会话






	}
}