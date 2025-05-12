package com.easychat.service.impl;
import com.easychat.constants.Constants;
import com.easychat.entity.config.AppConfig;
import com.easychat.entity.dto.SysSettingDto;
import com.easychat.entity.po.UserContact;
import com.easychat.entity.query.UserContactQuery;
import com.easychat.entity.query.UserInfoQuery;
import com.easychat.enums.*;
import com.easychat.exception.BusinessException;
import com.easychat.mappers.UserContactMapper;
import com.easychat.redis.RedisComponent;
import com.easychat.redis.RedisUtils;
import com.easychat.service.GroupInfoService;
import com.easychat.entity.po.GroupInfo;
import com.easychat.entity.query.GroupInfoQuery;
import com.easychat.entity.query.SimplePage;
import com.easychat.mappers.GroupInfoMapper;
import com.easychat.entity.vo.PaginationResultVo;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;;
import com.easychat.utils.StringTools;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 *@Description: Service
 *@date: 2025/03/31
 */
@Service("groupInfoService")
public class GroupInfoServiceImpl implements GroupInfoService {
	@Resource
	private GroupInfoMapper<GroupInfo,GroupInfoQuery> groupInfoMapper;
    @Resource
    private RedisComponent redisComponent;

	@Resource
	private AppConfig appConfig;

	@Resource
	private UserContactMapper<UserContact, UserContactQuery> userContactMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<GroupInfo> findListByParam(GroupInfoQuery param){
		return this.groupInfoMapper.selectList(param);
	}

	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(GroupInfoQuery param){
		return this.groupInfoMapper.selectCount(param);
	}

	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVo<GroupInfo> findListByPage(GroupInfoQuery query){
		int count = this.findCountByParam(query);
		int pageSize = query.getPageSize()==null?PageSize.SIZE15.getSize():query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<GroupInfo> list = this.findListByParam(query);
		PaginationResultVo<GroupInfo> result = new PaginationResultVo(count,page.getPageSize(),page.getPageNo(),page.getTotalPage(),list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(GroupInfo bean){
		return this.groupInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<GroupInfo> listBean){
		if(listBean==null||listBean.isEmpty()){
			return 0;
		}
		return this.groupInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增/修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<GroupInfo> listBean){
		if(listBean==null||listBean.isEmpty()){
			return 0;
		}
		return this.groupInfoMapper.insertOrUpdateBatch(listBean);
	}


	/**
	 * 根据GroupId查询
	 */
	@Override
	public GroupInfo getGroupInfoByGroupId(String groupId){
		return this.groupInfoMapper.selectByGroupId(groupId);
	}


	/**
	 * 根据GroupId更新
	 */
	@Override
	public Integer updateGroupInfoByGroupId(GroupInfo bean, String groupId){
		return this.groupInfoMapper.updateByGroupId(bean,groupId);
	}


	/**
	 * 根据GroupId删除
	 */
	@Override
	public Integer deleteGroupInfoByGroupId(String groupId){
		return this.groupInfoMapper.deleteByGroupId(groupId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveGroup(GroupInfo groupInfo, MultipartFile avatarFile, MultipartFile avatarCover) throws BusinessException, IOException {

		Date currentDate = new Date();
		if (StringUtils.isEmpty(groupInfo.getGroupId())) {
			GroupInfoQuery groupInfoQuery = new GroupInfoQuery();
			groupInfoQuery.setGroupId(groupInfo.getGroupId());
			Integer count = this.groupInfoMapper.selectCount(groupInfoQuery);
			SysSettingDto sysSetting = redisComponent.getSysSetting();
			if (count >= sysSetting.getMaxGroupCount()){
				throw new BusinessException("最多支持创建:"+sysSetting.getMaxGroupCount()+"个群聊");
			}

			if (null == avatarFile){
				throw new BusinessException(ResponseCodeEnum.CODE_600);
			}
			groupInfo.setCrateTime(currentDate);
			groupInfo.setGroupId(StringTools.getUserId());
			this.groupInfoMapper.insert(groupInfo);

			// 将群组添加为联系人
			UserContact userContact = new UserContact();
			userContact.setStatus(UserContactStatueEnum.FRIEND.getStatus());
			userContact.setContactType(UserContactTypeEnum.GROUP.getType());
			userContact.setContactId(groupInfo.getGroupId());
			userContact.setUserId(groupInfo.getGroupId());
			userContact.setCreateTime(currentDate);
			this.userContactMapper.insert(userContact);
			// 创建会话
			// 发送消息
		}else {
			GroupInfo dbInfo = this.groupInfoMapper.selectByGroupId(groupInfo.getGroupId());
			if (!dbInfo.getGroupOwnerId().equals(groupInfo.getGroupOwnerId())){
				throw new BusinessException(ResponseCodeEnum.CODE_600);
			}
			this.groupInfoMapper.updateByGroupId(groupInfo,groupInfo.getGroupId());
			// 更新相关表冗余信息

			// 修改群昵称发送ws消息

		}

		if (avatarFile == null){
			return;
		}
		String baseFolder = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE;
		File targetFileFolder = new File(baseFolder +Constants.FILE_FOLDER_AVATAR_NAME);
		if (!targetFileFolder.exists()){
			targetFileFolder.mkdirs();
		}
		String filePath = targetFileFolder.getPath()+"/" + groupInfo.getGroupId();
		avatarFile.transferTo(new File(filePath+Constants.IMAGE_SUFFIX));
		avatarCover.transferTo(new File(filePath+Constants.COVER_IMAGE_SUFFIX));
	}

	@Override
	public void dissolutionGroup(String groupOwnerId, String groupId) throws BusinessException {
		GroupInfo dbInfo = this.groupInfoMapper.selectByGroupId(groupId);
		if (dbInfo == null || !dbInfo.getGroupOwnerId().equals(groupOwnerId)){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		// 删除群组
		GroupInfo updateInfo = new GroupInfo();
		updateInfo.setStatus(GroupStatusEnum.DISSOLUTION.getStatus());
		this.groupInfoMapper.updateByGroupId(updateInfo,groupId);

		// 更新联系人信息
		UserContactQuery userContactQuery = new UserContactQuery();
		userContactQuery.setContactId(groupId);
		userContactQuery.setContactType(UserContactTypeEnum.GROUP.getType());
		UserContact updateContact = new UserContact();
		updateContact.setStatus(UserContactStatueEnum.DEL.getStatus());
		this.userContactMapper.updateByParam(updateContact,userContactQuery);

		// TODO 移除相关群员的联系人缓存

		// TODO 发消息 1、更新会话信息 2、记录群消息 3、发送群解散通知信息

	}
}