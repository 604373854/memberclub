package com.memberclub.starter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * 绑定 {@code memberclub.biz} 前缀下的核心业务配置。
 * 当前仅暴露用于标识本服务的商户编号。
 */
@Configuration
@Order(2)
@Data
@ConfigurationProperties(prefix = "memberclub.biz")
public class BizConfiguration {

    /**
     * 当前应用实例所属的商户编号。
     * 通常通过 {@code memberclub.biz.SELF_MERCHANT_ID} 进行配置。
     */
    private String SELF_MERCHANT_ID;

}
