package level.generator.structure

import SnowDawn
import com.github.bea4dev.vanilla_source.Resources
import com.github.bea4dev.vanilla_source.config.TomlConfig
import com.github.bea4dev.vanilla_source.server.level.util.BlockPosition
import com.github.michaelbull.result.unwrap
import java.io.File

val STRUCTURE_CONFIG_MAP = run {
    val filePathStr = "structures/snow_land.toml"
    val file = File(filePathStr)
    if (!file.exists()) {
        Resources.savePluginResource(filePathStr, false, SnowDawn::class.java)
    }

    val map = mutableMapOf<String, StructureConfig>()
    val files = File("structures").listFiles()!!
    for (configFile in files) {
        val config = TomlConfig.load<StructureConfig>(configFile).unwrap()
        map[configFile.name] = config
    }

    map
}

data class StructureConfig(
    val name: String,
    val position1: BlockPosition,
    val position2: BlockPosition,
    val ignoreAir: Boolean
): TomlConfig