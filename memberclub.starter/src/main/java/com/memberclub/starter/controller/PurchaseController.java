/**
 * @(#)PurchaseController.java, 一月 04, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.controller;

import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.context.purchase.PurchaseSubmitCmd;
import com.memberclub.domain.context.purchase.PurchaseSubmitResponse;
import com.memberclub.domain.exception.MemberException;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.infrastructure.sku.SkuBizService;
import com.memberclub.sdk.purchase.service.biz.PurchaseBizService;
import com.memberclub.starter.controller.convertor.PurchaseConvertor;
import com.memberclub.starter.controller.vo.PurchaseSubmitVO;
import com.memberclub.starter.controller.vo.purchase.PurchaseSubmitResponseVO;
import com.memberclub.starter.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 处理购买相关操作的控制器，接收前端的提单请求，转换为领域命令并委托
 * 购买服务执行。
 */
@RestController()
@RequestMapping("/memberclub/purchase")
public class PurchaseController {


    @Autowired
    private PurchaseBizService purchaseBizService;


    @Autowired
    private SkuBizService skuBizService;

    /**
     * 为当前用户提交购买订单。
     *
     * @param servletRequest 当前 HTTP 请求，用于填充安全上下文
     * @param param          购买参数
     * @return 包含交易号和错误信息的响应
     */
    @PostMapping("/submit")
    public PurchaseSubmitResponseVO submit(HttpServletRequest servletRequest, @RequestBody PurchaseSubmitVO param) {
        PurchaseSubmitResponseVO response = new PurchaseSubmitResponseVO();
        try {
            // 基础参数校验
            param.isValid();//通用参数校验
            // 设置安全上下文，让购买服务识别用户
            SecurityUtil.securitySet(servletRequest);
            // 将请求参数转换为领域命令
            PurchaseSubmitCmd cmd = PurchaseConvertor.toSubmitCmd(param);
            // 调用购买服务
            PurchaseSubmitResponse resp = purchaseBizService.submit(cmd);
            response.setSucc(resp.isSuccess());
            if (resp.isSuccess()) {
                response.setTradeId(resp.getMemberOrderDO().getTradeId());
            }
            response.setErrorCode(resp.getErrorCode());
            response.setErrorMsg(resp.getMsg());
        } catch (MemberException e) {
            // 已知的业务异常
            CommonLog.error("提单异常 param:{}", param, e);
            response.setSucc(false);
            response.setErrorCode(e.getCode().getCode());
            response.setErrorMsg(e.getCode().getMsg());
        } catch (Throwable e) {
            // 未知异常
            CommonLog.error("提单异常 param:{}", param, e);
            response.setSucc(false);
            response.setErrorCode(ResultCode.INTERNAL_ERROR.getCode());
            response.setErrorMsg(ResultCode.INTERNAL_ERROR.getMsg());
        }
        return response;
    }


}