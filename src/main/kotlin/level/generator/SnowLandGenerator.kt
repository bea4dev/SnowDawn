package level.generator

import de.articdive.jnoise.core.api.functions.Interpolation
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction
import de.articdive.jnoise.generators.noisegen.opensimplex.FastSimplexNoiseGenerator
import de.articdive.jnoise.pipeline.JNoise
import level.biome.SnowDawnBiomes
import level.generator.structure.Placement
import level.generator.structure.XZStructurePlacements
import level.generator.structure.structures
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.coordinate.Point
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.generator.GenerationUnit
import net.minestom.server.instance.generator.Generator
import kotlin.math.floor

private class SnowLandGeneratorVariables(seed: Long) {
    val baseNoise: JNoise = JNoise.newBuilder()
        .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed).build())
        .scale(0.005)
        .build()

    val shapeNoise1: JNoise = JNoise.newBuilder()
        .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 100).build())
        .scale(0.01)
        .build()

    val shapeNoise2: JNoise = JNoise.newBuilder()
        .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 200).build())
        .scale(0.002)
        .build()

    val detailNoise1: JNoise = JNoise.newBuilder()
        .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 300).build())
        .scale(0.003)
        .build()

    val detailNoise2: JNoise = JNoise.newBuilder()
        .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 400).build())
        .scale(0.02)
        .build()

    val caveNoise1: JNoise = JNoise.newBuilder()
        .perlin(seed + 500, Interpolation.LINEAR, FadeFunction.NONE)
        .scale(0.02)
        .build()

    val caveNoise2: JNoise = JNoise.newBuilder()
        .perlin(seed + 600, Interpolation.LINEAR, FadeFunction.NONE)
        .scale(0.03)
        .build()

    val caveNoise3: JNoise = JNoise.newBuilder()
        .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 700).build())
        .scale(0.05)
        .build()

    val caveNoise4: JNoise = JNoise.newBuilder()
        .perlin(seed + 800, Interpolation.COSINE, FadeFunction.NONE)
        .scale(0.02)
        .build()

    val caveNoise5: JNoise = JNoise.newBuilder()
        .perlin(seed + 900, Interpolation.LINEAR, FadeFunction.NONE)
        .scale(0.04)
        .build()

    val caveNoise6: JNoise = JNoise.newBuilder()
        .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 1000).build())
        .scale(0.06)
        .build()

    val iceNoise1: JNoise = JNoise.newBuilder()
        .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 1100).build())
        .scale(0.01)
        .build()

    val iceNoise2: JNoise = JNoise.newBuilder()
        .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 1200).build())
        .scale(0.02)
        .build()

    fun evaluateLowerHeight(bottom: Point): Double {
        return baseNoise.evaluateNoise(bottom.x(), bottom.z()) * 16 + 64
    }

    fun evaluateHeight(bottom: Point): Double {
        val lowerLandHeight = this.evaluateLowerHeight(bottom)
        var landHeight = lowerLandHeight

        val shapeValue1 = shapeNoise1.evaluateNoise(bottom.x(), bottom.z()) * 8 + 8
        if (shapeValue1 > 8) {
            landHeight += shapeValue1
        }

        val shapeValue2 = shapeNoise2.evaluateNoise(bottom.x(), bottom.z()) * 4
        if (shapeValue2 < -2 && shapeValue1 <= 8) {
            landHeight += shapeValue2
        }

        val detailValue1 = detailNoise1.evaluateNoise(bottom.x(), bottom.z()) * 1.5
        if (shapeValue1 > 8) {
            landHeight += detailValue1
        }

        val detailValue = detailNoise2.evaluateNoise(bottom.x(), bottom.z()) * 1.5
        landHeight += detailValue

        return landHeight
    }


    val xzStructurePlacements: XZStructurePlacements


    init {
        val tower1 = structures["tower1"]!!
        val tower2 = structures["tower2"]!!
        val tower3 = structures["tower3"]!!
        val tower4 = structures["tower4"]!!

        fun towerEvaluator1(sizeX: Int, sizeZ: Int, factor: Int): (Int, Int) -> Boolean {
            fun evaluate(minX: Int, minZ: Int): Boolean {
                if (minX % (sizeX * factor) != 0 || minZ % (sizeZ * factor) != 0) {
                    return false
                }

                for (x in minX..(minX + tower1.x)) {
                    for (z in minZ..(minZ + tower1.z)) {
                        val shapeValue = shapeNoise1.evaluateNoise(x.toDouble(), z.toDouble()) * 8 + 8
                        if (shapeValue <= 8) {
                            return false
                        }
                    }
                }

                return true
            }
            return ::evaluate
        }

        val tower1Placement = Placement(tower1, towerEvaluator1(tower1.x, tower1.z, 10))
        val tower2Placement = Placement(tower2, towerEvaluator1(tower2.x, tower2.z, 8))
        val tower3Placement = Placement(tower3, towerEvaluator1(tower3.x, tower3.z, 4))
        val tower4Placement = Placement(tower4, towerEvaluator1(tower4.x, tower4.z, 2))

        xzStructurePlacements = XZStructurePlacements(listOf(
            tower1Placement,
            tower2Placement,
            tower3Placement,
            tower4Placement
        ))
    }
}

