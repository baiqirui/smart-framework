package com.szzt.smart.framework.sample.feign;

import java.io.Serializable;

import lombok.Data;

/**
 * @ClassName: ParamConfig
 * @Description: 参数配置领域对象
 * @author: zhengwei.bu
 * @date:2017年2月10日 上午11:31:41
 */
@Data
public class ParamConfig implements Serializable
{
    private String paramCode;
    
    private String valueCode;
    
    private String name;
    
    private String descr;
    
    private String isReadOnly;
    
    private String isSaved;
    
}
