/**
 * @(#)DistributedConfigEnum.java, 一月 05, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.config;

/**
 * 配置中心实现枚举。
 * 添加新的枚举值可让 Spring 提示可用的配置中心实现。
 */
public enum DistributedConfigEnum {
    /**
     * 使用本地配置文件，不依赖外部配置中心。
     */
    local,
    /**
     * 使用 Apollo 作为配置中心。
     */
    apollo,
}
