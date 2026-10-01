# wurst-project-config

Shared Java model and parsing helpers for `wurst.build`.

This library intentionally stays small:

- Java records for the project DTOs
- no runtime YAML dependency for the top-level build settings parser
- shared WC3 patch target parsing for Grill and the compiler
- `buildMapData.gameDataVersion` and `buildMapData.v3ReforgedData` for Warcraft III Reforged 3 map settings
- lenient parsing: unknown build fields and unsupported legacy values are ignored
- Java 17 bytecode, built with a Java 17 toolchain

## Warcraft III Reforged 3 map settings

Map settings introduced by Warcraft III Reforged 3 can be configured under
`buildMapData.v3ReforgedData`. Setting any of these values, a player's `hudSkin`, or
the new option flags makes Wurst write the map data required by Reforged 3. The compiler rejects
these settings when the pinned `wc3Patch` is older than 3.0.

```yaml
buildMapData:
  gameDataVersion: FORSAKEN_KINGDOM
  v3ReforgedData:
    loadingScreenCrestRace: 4
    terrainFogStyle: 2
    drawTerrainFogOverSky: true
    terrainFogLinearStart: 1200.5
    terrainFogLinearEnd: 4000.25
    terrainFogMaxOpacity: 0.75
    terrainFogHeight: 80
    waterMinOpacity: 4
    waterMaxOpacity: 96
    waterReflectivity: 12
    waterEmissivity: 3
    waterEdgeSoftness: 42
    waterWavesVertexDisplacement: 15
    waterWavesNormalMapStrength: 90
    waterOverrideColor: 112233
    waterEnvMapReflectivity: 77
    waterUnknown: -1
  players:
    - id: 0
      hudSkin: 64
  optionsFlags:
    useAlphaTileMinimapColor: true
    useDynamicMinimap: true
```

`hudSkin` and the numeric settings are raw values from the map.

## Build

```sh
./gradlew test
```

## Publishing

The Gradle `maven-publish` setup works with JitPack tags:

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.wurstscript:wurst-project-config:<tag>'
}
```
