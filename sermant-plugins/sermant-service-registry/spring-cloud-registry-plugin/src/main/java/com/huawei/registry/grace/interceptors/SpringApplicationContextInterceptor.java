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

package com.huawei.registry.grace.interceptors;

import com.huawei.registry.config.ConfigConstants;
import com.huawei.registry.config.grace.GraceContext;
import com.huawei.registry.utils.CommonUtils;

import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextClosedEvent;

import java.util.Locale;

/**
 * 关闭前的优雅上下线逻辑处理
 *
 * @author zhouss
 * @since 2022-05-23
 */
public class SpringApplicationContextInterceptor extends GraceSwitchInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringApplicationContextInterceptor.class);

    @Override
    protected ExecuteContext doBefore(ExecuteContext context) {
        final Object[] arguments = context.getArguments();
        if (!(arguments[0] instanceof ContextClosedEvent)) {
            return context;
        }
        GraceContext.INSTANCE.getGraceShutDownManager().setShutDown(true);
        if (graceConfig.isEnableGraceShutdown()) {
            graceShutDown();
        } else {
            shutDown();
        }
        return context;
    }

    private void graceShutDown() {
        long shutdownWaitTime = graceConfig.getShutdownWaitTime() * ConfigConstants.SEC_DELTA;
        if (shutdownWaitTime <= 0) {
            LOGGER.warn(String.format(Locale.ENGLISH, "Invalid shutdown wait time %d!", shutdownWaitTime));
            return;
        }
        final long shutdownCheckTimeUnit = graceConfig.getShutdownCheckTimeUnit() * ConfigConstants.SEC_DELTA;
        while (GraceContext.INSTANCE.getGraceShutDownManager().getRequestCount() > 0 && shutdownWaitTime > 0) {
            LOGGER.info(String.format(Locale.ENGLISH, "Wait all request complete , remained count [%s]",
                    GraceContext.INSTANCE.getGraceShutDownManager().getRequestCount()));
            CommonUtils.sleep(shutdownCheckTimeUnit);
            shutdownWaitTime -= shutdownCheckTimeUnit;
        }
        final int requestCount = GraceContext.INSTANCE.getGraceShutDownManager().getRequestCount();
        if (requestCount > 0) {
            LOGGER.warn(String.format(Locale.ENGLISH, "Request num that does not completed is [%s] ", requestCount));
        } else {
            LOGGER.debug("Graceful shutdown completed!");
        }
    }

    private void shutDown() {
        CommonUtils.sleep(graceConfig.getShutdownWaitTime() * ConfigConstants.SEC_DELTA);
    }
}
