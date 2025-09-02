/**
 * @(#)InventorySkuOpDO.java, 一月 29, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.context.inventory;

import lombok.Data;

/**
 * 单个SKU的库存操作信息
 *
 * <p>author: 掘金五阳</p>
 */
@Data
public class InventorySkuOpDO {

    private String subKey; // 子键

    private long skuId; // SKU ID

    private long count; // 操作数量
}