package com.github.bea4dev.snowDawn.scenario.script

import com.github.bea4dev.snowDawn.camera.createCamera
import com.github.bea4dev.snowDawn.coroutine.CoroutineFlagRegistry
import com.github.bea4dev.snowDawn.coroutine.MainThread
import com.github.bea4dev.snowDawn.coroutine.async
import com.github.bea4dev.snowDawn.coroutine.play
import com.github.bea4dev.snowDawn.item.ItemRegistry
import com.github.bea4dev.snowDawn.listeners.sendCraftingSlotButtons
import com.github.bea4dev.snowDawn.listeners.CompassInventory
import com.github.bea4dev.snowDawn.music.BGMProcessorRegistry
import com.github.bea4dev.snowDawn.save.PlayerDataRegistry
import com.github.bea4dev.snowDawn.scenario.DEFAULT_TEXT_BOX
import com.github.bea4dev.snowDawn.scenario.Scenario
import com.github.bea4dev.snowDawn.scenario.getPlayerSkin
import com.github.bea4dev.snowDawn.scenario.lowSound
import com.github.bea4dev.snowDawn.text.Text
import com.github.bea4dev.snowDawn.world.WorldRegistry
import com.github.bea4dev.vanilla_source.api.VanillaSourceAPI
import com.github.bea4dev.vanilla_source.api.camera.CameraPositionAt
import com.github.bea4dev.vanilla_source.api.camera.CameraPositionsManager
import com.github.bea4dev.vanilla_source.api.player.EnginePlayer
import com.github.bea4dev.vanilla_source.api.text.TextBox
import com.github.shynixn.mccoroutine.bukkit.ticks
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.GameMode
import org.bukkit.Sound
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.time.Duration
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

object Prologue : Scenario() {
    private val POSITION = Vector(-20.5, 1.0, -0.5)
    private val SPAWN_POSITION = Vector(1.5, 64.0, 1.5)
    private val LOGIN_POSITION = Vector(0.5, 0.0, 0.5)

