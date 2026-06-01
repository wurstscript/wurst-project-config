package org.wurstscript.projectconfig;

public record WurstProjectBuildPlayer(
    int id,
    String name,
    Race race,
    Controller controller,
    Boolean fixedStartLoc
) {
}
