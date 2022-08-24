#
# Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
#
mvn test -Dsermant.integration.test.type=dynamic_config_zk --file sermant-integration-tests/spring-test/pom.xml
if [ $? == 0 ];then
  echo "success"
  echo "====================query notify=============="
  cat logs/sermant/core/app/2022-08-24/sermant-0.log | grep Origin Config Center
  echo "================end===================="
else
  echo "================all content================="
  cat logs/sermant/core/app/2022-08-24/sermant-0.log
  echo "====================query notify=============="
  cat logs/sermant/core/app/2022-08-24/sermant-0.log | grep Origin Config Center
  echo "================end===================="
  exit 9
fi
