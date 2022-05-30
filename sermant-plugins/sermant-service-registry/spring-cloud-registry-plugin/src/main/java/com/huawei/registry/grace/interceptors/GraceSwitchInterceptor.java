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

package com.huawei.registry.grace.interceptors;

import com.huawei.registry.config.ConfigConstants;
import com.huawei.registry.config.GraceConfig;
import com.huawei.registry.config.grace.GraceConstants;
import com.huawei.registry.context.RegisterContext;
import com.huawei.registry.support.RegisterSwitchSupport;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.plugin.config.PluginConfigManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 优雅上下线开关
 *
 * @author zhouss
 * @since 2022-05-17
 */
public class GraceSwitchInterceptor extends RegisterSwitchSupport {
    /**
     * 最大的缓存数量
     */
    protected static final int MAX_CACHE_SIZE = 500;

    /**
     * 预热开始时间缓存
     */
    protected final Map<String, String> warmUpStartTimeCache = new ConcurrentHashMap<>();

    protected final GraceConfig graceConfig;

    /**
     * 优雅上下线开关
     *
     * @since 2022-05-17
     */
    public GraceSwitchInterceptor() {
        graceConfig = PluginConfigManager.getPluginConfig(GraceConfig.class);
    }

    @Override
    protected boolean isEnabled() {
        return graceConfig.isEnableSpring();
    }

    /**
     * 构建endpoint
     *
     * @param host 域名
     * @param port 端口
     * @return endpoint
     */
    protected String buildEndpoint(String host, int port) {
        return String.format(Locale.ENGLISH, "%s:%s", host, port);
    }

    /**
     * 预热信息
     *
     * @param ip 实例IP
     * @param port 实例端口
     */
    protected void warmMessage(String ip, int port) {
        LoggerFactory.getLogger().fine(String.format(Locale.ENGLISH, "Instance [%s:%s] is warming up!", ip, port));
    }

    /**
     * 对单个实例计算权重
     *
     * @param metadata 原信息
     * @param cacheKey 缓存键
     * @param weights 权重分配
     * @param index 当前实例索引
     * @return WarmResponse
     */
    protected boolean calculate(Map<String, String> metadata, String cacheKey, int[] weights, int index) {
        final long currentTimeMillis = System.currentTimeMillis();
        String injectTimeStr = warmUpStartTimeCache.computeIfAbsent(cacheKey,
            fn -> {
                final String timeStr = metadata.getOrDefault(GraceConstants.WARM_KEY_INJECT_TIME,
                    String.valueOf(currentTimeMillis - GraceConstants.DEFAULT_WARM_UP_INJECT_TIME_GAP));
                return checkTime(timeStr, currentTimeMillis);
            });
        final String warmUpTimeStr = metadata
            .getOrDefault(GraceConstants.WARM_KEY_TIME, GraceConstants.DEFAULT_WARM_UP_TIME);
        final String warmUpWeightStr = metadata.getOrDefault(GraceConstants.WARM_KEY_WEIGHT,
            String.valueOf(GraceConstants.DEFAULT_WARM_UP_WEIGHT));
        final String warmUpCurveStr = metadata
            .getOrDefault(GraceConstants.WARM_KEY_CURVE, String.valueOf(GraceConstants.DEFAULT_WARM_UP_CURVE));
        final long injectTime = Long.parseLong(injectTimeStr);
        final long warmUpTime = Integer.parseInt(warmUpTimeStr) * ConfigConstants.SEC_DELTA;
        final int weight = this.calculateWeight(injectTime, warmUpTime, warmUpWeightStr, warmUpCurveStr);
        weights[index] = weight;
        return currentTimeMillis - injectTime > warmUpTime;
    }

    /**
     * 重置注入时间
     *
     * @param injectTimeStr 注入时间戳
     * @param currentTimeMillis 当前时间
     * @return 更新的注入时间
     */
    protected String checkTime(String injectTimeStr, long currentTimeMillis) {
        if (Long.parseLong(injectTimeStr) > currentTimeMillis - GraceConstants.DEFAULT_WARM_UP_INJECT_TIME_GAP) {
            if (warmUpStartTimeCache.size() > MAX_CACHE_SIZE) {
                clearExpired();
            }
            return String.valueOf(currentTimeMillis);
        }
        return injectTimeStr;
    }

