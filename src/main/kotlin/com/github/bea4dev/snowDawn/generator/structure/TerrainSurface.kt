package com.github.bea4dev.snowDawn.generator.structure

import org.bukkit.HeightMap
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.generator.LimitedRegion
import org.bukkit.generator.WorldInfo

internal fun LimitedRegion.findTerrainSurfaceY(worldInfo: WorldInfo, x: Int, z: Int): Int? {
    if (!isInRegion(x, worldInfo.minHeight, z)) {
        return null
    }

    val highestY = getHighestBlockYAt(x, z, HeightMap.WORLD_SURFACE)
        .coerceIn(worldInfo.minHeight, worldInfo.maxHeight - 1)
    for (y in highestY downTo worldInfo.minHeight) {
        val material = getBlockData(x, y, z).material
        if (material.isTerrainSurface()) {
            return y
        }
    }

    return null
}

internal fun LimitedRegion.findStructureTerrainSurfaceY(
    worldInfo: WorldInfo,
    minX: Int,
    maxX: Int,
    minZ: Int,
    maxZ: Int,
): Int? {
    val midX = (minX + maxX) / 2
    val midZ = (minZ + maxZ) / 2
    val samples = listOf(
        minX to minZ,
        maxX to minZ,
        minX to maxZ,
        maxX to maxZ,
        midX to midZ,
    )

    return samples
        .mapNotNull { (x, z) -> findTerrainSurfaceY(worldInfo, x, z) }
        .minOrNull()
}

private fun Material.isTerrainSurface(): Boolean {
    return this == Material.GRASS_BLOCK ||
            this == Material.PODZOL ||
            this == Material.MYCELIUM ||
            this == Material.SNOW_BLOCK ||
            this == Material.ICE ||
            this == Material.PACKED_ICE ||
            this == Material.BLUE_ICE ||
            this == Material.SAND ||
            this == Material.RED_SAND ||
            this == Material.GRAVEL ||
            Tag.DIRT.isTagged(this) ||
            Tag.BASE_STONE_OVERWORLD.isTagged(this)
}
