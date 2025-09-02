package com.memberclub.starter.controller.vo.aftersale;

import lombok.Data;

/**
 * 提交售后申请的请求体。
 */
@Data
public class AftersaleSubmitVO {

    private Integer bizType;

    private String tradeId;

    private int source;

    private String previewToken;
}
