package level.generator

import com.github.bea4dev.vanilla_source.server.level.generator.GeneratorRegistry

fun registerGenerators() {
    GeneratorRegistry.register("snow_land", SnowLandGenerator(0))
}