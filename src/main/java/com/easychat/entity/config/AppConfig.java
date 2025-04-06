package com.easychat.entity.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("appConfig")
public class AppConfig {

    @Value("${ws.port:}")
    private Integer port;

    @Value("${project.folder:}")
    private String  projectFolder;

    @Value("${admin.emails:}")
    private String  adminEmails;

    public Integer getWsPort() {
        return port;
    }

    public String getProjectFolder() {
        if (StringUtils.isBlank(projectFolder) && !projectFolder.endsWith("/")) {
            projectFolder = projectFolder + "/";
        }
        return projectFolder;
    }

    public String getAdminEmails() {
        return adminEmails;
    }
}
