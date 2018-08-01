package com.szzt.smart.framework.initializr.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class CompileDependency
{
    private String groupId;
    
    private String artifactId;
    
    private String version;
    
    private String type;
    
    private String name;
}