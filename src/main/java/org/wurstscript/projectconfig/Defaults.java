package org.wurstscript.projectconfig;

import java.util.List;
import java.util.function.Supplier;

final class Defaults {
    private Defaults() {
    }

    static String string(String value) {
        return string(value, "");
    }

    static String string(String value, String defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value;
    }

    static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    static <T> List<T> list(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }

    static <T> T value(T value, Supplier<T> fallback) {
        return value == null ? fallback.get() : value;
    }
}
