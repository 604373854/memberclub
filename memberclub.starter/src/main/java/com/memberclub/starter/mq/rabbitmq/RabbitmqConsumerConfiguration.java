/**
 * @(#)RabbitmqConsumerFacade.java, 一月 14, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.mq.rabbitmq;

import com.memberclub.infrastructure.dynamic_config.SwitchEnum;
import com.memberclub.infrastructure.mq.MQQueueEnum;
import com.memberclub.sdk.purchase.service.biz.PurchaseBizService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

import static com.memberclub.infrastructure.mq.MQContants.*;

/**
 * 应用的 RabbitMQ 消费者配置类，将业务队列绑定到本类方法，
 * 每个方法再调用 {@link AbstractRabbitmqConsumerConfiguration} 提供的辅助方法完成消费与重试。
 *
 * @see com.memberclub.infrastructure.mq.RabbitRegisterConfiguration
 * author: 掘金五阳
 */
@ConditionalOnProperty(name = "memberclub.infrastructure.mq", havingValue = "rabbitmq", matchIfMissing = true)
@Configuration
public class RabbitmqConsumerConfiguration extends AbstractRabbitmqConsumerConfiguration {


    @Autowired
    private PurchaseBizService purchaseBizService;

    /**
     * 处理财务前置的交易事件，使用自定义重试配置控制重新投递。
     */
    @RabbitListener(queues = {TRADE_EVENT_QUEUE_ON_PRE_FINANCE})
    @RabbitHandler
    public void consumeTradeEventPreFinanceQueue(String value, Channel channel, Message message) throws IOException {
        consumeAndFailRetry(value, channel, message,
                SwitchEnum.TRADE_EVENT_FOR_PRE_FINANCE_RETRY_TIMES,
                MQQueueEnum.TRADE_EVENT_FOR_PRE_FINANCE);
    }

    /**
     * 处理支付超时检查，此队列消息仅消费一次，不进行重试。
     */
    @RabbitListener(queues = {TRADE_PAYMENT_TIMEOUT_EVENT_QUEUE})
    @RabbitHandler
    public void consumePayTimeoutCheckQueue(String value, Channel channel, Message message) throws IOException {
        consume(value, channel, message, MQQueueEnum.TRADE_PAYMENT_TIMEOUT_EVENT_QUEUE);
    }

    /**
     * 处理支付成功后的交易事件，失败的消息按照动态配置进行重试。
     */
    @RabbitListener(queues = {TRADE_EVENT_QUEUE_ON_PAY_SUCCESS})
    @RabbitHandler
    public void consumeTradeEvent4PaySuccessQueue(String value, Channel channel, Message message) throws IOException {
        consumeAndFailRetry(value, channel, message,
                SwitchEnum.TRADE_EVENT_4_PAY_SUCCESS_RETRY_TIMES,
                MQQueueEnum.TRADE_EVENT_FOR_PAY_SUCCESS);
    }
}