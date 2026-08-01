package com.github.bea4dev.snowDawn.player

import com.github.bea4dev.snowDawn.music.BGMProcessorRegistry
import com.github.bea4dev.snowDawn.player.PlayerManager.ONLINE_PLAYERS
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.concurrent.CopyOnWriteArrayList

object PlayerManager {
    val ONLINE_PLAYERS = CopyOnWriteArrayList<Player>()

    fun getNearestPlayer(location: Location): Player? {
        var minDistance = Double.MAX_VALUE
        var nearestPlayer: Player? = null

        for (player in ONLINE_PLAYERS) {
            val playerLocation = player.location
            if (playerLocation.world != location.world) {
                continue
            }

            val distance = playerLocation.distanceSquared(location)
            if (distance < minDistance) {
                minDistance = distance
                nearestPlayer = player
            }
        }

        return nearestPlayer
    }
}

internal class PlayerManagerListener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        ONLINE_PLAYERS.add(player)
        BGMProcessorRegistry.onPlayerJoin(player)
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        val player = event.player
        BGMProcessorRegistry.onPlayerQuit(player)
        ONLINE_PLAYERS.remove(player)
    }
}
