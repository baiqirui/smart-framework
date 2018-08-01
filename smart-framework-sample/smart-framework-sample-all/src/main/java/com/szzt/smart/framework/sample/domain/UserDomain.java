package com.szzt.smart.framework.sample.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.szzt.smart.framework.web.model.PageRequest;

import lombok.Data;

/**
 * user
 *
 * @author
 * @Date 2017-10-13 13:51
 */
@Data
public class UserDomain extends PageRequest
{
    private String name;
    
    private Integer age;
    
    private Long classId;
    
    private Long groupId;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    
}
