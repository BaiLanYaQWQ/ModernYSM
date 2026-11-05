package com.elfmcys.yesstevemodel.util;

/**
 * 鼠标键编号常量（GLFW 域）。
 *
 * <p>26.2 沿用 1.20.1 的 GLFW 语义：0=左键、1=右键、2=中键，侧键 3..7。
 * 26.3 换 SDL 后是 1=左、2=中、3=右（侧键 4..8）——那个编号只适用于 26.3 分支，
 * 26.2/26.1.2 必须使用本文件的 GLFW 编号，否则左键判定失效、右键分支被左键误命中。
 */
public final class MouseButtons {

    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int MIDDLE = 2;

    private MouseButtons() {
    }
}
