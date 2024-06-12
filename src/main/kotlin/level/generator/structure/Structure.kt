package level.generator.structure

import com.github.bea4dev.vanilla_source.server.level.util.BlockPosition
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block

class Structure(level: Instance, val name: String, config: StructureConfig) {
    private val blockMap: Map<BlockPosition, Block>
    private val ignoreAir: Boolean = config.ignoreAir ?: true
    private val offset: BlockPosition = config.offset
    val x: Int
    val y: Int
    val z: Int

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
                    val position = BlockVec(x, y, z)
                    var chunk = level.getChunkAt(position)
                    if (chunk == null) {
                        chunk = level.loadChunk(BlockVec(x, y, z)).join()
                    }
                    val block = chunk!!.getBlock(position)
                    blockMap[BlockPosition(x - startX, y - startY, z - startZ)] = block
                }
            }
        }

        this.blockMap = blockMap

        x = endX - startX + 1
        y = endY - startY + 1
        z = endZ - startZ + 1
    }

    fun getBlock(x: Int, y: Int, z: Int, height: Int): Block? {
        val sx = x.mod(this.x) - offset.x
        val sy = y - height - offset.y
        val sz = z.mod(this.z) - offset.z

        if (sx < 0 || this.x < sx || sy < 0 || this.y < sy || sz < 0 || this.z < sz) {
            return null
        }

        val block = blockMap[BlockPosition(sx, sy, sz)]

        return if (ignoreAir && block?.isAir == true) {
            null
        } else {
            block
        }
    }

}