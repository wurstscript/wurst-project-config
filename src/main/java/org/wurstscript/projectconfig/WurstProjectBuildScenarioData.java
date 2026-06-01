package org.wurstscript.projectconfig;

public record WurstProjectBuildScenarioData(
    String description,
    String suggestedPlayers,
    WurstProjectBuildLoadingScreenData loadingScreen
) {
    public WurstProjectBuildScenarioData {
        description = Defaults.string(description);
        suggestedPlayers = Defaults.string(suggestedPlayers);
    }

    public static WurstProjectBuildScenarioData empty() {
        return new WurstProjectBuildScenarioData("", "", null);
    }
}
