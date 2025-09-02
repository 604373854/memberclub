/**
 * @(#)InfrastructureImplChecker.java, 十二月 26, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.config;

import com.memberclub.common.util.ApplicationContextUtils;
import com.memberclub.infrastructure.dynamic_config.DynamicConfig;
import com.memberclub.infrastructure.id.IdGenerator;
import com.memberclub.infrastructure.lock.DistributeLock;
import com.memberclub.infrastructure.mq.MessageQuenePublishFacade;
import lombok.Data;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;

import static org.springframework.util.Assert.notNull;

/**
 * 校验应用所需的基础设施组件是否已经提供。
 * 绑定前缀为 {@code memberclub.infrastructure} 的配置。
 * <p>
 * 在此处增加新的枚举值，可让 IDE 在 {@code application.yml} 中提示可选项。
 */

@Configuration
@Order(2)
@Data
@ConfigurationProperties(prefix = "memberclub.infrastructure")
public class InfrastructureImplChecker {


    /**
     * 分布式 ID 生成策略，默认 {@link DistributedIdEnum#redisson}。
     */
    private DistributedIdEnum id;

    /**
     * 分布式锁实现，值为 {@code local} 时表示不加锁。
     */
    private DistributedLockEnum lock;

    /**
     * 分布式配置中心，未配置时默认为 Apollo。
     */
    private DistributedConfigEnum config;


    /**
     * 提供延迟重试能力的组件，如 {@code @Retry} 注解依赖此组件。
     */
    private DistributedRetryEnum retry;

    /**
     * 分布式消息队列实现。
     */
    private DistributedMQEnum mq;

    /**
     * 订单中心接入实现。
     */
    private OrderCenterEnum order;

    /**
     * 资产中心接入实现。
     */
    private AssetCenterEnum asset;

    /**
     * 支付能力接入实现。
     */
    private PaymentEnum payment;

    /**
     * 分布式缓存后端。
     */
    private DistributedCacheEnum cache;

    /**
     * 用户标签存储后端。
     */
    private DistributedUserTagEnum usertag;

    /**
     * SKU 信息加载来源。
     */
    private SkuAccessEnum sku;

    /**
     * 控制 Feign 客户端行为的嵌套配置。
     */
    @NestedConfigurationProperty()
    private Feign feign;

    /**
     * 计算优惠金额的策略。
     */
    private AmountComputeEnum amountcompute;

    /**
     * 属性绑定完成后，校验每个基础设施枚举是否有对应的 Spring Bean 实现。
     */
    @PostConstruct
    public void init() {
        ApplicationContext applicationContext = ApplicationContextUtils.getContext();
        notNull(applicationContext, "未获取到 ApplicationContext");
        // 校验基础设施接口是否存在对应的实现类。
        check(() -> applicationContext.getBean(DynamicConfig.class),
                "DynamicConfig 未获取到实现类,请关注 配置项 memberclub.infrastructure.config,默认 apollo ");

        check(() -> applicationContext.getBean(DistributeLock.class),
                "DistributeLock 未获取到实现类,请关注 配置项 memberclub.infrastructure.lock,默认 redis ");

        check(() -> applicationContext.getBean(IdGenerator.class),
                "IdGenerator 未获取到实现类,请关注 配置项 memberclub.infrastructure.id,默认 redis ");

        check(() -> applicationContext.getBean(MessageQuenePublishFacade.class),
                "MessageQuenePublishFacade 未获取到实现类,请关注 配置项 memberclub.infrastructure.mq,默认 rabbitmq ");

    }

    /**
     * 执行校验，如果缺少 Bean，则抛出带有友好提示信息的异常。
     *
     * @param runnable 尝试获取 Bean 的逻辑
     * @param msg      当未找到 Bean 时抛出的提示信息
     */
    public void check(Runnable runnable, String msg) {
        try {
            runnable.run();
        } catch (NoSuchBeanDefinitionException e) {
            throw new RuntimeException(msg, e);
        }
    }

    /**
     * Feign 相关的嵌套配置。
     */
    @Data
    class Feign {

        /**
         * 是否启用 Feign 客户端。
         */

        private Boolean enabled;
    }
}