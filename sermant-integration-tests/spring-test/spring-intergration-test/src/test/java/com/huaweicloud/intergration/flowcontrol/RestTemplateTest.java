/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.huaweicloud.intergration.flowcontrol;

import com.huaweicloud.intergration.common.FlowControlConstants;
import com.huaweicloud.intergration.common.RequestUtils;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;

import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;

/**
 * RestTemplate协议测试
 *
 * @author zhouss
 * @since 2022-07-30
 */
public class RestTemplateTest {
    private static final int RATE_LIMITING_REQUEST_COUNT = 10;
    private static final int BREAKER_REQUEST_COUNT = 10;

    private static final String restConsumerUrl = "http://127.0.0.1:8005/flowcontrol";

    private static final Logger LOGGER = LoggerFactory.getLogger(RestTemplateTest.class);

    /**
     * 测试服务端限流
     */
    @Test
    public void testServerRateLimiting() {
        String url = restConsumerUrl + "/rateLimiting";
        boolean expected = false;
        for (int i = 0; i < RATE_LIMITING_REQUEST_COUNT; i++) {
            try {
                RequestUtils.get(url, Collections.emptyMap(), String.class);
            } catch (Exception ex) {
                if (ex.getMessage().startsWith(FlowControlConstants.COMMON_FLOW_CONTROL_CODE)) {
                    expected = true;
                    break;
                }
            }
        }
        Assert.assertTrue(expected);
    }

    /**
     * 测试客户端熔断-慢调用
     */
    @Test
    public void testTimedBreaker() {
        process("/timedBreaker", "Degraded and blocked", BREAKER_REQUEST_COUNT);
    }

    /**
     * 测试客户端熔断-异常
     */
    @Test
    public void testExceptionBreaker() {
        process("/exceptionBreaker", "Degraded and blocked", BREAKER_REQUEST_COUNT);
    }

    /**
     * 测试隔离仓功能
     */
    @Test
    public void testBulkHead() throws InterruptedException {
        final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 5, 0, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(100));
        final CountDownLatch countDownLatch = new CountDownLatch(100);
        for (int i = 0; i < 100; i ++) {
            threadPoolExecutor.execute(() -> {
                process("/bulkhead", "Exceeded the max concurrent calls", RATE_LIMITING_REQUEST_COUNT);
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        threadPoolExecutor.shutdown();
    }

    private void process(String api, String flowControlMsg, int requestCount) {
        String url = restConsumerUrl + api;
        AtomicBoolean expected = new AtomicBoolean(false);
        final BiFunction<ClientHttpResponse, String, String> callback =
                (clientHttpResponse, result) -> {
                    if (result.contains(flowControlMsg)) {
                        expected.set(true);
                    }
                    return result;
                };
        for (int i = 0; i < requestCount; i++) {
            if (expected.get()) {
                break;
            }
            try {
                RequestUtils.get(url, Collections.emptyMap(), String.class, callback);
            } catch (Exception ex) {

            }
        }
        Assert.assertTrue(expected.get());
    }
}
