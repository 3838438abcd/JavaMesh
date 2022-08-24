/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.huawei.dynamic.config.interceptors;

import com.huawei.dynamic.config.ConfigHolder;
import com.huawei.dynamic.config.DynamicConfiguration;
import com.huawei.dynamic.config.source.OriginConfigDisableSource;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.plugin.config.PluginConfigManager;

import org.springframework.cloud.bootstrap.config.BootstrapPropertySource;

import java.util.logging.Logger;

/**
 * 针对addFirst方法拦截, 当动态源配置中心时, 拦截禁止添加源配置中心配置源, 阻止配置生效
 *
 * @author zhouss
 * @since 2022-04-08
 */
public class MutableSourceInterceptor extends DynamicConfigSwitchSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    private static final String ZOOKEEPER_SOURCE = "org.springframework.cloud.zookeeper.config.ZookeeperPropertySource";

    private static final String NACOS_SOURCE = "com.alibaba.cloud.nacos.client.NacosPropertySource";

    private static final String BOOTSTRAP_SOURCE = "org.springframework.cloud.bootstrap.config.BootstrapPropertySource";

    private final DynamicConfiguration configuration;

    /**
     * 构造器
     */
    public MutableSourceInterceptor() {
        configuration = PluginConfigManager.getPluginConfig(DynamicConfiguration.class);
    }

    @Override
    public ExecuteContext doBefore(ExecuteContext context) {
        final Object source = context.getArguments()[0];
        if (!source.getClass().getName().equals(BOOTSTRAP_SOURCE)) {
            return context;
        }
        if (!configuration.isEnableOriginConfigCenter() || isDynamicClosed()) {
            BootstrapPropertySource<?> bootstrapPropertySource = (BootstrapPropertySource<?>) source;
            if (isTargetSource(bootstrapPropertySource.getDelegate())) {
                LOGGER.info("================skip source:" + source + "=========");
                context.skip(null);
            }
        }
        return context;
    }

    /**
     * 原配置中心是否已下发动态关闭
     *
     * @return 是否关闭
     */
    private boolean isDynamicClosed() {
        final Object config = ConfigHolder.INSTANCE.getConfig(OriginConfigDisableSource.ZK_CONFIG_CENTER_ENABLED);
        if (config == null) {
            return false;
        }
        return !Boolean.parseBoolean(config.toString());
    }

    private boolean isTargetSource(Object source) {
        final String name = source.getClass().getName();
        return name.equals(ZOOKEEPER_SOURCE) || name.equals(NACOS_SOURCE);
    }
}
