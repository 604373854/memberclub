/**
 * @(#)UserTagEnum.java, 一月 30, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.config;

/**
 * 用户标签系统的后端存储选项。
 * 决定用户标签数据的存储和读取位置。
 */
public enum DistributedUserTagEnum {

    /**
     * 用户标签保存在内存中，随应用生命周期而存在。
     */
    local,

    /**
     * 将用户标签持久化到 Redis。
     */
    redis,
    ;

}
