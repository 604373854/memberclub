package com.memberclub.starter.config;

/**
 * 支付能力接入方式枚举，用于路由支付相关操作。
 */
public enum PaymentEnum {
    /**
     * 本地测试实现。
     */
    local,
    /**
     * 调用外部 SPI 支付服务。
     */
    spi,
}
