package level.generator.structure

import SnowDawn
import com.github.bea4dev.vanilla_source.Resources
import com.github.bea4dev.vanilla_source.config.TomlConfig
import com.github.bea4dev.vanilla_source.server.level.Level
import com.github.michaelbull.result.unwrap
import java.io.File
import java.util.stream.Collectors

val structures = run {
    val filePathStr = "structures/test.toml"
    val dirPathStr = "structure"
    val dir = File(dirPathStr)
    if (!dir.exists()) {
        dir.mkdirs()
        Resources.savePluginResource(filePathStr, false, SnowDawn::class.java)
    }

    val map = mutableMapOf<String, StructureConfig>()
    val files = File("structures").listFiles()!!
    for (configFile in files) {
        val config = TomlConfig.load<StructureConfig>(configFile).unwrap()
        map[configFile.name] = config
    }

    val level = Level.load("asset_level").unwrap()

    map.entries.stream()
        .map { entry -> Structure(level, entry.key, entry.value) }
        .collect(Collectors.toMap({ e -> e.name }, { e -> e }))
}

fun initStructures() {}