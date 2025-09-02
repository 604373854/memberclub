package com.memberclub.starter.controller;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
/**
 * 基础 Web 配置，目前仅预留 CORS 映射接口，后续可按需扩展。
 */
public class WebConfig implements WebMvcConfigurer {
    /**
     * 配置跨域规则。
     *
     * @param registry CORS 注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 当前无需配置，若需允许跨域可在此处定制
    }
}