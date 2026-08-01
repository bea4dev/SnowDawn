package com.github.bea4dev.snowDawn.generator.structure

import java.util.Random
import org.bukkit.Material
import org.bukkit.generator.BlockPopulator
import org.bukkit.generator.LimitedRegion
import org.bukkit.generator.WorldInfo

private const val WALL_DISTANCE = 800

class SnowLandBoundary : BlockPopulator() {
    override fun populate(
        worldInfo: WorldInfo,
        random: Random,
        chunkX: Int,
        chunkZ: Int,
        limitedRegion: LimitedRegion,
    ) {
        val chunkMinX = chunkX * 16
        val chunkMinZ = chunkZ * 16

        for (localX in 0 until 16) {
            val worldX = chunkMinX + localX
            for (localZ in 0 until 16) {
                val worldZ = chunkMinZ + localZ

                if (isOutsideBoundary(worldX, worldZ)) {
                    for (y in worldInfo.minHeight until worldInfo.maxHeight) {
                        limitedRegion.setType(worldX, y, worldZ, Material.TUFF)
                    }
                }
            }
        }
    }

    private fun isOutsideBoundary(x: Int, z: Int): Boolean {
        return x <= -WALL_DISTANCE || x >= WALL_DISTANCE ||
            z <= -WALL_DISTANCE || z >= WALL_DISTANCE
    }
}
