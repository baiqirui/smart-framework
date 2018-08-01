package com.szzt.smart.framework.sample.web;

import com.szzt.smart.framework.kafka.LuImmediateMessage;
import com.szzt.smart.framework.kafka.LuKafkaProducer;
import com.szzt.smart.framework.mybatis.entity.PageResult;
import com.szzt.smart.framework.sample.domain.UserDomain;
import com.szzt.smart.framework.sample.feign.PaasFileApi;
import com.szzt.smart.framework.sample.feign.PaasPortalApi;
import com.szzt.smart.framework.sample.feign.ParamConfig;
import com.szzt.smart.framework.sample.feign.ParamConfigCondition;
import com.szzt.smart.framework.sample.mapper.UserMapper;
import com.szzt.smart.framework.sample.service.UserService;
import feign.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.szzt.smart.framework.sample.entity.User;
import com.szzt.smart.framework.web.exception.ArgumentException;
import com.szzt.smart.framework.web.model.ResultBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("test")
@Api(tags = "测试接口")
public class TestContronller
{
    
    @Autowired
    private LuKafkaProducer producer;
    
    @Autowired
    private UserService userService;

    @Autowired
    private PaasFileApi paasFileApi;
    
    // @Autowired
    // private HelloSender helloSender;
    
    // @Autowired
    // private ComputeClient computeClient;
    
    // @Autowired
    // private RedisService redisService;
    
    // @GetMapping("testRedisCluster")
    // @ApiOperation(value = "测试RedisCluster", response = String.class)
    // public String testRedisCluster(@RequestParam String key)
    // {
    // String value = redisService.get(key);
    // if (StringUtils.isBlank(value))
    // {
    // redisService.set(key, "Hello RedisSerivce");
    // }
    // return value;
    // }
    
    @GetMapping("testPage/{pageSize}/{pageNum}")
    @ApiOperation(value = "测试分页", response = String.class)
    public PageResult testRedisAnonation(@PathVariable Integer pageSize, @PathVariable Integer pageNum)
    {
        Example example = new Example(User.class, false, false);
        return userService.selectPage(example, pageNum, pageSize);
    }
    
    @GetMapping("testRedisAnonation")
    @ApiOperation(value = "测试Redis注解", response = String.class)
    public User testRedisAnonation(@RequestParam Long id)
    {
        return userService.getUser(id, "testRedisAnonation");
    }
    
    @Transactional
    @PostMapping("testTransactional")
    @ApiOperation(value = "测试事务", response = User.class)
    public User test0(@Validated @RequestBody @ApiParam User user)
    {
        userService.insert(user);
        String a = null;
        a.length();
        return user;
    }
    
    @PostMapping("testException")
    @ApiOperation(value = "测试错误码转换", response = User.class)
    public User test3(@RequestBody @ApiParam("请求参数") @Validated User user)
    {
        throw ArgumentException.createNullOrBlankString("name");
    }
    
    @GetMapping("testQuery")
    @ApiOperation(value = "测试查询", response = ResultBody.class)
    public ResultBody test1(@RequestParam Long id)
    {
        log.debug("参数：" + id);
        User user = userService.get(id);
        log.info("查询结果：" + user);
        return new ResultBody(user);
    }
    
    // @PostMapping("testFeign")
    // @ApiOperation(value = "testFeign", response = User.class)
    // public ResultBody test3(@ApiParam("加数A") @RequestParam(value = "a") Integer a,
    // @ApiParam("加数B") @RequestParam(value = "b") Integer b)
    // {
    // ResultBody a1 = computeClient.getA(a, "1234567899");
    // return a1;
    // }
    
    // @PostMapping("testRabbit")
    // @ApiOperation(value = "测试2", response = ResultBody.class)
    // public ResultBody testRabbit(@RequestBody User user)
    // {
    // helloSender.sendUser(user);
    // return ResultBody.success();
    // }
    
