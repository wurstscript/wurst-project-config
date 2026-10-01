package org.wurstscript.projectconfig;

public record WurstProjectBuildPlayer(
    int id,
    String name,
    Race race,
    Controller controller,
    Boolean fixedStartLoc,
    Integer hudSkin
) {
    public WurstProjectBuildPlayer(int id, String name, Race race, Controller controller, Boolean fixedStartLoc) {
        this(id, name, race, controller, fixedStartLoc, null);
    }
}
