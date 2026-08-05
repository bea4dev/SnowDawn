package com.github.bea4dev.snowDawn.listeners

import com.github.bea4dev.snowDawn.SnowDawn
import com.github.bea4dev.snowDawn.item.ItemRegistry
import com.github.bea4dev.snowDawn.item.weapon.PlayerWeaponTask
import com.github.bea4dev.snowDawn.player.PlayerTask
import com.github.bea4dev.snowDawn.save.PlayerDataRegistry
import com.github.bea4dev.snowDawn.scenario.MoviePlayerManager
import com.github.bea4dev.snowDawn.scenario.script.Center
import com.github.bea4dev.snowDawn.scenario.script.Prologue
import com.github.bea4dev.snowDawn.world.WorldRegistry
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAnimationEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.util.Vector

private val LOGIN_POSITION = Vector(0.5, 0.0, 0.5)

internal class PlayerJoinQuitListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        // ロード前に参加するのをキャンセル
        if (!SnowDawn.loaded) {
            event.player.kick(Component.text("Plugin is not loaded yet. Please try again.").color(NamedTextColor.AQUA))
            return
        }

        val player = event.player

        registerPacketListener(player)

        //player.teleport(Location(WorldRegistry.ASSET, 0.5, 1.0, 0.5))
        //player.teleport(Location(WorldRegistry.SNOW_LAND, 0.5, 330.0, 0.5))

        PlayerWeaponTask(player).runTaskTimer(SnowDawn.plugin, 0, 1)
        PlayerTask(player).start()

        player.teleport(LOGIN_POSITION.toLocation(WorldRegistry.PROLOGUE))
        //player.teleport(Location(world, 0.5, 240.0, 0.5))

        val playerData = PlayerDataRegistry[player]
        PlayerDataRegistry.load(playerData)

        if (playerData.finishedTutorial) {
            CompassInventory.ensure(player)
            player.teleport(playerData.lastLocation)
        } else {
            Prologue.start(player)
        }
        //player.teleport(Location(WorldRegistry.SNOW_LAND, 0.0, 0.0, 0.0))
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        MoviePlayerManager.onStopPlaying(player)
    }

    @EventHandler
    fun onPlayerClick(event: PlayerAnimationEvent) {
        val player = event.player

        if (player.isSneaking) {
            //val phage = Phage(player.location, 20.0F, 5.0F)
            //phage.block = Material.DEEPSLATE
            //phage.spawn()
        }
    }

}