    @GetMapping("testKafka")
    @ApiOperation(value = "测试kafka", response = ResultBody.class)
    public ResultBody testKafka(@RequestParam String msg)
    {
        producer.sendImmediate("testTopic1", msg);
        return ResultBody.success();
    }
    
    @KafkaListener(topics = "testTopic1", containerFactory = "kafkaListenerContainerFactory")
    public void receiveSpiderMsg(LuImmediateMessage message)
    {
        log.info("获取到Kafka消息，message is {}, opid is {}", message.getMessage(), message.getOpid());
    }
    
     @Transactional
    @PostMapping("addUser")
    @ApiOperation(value = "新增用户", response = User.class)
    // @Validated
    public ResultBody addUser(@RequestBody @ApiParam("用户信息") User user)
    {
        userService.insert(user);
//        String a = null;
//        a.length();
//        user.setAge(22);
//        user.setName("666555");
//        userService.insert(user);
        return ResultBody.success();
    }
    
    @GetMapping("/{id}")
    @ApiOperation(value = "根据ID获取用户", response = ResultBody.class)
    public ResultBody getUser(@PathVariable @ApiParam("用户ID") Long id)
    {
        log.debug("参数：" + id);
        User user = userService.get(id);
        log.info("查询结果：" + user);
        return ResultBody.success(user);
    }
    
    @PostMapping("/listPage")
    @ApiOperation(value = "获取用户列表(分页)", response = ResultBody.class)
    public ResultBody<PageResult<List<User>>> listPage(@RequestBody UserDomain userDomain)
    {
        Example example = new Example(UserDomain.class, false, false);
        return ResultBody.success(userService.selectPage(example, userDomain.getPageNum(), userDomain.getPageSize()));
    }


    @PostMapping("/list")
    @ApiOperation(value = "获取用户列表(不分页)", response = ResultBody.class)
    public ResultBody<List<User>> list(@RequestBody User user)
    {
        return ResultBody.success(userService.select(user));
    }
    
    @PutMapping("/{id}")
    @ApiOperation(value = "修改用户", response = ResultBody.class)
    public ResultBody modifyUser(@RequestBody User user, @PathVariable @ApiParam("用户ID") Long id)
    {
        user.setId(id);
        userService.update(user);
        return ResultBody.success();
    }
    
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除用户", response = ResultBody.class)
    public ResultBody deletetUser(@PathVariable @ApiParam("用户ID") Long id)
    {
        userService.deleteById(id);
        return ResultBody.success();
    }
    
    @Autowired
    private PaasPortalApi paasPortalApi;
    
    /**
     * @Title: listParamConfig
     * @Description: 查询参数配置列表
     * @param paramConfigCondition 参数配置条件
     * @return 参数列表
     */
    @ApiOperation(value = "查询参数配置列表", response = ResultBody.class)
    @PostMapping(value = "/listParamConfig")
    public ResultBody<List<ParamConfig>> listParamConfig(@RequestBody ParamConfigCondition paramConfigCondition)
    {
        List<ParamConfig> paramConfigList = paasPortalApi.listParamConfig(paramConfigCondition);
        return ResultBody.success(paramConfigList);
    }
    
    @GetMapping(value = "/getFile")
    public ResponseEntity<InputStreamResource> listParamConfig(@RequestParam String fileId)
        throws IOException
    {
        Response response = paasFileApi.getFile(fileId);
        Response.Body body = response.body();
        Map<String, Collection<String>> headers = response.headers();
        HttpHeaders httpHeaders = new HttpHeaders();
        
        headers.forEach((key, values) -> {
            List<String> headerValues = new LinkedList<>();
            headerValues.addAll(values);
            httpHeaders.put(key, headerValues);
        });
        
        InputStream inputStream = body.asInputStream();// HttpURLInputStream
        InputStreamResource resource = new InputStreamResource(inputStream);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).headers(httpHeaders).body(resource);
    }

    @GetMapping("/version")
    @ApiOperation(value = "版本信息", response = ResultBody.class)
    public ResultBody getVersion()
    {
        return ResultBody.success("2.0.0");
    }

}
