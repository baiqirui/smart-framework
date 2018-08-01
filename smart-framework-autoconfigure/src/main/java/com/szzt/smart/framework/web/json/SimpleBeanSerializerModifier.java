package com.szzt.smart.framework.web.json;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

/**
 * Created by lvjj on 2017/2/15.
 */
public class SimpleBeanSerializerModifier extends BeanSerializerModifier {

    private JsonSerializer<Object> nullArrayJsonSerializer = new NullArrayJsonSerializer();
    private JsonSerializer<Object> nullObjectJsonSerializer = new NullObjectJsonSerializer();

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc,
                                                     List<BeanPropertyWriter> beanProperties) {
        // 循环所有的beanPropertyWriter
        for (int i = 0; i < beanProperties.size(); i++) {
            BeanPropertyWriter writer = beanProperties.get(i);
            // 判断字段的类型，如果是array，list，set 则不注册 nullSerializer
            if (isArrayType(writer)) {
                //给writer注册一个自己的nullSerializer
                writer.assignNullSerializer(nullArrayJsonSerializer);
            } else {
                writer.assignNullSerializer(nullObjectJsonSerializer);
            }
        }
        return beanProperties;
    }

    /**
     * 集合类型
     * @param writer
     * @return
     */
    protected boolean isArrayType(BeanPropertyWriter writer) {
        Class<?> clazz = writer.getPropertyType();
        return clazz.isArray() || clazz.equals(List.class) || clazz.equals(Set.class);

    }

}