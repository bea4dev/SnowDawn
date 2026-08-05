package com.github.bea4dev.snowDawn.scenario.script

import com.github.bea4dev.snowDawn.camera.createCamera
import com.github.bea4dev.snowDawn.coroutine.MainThread
import com.github.bea4dev.snowDawn.coroutine.async
import com.github.bea4dev.snowDawn.coroutine.play
import com.github.bea4dev.snowDawn.item.weapon.WeaponTaskManager
import com.github.bea4dev.snowDawn.music.BGMProcessorRegistry
import com.github.bea4dev.snowDawn.scenario.DEFAULT_TEXT_BOX
import com.github.bea4dev.snowDawn.scenario.SCENARIO_TICK_THREAD
import com.github.bea4dev.snowDawn.scenario.Scenario
import com.github.bea4dev.snowDawn.scenario.getPlayerSkin
import com.github.bea4dev.snowDawn.scenario.lowSound
import com.github.bea4dev.snowDawn.text.Text
import com.github.bea4dev.snowDawn.toast.ToastKind
import com.github.bea4dev.snowDawn.toast.ToastNotification
import com.github.bea4dev.snowDawn.toast.sendToast
import com.github.bea4dev.snowDawn.world.WorldRegistry
import com.github.bea4dev.vanilla_source.api.VanillaSourceAPI
import com.github.bea4dev.vanilla_source.api.camera.CameraPositionAt
import com.github.bea4dev.vanilla_source.api.camera.CameraPositionsManager
import com.github.bea4dev.vanilla_source.api.camera.LookAtEntityTracker
import com.github.bea4dev.vanilla_source.api.entity.EngineEntity
import com.github.bea4dev.vanilla_source.api.player.EnginePlayer
import com.github.bea4dev.vanilla_source.api.text.TextBox
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import com.mojang.datafixers.util.Pair
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component
import net.minecraft.core.BlockPos
import net.minecraft.network.protocol.game.ClientboundAnimatePacket
import net.minecraft.network.protocol.game.ClientboundBlockEventPacket
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
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
    private val CAMERA_POSITION_2 = Vector(-2, 66, -1)
    private val CAMERA_POSITION_3 = Vector(-2, 64, 6)
    private val CAMERA_POSITION_4 = Vector(-1.5, 65.0, 1.5)
    private val CHEST_POSITION = Vector(0, 63, 3)
    private val CHEST_FRONT_POSITION = Vector(-0.5, 63.0, 3.5)
    private val COLD_SLEEP_FRONT_POSITION = Vector(-1, 63, 1)
    private val COLD_SLEEP_POSITION = Vector(1.5, 64.0, 1.5)

    override suspend fun run(player: Player) {
        WeaponTaskManager[player]?.enableBar?.set(false)

        delay(100.milliseconds)

        blackFeedOut(player, 1000)

        val nmsHandler = VanillaSourceAPI.getInstance().nmsHandler
        val enginePlayer = EnginePlayer.getEnginePlayer(player) ?: return

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
            player.inventory.clear()
            player.inventory.helmet = ItemStack(Material.CARVED_PUMPKIN)
        }.await()

        delay(1000.milliseconds)

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

        blackFeedOut(player, 1000)

        delay(1000.milliseconds)

        MainThread.sync {
            player.teleport(CAMERA_POSITION_2.toLocation(WorldRegistry.SNOW_LAND))
        }.await()

        delay(1500.milliseconds)

        val profile2 = GameProfile(UUID.randomUUID(), player.name)
        profile2.properties.put("textures", Property("textures", playerSkin.first, playerSkin.second))
        val npc2 = nmsHandler.createNMSEntityController(
            WorldRegistry.SNOW_LAND,
            CHEST_FRONT_POSITION.x,
            CHEST_FRONT_POSITION.y,
            CHEST_FRONT_POSITION.z,
            EntityType.PLAYER,
            profile2
        )

        val world = VanillaSourceAPI.getInstance().createUniverse(player.name).getWorld(WorldRegistry.SNOW_LAND.name)

        world.universe.addPlayer(enginePlayer)

        val npcEntity = EngineEntity(world, npc2, SCENARIO_TICK_THREAD, null)
        npcEntity.setRotation(-90.0F, 45.0F)
        npcEntity.spawn()

        // delay 2 ticks for tracker
        delay(100.milliseconds)

        val swingPacket = ClientboundAnimatePacket(npc2 as Entity, ClientboundAnimatePacket.SWING_MAIN_HAND)
        nmsHandler.sendPacket(player, swingPacket)

        val camera3 = createCamera(player)
        camera3.setCameraPositions(CameraPositionAt(CAMERA_POSITION_2))
        camera3.setLookAtPositions(LookAtEntityTracker(npcEntity))
        camera3.prepare()
        camera3.autoEnd(false)
        camera3.shake(false)

        BGMProcessorRegistry[player].disable()

        delay(1000.milliseconds)

        camera2.end()
        camera3.start()

        blackFeedIn(player, 2000)
        delay(2000.milliseconds)

        delay(500.milliseconds)

        nmsHandler.sendPacket(player, swingPacket)

        player.playSound(
            player.location,
            Sound.BLOCK_WOODEN_DOOR_OPEN,
            Float.MAX_VALUE,
            1.5F
        )

        delay(1000.milliseconds)

        nmsHandler.sendPacket(player, swingPacket)
        nmsHandler.sendPacket(
            player,
            ClientboundBlockEventPacket(
                BlockPos(CHEST_POSITION.blockX, CHEST_POSITION.blockY, CHEST_POSITION.blockZ),
                Blocks.CHEST,
                1,
                1
            )
        )

        player.playSound(
            CHEST_POSITION.toLocation(player.world),
            Sound.BLOCK_CHEST_OPEN,
            Float.MAX_VALUE,
            1.0F
        )

        delay(2000.milliseconds)

        blackFeedOut(player, 1000)
        delay(1000.milliseconds)

        val camera4 = createCamera(player)
        camera4.setCameraPositions(CameraPositionAt(CAMERA_POSITION_3))
        camera4.setLookAtPositions(CameraPositionAt(CHEST_POSITION))
        camera4.prepare()
        camera4.autoEnd(false)
        camera4.shake(false)

        camera3.end()
        camera4.start()

        delay(500.milliseconds)

        blackFeedIn(player, 2000)
        delay(1000.milliseconds)

        val npc2Entity = npc2 as LivingEntity
        val book = net.minecraft.world.item.ItemStack(Items.WRITTEN_BOOK)
        npc2Entity.setItemSlot(EquipmentSlot.MAINHAND, book)
        nmsHandler.sendPacket(
            player,
            ClientboundSetEquipmentPacket(
                npc2Entity.id,
                listOf(Pair.of(EquipmentSlot.MAINHAND, book))
            )
        )

        delay(1000.milliseconds)

        player.playSound(
            player.location,
            Sound.ITEM_BOOK_PAGE_TURN,
            Float.MAX_VALUE,
            1.0F
        )

        delay(1000.milliseconds)

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "",
            1,
            Text.CENTER_7[player, player.name]
        ).play().await()

        delay(1000.milliseconds)

        val air = net.minecraft.world.item.ItemStack(Items.AIR)
        npc2Entity.setItemSlot(EquipmentSlot.MAINHAND, air)
        nmsHandler.sendPacket(
            player,
            ClientboundSetEquipmentPacket(
                npc2Entity.id,
                listOf(Pair.of(EquipmentSlot.MAINHAND, air))
            )
        )

        delay(500.milliseconds)

        nmsHandler.sendPacket(player, swingPacket)
        nmsHandler.sendPacket(
            player,
            ClientboundBlockEventPacket(
                BlockPos(CHEST_POSITION.blockX, CHEST_POSITION.blockY, CHEST_POSITION.blockZ),
                Blocks.CHEST,
                1,
                0
            )
        )
        player.playSound(
            CHEST_POSITION.toLocation(player.world),
            Sound.BLOCK_CHEST_CLOSE,
            Float.MAX_VALUE,
            1.0F
        )

        delay(500.milliseconds)

        /*
        npcEntity.aiController.navigator.navigationGoal = BlockPosition(
            COLD_SLEEP_FRONT_POSITION.blockX,
            COLD_SLEEP_FRONT_POSITION.blockY,
            COLD_SLEEP_FRONT_POSITION.blockZ
        )*/

        blackFeedOut(player, 1000)

        delay(1000.milliseconds)

        val camera5 = createCamera(player)
        val camera5LookAtPositions = CameraPositionsManager.getCameraPositionsByName("last")
        camera5.setCameraPositions(CameraPositionAt(CAMERA_POSITION_4))
        camera5.setLookAtPositions(camera5LookAtPositions)
        camera5.prepare()
        camera5.autoEnd(false)
        camera5.shake(false)

        camera4.end()
        camera5.start()

        npcEntity.teleport(
            WorldRegistry.SNOW_LAND.name,
            COLD_SLEEP_POSITION.x,
            COLD_SLEEP_POSITION.y,
            COLD_SLEEP_POSITION.z,
            90.0F,
            0.0F
        )

        delay(500.milliseconds)

        blackFeedIn(player, 500)

        delay(500.milliseconds)

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            Text.LUCAS[player],
            1,
            Text.CENTER_8[player, player.name]
        ).lowSound().play().await()

        delay(2000.milliseconds)

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            Text.LUCAS[player],
            1,
            Text.CENTER_9[player, player.name]
        ).lowSound().play().await()

        player.playSound(
            net.kyori.adventure.sound.Sound.sound(
                Sound.MUSIC_DISC_OTHERSIDE,
                net.kyori.adventure.sound.Sound.Source.MUSIC,
                Float.MAX_VALUE,
                1.0F
            )
        )

        delay(1000.milliseconds)

        blackFeedOut(player, 2000)

        delay(2000.milliseconds)

        camera5.end()
        npcEntity.kill()
        npc.hide(null, enginePlayer)

        Ending.start(player)
    }
}
