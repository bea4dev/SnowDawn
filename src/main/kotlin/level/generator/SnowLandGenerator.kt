package level.generator

import de.articdive.jnoise.generators.noisegen.opensimplex.FastSimplexNoiseGenerator
import de.articdive.jnoise.pipeline.JNoise
import level.biome.SnowDawnBiomes
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.generator.GenerationUnit
import net.minestom.server.instance.generator.Generator
import kotlin.math.floor

class SnowLandGenerator(seed: Long): Generator {
    private val baseNoise = ThreadLocal.withInitial {
        JNoise.newBuilder()
            .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed).build())
            .scale(0.005)
            .build()
    }
    private val shapeNoise1 = ThreadLocal.withInitial {
        JNoise.newBuilder()
            .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 100).build())
            .scale(0.01)
            .build()
    }
    private val shapeNoise2 = ThreadLocal.withInitial {
        JNoise.newBuilder()
            .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 200).build())
            .scale(0.002)
            .build()
    }
    private val detailNoise1 = ThreadLocal.withInitial {
        JNoise.newBuilder()
            .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 300).build())
            .scale(0.003)
            .build()
    }
    private val detailNoise2 = ThreadLocal.withInitial {
        JNoise.newBuilder()
            .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 400).build())
            .scale(0.02)
            .build()
    }
    private val caveNoise1 = ThreadLocal.withInitial {
        JNoise.newBuilder()
            .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 500).build())
            .scale(0.01)
            .build()
    }
    private val caveNoise2 = ThreadLocal.withInitial {
        JNoise.newBuilder()
            .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 600).build())
            .scale(0.02)
            .build()
    }
    private val caveNoise3 = ThreadLocal.withInitial {
        JNoise.newBuilder()
            .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 700).build())
            .scale(0.05)
            .build()
    }


    override fun generate(unit: GenerationUnit) {
        val baseNoise = baseNoise.get()
        val shapeNoise1 = shapeNoise1.get()
        val shapeNoise2 = shapeNoise2.get()
        val detailNoise1 = detailNoise1.get()
        val detailNoise2 = detailNoise2.get()
        val caveNoise1 = caveNoise1.get()
        val caveNoise2 = caveNoise2.get()
        val caveNoise3 = caveNoise3.get()

        val start = unit.absoluteStart()
        for (x in 0..<unit.size().x().toInt()) {
            for (z in 0..<unit.size().z().toInt()) {
                val bottom = start.add(x.toDouble(), 0.0, z.toDouble())

                var landHeight = baseNoise.evaluateNoise(bottom.x(), bottom.z()) * 16 + 64

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

                for (y in 0..<unit.size().y().toInt()) {
                    val position = start.add(x.toDouble(), y.toDouble(), z.toDouble())

                    val caveValue = caveNoise1.evaluateNoise(position.x(), position.y(), position.z()) +
                            caveNoise2.evaluateNoise(position.x(), position.y(), position.z()) +
                            caveNoise3.evaluateNoise(position.x(), position.y(), position.z()) / 2

                    val isCave = if (position.y() < landHeight) {
                        caveValue > 1.2
                    } else {
                        caveValue > 1.5
                    }

                    if (position.y() < landHeight.toInt() - 1) {
                        if (!isCave) {
                            unit.modifier().setBlock(position, Block.STONE)
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
            }
        }

        unit.modifier().fillBiome(SnowDawnBiomes.SNOW_LAND)
    }
}