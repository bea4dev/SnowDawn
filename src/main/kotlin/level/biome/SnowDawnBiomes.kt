package level.biome

import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.sound.SoundEvent
import net.minestom.server.utils.NamespaceID
import net.minestom.server.world.biomes.Biome
import net.minestom.server.world.biomes.BiomeEffects

object SnowDawnBiomes {
    val SNOW_LAND: Biome

    init {
        val white = NamedTextColor.WHITE.value()

        SNOW_LAND = Biome.builder()
            .name(NamespaceID.from("snow_dawn:snow_land"))
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
            .precipitation(Biome.Precipitation.SNOW)
            .temperature(0.0F)
            .build()
        MinecraftServer.getBiomeManager().addBiome(SNOW_LAND)
    }

    fun init() {}
}