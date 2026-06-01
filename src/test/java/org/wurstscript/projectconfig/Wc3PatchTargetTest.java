package org.wurstscript.projectconfig;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class Wc3PatchTargetTest {
    @Test
    public void parsesAliasesAndJassHistoryNames() {
        assertTarget("pre1.29", Wc3PatchTarget.Kind.PRE_129, "1.28");
        assertTarget("v1.27b", Wc3PatchTarget.Kind.PRE_129, "1.27");
        assertTarget("TFT-v1.31.1.12173", Wc3PatchTarget.Kind.CLASSIC, "1.31");
        assertTarget("Reforged-v1.36.1.20719-w3-51d40ee", Wc3PatchTarget.Kind.REFORGED, "1.36");
        assertTarget("v2.0", Wc3PatchTarget.Kind.REFORGED, "2.0");
        assertTarget("Reforged-v2.0.4.24745", Wc3PatchTarget.Kind.REFORGED, "2.0");
        assertTarget("1.36", Wc3PatchTarget.Kind.REFORGED, "1.36");
        assertTarget("classic", Wc3PatchTarget.Kind.CLASSIC, "1.31");
    }

    @Test
    public void derivesRunCompatibilityFromVersion() {
        Wc3PatchTarget reforged = Wc3PatchTarget.parse("v2.0").orElseThrow();
        assertTrue(reforged.isReforgedOrNewer());
        assertFalse(reforged.usesClassicWindowArg());
        assertFalse(reforged.copiesRunMapToWarcraftMapDir());

        Wc3PatchTarget classic = Wc3PatchTarget.parse("v1.31").orElseThrow();
        assertFalse(classic.isReforgedOrNewer());
        assertFalse(classic.usesClassicWindowArg());
        assertTrue(classic.copiesRunMapToWarcraftMapDir());

        Wc3PatchTarget legacy = Wc3PatchTarget.parse("v1.27b").orElseThrow();
        assertTrue(legacy.usesClassicWindowArg());
        assertTrue(legacy.usesInstallDirForMaps());
    }

    @Test
    public void ignoresUnknownPatchNames() {
        assertFalse(Wc3PatchTarget.parse(null).isPresent());
        assertFalse(Wc3PatchTarget.parse("").isPresent());
        assertFalse(Wc3PatchTarget.parse("some-old-custom-value").isPresent());
    }

    private static void assertTarget(String input, Wc3PatchTarget.Kind kind, String gameVersion) {
        Wc3PatchTarget target = Wc3PatchTarget.parse(input).orElseThrow();
        assertEquals(target.kind(), kind);
        assertEquals(target.gameVersion(), gameVersion);
    }
}
