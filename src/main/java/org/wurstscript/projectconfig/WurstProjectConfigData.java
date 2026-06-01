package org.wurstscript.projectconfig;

import java.util.List;

public record WurstProjectConfigData(
    String projectName,
    List<String> dependencies,
    WurstProjectBuildMapData buildMapData,
    ScriptMode scriptMode,
    String wc3Patch
) {
    public WurstProjectConfigData {
        projectName = Defaults.string(projectName, "unnamed");
        dependencies = Defaults.list(dependencies);
        buildMapData = Defaults.value(buildMapData, WurstProjectBuildMapData::empty);
        wc3Patch = Defaults.blankToNull(wc3Patch);
    }

    public static WurstProjectConfigData empty() {
        return new WurstProjectConfigData("unnamed", List.of(), null, null, null);
    }
}
