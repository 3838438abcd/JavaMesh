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

package com.huaweicloud.spring.feign.consumer.controller;

import com.huaweicloud.spring.feign.api.FlowControlServerService;
import com.huaweicloud.spring.feign.api.FlowControlService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

/**
 * 流控测试
 *
 * @author zhouss
 * @since 2022-07-29
 */
@Controller
@ResponseBody
@RequestMapping("flowcontrol")
public class FlowController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlowController.class);

    @Autowired
    private FlowControlService flowControlService;

    @Autowired
    private FlowControlServerService flowControlServerService;

    /**
     * 实例隔离接口测试
     *
     * @return 实例隔离
     * @throws InterruptedException 线程中断抛出
     */
    @RequestMapping("instanceIsolation")
    public String instanceIsolation() throws InterruptedException {
        return flowControlService.instanceIsolation();
    }

    /**
     * 实例隔离接口测试
     *
     * @return 实例隔离
     */
    @RequestMapping("retry")
    public int retry() {
        Integer tryCount = null;
        try {
            tryCount = flowControlService.retry(UUID.randomUUID().toString());
        } catch (Exception ex) {
            LOGGER.error("Retry {} times", tryCount);
            LOGGER.error(ex.getMessage(), ex);
        }
        return tryCount == null ? 0 : tryCount;
    }

    /**
     * 限流测试
     *
     * @return ok
     */
    @RequestMapping("rateLimiting")
    public String rateLimiting() {
        return flowControlServerService.rateLimiting();
    }

    /**
     * 慢调用熔断测试
     *
     * @return ok
     */
    @RequestMapping("timedBreaker")
    public String timedBreaker() {
        return flowControlServerService.timedBreaker();
    }

    /**
     * 异常熔断测试
     *
     * @return ok
     * @throws Exception 模拟异常率
     */
    @RequestMapping("exceptionBreaker")
    public String exceptionBreaker() throws Exception {
        return flowControlServerService.exceptionBreaker();
    }

    /**
     * 隔离仓测试
     *
     * @return ok
     */
    @RequestMapping("bulkhead")
    public String bulkhead() {
        return flowControlServerService.bulkhead();
    }

    /**
     * 请求头匹配测试
     *
     * @return ok
     */
    @RequestMapping("header")
    public String header() {
        return flowControlServerService.header();
    }

    /**
     * 匹配服务名测试-匹配前提, 触发流控
     *
     * @return ok
     */
    @RequestMapping("serviceNameMatch")
    public String serviceNameMatch() {
        return flowControlServerService.serviceNameMatch();
    }

    /**
     * 匹配服务名测试-不匹配前提, 不触发流控
     *
     * @return ok
     */
    @RequestMapping("serviceNameNoMatch")
    public String serviceNameNoMatch() {
        return flowControlServerService.serviceNameNoMatch();
    }
}
