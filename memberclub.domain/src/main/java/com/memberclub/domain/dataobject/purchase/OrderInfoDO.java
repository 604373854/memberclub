/**
 * @(#)OrderInfoDO.java, 一月 04, 2025.
 * <p>
 * Copyright 2025 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.dataobject.purchase;

import com.memberclub.domain.common.OrderSystemTypeEnum;
import lombok.Data;

/**
 * author: 掘金五阳
 */
@Data
public class OrderInfoDO {
    private OrderSystemTypeEnum orderSystemType;

    private String orderId;
}