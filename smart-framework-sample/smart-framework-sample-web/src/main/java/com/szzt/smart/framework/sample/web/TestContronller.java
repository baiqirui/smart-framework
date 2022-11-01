package com.szzt.smart.framework.sample.web;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("test")
@Api(tags = "测试接口")
public class TestContronller
{
//    @Autowired
//    private UserService userService;
    
    @GetMapping
    public String test1()
    {
        return "Hello world";
    }
    
//    // @Transactional
//    @PostMapping("addUser")
//    // @Validated
//    public ResultBody addUser(@RequestBody @ApiParam("用户信息") User user)
//    {
//        userService.insert(user);
//        return ResultBody.success();
//    }
//
//    @PostMapping("test3")
//    @ApiOperation(value = "测试3", response = User.class)
//    public User test3(@RequestBody @ApiParam("请求参数") @Validated User user)
//    {
//        throw ArgumentException.createNullOrBlankString("name");
//    }
//
//    @GetMapping("/{id}")
//    @ApiOperation(value = "根据ID获取用户", response = ResultBody.class)
//    public ResultBody getUser(@PathVariable @ApiParam("用户ID") Long id)
//    {
//        log.debug("参数：" + id);
//        User user = userService.get(id);
//        log.info("查询结果：" + user);
//        return ResultBody.success(user);
//    }
//
//    @PostMapping("/list")
//    @ApiOperation(value = "获取用户列表", response = ResultBody.class)
//    public ResultBody listUser(@RequestBody User user)
//    {
//        List<User> userList = userService.select(user);
//        return ResultBody.success(userList);
//    }
//
//    @PutMapping("/{id}")
//    @ApiOperation(value = "修改用户", response = ResultBody.class)
//    public ResultBody modifyUser(@RequestBody User user, @PathVariable @ApiParam("用户ID") Long id)
//    {
//        user.setId(id);
//        userService.update(user);
//        return ResultBody.success();
//    }
//
//    @DeleteMapping("/{id}")
//    @ApiOperation(value = "删除用户", response = ResultBody.class)
//    public ResultBody deletetUser(@PathVariable @ApiParam("用户ID") Long id)
//    {
//        userService.deleteById(id);
//        return ResultBody.success();
//    }
    
}
