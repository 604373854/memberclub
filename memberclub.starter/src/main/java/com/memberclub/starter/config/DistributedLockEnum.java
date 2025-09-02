/**
 * @(#)DistributedLockEnum.java, 一月 05, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.config;

/**
 * 分布式锁的实现枚举，用于保护关键代码段。
 * 由配置项 {@code memberclub.infrastructure.lock} 决定使用哪种实现。
 */
public enum DistributedLockEnum {

    /**
     * 不进行分布式加锁，操作不做跨节点协调。
     */
    local,

    /**
     * 使用 Redisson 提供的基于 Redis 的锁实现。
     */
    redisson,

    /**
     * 自定义的 Redis 锁实现。
     */
    redis,
}
