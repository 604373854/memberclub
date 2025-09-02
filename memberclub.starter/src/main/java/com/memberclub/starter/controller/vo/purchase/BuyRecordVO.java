package com.memberclub.starter.controller.vo.purchase;

import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewResponse;
import lombok.Data;

import java.util.List;

/**
 * 展示用户购买记录及其子订单的视图对象。
 */
@Data
public class BuyRecordVO {

    private String tradeId;

    private Integer bizType;

    private List<BuySubOrderVO> subOrders;

    private String status;

    private AfterSalePreviewResponse previewResponse;
}
