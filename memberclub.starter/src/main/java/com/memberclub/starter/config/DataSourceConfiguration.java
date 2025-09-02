/**
 * @(#)DataSourceConfiguration.java, 二月 02, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.config;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.provider.AbstractDataSourceProvider;
import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import org.apache.shardingsphere.driver.jdbc.adapter.AbstractDataSourceAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Map;

/**
 * 将 ShardingSphere 管理的数据源与动态数据源库进行整合。
 * 当开启分表功能时，注册一个 provider 同时暴露分库分表与默认数据源以供路由。
 */
@Configuration
@AutoConfigureBefore({DynamicDataSourceAutoConfiguration.class,
        SpringBootConfiguration.class})
@ConditionalOnProperty(name = "spring.shardingsphere.enabled", havingValue = "true", matchIfMissing = false)
public class DataSourceConfiguration {

    /**
     * ShardingSphere 管理的分片数据源名称。
     */
    private static final String SHARDING_DATA_SOURCE_NAME = "sharding";

    /**
     * 通过 MyBatis Plus 配置定义的动态数据源属性。
     */
    @Autowired
    private DynamicDataSourceProperties properties;

    /**
     * ShardingSphere 为分表创建的复合数据源。
     */
    @Lazy
    @Resource(name = "shardingSphereDataSource")
    AbstractDataSourceAdapter shardingSphereDataSource;

    /**
     * 未使用分表时的默认数据源。
     */
    @Lazy
    @Resource(name = "dataSource")
    private DataSource dataSource;

    /**
     * 注册 {@link DynamicDataSourceProvider}，将 MyBatis Plus 定义的数据源
     * 与 ShardingSphere 提供的数据源合并。
     *
     * @return 可加载所有数据源的 provider
     */
    @Primary
    @Bean
    public DynamicDataSourceProvider dynamicDataSourceProvider() {
        // 先从 MyBatis Plus 配置加载数据源定义。
        Map<String, DataSourceProperty> datasourceMap = properties.getDatasource();
        return new AbstractDataSourceProvider() {
            @Override
            public Map<String, DataSource> loadDataSources() {
                Map<String, DataSource> dataSourceMap = createDataSourceMap(datasourceMap);
                // 注册 ShardingSphere 数据源，使其参与路由。
                dataSourceMap.put("tradeDataSource", shardingSphereDataSource);
                dataSourceMap.put("skuDataSource", shardingSphereDataSource);
                return dataSourceMap;
            }
        };
    }

    /**
     * 主动态路由数据源 bean，注入自定义 provider 并设置路由相关属性。
     *
     * @param dynamicDataSourceProvider 提供可用数据源的 provider
     * @return 配置好的 {@link DataSource}
     */
    @Primary
    @Bean
    public DataSource dataSource(DynamicDataSourceProvider dynamicDataSourceProvider) {
        DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource();
        // 设置默认的目标数据源名称。
        dataSource.setPrimary(properties.getPrimary());
        dataSource.setStrict(properties.getStrict());
        dataSource.setStrategy(properties.getStrategy());
        // 如有需要，开启 SQL 日志或分布式事务功能。
        dataSource.setP6spy(properties.getP6spy());
        dataSource.setSeata(properties.getSeata());
        return dataSource;
    }
}