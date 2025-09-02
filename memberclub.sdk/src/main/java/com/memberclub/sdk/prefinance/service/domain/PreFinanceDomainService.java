/**
 * @(#)PreFinanceAssetsDomainService.java, 一月 25, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
import com.memberclub.domain.dataobject.task.OnceTaskDO;
import com.memberclub.domain.facade.AssetDO;
import com.memberclub.infrastructure.mq.MQTopicEnum;
import com.memberclub.infrastructure.mq.MessageQuenePublishFacade;
import com.memberclub.infrastructure.mybatis.mappers.trade.OnceTaskDao;
import com.memberclub.sdk.prefinance.extension.PreFinanceBuildAssetsExtension;
import com.memberclub.sdk.prefinance.extension.PreFinanceMessageBuildExtension;
import com.memberclub.sdk.prefinance.extension.PreFinanceRepositoryExtension;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * author: 掘金五阳
 *
 * 处理预结算相关的资产构建、消息发送等领域逻辑。
 */
@Service
public class PreFinanceDomainService {

    @Autowired
    private ExtensionManager extensionManager;

    @Autowired
    private MessageQuenePublishFacade messageQuenePublishFacade;

    @Autowired
    private PreFinanceDataObjectFactory preFinanceDataObjectFactory;

    @Autowired
    private OnceTaskDao onceTaskDao;

    /**
     * 按照业务场景构建预结算资产数据。
     *
     * @param preFinanceContext 预结算上下文
     */
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

    /**
     * 根据上下文构建预结算事件消息。
     *
     * @param context 预结算上下文
     * @return 序列化后的消息内容
     */
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
            if (!performItem.isFinanceable()) {
                CommonLog.warn("该履约项无需参与资产结算 itemToken:{}, assetBatchCode:{}",
                        performItem.getItemToken(), performItem.getBatchCode());
                continue;
            }
            if (MapUtils.isEmpty(context.getItemToken2Assets())) {
                continue;
            }
            details.add(
                    buildBasicFinanceEventDetail(context, performItem, context.getItemToken2Assets().get(performItem.getItemToken()))
            );
        }

        return extensionManager.getExtension(BizScene.of(context.getBizType()),
                PreFinanceMessageBuildExtension.class).buildMessage(context, event);
    }

    /**
     * 将预结算事件发布到消息队列。
     *
     * @param context 预结算上下文
     * @param message 消息内容
     */
    public void publish(PreFinanceContext context, String message) {
        messageQuenePublishFacade.publish(MQTopicEnum.PRE_FINANCE_EVENT, message);
        CommonLog.warn("发布预结算事件 {} topic:{}, message:{}", context.getPreFinanceEventEnum().getName(),
                MQTopicEnum.PRE_FINANCE_EVENT.getName(), message);
    }

    /**
     * 构造预结算事件中的基础明细信息。
     *
     * @param context 预结算上下文
     * @param item    履约项
     * @param assets  关联的资产列表
     * @return 预结算事件明细
     */
    public PreFinanceEventDetail buildBasicFinanceEventDetail(PreFinanceContext context, MemberPerformItemDO item, List<AssetDO> assets) {
        PreFinanceEventDetail detail = new PreFinanceEventDetail();
        detail.setStime(item.getStime());
        detail.setEtime(item.getEtime());
        detail.setAssetBatchCode(item.getBatchCode());
        detail.setFinanceAssetType(item.getExtra().getSettleInfo().getFinanceAssetType());
        if (context.getPreFinanceEventEnum() == PreFinanceEventEnum.PERFORM) {
            detail.setAssetNum(item.getTotalCount());
            buildFinanceAssets(assets, detail);
        } else if (context.getPreFinanceEventEnum() == PreFinanceEventEnum.EXPIRE) {
            detail.setAssetNum(assets.size());
            buildFinanceAssets(assets, detail);
        } else if (context.getPreFinanceEventEnum() == PreFinanceEventEnum.REFUND) {
            //detail.setAssetNum(assets.size());

        } else if (context.getPreFinanceEventEnum() == PreFinanceEventEnum.FREEZE_NON_REFUND) {
            detail.setAssetNum(assets.size());
            buildFinanceAssets(assets, detail);
        }

        return detail;
    }

    /**
     * 将资产列表转换为金融资产并写入明细。
     *
     * @param assets 资产列表
     * @param detail 预结算事件明细
     */
    private void buildFinanceAssets(List<AssetDO> assets, PreFinanceEventDetail detail) {
        List<FinanceAssetDO> financeAssets = CollectionUtilEx.map(assets, (asset) -> {
            FinanceAssetDO financeAssetDO = new FinanceAssetDO();
            financeAssetDO.setAssetId(String.valueOf(asset.getAssetId()));
            return financeAssetDO;
        });
        detail.setAssets(financeAssets);
    }

    /**
     * 创建资产过期任务。
     *
     * @param context 预结算上下文
     */
    public void onCreateExpireTask(PreFinanceContext context) {
        List<OnceTaskDO> taskDOList = Lists.newArrayList();
        for (MemberPerformItemDO performItem : context.getPerformItems()) {
            if (!performItem.isFinanceable()) {
                CommonLog.warn("该履约项无需参与资产过期结算 itemToken:{}, assetBatchCode:{}",
                        performItem.getItemToken(), performItem.getBatchCode());
                continue;
            }
            OnceTaskDO task = preFinanceDataObjectFactory.buildFinanceExpireTask(context, performItem);
            taskDOList.add(task);
        }
        extensionManager.getExtension(BizScene.of(context.getBizType()),
                PreFinanceRepositoryExtension.class).onCreateExpiredTask(context, taskDOList);
    }
}