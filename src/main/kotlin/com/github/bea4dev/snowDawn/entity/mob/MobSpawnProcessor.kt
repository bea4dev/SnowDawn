package com.github.bea4dev.snowDawn.entity.mob

import com.github.bea4dev.snowDawn.SnowDawn
import com.github.bea4dev.snowDawn.item.ItemRegistry
import com.github.bea4dev.snowDawn.world.WorldRegistry
import com.github.bea4dev.vanilla_source.api.entity.TickBase
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import kotlin.random.Random

object MobSpawnProcessor : TickBase {
    private const val RANGE = 64
    private const val SPAWN_INTERVAL = 200
    private const val MAX_MOB_COUNT = 1024
    private const val COLD_SLEEP_SAFE_RADIUS_SQUARED = 30.0 * 30.0
    private const val TIER_2_DISTANCE_SQUARED = 250.0 * 250.0
    private const val TIER_3_DISTANCE_SQUARED = 500.0 * 500.0
    private const val TIER_4_DISTANCE_SQUARED = 800.0 * 800.0
    private val coldSleepPosition = Vector(1.5, 64.0, 1.5)
    private var tick = 0
    var mobCount = 0

    fun init() {
        SnowDawn.ENTITY_THREAD.addEntity(this)
    }

    override fun tick() {
        tick++

        if (tick % SPAWN_INTERVAL == 0) {
            for (player in Bukkit.getOnlinePlayers()) {
                val location = player.location

                if ((location.world != WorldRegistry.SNOW_LAND &&
                        location.world != WorldRegistry.SECOND_MEGA_STRUCTURE)
                    || player.gameMode == GameMode.SPECTATOR
                ) {
                    continue
                }

                for (x in location.blockX - RANGE until location.blockX + RANGE) {
                    for (y in location.blockY - RANGE until location.blockY + RANGE) {
                        for (z in location.blockZ - RANGE until location.blockZ + RANGE) {
                            if (location.world == WorldRegistry.SNOW_LAND && isInsideColdSleepSafeArea(x, y, z)) {
                                continue
                            }

                            val block = location.world.getBlockAt(x, y, z)
                            val ground = block.getRelative(BlockFace.DOWN)
                            val up = block.getRelative(BlockFace.UP)

                            if (mobCount < MAX_MOB_COUNT && block.lightFromBlocks.toInt() == 0 && block.isPassable && ground.isSolid && up.isPassable) {
                                var random = 2000
                                if (block.lightFromSky.toInt() != 0) {
                                    random = 5000
                                }

                                if (Random.nextInt(random) == 0) {
                                    if (location.world == WorldRegistry.SNOW_LAND) {
                                        val tier = getSnowLandTier(x, y, z)
                                        val phage = Phage(block.location.add(Vector(0.5, 0.0, 0.5)), tier)
                                        phage.aiController.navigator.speed = 0.20F
                                        phage.dropItems = listOf(
                                            listOf(ItemRegistry.SCRAP.createItemStack()),
                                            listOf(
                                                ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 2 }),
                                            listOf(ItemStack(Material.POTATO)),
                                            listOf(ItemRegistry.COAL.createItemStack()),
                                            listOf(
                                                ItemRegistry.FUEL.createItemStack().also { item -> item.amount = 2 }),
                                        )
                                        phage.spawn()
                                        mobCount++
                                    } else {
                                        val phage = Phage(
                                            block.location.add(Vector(0.5, 0.0, 0.5)),
                                            PhageTier.TIER_3
                                        )
                                        phage.dropItems = listOf(
                                            listOf(
                                                ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 2 }),
                                            listOf(ItemStack(Material.POTATO)),
                                            listOf(
                                                ItemRegistry.COAL.createItemStack().also { item -> item.amount = 2 }),
                                            listOf(ItemRegistry.IRON_INGOT.createItemStack()),
                                        )
                                        phage.spawn()
                                        mobCount++
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    internal fun getSnowLandTier(x: Int, y: Int, z: Int): PhageTier {
        val horizontalDistanceSquared = x.toDouble() * x.toDouble() + z.toDouble() * z.toDouble()
        val horizontalTier = when {
            horizontalDistanceSquared >= TIER_4_DISTANCE_SQUARED -> PhageTier.TIER_4
            horizontalDistanceSquared >= TIER_3_DISTANCE_SQUARED -> PhageTier.TIER_3
            horizontalDistanceSquared >= TIER_2_DISTANCE_SQUARED -> PhageTier.TIER_2
            else -> PhageTier.TIER_1
        }
        val undergroundTier = when {
            y <= 16 -> PhageTier.TIER_3
            y <= 40 -> PhageTier.TIER_2
            else -> PhageTier.TIER_1
        }

        return if (horizontalTier.ordinal >= undergroundTier.ordinal) horizontalTier else undergroundTier
    }

    private fun isInsideColdSleepSafeArea(x: Int, y: Int, z: Int): Boolean {
        val dx = x + 0.5 - coldSleepPosition.x
        val dy = y.toDouble() - coldSleepPosition.y
        val dz = z + 0.5 - coldSleepPosition.z
        return dx * dx + dy * dy + dz * dz <= COLD_SLEEP_SAFE_RADIUS_SQUARED
    }

    override fun shouldRemove(): Boolean {
        return false
    }
}
