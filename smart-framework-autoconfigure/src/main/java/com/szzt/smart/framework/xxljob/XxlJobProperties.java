package com.szzt.smart.framework.xxljob;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "xxljob")
public class XxlJobProperties
{
    private String addresses;

    private String appname;

    private String ip;

    private int port;

    private String logpath;

    private String accessToken;
}
