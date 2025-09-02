/**
 * @(#)ForceDBRouteHintShardingAlgorithm.java, 二月 04, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.config.db;

import org.apache.shardingsphere.sharding.api.sharding.hint.HintShardingAlgorithm;
import org.apache.shardingsphere.sharding.api.sharding.hint.HintShardingValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

/**
 * 基于 Hint 的分片算法，根据 {@link HintShardingValue} 提供的值强制路由到指定库。
 * 当调用方需要在运行时明确指定目标库时使用。
 */
public class ForceDBRouteHintShardingAlgorithm implements HintShardingAlgorithm<Integer> {

    /** {@inheritDoc} */
    @Override
    public void init() {

    }

    /** {@inheritDoc} */
    @Override
    public String getType() {
        return "hint";
    }

    /** {@inheritDoc} */
    @Override
    public Properties getProps() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void setProps(Properties props) {

    }

    /**
     * 将路由指向名称以提供的 hint 值结尾的数据库。
     *
     * @param collection       可选数据源名称集合
     * @param hintShardingValue 包含目标数据源后缀的 hint
     * @return 匹配 hint 的数据源集合
     */
    @Override
    public Collection<String> doSharding(Collection<String> collection, HintShardingValue<Integer> hintShardingValue) {
        Collection<String> result = new ArrayList<>();
        // 遍历所有可用数据源，与提供的 hint 进行匹配。
        for (String actualDb : collection) {
            // hintShardingValue 表示调用方显式提供的分片值。
            Collection<Integer> values = hintShardingValue.getValues();
            for (Integer value : values) {
                // 直接路由到名称后缀等于 hint 值的数据源。
                if (actualDb.endsWith(String.valueOf(value))) {
                    result.add(actualDb);
                }
            }
        }
        return result;
    }
}