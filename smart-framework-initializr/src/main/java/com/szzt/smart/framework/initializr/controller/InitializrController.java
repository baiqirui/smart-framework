package com.szzt.smart.framework.initializr.controller;

import com.szzt.smart.framework.initializr.domain.CompileDependency;
import com.szzt.smart.framework.initializr.domain.ProjectRequest;
import com.szzt.smart.framework.initializr.service.GenerateService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
public class InitializrController
{
    
    @Autowired
    private GenerateService generateService;
    
    @RequestMapping(value = "/index")
    public String index(ModelMap modelMap)
        throws Exception
    {
        ObjectMapper om = new ObjectMapper();
        List<CompileDependency> dependencies = om.readValue(
            ResourceUtils.getURL("classpath:static/json/dependencies.json"),
            new TypeReference<List<CompileDependency>>()
            {
            });
        modelMap.addAttribute("dependencies", dependencies);
        modelMap.addAttribute("frameworkVersion", "1.0.0-SNAPSHOT");
        
        return "index";
    }
    
    @RequestMapping(value = "/generate")
    public ResponseEntity<byte[]> generate(ProjectRequest request)
        throws Exception
    {
        log.info("ProjectRequest is : " + request);
        //调用服务生成项目文件
        File projectFile = generateService.generateProject(request);

        //转换成byte字节;
        FileInputStream fileInputStream = new FileInputStream(projectFile);
        byte[] content = StreamUtils.copyToByteArray(fileInputStream);
        log.info("Uploading: {} ({} bytes)", projectFile, content.length);
        String contentDispositionValue = "attachment; filename=\"" + request.getArtifactId() + ".zip" + "\"";

        ResponseEntity<byte[]> response = ResponseEntity.ok()
                .header("Content-Type", "application/zip")
                .header("Content-Disposition", contentDispositionValue)
                .body(content);

        fileInputStream.close();
        //删除目录
        generateService.cleanProjectDir();

        return response;
    }
    
}
