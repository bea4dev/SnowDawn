package com.github.bea4dev.snowDawn.item

import com.github.bea4dev.snowDawn.item.weapon.BluePrint
import com.github.bea4dev.snowDawn.item.weapon.Weapon
import com.github.bea4dev.snowDawn.text.Text
import de.tr7zw.changeme.nbtapi.NBT
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.function.Function

object ItemRegistry {
    internal val itemIdMap = HashMap<String, Item>()

    val SCRAP = Item("scrap", Material.NETHERITE_SCRAP, 1, 2, Text.ITEM_SCRAP, listOf(Text.ITEM_SCRAP_LORE_0))
    val SCRAP_PIPE = Weapon(
        "scrap_pipe", Material.SHEARS, 1, 2, Text.ITEM_SCRAP_PIPE, listOf(
            Text.ITEM_SCRAP_PIPE_LORE_0,
            Text.ITEM_SCRAP_PIPE_LORE_1,
            Text.ITEM_SCRAP_PIPE_LORE_2,
            Text.ITEM_SCRAP_PIPE_LORE_3,
            Text.ITEM_SCRAP_PIPE_LORE_4
        ), 10, 5.0, 4.0F
    )
    val STURDY_PIPE = Weapon(
        "sturdy_pipe", Material.SHEARS, 1, 2, Text.ITEM_STURDY_PIPE, listOf(
            Text.ITEM_STURDY_PIPE_LORE_0,
            Text.ITEM_SCRAP_PIPE_LORE_1,
            Text.ITEM_SCRAP_PIPE_LORE_2,
            Text.ITEM_SCRAP_PIPE_LORE_3,
            Text.ITEM_SCRAP_PIPE_LORE_4
        ), 15, 5.0, 16.0F
    )
    val BLUE_PRINT_STURDY_PIPE = BluePrint(
        "blue_print_sturdy_pipe",
        Material.PAPER,
        2,
        3,
        Text.ITEM_BLUE_PRINT_STURDY_PIPE,
        listOf(),
        listOf(STURDY_PIPE)
    )
    val ICE = Item("ice", Material.ICE, 0, 0, Text.ITEM_ICE, listOf())
    val COAL = Item("coal", Material.COAL, 0, 0, Text.ITEM_COAL, listOf())
    val TORCH = Item("torch", Material.TORCH, 0, 1, Text.ITEM_TORCH, listOf(Text.ITEM_TORCH_LORE_0))
    val CAMPFIRE = Item("campfire", Material.CAMPFIRE, 0, 1, Text.ITEM_CAMPFIRE, listOf())
    val FLINT_AND_STEEL = Item("flint_and_steel", Material.FLINT_AND_STEEL, 0, 1, Text.ITEM_FLINT_AND_STEEL, listOf())
    val STONE = Item("stone", Material.COBBLESTONE, 0, 1, Text.ITEM_STONE, listOf())
    val SAPLING = Item("sapling", Material.SPRUCE_SAPLING, 0, 1, Text.ITEM_SAPLING, listOf())
    val WOOD = Item("wood", Material.SPRUCE_LOG, 0, 1, Text.ITEM_WOOD, listOf())
    val CHEST = Item("chest", Material.CHEST, 0, 1, Text.ITEM_CHEST, listOf())
    val COLD_SLEEP_KEY = Item(
        "cold_sleep_key",
        Material.TRIPWIRE_HOOK,
        0,
        1,
        Text.ITEM_COLD_SLEEP_KEY,
        listOf(Text.ITEM_COLD_SLEEP_KEY_LORE_0)
    )
    val CRAFTING_TABLE = Item("crafting_table", Material.CRAFTING_TABLE, 0, 1, Text.ITEM_CRAFTING_TABLE, listOf())
    val BLUE_PRINT_CHEST =
        BluePrint("blue_print_chest", Material.PAPER, 0, 1, Text.ITEM_BLUE_PRINT_CHEST, listOf(), listOf(CHEST))
    val BOAT = Item("boat", Material.SPRUCE_BOAT, 0, 1, Text.ITEM_BOAT, listOf())
    val BLUE_PRINT_BOAT = BluePrint("blue_print_boat", Material.PAPER, 0, 1, Text.ITEM_BOAT, listOf(), listOf(BOAT))
    val DIRT = Item("dirt", Material.DIRT, 0, 1, Text.ITEM_DIRT, listOf())
    val CATALYST = Item("catalyst", Material.GUNPOWDER, 1, 2, Text.ITEM_CATALYST, listOf(Text.ITEM_CATALYST_LORE_0))
    val BLUE_PRINT_CATALYST = BluePrint(
        "blue_print_catalyst",
        Material.PAPER,
        2,
        3,
        Text.ITEM_BLUE_PRINT_CATALYST,
        listOf(),
        listOf(CATALYST)
    )
    val FERTILIZER =
        Item("fertilizer", Material.BONE_MEAL, 0, 1, Text.ITEM_FERTILIZER, listOf(Text.ITEM_FERTILIZER_LORE_0))
    val FUEL = Item("fuel", Material.CHARCOAL, 1, 2, Text.ITEM_FUEL, listOf(Text.ITEM_FUEL_LORE_0))
    val WOODEN_PICKAXE = Item("scrap_pickaxe", Material.WOODEN_PICKAXE, 1, 2, Text.ITEM_SCRAP_PICKAXE, listOf())
    val STONE_PICKAXE = Item("stone_pickaxe", Material.STONE_PICKAXE, 0, 1, Text.ITEM_STONE_PICKAXE, listOf())
    val STONE_HOE = Item("stone_hoe", Material.STONE_HOE, 0, 1, Text.ITEM_STONE_HOE, listOf())
    val STONE_SHOVEL = Item("stone_shovel", Material.STONE_SHOVEL, 0, 1, Text.ITEM_STONE_SHOVEL, listOf())
    val STONE_AXE = Item("stone_axe", Material.STONE_AXE, 0, 1, Text.ITEM_STONE_AXE, listOf())
    val FURNACE = Item("furnace", Material.FURNACE, 0, 1, Text.ITEM_FURNACE, listOf())
    val COPPER_INGOT = Item("copper_ingot", Material.COPPER_INGOT, 0, 1, Text.ITEM_COPPER_INGOT, listOf())
    val IRON_HELMET = Item("iron_helmet", Material.IRON_HELMET, 0, 1, Text.ITEM_IRON_HELMET, listOf())
    val IRON_CHESTPLATE =
        Item("iron_chestplate", Material.IRON_CHESTPLATE, 0, 1, Text.ITEM_IRON_CHEST_PLATE, listOf())
    val IRON_LEGGINGS =
        Item("iron_leggings", Material.IRON_LEGGINGS, 0, 1, Text.ITEM_IRON_LEGGINGS, listOf())
    val IRON_BOOTS = Item("iron_boots", Material.IRON_BOOTS, 0, 1, Text.ITEM_IRON_BOOTS, listOf())
    val STORY_MEMO = Item("empty_memo", Material.PAPER, 0, 1, Text.EMPTY, listOf())
    val IRON_INGOT = Item("iron_ingot", Material.IRON_INGOT, 0, 1, Text.ITEM_IRON_INGOT, listOf())
    val DIAMOND = Item("diamond", Material.DIAMOND, 0, 1, Text.ITEM_DIAMOND, listOf())
    val IRON_PICKAXE = Item("iron_pickaxe", Material.IRON_PICKAXE, 0, 1, Text.ITEM_IRON_PICKAXE, listOf())
    val IRON_HOE = Item("iron_hoe", Material.IRON_HOE, 0, 1, Text.ITEM_IRON_HOE, listOf())
    val IRON_SHOVEL = Item("iron_shovel", Material.IRON_SHOVEL, 0, 1, Text.ITEM_IRON_SHOVEL, listOf())
    val IRON_AXE = Item("iron_axe", Material.IRON_AXE, 0, 1, Text.ITEM_IRON_AXE, listOf())
    val COMPASS = Item(
        "compass",
        Material.COMPASS,
        1,
        2,
        Text.ITEM_COMPASS,
        listOf(Text.ITEM_COMPASS_LORE_0, Text.ITEM_COMPASS_LORE_1)
    )

