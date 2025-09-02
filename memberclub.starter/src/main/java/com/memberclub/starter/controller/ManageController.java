/**
 * @(#)ManageController.java, 一月 19, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.controller;

import com.google.common.collect.Lists;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.util.CollectionUtilEx;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.oncetask.trigger.OnceTaskTriggerCmd;
import com.memberclub.domain.context.perform.common.ShipTypeEnum;
import com.memberclub.domain.context.purchase.PurchaseSkuSubmitCmd;
import com.memberclub.domain.context.purchase.PurchaseSubmitCmd;
import com.memberclub.domain.context.purchase.PurchaseSubmitResponse;
import com.memberclub.domain.context.purchase.cancel.PurchaseCancelCmd;
import com.memberclub.domain.context.purchase.common.SubmitSourceEnum;
import com.memberclub.domain.dataobject.CommonUserInfo;
import com.memberclub.domain.dataobject.aftersale.ClientInfo;
import com.memberclub.domain.dataobject.membership.MemberShipUnionDO;
import com.memberclub.domain.dataobject.order.LocationInfo;
import com.memberclub.domain.dataobject.payment.PayAcccountTypeEnum;
import com.memberclub.domain.dataobject.payment.PayChannelTypeEnum;
import com.memberclub.domain.dataobject.payment.context.PaymentNotifyCmd;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.domain.dataobject.sku.SkuInfoDO;
import com.memberclub.sdk.aftersale.service.AfterSaleBizService;
import com.memberclub.sdk.memberorder.domain.MemberOrderDomainService;
import com.memberclub.sdk.membership.service.MemberShipCacheService;
import com.memberclub.sdk.payment.service.PaymentService;
import com.memberclub.sdk.perform.service.PerformBizService;
import com.memberclub.sdk.purchase.service.biz.PurchaseBizService;
import com.memberclub.sdk.sku.service.SkuDomainService;
import com.memberclub.starter.controller.convertor.ManageConvertor;
import com.memberclub.starter.controller.convertor.PurchaseConvertor;
import com.memberclub.starter.controller.vo.PurchaseSubmitVO;
import com.memberclub.starter.controller.vo.TestPayRequest;
import com.memberclub.starter.controller.vo.base.DataResponse;
import com.memberclub.starter.controller.vo.sku.SkuPreviewVO;
import com.memberclub.starter.controller.vo.test.PurchaseSubmitRequest;
import com.memberclub.starter.job.OnceTaskTriggerBizService;
import com.memberclub.starter.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 管理端控制器，仅在测试或单机环境下使用，提供查询商品、提交测试订单、
 * 触发异步任务以及查询会员信息等辅助接口，**严禁**在生产环境暴露。
 */
@Profile({"ut", "standalone", "test"})
@RestController()
@RequestMapping("/manage")
@Api(value = "管理接口", tags = {"管理接口"})
public class ManageController {

    public static final Logger LOG = LoggerFactory.getLogger(ManageController.class);

    @Autowired
    private PurchaseBizService purchaseBizService;


    @Autowired
    private SkuDomainService skuDomainService;
    @Autowired
    private MemberOrderDomainService memberOrderDomainService;
    @Autowired
    private PerformBizService performBizService;
    @Autowired
    private OnceTaskTriggerBizService onceTaskTriggerBizService;
    @Autowired
    private AfterSaleBizService aftersaleBizService;
    @Autowired
    private MemberShipCacheService memberShipCacheService;
    @Autowired
    private PaymentService paymentService;

    /**
     * 查询所有 SKU 信息。
     *
     * @return SKU 数据对象列表
     */
    @ApiOperation("查询商品列表")
    @PostMapping("/sku/list")
    @ResponseBody
    public List<SkuInfoDO> listSkus() {
        return skuDomainService.queryAllSkus();
    }

    /**
     * 构建按商品类别分组的 SKU 预览列表。
     *
     * @return SKU 预览视图对象列表
     */
    @ApiOperation("查询商品列表")
    @PostMapping("/sku/preview/list")
    @ResponseBody
    public List<SkuPreviewVO> getSkus() {
        List<SkuPreviewVO> skuPreviewVOS = Lists.newArrayList();
        skuPreviewVOS.addAll(buildDisplaySkusForMallMember());
        skuPreviewVOS.addAll(buildDisplaySkusForDouyinCouponPackage());
        skuPreviewVOS.addAll(buildDisplaySkusForLesson());
        return skuPreviewVOS;
    }

    /**
     * 组装商城会员 SKU 的预览信息。
     *
     * @return 商城会员产品的预览数据
     */
    public List<SkuPreviewVO> buildDisplaySkusForMallMember() {
        List<Long> skuIds = Lists.newArrayList();
        skuIds.add(200401L);//京西会员季卡
        skuIds.add(200402L);//京西会员季卡
        List<SkuInfoDO> skus = skuDomainService.queryByIds(skuIds);
        List<SkuPreviewVO> previewVOs = CollectionUtilEx.mapToList(skus, ManageConvertor::toSkuPreviewVO);
        for (SkuPreviewVO previewVO : previewVOs) {
            previewVO.setFirmName("电子商务");
            previewVO.setAttr_val("电商会员");
            previewVO.setSingleBuy(true);
            previewVO.setStock(0L);
        }
        return previewVOs;
    }

