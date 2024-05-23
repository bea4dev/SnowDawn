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
    private val shapeNoise = JNoise.newBuilder()
        .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(seed + 100).build())
        .scale(0.010)
        .build()

    override fun generate(unit: GenerationUnit) {
        val start = unit.absoluteStart()
        for (x in 0..<unit.size().x().toInt()) {
            for (z in 0..<unit.size().z().toInt()) {
                val bottom = start.add(x.toDouble(), 0.0, z.toDouble())

                var landHeight = synchronized(baseNoise) {
                    baseNoise.evaluateNoise(bottom.x(), bottom.z()) * 16 + 16
                }

                val shapeValue = synchronized(shapeNoise) {
                    shapeNoise.evaluateNoise(bottom.x(), bottom.z()) * 8 + 8
                }
                if (shapeValue > 8) {
                    landHeight += shapeValue
                }

                unit.modifier().fill(bottom, bottom.add(1.0, 0.0, 1.0).withY(landHeight), Block.STONE)

                val layerIndex = ((landHeight - floor(landHeight)) * 8).toInt()
                val layer = SnowLayer[layerIndex]
                unit.modifier().setBlock(bottom.withY(landHeight), layer)
            }
        }

        unit.modifier().fillBiome(SnowDawnBiomes.SNOW_LAND)
    }
}