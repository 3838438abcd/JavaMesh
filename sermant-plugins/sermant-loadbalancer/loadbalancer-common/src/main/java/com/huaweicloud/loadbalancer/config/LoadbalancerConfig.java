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

package com.huaweicloud.loadbalancer.config;

import com.huaweicloud.sermant.core.config.common.ConfigTypeKey;
import com.huaweicloud.sermant.core.plugin.config.PluginConfig;

/**
 * 负载均衡配置
 *
 * @author provenceee
 * @since 2022-01-21
 */
@ConfigTypeKey("loadbalancer.plugin")
public class LoadbalancerConfig implements PluginConfig {
    /**
     * dubbo负载均衡策略
     */
    private DubboLoadbalancerType dubboType = DubboLoadbalancerType.RANDOM;

    /**
     * spring cloud loadbalancer负载均衡策略
     */
    private SpringLoadbalancerType springType = SpringLoadbalancerType.ROUND_ROBIN;

    /**
     * ribbon负载均衡策略
     */
    private RibbonLoadbalancerType ribbonType = RibbonLoadbalancerType.ROUND_ROBIN;

    /**
     * 默认负载均衡规则
     */
    private String defaultRule;

    /**
     * 是否使用cse规则
     */
    private boolean useCseRule = true;

    /**
     * 是否强制使用插件的负载均衡, 当前配置仅对ribbon生效。ribbon可能存在用户自身的负载均衡key, 若用户不想影响自身的负载均衡key, 则可将之设置为false
     */
    private boolean forceUseSermantLb = true;

    public boolean isForceUseSermantLb() {
        return forceUseSermantLb;
    }

    public boolean isUseCseRule() {
        return useCseRule;
    }

    public void setUseCseRule(boolean useCseRule) {
        this.useCseRule = useCseRule;
    }

    public DubboLoadbalancerType getDubboType() {
        return dubboType;
    }

    public void setDubboType(DubboLoadbalancerType dubboType) {
        this.dubboType = dubboType;
    }

    public SpringLoadbalancerType getSpringType() {
        return springType;
    }

    public void setSpringType(SpringLoadbalancerType springType) {
        this.springType = springType;
    }

    public RibbonLoadbalancerType getRibbonType() {
        return ribbonType;
    }

    public String getDefaultRule() {
        return defaultRule;
    }

    public void setDefaultRule(String defaultRule) {
        this.defaultRule = defaultRule;
    }

    public void setRibbonType(RibbonLoadbalancerType ribbonType) {
        this.ribbonType = ribbonType;
    }
}
