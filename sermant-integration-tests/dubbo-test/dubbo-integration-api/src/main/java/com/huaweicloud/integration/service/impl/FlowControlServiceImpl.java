/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huaweicloud.integration.service.impl;

import com.huaweicloud.integration.service.FlowControlService;
import com.huaweicloud.integration.utils.ReflectUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 流控测试
 *
 * @author zhouss
 * @since 2022-09-15
 */
public class FlowControlServiceImpl implements FlowControlService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlowControlServiceImpl.class);

    private static final String APACHE_EX = "org.apache.dubbo.rpc.RpcException";

    private static final String ALIBABA_EX = "com.alibaba.dubbo.rpc.RpcException";

    private static final String OK = "ok";

    private static final long FAULT_DELAY_MS = 500L;

    private static final long SLEEP_MS = 100L;

    private static final int MAX_RETRY_TIMES = 3;

    private final Map<String, Integer> counterMap = new ConcurrentHashMap<>();

    @Override
    public String rateLimiting() {
        return OK;
    }

    @Override
    public String rateLimitingWithApplication() {
        return rateLimiting();
    }

    @Override
    public String rateLimitingWithHeader(Map<String, String> attachments) {
        LOGGER.info("accepted attachments {}", attachments);
        return rateLimiting();
    }

    @Override
    public String cirSlowInvoker() {
        try {
            Thread.sleep(SLEEP_MS);
        } catch (InterruptedException ignored) {
            // ignored
        }
        return OK;
    }

    @Override
    public String cirEx() {
        throw new IllegalStateException("cir test");
    }

    @Override
    public String instanceSlowInvoker() {
        return cirSlowInvoker();
    }

    @Override
    public String instanceEx() {
        return cirEx();
    }

    @Override
    public String faultNull() {
        return OK;
    }

    @Override
    public String faultThrowEx() {
        return OK;
    }

    @Override
    public String faultDelay() {
        try {
            Thread.sleep(FAULT_DELAY_MS);
        } catch (InterruptedException ignored) {
            // ignored
        }
        return OK;
    }

    @Override
    public String bulkhead() {
        return cirSlowInvoker();
    }

    @Override
    public String retry(String invocationId) {
        counterMap.putIfAbsent(invocationId, 0);
        counterMap.put(invocationId, counterMap.get(invocationId) + 1);

        int retry = counterMap.get(invocationId);

        if (retry >= MAX_RETRY_TIMES) {
            return String.valueOf(retry);
        }
        throw createRpcException();
    }

    @Override
    public void lb() {
    }

    private boolean isAlibaba() {
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            contextClassLoader.loadClass(ALIBABA_EX);
        } catch (ClassNotFoundException exception) {
            return false;
        }
        return true;
    }

    private RuntimeException createRpcException() {
        if (isAlibaba()) {
            return createRpcException(ALIBABA_EX);
        } else {
            return createRpcException(APACHE_EX);
        }
    }

    private RuntimeException createRpcException(String exClazz) {
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            final Class<?> rpcExClazz = contextClassLoader.loadClass(exClazz);
            final Optional<Constructor<?>> constructor = ReflectUtils
                    .findConstructor(rpcExClazz, new Class[]{String.class});
            if (constructor.isPresent()) {
                return (RuntimeException) constructor.get().newInstance("need retry");
            }
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException ignored) {
            // ignored
        }
        throw new IllegalArgumentException("Can not create rpc excetion!");
    }
}
