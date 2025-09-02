/**
 * @(#)InventoryOpContext.java, 一月 29, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.context.inventory;

import com.memberclub.domain.dataobject.sku.SkuInventoryInfo;
import com.memberclub.domain.entity.inventory.InventoryRecord;
import com.memberclub.domain.entity.inventory.InventoryTargetTypeEnum;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * 库存操作上下文，贯穿库存扣减与回补流程
 *
 * <p>author: 掘金五阳</p>
 */
@Data
public class InventoryOpContext {

    private InventoryTargetTypeEnum targetType; // 库存目标类型

    private InventoryOpCmd cmd; // 操作指令

    // 回补阶段使用历史记录构建要回补的库存
    Map<Long, List<InventoryRecord>> skuId2InventoryRecords; // SKU与历史记录映射

    // 扣减阶段基于商品信息构建要扣减的库存
    private Map<Long, SkuInventoryInfo> skuId2InventoryInfo; // SKU与库存信息映射

    /**
     * 是否允许进行库存操作
     *
     * @return true 表示可以操作
     */
    public boolean isOperatable() {
        // 当命令中包含SKU列表时才允许操作
        return !CollectionUtils.isEmpty(cmd.getSkus());
    }

}