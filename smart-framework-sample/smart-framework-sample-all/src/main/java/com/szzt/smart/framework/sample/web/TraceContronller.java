package com.szzt.smart.framework.sample.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import com.szzt.smart.framework.sample.feign.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.szzt.smart.framework.kafka.LuImmediateMessage;
import com.szzt.smart.framework.kafka.LuKafkaProducer;
import com.szzt.smart.framework.mybatis.entity.PageResult;
import com.szzt.smart.framework.sample.domain.UserDomain;
import com.szzt.smart.framework.sample.entity.User;
import com.szzt.smart.framework.sample.service.UserService;
import com.szzt.smart.framework.web.exception.ArgumentException;
import com.szzt.smart.framework.web.model.ResultBody;

import feign.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("trace")
@Api(tags = "测试接口")
public class TraceContronller
{
    
    @Autowired
    private Sample2Api sample2Api;

    @Autowired
    private Sample1Api sample1Api;

    @ApiOperation(value = "trace0", response = ResultBody.class)
    @PostMapping(value = "/trace0")
    public ResultBody<Void> trace0(HttpServletRequest request)
    {
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements())
        {
            String name = names.nextElement();
            log.info(name + " : " +  request.getHeader(name));
        }
        log.info("===<call trace-0, TraceId={}, SpanId={}>===",
                request.getHeader("X-B3-TraceId"), request.getHeader("X-B3-SpanId"));
        sample1Api.trace1();
        return ResultBody.success();
    }

    @ApiOperation(value = "trace1", response = ResultBody.class)
    @PostMapping(value = "/trace1")
    public ResultBody<Void> trace1(HttpServletRequest request)
    {
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements())
        {
            String name = names.nextElement();
            log.info(name + " : " +  request.getHeader(name));
        }
        log.info("===<call trace-1, TraceId={}, SpanId={}>===",
                request.getHeader("X-B3-TraceId"), request.getHeader("X-B3-SpanId"));
        sample2Api.trace2();
        return ResultBody.success();
    }

    @ApiOperation(value = "trace2", response = ResultBody.class)
    @PostMapping(value = "/trace2")
    public ResultBody<Void> trace2(HttpServletRequest request)
    {
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements())
        {
            String name = names.nextElement();
            log.info(name + " : " +  request.getHeader(name));
        }
        log.info("===<call trace-2, TraceId={}, SpanId={}>===",
                request.getHeader("X-B3-TraceId"), request.getHeader("X-B3-SpanId"));
        return ResultBody.success();
    }
    



}
