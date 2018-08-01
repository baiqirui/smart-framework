package ${package.Controller};

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

<#if restControllerStyle>
import org.springframework.web.bind.annotation.RestController;
<#else>
import org.springframework.stereotype.Controller;
</#if>
<#if superControllerClassPackage??>
import ${superControllerClassPackage};
</#if>

import ${package.ServiceImpl}.${table.serviceImplName};
import ${package.Entity}.${entity};
import org.springframework.web.bind.annotation.*;
import com.szzt.smart.framework.web.model.ResultBody;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * <p>
 * ${table.comment} 前端控制器
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
@Api(tags = "${table.comment}接口")
<#if restControllerStyle>
@RestController
<#else>
@Controller
</#if>
@RequestMapping("<#if package.ModuleName??>/${package.ModuleName}</#if>/<#if controllerMappingHyphenStyle??>${controllerMappingHyphen}<#else>${table.entityPath}</#if>")
<#if kotlin>
class ${table.controllerName}<#if superControllerClass??> : ${superControllerClass}()</#if>
<#else>
<#if superControllerClass??>
public class ${table.controllerName} extends ${superControllerClass} {
<#else>
public class ${table.controllerName} {

   @Autowired
   private ${table.serviceImplName} service;

   @GetMapping("/{id}")
   @ApiOperation(value = "根据ID获取详情", response = ResultBody.class)
   public  ResultBody<${entity}> get(@PathVariable Long id)
   {
       return ResultBody.success(service.get(id));
   }


    @PostMapping("/add")
    @ApiOperation(value = "新增实体", response = ResultBody.class)
    public ResultBody add(@RequestBody @ApiParam("实体信息") ${entity}  entity)
    {
        service.insert(entity);
        return ResultBody.success();
    }


    @PutMapping("/")
    @ApiOperation(value = "修改实体", response = ResultBody.class)
    public ResultBody update(@RequestBody ${entity} entity)
    {
        service.update(entity);
        return ResultBody.success();
    }


    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除实体", response = ResultBody.class)
    public ResultBody deletetUser(@PathVariable @ApiParam("主键ID") Long id)
    {
        service.deleteById(id);
        return ResultBody.success();
    }

}
</#if>
</#if>
