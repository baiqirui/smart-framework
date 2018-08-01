package com.szzt.smart.framework.apidoc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @Description: 用户实体类
 * @Author: peng.liu
 * @CreateDate: 2018/4/15 17:10
 */
@Data
@ConfigurationProperties(prefix = "apiDoc")
public class ApiDocProperties {

    private boolean enable;//是否开启

    private String title; // 标题

    private String description; // 描述

    private String version; // 版本

    private String packageName;//指定生成哪个包下controller的文档
}
