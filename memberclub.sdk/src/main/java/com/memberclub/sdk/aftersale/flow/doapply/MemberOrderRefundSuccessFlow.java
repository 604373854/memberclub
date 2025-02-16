/**
 * @(#)MemberOrderCompleteRefundFlow.java, 一月 05, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.flow.doapply;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.sdk.aftersale.service.domain.AftersaleDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class MemberOrderRefundSuccessFlow extends FlowNode<AfterSaleApplyContext> {

    @Autowired
    private AftersaleDomainService aftersaleDomainService;

    @Override
    public void process(AfterSaleApplyContext context) {

    }

    @Override
    public void success(AfterSaleApplyContext context) {
        aftersaleDomainService.onRefundSuccessForMemberOrder(context);
    }
}