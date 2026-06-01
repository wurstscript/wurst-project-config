package org.wurstscript.projectconfig;

public record WurstProjectBuildForceFlags(
    boolean allied,
    boolean alliedVictory,
    boolean sharedVision,
    boolean sharedControl,
    boolean sharedControlAdvanced
) {
    public static WurstProjectBuildForceFlags defaults() {
        return new WurstProjectBuildForceFlags(true, true, true, false, false);
    }
}
