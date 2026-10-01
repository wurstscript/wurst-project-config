package org.wurstscript.projectconfig;

/** Optional map metadata introduced by W3I format 39. */
public record WurstProjectBuildW3IV39Data(
    Integer loadingScreenCrestRace,
    Integer terrainFogStyle,
    Boolean drawTerrainFogOverSky,
    Float terrainFogLinearStart,
    Float terrainFogLinearEnd,
    Float terrainFogMaxOpacity,
    Float terrainFogHeight,
    Integer waterMinOpacity,
    Integer waterMaxOpacity,
    Integer waterReflectivity,
    Integer waterEmissivity,
    Integer waterEdgeSoftness,
    Integer waterWavesVertexDisplacement,
    Integer waterWavesNormalMapStrength,
    Integer waterOverrideColor,
    Integer waterEnvMapReflectivity,
    Integer waterUnknown
) {
    public static WurstProjectBuildW3IV39Data empty() {
        return new WurstProjectBuildW3IV39Data(null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null);
    }

    public boolean isConfigured() {
        return loadingScreenCrestRace != null
            || terrainFogStyle != null
            || drawTerrainFogOverSky != null
            || terrainFogLinearStart != null
            || terrainFogLinearEnd != null
            || terrainFogMaxOpacity != null
            || terrainFogHeight != null
            || waterMinOpacity != null
            || waterMaxOpacity != null
            || waterReflectivity != null
            || waterEmissivity != null
            || waterEdgeSoftness != null
            || waterWavesVertexDisplacement != null
            || waterWavesNormalMapStrength != null
            || waterOverrideColor != null
            || waterEnvMapReflectivity != null
            || waterUnknown != null;
    }
}
