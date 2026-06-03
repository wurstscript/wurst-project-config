package org.wurstscript.projectconfig;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Reads a full {@code wurst.build} file into a {@link WurstProjectConfigData}.
 * <p>
 * This is the heavyweight counterpart to {@link WurstBuildConfig}: where {@code WurstBuildConfig} only line-parses
 * the two launch-affecting settings ({@code scriptMode}, {@code wc3Patch}) without any dependency, this reader parses
 * the entire document (project name, dependencies, and the nested {@code buildMapData}) using snakeyaml. It exists so
 * tooling (the compiler) can load the complete project config without depending on the setup tool.
 * <p>
 * Parsing semantics mirror the historical setup-tool loader: unknown keys are ignored, a single dependency scalar is
 * accepted in place of a list, absent values fall back to the record defaults, and a blank {@code projectName}
 * defaults to the project folder name.
 */
public final class WurstProjectConfigReader {

    private WurstProjectConfigReader() {
    }

    /**
     * Loads the given {@code wurst.build} file.
     *
     * @return the parsed config, or {@code null} if the file does not exist.
     * @throws IOException if the file exists but cannot be read or is malformed.
     */
    public static WurstProjectConfigData load(Path buildFile) throws IOException {
        if (buildFile == null || !Files.exists(buildFile)) {
            return null;
        }
        Map<?, ?> root;
        try (Reader reader = Files.newBufferedReader(buildFile)) {
            Object parsed = newYaml().load(reader);
            root = asMap(parsed);
        } catch (RuntimeException e) {
            throw new IOException("Could not read " + buildFile + ": malformed wurst.build", e);
        }
        Path parent = buildFile.toAbsolutePath().getParent();
        String folderName = parent == null || parent.getFileName() == null ? null : parent.getFileName().toString();
        return fromMap(root, folderName);
    }

    /** Loads {@code <projectRoot>/wurst.build}; returns {@code null} if it does not exist. */
    public static WurstProjectConfigData loadFromProjectRoot(Path projectRoot) throws IOException {
        if (projectRoot == null) {
            return null;
        }
        return load(projectRoot.resolve(WurstBuildConfig.FILE_NAME));
    }

    static WurstProjectConfigData fromMap(Map<?, ?> root, String fallbackProjectName) {
        String projectName = str(get(root, "projectName"));
        if (projectName.isBlank() && fallbackProjectName != null && !fallbackProjectName.isBlank()) {
            projectName = fallbackProjectName;
        }
        return new WurstProjectConfigData(
            projectName,
            stringList(get(root, "dependencies")),
            buildMapData(asMap(get(root, "buildMapData"))),
            parseEnum(ScriptMode.class, str(get(root, "scriptMode"))),
            str(get(root, "wc3Patch"))
        );
    }

    private static WurstProjectBuildMapData buildMapData(Map<?, ?> m) {
        if (m == null) {
            return null;
        }
        return new WurstProjectBuildMapData(
            str(get(m, "name")),
            str(get(m, "fileName")),
            str(get(m, "author")),
            scenarioData(asMap(get(m, "scenarioData"))),
            optionFlags(asMap(get(m, "optionsFlags"))),
            players(asList(get(m, "players"))),
            forces(asList(get(m, "forces")))
        );
    }

    private static WurstProjectBuildScenarioData scenarioData(Map<?, ?> m) {
        if (m == null) {
            return null;
        }
        return new WurstProjectBuildScenarioData(
            str(get(m, "description")),
            str(get(m, "suggestedPlayers")),
            loadingScreen(asMap(get(m, "loadingScreen")))
        );
    }

    private static WurstProjectBuildLoadingScreenData loadingScreen(Map<?, ?> m) {
        if (m == null) {
            return null;
        }
        return new WurstProjectBuildLoadingScreenData(
            str(get(m, "model")),
            str(get(m, "background")),
            str(get(m, "title")),
            str(get(m, "subTitle")),
            str(get(m, "text"))
        );
    }

    private static WurstProjectBuildOptionFlagsData optionFlags(Map<?, ?> m) {
        if (m == null) {
            return null;
        }
        return new WurstProjectBuildOptionFlagsData(
            bool(get(m, "hideMinimapPreview"), false),
            bool(get(m, "forcesFixed"), false),
            bool(get(m, "maskedAreasPartiallyVisible"), false),
            bool(get(m, "showWavesOnCliffShores"), false),
            bool(get(m, "showWavesOnRollingShores"), false),
            bool(get(m, "useItemClassificationSystem"), false)
        );
    }

