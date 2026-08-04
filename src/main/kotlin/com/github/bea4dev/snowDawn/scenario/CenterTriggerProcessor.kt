package com.github.bea4dev.snowDawn.scenario

import com.github.bea4dev.snowDawn.SnowDawn
import com.github.bea4dev.snowDawn.save.ServerData
import com.github.bea4dev.snowDawn.scenario.script.Center
import com.github.bea4dev.snowDawn.world.WorldRegistry
import org.bukkit.Bukkit
import org.bukkit.util.Vector

object CenterTriggerProcessor {
    private val center = Vector(999, 64, 8)
    private const val RADIUS_SQUARED = 25.0 * 25.0

    fun init() {
        Bukkit.getScheduler().runTaskTimer(SnowDawn.plugin, Runnable(::tick), 1L, 5L)
    }

    private fun tick() {
        if (!SnowDawn.loaded || ServerData.centerScenarioStarted) {
            return
        }

        val entered = Bukkit.getOnlinePlayers().any { player ->
            player.world == WorldRegistry.SNOW_LAND &&
                player.location.toVector().distanceSquared(center) <= RADIUS_SQUARED
        }
        if (!entered || !ServerData.markCenterScenarioStarted()) {
            return
        }

        Bukkit.getOnlinePlayers().toList().forEach(Center::start)
    }
}
