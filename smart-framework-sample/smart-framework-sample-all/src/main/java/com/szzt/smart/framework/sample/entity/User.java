package com.szzt.smart.framework.sample.entity;

import java.util.Date;

import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.szzt.smart.framework.mybatis.entity.LongKeyBaseEntity;

import lombok.Data;

/**
 * user
 *
 * @author
 * @Date 2017-10-13 13:51
 */
@Data
@Table(name = "user")
public class User extends LongKeyBaseEntity
{
    private String name;
    
    private Integer age;
    
    private Long classId;
    
    private Long groupId;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    
    // 分页条数
    @Transient
    private int pageSize = 10;
    
    // 当前页数
    @Transient
    private int pageNum = 1;

    private Date createTime;

    private Date lastUpdateTime;
}
