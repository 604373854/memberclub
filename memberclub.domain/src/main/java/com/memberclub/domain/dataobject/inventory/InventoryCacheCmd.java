/**
 * @(#)InventoryCacheCmd.java, 一月 30, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.dataobject.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 批量缓存库存的命令对象
 *
 * <p>author: 掘金五阳</p>
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class InventoryCacheCmd {

    List<InventoryCacheDO> caches; // 需要缓存的库存列表
}