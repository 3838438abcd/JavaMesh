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

package com.huawei.flowcontrol.inject.retry;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

/**
 * 重试注入配置类
 *
 * @author zhouss
 * @since 2022-07-23
 */
@Configuration
@ConditionalOnProperty(value = "sermant.flowcontrol.retry.enabled",
        havingValue = "true", matchIfMissing = true)
public class SpringRetryConfiguration {
    /**
     * 流控重试注入
     *
     * @return resttemplate
     */
    @Bean
    @LoadBalanced
    @Primary
    @ConditionalOnClass(name = "org.springframework.web.client.RestTemplate")
    public RestTemplate restTemplate() {
        return new RetryableRestTemplate();
    }

    /**
     * feign重试注入
     *
     * @param delegate 代理
     * @param loadBalancerClient 负载均衡客户端
     * @return client
     */
    /*@Bean
    @ConditionalOnClass(name = "feign.Client")
    public Client feignClient(@Autowired(required = false) Client delegate, LoadBalancerClient loadBalancerClient) {
        return new RetryableFeignClient(delegate, loadBalancerClient);
    }*/
}
