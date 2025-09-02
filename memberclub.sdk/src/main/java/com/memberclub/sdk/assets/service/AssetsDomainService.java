/**
 * @(#)AssetsDomainService.java, 一月 25, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.assets.service;

import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.domain.facade.AssetDO;
import com.memberclub.domain.facade.AssetFetchRequestDO;
import com.memberclub.domain.facade.AssetFetchResponseDO;
import com.memberclub.infrastructure.assets.facade.AssetsFacadeSPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * author: 掘金五阳
 *
 * 负责与下游交互查询会员资产信息。
 */
@Service
public class AssetsDomainService {

    @Autowired
    private AssetsFacadeSPI assetsFacadeSPI;

    @Autowired
    private ExtensionManager extensionManager;

    /**
     * 根据用户信息和权益类型查询其资产详情。
     *
     * @param userId     用户标识
     * @param rightType  权益类型
     * @param itemTokens 资产标识集合
     * @return itemToken -> 资产列表映射
     */
    public Map<String, List<AssetDO>> queryAssets(Long userId, Integer rightType, List<String> itemTokens) {
        // TODO: 2025/1/25 暂时先假设下游均实现了 SPI
        AssetFetchRequestDO request = new AssetFetchRequestDO();
        request.setUserId(userId);

        request.setRightType(rightType);
        request.setItemTokens(itemTokens);
        AssetFetchResponseDO responseDO = assetsFacadeSPI.fetch(request);

        CommonLog.info("调用下游查询资产状态, 结果:{}, 请求:{}", responseDO, request);
        if (!responseDO.isSuccess()) {
            throw ResultCode.DEPENDENCY_ERROR.newException("查询下游资产异常");
        }
        return responseDO.getItemToken2AssetsMap();
    }

}