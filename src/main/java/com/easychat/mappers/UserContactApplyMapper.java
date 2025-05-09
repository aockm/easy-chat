package com.easychat.mappers;

import org.apache.ibatis.annotations.Param;

/**
 *@Description: 联系人申请Mapper
 *@date: 2025/03/31
 */
public interface UserContactApplyMapper<T,P> extends BaseMapper {

	/**
	 * 根据ApplyId查询
	 */
	T selectByApplyId(@Param("applyId") Integer applyId);

	/**
	 * 根据ApplyId更新
	 */
	Integer updateByApplyId(@Param("bean") T t, @Param("applyId") Integer applyId);

	/**
	 * 根据ApplyId删除
	 */
	Integer deleteByApplyId(@Param("applyId") Integer applyId);

	/**
	 * 根据ApplyUserIdAndReceiveUserIdAndContactId查询
	 */
	T selectByApplyUserIdAndReceiveUserIdAndContactId(@Param("applyUserId") String applyUserId, @Param("receiveUserId") String receiveUserId, @Param("contactId") String contactId);

	/**
	 * 根据ApplyUserIdAndReceiveUserIdAndContactId更新
	 */
	Integer updateByApplyUserIdAndReceiveUserIdAndContactId(@Param("bean") T t, @Param("applyUserId") String applyUserId, @Param("receiveUserId") String receiveUserId, @Param("contactId") String contactId);

	/**
	 * 根据ApplyUserIdAndReceiveUserIdAndContactId删除
	 */
	Integer deleteByApplyUserIdAndReceiveUserIdAndContactId(@Param("applyUserId") String applyUserId, @Param("receiveUserId") String receiveUserId, @Param("contactId") String contactId);

	Integer updateByParam(@Param("bean") T t, @Param("query") P p);
}