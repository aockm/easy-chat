package com.easychat.mappers;

import org.apache.ibatis.annotations.Param;

/**
 *@Description: app发布Mapper
 *@date: 2025/04/03
 */
public interface AppUpdateMapper<T,P> extends BaseMapper {

	/**
	 * 根据Id查询
	 */
	T selectById(@Param("id") Integer id);

	/**
	 * 根据Id更新
	 */
	Integer updateById(@Param("bean") T t, @Param("id") Integer id);

	/**
	 * 根据Id删除
	 */
	Integer deleteById(@Param("id") Integer id);

	/**
	 * 根据Version查询
	 */
	T selectByVersion(@Param("version") String version);

	/**
	 * 根据Version更新
	 */
	Integer updateByVersion(@Param("bean") T t, @Param("version") String version);

	/**
	 * 根据Version删除
	 */
	Integer deleteByVersion(@Param("version") String version);

	Integer updateByParam(@Param("bean") T t);

	T selectLatestUpdate(@Param("version")String version,@Param("uid")String uid);

}