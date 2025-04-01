package com.easychat.service;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.po.UserInfo;
import com.easychat.entity.query.UserInfoQuery;
import java.util.List;
import java.util.Map;

import com.easychat.entity.vo.PaginationResultVo;
import com.easychat.entity.vo.UserInfoVo;
import com.easychat.exception.BusinessException;

/**
 *@Description: Service
 *@date: 2025/03/28
 */
public interface UserInfoService {
	/**
	 * 根据条件查询列表
	 */
	List<UserInfo> findListByParam(UserInfoQuery param);
	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(UserInfoQuery param);
	/**
	 * 分页查询
	 */
	PaginationResultVo<UserInfo> findListByPage(UserInfoQuery param);
	/**
	 * 新增
	 */
	Integer add(UserInfo bean);
	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserInfo> listBean);
	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<UserInfo> bean);

	/**
	 * 根据UserId查询
	 */
	UserInfo getUserInfoByUserId(String userId);

	/**
	 * 根据UserId更新
	 */
	Integer updateUserInfoByUserId(UserInfo bean, String userId);

	/**
	 * 根据UserId删除
	 */
	Integer deleteUserInfoByUserId(String userId);

	void register(String email, String nickname, String password) throws BusinessException;

	UserInfoVo login(String email, String password) throws BusinessException;
}