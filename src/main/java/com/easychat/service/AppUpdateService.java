package com.easychat.service;
import com.easychat.entity.po.AppUpdate;
import com.easychat.entity.query.AppUpdateQuery;

import java.io.IOException;
import java.util.List;
import com.easychat.entity.vo.PaginationResultVo;
import com.easychat.exception.BusinessException;
import org.springframework.web.multipart.MultipartFile;

/**
 *@Description: app发布Service
 *@date: 2025/04/03
 */
public interface AppUpdateService {
	/**
	 * 根据条件查询列表
	 */
	List<AppUpdate> findListByParam(AppUpdateQuery param);
	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(AppUpdateQuery param);
	/**
	 * 分页查询
	 */
	PaginationResultVo<AppUpdate> findListByPage(AppUpdateQuery param);
	/**
	 * 新增
	 */
	Integer add(AppUpdate bean);
	/**
	 * 批量新增
	 */
	Integer addBatch(List<AppUpdate> listBean);
	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<AppUpdate> bean);

	/**
	 * 根据Id查询
	 */
	AppUpdate getAppUpdateById(Integer id);

	/**
	 * 根据Id更新
	 */
	Integer updateAppUpdateById(AppUpdate bean, Integer id);

	/**
	 * 根据Id删除
	 */
	Integer deleteAppUpdateById(Integer id) throws BusinessException;

	/**
	 * 根据Version查询
	 */
	AppUpdate getAppUpdateByVersion(String version);

	/**
	 * 根据Version更新
	 */
	Integer updateAppUpdateByVersion(AppUpdate bean, String version);

	/**
	 * 根据Version删除
	 */
	Integer deleteAppUpdateByVersion(String version);

	void saveUpdate(AppUpdate appUpdate, MultipartFile file) throws BusinessException, IOException;

	void postUpdate(Integer id, Integer status, String grayscaleUid) throws BusinessException;

	AppUpdate getLatestUpdate(String version,String uid);
}