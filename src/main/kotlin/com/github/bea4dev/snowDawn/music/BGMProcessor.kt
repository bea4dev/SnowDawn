package com.github.bea4dev.snowDawn.music

import com.github.bea4dev.snowDawn.SnowDawn
import com.github.bea4dev.snowDawn.save.ServerData
import com.github.bea4dev.snowDawn.text.Text
import java.time.Duration
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.ceil
import kotlin.random.Random
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.sound.SoundStop
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask

data class BGMTrack(
    val soundKey: Key,
    val displayName: Component,
    val length: Duration,
    val volume: Float = 1.0F,
    val pitch: Float = 1.0F,
) {
    init {
        require(!length.isZero && !length.isNegative) { "BGM length must be positive." }
    }

    constructor(
        soundKey: String,
        displayName: String,
        length: Duration,
        volume: Float = 1.0F,
        pitch: Float = 1.0F,
    ) : this(Key.key(soundKey), Component.text(displayName), length, volume, pitch)
}

val BGM_TRACK_THE_END = BGMTrack(
    org.bukkit.Sound.MUSIC_END.key(),
    Component.text("The End"),
    Duration.ofSeconds(930),
    1.0F,
    1.0F,
)

object BGMRegistry {
    private val tracks = CopyOnWriteArrayList(
        listOf(
            BGMTrack(
                org.bukkit.Sound.MUSIC_NETHER_CRIMSON_FOREST.key(),
                Component.text("Crimson Forest"),
                Duration.ofSeconds(330),
                1.0F,
                1.0F,
            ),
            BGMTrack(
                org.bukkit.Sound.MUSIC_NETHER_NETHER_WASTES.key(),
                Component.text("Nether Waste"),
                Duration.ofSeconds(330),
                1.0F,
                1.0F,
            ),
            BGMTrack(
                org.bukkit.Sound.MUSIC_NETHER_BASALT_DELTAS.key(),
                Component.text("Basalt Deltas"),
                Duration.ofSeconds(330),
                1.0F,
                1.0F,
            )
        )
    )

    fun setPlaylist(playlist: Collection<BGMTrack>) {
        tracks.clear()
        tracks.addAll(playlist)
    }

    fun setPlaylist(vararg playlist: BGMTrack) {
        setPlaylist(playlist.toList())
    }

    fun add(track: BGMTrack) {
        tracks.add(track)
    }

    fun getPlaylist(): List<BGMTrack> = tracks.toList()
}

class BGMProcessor(private val player: Player) {
    @Volatile
    var isEnabled: Boolean = true
        private set

    @Volatile
    var isRunning: Boolean = true
        private set

    private var currentTrack: BGMTrack? = null
    private var previousSoundKey: Key? = null
    private var remainingTicks = 0L
    private val additionalTracks = CopyOnWriteArrayList<BGMTrack>()
    private val random = Random(player.uniqueId.mostSignificantBits xor System.nanoTime())
    private val task: BukkitTask = Bukkit.getScheduler().runTaskTimer(
        SnowDawn.plugin,
        Runnable(::tick),
        1L,
        1L,
    )

    fun start() {
        isRunning = true
    }

    fun stop() {
        isRunning = false
        runOnMainThread(::stopCurrentTrack)
    }

    fun enable() {
        isEnabled = true
        isRunning = true
    }

    fun disable() {
        isEnabled = false
        isRunning = false
        runOnMainThread(::stopCurrentTrack)
    }

    fun addTrack(track: BGMTrack) {
        if (additionalTracks.none { existing -> existing.soundKey == track.soundKey }) {
            additionalTracks.add(track)
        }
    }

    fun dispose() {
        isEnabled = false
        isRunning = false
        runOnMainThread {
            stopCurrentTrack()
            task.cancel()
        }
    }

    private fun tick() {
        if (!player.isOnline || !isEnabled || !isRunning) {
            return
        }

        if (currentTrack == null) {
            playNextTrack()
            return
        }

        remainingTicks--
        if (remainingTicks <= 0L) {
            playNextTrack()
        }
    }

    private fun playNextTrack() {
        val playlist = (BGMRegistry.getPlaylist() + additionalTracks)
            .distinctBy { track -> track.soundKey }
        if (playlist.isEmpty()) {
            stopCurrentTrack()
            return
        }

        val candidates = playlist.filter { track -> track.soundKey != previousSoundKey }
        if (candidates.isEmpty()) {
            stopCurrentTrack()
            return
        }
        val nextTrack = candidates.random(random)

        stopCurrentTrack()
        currentTrack = nextTrack
        previousSoundKey = nextTrack.soundKey
        remainingTicks = ceil(nextTrack.length.toMillis() / 50.0).toLong().coerceAtLeast(1L)

        player.playSound(
            Sound.sound(
                nextTrack.soundKey,
                Sound.Source.MUSIC,
                nextTrack.volume,
                nextTrack.pitch,
            )
        )
        player.sendActionBar(
            Component.translatable(
                Text.BGM_NOW_PLAYING.toString(),
                nextTrack.displayName,
            )
        )
    }

    private fun stopCurrentTrack() {
        val track = currentTrack ?: return
        player.stopSound(SoundStop.namedOnSource(track.soundKey, Sound.Source.MUSIC))
        currentTrack = null
        remainingTicks = 0L
    }

    private fun runOnMainThread(block: () -> Unit) {
        if (Bukkit.isPrimaryThread()) {
            block()
        } else {
            Bukkit.getScheduler().runTask(SnowDawn.plugin, Runnable(block))
        }
    }
}

object BGMProcessorRegistry {
    private val processors = ConcurrentHashMap<UUID, BGMProcessor>()

    operator fun get(player: Player): BGMProcessor {
        return processors.computeIfAbsent(player.uniqueId) { BGMProcessor(player) }
    }

    fun onPlayerJoin(player: Player) {
        val processor = get(player)
        if (ServerData.theEndBgmUnlocked) {
            processor.addTrack(BGM_TRACK_THE_END)
        }
    }

    fun onPlayerQuit(player: Player) {
        processors.remove(player.uniqueId)?.dispose()
    }
}
