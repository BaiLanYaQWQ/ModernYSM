package com.elfmcys.yesstevemodel.mixin.client;

import com.elfmcys.yesstevemodel.client.event.ReplacePlayerHandRenderEvent;
import com.elfmcys.yesstevemodel.client.renderer.GeoBufferSource;
import com.elfmcys.yesstevemodel.client.renderer.ModelPreviewRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 第一人称手臂接管（26.2）。
 *
 * <p>26.3 把第一人称手部整体搬进 {@code FirstPersonHandsAndItemsRenderer}，26.2 还没有这个类——
 * 手臂仍在 {@link AvatarRenderer#renderRightHand} / {@link AvatarRenderer#renderLeftHand}
 * （即 1.20.1 {@code PlayerRenderer#renderRightHand} 的位置，1.20.1 分支的
 * {@code PlayerRendererHandMixin} 挂的就是这里）。
 *
 * <p>26.2 签名已改为 {@code (PoseStack, SubmitNodeCollector, int, Identifier, boolean)}，
 * 不再带实体参数，因此实体取 {@code Minecraft.getInstance().player}。
 * 返回 true = 已渲染 YSM 自定义手部模型，取消原版手臂。
 */
@Mixin(AvatarRenderer.class)
public class AvatarRendererHandMixin {

    @Unique
    private static boolean ysm$renderArm(HumanoidArm arm, PoseStack poseStack, SubmitNodeCollector collector, int packedLight) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return false;
        }
        // 手部渲染发生在 LevelRenderer#render 之后，firstPerson 标志已被清除；
        // HandItemRenderer → processAnimation 依赖该标志（1.20.1 中手部渲染发生在 renderLevel 内）。
        boolean prev = ModelPreviewRenderer.isFirstPerson();
        ModelPreviewRenderer.setFirstPersonMode(true);
        boolean replaced;
        try {
            GeoBufferSource bufferSource = new GeoBufferSource();
            replaced = ReplacePlayerHandRenderEvent.onRenderArm(player, arm, poseStack, bufferSource, packedLight);
            if (replaced) {
                bufferSource.flush(collector, poseStack);
            }
        } finally {
            ModelPreviewRenderer.setFirstPersonMode(prev);
        }
        return replaced;
    }

    @Inject(method = "renderRightHand", at = @At("HEAD"), cancellable = true)
    private void ysm$onRenderRightHand(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight,
                                       Identifier skinTexture, boolean sleeveVisible, CallbackInfo ci) {
        if (ysm$renderArm(HumanoidArm.RIGHT, poseStack, submitNodeCollector, packedLight)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderLeftHand", at = @At("HEAD"), cancellable = true)
    private void ysm$onRenderLeftHand(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight,
                                      Identifier skinTexture, boolean sleeveVisible, CallbackInfo ci) {
        if (ysm$renderArm(HumanoidArm.LEFT, poseStack, submitNodeCollector, packedLight)) {
            ci.cancel();
        }
    }
}
