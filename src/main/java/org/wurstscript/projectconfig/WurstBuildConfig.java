package org.wurstscript.projectconfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record WurstBuildConfig(Optional<ScriptMode> scriptMode, Optional<Wc3PatchTarget> wc3Patch) {
    public static final String FILE_NAME = "wurst.build";

    public WurstBuildConfig {
        scriptMode = scriptMode == null ? Optional.empty() : scriptMode;
        wc3Patch = wc3Patch == null ? Optional.empty() : wc3Patch;
    }

    public static WurstBuildConfig empty() {
        return new WurstBuildConfig(Optional.empty(), Optional.empty());
    }

    public static WurstBuildConfig fromProject(WurstProjectConfigData projectConfig) {
        if (projectConfig == null) {
            return empty();
        }
        return new WurstBuildConfig(
            Optional.ofNullable(projectConfig.scriptMode()),
            Wc3PatchTarget.parse(projectConfig.wc3Patch())
        );
    }

    public static WurstBuildConfig fromBuildFile(Path buildFile) throws IOException {
        if (buildFile == null || !Files.exists(buildFile)) {
            return empty();
        }
        Optional<ScriptMode> scriptMode = Optional.empty();
        Optional<Wc3PatchTarget> wc3Patch = Optional.empty();
        for (String rawLine : Files.readAllLines(buildFile)) {
                String line = stripComment(rawLine).trim();
                if (line.isEmpty() || Character.isWhitespace(rawLine.charAt(0))) {
                    continue;
                }
            int colon = line.indexOf(':');
            if (colon < 0) {
                continue;
            }
            String key = line.substring(0, colon).trim();
            String value = normalizeScalar(line.substring(colon + 1).trim());
            if (key.equals("scriptMode")) {
                scriptMode = parseScriptMode(value);
            } else if (key.equals("wc3Patch")) {
                wc3Patch = Wc3PatchTarget.parse(value);
            }
        }
        return new WurstBuildConfig(scriptMode, wc3Patch);
    }

    public static WurstBuildConfig fromProjectRoot(Path projectRoot) throws IOException {
        if (projectRoot == null) {
            return empty();
        }
        return fromBuildFile(projectRoot.resolve(FILE_NAME));
    }

    public List<String> applyToCompileArgs(List<String> compileArgs) {
        if (compileArgs == null) {
            compileArgs = List.of();
        }
        if (scriptMode.isEmpty()) {
            return compileArgs;
        }
        List<String> result = new ArrayList<>();
        for (String arg : compileArgs) {
            if (!"-lua".equals(arg)) {
                result.add(arg);
            }
        }
        if (scriptMode.orElseThrow() == ScriptMode.LUA) {
            result.add("-lua");
        }
        return List.copyOf(result);
    }

    public boolean shouldUseReforgedLaunchArgs(Optional<String> detectedGameVersion) {
        return optional(detectedGameVersion)
            .map(version -> Wc3PatchTarget.compareVersions(version, "1.32") >= 0)
            .orElseGet(() -> wc3Patch.map(Wc3PatchTarget::isReforgedOrNewer).orElse(true));
    }

    public boolean shouldUseClassicWindowArg(Optional<String> detectedGameVersion) {
        return optional(detectedGameVersion)
            .map(version -> Wc3PatchTarget.compareVersions(version, "1.31") < 0)
            .orElseGet(() -> wc3Patch.map(Wc3PatchTarget::usesClassicWindowArg).orElse(false));
    }

    public boolean shouldCopyRunMapToWarcraftMapDir(Optional<String> detectedGameVersion) {
        return optional(detectedGameVersion)
            .map(version -> Wc3PatchTarget.compareVersions(version, "1.32") < 0)
            .orElseGet(() -> wc3Patch.map(Wc3PatchTarget::copiesRunMapToWarcraftMapDir).orElse(false));
    }

    public boolean shouldUseInstallDirForMaps(Optional<String> detectedGameVersion) {
        return optional(detectedGameVersion)
            .map(version -> Wc3PatchTarget.compareVersions(version, "1.27.9") <= 0)
            .orElseGet(() -> wc3Patch.map(Wc3PatchTarget::usesInstallDirForMaps).orElse(false));
    }

    private static Optional<ScriptMode> parseScriptMode(String value) {
        try {
            return Optional.of(ScriptMode.valueOf(normalizeScalar(value).toUpperCase()));
        } catch (IllegalArgumentException | NullPointerException e) {
            return Optional.empty();
        }
    }

    private static String stripComment(String line) {
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\'' && !inDoubleQuote) {
                inSingleQuote = !inSingleQuote;
            } else if (c == '"' && !inSingleQuote) {
                inDoubleQuote = !inDoubleQuote;
            } else if (c == '#' && !inSingleQuote && !inDoubleQuote) {
                return line.substring(0, i);
            }
        }
        return line;
    }

    private static String normalizeScalar(String value) {
        String result = value == null ? "" : value.trim();
        if ((result.startsWith("\"") && result.endsWith("\"")) || (result.startsWith("'") && result.endsWith("'"))) {
            result = result.substring(1, result.length() - 1);
        }
        return result.trim();
    }

    private static <T> Optional<T> optional(Optional<T> value) {
        return value == null ? Optional.empty() : value;
    }
}
