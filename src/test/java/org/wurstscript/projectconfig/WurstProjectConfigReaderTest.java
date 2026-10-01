package org.wurstscript.projectconfig;

import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

public class WurstProjectConfigReaderTest {

    private static Path writeBuild(String dirPrefix, String content) throws Exception {
        Path project = Files.createTempDirectory(dirPrefix);
        Files.writeString(project.resolve("wurst.build"), content);
        return project;
    }

    @Test
    public void readsProjectNameAndDependencyList() throws Exception {
        Path project = writeBuild("wpc-basic", """
            ---
            projectName: wurst-castle-fight
            dependencies:
            - https://github.com/wurstscript/wurstStdlib2
            - https://github.com/Frotty/wurst-fsm
            buildMapData:
              name: Castle Fight DE Beta 9.0
              fileName: 5329_Castle_Fight_DE_beta9.0
              author: Frotty
            """);

        WurstProjectConfigData config = WurstProjectConfigReader.loadFromProjectRoot(project);

        assertEquals(config.projectName(), "wurst-castle-fight");
        assertEquals(config.dependencies().size(), 2);
        assertEquals(config.dependencies().get(0), "https://github.com/wurstscript/wurstStdlib2");
        assertEquals(config.buildMapData().name(), "Castle Fight DE Beta 9.0");
        assertEquals(config.buildMapData().fileName(), "5329_Castle_Fight_DE_beta9.0");
        assertEquals(config.buildMapData().author(), "Frotty");
    }

    @Test
    public void acceptsSingleDependencyScalarAsList() throws Exception {
        Path project = writeBuild("wpc-scalar-dep", """
            ---
            projectName: solo
            dependencies: https://github.com/wurstscript/wurstStdlib2
            """);

        WurstProjectConfigData config = WurstProjectConfigReader.loadFromProjectRoot(project);

        assertEquals(config.dependencies().size(), 1);
        assertEquals(config.dependencies().get(0), "https://github.com/wurstscript/wurstStdlib2");
    }

    @Test
    public void parsesScriptModeAndWc3Patch() throws Exception {
        Path project = writeBuild("wpc-settings", """
            ---
            projectName: settings
            scriptMode: lua
            wc3Patch: v2.0
            """);

        WurstProjectConfigData config = WurstProjectConfigReader.loadFromProjectRoot(project);

        assertEquals(config.scriptMode(), ScriptMode.LUA);
        assertEquals(config.wc3Patch(), "v2.0");
        // The shared build-settings view derives the same values.
        WurstBuildConfig build = WurstBuildConfig.fromProject(config);
        assertEquals(build.scriptMode().orElseThrow(), ScriptMode.LUA);
        assertEquals(build.wc3Patch().orElseThrow().gameVersion(), "2.0");
    }

    @Test
    public void parsesNestedScenarioPlayersAndForces() throws Exception {
        Path project = writeBuild("wpc-full", """
            ---
            projectName: full
            buildMapData:
              name: Gods' Arena
              gameDataVersion: FORSAKEN_KINGDOM
              scenarioData:
                description: PvE Hero Survival.
                suggestedPlayers: 4-8
                loadingScreen:
                  title: by Overkane and Frotty
                  subTitle: Gods' Arena
                  text: Survive the arena.
              optionsFlags:
                forcesFixed: true
                showWavesOnCliffShores: true
              players:
              - id: 0
                name: Player One
                race: HUMAN
                controller: USER
                fixedStartLoc: true
              forces:
              - name: Team A
                flags:
                  allied: true
                  sharedControl: true
                playerIds:
                - 0
                - 1
            """);

        WurstProjectConfigData config = WurstProjectConfigReader.loadFromProjectRoot(project);
        WurstProjectBuildMapData map = config.buildMapData();

        assertEquals(map.scenarioData().description(), "PvE Hero Survival.");
        assertEquals(map.gameDataVersion(), "FORSAKEN_KINGDOM");
        assertEquals(map.scenarioData().suggestedPlayers(), "4-8");
        assertEquals(map.scenarioData().loadingScreen().title(), "by Overkane and Frotty");
        assertTrue(map.optionsFlags().forcesFixed());
        assertTrue(map.optionsFlags().showWavesOnCliffShores());
        assertTrue(map.optionsFlags().useItemClassificationSystem() == false);

        assertEquals(map.players().size(), 1);
        WurstProjectBuildPlayer player = map.players().get(0);
        assertEquals(player.id(), 0);
        assertEquals(player.name(), "Player One");
        assertEquals(player.race(), Race.HUMAN);
        assertEquals(player.controller(), Controller.USER);
        assertEquals(player.fixedStartLoc(), Boolean.TRUE);

        assertEquals(map.forces().size(), 1);
        WurstProjectBuildForce force = map.forces().get(0);
        assertEquals(force.name(), "Team A");
        assertTrue(force.flags().allied());
        assertTrue(force.flags().sharedControl());
        // alliedVictory defaults to true when omitted, matching WurstProjectBuildForceFlags.defaults()
        assertTrue(force.flags().alliedVictory());
        assertEquals(force.playerIds(), java.util.List.of(0, 1));
    }

    @Test
    public void defaultsBlankProjectNameToFolderName() throws Exception {
        Path project = Files.createTempDirectory("wpc-folder-name-default");
        Files.writeString(project.resolve("wurst.build"), """
            ---
            dependencies: []
            """);

        WurstProjectConfigData config = WurstProjectConfigReader.loadFromProjectRoot(project);

        assertEquals(config.projectName(), project.getFileName().toString());
    }

    @Test
    public void ignoresUnknownKeysAndUsesDefaults() throws Exception {
        Path project = writeBuild("wpc-unknown", """
            ---
            projectName: legacy
            oldBuildFlag: sure
            buildMapData:
              obsoleteNestedSetting: true
            """);

        WurstProjectConfigData config = WurstProjectConfigReader.loadFromProjectRoot(project);

        assertEquals(config.projectName(), "legacy");
        assertEquals(config.buildMapData().name(), "");
        assertTrue(config.buildMapData().players().isEmpty());
        assertTrue(config.buildMapData().forces().isEmpty());
    }

    @Test
    public void returnsNullWhenFileMissing() throws Exception {
        Path project = Files.createTempDirectory("wpc-missing");
        assertNull(WurstProjectConfigReader.loadFromProjectRoot(project));
    }
}
