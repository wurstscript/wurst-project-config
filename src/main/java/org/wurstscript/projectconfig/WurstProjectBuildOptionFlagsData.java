package org.wurstscript.projectconfig;

public record WurstProjectBuildOptionFlagsData(
    boolean hideMinimapPreview,
    boolean forcesFixed,
    boolean maskedAreasPartiallyVisible,
    boolean showWavesOnCliffShores,
    boolean showWavesOnRollingShores,
    boolean useItemClassificationSystem,
    boolean useAlphaTileMinimapColor,
    boolean useDynamicMinimap
) {
    public WurstProjectBuildOptionFlagsData(
        boolean hideMinimapPreview,
        boolean forcesFixed,
        boolean maskedAreasPartiallyVisible,
        boolean showWavesOnCliffShores,
        boolean showWavesOnRollingShores,
        boolean useItemClassificationSystem
    ) {
        this(hideMinimapPreview, forcesFixed, maskedAreasPartiallyVisible, showWavesOnCliffShores,
            showWavesOnRollingShores, useItemClassificationSystem, false, false);
    }

    public static WurstProjectBuildOptionFlagsData empty() {
        return new WurstProjectBuildOptionFlagsData(false, false, false, false, false, false, false, false);
    }
}
