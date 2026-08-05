package com.github.bea4dev.snowDawn.listeners

import com.github.bea4dev.snowDawn.SnowDawn
import com.github.bea4dev.snowDawn.item.ItemRegistry
import com.github.bea4dev.snowDawn.item.getItem
import com.github.bea4dev.snowDawn.save.ServerData
import com.github.bea4dev.snowDawn.world.WorldRegistry
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.CompassMeta

object CompassInventory {
    const val SLOT = 8
    private const val DESTINATION_X = 999
    private const val DESTINATION_Y = 64
    private const val DESTINATION_Z = 8

    fun isLocked(player: Player): Boolean = true

    fun createItemStack(): ItemStack {
        val compass = ItemRegistry.COMPASS.createItemStack()
        if (!ServerData.compassDestinationUnlocked) {
            return compass
        }

        val meta = compass.itemMeta as CompassMeta
        meta.lodestone = Location(
            WorldRegistry.SNOW_LAND,
            DESTINATION_X.toDouble(),
            DESTINATION_Y.toDouble(),
            DESTINATION_Z.toDouble()
        )
        meta.isLodestoneTracked = false
        compass.itemMeta = meta
        return compass
    }

    fun ensure(player: Player) {
        if (!isLocked(player)) {
            return
        }

        val inventory = player.inventory
        for (slot in inventory.storageContents.indices) {
            if (slot != SLOT && inventory.getItem(slot)?.getItem() == ItemRegistry.COMPASS) {
                inventory.setItem(slot, null)
            }
        }

        if (isCurrentCompass(inventory.getItem(SLOT))) {
            return
        }

        val displacedItem = inventory.getItem(SLOT)
        inventory.setItem(SLOT, null)
        if (displacedItem != null && !displacedItem.type.isAir) {
            val availableSlot = inventory.storageContents.indices
                .firstOrNull { slot -> slot != SLOT && inventory.getItem(slot) == null }
            if (availableSlot == null) {
                player.world.dropItemNaturally(player.location, displacedItem)
            } else {
                inventory.setItem(availableSlot, displacedItem)
            }
        }

        inventory.setItem(SLOT, createItemStack())
    }

    private fun isCurrentCompass(item: ItemStack?): Boolean {
        if (item?.getItem() != ItemRegistry.COMPASS || item.amount != 1) {
            return false
        }

        val meta = item.itemMeta as? CompassMeta ?: return false
        if (!ServerData.compassDestinationUnlocked) {
            return !meta.hasLodestone()
        }

        val lodestone = meta.lodestone ?: return false
        return lodestone.world == WorldRegistry.SNOW_LAND &&
            lodestone.blockX == DESTINATION_X &&
            lodestone.blockY == DESTINATION_Y &&
            lodestone.blockZ == DESTINATION_Z &&
            !meta.isLodestoneTracked
    }
}

class CompassListener : Listener {
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        if (!CompassInventory.isLocked(player)) {
            return
        }

        val clickedLockedSlot = event.clickedInventory == player.inventory && event.slot == CompassInventory.SLOT
        val movingCompass = event.currentItem?.getItem() == ItemRegistry.COMPASS ||
            event.cursor?.getItem() == ItemRegistry.COMPASS
        val swappingWithLockedSlot = event.hotbarButton == CompassInventory.SLOT
        if (clickedLockedSlot || movingCompass || swappingWithLockedSlot) {
            event.isCancelled = true
            CompassInventory.ensure(player)
        }
    }

    @EventHandler
    fun onInventoryDrag(event: InventoryDragEvent) {
        val player = event.whoClicked as? Player ?: return
        if (!CompassInventory.isLocked(player)) {
            return
        }

        if (event.oldCursor.getItem() == ItemRegistry.COMPASS) {
            event.isCancelled = true
            CompassInventory.ensure(player)
            return
        }

        val touchesLockedSlot = event.rawSlots.any { rawSlot ->
            event.view.getInventory(rawSlot) == player.inventory &&
                event.view.convertSlot(rawSlot) == CompassInventory.SLOT
        }
        if (touchesLockedSlot) {
            event.isCancelled = true
            CompassInventory.ensure(player)
        }
    }

    @EventHandler
    fun onDrop(event: PlayerDropItemEvent) {
        if (CompassInventory.isLocked(event.player) && event.itemDrop.itemStack.getItem() == ItemRegistry.COMPASS) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onSwapHands(event: PlayerSwapHandItemsEvent) {
        if (!CompassInventory.isLocked(event.player)) {
            return
        }

        if (event.mainHandItem.getItem() == ItemRegistry.COMPASS ||
            event.offHandItem.getItem() == ItemRegistry.COMPASS
        ) {
            event.isCancelled = true
            CompassInventory.ensure(event.player)
        }
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        if (!CompassInventory.isLocked(event.entity)) {
            return
        }

        event.keepInventory = false
        event.drops.removeIf { item -> item.getItem() == ItemRegistry.COMPASS }
    }

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        if (!CompassInventory.isLocked(event.player)) {
            return
        }

        Bukkit.getScheduler().runTask(SnowDawn.plugin, Runnable {
            CompassInventory.ensure(event.player)
        })
    }
}
