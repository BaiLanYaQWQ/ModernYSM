# 移植记录：26.3 → 26.2 → 26.1.2

本仓库只保留 **Fabric + NeoForge** 两个加载器（forge 模块已移除）。

三个分支的代码主干相同，差异集中在 Minecraft 三代渲染栈的 API 上。

## 版本矩阵

| 分支 | Minecraft | JDK | fabric-api | NeoForge |
| --- | --- | --- | --- | --- |
| `port/26.3` | 26.3 | 25 | 0.161.0+26.3 | 26.3.0.99 |
| `port/26.2` | 26.2 | 25 | 0.161.0+26.2 | 26.2.0.93 |
| `port/26.1.2` | 26.1.2 | 25 | 0.155.3+26.1.2 | 26.1.2.126 |

版本号统一在 `gradle.properties` 里切换，`fabric.mod.json` 与 `neoforge.mods.toml` 的版本约束同步改。

## 26.3 → 26.2

26.2 与 26.3 属于同一代（都有提取式 GUI、`SubmitNodeCollector`、`KeyEvent`），差异主要在渲染管线的包结构与输入域。

1. **渲染管线拆包**：26.3 把 blaze3d 的渲染抽象搬进了 `com.mojang.renderpearl.*`，26.2 还在 `com.mojang.blaze3d.*`
   - `renderpearl.api.pipeline.*` → `blaze3d.pipeline.*`
   - `renderpearl.api.textures.*` → `blaze3d.textures.*`
   - `renderpearl.api.vertex.*` → `blaze3d.vertex.*`
   - `renderpearl.api.GpuFormat` → `blaze3d.GpuFormat`
   - `PrimitiveTopology` 移到 `blaze3d` 根包
2. **输入退回 GLFW**
   - `InputConstants.Type.KEYBOARD` → `KEYSYM`
   - 键位默认值改回 GLFW 键码：`Y=89`、`Z=90`、`L=76`、`B=66`、`P=80`、未绑定 `-1`
   - `MouseButtons`：`0=左 / 1=右 / 2=中`（26.3 是 1/2/3）
3. **挥动状态回到公开字段**：`swinging` / `swingTime` / `swingingArm` 直接可读，
   `SwingCompat` 与 molang 绑定按字段语义实现
4. **第一人称手臂改挂点**：26.2 没有 `FirstPersonHandsAndItemsRenderer`，
   改挂 `AvatarRenderer#renderRightHand/renderLeftHand` 与 `ItemInHandRenderer#submitHandsWithItems`
5. **PoseStack**：`rotate` → `mulPose`，`rotateDegrees(Axis.X, v)` → `mulPose(Axis.X.rotationDegrees(v))`
6. 零散：`Blaze3D.openUri` → `Util.getPlatform().openUri`、`EntityRendererProvider.Context` 少一个
   `PalettedTextureManager` 参数、`VertexConsumer` 没有 `setUv3`（但有 `setLineWidth`）

## 26.2 → 26.1.2

26.1.2 再旧一层，有三处结构性差异。

1. **渲染管线没有 BindGroupLayout**：`RenderPipeline` 回到 sampler/uniform 模型。
   但 vanilla 已自带完整实体管线，因此 `YsmRenderPipelines` 直接复用：
   `RenderPipelines.ENTITY_CUTOUT_CULL / ENTITY_CUTOUT / ENTITY_TRANSLUCENT / ENTITY_TRANSLUCENT_EMISSIVE`，
   并删掉原来的 `RenderPipelinesInvoker`（26.1.2 的 `RenderPipelines` 没有 `register`）。
2. **PiP 是旧形态**：`PictureInPictureRenderer` 构造收 `BufferSource`、`prepare(T, GuiRenderState, int)`、
   `renderToTexture(T, PoseStack)`，内部自己取 `FeatureRenderDispatcher` 提交。
   NeoForge 侧 `RegisterPictureInPictureRenderersEvent` 的工厂是
   `Function<BufferSource, PictureInPictureRenderer<T>>`（26.3 才是 `Supplier`）。
3. **零散 API**：`Minecraft.screen` 是公开字段（无 `gui.screen()`）、`options.hideGui`、
   `getMainCamera()` / `getLighting()` / `getGameRenderState()`、`EntityType.HORSE`（无 `EntityTypes`）、
   `Blocks.RED_BED`、`TextureFormat.RGBA8`、顶点格式判定用 `equals(DefaultVertexFormat.ENTITY)`

## 关于 1.21.11

1.21.11 **不是 26.x 的同代版本**，实测从 26.3 直接回退会得到 1000+ 编译错误。已知的硬性障碍：

- `loom` 1.18 只支持去混淆的 26.x（报 "Cannot use Mojang mappings in a non-obfuscated environment"），
  1.21.11 必须用 **loom 1.13 + Gradle 8.x + JDK 21**，并需要 officialMojangMappings
- `ForgeGradle 7` 要求 Gradle 9.3+，与 loom 1.13 的 Gradle 8.x 无法共存
- GUI 层是立即模式（`GuiGraphics`）而非提取式（`GuiGraphicsExtractor`），全项目几百处调用要改
- 输入是 GLFW 直接键码，没有 `KeyEvent` record；fabric-api 0.141.6 也没有 networking context API

结论：1.21.11 应当从上游的 `1.20.1-forge` 分支**向上**移植，而不是从 26.3 **向下**回退。

## 构建环境注意

- **JDK 25**：26.x 系列用 Java 25，Gradle 9.7.1 跑不了更低版本
- **locale 必须是 UTF-8**：资源目录里有中文文件名的 avif，`LANG` 未设置时
  `processResources` 会因 MD5 计算失败而中断。CI 里显式设了 `LANG/LC_ALL=C.UTF-8`
- `/sdcard` 之类的 noexec 挂载点下要 `sh ./gradlew` 而不是 `./gradlew`

## 验证范围

三个分支目前完成的是**编译与打包**验证（`compileJava` 通过、`build` 产出 jar、CI 绿）。
**未做运行时验证**：键盘/鼠标域、PiP 纹理池、第一人称手臂这些改动都是对照字节码的静态等价推导，
需要实机确认。
