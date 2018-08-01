package com.szzt.smart.framework.sample.feign;

import feign.Response;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "PAAS-FILE", fallbackFactory = PaasFileApiFallback.class)
public interface PaasFileApi
{
    
    @RequestMapping(value = "/file/getFile", method = RequestMethod.GET)
    public Response getFile(@RequestParam("fileId") String fileId);
}
