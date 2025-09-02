package com.memberclub.starter.controller.vo.purchase;

import com.memberclub.starter.controller.vo.base.BaseResponse;
import lombok.Data;

/**
 * 提交购买订单后返回的响应。
 */
@Data
public class PurchaseSubmitResponseVO extends BaseResponse {

    private String tradeId;
}
