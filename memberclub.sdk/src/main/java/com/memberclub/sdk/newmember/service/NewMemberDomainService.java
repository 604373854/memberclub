/**
 * @(#)NewMemberDomainService.java, 一月 31, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.newmember.service;

import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.context.usertag.*;
import com.memberclub.domain.dataobject.newmember.NewMemberMarkContext;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.infrastructure.dynamic_config.SwitchEnum;
import com.memberclub.infrastructure.usertag.UserTagService;
import com.memberclub.sdk.newmember.extension.NewMemberExtension;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * author: 掘金五阳
 *
 * 提供对新会员标签的增删查等核心能力。
 */
@Service
public class NewMemberDomainService {

    @Autowired
    private ExtensionManager extensionManager;

    @Autowired
    private UserTagService userTagService;

    /**
     * 为指定上下文中的用户打上新会员标签。
     *
     * @param context 新会员标记上下文
     */
    public void mark(NewMemberMarkContext context) {
        UserTagOpCmd cmd = new UserTagOpCmd();
        cmd.buildUniqueKey(UserTagTypeEnum.newmember, context.getBizType(), context.getUniqueKey());
        cmd.setOpType(UserTagOpTypeEnum.ADD);
        cmd.setExpireSeconds(
                SwitchEnum.NEW_MEMBER_USER_TAG_UNIQUE_KEY_TIMEOUT.getLong(context.getBizType().getCode())
        );

        extensionManager.getExtension(BizScene.of(context.getBizType()),
                NewMemberExtension.class).buildUserTagOpList(context);
        List<UserTagOpDO> userTagOpDOList = context.getUserTagOpDOList();
        if (CollectionUtils.isEmpty(userTagOpDOList)) {
            CommonLog.info("无用户标签需要标记 context:{}", context);
            return;
        }
        cmd.setTags(userTagOpDOList);

        try {
            UserTagOpResponse response = userTagService.operate(cmd);
            if (!response.isSuccess()) {
                CommonLog.error("提单成功后写新会员usertag失败,内部有重试! cmd:{}", cmd);
                return;
            }
            CommonLog.info("提单成功后写新会员usertag成功 cmd:{}", cmd);
        } catch (Exception e) {
            CommonLog.error("提单成功后写新会员usertag异常,内部有重试! cmd:{}", cmd, e);
        }
    }

    /**
     * 取消指定上下文中的新会员标签。
     *
     * @param context 新会员标记上下文
     */
    public void unmark(NewMemberMarkContext context) {
        UserTagOpCmd cmd = new UserTagOpCmd();
        cmd.buildUniqueKey(UserTagTypeEnum.newmember, context.getBizType(), context.getUniqueKey());
        cmd.setOpType(UserTagOpTypeEnum.DEL);
        cmd.setExpireSeconds(
                SwitchEnum.NEW_MEMBER_USER_TAG_UNIQUE_KEY_TIMEOUT.getLong(context.getBizType().getCode())
        );

        extensionManager.getExtension(BizScene.of(context.getBizType()),
                NewMemberExtension.class).buildUserTagOpList(context);
        List<UserTagOpDO> userTagOpDOList = context.getUserTagOpDOList();
        if (CollectionUtils.isEmpty(userTagOpDOList)) {
            CommonLog.info("无用户标签需要 取消标记 context:{}", context);
            return;
        }
        cmd.setTags(userTagOpDOList);
        try {
            UserTagOpResponse response = userTagService.operate(cmd);
            if (!response.isSuccess()) {
                CommonLog.error("删除新会员usertag失败,内部有重试! cmd:{}", cmd);
                return;
            }
            CommonLog.info("删除新会员usertag成功 cmd:{}", cmd);
        } catch (Exception e) {
            CommonLog.error("删除新会员usertag异常,内部有重试! cmd:{}", cmd, e);
        }
    }


    /**
     * 校验用户是否为新会员，并在上下文中写入结果。
     *
     * @param context 新会员标记上下文
     */
    public void validate(NewMemberMarkContext context) {
        UserTagOpCmd cmd = new UserTagOpCmd();
        cmd.setOpType(UserTagOpTypeEnum.GET);

        extensionManager.getExtension(BizScene.of(context.getBizType()),
                NewMemberExtension.class).buildUserTagOpList(context);
        List<UserTagOpDO> userTagOpDOList = context.getUserTagOpDOList();
        if (CollectionUtils.isEmpty(userTagOpDOList)) {
            return;
        }
        cmd.setTags(userTagOpDOList);
        try {
            UserTagOpResponse response = userTagService.operate(cmd);
            if (!response.isSuccess()) {
                CommonLog.error("查新会员usertag失败 cmd:{}, response:{}", cmd, response);
                throw ResultCode.NEW_MEMBER_ERROR.newException("查新会员 usertag 失败");
            }
            boolean newmember = true;
            if (CollectionUtils.isEmpty(response.getTags())) {
                CommonLog.info("查询新会员usertag为空, 认定为新会员 cmd:{}, response:{}", cmd, response);
                newmember = true;
            } else {
                for (UserTagDO tag : response.getTags()) {
                    if (tag.getCount() > 0) {
                        CommonLog.info("查询新会员usertag后, 非新会员 cmd:{}, response:{}", cmd, response);
                        newmember = false;
                    }
                }
            }
            context.setNewmember(newmember);
        } catch (Exception e) {
            CommonLog.error("查询新会员usertag异常 cmd:{}", cmd, e);
            throw ResultCode.NEW_MEMBER_ERROR.newException("新会员标记查询异常", e);
        }
    }
}