    private static List<WurstProjectBuildPlayer> players(List<?> list) {
        if (list == null) {
            return null;
        }
        List<WurstProjectBuildPlayer> result = new ArrayList<>();
        for (Object o : list) {
            Map<?, ?> p = asMap(o);
            if (p == null) {
                continue;
            }
            result.add(new WurstProjectBuildPlayer(
                intVal(get(p, "id"), 0),
                strOrNull(get(p, "name")),
                parseEnum(Race.class, str(get(p, "race"))),
                parseEnum(Controller.class, str(get(p, "controller"))),
                boolOrNull(get(p, "fixedStartLoc"))
            ));
        }
        return result;
    }

    private static List<WurstProjectBuildForce> forces(List<?> list) {
        if (list == null) {
            return null;
        }
        List<WurstProjectBuildForce> result = new ArrayList<>();
        for (Object o : list) {
            Map<?, ?> f = asMap(o);
            if (f == null) {
                continue;
            }
            result.add(new WurstProjectBuildForce(
                str(get(f, "name")),
                forceFlags(asMap(get(f, "flags"))),
                intList(get(f, "playerIds"))
            ));
        }
        return result;
    }

    private static WurstProjectBuildForceFlags forceFlags(Map<?, ?> m) {
        if (m == null) {
            return null;
        }
        WurstProjectBuildForceFlags d = WurstProjectBuildForceFlags.defaults();
        return new WurstProjectBuildForceFlags(
            bool(get(m, "allied"), d.allied()),
            bool(get(m, "alliedVictory"), d.alliedVictory()),
            bool(get(m, "sharedVision"), d.sharedVision()),
            bool(get(m, "sharedControl"), d.sharedControl()),
            bool(get(m, "sharedControlAdvanced"), d.sharedControlAdvanced())
        );
    }

    // ---- snakeyaml + coercion helpers -------------------------------------------------------------------------

    private static Yaml newYaml() {
        // SafeConstructor: parse plain data only (no arbitrary object instantiation from the document).
        return new Yaml(new SafeConstructor(new LoaderOptions()));
    }

    private static Object get(Map<?, ?> map, String key) {
        return map == null ? null : map.get(key);
    }

    private static Map<?, ?> asMap(Object o) {
        return o instanceof Map<?, ?> m ? m : null;
    }

    private static List<?> asList(Object o) {
        if (o instanceof List<?> l) {
            return l;
        }
        if (o == null) {
            return null;
        }
        // Accept a single value where a list is expected.
        return Collections.singletonList(o);
    }

    /** Empty string when absent/blank. */
    private static String str(Object o) {
        return o == null ? "" : o.toString().trim();
    }

    /** Null when absent/blank (for fields whose absence is meaningful, e.g. player name). */
    private static String strOrNull(Object o) {
        String s = str(o);
        return s.isBlank() ? null : s;
    }

    private static List<String> stringList(Object o) {
        List<?> list = asList(o);
        if (list == null) {
            return List.of();
        }
        List<String> result = new ArrayList<>();
        for (Object item : list) {
            if (item != null) {
                result.add(item.toString().trim());
            }
        }
        return result;
    }

    private static List<Integer> intList(Object o) {
        List<?> list = asList(o);
        if (list == null) {
            return List.of();
        }
        List<Integer> result = new ArrayList<>();
        for (Object item : list) {
            Integer value = toInt(item);
            if (value != null) {
                result.add(value);
            }
        }
        return result;
    }

    private static boolean bool(Object o, boolean defaultValue) {
        Boolean b = boolOrNull(o);
        return b == null ? defaultValue : b;
    }

    private static Boolean boolOrNull(Object o) {
        if (o instanceof Boolean b) {
            return b;
        }
        if (o == null) {
            return null;
        }
        String s = o.toString().trim().toLowerCase(Locale.ROOT);
        if (s.equals("true")) {
            return Boolean.TRUE;
        }
        if (s.equals("false")) {
            return Boolean.FALSE;
        }
        return null;
    }

    private static int intVal(Object o, int defaultValue) {
        Integer value = toInt(o);
        return value == null ? defaultValue : value;
    }

    private static Integer toInt(Object o) {
        if (o instanceof Number n) {
            return n.intValue();
        }
        if (o == null) {
            return null;
        }
        try {
            return Integer.valueOf(o.toString().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static <E extends Enum<E>> E parseEnum(Class<E> type, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Enum.valueOf(type, value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
