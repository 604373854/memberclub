/**
 * @(#)PreFinanceAssetsDomainService.java, 一月 25, 2025.
 * <p>
 * Copyright 2025 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.prefinance.service.domain;

import com.google.common.collect.Lists;
import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.util.CollectionUtilEx;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.context.prefinance.FinanceAssetDO;
import com.memberclub.domain.context.prefinance.PreFinanceContext;
import com.memberclub.domain.context.prefinance.PreFinanceEvent;
import com.memberclub.domain.context.prefinance.PreFinanceEventDetail;
import com.memberclub.domain.context.prefinance.common.PreFinanceEventEnum;
import com.memberclub.domain.dataobject.perform.MemberPerformItemDO;
import com.memberclub.domain.facade.AssetDO;
import com.memberclub.infrastructure.mq.MQTopicEnum;
import com.memberclub.infrastructure.mq.MessageQuenePublishFacade;
import com.memberclub.sdk.prefinance.extension.PreFinanceBuildAssetsExtension;
import com.memberclub.sdk.prefinance.extension.PreFinanceMessageBuildExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * author: 掘金五阳
 */
@Service
public class PreFinanceDomainService {

    @Autowired
    private ExtensionManager extensionManager;

    @Autowired
    private MessageQuenePublishFacade messageQuenePublishFacade;

    public void buildAssets(PreFinanceContext preFinanceContext) {
        PreFinanceBuildAssetsExtension extension =
                extensionManager.getExtension(BizScene.of(preFinanceContext.getBizType()), PreFinanceBuildAssetsExtension.class);
        boolean skipable = !extension.buildAssets(preFinanceContext);
        if (skipable) {
            return;
        }
        if (PreFinanceEventEnum.PERFORM == preFinanceContext.getPreFinanceEventEnum()) {
            extension.buildOnPerform(preFinanceContext);
        } else if (PreFinanceEventEnum.FREEZE_NON_REFUND == preFinanceContext.getPreFinanceEventEnum()) {
            extension.buildOnFreeze(preFinanceContext);
        } else if (PreFinanceEventEnum.EXPIRE == preFinanceContext.getPreFinanceEventEnum()) {
            extension.buildOnExpire(preFinanceContext);
        } else if (PreFinanceEventEnum.REFUND == preFinanceContext.getPreFinanceEventEnum()) {
            extension.buildOnRefund(preFinanceContext);
        }
    }

    public String buildMessage(PreFinanceContext context) {
        PreFinanceEvent event = new PreFinanceEvent();
        context.setPreFinanceEvent(event);
        event.setEvent(context.getPreFinanceEventEnum().getCode());
        event.setBizOrderId(String.valueOf(context.getSubTradeId()));
        event.setStime(context.getSubOrder().getStime());
        event.setEtime(context.getSubOrder().getEtime());
        event.setFinanceProductType(context.getSubOrder().getExtra().getSettleInfo().getFinanceProductType());
        event.setFinanceContractorId(context.getSubOrder().getExtra().getSettleInfo().getContractorId());
        event.setUserId(context.getUserId());
        event.setPeriodCount(context.getSubOrder().getExtra().getSettleInfo().getPeriodCycle());
        event.setPeriodIndex(context.getEvent().getDetail().getPeriodIndex());

        List<PreFinanceEventDetail> details = Lists.newArrayList();
        event.setDetails(details);

        for (MemberPerformItemDO performItem : context.getPerformItems()) {
            if (Boolean.FALSE.equals(performItem.getExtra().getSettleInfo().getFinanceable())) {
                CommonLog.warn("该履约项无需参与资产结算 itemToken:{}, assetBatchCode:{}",
                        performItem.getItemToken(), performItem.getBatchCode());
                continue;
            }
            details.add(
                    buildBasicFinanceEventDetail(context, performItem, context.getBatchCode2Assets().get(performItem.getBatchCode()))
            );
        }

        return extensionManager.getExtension(BizScene.of(context.getBizType()),
                PreFinanceMessageBuildExtension.class).buildMessage(context, event);
    }

    public void publish(PreFinanceContext context, String message) {
        messageQuenePublishFacade.publish(MQTopicEnum.PRE_FINANCE_EVENT, message);
        CommonLog.warn("发布预结算事件 {} topic:{}, message:{}", context.getPreFinanceEventEnum().getName(),
                MQTopicEnum.PRE_FINANCE_EVENT.getName(), message);
    }

    public PreFinanceEventDetail buildBasicFinanceEventDetail(PreFinanceContext context, MemberPerformItemDO item, List<AssetDO> assets) {
        PreFinanceEventDetail detail = new PreFinanceEventDetail();
        detail.setAssetNum(item.getAssetCount());
        detail.setStime(item.getStime());
        detail.setEtime(item.getEtime());
        if (context.getPreFinanceEventEnum() == PreFinanceEventEnum.PERFORM) {
            detail.setAssetNum(item.getAssetCount());
        } else if (context.getPreFinanceEventEnum() == PreFinanceEventEnum.EXPIRE) {
            detail.setAssetNum(assets.size());
        } else if (context.getPreFinanceEventEnum() == PreFinanceEventEnum.REFUND) {
            detail.setAssetNum(assets.size());
        } else if (context.getPreFinanceEventEnum() == PreFinanceEventEnum.FREEZE_NON_REFUND) {
            detail.setAssetNum(assets.size());
        }

        detail.setFinanceAssetType(item.getExtra().getSettleInfo().getFinanceAssetType());
        List<FinanceAssetDO> financeAssets = CollectionUtilEx.map(assets, (asset) -> {
            FinanceAssetDO financeAssetDO = new FinanceAssetDO();
            financeAssetDO.setAssetId(String.valueOf(asset.getAssetId()));
            return financeAssetDO;
        });
        detail.setAssets(financeAssets);
        return detail;
    }
}