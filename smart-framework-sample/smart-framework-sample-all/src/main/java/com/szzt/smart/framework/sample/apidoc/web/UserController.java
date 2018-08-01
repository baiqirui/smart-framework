package com.szzt.smart.framework.sample.apidoc.web;

import com.szzt.smart.framework.sample.apidoc.Result;
import com.szzt.smart.framework.sample.apidoc.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apidoc.annotation.*;
import com.apidoc.enumeration.DataType;
import com.apidoc.enumeration.Method;
import com.apidoc.enumeration.ParamType;

/**
 * @Description: 用户Controller
 *               <p>
 *               用以展示api-doc的用法
 * @Author: peng.liu
 * @CreateDate: 2018/4/15 17:18
 */
@Api(name = "用户管理", mapping = "user")
@RestController
@RequestMapping("/user")
public class UserController
{
    
    // --------------演示get请求--------------------
    // 演示： get请求 无参数
    @ApiAction(name = "获取用户信息", mapping = "/get", method = Method.GET)
    @ApiRespParams({@ApiParam(name = "code", dataType = DataType.NUMBER, defaultValue = "0", description = "状态编码"),
        @ApiParam(name = "data", dataType = DataType.OBJECT, defaultValue = "null", description = "响应数据", object = "user"),
        @ApiParam(name = "name", dataType = DataType.STRING, defaultValue = "", description = "姓名", belongTo = "user"),
        @ApiParam(name = "age", dataType = DataType.NUMBER, defaultValue = "", description = "年龄", belongTo = "user"),
        
        @ApiParam(name = "message", dataType = DataType.STRING, defaultValue = "", description = "提示信息")})
    @GetMapping("/get")
    public Result getUser()
    {
        return Result.success(new User("1001", "一切问题，都只是时间问题。", 18));
    }
    
    // 演示：get请求 url带参数（标准RESTful风格参数）
    @ApiAction(name = "通过id获得用户信息", mapping = "/get/rest", method = Method.GET)
    @ApiReqParams(type = ParamType.URL, value = {
        @ApiParam(name = "id", dataType = DataType.STRING, defaultValue = "", description = "用户id")})
    @ApiRespParams({@ApiParam(name = "code", dataType = DataType.NUMBER, defaultValue = "0", description = "状态编码"),
        @ApiParam(name = "data", dataType = DataType.OBJECT, defaultValue = "null", description = "响应数据", object = "user"),
        @ApiParam(name = "name", dataType = DataType.STRING, defaultValue = "", description = "姓名", belongTo = "user"),
        @ApiParam(name = "age", dataType = DataType.NUMBER, defaultValue = "", description = "年龄", belongTo = "user"),
        
        @ApiParam(name = "message", dataType = DataType.STRING, defaultValue = "", description = "提示信息")})
    @GetMapping("/get/rest/{id}")
    public Result getUserByID(@PathVariable("id") String id)
    {
        return Result.success(new User("1001", "毁灭你们，与你何干！", 18));
    }


    
}
