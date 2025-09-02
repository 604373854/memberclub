/**
 * @(#)Inventory.java, 一月 29, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.entity.inventory;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.memberclub.domain.dataobject.inventory.InventoryCacheDO;
import com.memberclub.domain.dataobject.sku.InventoryTypeEnum;
import lombok.Data;

/**
 * 库存实体
 *
 * <p>author: 掘金五阳</p>
 */
@Data
public class Inventory {

    @TableId(type = IdType.AUTO)
    private Long id; // 主键ID

    private int bizType; // 业务类型

    private Long targetId; // 目标ID

    private int targetType; // 目标类型

    private String subKey; // 子键

    private long saleCount; // 销售数量

    private long totalCount; // 总数量

    private long stime; // 开始时间

    private long etime; // 结束时间

    private int status; // 状态

    private long version; // 版本号

    private long utime; // 更新时间

    private long ctime; // 创建时间

    /**
     * 构建库存键
     *
     * @param targetType 目标类型
     * @param targetId   目标ID
     * @param subKey     子键
     * @return 库存键
     */
    public static String buildInventoryKey(int targetType, Long targetId, String subKey) {
        // 通过目标类型、目标ID和子键拼接库存键
        return String.format("%s_%s_%s", targetType, targetId, subKey);
    }

    /**
     * 根据库存类型构建子键
     *
     * @param type 库存类型
     * @return 子键
     */
    public static String buildSubKey(InventoryTypeEnum type) {
        // 总库存使用固定子键
        if (type == InventoryTypeEnum.TOTAL) {
            return "total";
        }
        // 其他类型返回空字符串
        return "";
    }

    /**
     * 将库存对象转换为缓存对象
     *
     * @param inventory 库存对象
     * @return 缓存对象
     */
    public static InventoryCacheDO toCache(Inventory inventory) {
        InventoryCacheDO cache = new InventoryCacheDO();
        // 复制销售数量
        cache.setSaleCount(inventory.getSaleCount());
        // 复制总数量
        cache.setTotalCount(inventory.getTotalCount());
        // 复制版本号
        cache.setVersion(inventory.getVersion());
        // 设置目标信息和子键
        cache.setTargetId(inventory.getTargetId());
        cache.setTargetType(inventory.getTargetType());
        cache.setSubKey(inventory.getSubKey());
        return cache;
    }
}
