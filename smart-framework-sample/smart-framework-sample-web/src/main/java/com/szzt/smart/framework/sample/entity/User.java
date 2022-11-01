package com.szzt.smart.framework.sample.entity;

import com.szzt.smart.framework.mybatis.entity.LongKeyBaseEntity;
import lombok.Data;

import javax.persistence.Table;
import java.util.Date;

/**
 * user
 *
 * @author
 * @Date 2017-10-13 13:51
 */
@Data
@Table(name = "newton_user")
public class User extends LongKeyBaseEntity
{
    private String name;

    private Integer age;

    private Date birthDate;
}
