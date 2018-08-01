package com.szzt.smart.framework.apidoc.config;

import javax.sql.DataSource;

import com.apidoc.GeneratorApiDoc;
import com.szzt.smart.framework.ConditionalOnMapProperty;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.apidoc.bean.ApiDoc;
import com.apidoc.bean.ApiDocInfo;

/**
 *
 */
@Configuration
@ConditionalOnClass({ApiDoc.class, DataSource.class})
@ConditionalOnMapProperty(prefix = "apiDoc.")
@ConditionalOnProperty(value = "apiDoc.enable", matchIfMissing = true)
@EnableConfigurationProperties(ApiDocProperties.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class ApiDocConfiguration
{
    @Bean("apiDoc")
    @DependsOn(value = "apiDocInfo")
    public ApiDoc createDocket(ApiDocProperties apiDocProperties, ApiDocInfo apiDocInfo)
    {
        return new GeneratorApiDoc().setInfo(apiDocInfo)// 设置文档基本信息
            .generator(apiDocProperties.getPackageName());// 指定生成哪个包下controller的文档
    }
    
    @Bean("apiDocInfo")
    public ApiDocInfo createApi(ApiDocProperties apiDocProperties)
    {
        return new ApiDocInfo().setTitle(apiDocProperties.getTitle())
            .setVersion(apiDocProperties.getVersion())
            .setDescription(apiDocProperties.getDescription());
    }
}
