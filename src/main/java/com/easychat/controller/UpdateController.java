package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.constants.Constants;
import com.easychat.entity.config.AppConfig;
import com.easychat.entity.po.AppUpdate;
import com.easychat.entity.vo.AppUpdateVo;
import com.easychat.entity.vo.ResponseVo;
import com.easychat.enums.AppUpdateFileTypeEnum;
import com.easychat.service.AppUpdateService;
import com.easychat.utils.CopyTools;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.util.Arrays;

@RestController
@RequestMapping("/update")
public class UpdateController extends ABaseController {

    @Resource
    private AppUpdateService appUpdateService;

    @Resource
    private AppConfig appConfig;

    @RequestMapping("/checkVersion")
    @GlobalInterceptor
    public ResponseVo checkVersion(String version,String uid) {
        if (StringUtils.isBlank(version)) {
            return getSuccessResponseVo(null);
        }
        AppUpdate appUpdate = appUpdateService.getLatestUpdate(version, uid);
        if (appUpdate == null) {
            return getSuccessResponseVo(null);
        }
        AppUpdateVo appUpdateVo = CopyTools.copy(appUpdate,AppUpdateVo.class);
        if (AppUpdateFileTypeEnum.LOCAL.getType().equals(appUpdateVo.getFileType())) {
            File file = new File(appConfig.getProjectFolder()+ Constants.APP_UPDATE_FOLDER+appUpdate.getId()+Constants.APP_TYPE_SUFFIX);
            appUpdateVo.setSize(file.length());
        }else {
            appUpdateVo.setSize(0L);
        }
        appUpdateVo.setUpdateList(Arrays.asList(appUpdate.getUpdateDescArray()));
        String fileName = Constants.APP_NAME+appUpdate.getVersion()+Constants.APP_TYPE_SUFFIX;
        appUpdateVo.setFileName(fileName);

        return getSuccessResponseVo(appUpdateVo);
    }
}
