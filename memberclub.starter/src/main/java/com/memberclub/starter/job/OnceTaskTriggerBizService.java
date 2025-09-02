/**
 * @(#)OnceTaskTriggerDomainService.java, 一月 27, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.job;

import com.google.common.collect.Lists;
import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.context.oncetask.common.OnceTaskStatusEnum;
import com.memberclub.domain.context.oncetask.common.TaskTypeEnum;
import com.memberclub.domain.context.oncetask.trigger.OnceTaskTriggerCmd;
import com.memberclub.domain.context.oncetask.trigger.OnceTaskTriggerContext;
import com.memberclub.infrastructure.dynamic_config.SwitchEnum;
import com.memberclub.sdk.oncetask.trigger.extension.OnceTaskTriggerExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static com.memberclub.infrastructure.dynamic_config.SwitchEnum.ONCE_TASK_SCAN_PERIOD_PERFORM_ELASPED_DAYS;

/**
 * 负责触发一次性任务的执行，不同触发类型（例如周期执行、资金过期）
 * 会被转换为 {@link OnceTaskTriggerCmd} 并下发到对应的扩展实现。
 *
 * <p>该类本身逻辑较少，真实业务由从 {@link ExtensionManager} 获取的
 * {@link OnceTaskTriggerExtension} 执行。</p>
 *
 * author: 掘金五阳
 */
@Service
public class OnceTaskTriggerBizService {

    @Autowired
    private ExtensionManager extensionManager;


    /**
     * 触发周期执行的任务，根据配置计算时间窗口并填充指令，最终交由
     * {@link #trigger(OnceTaskTriggerCmd)} 执行。
     *
     * @param cmd 描述任务的指令
     */
    public void triggerPeriodPerform(OnceTaskTriggerCmd cmd) {
        cmd.setTaskType(TaskTypeEnum.PERIOD_PERFORM);

        long minStime =
                TimeUtil.now() - TimeUnit.DAYS.toMillis(ONCE_TASK_SCAN_PERIOD_PERFORM_ELASPED_DAYS.getInt(cmd.getBizType().getCode()));

        long maxStime = TimeUtil.now() +
                TimeUnit.DAYS.toMillis(SwitchEnum.ONCE_TASK_SCAN_PERIOD_PERFORM_PRE_DAYS.getInt(cmd.getBizType().getCode()));

        // 仅触发指定状态范围内的任务
        cmd.setStatus(Lists.newArrayList(OnceTaskStatusEnum.FAIL, OnceTaskStatusEnum.INIT, OnceTaskStatusEnum.PROCESSING));
        cmd.setMinTriggerStime(minStime);
        cmd.setMaxTriggerStime(maxStime);

        trigger(cmd);
    }

    /**
     * 触发资金相关的过期任务。
     *
     * @param cmd 描述任务的指令
     */
    public void triggerFinanceExpire(OnceTaskTriggerCmd cmd) {
        cmd.setTaskType(TaskTypeEnum.FINANCE_EXPIRE);
        // 触发失败或未处理的任务
        cmd.setStatus(Lists.newArrayList(OnceTaskStatusEnum.FAIL, OnceTaskStatusEnum.INIT, OnceTaskStatusEnum.PROCESSING));

        trigger(cmd);
    }

    /**
     * 将执行委托给对应业务的 {@link OnceTaskTriggerExtension}，本方法仅准备上下文并收集统计信息。
     *
     * @param cmd 描述任务的指令
     */
    public void trigger(OnceTaskTriggerCmd cmd) {
        OnceTaskTriggerContext context = new OnceTaskTriggerContext();
        context.setBizType(cmd.getBizType());
        context.setUserIds(cmd.getUserIds());
        context.setTaskGroupIds(cmd.getTaskGroupIds());
        context.setStatus(cmd.getStatus());
        context.setTaskType(cmd.getTaskType());
        context.setNow(TimeUtil.now());
        context.setMinTriggerStime(cmd.getMinTriggerStime());
        context.setMaxTriggerStime(cmd.getMaxTriggerStime());
        context.setSuccessCount(new AtomicLong(0));
        context.setFailCount(new AtomicLong(0));
        context.setTotalCount(new AtomicLong(0));

        // 根据业务场景获取扩展实现并触发任务
        extensionManager.getExtension(BizScene.of(cmd.getBizType(), cmd.getTaskType().getCode() + ""),
                OnceTaskTriggerExtension.class).trigger(context);
    }
}