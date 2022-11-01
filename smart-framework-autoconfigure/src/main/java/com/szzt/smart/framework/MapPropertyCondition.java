package com.szzt.smart.framework;

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Properties;

class MapPropertyCondition extends SpringBootCondition
{
    
    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata)
    {
        String prefix = attribute(metadata, "prefix");
//        RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(context.getEnvironment());
//        Map<String, Object> properties = resolver.getSubProperties(prefix);
        Iterable<ConfigurationPropertySource> sources = ConfigurationPropertySources.get(context.getEnvironment());
        Binder binder = new Binder(sources);
        BindResult<Properties> bindResult = binder.bind("spring.datasource", Properties.class);
        Properties properties= bindResult.get();
        return new ConditionOutcome(!properties.isEmpty(), String.format("Map property [%s] is empty", prefix));
    }
    
    private static String attribute(AnnotatedTypeMetadata metadata, String name)
    {
        return (String)metadata.getAnnotationAttributes(ConditionalOnMapProperty.class.getName()).get(name);
    }
}