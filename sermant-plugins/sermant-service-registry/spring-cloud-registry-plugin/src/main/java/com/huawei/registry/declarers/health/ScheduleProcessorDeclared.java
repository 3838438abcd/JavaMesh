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

package com.huawei.registry.declarers.health;

import com.huawei.registry.declarers.AbstractDoubleRegistryDeclarer;
import com.huawei.registry.interceptors.health.ScheduleProcessorInterceptor;

import com.huaweicloud.sermant.core.plugin.agent.declarer.InterceptDeclarer;
import com.huaweicloud.sermant.core.plugin.agent.matcher.ClassMatcher;
import com.huaweicloud.sermant.core.plugin.agent.matcher.MethodMatcher;

/**
 * spring定时器注解@schudled拦截
 * 当前用于拿到consul1.x的心跳定时器
 *
 * @author zhouss
 * @since 2022-06-13
 */
public class ScheduleProcessorDeclared extends AbstractDoubleRegistryDeclarer {
    /**
     * 定时器自动配置类
     */
    private static final String ENHANCE_CLASS = "org.springframework.scheduling.annotation.SchedulingConfiguration";

    /**
     * 拦截类的全限定名
     */
    private static final String INTERCEPT_CLASS = ScheduleProcessorInterceptor.class.getCanonicalName();

    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.nameEquals(ENHANCE_CLASS);
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        return new InterceptDeclarer[] {
                InterceptDeclarer.build(MethodMatcher.nameEquals("scheduledAnnotationProcessor"), INTERCEPT_CLASS)
        };
    }
}
