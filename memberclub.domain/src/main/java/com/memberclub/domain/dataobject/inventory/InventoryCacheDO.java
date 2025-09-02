/**
 * @(#)InventoryCacheDO.java, 一月 29, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.dataobject.inventory;

import com.memberclub.domain.entity.inventory.Inventory;
import lombok.Data;

import java.io.Serializable;

/**
 * 库存缓存数据对象
 *
 * <p>author: 掘金五阳</p>
 */
@Data
public class InventoryCacheDO implements Serializable {

    public Long targetId; // 目标ID

    public int targetType; // 目标类型

    public String subKey; // 子键

    public long totalCount; // 总数量

    public long saleCount; // 已售数量

    public long version; // 版本号

    /**
     * 构建缓存中的唯一键
     *
     * @return 缓存键
     */
    public String getKey() {
        // 通过目标信息和子键拼接缓存键
        return Inventory.buildInventoryKey(targetType, targetId, subKey);
    }
}