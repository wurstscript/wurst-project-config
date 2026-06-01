package org.wurstscript.projectconfig;

import java.util.List;

public record WurstProjectBuildMapData(
    String name,
    String fileName,
    String author,
    WurstProjectBuildScenarioData scenarioData,
    WurstProjectBuildOptionFlagsData optionsFlags,
    List<WurstProjectBuildPlayer> players,
    List<WurstProjectBuildForce> forces
) {
    public WurstProjectBuildMapData {
        name = Defaults.string(name);
        fileName = Defaults.string(fileName);
        author = Defaults.string(author);
        scenarioData = Defaults.value(scenarioData, WurstProjectBuildScenarioData::empty);
        optionsFlags = Defaults.value(optionsFlags, WurstProjectBuildOptionFlagsData::empty);
        players = Defaults.list(players);
        forces = Defaults.list(forces);
    }

    public static WurstProjectBuildMapData empty() {
        return new WurstProjectBuildMapData("", "", "", null, null, List.of(), List.of());
    }
}
