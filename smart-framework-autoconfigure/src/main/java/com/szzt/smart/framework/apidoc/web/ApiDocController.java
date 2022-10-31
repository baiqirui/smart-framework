//package com.szzt.smart.framework.apidoc.web;
//
//import javax.sql.DataSource;
//
//import com.apidoc.GeneratorApiDoc;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.AutoConfigureAfter;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.apidoc.bean.ApiDoc;
//import com.apidoc.bean.ApiDocAction;
//import com.szzt.smart.framework.apidoc.config.ApiDocConfiguration;
//import com.szzt.smart.framework.mybatis.config.DataSourceConfigruation;
//
///**
// * @Description: 文档生成controller
// * @Author: admin
// * @CreateDate: 2018/1/8 16:12
// */
//@RequestMapping("/apidoc")
//@RestController
//@ConditionalOnClass({ApiDoc.class,DataSource.class})
//@AutoConfigureAfter(value = {ApiDocConfiguration.class, DataSourceConfigruation.class})
//public class ApiDocController
//{
//
//    @Autowired
//    private ApiDoc apiDoc;
//
//    @Autowired
//    private DataSource dataSource;
//
//    /**
//     * 后台管理系统文档
//     * <p>
//     * 返回文档基本信息和目录
//     */
//    @GetMapping("/api/{type}")
//    public ApiDoc admin(@PathVariable("type") String type)
//    {
//        return apiDoc;
//    }
//
//    /**
//     * 模块详细信息
//     */
//    @GetMapping("/detail")
//    public ApiDocAction detail(String methodUUID)
//    {
//        ApiDocAction detail = new GeneratorApiDoc()
//            // 设置数据库连接信息，可忽略
//
//            .getApiOfMethod(methodUUID);
//
//        return detail;
//    }
//
//}
