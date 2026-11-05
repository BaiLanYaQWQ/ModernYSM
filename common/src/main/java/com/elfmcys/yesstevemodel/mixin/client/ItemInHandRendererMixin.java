package com.elfmcys.yesstevemodel.mixin.client;

import com.elfmcys.yesstevemodel.client.event.RenderFirstPlayerBackground;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 对应 1.20.1 forge {@code RenderHandEvent} → {@link RenderFirstPlayerBackground}：
 * 每帧一次提交带自定义肢体模型的 background（遮盖原版空手手臂）。
 *
 * <p>26.3 这个入口在 {@code FirstPersonHandsAndItemsRenderer#submitHandsWithItems}，
 * 26.2 仍在 {@link ItemInHandRenderer#submitHandsWithItems}
 * （签名 {@code (float, PoseStack, SubmitNodeCollector, LocalPlayer, int packedLight)}）。
 * {@code resetFrame} 放这里：每帧仅此一次入口。
 */
@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

    @Inject(method = "submitHandsWithItems", at = @At("HEAD"))
    private void ysm$onSubmitHandsWithItems(float partialTick, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
                                            LocalPlayer player, int packedLight, CallbackInfo ci) {
        RenderFirstPlayerBackground.resetFrame();
        RenderFirstPlayerBackground.onRenderHand(poseStack, submitNodeCollector, packedLight, partialTick);
    }
}
