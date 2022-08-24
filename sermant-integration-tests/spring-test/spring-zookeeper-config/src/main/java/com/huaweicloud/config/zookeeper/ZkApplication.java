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

package com.huaweicloud.config.zookeeper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.bootstrap.config.BootstrapPropertySource;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * zk测试
 *
 * @author zhouss
 * @since 2022-07-15
 */
@Controller
@RestController
@SpringBootApplication(scanBasePackages = {
    "com.huaweicloud.config.zookeeper",
    "com.huaweicloud.spring.common.config"
})
public class ZkApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZkApplication.class);

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(ZkApplication.class, args);
    }

    /**
     * 获取标签
     *
     * @return 标签
     */
    @RequestMapping("/labels")
    public Map<String, String> getLabels() {
        final HashMap<String, String> labels = new HashMap<>();
        labels.put("app", environment.getProperty("service.meta.application"));
        labels.put("environment", environment.getProperty("service.meta.environment"));
        return labels;
    }

    @EventListener(value = RefreshEvent.class)
    public void refresh() {
        LOGGER.info("====================环境刷新=================");
        LOGGER.info(environment.toString());
        final PropertySource<?> propertySource = ((ConfigurableEnvironment) environment).getPropertySources()
                .get("bootstrapProperties-config/application");
        if (propertySource instanceof BootstrapPropertySource) {
            LOGGER.info("======bootstrap:[{}]==========", ((BootstrapPropertySource<?>) propertySource).getDelegate());
        }
        LOGGER.info("====================环境结束=================");
    }
}
