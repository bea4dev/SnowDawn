package level.biome

import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.registry.DynamicRegistry
import net.minestom.server.sound.SoundEvent
import net.minestom.server.world.biome.Biome
import net.minestom.server.world.biome.BiomeEffects

object SnowDawnBiomes {
    val SNOW_LAND: DynamicRegistry.Key<Biome>

    init {
        val white = NamedTextColor.WHITE.value()

        val snowLand = Biome.builder()
            .effects(
                BiomeEffects(
                    white,
                    white,
                    white,
                    white,
                    white,
                    white,
                    null,
                    null,
                    SoundEvent.MUSIC_NETHER_NETHER_WASTES.namespace(),
                    null,
                    null,
                    BiomeEffects.Music(SoundEvent.MUSIC_NETHER_BASALT_DELTAS.namespace(), 12000, 24000, true)
                )
            )
            .temperature(0.0F)
            .build()
        SNOW_LAND = MinecraftServer.getBiomeRegistry().register("snow_dawn:snow_land", snowLand)
    }

    fun init() {}
}