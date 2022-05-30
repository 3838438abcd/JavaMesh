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

package com.huawei.registry.config.grace;

import com.huawei.registry.config.ConfigConstants;
import com.huawei.registry.config.GraceConfig;
import com.huawei.registry.config.RegisterConfig;

import com.huaweicloud.sermant.core.plugin.config.PluginConfigManager;

/**
 * 优雅上下线上下文
 *
 * @author zhouss
 * @since 2022-05-17
 */
public enum GraceContext {
    /**
     * 单例
     */
    INSTANCE;

    /**
     * 优雅下线管理
     */
    private final GraceShutDownManager graceShutDownManager = new GraceShutDownManager();

    /**
     * 插件开始加载时间
     */
    private long startTime;

    /**
     * 注册完成时间
     */
    private long registryFinishTime;

    /**
     * 第二个注册中心注册完成时间
     */
    private long secondRegistryFinishTime;

    /**
     * 开始预热时间
     */
    private long startWarmUpTime;

    /**
     * 优雅上下线配置
     */
    private GraceConfig graceConfig;

    /**
     * 注册配置
     */
    private RegisterConfig registerConfig;

    /**
     * 是否准备就绪
     *
     * @return 就绪返回true
     */
    public boolean isReady() {
        final GraceConfig curGraceConfig = getGraceConfig();
        if (curGraceConfig == null) {
            return false;
        }
        if (curGraceConfig.isEnableWarmUp() && curGraceConfig.isReadyBeforeWarmUp()) {
            // 预热判断
            if (startWarmUpTime == 0L) {
                return false;
            }
            return System.currentTimeMillis() - startWarmUpTime >= getGraceConfig().getWarmUpTime()
                    * ConfigConstants.SEC_DELTA;
        }
        return waitWithRegistry();
    }

    private boolean waitWithRegistry() {
        if (registryFinishTime == 0) {
            return System.currentTimeMillis() - startTime >= getGraceConfig().getNoRegistryMaxWaitTime()
                    * ConfigConstants.SEC_DELTA;
        }
        final boolean isFirstRegistryReady = isRegistryReady(registryFinishTime);
        final boolean isOpenMigration = getRegisterConfig().isOpenMigration();
        if (!isOpenMigration) {
            return isFirstRegistryReady;
        }
        return isFirstRegistryReady && isRegistryReady(secondRegistryFinishTime);
    }

    private boolean isRegistryReady(long finishTime) {
        return System.currentTimeMillis() - finishTime >= getGraceConfig().getK8sReadinessWaitTime()
                * ConfigConstants.SEC_DELTA;
    }

    private RegisterConfig getRegisterConfig() {
        if (registerConfig == null) {
            registerConfig = PluginConfigManager.getPluginConfig(RegisterConfig.class);
        }
        return registerConfig;
    }

    private GraceConfig getGraceConfig() {
        if (graceConfig == null) {
            graceConfig = PluginConfigManager.getPluginConfig(GraceConfig.class);
        }
        return graceConfig;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getRegistryFinishTime() {
        return registryFinishTime;
    }

    public void setRegistryFinishTime(long registryFinishTime) {
        this.registryFinishTime = registryFinishTime;
    }

    public GraceShutDownManager getGraceShutDownManager() {
        return graceShutDownManager;
    }

    public void setStartWarmUpTime(long startWarmUpTime) {
        this.startWarmUpTime = startWarmUpTime;
    }

    public long getStartWarmUpTime() {
        return startWarmUpTime;
    }

    public long getSecondRegistryFinishTime() {
        return secondRegistryFinishTime;
    }

    public void setSecondRegistryFinishTime(long secondRegistryFinishTime) {
        this.secondRegistryFinishTime = secondRegistryFinishTime;
    }
}
