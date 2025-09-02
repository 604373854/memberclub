package com.memberclub.starter.config;

/**
 * 应用中用于计算金额的策略枚举。
 * 通过配置项决定折扣或手续费计算所使用的实现。
 */
public enum AmountComputeEnum {

    /**
     * 轻量级的内存实现，适合测试或无需外部服务的场景。
     */
    local,

    /**
     * 标准生产算法或远程服务实现。
     */
    normal,
}
