package level.generator

import net.minestom.server.instance.block.Block

object SnowLayer {
    private val layers: List<Block>

    init {
        val layers = mutableListOf<Block>()
        for (i in 1..8) {
            layers.add(Block.SNOW.withProperty("layers", i.toString()))
        }
        this.layers = layers
    }

    operator fun get(index: Int): Block {
        return layers[index]
    }
}