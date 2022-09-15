/*
 * Copyright (C) 2021-2022 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huaweicloud.sermant.router.dubbo.strategy;

import com.huaweicloud.sermant.router.config.label.entity.Route;
import com.huaweicloud.sermant.router.config.strategy.RuleStrategy;
import com.huaweicloud.sermant.router.dubbo.strategy.rule.InvokerRuleStrategy;

import java.util.List;
import java.util.Map;

/**
 * 路由策略处理器
 *
 * @author provenceee
 * @since 2021-10-14
 */
public enum RuleStrategyHandler {
    /**
     * 实例
     */
    INSTANCE;

    private final RuleStrategy<Object> ruleStrategy;

    RuleStrategyHandler() {
        this.ruleStrategy = new InvokerRuleStrategy();
    }

    /**
     * 选取标签应用的invokers
     *
     * @param routes 路由规则
     * @param invokers dubbo invokers
     * @return 标签应用的invokers
     */
    public List<Object> getTargetInvoker(List<Route> routes, List<Object> invokers) {
        return ruleStrategy.getTargetInstances(routes, invokers);
    }

    /**
     * 选取不匹配标签的实例
     *
     * @param tags 标签
     * @param invokers 实例列表
     * @return 路由过滤后的实例
     */
    public List<Object> getMissMatchInstances(List<Map<String, String>> tags, List<Object> invokers) {
        return ruleStrategy.getMismatchInstances(tags, invokers);
    }
}