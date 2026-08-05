package com.github.bea4dev.snowDawn.scenario.script

import com.github.bea4dev.snowDawn.camera.createCamera
import com.github.bea4dev.snowDawn.coroutine.MainThread
import com.github.bea4dev.snowDawn.coroutine.play
import com.github.bea4dev.snowDawn.scenario.CreditTimeline
import com.github.bea4dev.snowDawn.scenario.DEFAULT_TEXT_BOX
import com.github.bea4dev.snowDawn.scenario.Scenario
import com.github.bea4dev.snowDawn.text.Text
import com.github.bea4dev.snowDawn.world.WorldRegistry
import com.github.bea4dev.vanilla_source.api.camera.CameraPositionAt
import com.github.bea4dev.vanilla_source.api.camera.CameraPositionsManager
import com.github.bea4dev.vanilla_source.api.text.TextBox
import kotlinx.coroutines.delay
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

object Ending: Scenario() {
    private val LOGIN_POSITION = Vector(0.5, 0.0, 0.5)
    private val CAMERA_LOOK_AT_POSITION = Vector(1006.5, 109.0, 6.0)

    override suspend fun run(player: Player) {
        MainThread.sync {
            player.teleport(LOGIN_POSITION.toLocation(WorldRegistry.SNOW_LAND))
        }.await()

        val camera = createCamera(player)
        val cameraPositions = CameraPositionsManager.getCameraPositionsByName("ending3")
        camera.setCameraPositions(cameraPositions)
        camera.setLookAtPositions(CameraPositionAt(CAMERA_LOOK_AT_POSITION))
        camera.prepare()
        camera.autoEnd(false)
        camera.shake(false)

        delay(18.seconds)

        camera.start()
        clearBlack(player)

        delay(100.milliseconds)

        val credits = CreditTimeline(player, 174.seconds)

        credits.show(
            Text.CREDIT_SCENARIO_AND_WORLD,
            Text.CREDIT_BEA4DEV,
            NamedTextColor.AQUA,
            NamedTextColor.WHITE,
            21_125.milliseconds,
        ).await()
        credits.show(
            Text.CREDIT_DEVELOPMENT,
            Text.CREDIT_BEA4DEV,
            NamedTextColor.GOLD,
            NamedTextColor.WHITE,
            21_125.milliseconds,
        ).await()
        credits.show(
            Text.CREDIT_MODELING,
            Text.CREDIT_BEA4DEV,
            NamedTextColor.LIGHT_PURPLE,
            NamedTextColor.WHITE,
            21_125.milliseconds,
        ).await()
        credits.show(
            Text.CREDIT_INTERNAL_PLUGINS,
            Text.CREDIT_INTERNAL_PLUGIN_NAMES,
            NamedTextColor.GREEN,
            NamedTextColor.WHITE,
            21_125.milliseconds,
        ).await()
        credits.show(
            Text.CREDIT_EXTERNAL_PLUGINS,
            Text.CREDIT_EXTERNAL_PLUGIN_NAMES,
            NamedTextColor.BLUE,
            NamedTextColor.WHITE,
            21_125.milliseconds,
        ).await()
        credits.show(
            Text.CREDIT_SERVER_PLATFORM,
            Text.CREDIT_CHIYOGAMI,
            NamedTextColor.DARK_AQUA,
            NamedTextColor.WHITE,
            21_125.milliseconds,
        ).await()
        credits.show(
            Text.CREDIT_LEVEL_DESIGN,
            Text.CREDIT_BEA4DEV,
            NamedTextColor.YELLOW,
            NamedTextColor.WHITE,
            21_125.milliseconds,
        ).await()
        credits.show(
            Text.CREDIT_MINECRAFT,
            Text.CREDIT_MOJANG_STUDIOS,
            NamedTextColor.GRAY,
            NamedTextColor.WHITE,
            21_125.milliseconds,
        ).await()
        credits.show(
            Text.EMPTY,
            Text.CREDIT_THANK_YOU,
            NamedTextColor.WHITE,
            NamedTextColor.GOLD,
            5.seconds,
        ).await()

        credits.finish().await()

        blackFeedOut(player, 4000)
        delay(6000.milliseconds)

        camera.end()

        delay(500.milliseconds)

        MainThread.sync {
            player.gameMode = GameMode.ADVENTURE
            player.teleport(LOGIN_POSITION.toLocation(WorldRegistry.PROLOGUE))
            player.inventory.clear()
        }.await()

        delay(1000.milliseconds)

        clearBlack(player)

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "",
            1,
            Text.CENTER_10[player]
        ).play().await()
    }
}
