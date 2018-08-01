//package com.szzt.smart.framework.sample.feign;
//
//import com.szzt.smart.framework.web.model.ResultBody;
//import org.springframework.cloud.netflix.feign.FeignClient;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//
//@FeignClient(value = "COMPUTE-SERVICE1",  fallbackFactory = ComputetFailBackFactory.class )
//public interface ComputeClient
//{
//    @RequestMapping(method = RequestMethod.GET, value = "/add")
//    Integer add(@RequestParam(value = "a") Integer a, @RequestParam(value = "b") Integer b);
//
//    @RequestMapping(method = RequestMethod.GET, value = "/getA")
//    ResultBody getA(@RequestParam(value = "a") Integer a, @RequestHeader("sessionId") String sessionId);
//}