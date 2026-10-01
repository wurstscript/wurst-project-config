# wurst-project-config

Shared Java model and parsing helpers for `wurst.build`.

This library intentionally stays small:

- Java records for the project DTOs
- no runtime YAML dependency for the top-level build settings parser
- shared WC3 patch target parsing for Grill and the compiler
- `buildMapData.gameDataVersion` and `buildMapData.w3iV39` for W3I game-data selection and format-39 metadata
- lenient parsing: unknown build fields and unsupported legacy values are ignored
- Java 17 bytecode, built with a Java 17 toolchain

## W3I format 39 settings

W3I fields introduced by Warcraft III 3.0 can be configured under
`buildMapData.w3iV39`. Setting any of these values, a player's `hudSkin`, or
the v39 option flags promotes the map's W3I to format 39. The compiler rejects
these settings when the pinned `wc3Patch` is older than 3.0.

```yaml
buildMapData:
  gameDataVersion: FORSAKEN_KINGDOM
  w3iV39:
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

`hudSkin` and the numeric metadata fields are raw values from the W3I file.

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
