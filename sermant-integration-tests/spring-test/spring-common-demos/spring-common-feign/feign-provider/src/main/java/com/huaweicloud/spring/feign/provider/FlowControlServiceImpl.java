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

package com.huaweicloud.spring.feign.provider;

import com.huaweicloud.spring.common.flowcontrol.provider.ProviderClientController;
import com.huaweicloud.spring.feign.api.FlowControlService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 流控能力测试
 *
 * @author zhouss
 * @since 2022-07-29
 */
@Controller
@ResponseBody
public class FlowControlServiceImpl extends ProviderClientController implements FlowControlService {

}
