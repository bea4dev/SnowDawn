package com.github.bea4dev.snowDawn.scenario

import com.github.bea4dev.snowDawn.SnowDawn
import com.github.bea4dev.snowDawn.text.Text
import com.github.shynixn.mccoroutine.bukkit.scope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.title.Title
import org.bukkit.entity.Player
import java.time.Duration as JavaDuration
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

class CreditTimeline(
    private val player: Player,
    private val totalDuration: Duration,
) {
    private val startedAtNanos = System.nanoTime()
    private var allocatedDuration = Duration.ZERO

    init {
        require(totalDuration.isPositive()) { "Credit duration must be positive." }
    }

    fun show(
        title: Text,
        subtitle: Text,
        titleColor: TextColor,
        subtitleColor: TextColor,
        duration: Duration,
    ): Deferred<Unit> {
        require(duration.isPositive()) { "Credit entry duration must be positive." }

        val entryStart = allocatedDuration
        val entryEnd = entryStart + duration
        require(entryEnd <= totalDuration) { "Credit entries exceed the total duration." }
        allocatedDuration = entryEnd

        return SnowDawn.plugin.scope.async {
            val titleText = title[player]
            val subtitleText = subtitle[player]
            val animationDuration = minOf(2.seconds, duration / 3)

            animateEntry(
                revealing = true,
                titleText = titleText,
                subtitleText = subtitleText,
                titleColor = titleColor,
                subtitleColor = subtitleColor,
                startsAt = entryStart,
                duration = animationDuration,
            )

            val hideStart = entryEnd - animationDuration
            delayUntil(hideStart)
            animateEntry(
                revealing = false,
                titleText = titleText,
                subtitleText = subtitleText,
                titleColor = titleColor,
                subtitleColor = subtitleColor,
                startsAt = hideStart,
                duration = animationDuration,
            )

            delayUntil(entryEnd)
            resetTitle()
        }
    }

    fun finish(): Deferred<Unit> {
        check(allocatedDuration == totalDuration) {
            "Credit entries must total exactly $totalDuration, but totaled $allocatedDuration."
        }

        return SnowDawn.plugin.scope.async {
            delayUntil(totalDuration)
            resetTitle()
        }
    }

    private suspend fun animateEntry(
        revealing: Boolean,
        titleText: String,
        subtitleText: String,
        titleColor: TextColor,
        subtitleColor: TextColor,
        startsAt: Duration,
        duration: Duration,
    ) {
        val titleCodePoints = titleText.codePoints().toArray()
        val subtitleCodePoints = subtitleText.codePoints().toArray()
        val totalCodePoints = titleCodePoints.size + subtitleCodePoints.size
        if (totalCodePoints == 0) {
            return
        }

        for (frame in 0 until totalCodePoints) {
            delayUntil(startsAt + duration * frame / totalCodePoints)

            val visibleCodePoints = if (revealing) frame + 1 else totalCodePoints - frame - 1
            val visibleTitleCount = minOf(visibleCodePoints, titleCodePoints.size)
            val visibleSubtitleCount = (visibleCodePoints - titleCodePoints.size)
                .coerceIn(0, subtitleCodePoints.size)

            showTitle(
                title = String(titleCodePoints, 0, visibleTitleCount),
                subtitle = String(subtitleCodePoints, 0, visibleSubtitleCount),
                titleColor = titleColor,
                subtitleColor = subtitleColor,
            )
        }
    }

    private suspend fun showTitle(
        title: String,
        subtitle: String,
        titleColor: TextColor,
        subtitleColor: TextColor,
    ) {
        if (!player.isOnline) {
            return
        }

        player.showTitle(
            Title.title(
                Component.text(title).color(titleColor),
                Component.text(subtitle).color(subtitleColor),
                Title.Times.times(JavaDuration.ZERO, JavaDuration.ofHours(1), JavaDuration.ZERO),
            )
        )
    }

    private suspend fun resetTitle() {
        if (player.isOnline) {
            player.resetTitle()
        }
    }

    private suspend fun delayUntil(offset: Duration) {
        val targetNanos = startedAtNanos + offset.inWholeNanoseconds
        val remainingNanos = targetNanos - System.nanoTime()
        if (remainingNanos > 0L) {
            delay(remainingNanos.nanoseconds)
        }
    }
}
