package com.easychat.service;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.dto.UserContactSearchResultDto;
import com.easychat.entity.po.UserContact;
import com.easychat.entity.query.UserContactQuery;
import java.util.List;
import com.easychat.entity.vo.PaginationResultVo;
import com.easychat.enums.UserContactStatueEnum;
import com.easychat.exception.BusinessException;

/**
 *@Description: 联系人Service
 *@date: 2025/03/31
 */
public interface UserContactService {
	/**
	 * 根据条件查询列表
	 */
	List<UserContact> findListByParam(UserContactQuery param);
	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(UserContactQuery param);
	/**
	 * 分页查询
	 */
	PaginationResultVo<UserContact> findListByPage(UserContactQuery param);
	/**
	 * 新增
	 */
	Integer add(UserContact bean);
	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserContact> listBean);
	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<UserContact> bean);

	/**
	 * 根据UserIdAndContactId查询
	 */
	UserContact getUserContactByUserIdAndContactId(String userId, String contactId);

	/**
	 * 根据UserIdAndContactId更新
	 */
	Integer updateUserContactByUserIdAndContactId(UserContact bean, String userId, String contactId);

	/**
	 * 根据UserIdAndContactId删除
	 */
	Integer deleteUserContactByUserIdAndContactId(String userId, String contactId);

	UserContactSearchResultDto searchContact(String userId, String contactId);

	Integer applyAdd(TokenUserInfoDto tokenUserInfoDto,String contactId,String applyInfo) throws BusinessException;

	void addContact(String applyUserId,String receiveUserId,String contactId,Integer contactType, String applyInfo) throws BusinessException;

	void removeUserContact(String userId, String contactId, UserContactStatueEnum statueEnum);

	void addContact4Robot(String userId);

}