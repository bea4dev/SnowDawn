package level.generator.structure

import com.github.bea4dev.vanilla_source.server.level.util.BlockPosition
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block

class Structure(level: Instance, val name: String, config: StructureConfig) {
    val blockMap: Map<BlockPosition, Block>
    val ignoreAir: Boolean = config.ignoreAir

    init {
        val p1 = config.position1
        val p2 = config.position2
        val startX = minOf(p1.x, p2.x)
        val startY = minOf(p1.y, p2.y)
        val startZ = minOf(p1.z, p2.z)
        val endX = maxOf(p1.x, p2.x)
        val endY = maxOf(p1.y, p2.y)
        val endZ = maxOf(p1.z, p2.z)

        val blockMap = mutableMapOf<BlockPosition, Block>()
        for (x in startX..endX) {
            for (y in startY..endY) {
                for (z in startZ..endZ) {
                    val block = level.getBlock(x, y, z)
                    blockMap[BlockPosition(x, y, z)] = block
                }
            }
        }

        this.blockMap = blockMap
    }

}