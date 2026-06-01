package org.wurstscript.projectconfig;

public record WurstProjectBuildLoadingScreenData(
    String model,
    String background,
    String title,
    String subTitle,
    String text
) {
    public WurstProjectBuildLoadingScreenData {
        model = Defaults.string(model);
        background = Defaults.string(background);
        title = Defaults.string(title);
        subTitle = Defaults.string(subTitle);
        text = Defaults.string(text);
    }
}