    /**
     * 组装抖音券包 SKU 的预览信息。
     *
     * @return 券包产品的预览数据
     */
    public List<SkuPreviewVO> buildDisplaySkusForDouyinCouponPackage() {
        List<Long> skuIds = Lists.newArrayList();
        skuIds.add(200403L);//抖音单权益券包
        skuIds.add(200404L);//抖音双权益券包
        List<SkuInfoDO> skus = skuDomainService.queryByIds(skuIds);
        List<SkuPreviewVO> previewVOs = CollectionUtilEx.mapToList(skus, ManageConvertor::toSkuPreviewVO);
        for (SkuPreviewVO previewVO : previewVOs) {
            previewVO.setFirmName("优惠券包");
            previewVO.setAttr_val("券包");
            previewVO.setSingleBuy(false);
            previewVO.setStock(2L);
        }
        return previewVOs;
    }

    /**
     * 组装课程类 SKU 的预览信息。
     *
     * @return 课程产品的预览数据
     */
    public List<SkuPreviewVO> buildDisplaySkusForLesson() {
        List<Long> skuIds = Lists.newArrayList();
        skuIds.add(200405L);//数学课 附赠购课券
        skuIds.add(200406L);//语文课 无赠券
        List<SkuInfoDO> skus = skuDomainService.queryByIds(skuIds);
        List<SkuPreviewVO> previewVOs = CollectionUtilEx.mapToList(skus, ManageConvertor::toSkuPreviewVO);
        for (SkuPreviewVO previewVO : previewVOs) {
            previewVO.setFirmName("在线教育");
            previewVO.setAttr_val("课程");
            previewVO.setSingleBuy(false);
            previewVO.setStock(1000L);
        }
        return previewVOs;
    }

    /**
     * 提交测试用的购票订单，会模拟用户及客户端信息，禁止在生产环境使用。
     *
     * @param servletRequest 当前 HTTP 请求
     * @param request        购票参数，如 skuId 和数量
     * @return 购票服务返回的提交结果
     */
    @PostMapping("/purchase/submit")
    public PurchaseSubmitResponse submit(HttpServletRequest servletRequest, @RequestBody PurchaseSubmitRequest request) {
        try {
            PurchaseSubmitVO param = new PurchaseSubmitVO();
            ClientInfo clientInfo = new ClientInfo();
            clientInfo.setClientCode(1);
            clientInfo.setClientName("member-ios");
            param.setClientInfo(clientInfo);
            CommonUserInfo userInfo = new CommonUserInfo();
            userInfo.setIp("127.0.0.1");
            // 查询 SKU 信息以确定业务类型
            SkuInfoDO skuInfo = skuDomainService.queryById(request.getSkuId());

            param.setBizId(skuInfo.getBizType());
            param.setUserInfo(userInfo);
            param.setSubmitSource(SubmitSourceEnum.HOMEPAGE.getCode());
            // 将 VO 转换为领域服务的命令对象
            PurchaseSubmitCmd cmd = PurchaseConvertor.toSubmitCmd(param);

            PurchaseSkuSubmitCmd skuCmd = new PurchaseSkuSubmitCmd();
            if (request.getBuyCount() > 10) {
                // 简单校验，避免测试时数量过大
                throw new RuntimeException("数量太多");
            }
            skuCmd.setBuyCount(request.getBuyCount());
            skuCmd.setSkuId(request.getSkuId());
            cmd.setSkus(Lists.newArrayList(skuCmd));
            // 随机生成 userId 与提交 token 供测试使用
            cmd.setUserId(RandomUtils.nextLong(1, 1000000));
            cmd.setSubmitToken(RandomStringUtils.randomAscii(10));

            LocationInfo locationInfo = new LocationInfo();
            locationInfo.setActualSecondCityId("110100");
            cmd.setLocationInfo(locationInfo);

            // 调用购票服务提交订单
            PurchaseSubmitResponse response = purchaseBizService.submit(cmd);
            return response;
        } catch (Exception e) {
            LOG.info("提单失败:{}", request, e);
            PurchaseSubmitResponse response = new PurchaseSubmitResponse();
            response.setMsg(e.getMessage());
            response.setSuccess(false);
            return response;
        }
    }

