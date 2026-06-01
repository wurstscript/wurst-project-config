package org.wurstscript.projectconfig;

import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class WurstBuildConfigTest {
    @Test
    public void readsTopLevelBuildSettingsWithoutYamlDependency() throws Exception {
        Path project = Files.createTempDirectory("wurst-project-config");
        Files.writeString(project.resolve("wurst.build"), """
            projectName: Test
            dependencies:
              - https://github.com/wurstscript/wurstStdlib2
            scriptMode: lua
            wc3Patch: TFT-v1.31.1.12173
            """);

        WurstBuildConfig config = WurstBuildConfig.fromProjectRoot(project);

        assertEquals(config.scriptMode().orElseThrow(), ScriptMode.LUA);
        assertEquals(config.wc3Patch().orElseThrow().gameVersion(), "1.31");
        assertFalse(config.shouldUseReforgedLaunchArgs(Optional.empty()));
        assertTrue(config.shouldCopyRunMapToWarcraftMapDir(Optional.empty()));
    }

    @Test
    public void compileArgsFollowScriptMode() {
        WurstBuildConfig lua = new WurstBuildConfig(
            Optional.of(ScriptMode.LUA),
            Optional.empty()
        );
        assertEquals(lua.applyToCompileArgs(List.of("-runcompiletimefunctions")), List.of("-runcompiletimefunctions", "-lua"));

        WurstBuildConfig jass = new WurstBuildConfig(
            Optional.of(ScriptMode.JASS),
            Optional.empty()
        );
        assertEquals(jass.applyToCompileArgs(List.of("-runcompiletimefunctions", "-lua")), List.of("-runcompiletimefunctions"));
    }

    @Test
    public void ignoresUnknownLegacySettings() throws Exception {
        Path project = Files.createTempDirectory("wurst-project-config-legacy");
        Files.writeString(project.resolve("wurst.build"), """
            projectName: Legacy
            oldBuildFlag: sure
            scriptMode: weird
            wc3Patch: no-longer-valid
            buildMapData:
              obsoleteNestedSetting: true
            """);

        WurstBuildConfig config = WurstBuildConfig.fromProjectRoot(project);

        assertFalse(config.scriptMode().isPresent());
        assertFalse(config.wc3Patch().isPresent());
        assertTrue(config.shouldUseReforgedLaunchArgs(Optional.empty()));
    }

    @Test
    public void keepsCommentsInsideQuotedValues() throws Exception {
        Path project = Files.createTempDirectory("wurst-project-config-comments");
        Files.writeString(project.resolve("wurst.build"), """
            scriptMode: "lua#still-a-value"
            wc3Patch: "1.36#comment-inside-quote"
            """);

        WurstBuildConfig config = WurstBuildConfig.fromProjectRoot(project);

        assertFalse(config.scriptMode().isPresent());
        assertEquals(config.wc3Patch().orElseThrow().gameVersion(), "1.36");
    }
}
