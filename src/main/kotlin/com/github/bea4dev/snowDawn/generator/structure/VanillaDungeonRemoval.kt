package com.github.bea4dev.snowDawn.generator.structure

import org.bukkit.Material
import org.bukkit.block.CreatureSpawner
import org.bukkit.generator.BlockPopulator
import org.bukkit.generator.LimitedRegion
import org.bukkit.generator.WorldInfo
import java.util.Random
import kotlin.math.sign

class VanillaDungeonRemoval : BlockPopulator() {
    override fun populate(
        worldInfo: WorldInfo,
        random: Random,
        chunkX: Int,
        chunkZ: Int,
        limitedRegion: LimitedRegion,
    ) {
        val minX = chunkX shl 4
        val minZ = chunkZ shl 4
        val spawners = limitedRegion.tileEntities
            .filterIsInstance<CreatureSpawner>()
            .filter { spawner -> spawner.x in minX..(minX + 15) && spawner.z in minZ..(minZ + 15) }

        for (spawner in spawners) {
            val bounds = findDungeonBounds(limitedRegion, spawner.x, spawner.y, spawner.z) ?: continue
            removeDungeon(limitedRegion, spawner.x, spawner.y, spawner.z, bounds)
        }
    }

    private fun findDungeonBounds(region: LimitedRegion, x: Int, y: Int, z: Int): DungeonBounds? {
        return DUNGEON_BOUNDS.firstOrNull { bounds ->
            var dungeonFloorBlocks = 0
            var mossyFloorBlocks = 0
            var floorSize = 0

            for (dx in -bounds.radiusX..bounds.radiusX) {
                for (dz in -bounds.radiusZ..bounds.radiusZ) {
                    if (!region.isInRegion(x + dx, y - 1, z + dz)) {
                        return@firstOrNull false
                    }

                    floorSize++
                    when (region.getBlockData(x + dx, y - 1, z + dz).material) {
                        Material.COBBLESTONE -> dungeonFloorBlocks++
                        Material.MOSSY_COBBLESTONE -> {
                            dungeonFloorBlocks++
                            mossyFloorBlocks++
                        }
                        else -> Unit
                    }
                }
            }

            mossyFloorBlocks > 0 && dungeonFloorBlocks * 100 >= floorSize * MIN_FLOOR_MATCH_PERCENT
        }
    }

    private fun removeDungeon(
        region: LimitedRegion,
        centerX: Int,
        centerY: Int,
        centerZ: Int,
        bounds: DungeonBounds,
    ) {
        val openings = findOpenings(region, centerX, centerY, centerZ, bounds)

        for (dx in -bounds.radiusX..bounds.radiusX) {
            for (dy in -1..3) {
                for (dz in -bounds.radiusZ..bounds.radiusZ) {
                    val x = centerX + dx
                    val y = centerY + dy
                    val z = centerZ + dz
                    if (!region.isInRegion(x, y, z)) {
                        continue
                    }

                    region.setBlockData(x, y, z, naturalStoneAt(y).createBlockData())
                }
            }
        }

        openings.forEach { opening ->
            carvePassage(region, opening.x, opening.z, centerX, centerY, centerZ)
        }
        region.setBlockData(centerX, centerY, centerZ, Material.CAVE_AIR.createBlockData())
        region.setBlockData(centerX, centerY + 1, centerZ, Material.CAVE_AIR.createBlockData())
    }

    private fun findOpenings(
        region: LimitedRegion,
        centerX: Int,
        centerY: Int,
        centerZ: Int,
        bounds: DungeonBounds,
    ): Set<DungeonOpening> {
        val openings = mutableSetOf<DungeonOpening>()
        for (dx in -bounds.radiusX..bounds.radiusX) {
            for (dz in -bounds.radiusZ..bounds.radiusZ) {
                if (dx != -bounds.radiusX && dx != bounds.radiusX &&
                    dz != -bounds.radiusZ && dz != bounds.radiusZ
                ) {
                    continue
                }

                val x = centerX + dx
                val z = centerZ + dz
                if (region.isInRegion(x, centerY, z) &&
                    region.isInRegion(x, centerY + 1, z) &&
                    region.getBlockData(x, centerY, z).material.isAir &&
                    region.getBlockData(x, centerY + 1, z).material.isAir
                ) {
                    openings.add(DungeonOpening(x, z))
                }
            }
        }

        return openings
    }

    private fun carvePassage(
        region: LimitedRegion,
        startX: Int,
        startZ: Int,
        centerX: Int,
        centerY: Int,
        centerZ: Int,
    ) {
        var x = startX
        var z = startZ
        while (x != centerX) {
            carvePassageColumn(region, x, centerY, z)
            x += (centerX - x).sign
        }
        while (z != centerZ) {
            carvePassageColumn(region, x, centerY, z)
            z += (centerZ - z).sign
        }
        carvePassageColumn(region, centerX, centerY, centerZ)
    }

    private fun carvePassageColumn(region: LimitedRegion, x: Int, y: Int, z: Int) {
        for (dy in 0..1) {
            if (region.isInRegion(x, y + dy, z)) {
                region.setBlockData(x, y + dy, z, Material.CAVE_AIR.createBlockData())
            }
        }
    }

    private fun naturalStoneAt(y: Int): Material {
        return if (y <= 0) Material.DEEPSLATE else Material.STONE
    }

    private data class DungeonBounds(val radiusX: Int, val radiusZ: Int)
    private data class DungeonOpening(val x: Int, val z: Int)

    companion object {
        private const val MIN_FLOOR_MATCH_PERCENT = 85
        private val DUNGEON_BOUNDS = listOf(
            DungeonBounds(4, 4),
            DungeonBounds(4, 3),
            DungeonBounds(3, 4),
            DungeonBounds(3, 3),
        )
    }
}
