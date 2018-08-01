package ${package.Entity};

<#--<#list table.importPackages as pkg>-->
<#--import ${pkg};-->
<#--</#list>-->
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import com.szzt.smart.framework.mybatis.entity.BaseEntity;

<#if entityLombokModel>
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
</#if>

import javax.persistence.Table;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * ${table.comment}
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
<#if entityLombokModel>
@Data
<#if superEntityClass??>
@EqualsAndHashCode(callSuper = true)
</#if>
@Accessors(chain = true)
</#if>
<#--<#if table.convert>-->
<#--@TableName("${table.name}")-->
<#--</#if>-->
<#--自己的代码 -->
@ApiModel
@Table(name="${table.name}")
<#if superEntityClass??>
public class ${entity} extends ${superEntityClass}<#list table.fields as field><#if field.keyIdentityFlag><${field.propertyType}></#if></#list> {
<#elseif activeRecord>
public class ${entity} extends Model<${entity}> {
<#else>
public class ${entity} implements Serializable {
</#if>

    private static final long serialVersionUID = 1L;
<#-- ----------  BEGIN 字段循环遍历  ---------->
<#list table.fields as field>
<#if field.keyFlag>
<#assign keyPropertyName="${field.propertyName}"/>
</#if>
<#--<#if field.keyFlag>-->
<#--&lt;#&ndash; 主键 &ndash;&gt;-->
<#--<#if field.keyIdentityFlag>-->
    <#--@TableId(value = "${field.name}", type = IdType.AUTO)-->
<#--<#elseif idType??>-->
    <#--@TableId(value = "${field.name}", type = IdType.${idType})-->
<#--<#elseif field.convert>-->
    <#--@TableId("${field.name}")-->
<#--</#if>-->
<#--&lt;#&ndash; 普通字段 &ndash;&gt;-->
<#--<#elseif field.fill??>-->
<#--&lt;#&ndash; -----   存在字段填充设置   ---&ndash;&gt;-->
<#--<#if field.convert>-->
    <#--@TableField(value = "${field.name}", fill = FieldFill.${field.fill})-->
<#--<#else>-->
    <#--@TableField(fill = FieldFill.${field.fill})-->
<#--</#if>-->
<#--<#elseif field.convert>-->
    <#--@TableField("${field.name}")-->
<#--</#if>-->
<#--&lt;#&ndash; 乐观锁注解 &ndash;&gt;-->
<#--<#if versionFieldName!"" == field.name>-->
    <#--@Version-->
<#--</#if>-->
<#--&lt;#&ndash; 逻辑删除注解 &ndash;&gt;-->
<#--<#if logicDeleteFieldName!"" == field.name>-->
    <#--@TableLogic-->
<#--</#if>-->
<#if !field.keyIdentityFlag>
    <#if field.comment!?length gt 0>
    /**
     * ${field.comment}
     */
    </#if>
   @ApiModelProperty(value = "${field.comment}")
   private ${field.propertyType} ${field.propertyName};
</#if>
</#list>
<#------------  END 字段循环遍历  ---------->

<#if !entityLombokModel>
<#list table.fields as field>
<#if field.propertyType == "boolean">
    <#assign getprefix="is"/>
<#else>
    <#assign getprefix="get"/>
</#if>
    public ${field.propertyType} ${getprefix}${field.capitalName}() {
        return ${field.propertyName};
    }

<#if entityBuilderModel>
    public ${entity} set${field.capitalName}(${field.propertyType} ${field.propertyName}) {
<#else>
    public void set${field.capitalName}(${field.propertyType} ${field.propertyName}) {
</#if>
        this.${field.propertyName} = ${field.propertyName};
<#if entityBuilderModel>
        return this;
</#if>
    }
</#list>
</#if>

<#if entityColumnConstant>
<#list table.fields as field>
    public static final String ${field.name?upper_case} = "${field.name}";

</#list>
</#if>
<#--<#if activeRecord>-->
    <#--@Override-->
    <#--protected Serializable pkVal() {-->
<#--<#if keyPropertyName??>-->
        <#--return this.${keyPropertyName};-->
<#--<#else>-->
        <#--return this.id;-->
<#--</#if>-->
    <#--}-->

<#--</#if>-->
<#if !entityLombokModel>
    @Override
    public String toString() {
        return "${entity}{" +
<#list table.fields as field>
<#if field_index==0>
        "${field.propertyName}=" + ${field.propertyName} +
<#else>
        ", ${field.propertyName}=" + ${field.propertyName} +
</#if>
</#list>
        "}";
    }
</#if>
}
