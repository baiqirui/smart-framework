package com.szzt.smart.framework.apigateway.filter;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.szzt.smart.framework.apigateway.config.JwtFilterConfig;
import com.szzt.smart.framework.apigateway.constants.ApiGatewayConstant;
import com.szzt.smart.framework.apigateway.feign.MobileAppFeign;
import com.szzt.smart.framework.apigateway.jwt.AccessJwtToken;
import com.szzt.smart.framework.constant.ResultCodeConstant;
import com.szzt.smart.framework.web.exception.BusinessException;
import com.szzt.smart.framework.web.model.ResultBody;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;

/**
 * jwt Token过滤器
 */
@Slf4j
public class JwtTokenFilter extends ZuulFilter
{
    // @Autowired
    // private AuthoritiesFeign authoritiesFeign;
    
    @Autowired
    private MobileAppFeign mobileAppFeign;
    
    @Autowired
    private JwtFilterConfig jwtFilterConfig;
    
    @Override
    public String filterType()
    {
        return "pre";
    }
    
    @Override
    public int filterOrder()
    {
        return 1;
    }
    
    @Override
    public boolean shouldFilter()
    {
        if (!jwtFilterConfig.getEnable())
        {
//            log.info("token验证已关闭!");
            return false;
        }
        
        List<String> excludeds = jwtFilterConfig.getExcludeds();
        if (CollectionUtils.isNotEmpty(excludeds))
        {
            RequestContext ctx = RequestContext.getCurrentContext();
            String url = ctx.getRequest().getRequestURI();
            if (excludeds.contains(url))
            {
//                log.info(url + " 接口跳过token验证");
                return false;
            }
        }
        
        return false;
    }
    
    @Override
    public Object run()
    {
        RequestContext ctx = RequestContext.getCurrentContext();
        // token校验
        HttpServletRequest request = ctx.getRequest();
        // 获取请求头信息authorization信息 Header: Authorization: Bearer <token>
        final String authHeader = request.getHeader(ApiGatewayConstant.HEADER_AUTHORIZATION);
        if (authHeader == null)
        {
            throw new BusinessException(ResultCodeConstant.PARAMETER_IS_NULL_OR_EMPTY,
                ApiGatewayConstant.HEADER_AUTHORIZATION);
        }
        if (!authHeader.startsWith(ApiGatewayConstant.HEADER_BEARER) || !authHeader.endsWith(">"))
        {
            throw new BusinessException(ResultCodeConstant.PARAMETER_INVALID, ApiGatewayConstant.HEADER_AUTHORIZATION);
        }
        
        // 获取token值
        String token = authHeader.substring(authHeader.indexOf("<") + 1, authHeader.lastIndexOf(">"));
        if (StringUtils.isBlank(token))
        {
            throw new BusinessException(ResultCodeConstant.PARAMETER_INVALID, "token");
        }
        
        // 调用用户鉴权中心，解析token;
        AccessJwtToken accessJwtToken = new AccessJwtToken(token);
        ResultBody<AccessJwtToken> resultBody = mobileAppFeign.parseToken(accessJwtToken);
        // 解析token失败
        if (null == resultBody || null == resultBody.getData() || null == resultBody.getData().getToken())
        {
            throw new BusinessException(ApiGatewayConstant.TOKEN_PARSE_ERROR);
        }
        Claims claims = resultBody.getData().getClaims();
        // 封装公共信息放入session
        request.getSession().setAttribute("userId", claims.get("userId"));
        request.getSession().setAttribute("userName", claims.get("userName"));
        
        return null;
    }
    
}