/**
 * @(#)DistributedMQEnum.java, 一月 12, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.config;

/**
 * 支持的消息队列实现。
 * 通过配置项 {@code memberclub.infrastructure.mq} 进行选择。
 */
public enum DistributedMQEnum {

    /**
     * 本地实现，不与外部消息系统交互。
     */
    local,

    /**
     * 使用 RabbitMQ 作为消息中间件。
     */
    rabbitmq,
}
