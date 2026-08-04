package com.github.bea4dev.snowDawn.coroutine

import com.github.bea4dev.snowDawn.save.PlayerDataRegistry
import com.github.bea4dev.snowDawn.scenario.DEFAULT_TEXT_BOX
import com.github.bea4dev.snowDawn.scenario.lowSound
import com.github.bea4dev.snowDawn.text.Text
import com.github.bea4dev.vanilla_source.api.text.TextBox
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import org.bukkit.entity.Player

object PlayerCoroutineEventRegistry {
    private val events = mutableMapOf<String, PlayerCoroutineEvent>()

    val FIRST_CRAFT_INVENTORY_CLOSED = register("first_craft_inventory_closed_after_tutorial")

    @Synchronized
    private fun register(id: String): PlayerCoroutineEvent {
        return PlayerCoroutineEvent(id).also { event -> events[id] = event }
    }

    init {
        FIRST_CRAFT_INVENTORY_CLOSED.register { player ->
            TextBox(
                player,
                DEFAULT_TEXT_BOX,
                Text.LUCAS[player],
                1,
                Text.FIRST_CRAFT_0[player]
            ).lowSound().play().await()

            TextBox(
                player,
                DEFAULT_TEXT_BOX,
                Text.LUCAS[player],
                1,
                Text.FIRST_CRAFT_1[player]
            ).lowSound().play().await()
        }
    }
}

class PlayerCoroutineEvent internal constructor(val id: String) {
    private val handlers = mutableListOf<suspend (Player) -> Unit>()

    @Synchronized
    fun register(handler: suspend (Player) -> Unit) {
        handlers.add(handler)
    }

    fun fire(player: Player) {
        val progress = PlayerDataRegistry[player].completedCoroutineEvents
        if (!progress.add(id)) {
            return
        }

        val registeredHandlers = getHandlers()
        MainThread.launch {
            registeredHandlers.forEach { handler -> handler(player) }
        }
    }

    @Synchronized
    private fun getHandlers(): List<suspend (Player) -> Unit> = handlers.toList()
}

object FirstCraftCloseEventTracker {
    private val craftedPlayers = ConcurrentHashMap.newKeySet<UUID>()

    fun onCraft(player: Player) {
        if (!PlayerDataRegistry[player].finishedTutorial) {
            craftedPlayers.remove(player.uniqueId)
            return
        }

        craftedPlayers.add(player.uniqueId)
    }

    fun onCraftInventoryClose(player: Player) {
        if (!PlayerDataRegistry[player].finishedTutorial) {
            craftedPlayers.remove(player.uniqueId)
            return
        }

        if (!craftedPlayers.remove(player.uniqueId)) {
            return
        }

        PlayerCoroutineEventRegistry.FIRST_CRAFT_INVENTORY_CLOSED.fire(player)
    }
}
