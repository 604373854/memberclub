package com.memberclub.starter.controller.vo.aftersale;

import lombok.Data;

/**
 * 用于预览售后操作的请求参数。
 */
@Data
public class AftersalePreviewVO {

    private int bizType;

    private String tradeId;

    private int source;
}
