package com.memberclub.starter.controller.convertor;

import com.memberclub.domain.dataobject.sku.SkuInfoDO;
import com.memberclub.sdk.util.PriceUtils;
import com.memberclub.starter.controller.vo.sku.SkuPreviewVO;

/**
 * 管理端使用的转换工具，将领域层的 SKU 对象转换为预览视图对象。
 */
public class ManageConvertor {

    /**
     * 将 {@link SkuInfoDO} 转换为预览视图对象。
     *
     * @param skuInfoDO 领域层 SKU 信息
     * @return 前端展示所需的视图对象
     */
    public static SkuPreviewVO toSkuPreviewVO(SkuInfoDO skuInfoDO) {
        SkuPreviewVO vo = new SkuPreviewVO();
        vo.setId(skuInfoDO.getSkuId());
        vo.setImage(skuInfoDO.getViewInfo().getDisplayImage());
        vo.setOriginPrice(PriceUtils.change2Yuan(skuInfoDO.getSaleInfo().getOriginPriceFen()));
        vo.setPrice(PriceUtils.change2Yuan(skuInfoDO.getSaleInfo().getSalePriceFen()));
        vo.setFirmId(skuInfoDO.getBizType());
        vo.setBizId(skuInfoDO.getBizType());
        vo.setDesc(skuInfoDO.getViewInfo().getDisplayDesc());
        vo.setTitle(skuInfoDO.getViewInfo().getDisplayName());
        return vo;
    }
}
