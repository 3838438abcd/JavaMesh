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

package com.huawei.registry.service.server;

import com.huawei.registry.config.grace.GraceConstants;
import com.huawei.registry.config.grace.GraceContext;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 健康检查
 *
 * @author zhouss
 * @since 2022-06-15
 */
public class HealthCheckHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!GraceConstants.GRACE_HTTP_METHOD_GET.equalsIgnoreCase(exchange.getRequestMethod())) {
            return;
        }
        final boolean isReady = GraceContext.INSTANCE.isReady();
        if (isReady) {
            writeMsg(exchange, GraceConstants.GRACE_HEALTH_OK_MSG, GraceConstants.GRACE_HTTP_SUCCESS_CODE);
        } else {
            writeMsg(exchange, GraceConstants.GRACE_FAILURE_MSG, GraceConstants.GRACE_HTTP_FAILURE_CODE);
        }
        exchange.close();
    }

    private void writeMsg(HttpExchange exchange, String message, int code) throws IOException {
        exchange.sendResponseHeaders(code, message.length());
        exchange.getResponseBody().write(message.getBytes(StandardCharsets.UTF_8));
        exchange.getResponseBody().flush();
    }
}
