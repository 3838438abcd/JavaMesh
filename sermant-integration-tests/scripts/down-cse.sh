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

#!/bin/bash
tryTimes=3
#set -x
download() {
#  curl -o Local-CSE-2.1.3-linux-amd64.zip -L https://cse-bucket.obs.cn-north-1.myhuaweicloud.com/LocalCSE/Local-CSE-2.1.3-linux-amd64.zip
  curl -v localhost:30110
}
for ((i=1; i<=${tryTimes};i++))
do
  echo "try down cse at $i times"
  curl localhost:30111
  if [ $? == 0 ];then
    break
  fi
done
#unzip Local-CSE-2.1.3-linux-amd64.zip -d cse
#sh cse/start.sh &
