/**
 * @(#)InventoryRecord.java, 一月 29, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.entity.inventory;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 库存操作记录，用于记录每次库存变动
 *
 * <p>author: 掘金五阳</p>
 */
@Data
public class InventoryRecord {

    @TableId(type = IdType.AUTO)
    private Long id; // 主键ID

    private int bizType; // 业务类型

    private long userId; // 用户ID

    private String inventoryKey; // 库存键

    private String operateKey; // 操作单号

    private long targetId; // 目标ID

    private int targetType; // 目标类型

    private String subKey; // 子键

    private long opCount; // 操作数量

    private int opType; // 操作类型

    private long utime; // 更新时间

    private long ctime; // 创建时间
}