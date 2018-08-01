package com.szzt.smart.framework.phoenix;

import lombok.Data;

/**
 * phoenix, mybatis配置属性
 *
 * @author
 * @Date 2017-10-12 16:59
 */
@Data
public class PhoenixMybatisProperties
{
    private String configLocation;

    private String mapperLocation;

    private String aliasPackage;

    private String mapperScanner;
}
