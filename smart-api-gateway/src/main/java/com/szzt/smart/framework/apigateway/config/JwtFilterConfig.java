package com.szzt.smart.framework.apigateway.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtFilterConfig {

	private List<String> excludeds;

	private Boolean enable;
}
