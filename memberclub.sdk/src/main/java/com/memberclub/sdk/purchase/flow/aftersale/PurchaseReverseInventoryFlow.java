/**
 * @(#)PurchaseReverseInventoryFlow.java, 二月 01, 2025.
 * <p>
 * Copyright 2025 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.purchase.flow.aftersale;

import com.google.common.collect.Lists;
import com.memberclub.common.flow.FlowNode;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.retry.Retryable;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.context.inventory.InventoryOpCmd;
import com.memberclub.domain.context.inventory.InventoryOpResponse;
import com.memberclub.domain.context.inventory.InventoryOpTypeEnum;
import com.memberclub.domain.context.inventory.InventorySkuOpDO;
import com.memberclub.domain.dataobject.perform.MemberSubOrderDO;
import com.memberclub.domain.entity.inventory.InventoryTargetTypeEnum;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.sdk.inventory.service.InventoryBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * author: 掘金五阳
 */
@Service
public class PurchaseReverseInventoryFlow extends FlowNode<AfterSaleApplyContext> {

    @Autowired
    private PurchaseReverseInventoryFlow purchaseReverseInventoryFlow;

    @Autowired
    private InventoryBizService inventoryBizService;

    @Override
    public void process(AfterSaleApplyContext context) {

    }

    @Override
    public void success(AfterSaleApplyContext context) {
        InventoryOpCmd cmd = new InventoryOpCmd();
        cmd.setUserId(context.getCmd().getUserId());
        cmd.setTargetType(InventoryTargetTypeEnum.SKU);
        cmd.setSource(context.getPreviewContext().getMemberOrder().getSource().getCode());
        cmd.setOpType(InventoryOpTypeEnum.ROLLBACK);
        cmd.setOperateKey(context.getPreviewContext().getMemberOrder().getTradeId());
        cmd.setBizType(context.getPreviewContext().getMemberOrder().getBizType());
        List<InventorySkuOpDO> skuOpDOList = Lists.newArrayList();
        for (MemberSubOrderDO subOrder : context.getPreviewContext().getMemberOrder().getSubOrders()) {
            InventorySkuOpDO skuOpDO = new InventorySkuOpDO();
            skuOpDO.setSkuId(subOrder.getSkuId());
            skuOpDO.setCount(subOrder.getBuyCount());
            skuOpDOList.add(skuOpDO);
        }
        cmd.setSkus(skuOpDOList);
        InventoryOpResponse response = inventoryBizService.operateSkuInventory(cmd);
        if (response.isNeedRetry()) {
            CommonLog.error("库存回补操作需要重试 response:{} cmd:{}", response, cmd);
            purchaseReverseInventoryFlow.retryOperate(cmd);
        }

        if (!response.isSuccess()) {
            throw ResultCode.COMMON_ORDER_SUBMIT_ERROR.newException("回补库存失败", response.getE());
        }
    }


    @Retryable(throwException = false)
    public void retryOperate(InventoryOpCmd cmd) {
        inventoryBizService.operateSkuInventory(cmd);
    }
}