package com.szzt.smart.framework.web.util;

/**
 * 表示 Date 的某一部分的值。
 * @author:  yidi
 */
public enum DatePart
{
    /**
     * 表示年份部分。
     */
    Year,

    /**
     * 表示月份部分。
     */
    Month,

    /**
     * 表示该月中的第几天。
     */
    Day,

    /**
     * 表示小时部分。
     */
    Hour,

    /**
     * 表示分钟部分。
     */
    Minute,

    /**
     * 表示秒部分。
     */
    Second,
    
    /**
     * 表示毫秒部分。
     */
    Millisecond;
}
