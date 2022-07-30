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

package com.huaweicloud.spring.feign.api;

import com.huaweicloud.spring.feign.api.configuration.HeaderMatchConfiguration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 流控能力测试
 *
 * @author zhouss
 * @since 2022-07-29
 */
@FeignClient(name = "feign-provider", contextId = "flowcontrol-client", configuration = HeaderMatchConfiguration.class)
public interface FlowControlService {
    /**
     * 实例隔离接口测试
     *
     * @return 实例隔离
     * @throws InterruptedException 线程中断抛出
     */
    @RequestMapping("instanceIsolation")
    String instanceIsolation() throws InterruptedException;

    /**
     * 实例隔离接口测试
     *
     * @param invocationId 调用ID
     * @return 实例隔离
     * @throws Exception 模拟异常重试
     */
    @RequestMapping("retry")
    int retry(@RequestParam("invocationId") String invocationId) throws Exception;
}
