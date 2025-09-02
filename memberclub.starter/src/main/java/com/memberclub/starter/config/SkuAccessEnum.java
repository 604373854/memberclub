/**
 * @(#)SkuAccessEnum.java, 一月 19, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.config;

/**
 * 应用中读取 SKU 信息的方式枚举。
 */
public enum SkuAccessEnum {

    /**
     * 从本地内存结构读取 SKU 数据。
     */
    local,
    /**
     * 从数据库获取 SKU 数据。
     */
    db,
    ;

}
