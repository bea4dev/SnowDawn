package level.generator.structure

import com.github.bea4dev.vanilla_source.server.level.util.BlockPosition
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block

class Structure(level: Instance, config: StructureConfig) {
    val name: String = config.name
    val blockMap: Map<BlockPosition, Block>

    init {

    }


}