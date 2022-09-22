# Graceful Online/Offline

[简体中文](document-zh.md) | [English](document.md)

该文档主要介绍优雅上下线能力以及其使用方法，该功能当前集成在[注册插件](../../../sermant-plugins/sermant-service-registry) 中, 但功能可独立使用。

## 功能

应用的重启、升级、扩容等操作无法避免，而该过程中时常会遇到以下问题：

- 刚上线的实例，由于流量过大，该实例在初始化时便被大量流量访问，导致请求阻塞，甚至宕机。例如存在懒加载的场景
- 实例下线时，因注册发现延迟刷新问题，无法及时告知上游，导致流量丢失或者错误。

为解决上述问题，优雅上下线应运而生，针对以上两个问题，插件提供**预热**与**优雅下线**能力，对上述情况基于保护。

**预热**， 顾名思义，即先用少部分流量对实例访问，后续根据时间推移，逐渐增加增加流量，确保新启动实例能成功过渡。

**优雅上下线**，即对下线的实例提供保护，插件基于实时通知+刷新缓存的机制快速更新上游缓存，同时基于流量统计的方式，确保即将下线的实例尽可能的将流量处理完成，最大程度避免流量丢失。

## 版本支持

当前优雅上下线能力**仅支持SpringCloud应用**，需确保SpringCloud版本在`Edgware.SR2`及以上。

注册中心支持：Zookeeper、Consul、Naocs、Eureka、ServiceCenter

**特别说明**：优雅上下线能力基于SpringCloud的默认负载均衡能力开发，若您实现了自定义负载均衡能力，该能力将不再适用。

## 使用说明

### 开启优雅上下线

若需要使用优雅上下线能力，首先需开启该能力，相关配置见[配置文件](../../../sermant-plugins/sermant-service-registry/config/config.yaml)， 若您已完成Sermant打包，则配置文件路径为`${agent path}/agent/pluginPackge/service-registry/config/config.yaml`

```yaml
grace.rule:
  enableSpring: true # springCloud优雅上下线开关
  startDelayTime: 0  # 优雅上下线启动延迟时间, 单位S
  enableWarmUp: true # 是否开启预热
  warmUpTime: 120    # 预热时间, 单位S
  enableGraceShutdown: true # 是否开启优雅下线
  shutdownWaitTime: 30  # 关闭前相关流量检测的最大等待时间, 单位S. 需开启enabledGraceShutdown才会生效
  enableOfflineNotify: true # 是否开启下线主动通知
  httpServerPort: 16688 # 开启下线主动通知时的httpServer端口
  upstreamAddressMaxSize: 500 # 缓存上游地址的默认大小
  upstreamAddressExpiredTime: 60 # 缓存上游地址的过期时间
```

除上述配置文件方式外，您还可基于环境变量或者系统参数进行配置，以开启优雅上下线开关为例，此处可配置环境变量`-Dgrace.rule.enableSpring=true`开启开关。

### 使用优雅上下线

#### 虚机场景

根据OS，携带agent即优雅上下线相关参数启动即可。

```shell
# windows
java -Dgrace.rule.enableSpring=true -Dgrace.rule.enableWarmUp=true -Dgrace.rule.enableGraceShutdown=true -Dgrace.rule.enableOfflineNotify=true -javaagent:${path}\sermant-agent-x.x.x\agent\sermant-agent.jar=appName=default -jar xxx.jar

# mac, linux
java -Dgrace.rule.enableSpring=true -Dgrace.rule.enableWarmUp=true -Dgrace.rule.enableGraceShutdown=true -Dgrace.rule.enableOfflineNotify=true -javaagent:${path}/sermant-agent-x.x.x/agent/sermant-agent.jar=appName=default -jar xxx.jar
```

相关标识符说明：

- ${path}: 此处路径（path）为实际打包的agent路径，请替换为实际路径
- sermant-agent-x.x.x： x.x.x请换成实际打包的Sermant版本， 例如1.0.0
- xxx.jar: 为您的应用包，请根据你的应用包名进行替换

#### 容器场景

容器场景可基于`Sermant-Injector`快速启动， 参考[容器化部署](../injector-zh.md)。

同时需修改[Deployment编排文件](../injector-zh.md#部署)，添加优雅上下线环境变量，修改后的编排文件如下：

```yaml
apiVersion: v1
kind: Deployment
metadata:
  name: demo-test
  labels:
    app: demo-test
spec:
  replicas: 1
  selector:
    app: demo-test
    matchLabels:
      app: demo-test
  template:
    metadata:
      labels:
        app: demo-test
        sermant-injection: enabled
    spec:
      containers:
      - name: image
        # 请替换成您的应用镜像
        image: image:1.0.0
        ports: 
        - containerPort: 8080
        env:
        - name: "grace_rule_enableSpring"
          value: "true"
        - name: "grace.rule.enableWarmUp"
          value: "true"
        - name: "grace_rule_enableGraceShutdown"
          value: "true"
        - name: "grace.rule.enableOfflineNotify"
          value: "true"
  ports:
    - port: 443
      targetPort: 8443
```

随后按照容器化流程启动即可。

## 快速入门

