/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.flowcontrol.common.util;

import com.huaweicloud.sermant.core.utils.ReflectUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * 请求体转换工具类, 主要基于反射
 *
 * @author zhouss
 * @since 2022-09-19
 */
public class DubboAttachmentsHelper {
    private static final String APACHE_INVOCATION = "org.apache.dubbo.rpc.Invocation";
    private static final String ATTACHMENTS_FIELD = "attachments";

    private DubboAttachmentsHelper() {
    }

    /**
     * 获取attachments
     *
     * @param invocation 调用信息
     * @return Map
     */
    public static Map<String, String> resolveAttachments(Object invocation) {
        if (invocation == null) {
            return Collections.emptyMap();
        }
        final Optional<Object> fieldValue = ReflectUtils.getFieldValue(invocation, ATTACHMENTS_FIELD);
        if (fieldValue.isPresent() && fieldValue.get() instanceof Map) {
            return (Map<String, String>) fieldValue.get();
        }
        return Collections.emptyMap();
    }

    private static boolean isApache(Object invocation) {
        return APACHE_INVOCATION.equals(invocation.getClass().getName());
    }
}
