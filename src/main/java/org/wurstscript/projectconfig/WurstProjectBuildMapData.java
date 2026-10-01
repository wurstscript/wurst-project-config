package org.wurstscript.projectconfig;

import java.util.List;

public record WurstProjectBuildMapData(
    String name,
    String fileName,
    String author,
    WurstProjectBuildScenarioData scenarioData,
    WurstProjectBuildOptionFlagsData optionsFlags,
    List<WurstProjectBuildPlayer> players,
    List<WurstProjectBuildForce> forces,
    String gameDataVersion,
    WurstProjectBuildW3IV39Data w3iV39
) {
    /** Compatibility constructor for consumers that do not configure a W3I game-data version. */
    public WurstProjectBuildMapData(
        String name,
        String fileName,
        String author,
        WurstProjectBuildScenarioData scenarioData,
        WurstProjectBuildOptionFlagsData optionsFlags,
        List<WurstProjectBuildPlayer> players,
        List<WurstProjectBuildForce> forces
    ) {
        this(name, fileName, author, scenarioData, optionsFlags, players, forces, null, null);
    }

    /** Compatibility constructor for consumers that configure game-data version only. */
    public WurstProjectBuildMapData(
        String name,
        String fileName,
        String author,
        WurstProjectBuildScenarioData scenarioData,
        WurstProjectBuildOptionFlagsData optionsFlags,
        List<WurstProjectBuildPlayer> players,
        List<WurstProjectBuildForce> forces,
        String gameDataVersion
    ) {
        this(name, fileName, author, scenarioData, optionsFlags, players, forces, gameDataVersion, null);
    }

    public WurstProjectBuildMapData {
        name = Defaults.string(name);
        fileName = Defaults.string(fileName);
        author = Defaults.string(author);
        scenarioData = Defaults.value(scenarioData, WurstProjectBuildScenarioData::empty);
        optionsFlags = Defaults.value(optionsFlags, WurstProjectBuildOptionFlagsData::empty);
        players = Defaults.list(players);
        forces = Defaults.list(forces);
        gameDataVersion = Defaults.blankToNull(gameDataVersion);
        w3iV39 = Defaults.value(w3iV39, WurstProjectBuildW3IV39Data::empty);
    }

    public static WurstProjectBuildMapData empty() {
        return new WurstProjectBuildMapData("", "", "", null, null, List.of(), List.of(), null, null);
    }
}
