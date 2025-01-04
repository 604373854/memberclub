/**
 * @(#)CalculateDelayPerformItemPeriodFlow.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.flow.build;

import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.flow.FlowNode;
import com.memberclub.common.util.TimeRange;
import com.memberclub.domain.context.perform.common.RightTypeEnum;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.context.perform.SubOrderPerformContext;
import com.memberclub.domain.dataobject.perform.MemberPerformItemDO;
import com.memberclub.sdk.perform.extension.build.PerformItemCalculateExtension;
import com.memberclub.sdk.uti.BizUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * author: 掘金五阳
 */
@Service
public class CalculateDelayPerformItemPeriodFlow extends FlowNode<PerformContext> {


    @Autowired
    private ExtensionManager extensionManager;

    @Override
    public void process(PerformContext context) {
        for (SubOrderPerformContext subOrderPerformContext : context.getSubOrderPerformContexts()) {
            if (CollectionUtils.isEmpty(subOrderPerformContext.getDelayPerformItems())) {
                continue;
            }


            Map<RightTypeEnum, List<MemberPerformItemDO>> rightType2Items =
                    subOrderPerformContext.getDelayPerformItems().stream().collect(Collectors.groupingBy(MemberPerformItemDO::getRightType));

            for (Map.Entry<RightTypeEnum, List<MemberPerformItemDO>> entry : rightType2Items.entrySet()) {
                long stime = context.getImmediatePerformEtime() + 1;

                Collections.sort(entry.getValue());

                for (MemberPerformItemDO delayItem : entry.getValue()) {
                    // TODO: 2024/12/15
                    TimeRange timeRange = extensionManager.getExtension(context.toDefaultScene(),
                            PerformItemCalculateExtension.class).buildDelayPeriod(stime, delayItem);
                    delayItem.setStime(timeRange.getStime());
                    delayItem.setEtime(timeRange.getEtime());
                    stime = timeRange.getEtime() + 1;

                    String itemToken = BizUtils.toItemToken(
                            subOrderPerformContext.getSubOrder().getSubOrderToken(),
                            delayItem.getRightId(),
                            delayItem.getBuyIndex(), delayItem.getPhase());
                    delayItem.setItemToken(itemToken);
                }
            }
        }
    }
}