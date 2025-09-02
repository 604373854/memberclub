/**
 * @(#)DistributedCacheEnum.java, 一月 29, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.config;

/**
 * 分布式缓存功能支持的后端实现。
 * 由配置项 {@code memberclub.infrastructure.cache} 决定选择哪种实现。
 */
public enum DistributedCacheEnum {

    /**
     * 本地内存缓存，适合本地调试使用。
     */
    local,

    /**
     * 使用 Redis 存储缓存数据。
     */
    redis,
    ;
}
