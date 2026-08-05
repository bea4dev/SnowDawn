package com.github.bea4dev.snowDawn.generator

import com.github.bea4dev.snowDawn.generator.structure.FixedPositionStructures
import com.github.bea4dev.snowDawn.generator.structure.ItemChest
import com.github.bea4dev.snowDawn.generator.structure.SleepStructureLayout
import com.github.bea4dev.snowDawn.generator.structure.SnowLandBoundary
import com.github.bea4dev.snowDawn.generator.structure.SurfaceStructures
import com.github.bea4dev.snowDawn.generator.structure.StructureChestRequirement
import com.github.bea4dev.snowDawn.generator.structure.UnderGroundStructures
import com.github.bea4dev.snowDawn.generator.structure.VanillaDungeonRemoval
import com.github.bea4dev.snowDawn.generator.structure.VanillaOreRemoval
import com.github.bea4dev.snowDawn.item.ItemRegistry
import com.github.bea4dev.vanilla_source.api.asset.WorldAssetsRegistry
import de.articdive.jnoise.generators.noisegen.opensimplex.FastSimplexNoiseGenerator
import de.articdive.jnoise.pipeline.JNoise
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Biome
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.Chest
import org.bukkit.generator.BiomeParameterPoint
import org.bukkit.generator.BiomeProvider
import org.bukkit.generator.BlockPopulator
import org.bukkit.generator.ChunkGenerator
import org.bukkit.generator.WorldInfo
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

private class SnowLandBiomeProvider(seed: Long) : BiomeProvider() {
    private val biomes = listOf(
        Biome.SNOWY_PLAINS,
        Biome.ICE_SPIKES,
        Biome.SNOWY_TAIGA,
        Biome.GROVE,
        Biome.SNOWY_SLOPES,
        Biome.FROZEN_PEAKS,
        Biome.JAGGED_PEAKS,
        Biome.FROZEN_RIVER,
        Biome.FROZEN_OCEAN,
        Biome.DEEP_FROZEN_OCEAN,
        Biome.SNOWY_BEACH,
    )
    private val noise = ThreadLocal.withInitial {
        JNoise.newBuilder()
            .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed).build())
            .scale(0.0015)
            .build()
    }
    private val detailNoise = ThreadLocal.withInitial {
        JNoise.newBuilder()
            .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 1).build())
            .scale(0.006)
            .build()
    }

    override fun getBiome(worldInfo: WorldInfo, x: Int, y: Int, z: Int): Biome {
        return getBiome(worldInfo, x, y, z, object : BiomeParameterPoint {
            override fun getTemperature() = 0.0
            override fun getMaxTemperature() = 1.0
            override fun getMinTemperature() = -1.0
            override fun getHumidity() = 0.0
            override fun getMaxHumidity() = 1.0
            override fun getMinHumidity() = -1.0
            override fun getContinentalness() = 0.0
            override fun getMaxContinentalness() = 1.0
            override fun getMinContinentalness() = -1.0
            override fun getErosion() = 0.0
            override fun getMaxErosion() = 1.0
            override fun getMinErosion() = -1.0
            override fun getDepth() = 0.0
            override fun getMaxDepth() = 1.0
            override fun getMinDepth() = -1.0
            override fun getWeirdness() = 0.0
            override fun getMaxWeirdness() = 1.0
            override fun getMinWeirdness() = -1.0
        })
    }

    override fun getBiome(
        worldInfo: WorldInfo,
        x: Int,
        y: Int,
        z: Int,
        biomeParameterPoint: BiomeParameterPoint
    ): Biome {
        val continentalness = biomeParameterPoint.continentalness
        val erosion = biomeParameterPoint.erosion
        val weirdness = biomeParameterPoint.weirdness
        val value = noise.get().evaluateNoise(x.toDouble(), z.toDouble()) +
                detailNoise.get().evaluateNoise(x.toDouble(), z.toDouble()) * 0.35

        if (continentalness < -0.45) {
            return if (continentalness < -0.75) Biome.DEEP_FROZEN_OCEAN else Biome.FROZEN_OCEAN
        }
        if (continentalness < -0.25) {
            return Biome.SNOWY_BEACH
        }
        if (erosion > 0.55 && value < -0.25) {
            return Biome.FROZEN_RIVER
        }
        if (weirdness > 0.45) {
            return if (value > 0.45) Biome.JAGGED_PEAKS else Biome.FROZEN_PEAKS
        }

        return when {
            value < -0.55 -> Biome.ICE_SPIKES
            value < -0.2 -> Biome.SNOWY_PLAINS
            value < 0.2 -> Biome.SNOWY_TAIGA
            value < 0.45 -> Biome.GROVE
            else -> Biome.SNOWY_SLOPES
        }
    }

    override fun getBiomes(worldInfo: WorldInfo): List<Biome> {
        return biomes
    }
}

