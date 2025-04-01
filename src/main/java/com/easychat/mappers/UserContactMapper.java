package com.easychat.mappers;

import com.easychat.entity.po.UserInfo;
import org.apache.ibatis.annotations.Param;

/**
 *@Description: 联系人Mapper
 *@date: 2025/03/31
 */
public interface UserContactMapper<T,P> extends BaseMapper {

	/**
	 * 根据UserId查询
	 */
	UserInfo selectByUserId(@Param("userId") String userId);


	/**
	 * 根据UserIdAndContactId查询
	 */
	T selectByUserIdAndContactId(@Param("userId") String userId, @Param("contactId") String contactId);

	/**
	 * 根据UserIdAndContactId更新
	 */
	Integer updateByUserIdAndContactId(@Param("bean") T t, @Param("userId") String userId, @Param("contactId") String contactId);

	/**
	 * 根据UserIdAndContactId删除
	 */
	Integer deleteByUserIdAndContactId(@Param("userId") String userId, @Param("contactId") String contactId);

}