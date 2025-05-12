package com.easychat.service.impl;
import com.easychat.entity.dto.SysSettingDto;
import com.easychat.entity.po.UserContact;
import com.easychat.entity.query.UserContactQuery;
import com.easychat.enums.*;
import com.easychat.exception.BusinessException;
import com.easychat.mappers.UserContactMapper;
import com.easychat.redis.RedisComponent;
import com.easychat.redis.RedisUtils;
import com.easychat.service.UserContactApplyService;
import com.easychat.entity.po.UserContactApply;
import com.easychat.entity.query.UserContactApplyQuery;
import com.easychat.entity.query.SimplePage;
import com.easychat.mappers.UserContactApplyMapper;
import com.easychat.entity.vo.PaginationResultVo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;;
import com.easychat.service.UserContactService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *@Description: 联系人申请Service
 *@date: 2025/03/31
 */
@Service("userContactApplyService")
public class UserContactApplyServiceImpl implements UserContactApplyService {
	@Resource
	private UserContactApplyMapper<UserContactApply,UserContactApplyQuery> userContactApplyMapper;
	@Resource
	private UserContactMapper<UserContact, UserContactQuery> userContactMapper;

	@Resource
	private UserContactService userContactService;


	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<UserContactApply> findListByParam(UserContactApplyQuery param){
		return this.userContactApplyMapper.selectList(param);
	}

	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(UserContactApplyQuery param){
		return this.userContactApplyMapper.selectCount(param);
	}

	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVo<UserContactApply> findListByPage(UserContactApplyQuery query){
		int count = this.findCountByParam(query);
		int pageSize = query.getPageSize()==null?PageSize.SIZE15.getSize():query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<UserContactApply> list = this.findListByParam(query);
		PaginationResultVo<UserContactApply> result = new PaginationResultVo(count,page.getPageSize(),page.getPageNo(),page.getTotalPage(),list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(UserContactApply bean){
		return this.userContactApplyMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<UserContactApply> listBean){
		if(listBean==null||listBean.isEmpty()){
			return 0;
		}
		return this.userContactApplyMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增/修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<UserContactApply> listBean){
		if(listBean==null||listBean.isEmpty()){
			return 0;
		}
		return this.userContactApplyMapper.insertOrUpdateBatch(listBean);
	}


	/**
	 * 根据ApplyId查询
	 */
	@Override
	public UserContactApply getUserContactApplyByApplyId(Integer applyId){
		return this.userContactApplyMapper.selectByApplyId(applyId);
	}


	/**
	 * 根据ApplyId更新
	 */
	@Override
	public Integer updateUserContactApplyByApplyId(UserContactApply bean, Integer applyId){
		return this.userContactApplyMapper.updateByApplyId(bean,applyId);
	}


	/**
	 * 根据ApplyId删除
	 */
	@Override
	public Integer deleteUserContactApplyByApplyId(Integer applyId){
		return this.userContactApplyMapper.deleteByApplyId(applyId);
	}


	/**
	 * 根据ApplyUserIdAndReceiveUserIdAndContactId查询
	 */
	@Override
	public UserContactApply getUserContactApplyByApplyUserIdAndReceiveUserIdAndContactId(String applyUserId, String receiveUserId, String contactId){
		return this.userContactApplyMapper.selectByApplyUserIdAndReceiveUserIdAndContactId(applyUserId, receiveUserId, contactId);
	}


	/**
	 * 根据ApplyUserIdAndReceiveUserIdAndContactId更新
	 */
	@Override
	public Integer updateUserContactApplyByApplyUserIdAndReceiveUserIdAndContactId(UserContactApply bean, String applyUserId, String receiveUserId, String contactId){
		return this.userContactApplyMapper.updateByApplyUserIdAndReceiveUserIdAndContactId(bean,applyUserId, receiveUserId, contactId);
	}


	/**
	 * 根据ApplyUserIdAndReceiveUserIdAndContactId删除
	 */
	@Override
	public Integer deleteUserContactApplyByApplyUserIdAndReceiveUserIdAndContactId(String applyUserId, String receiveUserId, String contactId){
		return this.userContactApplyMapper.deleteByApplyUserIdAndReceiveUserIdAndContactId(applyUserId, receiveUserId, contactId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void dealWithApply(String userId, Integer applyId, Integer status) throws BusinessException {
		UserContactApplyStatusEnum statusEnum = UserContactApplyStatusEnum.getByStatus(status);
		if(statusEnum==null ||UserContactApplyStatusEnum.INIT==statusEnum){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		UserContactApply applyInfo = this.userContactApplyMapper.selectByApplyId(applyId);
		if(applyInfo==null || !userId.equals(applyInfo.getReceiveUserId())){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		UserContactApply updateInfo = new UserContactApply();
		updateInfo.setStatus(status);
		updateInfo.setLastApplyTime(System.currentTimeMillis());

		UserContactApplyQuery applyQuery = new UserContactApplyQuery();
		applyQuery.setApplyId(applyId);
		applyQuery.setStatus(UserContactApplyStatusEnum.INIT.getStatus());
		Integer count = userContactApplyMapper.updateByParam(updateInfo, applyQuery);
		if(count==0){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		if (UserContactApplyStatusEnum.PASS.getStatus().equals(applyInfo.getStatus())) {

			userContactService.addContact(applyInfo.getApplyUserId(),applyInfo.getReceiveUserId(),applyInfo.getContactId(),applyInfo.getContactType(),applyInfo.getApplyInfo());

			return;
		}

		if (UserContactApplyStatusEnum.BLACKLIST == statusEnum){
			Date date = new Date();
			UserContact userContact = new UserContact();
			userContact.setUserId(applyInfo.getApplyUserId());
			userContact.setContactId(applyInfo.getContactId());
			userContact.setContactType(applyInfo.getContactType());
			userContact.setCreateTime(date);
			userContact.setStatus(UserContactApplyStatusEnum.BLACKLIST.getStatus());
			userContact.setLastUpdateTime(date);
			userContactMapper.insertOrUpdate(userContact);
		}

	}


}