    /**
     * 计算权重
     *
     * @param injectTime 预热参数注入时间
     * @param warmUpTime 预热时间
     * @param warmUpWeightStr 预热权重
     * @param warmUpCurveStr 预热计算曲线值
     * @return 权重
     */
    protected int calculateWeight(long injectTime, long warmUpTime, String warmUpWeightStr,
        String warmUpCurveStr) {
        final int warmUpWeight = Integer.parseInt(warmUpWeightStr);
        int warmUpCurve = Integer.parseInt(warmUpCurveStr);
        if (warmUpTime <= 0 || injectTime <= 0) {
            // 未开启预热的服务默认100权重
            return warmUpWeight;
        }
        if (warmUpCurve < 0) {
            warmUpCurve = GraceConstants.DEFAULT_WARM_UP_CURVE;
        }
        final long runtime = System.currentTimeMillis() - injectTime;
        if (runtime > 0 && runtime < warmUpTime) {
            // 预热未结束
            return calculateWeight(runtime, warmUpTime, warmUpCurve, warmUpWeight);
        }
        return Math.max(0, warmUpWeight);
    }

    /**
     * 计算权重
     *
     * @param runtime 运行时间（从启动开始）
     * @param warmUpTime 预热时间
     * @param warmUpCurve 预热计算曲线
     * @param warmUpWeight 预热权重
     * @return 权重
     */
    protected int calculateWeight(double runtime, double warmUpTime, int warmUpCurve, int warmUpWeight) {
        final int round = (int) Math.round(Math.pow(runtime / warmUpTime, warmUpCurve) * warmUpWeight);
        return round < 1 ? 1 : Math.min(round, warmUpWeight);
    }

    /**
     * 清理缓存
     */
    protected void clearExpired() {
        final long currentTimeMillis = System.currentTimeMillis();
        final Iterator<Entry<String, String>> iterator = warmUpStartTimeCache.entrySet().iterator();
        while (iterator.hasNext()) {
            final Entry<String, String> entry = iterator.next();
            final long timestamp = Long.parseLong(entry.getValue());
            if (currentTimeMillis >= timestamp + GraceConstants.DEFAULT_WARM_UP_INJECT_TIME_GAP) {
                iterator.remove();
            }
        }
    }

    /**
     * 构建缓存键
     *
     * @param host 域名
     * @param port 端口
     * @param metadata 元数据
     * @return key
     */
    protected String buildCacheKey(String host, int port, Map<String, String> metadata) {
        return String.format(Locale.ENGLISH, "%s:%s:%s", host, port, metadata.get(
            GraceConstants.WARM_KEY_INJECT_TIME));
    }

    /**
     * 选择实例
     *
     * @param totalWeight 总权重
     * @param weights 基于所有实例的权重分配
     * @param serverList 服务实例列表
     * @return 确定实例
     */
    protected Optional<Object> chooseServer(int totalWeight, int[] weights, List<?> serverList) {
        if (totalWeight <= 0) {
            return Optional.empty();
        }
        int position = new Random().nextInt(totalWeight);
        for (int i = 0; i < weights.length; i++) {
            position -= weights[i];
            if (position < 0) {
                return Optional.of(serverList.get(i));
            }
        }
        return Optional.empty();
    }

    /**
     * 获取本地ip的请求头
     *
     * @return 请求头
     */
    protected Map<String, List<String>> getGraceIpHeaders() {
        String address = RegisterContext.INSTANCE.getClientInfo().getIp() + ":" + graceConfig.getHttpServerPort();
        Map<String, List<String>> headers = new HashMap<>();
        headers.put(GraceConstants.SERMANT_GRACE_ADDRESS, Collections.singletonList(address));
        headers.put(GraceConstants.GRACE_OFFLINE_SOURCE_KEY,
            Collections.singletonList(GraceConstants.GRACE_OFFLINE_SOURCE_VALUE));
        return headers;
    }
}
