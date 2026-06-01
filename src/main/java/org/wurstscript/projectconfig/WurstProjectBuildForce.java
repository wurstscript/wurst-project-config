package org.wurstscript.projectconfig;

import java.util.List;

public record WurstProjectBuildForce(
    String name,
    WurstProjectBuildForceFlags flags,
    List<Integer> playerIds
) {
    public WurstProjectBuildForce {
        name = Defaults.string(name);
        flags = Defaults.value(flags, WurstProjectBuildForceFlags::defaults);
        playerIds = Defaults.list(playerIds);
    }
}
