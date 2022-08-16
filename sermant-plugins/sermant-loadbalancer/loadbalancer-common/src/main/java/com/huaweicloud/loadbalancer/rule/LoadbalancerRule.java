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

package com.huaweicloud.loadbalancer.rule;

/**
 * 负载均衡
 *
 * @author zhouss
 * @since 2022-08-09
 */
public class LoadbalancerRule {
    private String serviceName;

    /**
     * 负载均衡规则名称
     */
    private String rule;

    /**
     * 构造器
     */
    public LoadbalancerRule() {
    }

    /**
     * 负载均衡构造器
     *
     * @param serviceName 服务名
     * @param rule 负载均衡类型
     */
    public LoadbalancerRule(String serviceName, String rule) {
        this.serviceName = serviceName;
        this.rule = rule;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    @Override
    public String toString() {
        return "LoadbalancerRule{"
                + "serviceName='" + serviceName + '\''
                + ", rule='" + rule + '\'' + '}';
    }
}
