package com.szzt.smart.generatorcode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.persistence.Table;

import org.apache.commons.collections.CollectionUtils;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.DbType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import cn.hutool.core.util.ClassUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenerateCodeUtils
{
    
    /**
     * 根据自定义配置属性生成code
     * 
     * @param properties 自定义属性,可以new GenerateCodeProperties 也可以在classpath下面创建 generatorCode.properties
     * @param tableNames 需要生成的表名称，可选值， 为空表示全部的表
     * @throws Exception
     */
    public static void generate(GenerateCodeProperties properties, String... tableNames)
        throws Exception
    {
        if (null == tableNames || tableNames.length == 0)
        {
            tableNames = getGeneatorTables(properties);
        }
        
        run(properties.getOutputDir(),
            properties.getParentPackaageName(),
            properties.getModuleName(),
            properties.getUrl(),
            properties.getDriverName(),
            properties.getUserName(),
            properties.getPassword(),
            tableNames);
    }
    
    /**
     * 代码生成 根据classpath下面 generatorCode.properties进行创建
     *
     * @param tableNames
     * @throws Exception
     */
    public static void generate(String... tableNames)
        throws Exception
    {
        Properties p = new Properties();
        p.load(GenerateCodeUtils.class.getClassLoader().getResourceAsStream("generatorCode.properties"));
        if (null == p)
        {
            log.error("无法读取到 generatorCode.properties");
            return;
        }
        GenerateCodeProperties properties = new GenerateCodeProperties();
        String outputDir = p.getProperty("outputDir");
        String driverName = p.getProperty("driverName");
        String url = p.getProperty("url");
        String userName = p.getProperty("userName");
        String password = p.getProperty("password");
        String parentPackaageName = p.getProperty("parentPackaageName");
        String moduleName = p.getProperty("moduleName");
        String dataBaseName = p.getProperty("dataBaseName");
        
        properties.setOutputDir(outputDir);
        
        properties.setDriverName(driverName);
        properties.setUrl(url);
        properties.setUserName(userName);
        properties.setPassword(password);
        properties.setDataBaseName(dataBaseName);
        
        properties.setParentPackaageName(parentPackaageName);
        properties.setModuleName(moduleName);
        
        generate(properties, tableNames);
        
    }
    
    /**
     * 根据数据库连接获取指定库下面的所有表名称
     *
     * @param driverName
     * @param url
     * @param userName
     * @param password
     * @param dataBaseName
     * @return
     * @throws Exception
     */
    public static List<String> getAllTableNames(String driverName, String url, String userName, String password,
        String dataBaseName)
        throws Exception
    {
        DatabaseMetaDataUtil.init(driverName, url, userName, password);
        List<String> tableNames = DatabaseMetaDataUtil.getTablesAndViews(dataBaseName);
        return tableNames;
    }
    
    /**
     * 获取指定目录下的所有entity名称
     *
     * @param parentPackaageName
     * @param moduleName
     * @return
     * @throws IOException
     */
    public static List<String> getEntityNames(String parentPackaageName, String moduleName)
        throws IOException
    {
        String packageFullName = parentPackaageName + "." + moduleName + ".entity";
        Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation(packageFullName, Table.class);
        List<String> tableNames = new ArrayList<>(classes.size());
        // 循环获取信息
        for (Class entityClass : classes)
        {
            Table table = (Table)entityClass.getAnnotation(Table.class);
            tableNames.add(table.name());
        }
        return tableNames;
    }
    
    /**
     * 获取需要自动生成代码的表名
     *
     * @param properties
     * @return
     * @throws Exception
     */
    public static String[] getGeneatorTables(GenerateCodeProperties properties)
        throws Exception
    {
        
        // 1.首先获取该数据库下面的所有表名称;
        List<String> tableNames = getAllTableNames(properties.getDriverName(),
            properties.getUrl(),
            properties.getUserName(),
            properties.getPassword(),
            properties.getDataBaseName());
        if (CollectionUtils.isEmpty(tableNames))
        {
            return new String[0];
        }
        // 2.然后再获取该项目中所有实体类信息;
        List<String> entityNames = getEntityNames(properties.getParentPackaageName(), properties.getModuleName());
        // 3.然后开始比较，如果表名存在，实体类不存在，则说明要生成该表的信息;
        if (CollectionUtils.isEmpty(entityNames))
        {
            String[] allTableName = new String[tableNames.size()];
            return tableNames.toArray(allTableName);
        }
        
        List<String> geneatorTables = new ArrayList<>();
        for (String tableName : tableNames)
        {
            if (!entityNames.contains(tableName))
            {
                geneatorTables.add(tableName);
            }
        }
        System.out.println(geneatorTables);
        String[] geneatorTablesNames = new String[geneatorTables.size()];
        return geneatorTables.toArray(geneatorTablesNames);
    }


    /**
     * 调用mybatis plus进行代码生成
     * @param outputDir
     * @param parentPackaageName
     * @param moduleName
     * @param url
     * @param driverName
     * @param userName
     * @param password
     * @param tableNames
     */
    public static void run(String outputDir, String parentPackaageName, String moduleName, String url,
        String driverName, String userName, String password, String[] tableNames)
    {
        new AutoGenerator()
            // 全局 相关配置
            .setGlobalConfig(new GlobalConfig()
                // 生成文件的输出目录【默认 D 盘根目录 D:// 】
                .setOutputDir(outputDir)
                // 是否覆盖已有文件
                .setFileOverride(true)
                // 是否打开输出目录
                .setOpen(false)
                // 是否在xml中添加二级缓存配置
                .setEnableCache(false)
                // 开发人员
                .setAuthor("此代码为自动生成")
                // 开启 Kotlin 模式
                .setKotlin(false)
                // 开启 ActiveRecord 模式
                // 此模式详细解释http://baomidou.oschina.io/mybatis-plus-doc/#/quick-start?id=%e7%ae%80%e5%8d%95%e7%a4%ba%e4%be%8bactiverecord
                 .setActiveRecord(false)
                // 开启 BaseResultMap
                .setBaseResultMap(true)
                // 开启 baseColumnList
                .setBaseColumnList(true)
                // 各层文件名称方式，例如： %Action 生成 UserAction
                // 自定义文件命名，注意 %s 会自动填充表实体属性！
                .setMapperName("%sMapper")
                .setXmlName("%sMapper")
                // .setServiceName("%sService")
                .setServiceImplName("%sService")
                .setControllerName("%sController")
                // 指定生成的主键的ID类型
                // AUTO(0, "数据库ID自增"), INPUT(1, "用户输入ID"),
                // /* 以下2种类型、只有当插入对象ID 为空，才自动填充。 */
                // ID_WORKER(2, "全局唯一ID"), UUID(3, "全局唯一ID"), NONE(4, "该类型为未设置主键类型");
                .setIdType(IdType.NONE)

            )
            // 数据源配置
            .setDataSource(new DataSourceConfig()
                // 设置数据库类型，支持：mysql,oracle,sql_server,postgre_sql
                .setDbType(DbType.MYSQL)
                // .setSchemaname()//PostgreSQL schemaname
                .setTypeConvert(new MySqlTypeConvert()
                {
                    // 自定义数据库表字段类型转换【可选】
                    @Override
                    public DbColumnType processTypeConvert(String fieldType)
                    {
                        // System.out.println("转换类型：" + fieldType);
                        // 注意！！processTypeConvert 存在默认类型转换，如果不是你要的效果请自定义返回、非如下直接返回。
                        return super.processTypeConvert(fieldType);
                    }
                })
                // 驱动名称
                .setDriverName(driverName)
                // 连接数据库的url
                .setUrl(url)
                // 数据库连接用户名
                .setUsername(userName)
                // 数据库连接密码
                .setPassword(password))
            // 数据库表配置，生成策略配置
            .setStrategy(new StrategyConfig()
                // 是否大写命名 ORACLE 注意
                // .setCapitalMode(true)
                // 数据库表映射到实体的命名策略
                // nochange不做任何改变，原样输出 underline_to_camel下划线转驼峰命名
                .setNaming(NamingStrategy.underline_to_camel)
                // 此处可以修改为您的表前缀
                .setTablePrefix(new String[] {"t_", "acc_", "res_", "sys_"})
                // 字段前缀
                // .setFieldPrefix()
                // 自定义继承的Entity类全称，带包名
                .setSuperEntityClass("com.szzt.smart.framework.mybatis.entity.BaseEntity")
                // 自定义基础的Entity类，公共字段
                // .setSuperEntityColumns(new String[] { "test_id", "age" })
                // 自定义继承的Mapper类全称，带包名
                .setSuperMapperClass("com.szzt.smart.framework.mybatis.mapper.FrameworkBaseMapper")
                // 自定义继承的Service类全称，带包名
                // .setSuperServiceClass("com.baomidou.demo.TestService")
                // 自定义继承的ServiceImpl类全称，带包名
                .setSuperServiceImplClass("com.szzt.smart.framework.mybatis.service.BaseService")
                // 自定义继承的Controller类全称，带包名
                // .setSuperControllerClass("com.qdzklt.cloud.web.BaseController")
                // 需要包含的表名（与exclude二选一配置）
                .setInclude(tableNames)
                // 需要排除的表名
                // .setExclude(new String[]{"test"})
                // * 【实体】是否生成字段常量（默认 false）<br> public static final String ID = "test_id";
                // .setEntityColumnConstant(true)
                // 【实体】是否为构建者模型（默认 false）<br> public User setName(String name) { this.name = name; return this; }
                // .setEntityBuilderModel(true)
                // 【实体】是否为lombok模型（默认 false）<br> <a href="https://projectlombok.org/">document</a>
                .setEntityLombokModel(true)
                // Boolean类型字段是否移除is前缀（默认 false）<br>
                // 比如 : 数据库字段名称 : 'is_xxx',类型为 : tinyint. 在映射实体的时候则会去掉is,在实体类中映射最终结果为 xxx
                // .setEntityBooleanColumnRemoveIsPrefix()
                /*
                 * 生成 <code>@RestController</code> 控制器 <pre> <code>@Controller</code> -> <code>@RestController</code>
                 * </pre>
                 */
                .setRestControllerStyle(true)
                /*
                 * 驼峰转连字符 <pre> <code>@RequestMapping("/managerUserActionHistory")</code> ->
                 * <code>@RequestMapping("/manager-user-action-history")</code> </pre>
                 */
                // .setControllerMappingHyphenStyle()
                // 是否生成实体时，生成字段注解
                .entityTableFieldAnnotationEnable(false)
            // 乐观锁属性名称
            // .setVersionFieldName()
            // 逻辑删除属性名称
            // .setLogicDeleteFieldName()
            // 表填充字段
            // .setTableFillList()
            )
            // 包 相关配置
            .setPackageInfo(new PackageConfig()
                // 父包名。如果为空，将下面子包名必须写全部， 否则就只需写子包名
                .setParent(parentPackaageName)
                // 父包模块名
                .setModuleName(moduleName)
                // Entity包名
                .setEntity("entity")
                // Service包名
                // .setService("service")
                // Service Impl包名
                .setServiceImpl("service")
                // Mapper包名
                .setMapper("mapper")
                // Mapper XML包名
                .setXml("mapper")
                // Controller包名
                .setController("controller"))
            // 注入自定义配置，可以在 VM 中使用 cfg.abc 【可无】
            // .setCfg(
            // new InjectionConfig() {
            // @Override
            // public void initMap() {
            // Map<String, Object> map = new HashMap<String, Object>();
            // map.put("abc", this.getConfig().getGlobalConfig().getAuthor() + "-mp");
            // this.setMap(map);
            // }
            // }
            // )
            // 模板 相关配置
            .setTemplate(
                // // 自定义模板配置，可以 copy 源码 mybatis-plus/src/main/resources/templates 下面内容修改，
                // // 放置自己项目的 src/main/resources/templates 目录下, 默认名称一下可以不配置，也可以自定义模板名称
                new TemplateConfig()
                    // .setController("classpath*:/templates/controller.java.vm")
                    // .setEntity("classpath*:/templates/entity.java.vm")
                    // .setMapper("classpath*:/templates/mapper.java.vm")
                    // .setXml("classpath*:/templates/mapper.xml.vm")
                    //// .setService("...")
                    // .setServiceImpl("classpath*:/templates/serviceImpl.java.vm")
                    // 如上任何一个模块如果设置 空 OR Null 将不生成该模块。
                    // mpg.setTemplate(tc);
                    .setController("/templates/controller.java")
                    .setEntity("/templates/entity.java")
                    .setMapper("/templates/mapper.java")
                    .setXml("/templates/mapper.xml")
                    .setService(null)
                    .setServiceImpl("/templates/serviceImpl.java"))
            .setTemplateEngine(new FreemarkerTemplateEngine())
            // 执行代码生成
            .execute();
    }
    
}
