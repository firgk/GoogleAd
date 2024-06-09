package com.lgh.test;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.lgh.test.Setting.PicLocation;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 2. 映射 /externalresources/** 到外部文件系统的指定目录
        //    注意：需要替换成实际的外部文件系统路径
        registry.addResourceHandler("/pic/**")
                .addResourceLocations(PicLocation);
    }
}

