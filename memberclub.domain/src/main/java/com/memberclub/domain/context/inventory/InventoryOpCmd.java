/**
 * @(#)InventoryOpCmd.java, 一月 29, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.context.inventory;

import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.purchase.common.SubmitSourceEnum;
import com.memberclub.domain.entity.inventory.InventoryTargetTypeEnum;
import lombok.Data;

import java.util.List;

/**
 * 库存操作指令，封装一次库存变动请求
 *
 * <p>author: 掘金五阳</p>
 */
@Data
public class InventoryOpCmd {

    private long userId; // 用户ID

    private BizTypeEnum bizType; // 业务类型

    /**
     * 操作来源
     *
     * @see SubmitSourceEnum
     */
    private int source; // 提交来源

    private String operateKey; // 操作流水号

    private InventoryOpTypeEnum opType; // 操作类型

    private List<InventorySkuOpDO> skus; // 需要操作的SKU列表

    private InventoryTargetTypeEnum targetType; // 库存目标类型

    private long submitTime; // 提交时间
}