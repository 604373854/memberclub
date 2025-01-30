/**
 * @(#)SkuDomainService.java, 一月 04, 2025.
 * <p>
 * Copyright 2025 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.sku.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.memberclub.domain.dataobject.sku.SkuInfoDO;
import com.memberclub.domain.entity.sku.MemberSku;
import com.memberclub.infrastructure.mybatis.mappers.sku.MemberSkuDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * author: 掘金五阳
 */
@DS("skuDataSource")
@Service
public class SkuDomainService {

    @Autowired
    private MemberSkuDao memberSkuDao;

    public static final Logger LOG = LoggerFactory.getLogger(SkuDomainService.class);

    @Autowired
    private MemberSkuDataObjectFactory memberSkuDataObjectFactory;


    @Transactional(rollbackFor = Exception.class)
    public void createMemberSku(SkuInfoDO sku) {
        MemberSku memberSku = memberSkuDataObjectFactory.toSku(sku);
        int cnt = memberSkuDao.insertIgnoreBatch(Lists.newArrayList(memberSku));
    }


    public SkuInfoDO queryById(Long id) {
        LambdaQueryWrapper<MemberSku> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberSku::getId, id);
        MemberSku memberSku = memberSkuDao.selectOne(wrapper);
        return memberSkuDataObjectFactory.toSkuInfoDO(memberSku);
    }

    public List<SkuInfoDO> queryAllSkus() {
        LambdaQueryWrapper<MemberSku> wrapper = new LambdaQueryWrapper<>();
        List<MemberSku> skus = memberSkuDao.selectList(wrapper);
        return skus.stream().map(memberSkuDataObjectFactory::toSkuInfoDO).collect(Collectors.toList());
    }
}