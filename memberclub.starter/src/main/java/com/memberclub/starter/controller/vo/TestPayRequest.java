/**
 * @(#)TestPayRequest.java, 一月 19, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.controller.vo;

import lombok.Data;

/**
 * 管理端用于模拟指定交易支付回调的请求对象。
 */
@Data
public class TestPayRequest {

    private String tradeId;
}