    override suspend fun run(player: Player) {
        BGMProcessorRegistry[player].disable()

        delay(4000.milliseconds)

        MainThread.sync {
            player.gameMode = GameMode.ADVENTURE
            player.teleport(LOGIN_POSITION.toLocation(WorldRegistry.PROLOGUE))
        }.await()

        player.playSound(
            player.location,
            Sound.MUSIC_DISC_STAL,
            Float.MAX_VALUE,
            1.0F
        )

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "",
            1,
            Text.PROLOGUE_SHIFT_0[player]
        ).play().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "",
            1,
            Text.PROLOGUE_SHIFT_1[player]
        ).play().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "",
            1,
            Text.PROLOGUE_MUSIC_0[player]
        ).play().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "",
            1,
            Text.PROLOGUE_MUSIC_1[player]
        ).play().await()

        player.stopAllSounds()

        black(player)

        val playerSkin = async { getPlayerSkin(player.uniqueId) }.await()

        delay(1000.milliseconds)

        MainThread.sync {
            player.teleport(POSITION.toLocation(WorldRegistry.PROLOGUE))
            player.inventory.clear()
            sendCraftingSlotButtons(player)
        }.await()

        blackFeedIn(player, 2000)

        delay(2.seconds)

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "",
            1,
            Text.PROLOGUE_0[player]
        ).play().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "",
            1,
            Text.PROLOGUE_1[player]
        ).play().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "",
            1,
            Text.PROLOGUE_2[player]
        ).play().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "",
            1,
            Text.PROLOGUE_3[player]
        ).play().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "",
            1,
            Text.PROLOGUE_4[player]
        ).play().await()

        MainThread.sync {
            player.inventory.addItem(ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 2 })
        }.await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "",
            1,
            Text.PROLOGUE_5[player]
        ).play().await()

        CoroutineFlagRegistry.CRAFTING_WEAPON[player].future().await()
        MainThread.sync { player.inventory.close() }.await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "",
            1,
            Text.PROLOGUE_6[player]
        ).play().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "",
            1,
            Text.PROLOGUE_7[player]
        ).play().await()

        MainThread.sync {
            player.inventory.addItem(ItemRegistry.COAL.createItemStack().also { item -> item.amount = 1 })
        }.await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "",
            1,
            Text.PROLOGUE_8[player]
        ).play().await()

        CoroutineFlagRegistry.CRAFTING_TORCH[player].future().await()
        MainThread.sync { player.inventory.close() }.await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "",
            1,
            Text.PROLOGUE_CAMPFIRE[player]
        ).play().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "",
            1,
            Text.PROLOGUE_JUUYOU[player]
        ).play().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "",
            1,
            Text.PROLOGUE_9[player]
        ).play().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "",
            1,
            Text.PROLOGUE_10[player]
        ).play().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "",
            1,
            Text.PROLOGUE_11[player]
        ).play().await()

        blackFeedOut(player, 2000)

        delay(1.seconds)

        MainThread.sync {
            player.gameMode = GameMode.SPECTATOR
            player.teleport(SPAWN_POSITION.toLocation(WorldRegistry.SNOW_LAND))
        }.await()

        BGMProcessorRegistry[player].enable()

        val nmsHandler = VanillaSourceAPI.getInstance().nmsHandler
        val enginePlayer = EnginePlayer.getEnginePlayer(player)

        val profile = GameProfile(UUID.randomUUID(), player.name)
        profile.properties.put("textures", Property("textures", playerSkin.first, playerSkin.second))
        val npc = nmsHandler.createNMSEntityController(
            player.world,
            SPAWN_POSITION.x,
            SPAWN_POSITION.y,
            SPAWN_POSITION.z,
            EntityType.PLAYER,
            profile
        )
        npc.setRotation(90.0F, 0.0F)
        npc.show(null, enginePlayer)

        val camera0 = createCamera(player)
        val camera0Positions = CameraPositionsManager.getCameraPositionsByName("tutorial_0")
        camera0.setCameraPositions(camera0Positions)
        camera0.setLookAtPositions(CameraPositionAt(SPAWN_POSITION))
        camera0.prepare()
        camera0.shake(false)

        val camera1 = createCamera(player)
        val camera1Positions = CameraPositionsManager.getCameraPositionsByName("tutorial_1")
        camera1.setCameraPositions(camera1Positions)
        camera1.setLookAtPositions(CameraPositionAt(SPAWN_POSITION))
        camera1.prepare()
        camera1.shake(false)

        delay(1.seconds)

        blackFeedIn(player, 2000)

        val handle0 = camera0.play()

        delay(100.ticks.milliseconds)

        blackFeedOut(player, 100.ticks.milliseconds.toLong(DurationUnit.MILLISECONDS))

        delay(50.ticks.milliseconds)

        blackFeedIn(player, 1000)

        handle0.await()

        val handle1 = camera1.play()

        delay(5.seconds)

        player.showTitle(
            Title.title(
                Component.text("Snow Dawn").color(NamedTextColor.AQUA),
                Component.empty(),
                Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(2), Duration.ofSeconds(1))
            )
        )

        delay(3.seconds)

        blackFeedOut(player, 2000)

        npc.hide(null, enginePlayer)

        handle1.await()

        delay(2.seconds)

        MainThread.sync {
            val location = SPAWN_POSITION.toLocation(WorldRegistry.SNOW_LAND)
            location.yaw = 90.0F
            player.teleport(location)
            player.gameMode = GameMode.ADVENTURE

            player.inventory.clear()
            player.inventory.addItem(ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 2 })
            player.inventory.addItem(ItemRegistry.COAL.createItemStack().also { item -> item.amount = 1 })
            player.inventory.setItem(8, CompassInventory.createItemStack().also { item -> item.amount = 1 })
        }.await()

        blackFeedIn(player, 2000)

        delay(3.seconds)

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "?????",
            1,
            Text.PROLOGUE_12[player]
        ).lowSound().play().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "?????",
            1,
            Text.PROLOGUE_13[player]
        ).lowSound().play().await()

        CoroutineFlagRegistry.MAIN_HAND_COMPASS[player].future().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            Text.LUCAS[player],
            1,
            Text.PROLOGUE_14[player]
        ).lowSound().play().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            Text.LUCAS[player],
            1,
            Text.PROLOGUE_15[player, player.name]
        ).lowSound().play().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            Text.LUCAS[player],
            1,
            Text.PROLOGUE_16[player]
        ).lowSound().play().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            Text.LUCAS[player],
            1,
            Text.PROLOGUE_17[player]
        ).lowSound().play().await()

        MainThread.sync {
            val playerData = PlayerDataRegistry[player]
            playerData.finishedTutorial = true
            playerData.respawnLocation = SPAWN_POSITION.toLocation(WorldRegistry.SNOW_LAND)

            CompassInventory.ensure(player)
            player.gameMode = GameMode.SURVIVAL
        }.await()
    }
}
