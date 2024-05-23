package level.generator

import de.articdive.jnoise.generators.noisegen.opensimplex.FastSimplexNoiseGenerator
import de.articdive.jnoise.pipeline.JNoise
import level.biome.SnowDawnBiomes
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.generator.GenerationUnit
import net.minestom.server.instance.generator.Generator
import kotlin.math.floor

class SnowLandGenerator(seed: Long): Generator {
    private val baseNoise = JNoise.newBuilder()
        .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed).build())
        .scale(0.005)
        .build()
    private val shapeNoise1 = JNoise.newBuilder()
        .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 100).build())
        .scale(0.01)
        .build()
    private val shapeNoise2 = JNoise.newBuilder()
        .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 200).build())
        .scale(0.002)
        .build()
    private val detailNoise1 = JNoise.newBuilder()
        .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 300).build())
        .scale(0.003)
        .build()
    private val detailNoise2 = JNoise.newBuilder()
        .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 400).build())
        .scale(0.02)
        .build()

    override fun generate(unit: GenerationUnit) {
        val start = unit.absoluteStart()
        for (x in 0..<unit.size().x().toInt()) {
            for (z in 0..<unit.size().z().toInt()) {
                val bottom = start.add(x.toDouble(), 0.0, z.toDouble())

                var landHeight = synchronized(baseNoise) {
                    baseNoise.evaluateNoise(bottom.x(), bottom.z()) * 16 + 64
                }

                val shapeValue1 = synchronized(shapeNoise1) {
                    shapeNoise1.evaluateNoise(bottom.x(), bottom.z()) * 8 + 8
                }
                if (shapeValue1 > 8) {
                    landHeight += shapeValue1
                }

                val shapeValue2 = synchronized(shapeNoise2) {
                    shapeNoise2.evaluateNoise(bottom.x(), bottom.z()) * 4
                }
                if (shapeValue2 < -2 && shapeValue1 <= 8) {
                    landHeight += shapeValue2
                }

                val detailValue1 = synchronized(detailNoise1) {
                    detailNoise1.evaluateNoise(bottom.x(), bottom.z()) * 1.5
                }
                if (shapeValue1 > 8) {
                    landHeight += detailValue1
                }

                val detailValue = synchronized(detailNoise2) {
                    detailNoise2.evaluateNoise(bottom.x(), bottom.z()) * 1.5
                }
                landHeight += detailValue

                unit.modifier().fill(bottom, bottom.add(1.0, 0.0, 1.0).withY(landHeight), Block.STONE)

                val layerIndex = ((landHeight - floor(landHeight)) * 8).toInt()
                val layer = SnowLayer[layerIndex]
                unit.modifier().setBlock(bottom.withY(landHeight), layer)
            }
        }

        unit.modifier().fillBiome(SnowDawnBiomes.SNOW_LAND)
    }
}