/**
 * @(#)PerformDataObjectBuildFactory.java, 一月 04, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.service.domain;

import com.memberclub.common.log.CommonLog;
import com.memberclub.common.util.JsonUtils;
import com.memberclub.domain.context.perform.common.GrantTypeEnum;
import com.memberclub.domain.context.perform.common.PerformItemStatusEnum;
import com.memberclub.domain.context.perform.common.PeriodTypeEnum;
import com.memberclub.domain.context.perform.common.RightTypeEnum;
import com.memberclub.domain.context.perform.period.PeriodPerformContext;
import com.memberclub.domain.context.prefinance.task.FinanceTaskContent;
import com.memberclub.domain.dataobject.perform.MemberPerformItemDO;
import com.memberclub.domain.dataobject.perform.item.PerformItemExtraInfo;
import com.memberclub.domain.dataobject.task.OnceTaskDO;
import com.memberclub.domain.dataobject.task.TaskContentDO;
import com.memberclub.domain.dataobject.task.perform.PerformTaskContentDO;
import com.memberclub.domain.entity.trade.MemberPerformItem;
import com.memberclub.domain.entity.trade.OnceTask;
import com.memberclub.infrastructure.mapstruct.PerformConvertor;
import com.memberclub.infrastructure.mapstruct.PerformCustomConvertor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * author: 掘金五阳
 */
@Service
public class PerformDataObjectBuildFactory {

    public PeriodPerformContext buildPeriodPerformContext(OnceTaskDO taskDO) {
        PeriodPerformContext context = PerformConvertor.INSTANCE.toPeriodPerformContextForTask(taskDO);
        context.setSubTradeId(String.valueOf(context.getContent().getSubTradeId()));
        return context;
    }

    public List<MemberPerformItemDO> toMemberPerformItemDOs(List<MemberPerformItem> items) {
        return items.stream().map(this::toMemberPerformItemDO).collect(Collectors.toList());
    }

    public MemberPerformItemDO toMemberPerformItemDO(MemberPerformItem item) {
        MemberPerformItemDO itemDO = new MemberPerformItemDO();
        itemDO.setAssetCount(item.getAssetCount());
        itemDO.setBatchCode(item.getBatchCode());
        itemDO.setBuyIndex(item.getBuyIndex());
        itemDO.setCycle(item.getCycle());
        itemDO.setEtime(item.getEtime());
        itemDO.setExtra(JsonUtils.fromJson(item.getExtra(), PerformItemExtraInfo.class));
        itemDO.setGrantType(GrantTypeEnum.findByCode(item.getGrantType()));
        itemDO.setItemToken(item.getItemToken());
        itemDO.setPeriodCount(itemDO.getExtra().getGrantInfo().getPeriodCount());
        itemDO.setPeriodType(PeriodTypeEnum.findByCode(itemDO.getExtra().getGrantInfo().getPeriodType()));
        itemDO.setPhase(item.getPhase());
        itemDO.setProviderId(item.getProviderId());
        itemDO.setRightId(item.getRightId());
        itemDO.setRightType(RightTypeEnum.findByCode(item.getRightType()));
        itemDO.setSkuId(item.getSkuId());
        itemDO.setSubTradeId(item.getSubTradeId());
        itemDO.setStime(item.getStime());
        itemDO.setStatus(PerformItemStatusEnum.findByCode(item.getStatus()));
        return itemDO;
    }


    public OnceTaskDO toOnceTaskDO(OnceTask task) {
        OnceTaskDO taskDO = PerformConvertor.INSTANCE.toOnceTaskDO(task);
        TaskContentDO contentDO = PerformCustomConvertor.toTaskContentDO(task.getContent(), task.getTaskContentClassName());
        if (contentDO instanceof PerformTaskContentDO) {
            taskDO.setTradeId(((PerformTaskContentDO) contentDO).getTradeId());
        } else if (contentDO instanceof FinanceTaskContent) {
            taskDO.setTradeId(((FinanceTaskContent) contentDO).getTradeId());
        } else {
            CommonLog.error("未知的任务类型:{}, className:{}, content:{}", task.getTaskType(), task.getTaskContentClassName(), contentDO);
        }

        taskDO.setContent(contentDO);
        return taskDO;
    }
}