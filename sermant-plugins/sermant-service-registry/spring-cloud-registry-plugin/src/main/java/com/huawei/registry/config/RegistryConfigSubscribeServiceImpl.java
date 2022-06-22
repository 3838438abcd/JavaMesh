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
import com.huaweicloud.sermant.core.plugin.service.PluginService;
import com.huaweicloud.sermant.core.plugin.subscribe.ConfigSubscriber;
import com.huaweicloud.sermant.core.plugin.subscribe.CseGroupConfigSubscriber;
import com.huaweicloud.sermant.core.plugin.subscribe.DefaultGroupConfigSubscriber;
import com.huaweicloud.sermant.core.service.dynamicconfig.common.DynamicConfigEvent;
import com.huaweicloud.sermant.core.service.dynamicconfig.common.DynamicConfigListener;

/**
 * 注册配置订阅服务
 *
 * @author zhouss
 * @since 2022-05-24
 */
public class RegistryConfigSubscribeServiceImpl implements PluginService {
    /**
     * 订阅注册配置
     *
     * @param serviceName 服务名
     */
    public void subscribeRegistryConfig(String serviceName) {
        ConfigSubscriber subscriber;
        final RegisterConfig registerConfig = PluginConfigManager.getPluginConfig(RegisterConfig.class);
        if (registerConfig.getRegisterType() == RegisterType.SERVICE_COMB && registerConfig.isEnableSpringRegister()) {
            // 使用了CSE
            subscriber = new CseGroupConfigSubscriber(serviceName, new RegistryConfigListener(), "SpringCloudRegistry");
        } else {
            // 其他场景
            subscriber = new DefaultGroupConfigSubscriber(serviceName, new RegistryConfigListener(),
                    "SpringCloudRegistry");
        }
        subscriber.subscribe();
        PluginConfigManager.getPluginConfig(GraceConfig.class).fixGraceSwitch();
    }

    /**
     * 注册配置监听
     *
     * @since 2022-05-24
     */
    static class RegistryConfigListener implements DynamicConfigListener {
        private final RegistryConfigResolver graceConfigResolver = new GraceConfigResolver();
        private final RegistryConfigResolver switchConfigResolver = new OriginRegistrySwitchConfigResolver();

        @Override
        public void process(DynamicConfigEvent event) {
            switchConfigResolver.updateConfig(event);
            graceConfigResolver.updateConfig(event);
        }
    }
}
