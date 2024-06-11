package level.generator.structure

import cc.ekblad.toml.TomlMapper
import cc.ekblad.toml.tomlMapper
import com.github.bea4dev.vanilla_source.config.DefaultTomlConfig
import com.github.bea4dev.vanilla_source.config.TomlConfig
import com.github.bea4dev.vanilla_source.server.level.util.BlockPosition

data class StructureConfig(
    val position1: BlockPosition,
    val position2: BlockPosition,
    val ignoreAir: Boolean
): TomlConfig {
    companion object: DefaultTomlConfig {
        override fun default(): TomlConfig {
            return StructureConfig(BlockPosition(0, 0, 0), BlockPosition(0, 0, 0), true)
        }

        override fun mapper(): TomlMapper {
            return tomlMapper {
                "ignore_air" to "ignoreAir"
            }
        }

    }
}