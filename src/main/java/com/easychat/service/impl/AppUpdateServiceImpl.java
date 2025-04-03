package com.easychat.service.impl;
import com.easychat.constants.Constants;
import com.easychat.entity.config.AppConfig;
import com.easychat.enums.AppUpdateFileTypeEnum;
import com.easychat.enums.AppUpdateStatusEnum;
import com.easychat.enums.ResponseCodeEnum;
import com.easychat.exception.BusinessException;
import com.easychat.service.AppUpdateService;
import com.easychat.entity.po.AppUpdate;
import com.easychat.entity.query.AppUpdateQuery;
import com.easychat.entity.query.SimplePage;
import com.easychat.mappers.AppUpdateMapper;
import com.easychat.enums.PageSize;
import com.easychat.entity.vo.PaginationResultVo;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *@Description: app发布Service
 *@date: 2025/04/03
 */
@Service("appUpdateService")
public class AppUpdateServiceImpl implements AppUpdateService {
	@Resource
	private AppUpdateMapper<AppUpdate,AppUpdateQuery> appUpdateMapper;

	@Resource
	private AppConfig appConfig;


	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<AppUpdate> findListByParam(AppUpdateQuery param){
		return this.appUpdateMapper.selectList(param);
	}

	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(AppUpdateQuery param){
		return this.appUpdateMapper.selectCount(param);
	}

	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVo<AppUpdate> findListByPage(AppUpdateQuery query){
		int count = this.findCountByParam(query);
		int pageSize = query.getPageSize()==null?PageSize.SIZE15.getSize():query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<AppUpdate> list = this.findListByParam(query);
		PaginationResultVo<AppUpdate> result = new PaginationResultVo(count,page.getPageSize(),page.getPageNo(),page.getTotalPage(),list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(AppUpdate bean){
		return this.appUpdateMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<AppUpdate> listBean){
		if(listBean==null||listBean.isEmpty()){
			return 0;
		}
		return this.appUpdateMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增/修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<AppUpdate> listBean){
		if(listBean==null||listBean.isEmpty()){
			return 0;
		}
		return this.appUpdateMapper.insertOrUpdateBatch(listBean);
	}


	/**
	 * 根据Id查询
	 */
	@Override
	public AppUpdate getAppUpdateById(Integer id){
		return this.appUpdateMapper.selectById(id);
	}


	/**
	 * 根据Id更新
	 */
	@Override
	public Integer updateAppUpdateById(AppUpdate bean, Integer id){
		return this.appUpdateMapper.updateById(bean,id);
	}


	/**
	 * 根据Id删除
	 */
	@Override
	public Integer deleteAppUpdateById(Integer id) throws BusinessException {
		AppUpdate dbInfo = this.getAppUpdateById(id);
		if(!AppUpdateStatusEnum.INIT.getStatus().equals(dbInfo.getStatus())){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		return this.appUpdateMapper.deleteById(id);
	}


	/**
	 * 根据Version查询
	 */
	@Override
	public AppUpdate getAppUpdateByVersion(String version){
		return this.appUpdateMapper.selectByVersion(version);
	}


	/**
	 * 根据Version更新
	 */
	@Override
	public Integer updateAppUpdateByVersion(AppUpdate bean, String version){
		return this.appUpdateMapper.updateByVersion(bean,version);
	}


	/**
	 * 根据Version删除
	 */
	@Override
	public Integer deleteAppUpdateByVersion(String version){
		return this.appUpdateMapper.deleteByVersion(version);
	}

	@Override
	public void saveUpdate(AppUpdate appUpdate, MultipartFile file) throws BusinessException, IOException {
		AppUpdateFileTypeEnum fileTypeEnum = AppUpdateFileTypeEnum.getByType(appUpdate.getFileType());
		if(fileTypeEnum==null){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		if (appUpdate.getId()!=null){
			AppUpdate dbInfo = this.getAppUpdateById(appUpdate.getId());
			if(!AppUpdateStatusEnum.INIT.getStatus().equals(dbInfo.getStatus())){
				throw new BusinessException(ResponseCodeEnum.CODE_600);
			}
		}
		AppUpdateQuery appUpdateQuery = new AppUpdateQuery();
		appUpdateQuery.setOrderBy("id desc");
		appUpdateQuery.setSimplePage(new SimplePage(0,1));
		List<AppUpdate> appUpdateList = appUpdateMapper.selectList(appUpdateQuery);
		if(!appUpdateList.isEmpty()){
			AppUpdate latest = appUpdateList.get(0);
			Long dbVersion = Long.parseLong(latest.getVersion().replace(".",""));
			Long currentVersion = Long.parseLong(appUpdate.getVersion().replace(".",""));
			if(appUpdate.getId() == null && currentVersion <= dbVersion){
				throw new BusinessException("当前版本必须大于历史版本");
			}
			if(appUpdate.getId() != null && currentVersion <= dbVersion && !appUpdate.getId().equals(latest.getId())){
				throw new BusinessException("当前版本必须大于历史版本");
			}
			AppUpdate dbUpdate = appUpdateMapper.selectByVersion(appUpdate.getVersion());
			if(dbUpdate!=null&&dbUpdate.getId()!=null&& !dbUpdate.getId().equals(latest.getId())){
				throw new BusinessException("版本号已存在");
			}
		}


		if(appUpdate.getId()==null){
			appUpdate.setCreatTime(new Date());
			appUpdate.setStatus(AppUpdateStatusEnum.INIT.getStatus());
			appUpdateMapper.insert(appUpdate);
		}else {
			appUpdateMapper.updateById(appUpdate,appUpdate.getId());
		}

		if (file!=null) {
			File baseFolder = new File(appConfig.getProjectFolder() + Constants.APP_UPDATE_FOLDER);
			if (!baseFolder.exists()) {
				baseFolder.mkdirs();
			}
			file.transferTo(new File(baseFolder.getAbsolutePath()+"/"+appUpdate.getId()+Constants.APP_TYPE_SUFFIX));

		}

	}

	@Override
	public void postUpdate(Integer id, Integer status, String grayscaleUid) throws BusinessException {
		AppUpdateStatusEnum statusEnum = AppUpdateStatusEnum.getByStatus(status);

		if(statusEnum==null){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		if (AppUpdateStatusEnum.GRAYSCALE == statusEnum && StringUtils.isEmpty(grayscaleUid)) {
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		if (AppUpdateStatusEnum.GRAYSCALE != statusEnum) {
			grayscaleUid = "";
		}

		AppUpdate appUpdate = new AppUpdate();
		appUpdate.setStatus(status);
		appUpdate.setGrayscaleUid(grayscaleUid);
		appUpdateMapper.updateById(appUpdate,id);
	}
}