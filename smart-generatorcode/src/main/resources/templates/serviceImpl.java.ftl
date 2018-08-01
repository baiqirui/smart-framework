package ${package.ServiceImpl};

import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
import ${superServiceImplClassPackage};
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.szzt.smart.framework.mybatis.mapper.FrameworkBaseMapper;

/**
 * <p>
 * ${table.comment} 服务实现类
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
@Service
<#if kotlin>
open class ${table.serviceImplName} : ${superServiceImplClass}<${table.mapperName}, ${entity}>(), ${table.serviceName}
{

}
<#else>
public class ${table.serviceImplName} extends ${superServiceImplClass}<${entity}>
{

    @Autowired
    private ${table.mapperName} mapper;

    @Override
    protected FrameworkBaseMapper getMapper()
    {
        return mapper;
    }
}
</#if>
