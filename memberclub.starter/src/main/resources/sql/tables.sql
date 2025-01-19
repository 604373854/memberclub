-- 用于单测使用的 H2 数据库初始化

SET MODE MYSQL;

USE member_db;

CREATE TABLE IF NOT EXISTS member_order (
    id BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '表自增主键',
    biz_type INT(11)  NOT NULL COMMENT '产品线',
    user_id BIGINT(20)  NOT NULL COMMENT 'userId',
    order_system_type INT(11)  NULL COMMENT '订单系统类型',
    order_id VARCHAR(128)  NULL COMMENT '订单  id',
    trade_id VARCHAR(128)  NOT NULL COMMENT '交易 id',
    renew_type INT(11)  NOT NULL COMMENT '续费类型 0 无续费,1 用户续费 2 系统自动续费',
    act_price_fen INT(11)  NULL COMMENT '实付金额',
    origin_price_fen INT(11)  NOT NULL COMMENT '原价金额',
    sale_price_fen INT(11)  NOT NULL COMMENT '原价金额',
    status INT(11)  NOT NULL COMMENT '主状态',
    perform_status INT(11)  NOT NULL COMMENT '履约状态',
    extra TEXT NOT NULL COMMENT '扩展属性',
    stime BIGINT(20)  NULL DEFAULT '0' COMMENT '开始时间',
    etime BIGINT(20)  NULL DEFAULT '0' COMMENT '截止时间',
    utime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '更新时间',
    ctime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uniq_member_order (user_id, trade_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

CREATE TABLE IF NOT EXISTS member_sub_order (
    id BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '表自增主键',
    biz_type INT(11)  NOT NULL COMMENT '产品线',
    user_id BIGINT(20)  NOT NULL COMMENT 'userId',
    order_system_type INT(11)  NULL COMMENT '订单系统类型',
    order_id VARCHAR(128)  NULL COMMENT '订单  id',
    trade_id VARCHAR(128)  NOT NULL COMMENT '交易 id',
    sub_trade_id BIGINT(20)  NOT NULL COMMENT '子单交易 id',
    sku_id BIGINT(20)  NOT NULL COMMENT 'skuId',
    act_price_fen INT(11)  NULL COMMENT '实付金额',
    origin_price_fen INT(11)  NULL COMMENT '原价金额',
    sale_price_fen INT(11)  NOT NULL COMMENT '原价金额',
    buy_count INT(11)  NOT NULL COMMENT '购买数量',
    status INT(11)  NOT NULL COMMENT '主状态',
    perform_status INT(11)  NOT NULL COMMENT '履约状态',
    extra TEXT NOT NULL COMMENT '扩展属性',
    stime BIGINT(20)   NULL DEFAULT '0' COMMENT '开始时间',
    etime BIGINT(20)   NULL DEFAULT '0' COMMENT '截止时间',
    utime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '更新时间',
    ctime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uniq_sub_order (user_id, trade_id, sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;


CREATE TABLE IF NOT EXISTS once_task (
    id BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '表自增主键',
    biz_type INT(11)  NOT NULL COMMENT '产品线',
    task_group_id VARCHAR(128)  NOT NULL COMMENT '任务群组 id,由业务自定义',
    task_token VARCHAR(128)  NOT NULL COMMENT 'taskToken',
    user_id BIGINT(20)  NOT NULL COMMENT 'userId',
    task_type INT(11)  NOT NULL COMMENT '任务类型',
    status INT(11)  NOT NULL COMMENT '状态',
    task_content_class_name VARCHAR(256)  NOT NULL COMMENT '类型名称',
    content TEXT NOT NULL COMMENT '扩展属性',
    stime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '开始时间',
    etime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '截止时间',
    utime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '更新时间',
    ctime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uniq_once_task (user_id, task_token, task_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;





CREATE TABLE IF NOT EXISTS member_perform_item (
    id BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '表自增主键',
    biz_type INT(11)  NOT NULL COMMENT '产品线',
    user_id BIGINT(20)  NOT NULL COMMENT 'userId',
    trade_id VARCHAR(128)  NOT NULL COMMENT '交易 id',
    sku_id BIGINT(20)  NOT NULL COMMENT 'skuId',
    right_id INT(11)  NOT NULL COMMENT '权益Id',
    right_type INT(11)  NOT NULL COMMENT '权益类型',
    asset_count INT(11)  NOT NULL COMMENT '资产数量',
    grant_type INT(11)  NOT NULL COMMENT '发放类型,直发 ,激活',
    item_token VARCHAR(128)  NOT NULL COMMENT '履约项凭证',
    batch_code VARCHAR(128)  NULL COMMENT '发放批次码',
    provider_id INT(11)  NOT NULL COMMENT '履约方 id',
    phase INT(11)  NOT NULL COMMENT '期数',
    cycle INT(11)  NOT NULL COMMENT '周期数',
    buy_index INT(11)  NOT NULL COMMENT '购买序号',
    status INT(11)  NOT NULL COMMENT '状态',
    extra TEXT NOT NULL COMMENT '扩展属性',
    stime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '开始时间',
    etime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '截止时间',
    utime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '更新时间',
    ctime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY key_perform_item_batch (user_id, trade_id, batch_code),
    UNIQUE KEY uniq_perform_item (user_id, item_token, biz_type)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 ;




CREATE TABLE IF NOT EXISTS aftersale_order (
    id BIGINT(20)  NOT NULL COMMENT '表主键',
    biz_type INT(11)  NOT NULL COMMENT '产品线',
    user_id BIGINT(20)  NOT NULL COMMENT 'userId',
    source INT(11)  NOT NULL COMMENT '售后来源',
    trade_id VARCHAR(128)  NOT NULL COMMENT '会员单 id',
    operator VARCHAR(128)  NOT NULL COMMENT '操作人',
    act_pay_price_fen INT(11)  NOT NULL COMMENT '实付金额分',
    act_refund_price_fen INT(11)  NOT NULL COMMENT '实际退款金额分',
    recommend_refund_price_fen INT(11)  NOT NULL COMMENT '推荐的退款金额分',
    status INT(11)  NOT NULL COMMENT '售后状态',
    refund_type INT(11)  NOT NULL COMMENT '退款类型 全部退或部分退',
    refund_way INT(11)  NOT NULL COMMENT '退款渠道',
    extra TEXT NOT NULL COMMENT '扩展属性',
    utime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '更新时间',
    ctime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '创建时间',
    PRIMARY KEY (id),
    --UNIQUE KEY uniq_aftersale_order (user_id, preview_token),
    KEY tradeid_key (user_id, trade_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;


USE member_sku;

CREATE TABLE IF NOT EXISTS member_sku (
    id BIGINT(20)  NOT NULL COMMENT '表主键',
    biz_type INT(11)  NOT NULL COMMENT '产品线',
    status INT(11)  NOT NULL COMMENT '商品状态',
    sale_info TEXT NOT NULL COMMENT '扩展属性',
    finance_info TEXT NOT NULL COMMENT '扩展属性',
    view_info TEXT NOT NULL COMMENT '扩展属性',
    performance_info TEXT NOT NULL COMMENT '扩展属性',
    restrict_info TEXT NOT NULL COMMENT '扩展属性',
    inventory_info TEXT NOT NULL COMMENT '扩展属性',
    extra TEXT NOT NULL COMMENT '扩展属性',
    utime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '更新时间',
    ctime BIGINT(20)  NOT NULL DEFAULT '0' COMMENT '创建时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;