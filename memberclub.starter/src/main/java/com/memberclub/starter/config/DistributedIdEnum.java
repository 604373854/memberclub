/**
 * @(#)DistributedIdEnum.java, 十二月 31, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.config;

/**
 * 可插拔的分布式 ID 生成策略。
 * 通过配置项 {@code memberclub.infrastructure.id} 选择具体实现。
 */
public enum DistributedIdEnum {

    /**
     * 本地生成 ID，可使用随机数或内存算法。
     */
    local,

    /**
     * 使用基于 Redisson 的实现，状态保存在 Redis 中。
     */
    redisson,
}
