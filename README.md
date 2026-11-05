<div align="center">
  <img src="images/banner.png" alt="logo"/>
  <h2>OpenYSM</h2>
  <p>Yes Steve Model 的开源替代品 · 向下移植版</p>

  <p>
    <a href="LICENSE.txt"><img src="https://img.shields.io/badge/license-MIT-green.svg" alt="License"/></a>
    <a href="../../actions/workflows/build.yml"><img src="../../actions/workflows/build.yml/badge.svg?branch=port%2F26.3" alt="Build"/></a>
    <a href="../../releases"><img src="https://img.shields.io/github/v/release/BaiLanYaQWQ/ModernYSM?display_name=tag" alt="Release"/></a>
  </p>
</div>

## 说明

OpenYSM 是一款基于 [Yes Steve Model](https://modrinth.com/mod/yes-steve-model) 的模组，它修改了原版玩家模型。核心使用 [GeckoLib](https://github.com/bernie-g/geckolib) 库，采用 Minecraft 基岩版的模型与动画文件，玩家可以自由更换自己的玩家模型和动画。

本仓库在上游 `port/26.3` 的基础上做**向下移植**，把同一套功能回移到更早的 Minecraft 版本，并只保留 **Fabric + NeoForge** 两个加载器（forge 模块已移除）。

## 支持的版本

| 分支 | Minecraft | Fabric | NeoForge |
| --- | --- | --- | --- |
| `port/26.3`（默认） | 26.3 | ✅ | ✅ |
| `port/26.2` | 26.2 | ✅ | ✅ |
| `port/26.1.2` | 26.1.2 | ✅ | ✅ |

三个分支都由 [GitHub Actions](../../actions) 自动构建，推送即触发，也可以在 Actions 页面手动运行。

## 下载

**推荐**：直接去 [Releases](../../releases) 下载打包好的 jar：

- `openysm-<mod版本>-fabric-<MC版本>.jar` — Fabric 版
- `openysm-<mod版本>-neoforge-<MC版本>.jar` — NeoForge 版

每个 Release 对应一个 Minecraft 版本（`v2.6.6.6-26.3`、`v2.6.6.6-26.2`、`v2.6.6.6-26.1.2`）。

也可以从 [Actions](../../actions) 的 Artifacts 里取每次提交的构建产物，或自己构建（见下）。

## 构建

需要 **JDK 25**（26.x 系列使用 Java 25）。仓库已包含 Gradle wrapper，无需单独安装 Gradle。

```bash
./gradlew :fabric:build :neoforge:build
```

产物：

- `fabric/build/libs/openysm-*.jar`
- `neoforge/build/libs/openysm-*.jar`

> 注意：26.x 已是去混淆版本，不需要 mappings；`common/` 与 `compat-stubs/` 是共享源码目录，由两个加载器子工程通过 `srcDirs` 引入。

## 移植

26.3 → 26.2 → 26.1.2 并不是改个版本号：三代渲染栈各不相同（renderpearl / blaze3d / 旧版 RenderPipeline），
输入域在 SDL 与 GLFW 之间来回换过，GUI 也经历了立即模式 → 提取式的重构。

具体差异、踩过的坑和每个分支改动理由见 [docs/PORTING.md](docs/PORTING.md)。

## 贡献

欢迎任何形式的贡献，包括但不限于提交 Issue、改进文档、修复 Bug、新增功能。

## 开源协议

### 源代码协议

本项目的源代码采用 MIT License 开放，您可以自由地使用、修改和分发代码，仅需要保留原始的版权声明。

详细的许可证条款请参见 [LICENSE.txt](LICENSE.txt) 文件。

### 模型资源协议

仓库中自带的模型文件采用不同的协议：

- 默认模型: 采用 CC0 (Creative Commons Zero) 协议，完全开放，无任何使用限制
- 酒狐 (Wine Fox) 模型: 采用 CC BY-NC-SA 4.0 协议，允许非商业使用，需要署名，并且衍生作品需要采用相同协议

请在使用相应模型时严格遵守对应的协议要求。

## 致谢

- 上游项目：[hongshaoluobotou/ModernYSM](https://github.com/hongshaoluobotou/ModernYSM)
- 原始模组：[Yes Steve Model](https://modrinth.com/mod/yes-steve-model)
- 动画/模型库：[GeckoLib](https://github.com/bernie-g/geckolib)