class SnowLandGenerator(seed: Long): Generator {

    private val variables = ThreadLocal.withInitial { SnowLandGeneratorVariables(seed) }

    override fun generate(unit: GenerationUnit) {
        val variables = this.variables.get()

        val start = unit.absoluteStart()
        for (x in 0..<unit.size().x().toInt()) {
            for (z in 0..<unit.size().z().toInt()) {
                val bottom = start.add(x.toDouble(), 0.0, z.toDouble())

                val lowerLandHeight = variables.evaluateLowerHeight(bottom)
                val landHeight = variables.evaluateHeight(bottom)

                for (y in 0..<unit.size().y().toInt()) {
                    val position = start.add(x.toDouble(), y.toDouble(), z.toDouble())

                    val caveValue1 = variables.caveNoise1.evaluateNoise(position.x(), position.y(), position.z()) +
                            variables.caveNoise2.evaluateNoise(position.x(), position.y(), position.z()) +
                            variables.caveNoise3.evaluateNoise(position.x(), position.y(), position.z()) / 2

                    val caveValue2 = variables.caveNoise4.evaluateNoise(position.x(), position.y(), position.z()) +
                            variables.caveNoise5.evaluateNoise(position.x(), position.y(), position.z()) +
                            variables.caveNoise6.evaluateNoise(position.x(), position.y(), position.z()) / 3

                    val isCave = if (position.y() < lowerLandHeight) {
                        caveValue1 > 0.8 || caveValue2 > 0.8 || caveValue1 < -0.8 || caveValue2 < -0.8
                    } else {
                        caveValue1 > 1.3 || caveValue2 > 1.0 || caveValue1 < -1.3 || caveValue2 < -1.0
                    }

                    val iceValue = variables.iceNoise1.evaluateNoise(position.x(), position.y(), position.z()) +
                            variables.iceNoise2.evaluateNoise(position.x(), position.y(), position.z()) / 2

                    if (position.y() < landHeight.toInt() - 1) {
                        if (!isCave) {
                            if (iceValue > 0.8) {
                                if (iceValue > 1.0) {
                                    unit.modifier().setBlock(position, Block.PACKED_ICE)
                                } else {
                                    unit.modifier().setBlock(position, Block.ICE)
                                }
                            } else {
                                unit.modifier().setBlock(position, Block.STONE)
                            }
                        }
                    }

                    if (position.y().toInt() == landHeight.toInt() - 1) {
                        if (isCave) {
                            unit.modifier().setBlock(position, Block.POWDER_SNOW)
                        } else {
                            unit.modifier().setBlock(position, Block.SNOW_BLOCK)
                        }
                    }
                }

                val layerIndex = ((landHeight - floor(landHeight)) * 8).toInt()
                val layer = SnowLayer[layerIndex]
                unit.modifier().setBlock(bottom.withY(landHeight), layer)

                val structure = variables.xzStructurePlacements.evaluate(bottom.blockX(), bottom.blockZ())
                if (structure != null) {
                    val minX = bottom.blockX() - bottom.blockX().mod(structure.x)
                    val minZ = bottom.blockZ() - bottom.blockZ().mod(structure.z)
                    val height = variables.evaluateHeight(BlockVec(minX, 0, minZ)).toInt()

                    for (y in 0..<unit.size().y().toInt()) {
                        val block = structure.getBlock(bottom.blockX(), start.blockY() + y, bottom.blockZ(), height)
                        if (block != null) {
                            unit.modifier().setBlock(bottom.blockX(), start.blockY() + y, bottom.blockZ(), block)
                        }
                    }
                }
            }
        }

        unit.modifier().fillBiome(SnowDawnBiomes.SNOW_LAND)
    }
}