package com.szzt.smart.framework.sample.entity;

import com.szzt.smart.framework.mybatis.entity.LongKeyBaseEntity;
import lombok.Data;

/**
 * user
 *
 * @author
 * @Date 2017-10-13 13:51
 */
@Data
//@Table(name = "user")
public class User extends LongKeyBaseEntity
{
    private String name;

    private Integer age;

    private Long classId;

    private Long groupId;
}
