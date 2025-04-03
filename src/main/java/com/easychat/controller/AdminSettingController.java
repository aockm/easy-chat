package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.constants.Constants;
import com.easychat.entity.config.AppConfig;
import com.easychat.entity.dto.SysSettingDto;
import com.easychat.entity.vo.ResponseVo;
import com.easychat.redis.RedisComponent;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/admin")
public class AdminSettingController extends ABaseController {

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private AppConfig appConfig;



    @RequestMapping("/getSysSetting")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVo getSysSetting(){

        SysSettingDto sysSettingDto = redisComponent.getSysSetting();

        return getSuccessResponseVo(null);
    }
    @RequestMapping("/saveSysSetting")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVo saveSysSetting(SysSettingDto sysSettingDto, MultipartFile avatarFile,MultipartFile avatarCover) throws IOException {
        if (avatarFile != null) {
            String baseFolder = appConfig.getProjectFolder()+ Constants.FILE_FOLDER_FILE;
            File targetFileFolder = new File(baseFolder+Constants.FILE_FOLDER_FILE);
            if (!targetFileFolder.exists()) {
                targetFileFolder.mkdirs();
            }
            String filePath = targetFileFolder.getPath() +"/" +Constants.ROBOT_UID +Constants.IMAGE_SUFFIX;
            avatarFile.transferTo(new File(filePath));
            avatarCover.transferTo(new File(filePath+Constants.COVER_IMAGE_SUFFIX));
        }


        return getSuccessResponseVo(null);
    }
}
