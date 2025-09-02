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
import com.memberclub.domain.exception.ResultCode;
import lombok.Data;

import java.util.List;

/**
 * 前端提交购买请求时使用的视图对象。
 */
@Data
public class PurchaseSubmitVO {

    private Integer bizId;

    private CommonUserInfo userInfo;

    private LocationInfo locationInfo;

    private ClientInfo clientInfo;

    private List<PurchaseSkuSubmitCmd> skus;

    private Integer submitSource;

    private String submitToken;

    /**
     * 基本参数校验。
     *
     * @return 参数合法返回 {@code true}
     */
    public boolean isValid() {
        //TODO 补充校验
        if (bizId == null) {
            // bizId 是确定业务上下文的必传字段
            throw ResultCode.PARAM_VALID.newException("bizId必传");
        }
        return true;
    }
}