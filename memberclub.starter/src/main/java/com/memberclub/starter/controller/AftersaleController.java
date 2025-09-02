package com.memberclub.starter.controller;

import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.aftersale.apply.AftersaleApplyCmd;
import com.memberclub.domain.context.aftersale.apply.AftersaleApplyResponse;
import com.memberclub.domain.context.aftersale.contant.AftersaleSourceEnum;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewCmd;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewResponse;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.sdk.aftersale.service.AfterSaleBizService;
import com.memberclub.sdk.memberorder.biz.MemberOrderAftersalePreviewDO;
import com.memberclub.sdk.memberorder.biz.MemberOrderBizService;
import com.memberclub.starter.controller.convertor.PurchaseConvertor;
import com.memberclub.starter.controller.vo.aftersale.AftersalePreviewVO;
import com.memberclub.starter.controller.vo.aftersale.AftersaleSubmitVO;
import com.memberclub.starter.controller.vo.base.DataResponse;
import com.memberclub.starter.controller.vo.purchase.BuyRecordVO;
import com.memberclub.starter.controller.vo.purchase.BuyRecordsQueryVO;
import com.memberclub.starter.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 售后相关接口控制器，负责查询用户购买记录、预览售后信息以及提交售后申请，
 * 内部委托领域服务完成具体业务逻辑。
 */
@RestController
@RequestMapping("/memberclub/aftersale")
public class AftersaleController {
    @Autowired
    private MemberOrderBizService memberOrderBizService;

    @Autowired
    private AfterSaleBizService aftersaleBizService;

    /**
     * 查询当前用户已支付且可售后的订单列表。
     *
     * @param servletRequest 当前 HTTP 请求，用于填充安全上下文
     * @param queryVO        查询参数（当前无实际使用）
     * @return 包含用户购买记录的数据响应
     */
    @PostMapping("/records")
    public DataResponse<List<BuyRecordVO>> queryBuyRecords(HttpServletRequest servletRequest, @RequestBody BuyRecordsQueryVO queryVO) {
        DataResponse<List<BuyRecordVO>> response = new DataResponse<>();
        try {
            // 设置安全上下文，方便下游服务获取用户信息
            SecurityUtil.securitySet(servletRequest);
            // 通过会员订单服务查询当前用户已支付的订单
            List<MemberOrderAftersalePreviewDO> previews = memberOrderBizService
                    .queryPayedOrders(SecurityUtil.getUserId(), AftersaleSourceEnum.User);

            response.setSucc(true);
            // 将领域对象转换为视图对象返回
            response.setData(PurchaseConvertor.toBuyRecordVOS(previews));
        } catch (Exception e) {
            // 出现异常时记录日志并标记为失败
            CommonLog.warn("异常 ", e);
            response.setSucc(false);
        } finally {
            // 清理安全上下文，避免泄露鉴权信息
            SecurityUtil.clear();
        }
        return response;
    }

    /**
     * 预览指定订单的售后结果。
     *
     * @param servletRequest 当前 HTTP 请求，用于填充安全上下文
     * @param vo             包含预览参数的请求体
     * @return 来自售后服务的预览结果
     */
    @RequestMapping("/preview")
    public DataResponse<AfterSalePreviewResponse> preview(HttpServletRequest servletRequest, @RequestBody AftersalePreviewVO vo) {
        DataResponse<AfterSalePreviewResponse> response = new DataResponse<>();

        try {
            // 设置安全上下文以确认预览的用户
            SecurityUtil.securitySet(servletRequest);
            // 构造领域服务所需的命令对象
            AfterSalePreviewCmd cmd = new AfterSalePreviewCmd();
            cmd.setSource(AftersaleSourceEnum.findByCode(vo.getSource()));
            cmd.setBizType(BizTypeEnum.findByCode(vo.getBizType()));
            cmd.setUserId(SecurityUtil.getUserId());
            cmd.setOperator(String.valueOf(SecurityUtil.getUserId()));
            cmd.setTradeId(vo.getTradeId());

            // 调用售后服务执行预览
            AfterSalePreviewResponse previewResponse = aftersaleBizService.preview(cmd);
            response.setSucc(previewResponse.isSuccess());
            response.setErrorCode(previewResponse.getUnableCode());
            response.setErrorMsg(previewResponse.getUnableTip());
            response.setData(previewResponse);
        } catch (Exception e) {
            // 发生异常时填充通用错误信息
            response.setSucc(false);
            response.setErrorCode(ResultCode.INTERNAL_ERROR.getCode());
            response.setErrorMsg(ResultCode.PERFORM_ITEM_GRANT_ERROR.getMsg());
        } finally {
            // 处理完成后清理安全上下文
            SecurityUtil.clear();
        }
        return response;
    }

    /**
     * 为指定订单提交售后申请。
     *
     * @param servletRequest 当前 HTTP 请求，用于填充安全上下文
     * @param vo             包含申请信息的请求体
     * @return 售后申请处理结果
     */
    @RequestMapping("/apply")
    public DataResponse<AftersaleApplyResponse> submit(HttpServletRequest servletRequest, @RequestBody AftersaleSubmitVO vo) {
        DataResponse response = new DataResponse();

        try {
            // 初始化安全上下文以识别操作人
            SecurityUtil.securitySet(servletRequest);
            // 构造带有用户及订单信息的命令
            AftersaleApplyCmd cmd = new AftersaleApplyCmd();
            cmd.setSource(AftersaleSourceEnum.findByCode(vo.getSource()));
            cmd.setBizType(BizTypeEnum.findByCode(vo.getBizType()));
            cmd.setUserId(SecurityUtil.getUserId());
            cmd.setOperator(String.valueOf(SecurityUtil.getUserId()));
            cmd.setTradeId(vo.getTradeId());
            cmd.setPreviewToken(vo.getPreviewToken());

            // 调用领域服务提交申请
            AftersaleApplyResponse applyResponse = aftersaleBizService.apply(cmd);
            response.setSucc(applyResponse.isSuccess());
            response.setErrorCode(applyResponse.getUnableCode());
            response.setErrorMsg(applyResponse.getUnableTip());
            response.setData(applyResponse);
        } catch (Exception e) {
            // 发生异常时填充通用错误信息
            response.setSucc(false);
            response.setErrorCode(ResultCode.INTERNAL_ERROR.getCode());
            response.setErrorMsg(ResultCode.PERFORM_ITEM_GRANT_ERROR.getMsg());
        } finally {
            // 处理完成后清理安全上下文
            SecurityUtil.clear();
        }
        return response;
    }
}
