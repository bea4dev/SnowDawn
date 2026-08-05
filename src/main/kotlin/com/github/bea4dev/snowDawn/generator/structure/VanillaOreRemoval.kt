package com.github.bea4dev.snowDawn.generator.structure

import org.bukkit.Material
import org.bukkit.generator.BlockPopulator
import org.bukkit.generator.LimitedRegion
import org.bukkit.generator.WorldInfo
import java.util.Random

class VanillaOreRemoval : BlockPopulator() {
    override fun populate(
        worldInfo: WorldInfo,
        random: Random,
        chunkX: Int,
        chunkZ: Int,
        limitedRegion: LimitedRegion,
    ) {
        val minX = chunkX shl 4
        val minZ = chunkZ shl 4

        for (x in minX..(minX + 15)) {
            for (z in minZ..(minZ + 15)) {
                for (y in worldInfo.minHeight until worldInfo.maxHeight) {
                    val replacement = when (limitedRegion.getBlockData(x, y, z).material) {
                        Material.REDSTONE_ORE,
                        Material.LAPIS_ORE -> Material.STONE
                        Material.DEEPSLATE_REDSTONE_ORE,
                        Material.DEEPSLATE_LAPIS_ORE -> Material.DEEPSLATE
                        else -> continue
                    }

                    limitedRegion.setBlockData(x, y, z, replacement.createBlockData())
                }
            }
        }
    }
}
