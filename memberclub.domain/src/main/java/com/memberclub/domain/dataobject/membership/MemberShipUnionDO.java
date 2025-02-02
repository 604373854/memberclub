/**
 * @(#)MemberShipUnionDO.java, 二月 02, 2025.
 * <p>
 * Copyright 2025 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.dataobject.membership;

import lombok.Data;

import java.util.List;

/**
 * author: 掘金五阳
 */
@Data
public class MemberShipUnionDO {

    private Integer bizType;

    private long userId;

    private long stime;

    private long etime;

    private List<MemberShipItemDO> items;
}