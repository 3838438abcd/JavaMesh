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

package com.huawei.registry.config;

import com.huaweicloud.sermant.core.plugin.config.PluginConfigManager;

/**
 * 优雅上下线配置解析
 *
 * @author zhouss
 * @since 2022-05-24
 */
public class GraceConfigResolver extends RegistryConfigResolver {
    /**
     * 优雅上下线配置前缀
     */
    private static final String GRACE_CONFIG_PREFIX = "grace.rule.";

    private GraceConfig defaultConfig = null;

    @Override
    protected String getConfigPrefix() {
        return GRACE_CONFIG_PREFIX;
    }

    @Override
    protected GraceConfig getDefaultConfig() {
        if (defaultConfig == null) {
            defaultConfig = getOriginConfig().clone();
        }
        return defaultConfig;
    }

    @Override
    protected GraceConfig getOriginConfig() {
        return PluginConfigManager.getPluginConfig(GraceConfig.class);
    }
}
