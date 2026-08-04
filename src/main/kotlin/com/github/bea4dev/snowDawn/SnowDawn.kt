package com.github.bea4dev.snowDawn

import com.github.bea4dev.snowDawn.biome.BiomeRegistry
import com.github.bea4dev.snowDawn.dimension.DimensionRegistry
import com.github.bea4dev.snowDawn.entity.mob.MobSpawnProcessor
import com.github.bea4dev.snowDawn.furnace.FurnaceRecipe
import com.github.bea4dev.snowDawn.generator.GeneratorRegistry
import com.github.bea4dev.snowDawn.listeners.BlockListener
import com.github.bea4dev.snowDawn.listeners.BluePrintListener
import com.github.bea4dev.snowDawn.listeners.ChestOpenListener
import com.github.bea4dev.snowDawn.listeners.ChunkListener
import com.github.bea4dev.snowDawn.listeners.CompassListener
import com.github.bea4dev.snowDawn.listeners.CraftListener
import com.github.bea4dev.snowDawn.listeners.DiggingListener
import com.github.bea4dev.snowDawn.listeners.EntranceDoorListener
import com.github.bea4dev.snowDawn.listeners.FurnaceListener
import com.github.bea4dev.snowDawn.listeners.InventoryListener
import com.github.bea4dev.snowDawn.listeners.ItemReplaceListener
import com.github.bea4dev.snowDawn.listeners.PlayerJoinQuitListener
import com.github.bea4dev.snowDawn.listeners.PlayerRespawnListener
import com.github.bea4dev.snowDawn.listeners.StoryMemoListener
import com.github.bea4dev.snowDawn.listeners.WeaponListener
import com.github.bea4dev.snowDawn.player.PlayerManagerListener
import com.github.bea4dev.snowDawn.save.PlayerDataRegistry
import com.github.bea4dev.snowDawn.save.ServerData
import com.github.bea4dev.snowDawn.scenario.CenterTriggerProcessor
import com.github.bea4dev.snowDawn.world.WorldRegistry
import com.github.bea4dev.vanilla_source.api.VanillaSourceAPI
import com.github.bea4dev.vanilla_source.api.entity.tick.TickThread
import com.github.shynixn.mccoroutine.bukkit.ShutdownStrategy
import com.github.shynixn.mccoroutine.bukkit.mcCoroutineConfiguration
import com.github.shynixn.mccoroutine.bukkit.scope
import java.time.Duration
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.spigotmc.SpigotConfig

class SnowDawn : JavaPlugin() {

    companion object {
        lateinit var ENTITY_THREAD: TickThread
            private set
        lateinit var plugin: SnowDawn
            private set
        var loaded: Boolean = false
            private set
    }

    override fun onEnable() {
        plugin = this

        ENTITY_THREAD = VanillaSourceAPI.getInstance().tickThreadPool.nextTickThread

        DimensionRegistry.init()
        BiomeRegistry.init()
        GeneratorRegistry.init(0)
        WorldRegistry.init()
        MobSpawnProcessor.init()
        FurnaceRecipe.init()
        CenterTriggerProcessor.init()

        // ワールドのロードがBukkitRunnableで行われるのでそれに合わせて少し実行を遅らせる
        Bukkit.getScheduler().runTaskLater(this, Runnable {
            ServerData.load()
            PlayerDataRegistry.loadAll()

            loaded = true
        }, 1)

        val pluginManager = Bukkit.getPluginManager()
        pluginManager.registerEvents(PlayerJoinQuitListener(), this)
        pluginManager.registerEvents(ChunkListener(), this)
        pluginManager.registerEvents(CompassListener(), this)
        pluginManager.registerEvents(PlayerManagerListener(), this)
        pluginManager.registerEvents(WeaponListener(), this)
        pluginManager.registerEvents(CraftListener(), this)
        pluginManager.registerEvents(BlockListener(), this)
        pluginManager.registerEvents(ItemReplaceListener(), this)
        pluginManager.registerEvents(PlayerRespawnListener(), this)
        pluginManager.registerEvents(InventoryListener(), this)
        pluginManager.registerEvents(EntranceDoorListener(), this)
        pluginManager.registerEvents(ChestOpenListener(), this)
        pluginManager.registerEvents(StoryMemoListener(), this)
        pluginManager.registerEvents(BluePrintListener(), this)
        pluginManager.registerEvents(FurnaceListener(), this)
        pluginManager.registerEvents(DiggingListener(), this)

        SpigotConfig.disabledAdvancements.add("*")

        this.mcCoroutineConfiguration.shutdownStrategy = ShutdownStrategy.MANUAL
    }

    override fun onDisable() {
        PlayerDataRegistry.saveAll()
        ServerData.save()

        for (player in Bukkit.getOnlinePlayers()) {
            player.sendMessage(
                Component.text("[Server] ")
                    .append(
                        Component.text("Starting shutdown...")
                            .color(NamedTextColor.YELLOW)
                    )
            )
        }

        // shutdown coroutine jobs
        runBlocking {
            withTimeout(Duration.ofSeconds(10).toMillis()) {
                this@SnowDawn.scope.coroutineContext[Job]!!.children.forEach { childJob ->
                    childJob.join()
                }
            }
        }

        this.mcCoroutineConfiguration.disposePluginSession()
    }
}
