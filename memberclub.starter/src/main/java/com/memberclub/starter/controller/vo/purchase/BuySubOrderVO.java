package com.memberclub.starter.controller.vo.purchase;

import lombok.Data;

/**
 * 描述购买记录中子订单的视图对象。
 */
@Data
public class BuySubOrderVO {
    private String title;

    private String subTradeId;

    private String effectiveTime;

    private String buyTime;

    private String payPrice;

    private Integer buyCount;

    private String image;

    private String status;
}
