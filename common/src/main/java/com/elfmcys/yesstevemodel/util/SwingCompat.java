package com.elfmcys.yesstevemodel.util;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;

/**
 * 挥动状态兼容层。
 *
 * <p>26.3 移除了 {@code swinging/swingTime/swingingArm} 公开字段，统一走
 * {@code getCurrentSwing()} + {@code SwingAnimation} 组件；26.2 仍是 1.20.x 的字段语义
 * （{@code swingTime} 从 0 递增，0 = 刚开始挥动）。此处按 26.2 的真实字段实现，
 * 对外保持与移植期一致的语义（未挥动时 {@link #getSwingArm} 返回 null、{@link #getSwingTime} 返回 -1）。
 */
public class SwingCompat {

    private SwingCompat() {
    }

    public static boolean isSwinging(LivingEntity entity) {
        return entity.swinging;
    }

    /**
     * 返回当前挥动手，未挥动时返回 null（等价旧 swingingArm 的"未挥动"语义）。
     */
    public static InteractionHand getSwingArm(LivingEntity entity) {
        return entity.swinging ? entity.swingingArm : null;
    }

    /**
     * 挥动进度计数：0 = 刚开始挥动（与 1.20.x {@code swingTime} 语义相同），未挥动返回 -1。
     */
    public static int getSwingTime(LivingEntity entity) {
        return entity.swinging ? entity.swingTime : -1;
    }

    public static boolean startSwing(LivingEntity entity, InteractionHand hand) {
        entity.swing(hand, false);
        return true;
    }
}