    /**
     * 为测试订单发送支付成功通知。
     *
     * @param servletRequest 当前 HTTP 请求，用于填充安全上下文
     * @param request        待支付订单的交易信息
     * @return 支付通知是否成功
     */
    @ResponseBody
    @PostMapping("/purchase/pay")
    public boolean pay(HttpServletRequest servletRequest, @RequestBody TestPayRequest request) {
        PaymentNotifyCmd cmd = new PaymentNotifyCmd();

        try {
            // 设置安全上下文获取用户信息
            SecurityUtil.securitySet(servletRequest);
            cmd.setUserId(SecurityUtil.getUserId());
            cmd.setTradeId(request.getTradeId());
            // 查询订单以补充支付相关信息
            MemberOrderDO order = memberOrderDomainService.getMemberOrderDO(cmd.getUserId(), cmd.getTradeId());
            if (order == null) {
                throw new RuntimeException("输入错误订单 id");
            }
            cmd.setPayAccount("alipay_" + RandomStringUtils.randomNumeric(10));
            cmd.setPayTime(TimeUtil.now());
            cmd.setPayTradeNo(order.getPaymentInfo().getPayTradeNo());
            cmd.setPayChannelType(PayChannelTypeEnum.ALIPAY.getName());
            cmd.setPayAccountType(PayAcccountTypeEnum.THIRD_PARTY.getName());
            cmd.setPayTime(TimeUtil.now());
            cmd.setPayAmountFen(order.getActPriceFen());

            // 调用支付服务模拟支付回调
            paymentService.paymentNotify(cmd);
            return true;
        } catch (Exception e) {
            CommonLog.warn("管理接口调用支付异常", e);
            return false;
        } finally {
            // 请求完成后清理安全上下文
            SecurityUtil.clear();
        }
    }

    /**
     * 取消测试场景下已提交的订单。
     *
     * @param servletRequest 当前 HTTP 请求，用于填充安全上下文
     * @param request        需要取消的订单信息
     * @return 取消是否成功的响应
     */
    @ResponseBody
    @PostMapping("/purchase/cancel")
    public DataResponse<Boolean> cancel(HttpServletRequest servletRequest, @RequestBody TestPayRequest request) {
        PurchaseCancelCmd cmd = new PurchaseCancelCmd();
        DataResponse response = new DataResponse();
        try {
            // 设置安全上下文获取用户信息
            SecurityUtil.securitySet(servletRequest);
            cmd.setUserId(SecurityUtil.getUserId());
            cmd.setTradeId(request.getTradeId());
            // 查询订单以确定其存在并获取业务类型
            MemberOrderDO order = memberOrderDomainService.getMemberOrderDO(cmd.getUserId(), cmd.getTradeId());
            if (order == null) {
                throw new RuntimeException("输入错误订单 id");
            }
            cmd.setBizType(order.getBizType());

            // 调用购票服务执行取消
            purchaseBizService.cancel(cmd);
            response.setSucc(true);
        } catch (Exception e) {
            response.setSucc(false);
            response.setErrorMsg(e.getMessage());
        } finally {
            // 始终清理安全上下文
            SecurityUtil.clear();
        }
        return response;
    }

    /**
     * 提交订单并立即模拟支付成功通知。
     *
     * @param servletRequest 当前 HTTP 请求
     * @param request        购票参数，如 skuId 和数量
     * @return 若提交及支付均成功返回 true
     */
    @PostMapping("/purchase/submitAndPay")
    public boolean submitAndPay(HttpServletRequest servletRequest, @RequestBody PurchaseSubmitRequest request) {
        PurchaseSubmitResponse response = submit(servletRequest, request);
        if (response.isSuccess()) {
            TestPayRequest payRequest = new TestPayRequest();
            payRequest.setTradeId(response.getMemberOrderDO().getTradeId());
            return pay(servletRequest, payRequest);
        }
        throw new RuntimeException("提单失败");
    }

    /**
     * 触发一次性任务，例如定时履约。
     *
     * @param cmd 任务触发命令
     */
    @PostMapping("/task/trigger")
    public void periodPerform(@RequestBody OnceTaskTriggerCmd cmd) {
        onceTaskTriggerBizService.triggerPeriodPerform(cmd);
    }

    /**
     * 触发售后退款任务。
     *
     * @param cmd 任务触发命令
     */
    @PostMapping("/task/expire_refund/trigger")
    public void expireRefund(@RequestBody OnceTaskTriggerCmd cmd) {
        aftersaleBizService.triggerRefund(cmd);
    }

    /**
     * 查询指定用户的会员信息。
     *
     * @param bizType      业务类型编码
     * @param shipTypeEnum 会员类型编码
     * @param userId       待查询的用户 ID
     * @return 包含会员数据的 {@link DataResponse}
     */
    @PostMapping("/membership/query")
    public DataResponse<MemberShipUnionDO> queryMemberShip(@RequestParam int bizType, @RequestParam int shipTypeEnum,
                                                           @ApiParam(defaultValue = "12345678") @RequestParam long userId) {
        MemberShipUnionDO unionDO = memberShipCacheService.get(BizTypeEnum.findByCode(bizType),
                ShipTypeEnum.findByCode(shipTypeEnum), userId, TimeUtil.now());

        return DataResponse.success(unionDO);
    }

}