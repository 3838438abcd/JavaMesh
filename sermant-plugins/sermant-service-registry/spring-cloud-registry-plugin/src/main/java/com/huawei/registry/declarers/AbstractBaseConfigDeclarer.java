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

package com.huawei.registry.declarers;

import com.huawei.registry.config.GraceConfig;
import com.huawei.registry.config.RegisterConfig;

import com.huaweicloud.sermant.core.plugin.agent.declarer.AbstractPluginDeclarer;
import com.huaweicloud.sermant.core.plugin.config.PluginConfigManager;

/**
 * 基础配置拦截点
 *
 * @author zhouss
 * @since 2022-08-18
 */
public abstract class AbstractBaseConfigDeclarer extends AbstractPluginDeclarer {
    private final RegisterConfig registerConfig;

    private final GraceConfig graceConfig;

    /**
     * 构造器
     */
    protected AbstractBaseConfigDeclarer() {
        this.registerConfig = PluginConfigManager.getPluginConfig(RegisterConfig.class);
        this.graceConfig = PluginConfigManager.getPluginConfig(GraceConfig.class);
    }

    protected RegisterConfig getRegisterConfig() {
        return registerConfig;
    }

    protected GraceConfig getGraceConfig() {
        return graceConfig;
    }

    /**
     * 是否开启双注册, 默认
     *
     * @return true 双注册
     */
    protected boolean isEnableSpringDoubleRegistry() {
        return registerConfig.isEnableSpringRegister() && registerConfig.isOpenMigration();
    }

    /**
     * 是否开启优雅上下线
     *
     * @return true 开启优雅上下线
     */
    protected boolean isEnableGrace() {
        return graceConfig.isEnableSpring() || graceConfig.isEnableGrace();
    }
}
