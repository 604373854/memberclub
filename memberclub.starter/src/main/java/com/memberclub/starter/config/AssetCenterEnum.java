/**
 * @(#)AssetCenterEnum.java, 一月 19, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.config;

/**
 * 资产中心接入方式枚举。
 * 通过配置项可在本地模拟实现与远程 SPI 实现之间切换。
 */
public enum AssetCenterEnum {

    /**
     * 本地 SPI 实现，常用于开发或无外部依赖的测试场景。
     */
    local,

    /**
     * 调用生产环境提供的远程 SPI 实现。
     */
    spi,
    ;
}
