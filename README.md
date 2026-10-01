# wurst-project-config

Shared Java model and parsing helpers for `wurst.build`.

This library intentionally stays small:

- Java records for the project DTOs
- no runtime YAML dependency for the top-level build settings parser
- shared WC3 patch target parsing for Grill and the compiler
- `buildMapData.gameDataVersion` for selecting the W3I game-data version (for example `FORSAKEN_KINGDOM`)
- lenient parsing: unknown build fields and unsupported legacy values are ignored
- Java 17 bytecode, built with a Java 17 toolchain

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
