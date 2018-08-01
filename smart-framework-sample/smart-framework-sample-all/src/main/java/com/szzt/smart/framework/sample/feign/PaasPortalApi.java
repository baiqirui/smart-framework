package com.szzt.smart.framework.sample.feign;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * paas-portal feign调用
 */
@FeignClient(name = "PAAS-PORTAL", fallbackFactory = PaasPortalApiFallback.class)
public interface PaasPortalApi {


    /**
     * 查询参数配置列表
     *
     * @param paramConfigCondition
     * @return
     */
    @RequestMapping(value = "/paramConfig/listParamConfig", method = RequestMethod.POST)
    public List<ParamConfig> listParamConfig(@RequestBody ParamConfigCondition paramConfigCondition);
}
