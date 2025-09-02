/**
 * @(#)DistributedRetryEnum.java, 一月 05, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.config;

/**
 * 自动重试与延迟队列的实现方式枚举。
 * 不同的取值会影响失败任务的重试策略。
 */
public enum DistributedRetryEnum {

    /**
     * 在本地执行重试，不进行分布式协调。
     */
    local,

    /**
     * 使用 RabbitMQ 延迟队列处理重试。
     */
    rabbitmq,

    /**
     * 使用基于 Redisson 的调度器实现重试。
     */
    redisson,
    ;
}