    operator fun get(id: String): Item? {
        return itemIdMap[id]
    }
}

const val ITEM_ID_TAG = "item_id"

open class Item(
    val id: String,
    val material: Material,
    val customModelData: Int,
    val inactiveModelData: Int?,
    val displayName: Text,
    val lore: List<Text>,
    val fontIcon: String? = null,
) {
    init {
        ItemRegistry.itemIdMap[id] = this
    }

    open fun createItemStack(): ItemStack {
        return ItemStack(material).also { item ->
            val meta = item.itemMeta
            meta.setCustomModelData(customModelData)

            meta.displayName(Component.translatable(displayName.toString()))
            meta.lore(lore.map { line ->
                Component.translatable(line.toString()).color(NamedTextColor.GRAY).decoration(
                    TextDecoration.ITALIC, false
                )
            })
            item.itemMeta = meta
        }.also { item ->
            NBT.modify(item) { nbt -> nbt.setString(ITEM_ID_TAG, id) }
        }
    }

    open fun createInactiveItemStack(): ItemStack {
        return createItemStack().also { item ->
            val meta = item.itemMeta
            meta.setCustomModelData(inactiveModelData)
            item.itemMeta = meta
        }
    }
}

fun ItemStack.getID(): String? {
    if (this.isEmpty) {
        return null
    }
    return NBT.get(this, Function { nbt -> nbt.getOrNull(ITEM_ID_TAG, String::class.java) })
}

fun ItemStack.getItem(): Item? {
    return this.getID()?.let { id -> ItemRegistry[id] }
}
