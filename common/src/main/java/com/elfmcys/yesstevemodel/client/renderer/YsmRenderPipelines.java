package com.elfmcys.yesstevemodel.client.renderer;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;

/**
 * YSM 自定义渲染管线集（26.1.2）。
 *
 * <p>26.3 需要自建管线：renderpearl 拆包后 vanilla 的 snippet 链不可复用，必须用
 * {@code BindGroupLayout} 逐个补齐 GLOBALS/PROJECTION/DYNAMIC_TRANSFORMS/FOG/SAMPLER0/1/2/LIGHTING，
 * 再经私有 {@code RenderPipelines#register} 塞进启动期 shader 预热清单。
 *
 * <p>26.1.2 的 builder 仍是 1.21.x 的 sampler/uniform 模型，且 vanilla 已经定义了整套实体管线
 * （{@code ENTITY_CUTOUT_CULL}、{@code ENTITY_CUTOUT}、{@code ENTITY_TRANSLUCENT}、
 * {@code ENTITY_TRANSLUCENT_EMISSIVE}），shader 同为 {@code core/entity}，uniform 布局与
 * YSM 的需求一致（Sampler0 + Sampler1 overlay + Sampler2 lightmap + FOG/LIGHTING）——
 * 因此这里直接复用 vanilla 管线，不再自建、也不再需要 {@code RenderPipelinesInvoker}。
 * 自定义管线热路径（wrap 点）随之在 26.1.2 上退化为 vanilla 常量别名，语义等价。
 */
public final class YsmRenderPipelines {

    /** 对照 26.3 GEO_CUTOUT_CULL：不透明单面（GPU cull back）。 */
    public static final RenderPipeline GEO_CUTOUT_CULL = RenderPipelines.ENTITY_CUTOUT_CULL;

    /** 对照 26.3 GEO_CUTOUT_NO_CULL：不透明双面（ysmGlow 零厚度面片等不可判向几何）。 */
    public static final RenderPipeline GEO_CUTOUT_NO_CULL = RenderPipelines.ENTITY_CUTOUT;

    /** 对照 26.3 GEO_TRANSLUCENT：半透明双面，配合 RenderSetup.sortOnUpload 逐 quad 排序。 */
    public static final RenderPipeline GEO_TRANSLUCENT = RenderPipelines.ENTITY_TRANSLUCENT;

    /** 全亮变体（vanilla EMISSIVE define 语义），暂未接线，见 26.3 分支说明。 */
    public static final RenderPipeline GEO_GLOW = RenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE;

    private YsmRenderPipelines() {
    }

    public static void init() {
    }
}
