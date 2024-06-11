import com.github.bea4dev.vanilla_source.plugin.VanillaSourcePlugin
import level.biome.SnowDawnBiomes
import level.generator.registerGenerators
import level.generator.structure.initStructures
import level.listener.registerLevelListener

class SnowDawn: VanillaSourcePlugin {

    override fun onEnable() {
        println("Starting SnowDawn")

        SnowDawnBiomes.init()

        initStructures()

        registerGenerators()

        registerLevelListener()
    }

    override fun onDisable() {}

}