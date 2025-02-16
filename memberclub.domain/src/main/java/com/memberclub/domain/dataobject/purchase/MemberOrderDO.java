/**
 * @(#)MemberOrderDO.java, 十二月 14, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.dataobject.purchase;

import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.context.perform.common.MemberOrderPerformStatusEnum;
import com.memberclub.domain.context.perform.common.SubOrderPerformStatusEnum;
import com.memberclub.domain.context.perform.reverse.ReversePerformContext;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.domain.context.purchase.cancel.PurchaseCancelContext;
import com.memberclub.domain.context.purchase.common.MemberOrderStatusEnum;
import com.memberclub.domain.context.purchase.common.PurchaseSourceEnum;
import com.memberclub.domain.context.purchase.common.SubOrderStatusEnum;
import com.memberclub.domain.dataobject.CommonUserInfo;
import com.memberclub.domain.dataobject.order.LocationInfo;
import com.memberclub.domain.dataobject.order.MemberOrderExtraInfo;
import com.memberclub.domain.dataobject.order.MemberOrderFinanceInfo;
import com.memberclub.domain.dataobject.order.MemberOrderSaleInfo;
import com.memberclub.domain.dataobject.perform.MemberSubOrderDO;
import com.memberclub.domain.exception.ResultCode;
import lombok.Data;

import java.util.List;

/**
 * @author 掘金五阳
 */
@Data
public class MemberOrderDO {

    private BizTypeEnum bizType;

    private long userId;

    private String tradeId;

    private PurchaseSourceEnum source;

    private OrderInfoDO orderInfo;

    private LocationInfo locationInfo;

    private CommonUserInfo userInfo;

    private MemberOrderFinanceInfo settleInfo;

    private MemberOrderSaleInfo saleInfo;

    private MemberOrderExtraInfo extra;

    private Integer actPriceFen;

    private Integer originPriceFen;

    private Integer salePriceFen;

    private MemberOrderStatusEnum status;

    private com.memberclub.domain.context.perform.common.MemberOrderPerformStatusEnum performStatus;

    private long stime;

    private long etime;

    private long utime;

    private long ctime;

    private List<MemberSubOrderDO> subOrders;

    public boolean isPerformed() {
        return getStatus() == MemberOrderStatusEnum.PERFORMED;
    }

    public void onSubmitSuccess(PurchaseSubmitContext context) {
        status = MemberOrderStatusEnum.SUBMITED;
        for (MemberSubOrderDO subOrder : subOrders) {
            subOrder.setStatus(SubOrderStatusEnum.SUBMITED);
        }
    }

    public void onSubmitFail(PurchaseSubmitContext context) {
        status = MemberOrderStatusEnum.FAIL;
        for (MemberSubOrderDO subOrder : subOrders) {
            subOrder.setStatus(SubOrderStatusEnum.FAIL);
        }
    }


    public void onSubmitCancel(PurchaseCancelContext context) {
        status = MemberOrderStatusEnum.CANCELED;
        for (MemberSubOrderDO subOrder : subOrders) {
            subOrder.setStatus(SubOrderStatusEnum.CANCELED);
        }
    }

    public void onReversePerformSuccess(ReversePerformContext context) {
        boolean hasPortionReverse = false;
        for (MemberSubOrderDO subOrder : subOrders) {
            if (subOrder.getPerformStatus() == SubOrderPerformStatusEnum.PORTION_REVERSED) {
                hasPortionReverse = true;
            }
        }
        if (hasPortionReverse) {
            performStatus = MemberOrderPerformStatusEnum.PORTION_REVERSED;
        } else {
            performStatus = MemberOrderPerformStatusEnum.COMPLETED_REVERSED;
        }
    }

    public void onRefundSuccess(AfterSaleApplyContext context) {
        if (performStatus == MemberOrderPerformStatusEnum.COMPLETED_REVERSED) {
            status = MemberOrderStatusEnum.COMPLETE_REFUNDED;
        } else if (performStatus == MemberOrderPerformStatusEnum.PORTION_REVERSED) {
            status = MemberOrderStatusEnum.PORTION_REFUNDED;
        } else {
            throw ResultCode.INTERNAL_ERROR.newException(String.format("更新主单状态为退款成功时 未预期的履约状态:%s", performStatus));
        }

        for (MemberSubOrderDO subOrder : subOrders) {
            if (subOrder.getPerformStatus() == SubOrderPerformStatusEnum.COMPLETED_REVERSED) {
                subOrder.setStatus(SubOrderStatusEnum.REFUNDED);
            } else if (subOrder.getPerformStatus() == SubOrderPerformStatusEnum.PORTION_REVERSED) {
                subOrder.setStatus(SubOrderStatusEnum.PORTION_REFUNDED);
            } else {
                throw ResultCode.INTERNAL_ERROR.newException(String.format("更新子单状态为退款成功时 未预期的履约状态:%s", subOrder.getPerformStatus()));
            }
        }
    }

    public void onPerformSuccess(PerformContext context) {
        setStatus(MemberOrderStatusEnum.PERFORMED);
        setPerformStatus(MemberOrderPerformStatusEnum.PERFORMED);
        setUtime(System.currentTimeMillis());
        setStime(context.getStime());
        setEtime(context.getEtime());
    }
}