/**
 * @(#)InventoryOpResponse.java, 一月 29, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.context.inventory;

import lombok.Data;

/**
 * 库存操作响应，封装处理结果
 *
 * <p>author: 掘金五阳</p>
 */
@Data
public class InventoryOpResponse {

    private boolean success; // 是否成功

    private boolean needRetry; // 是否需要重试

    private int errorCode; // 错误码

    private String msg; // 错误信息

    private Exception e; // 异常对象
}