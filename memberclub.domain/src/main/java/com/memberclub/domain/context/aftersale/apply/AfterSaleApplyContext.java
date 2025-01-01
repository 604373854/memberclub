/**
 * @(#)AfterSaleApplyContext.java, 十二月 22, 2024.
 * <p>
 * Copyright 2024 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.context.aftersale.apply;

import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.context.aftersale.preview.AftersalePreviewContext;
import com.memberclub.domain.dataobject.aftersale.AftersaleOrderDO;
import com.memberclub.domain.entity.AftersaleOrder;
import lombok.Data;

/**
 * author: 掘金五阳
 */
@Data
public class AfterSaleApplyContext {

    private String scene;

    /******************************************/

    //售后预览和入参

    private AftersaleApplyCmd cmd;

    private AftersalePreviewContext previewContext;

    private String lockValue;//成功后释放锁

    /******************************************/
    //售后订单信息

    private AftersaleOrderDO aftersaleOrderDO;

    AftersaleOrder order;

    /******************************************/


    public BizScene toBizScene() {
        return BizScene.of(cmd.getBizType(), scene);
    }
}