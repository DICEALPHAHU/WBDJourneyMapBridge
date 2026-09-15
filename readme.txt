# WBDJourneyMapBridge

将 [WarZBombDefuse](https://www.minebbs.com/resources/folia-warzbombdefuse-cs-t-ct-tacz.17007/) 的队伍同步到原版 Team，配合 JourneyMap Teams 隐藏敌军。

## 前置要求

- **WarZBombDefuse**（闭源付费插件，需自行购买，本仓库不包含）
- JourneyMap + JourneyMap Teams（服务端 Forge 版 + 客户端）
- Arclight 1.20.1

## 编译

1. 将 WarZBombDefuse 的 jar 放入 `libs/`，重命名为 `wbd.jar`
2. `mvn clean package`

## 使用

jar 放入 `plugins/`，服务端 `mods/` 放入JourneyMap 与 JourneyMap Teams，客户端也装好，启动即可。
如果你是类Spigot纯净内核（Paper，Folia，Purpur等）就只用把jar 放入 `plugins/`行了

## 鸣谢

感谢 [Crazy_Jky](https://www.minebbs.com/members/crazy-jky.88908/) 开发的 WarZBombDefuse 并提供 API 支持。

## 许可

GPL-3.0
