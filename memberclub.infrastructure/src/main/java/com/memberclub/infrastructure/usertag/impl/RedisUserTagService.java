/**
 * @(#)RedisUserTagService.java, 一月 30, 2025.
 * <p>
 * Copyright 2025 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.infrastructure.usertag.impl;

import com.google.common.collect.Lists;
import com.memberclub.common.retry.Retryable;
import com.memberclub.common.util.CollectionUtilEx;
import com.memberclub.domain.context.usertag.UserTagDO;
import com.memberclub.domain.context.usertag.UserTagOpCmd;
import com.memberclub.domain.context.usertag.UserTagOpDO;
import com.memberclub.domain.context.usertag.UserTagOpResponse;
import com.memberclub.domain.context.usertag.UserTagOpTypeEnum;
import com.memberclub.infrastructure.usertag.UserTagLuaUtil;
import com.memberclub.infrastructure.usertag.UserTagService;
import jodd.io.FileUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * author: 掘金五阳
 */
@ConditionalOnProperty(name = "memberclub.infrastructure.usertag", havingValue = "redis", matchIfMissing = true)
@Service
public class RedisUserTagService implements UserTagService {

    public static final Logger LOG = LoggerFactory.getLogger(RedisUserTagService.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisUserTagService redisUserTagService;


    private static String userTagAddLua = null;

    private static String userTagDelLua = null;

    static {
        try {
            userTagAddLua = FileUtil.readString(RedisUserTagService.class.getClassLoader().getResource("lua/user_tag_add.lua").getFile());
            userTagDelLua = FileUtil.readString(RedisUserTagService.class.getClassLoader().getResource("lua/user_tag_del.lua").getFile());

            LOG.info("提前加载 Lua 脚本 user_tag_add.lua:{}", userTagAddLua);
            LOG.info("提前加载 Lua 脚本 user_tag_del.lua:{}", userTagDelLua);
        } catch (Exception e) {
            LOG.error("加载 lua 脚本异常", e);
        }
    }

    @Override
    public UserTagOpResponse operate(UserTagOpCmd cmd) {
        UserTagOpResponse response = new UserTagOpResponse();
        if (cmd.getOpType() == UserTagOpTypeEnum.ADD) {
            redisUserTagService.add(cmd, response);
        } else if (cmd.getOpType() == UserTagOpTypeEnum.DEL) {
            redisUserTagService.del(cmd, response);
        } else if (cmd.getOpType() == UserTagOpTypeEnum.GET) {
            get(cmd, response);
        }
        return response;
    }

    @Retryable(throwException = true)
    public void add(UserTagOpCmd cmd, UserTagOpResponse response) {
        RedisScript<Long> script = new DefaultRedisScript<>(userTagAddLua, Long.class);
        Long value = redisTemplate.execute(script, UserTagLuaUtil.buildLuaKeys(cmd));
        if (value == 1) {
            response.setSuccess(true);
        } else if (value == -1) {
            response.setSuccess(true);//幂等
        }
    }

    @Retryable(throwException = true)
    public void del(UserTagOpCmd cmd, UserTagOpResponse response) {
        RedisScript<Long> script = new DefaultRedisScript<>(userTagDelLua, Long.class);
        Long value = redisTemplate.execute(script, UserTagLuaUtil.buildLuaKeys(cmd));
        if (value == 1) {
            response.setSuccess(true);
        } else if (value == -1) {
            response.setSuccess(true);//幂等
        }
    }

    private void get(UserTagOpCmd cmd, UserTagOpResponse response) {
        List<String> keys = CollectionUtilEx.mapToList(cmd.getTags(), UserTagOpDO::getKey);
        List<Object> values = redisTemplate.opsForValue().multiGet(keys);
        List<UserTagDO> userTags = Lists.newArrayList();

        for (int i = 0; i < keys.size(); i++) {
            UserTagDO userTag = new UserTagDO();
            userTag.setKey(keys.get(i));
            Long count = 0L;
            if (CollectionUtils.isNotEmpty(values) && values.size() > i) {
                Object v = values.get(i);
                count = v == null ? 0 : Long.valueOf(v.toString());
            }
            userTag.setCount(count);
            userTags.add(userTag);
        }
        response.setSuccess(true);
        response.setTags(userTags);
    }
}