class SnowLand internal constructor(seed: Long) : ChunkGenerator() {

    private val biomeProvider = SnowLandBiomeProvider(seed)
    private val centerAsset = WorldAssetsRegistry.getAsset("center")!!
    private val centerStructure = FixedPositionStructures(
        listOf(
            centerAsset to Vector(
                999,
                114 - (centerAsset.endPosition.blockY - centerAsset.startPosition.blockY),
                8 - (centerAsset.endPosition.blockZ - centerAsset.startPosition.blockZ),
            )
        ),
        merge = false,
    )
    private val sleepStructures = FixedPositionStructures(
        SleepStructureLayout.structures,
        listOf(
            Material.CHEST.createBlockData { blockData ->
                (blockData as Chest).facing = BlockFace.WEST
            } to SleepStructureLayout.chestPosition,
        ),
        merge = false,
    )
    private val surfaceStructures = SurfaceStructures(
        { minX, surfaceY, minZ, asset -> true },
        listOf(
            WorldAssetsRegistry.getAsset("bridge_0")!! to 0.1,
            WorldAssetsRegistry.getAsset("bridge_1")!! to 0.2,
            WorldAssetsRegistry.getAsset("bridge_2")!! to 0.2,
            WorldAssetsRegistry.getAsset("bridge_3")!! to 0.1,
            WorldAssetsRegistry.getAsset("bridge_4")!! to 0.1,
            WorldAssetsRegistry.getAsset("bridge_mini_0")!! to 0.2,
            WorldAssetsRegistry.getAsset("bridge_mini_1")!! to 0.2,
            WorldAssetsRegistry.getAsset("ant_0")!! to 0.2,
        ),
        null,
        ItemChest(
            listOf(
                listOf(
                    ItemRegistry.COAL.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 5 },
                    ItemRegistry.TORCH.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.FUEL.createItemStack().also { item -> item.amount = 2 },
                    ItemRegistry.SAPLING.createItemStack().also { item -> item.amount = 1 },
                ),
                listOf(
                    ItemStack(Material.POTATO, 5),
                    ItemStack(Material.POISONOUS_POTATO, 2),
                    ItemStack(Material.CARROT, 7),
                    ItemStack(Material.BAKED_POTATO, 3),
                    ItemRegistry.DIRT.createItemStack().also { item -> item.amount = 2 },
                    ItemRegistry.DIRT.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.ICE.createItemStack().also { item -> item.amount = 2 },
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 3 },
                ),
                listOf(
                    ItemRegistry.COAL.createItemStack().also { item -> item.amount = 1 },
                    ItemStack(Material.POTATO, 1),
                    ItemStack(Material.POISONOUS_POTATO, 1),
                    ItemStack(Material.CARROT, 2),
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 2 },
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 5 },
                    ItemRegistry.ICE.createItemStack().also { item -> item.amount = 3 },
                    ItemRegistry.DIRT.createItemStack().also { item -> item.amount = 3 },
                ),
                listOf(
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 3 },
                    ItemRegistry.FUEL.createItemStack().also { item -> item.amount = 3 },
                    ItemRegistry.SAPLING.createItemStack().also { item -> item.amount = 1 },
                ),
                listOf(
                    ItemRegistry.COAL.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.COAL.createItemStack().also { item -> item.amount = 2 },
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 7 },
                    ItemRegistry.BLUE_PRINT_CATALYST.createItemStack(),
                ),
                listOf(
                    ItemRegistry.COPPER_INGOT.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.FUEL.createItemStack().also { item -> item.amount = 2 },
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 2 },
                ),
                listOf(
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 4 },
                    ItemRegistry.DIRT.createItemStack().also { item -> item.amount = 3 },
                    ItemRegistry.BLUE_PRINT_CATALYST.createItemStack(),
                    ItemRegistry.ICE.createItemStack().also { item -> item.amount = 2 },
                    ItemRegistry.STORY_MEMO.createItemStack(),
                ),
                listOf(
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 2 },
                    ItemRegistry.SAPLING.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.DIRT.createItemStack().also { item -> item.amount = 4 },
                    ItemStack(Material.POTATO, 3),
                    ItemStack(Material.POTATO, 2),
                    ItemRegistry.FUEL.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.ICE.createItemStack().also { item -> item.amount = 5 },
                    ItemRegistry.BLUE_PRINT_CHEST.createItemStack(),
                )
            ),
            StructureChestRequirement.IRON_INGOT,
        ),
        seed,
    )
    private val roomStructures = UnderGroundStructures(
        { minX, surfaceY, minZ, asset -> true },
        listOf(
            WorldAssetsRegistry.getAsset("room_0")!! to 0.1,
            WorldAssetsRegistry.getAsset("room_1")!! to 0.2,
        ),
        ItemChest(
            listOf(
                listOf(
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 4 },
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.FUEL.createItemStack().also { item -> item.amount = 4 },
                    ItemRegistry.BLUE_PRINT_STURDY_PIPE.createItemStack(),
                ),
                listOf(
                    ItemRegistry.COAL.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 2 },
                    ItemRegistry.STORY_MEMO.createItemStack(),
                    ItemRegistry.BLUE_PRINT_CATALYST.createItemStack(),
                ),
                listOf(
                    ItemStack(Material.POTATO, 3),
                    ItemStack(Material.POISONOUS_POTATO, 2),
                    ItemStack(Material.CARROT, 1),
                    ItemRegistry.STORY_MEMO.createItemStack(),
                    ItemRegistry.BLUE_PRINT_CHEST.createItemStack(),
                ),
                listOf(
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 3 },
                    ItemRegistry.BLUE_PRINT_CATALYST.createItemStack(),
                    ItemRegistry.BLUE_PRINT_STURDY_PIPE.createItemStack(),
                ),
                listOf(
                    ItemRegistry.COAL.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.STORY_MEMO.createItemStack(),
                ),
                listOf(
                    ItemRegistry.COPPER_INGOT.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.STORY_MEMO.createItemStack(),
                    ItemRegistry.FUEL.createItemStack().also { item -> item.amount = 5 },
                )
            ),
            StructureChestRequirement.DIAMOND,
        ),
        seed,
        0,
        merge = false,
    )
    private val surfaceRoomStructures = SurfaceStructures(
        { minX, surfaceY, minZ, asset -> true },
        listOf(
            WorldAssetsRegistry.getAsset("room_0")!! to 0.05,
            WorldAssetsRegistry.getAsset("room_1")!! to 0.05,
        ),
        null,
        ItemChest(
            listOf(
                listOf(
                    ItemRegistry.COAL.createItemStack().also { item -> item.amount = 2 },
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.TORCH.createItemStack().also { item -> item.amount = 2 },
                    ItemRegistry.BLUE_PRINT_CATALYST.createItemStack()
                ),
                listOf(
                    ItemRegistry.COAL.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.FUEL.createItemStack().also { item -> item.amount = 3 },
                ),
                listOf(
                    ItemStack(Material.POTATO, 3),
                    ItemStack(Material.POISONOUS_POTATO, 2),
                    ItemRegistry.STORY_MEMO.createItemStack(),
                    ItemStack(Material.CARROT, 1),
                    ItemStack(Material.POTATO, 1),
                ),
                listOf(
                    ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 3 },
                ),
                listOf(
                    ItemRegistry.COAL.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.BLUE_PRINT_CATALYST.createItemStack()
                ),
                listOf(
                    ItemRegistry.COPPER_INGOT.createItemStack().also { item -> item.amount = 1 },
                    ItemRegistry.STORY_MEMO.createItemStack(),
                    ItemRegistry.FUEL.createItemStack().also { item -> item.amount = 5 },
                )
            ),
            StructureChestRequirement.IRON_INGOT,
        ),
        seed,
        merge = false,
    )

    private val populators = listOf(
        VanillaDungeonRemoval(),
        VanillaOreRemoval(),
        sleepStructures,
        surfaceStructures,
        roomStructures,
        surfaceRoomStructures,
        SnowLandBoundary(),
        centerStructure,
    )

    override fun shouldGenerateNoise(): Boolean {
        return true
    }

    override fun shouldGenerateSurface(): Boolean {
        return true
    }

    override fun shouldGenerateCaves(): Boolean {
        return true
    }

    override fun shouldGenerateDecorations(): Boolean {
        return true
    }

    override fun shouldGenerateMobs(): Boolean {
        return false
    }

    override fun shouldGenerateStructures(): Boolean {
        return false
    }

    override fun getDefaultBiomeProvider(worldInfo: WorldInfo): BiomeProvider {
        return biomeProvider
    }

    override fun getDefaultPopulators(world: World): List<BlockPopulator> {
        return populators
    }

}
