/**
 * @(#)PurchaseSubmitVO.java, 一月 04, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.controller.vo;

import com.memberclub.domain.context.purchase.PurchaseSkuSubmitCmd;
import com.memberclub.domain.dataobject.CommonUserInfo;
import com.memberclub.domain.dataobject.aftersale.ClientInfo;
import com.memberclub.domain.dataobject.order.LocationInfo;
import lombok.Data;

import java.util.List;

/**
 * author: 掘金五阳
 */
@Data
public class PurchaseSubmitVO {

    private Integer bizType;

    private CommonUserInfo userInfo;

    private LocationInfo locationInfo;

    private ClientInfo clientInfo;

    private List<PurchaseSkuSubmitCmd> skus;

    private Integer submitSource;

    private String submitToken;
}