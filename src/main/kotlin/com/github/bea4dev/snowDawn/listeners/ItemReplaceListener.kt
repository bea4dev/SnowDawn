package com.github.bea4dev.snowDawn.listeners

import com.github.bea4dev.snowDawn.craft.CraftGUI
import com.github.bea4dev.snowDawn.item.Item
import com.github.bea4dev.snowDawn.item.ItemRegistry
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class ItemReplaceListener : Listener {
    @EventHandler
    fun onPickupItem(event: EntityPickupItemEvent) {
        if (event.entity !is Player) {
            return
        }
        val item = replace(event.item.itemStack) ?: return
        event.item.itemStack = item.createItemStack().also { item -> item.amount = event.item.itemStack.amount }
    }

    @EventHandler
    fun onItemClick(event: InventoryClickEvent) {
        if (event.clickedInventory?.holder is CraftGUI) {
            return
        }
        val item = event.currentItem ?: return
        val newItem = replace(item) ?: return
        event.currentItem = newItem.createItemStack().also { newItem -> newItem.amount = item.amount }
    }

    private fun replace(item: ItemStack): Item? {
        if (item.itemMeta?.hasCustomModelData() == true && (item.itemMeta?.customModelData != 0)) {
            return null
        }

        return when (item.type) {
            Material.TORCH -> ItemRegistry.TORCH
            Material.CAMPFIRE -> ItemRegistry.CAMPFIRE
            Material.FLINT_AND_STEEL -> ItemRegistry.FLINT_AND_STEEL
            Material.SPRUCE_LOG -> ItemRegistry.WOOD
            Material.COAL -> ItemRegistry.COAL
            Material.CHARCOAL -> ItemRegistry.COAL
            Material.COBBLESTONE -> ItemRegistry.STONE
            Material.FURNACE -> ItemRegistry.FURNACE
            Material.COPPER_INGOT -> ItemRegistry.COPPER_INGOT
            Material.IRON_INGOT -> ItemRegistry.IRON_INGOT
            Material.DIAMOND -> ItemRegistry.DIAMOND
            Material.SPRUCE_SAPLING -> ItemRegistry.SAPLING
            Material.DIRT -> ItemRegistry.DIRT
            Material.COMPASS -> ItemRegistry.COMPASS
            else -> null
        }
    }
}
