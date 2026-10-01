package org.wurstscript.projectconfig;

import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Wc3PatchTarget(String name, Kind kind, String gameVersion) {
    private static final Pattern VERSION_PATTERN = Pattern.compile("(?:^|[^0-9])v?(\\d+\\.\\d+)", Pattern.CASE_INSENSITIVE);

    public enum Kind {
        REFORGED,
        CLASSIC,
        PRE_129
    }

    public Wc3PatchTarget {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Patch target name cannot be blank.");
        }
        if (kind == null) {
            throw new IllegalArgumentException("Patch target kind cannot be null.");
        }
        if (gameVersion == null || gameVersion.isBlank()) {
            throw new IllegalArgumentException("Patch target game version cannot be blank.");
        }
        name = name.trim();
        gameVersion = gameVersion.trim();
    }

    public static Optional<Wc3PatchTarget> parse(String input) {
        String name = normalize(input);
        if (name.isEmpty()) {
            return Optional.empty();
        }
        String lowered = name.toLowerCase(Locale.ROOT);
        if (lowered.equals("reforged") || lowered.equals("latest")) {
            return Optional.of(new Wc3PatchTarget(name, Kind.REFORGED, "1.32"));
        }
        if (lowered.equals("classic") || lowered.equals("tft")) {
            return Optional.of(new Wc3PatchTarget(name, Kind.CLASSIC, "1.31"));
        }
        if (lowered.equals("pre1.29") || lowered.equals("pre-1.29") || lowered.equals("pre_129") || lowered.equals("pre-129")) {
            return Optional.of(new Wc3PatchTarget(name, Kind.PRE_129, "1.28"));
        }

        Matcher matcher = VERSION_PATTERN.matcher(name);
        if (!matcher.find()) {
            return Optional.empty();
        }
        String gameVersion = matcher.group(1);
        return Optional.of(new Wc3PatchTarget(name, kindForVersion(gameVersion), gameVersion));
    }

    public boolean isReforgedOrNewer() {
        return compareVersions(gameVersion, "1.32") >= 0;
    }

    public boolean isReignOfChaos() {
        return name.regionMatches(true, 0, "ROC-", 0, 4);
    }

    public boolean usesClassicWindowArg() {
        return compareVersions(gameVersion, "1.31") < 0;
    }

    public boolean copiesRunMapToWarcraftMapDir() {
        return compareVersions(gameVersion, "1.32") < 0;
    }

    public boolean usesInstallDirForMaps() {
        return compareVersions(gameVersion, "1.27.9") <= 0;
    }

    private static Kind kindForVersion(String version) {
        if (compareVersions(version, "1.29") < 0) {
            return Kind.PRE_129;
        }
        if (compareVersions(version, "1.32") < 0) {
            return Kind.CLASSIC;
        }
        return Kind.REFORGED;
    }

    private static String normalize(String input) {
        String value = input == null ? "" : input.trim();
        if ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'"))) {
            value = value.substring(1, value.length() - 1);
        }
        return value.trim();
    }

    static int compareVersions(String a, String b) {
        String[] left = a.split("\\.");
        String[] right = b.split("\\.");
        int length = Math.max(left.length, right.length);
        for (int i = 0; i < length; i++) {
            int l = i < left.length ? parsePart(left[i]) : 0;
            int r = i < right.length ? parsePart(right[i]) : 0;
            if (l != r) {
                return Integer.compare(l, r);
            }
        }
        return 0;
    }

    private static int parsePart(String value) {
        Matcher matcher = Pattern.compile("\\d+").matcher(value);
        if (!matcher.find()) {
            return 0;
        }
        try {
            return Integer.parseInt(matcher.group());
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }
}
