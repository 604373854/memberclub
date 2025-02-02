/**
 * @(#)MemberShipDataObjectFactory.java, 二月 01, 2025.
 * <p>
 * Copyright 2025 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.membership.service;

import com.memberclub.common.util.JsonUtils;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.perform.PerformItemContext;
import com.memberclub.domain.dataobject.membership.MemberShipDO;
import com.memberclub.domain.dataobject.membership.MemberShipExtraDO;
import com.memberclub.domain.dataobject.membership.MemberShipStatusEnum;
import com.memberclub.domain.dataobject.perform.MemberPerformItemDO;
import com.memberclub.domain.entity.trade.MemberShip;
import com.memberclub.infrastructure.mapstruct.PerformConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class MemberShipDataObjectFactory {

    @Autowired
    private MemberShipDataObjectFactory memberShipDataObjectFactory;

    public MemberShip buildMemberShip(MemberShipDO memberShipDO) {
        MemberShip memberShip = PerformConvertor.INSTANCE.toMemberShip(memberShipDO);
        memberShip.setStatus(memberShipDO.getStatus().getCode());
        memberShip.setBizType(memberShipDO.getBizType().getCode());
        memberShip.setExtra(JsonUtils.toJson(memberShipDO.getExtra()));
        return memberShip;
    }


    public MemberShipDO buildMemberShipDO(MemberShip memberShip) {
        MemberShipDO memberShipDO = PerformConvertor.INSTANCE.toMemberShipDO(memberShip);
        memberShipDO.setStatus(MemberShipStatusEnum.findByCode(memberShip.getStatus()));
        memberShipDO.setBizType(BizTypeEnum.findByCode(memberShip.getBizType()));
        memberShipDO.setExtra(JsonUtils.fromJson(memberShip.getExtra(), MemberShipExtraDO.class));
        return memberShipDO;
    }

    public MemberShipDO buildMemberShipDO(PerformItemContext context, MemberPerformItemDO item) {
        MemberShipDO memberShipDO = new MemberShipDO();
        memberShipDO.setBizType(context.getBizType());
        memberShipDO.setCtime(TimeUtil.now());
        memberShipDO.setUtime(TimeUtil.now());
        memberShipDO.setEtime(item.getStime());
        memberShipDO.setStime(item.getStime());
        memberShipDO.setExtra(new MemberShipExtraDO());
        memberShipDO.setGrantCode(item.getItemToken());
        memberShipDO.setRightId(item.getRightId());
        memberShipDO.setStatus(MemberShipStatusEnum.INIT);
        memberShipDO.setSubTradeId(item.getSubTradeId());
        memberShipDO.setTradeId(context.getTradeId());
        memberShipDO.setItemToken(item.getItemToken());
        memberShipDO.setUserId(context.getUserId());
        return memberShipDO;
    }
}