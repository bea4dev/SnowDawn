package com.github.bea4dev.snowDawn.scenario.script

import com.github.bea4dev.snowDawn.camera.createCamera
import com.github.bea4dev.snowDawn.coroutine.MainThread
import com.github.bea4dev.snowDawn.coroutine.async
import com.github.bea4dev.snowDawn.coroutine.play
import com.github.bea4dev.snowDawn.scenario.DEFAULT_TEXT_BOX
import com.github.bea4dev.snowDawn.scenario.Scenario
import com.github.bea4dev.snowDawn.scenario.getPlayerSkin
import com.github.bea4dev.snowDawn.scenario.lowSound
import com.github.bea4dev.snowDawn.text.Text
import com.github.bea4dev.snowDawn.toast.ToastKind
import com.github.bea4dev.snowDawn.toast.ToastNotification
import com.github.bea4dev.snowDawn.toast.sendToast
import com.github.bea4dev.vanilla_source.api.VanillaSourceAPI
import com.github.bea4dev.vanilla_source.api.camera.CameraPositionAt
import com.github.bea4dev.vanilla_source.api.camera.CameraPositionsManager
import com.github.bea4dev.vanilla_source.api.player.EnginePlayer
import com.github.bea4dev.vanilla_source.api.text.TextBox
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component
import net.minecraft.network.protocol.game.ClientboundAnimatePacket
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket
import net.minecraft.world.entity.Entity
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

object Center : Scenario() {
    private val PLAYER_POSITION = Vector(1006.5, 109.0, 6.0)
    private val CAMERA_POSITION_0 = Vector(1010, 112, 3)
    private val CAMERA_POSITION_1 = Vector(1008.5, 109.2, 7.5)

    override suspend fun run(player: Player) {
        blackFeedOut(player, 1000)

        val nmsHandler = VanillaSourceAPI.getInstance().nmsHandler
        val enginePlayer = EnginePlayer.getEnginePlayer(player)

        val playerSkin = async { getPlayerSkin(player.uniqueId) }.await()

        val profile = GameProfile(UUID.randomUUID(), player.name)
        profile.properties.put("textures", Property("textures", playerSkin.first, playerSkin.second))
        val npc = nmsHandler.createNMSEntityController(
            player.world,
            PLAYER_POSITION.x,
            PLAYER_POSITION.y,
            PLAYER_POSITION.z,
            EntityType.PLAYER,
            profile
        )
        npc.setRotation(0.0F, 0.0F)
        npc.show(null, enginePlayer)

        val camera0 = createCamera(player)
        val camera0Positions = CameraPositionsManager.getCameraPositionsByName("center")
        camera0.setCameraPositions(camera0Positions)
        camera0.setLookAtPositions(CameraPositionAt(PLAYER_POSITION))
        camera0.prepare()
        camera0.autoEnd(false)
        camera0.shake(false)

        val camera1 = createCamera(player)
        camera1.setCameraPositions(CameraPositionAt(CAMERA_POSITION_0))
        camera1.setLookAtPositions(CameraPositionAt(PLAYER_POSITION))
        camera1.prepare()
        camera1.autoEnd(false)
        camera1.shake(false)

        val camera2 = createCamera(player)
        camera2.setCameraPositions(CameraPositionAt(CAMERA_POSITION_1))
        camera2.setLookAtPositions(CameraPositionAt(PLAYER_POSITION.clone().add(Vector(0.0, 1.5, 0.0))))
        camera2.prepare()
        camera2.autoEnd(false)
        camera2.shake(false)

        delay(1000.milliseconds)

        MainThread.sync {
            player.gameMode = GameMode.SPECTATOR
        }.await()

        blackFeedIn(player, 1000)

        camera0.play().await()

        blackFeedOut(player, 1000)

        delay(1000.milliseconds)

        camera0.end()
        camera1.start()

        blackFeedIn(player, 1000)

        delay(1000.milliseconds)

        val animationPacket = ClientboundAnimatePacket(npc as Entity, ClientboundAnimatePacket.SWING_MAIN_HAND)
        nmsHandler.sendPacket(player, animationPacket)

        delay(100.milliseconds)

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "",
            1,
            Text.CENTER_0[player]
        ).play().await()

        blackFeedOut(player, 1000)
        delay(1000.milliseconds)

        npc.setRotation(0.0F, 30.0F)
        nmsHandler.sendPacket(player, ClientboundTeleportEntityPacket(npc as Entity))

        camera1.end()
        camera2.start()

        blackFeedIn(player, 1000)
        delay(1000.milliseconds)

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "",
            1,
            Text.CENTER_1[player]
        ).play().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "",
            1,
            Text.CENTER_2[player]
        ).play().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "",
            1,
            Text.CENTER_3[player]
        ).play().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "",
            1,
            Text.CENTER_4[player]
        ).play().await()

        delay(500.milliseconds)

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "",
            1,
            Text.CENTER_5[player]
        ).play().await()

        delay(500.milliseconds)

        player.playSound(
            player.location,
            Sound.ENTITY_ARROW_HIT_PLAYER,
            Float.MAX_VALUE,
            1.5F
        )

        player.sendToast(
            ToastNotification(
                Component.translatable(Text.MESSAGE_CHEST_KEY.toString()), ItemStack(Material.TRIPWIRE_HOOK),
                ToastKind.GOAL
            )
        )

        delay(3000.milliseconds)

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            Text.LUCAS[player],
            1,
            Text.CENTER_6[player, player.name]
        ).lowSound().play().await()
    }
}