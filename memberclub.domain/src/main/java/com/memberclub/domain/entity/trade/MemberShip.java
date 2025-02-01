/**
 * @(#)MemberShip.java, 二月 01, 2025.
 * <p>
 * Copyright 2025 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.entity.trade;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * author: 掘金五阳
 */
@Data
public class MemberShip {

    @TableId(type = IdType.AUTO)
    private Long id;

    private long userId;

    private int bizType;

    private String tradeId;

    private String subTradeId;

    private String itemToken;

    private String grantCode;

    private String extra;

    private int rightId;

    private int status;

    private long stime;

    private long etime;

    private long utime;

    private long ctime;
}