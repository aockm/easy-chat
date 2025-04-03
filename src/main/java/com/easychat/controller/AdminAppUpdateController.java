package com.easychat.controller;
import com.easychat.annotation.GlobalInterceptor;
import com.easychat.enums.AppUpdateStatusEnum;
import com.easychat.enums.ResponseCodeEnum;
import com.easychat.exception.BusinessException;
import com.easychat.service.AppUpdateService;
import com.easychat.entity.po.AppUpdate;
import com.easychat.entity.query.AppUpdateQuery;
import com.easychat.entity.vo.ResponseVo;

import java.io.IOException;
import java.util.List;
import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

/**
 *@Description: app发布Service
 *@date: 2025/04/02
 */
@RestController
@RequestMapping("appUpdate")
public class AdminAppUpdateController extends ABaseController {
	@Resource
	private AppUpdateService appUpdateService;


	@RequestMapping("/loadUpdateList")
	@GlobalInterceptor(checkAdmin = true)
	public ResponseVo loadUpdateList(AppUpdateQuery query){
		query.setOrderBy("id desc");
		return getSuccessResponseVo(appUpdateService.findListByPage(query));
	}

	@RequestMapping("/saveUpdate")
	@GlobalInterceptor(checkAdmin = true)
	public ResponseVo saveUpdate(Integer id,
								 @NotEmpty String version,
								 @NotEmpty String updateDesc,
								 @NotNull Integer fileType,
								 String outerLink,
								 MultipartFile file) throws BusinessException, IOException {
		AppUpdate appUpdate = new AppUpdate();
		appUpdate.setId(id);
		appUpdate.setVersion(version);
		appUpdate.setUpdateDesc(updateDesc);
		appUpdate.setFileType(fileType);
		if (fileType == 1){
			appUpdate.setOuterLink(outerLink);
		}
		appUpdateService.saveUpdate(appUpdate,file);
		return getSuccessResponseVo(null);
	}

	@RequestMapping("/delUpdate")
	@GlobalInterceptor(checkAdmin = true)
	public ResponseVo delUpdate(@NotNull Integer id) {

		appUpdateService.deleteAppUpdateById(id);
		return getSuccessResponseVo(null);
	}


	@RequestMapping("/postUpdate")
	@GlobalInterceptor(checkAdmin = true)
	public ResponseVo postUpdate(@NotNull Integer id,@NotNull Integer status,String grayscaleUid) throws BusinessException {
		appUpdateService.postUpdate(id, status, grayscaleUid);
		return getSuccessResponseVo(null);
	}


	@RequestMapping("/update/checkVersion")
	@GlobalInterceptor(checkAdmin = true)
	public ResponseVo checkVersion(){

		return getSuccessResponseVo(null);
	}


}