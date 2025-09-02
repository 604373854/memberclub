/**
 * @(#)OrderCenterEnum.java, 一月 19, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.config;

/**
 * 订单中心接入方式枚举，决定订单是在本地处理还是通过外部 SPI 实现处理。
 */
public enum OrderCenterEnum {

    /**
     * 本地 SPI 实现处理订单。
     */
    local,

    /**
     * 调用生产环境的 SPI 实现。
     */
    spi,
}
