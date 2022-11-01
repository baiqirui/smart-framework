package com.szzt.smart.framework.swagger;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;


import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@ConditionalOnClass(Docket.class)
@ConditionalOnProperty(value = "smart.swagger.enable", havingValue = "true")
@EnableConfigurationProperties(SwaggerProperties.class)
public class SwaggerConfiguration
{
    
    @Bean
    @DependsOn(value = "swaggerApiInfo")
    public Docket createDocket(SwaggerProperties swaggerProperties, ApiInfo apiInfo)
    {
        Docket docket = new Docket(DocumentationType.SWAGGER_2).groupName(swaggerProperties.getGroupName())
            .useDefaultResponseMessages(false)
            .forCodeGeneration(true)
            .enableUrlTemplating(false)
            .pathMapping("/")// base，最终调用接口后会和paths拼接在一起
            .select()
            .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage()))
            .build()
            .apiInfo(apiInfo);
        
        // 添加head参数
        if (CollectionUtils.isNotEmpty(swaggerProperties.getHeaders()))
        {
            List<Parameter> paramList = new ArrayList<Parameter>();
            for (SwaggerHeader header : swaggerProperties.getHeaders())
            {
                ParameterBuilder param = new ParameterBuilder();
                param.name(header.getName())
                    .description(header.getDescription())
                    .modelRef(new ModelRef(header.getType()))
                    .parameterType("header")
                    .required(header.isRequired())
                    .build();
                paramList.add(param.build());
            }
            docket.globalOperationParameters(paramList);
        }
        
        return docket;
    }
    
    @Bean("swaggerApiInfo")
    public ApiInfo createApi(SwaggerProperties swaggerProperties)
    {
        return new ApiInfoBuilder().title(swaggerProperties.getTitle()) // 标题
            .description(swaggerProperties.getDescription()) // 描述
            .termsOfServiceUrl("") // 网址
            .version(swaggerProperties.getVersion()) // 版本号
            .build();
    }
    
}