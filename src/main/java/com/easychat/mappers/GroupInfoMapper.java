package com.easychat.mappers;

import org.apache.ibatis.annotations.Param;

/**
 *@Description: Mapper
 *@date: 2025/03/31
 */
public interface GroupInfoMapper<T,P> extends BaseMapper {

	/**
	 * 根据GroupId查询
	 */
	T selectByGroupId(@Param("groupId") String groupId);

	/**
	 * 根据GroupId更新
	 */
	Integer updateByGroupId(@Param("bean") T t, @Param("groupId") String groupId);

	/**
	 * 根据GroupId删除
	 */
	Integer deleteByGroupId(@Param("groupId") String groupId);

}