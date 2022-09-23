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

package com.huawei.flowcontrol.common.context;

import com.huawei.flowcontrol.common.entity.RequestEntity;
import com.huawei.flowcontrol.common.entity.RequestEntity.RequestType;

import java.util.function.Supplier;

/**
 * 流控上下文, 当前仅用于标记是否触发流控规则
 *
 * @author zhouss
 * @since 2022-09-13
 */
public enum FlowControlContext {
    /**
     * 单例
     */
    INSTANCE;

    /**
     * 是否被流控
     */
    private final ThreadLocal<Boolean> isFlowControl = new ThreadLocal<>();

    /**
     * 客户端请求体
     */
    private final ThreadLocal<RequestEntity> clientEntity = new ThreadLocal<>();

    /**
     * 服务端请求体
     */
    private final ThreadLocal<RequestEntity> serverEntity = new ThreadLocal<>();

    /**
     * 触发流控
     */
    public void triggerFlowControl() {
        isFlowControl.set(Boolean.TRUE);
    }

    /**
     * 获取请求体
     *
     * @param requestType 请求流向
     * @param supplier 构建器
     * @return RequestEntity
     */
    public RequestEntity getRequestEntity(RequestType requestType, Supplier<RequestEntity> supplier) {
        RequestEntity requestEntity;
        if (requestType == RequestType.CLIENT) {
            requestEntity = clientEntity.get();
            if (requestEntity == null) {
                requestEntity = supplier.get();
                clientEntity.set(requestEntity);
            }
        } else {
            requestEntity = serverEntity.get();
            if (requestEntity == null) {
                requestEntity = supplier.get();
                serverEntity.set(requestEntity);
            }
        }
        return requestEntity;
    }

    /**
     * 获取请求体
     *
     * @param isServer 是否为服务端
     * @param supplier 构建器
     * @return RequestEntity
     */
    public RequestEntity getRequestEntity(boolean isServer, Supplier<RequestEntity> supplier) {
        return getRequestEntity(isServer ? RequestType.SERVER : RequestType.CLIENT, supplier);
    }

    /**
     * 移除请求体线程变量
     *
     * @param isServer 是否为服务端
     */
    public void remove(boolean isServer) {
        remove(isServer ? RequestType.SERVER : RequestType.CLIENT);
    }

    /**
     * 移除线程变量
     *
     * @param requestType 请求流向
     */
    public void remove(RequestType requestType) {
        if (requestType == RequestType.CLIENT) {
            clientEntity.remove();
        } else {
            serverEntity.remove();
        }
    }

    /**
     * 流控标记
     */
    public void removeFlowControl() {
        isFlowControl.remove();
    }

    /**
     * 清理
     */
    public void clear() {
        isFlowControl.remove();
        clientEntity.remove();
        serverEntity.remove();
    }

    /**
     * 是否触发流控
     *
     * @return 是否触发流控
     */
    public boolean isFlowControl() {
        return isFlowControl.get() != null;
    }
}
