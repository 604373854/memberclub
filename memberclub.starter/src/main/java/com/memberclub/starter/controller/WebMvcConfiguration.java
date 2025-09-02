/**
 * @(#)WebMvcConfiguration.java, 一月 19, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.controller;

import com.memberclub.common.util.JsonUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * Spring MVC 配置类，定制静态资源、CORS 规则以及 JSON 序列化行为。
 */
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurationSupport {
    /**
     * 配置静态资源映射，如 swagger 与 webjars。
     *
     * @param registry 资源处理注册器
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
    }

    /**
     * 配置全局跨域规则，允许测试环境跨域访问。
     *
     * @param registry CORS 注册器
     */
    @Override
    protected void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 所有接口
                .allowCredentials(true) // 是否发送 Cookie
                .allowedOriginPatterns("*") // 支持域
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 支持方法
                .allowedHeaders("*")
                .exposedHeaders("*");
    }

    /**
     * 注册消息转换器，包含自定义 Jackson 转换器以避免空对象序列化问题。
     *
     * @param converters 需要修改的转换器列表
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.configureMessageConverters(converters);
        // 使用共享的 ObjectMapper 配置添加转换器
        converters.add(mappingJackson2HttpMessageConverter());
    }

    // 解决序列化空对象问题
    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter =
                new MappingJackson2HttpMessageConverter(JsonUtils.getObjectMapper());
        return converter;
    }

}