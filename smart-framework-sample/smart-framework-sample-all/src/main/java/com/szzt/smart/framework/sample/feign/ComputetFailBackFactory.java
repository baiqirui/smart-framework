//package com.szzt.smart.framework.sample.feign;
//
//import java.util.List;
//
//import com.szzt.smart.framework.feign.FeignConfiguration;
//import com.szzt.smart.framework.web.model.ResultBody;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//
//import feign.hystrix.FallbackFactory;
//import lombok.extern.slf4j.Slf4j;
//
///**
// * Created by Hikaru on 17/8/16.
// */
//@Component
//@Slf4j
//public class ComputetFailBackFactory implements FallbackFactory<ComputeClient> {
//
//  @Autowired
//  private FeignConfiguration.FeignExceptionHandler feignExceptionHandler;
//
//  @Override
//  public ComputeClient create(final Throwable throwable) {
//    return new ComputeClient() {
//      @Override
//      public Integer add(Integer a, Integer b) {
//        return null;
//      }
//
//      @Override
//      public ResultBody getA(Integer a, String sessionId) {
//        return feignExceptionHandler.handleException(throwable);
//      }
//    };
//  }
//}
