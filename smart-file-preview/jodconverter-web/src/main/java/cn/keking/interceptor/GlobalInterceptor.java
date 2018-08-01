package cn.keking.interceptor;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 全局拦截器，用于一些全局设置
 */
public class GlobalInterceptor implements HandlerInterceptor
{
    private Logger logger = LoggerFactory.getLogger(GlobalInterceptor.class);

    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     *
     * @param request
     * @param response
     * @param handler
     * @return 有返回true才会继续向下执行，返回false取消当前请求
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception
    {
        // response.setContentType("application/json;charset=utf-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        response.setHeader("Access-Control-Max-Age", "2592000");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type,timestamp,sign,token,X-Device-Id");
//        response.setHeader("Access-Control-Allow-Headers", "*");
//        response.setHeader("Access-Control-Allow-Credentials","true");
        

        // 设置api调用的起始时间;
        request.setAttribute("api_start", System.currentTimeMillis());
        
        return true;
    }
    

    /**
     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     * 
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception[参数说明] 请参见：@see
     *             org.springframework.web.servlet.HandlerInterceptor#postHandle(javax.servlet.http.HttpServletRequest,
     *             javax.servlet.http.HttpServletResponse, java.lang.Object,
     *             org.springframework.web.servlet.ModelAndView)
     */
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
        ModelAndView modelAndView)
        throws Exception
    {
        String accept = request.getHeader("Accept");
        if (MediaType.APPLICATION_JSON_VALUE.equals(accept) || MediaType.ALL_VALUE.equals(accept))
        {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        }
        
        // API 执行时间
        Long apiStart = (Long)request.getAttribute("api_start");
        Long apiRuntime = (null != apiStart) ? new Date().getTime() - apiStart : 0;
        
        // 打印 api 访问时间
        StringBuffer requestUrl = request.getRequestURL();
        if (null != request.getQueryString())
        {
            requestUrl.append("/").append(request.getQueryString());
        }
        logger.info("api runtime " + (apiRuntime / 1000.0) + "s  url = " + requestUrl.toString());
        
    }
    
    /**
     * 在整个请求结束之后被调用，也就是在DispatcherServlet 渲染了对应的视图之后执行（主要是用于进行资源清理工作）
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
        throws Exception
    {
        
    }
    
}