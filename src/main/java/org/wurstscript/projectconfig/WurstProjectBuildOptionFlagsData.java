package org.wurstscript.projectconfig;

public record WurstProjectBuildOptionFlagsData(
    boolean hideMinimapPreview,
    boolean forcesFixed,
    boolean maskedAreasPartiallyVisible,
    boolean showWavesOnCliffShores,
    boolean showWavesOnRollingShores,
    boolean useItemClassificationSystem
) {
    public static WurstProjectBuildOptionFlagsData empty() {
        return new WurstProjectBuildOptionFlagsData(false, false, false, false, false, false);
    }
}
