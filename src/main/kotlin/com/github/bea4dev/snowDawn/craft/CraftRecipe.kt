package com.github.bea4dev.snowDawn.craft

import com.github.bea4dev.snowDawn.item.Item
import com.github.bea4dev.snowDawn.item.ItemRegistry
import com.github.bea4dev.snowDawn.item.getItem
import com.github.bea4dev.snowDawn.save.ServerData
import com.github.bea4dev.snowDawn.text.Text
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object CraftRecipeRegistry {
    val RECIPES = listOf(
        CraftRecipe(listOf(RequiredItem(ItemRegistry.SCRAP, 2)), ItemRegistry.SCRAP_PIPE),
        CraftRecipe(
            listOf(RequiredItem(ItemRegistry.IRON_INGOT, 6), RequiredItem(ItemRegistry.DIAMOND, 2)),
            ItemRegistry.STURDY_PIPE
        ),
        CraftRecipe(listOf(RequiredItem(ItemRegistry.WOOD, 1)), ItemRegistry.WOODEN_PICKAXE),
        CraftRecipe(listOf(RequiredItem(ItemRegistry.COAL, 1)), ItemRegistry.TORCH, 2),
        CraftRecipe(listOf(RequiredItem(ItemRegistry.COAL, 4), RequiredItem(ItemRegistry.WOOD, 2)), ItemRegistry.CAMPFIRE),
        CraftRecipe(listOf(RequiredItem(ItemRegistry.WOOD, 1)), ItemRegistry.CHEST),
        CraftRecipe(listOf(RequiredItem(ItemRegistry.WOOD, 1)), ItemRegistry.CRAFTING_TABLE),
        CraftRecipe(listOf(RequiredItem(ItemRegistry.WOOD, 2)), ItemRegistry.BOAT),
        CraftRecipe(listOf(RequiredItem(ItemRegistry.SCRAP, 1)), ItemRegistry.FLINT_AND_STEEL),
        CraftRecipe(listOf(RequiredItem(ItemRegistry.STONE, 5)), ItemRegistry.STONE_PICKAXE),
        CraftRecipe(listOf(RequiredItem(ItemRegistry.STONE, 8)), ItemRegistry.FURNACE),
        CraftRecipe(
            listOf(RequiredItem(ItemRegistry.COPPER_INGOT, 9), RequiredItem(ItemRegistry.SCRAP, 3)),
            ItemRegistry.COMPASS
        ),
        CraftRecipe(listOf(RequiredItem(ItemRegistry.SCRAP, 1)), ItemRegistry.CATALYST, 2),
        CraftRecipe(listOf(RequiredItem(ItemRegistry.IRON_INGOT, 6)), ItemRegistry.IRON_HELMET),
        CraftRecipe(listOf(RequiredItem(ItemRegistry.IRON_INGOT, 8)), ItemRegistry.IRON_CHESTPLATE),
        CraftRecipe(listOf(RequiredItem(ItemRegistry.IRON_INGOT, 6)), ItemRegistry.IRON_LEGGINGS),
        CraftRecipe(listOf(RequiredItem(ItemRegistry.IRON_INGOT, 4)), ItemRegistry.IRON_BOOTS),
        CraftRecipe(
            listOf(RequiredItem(ItemRegistry.IRON_INGOT, 3), RequiredItem(ItemRegistry.SCRAP, 2)),
            ItemRegistry.IRON_PICKAXE
        ),
        CraftRecipe(
            listOf(RequiredItem(ItemRegistry.STONE, 2), RequiredItem(ItemRegistry.SCRAP, 2)),
            ItemRegistry.STONE_HOE
        ),
        CraftRecipe(
            listOf(RequiredItem(ItemRegistry.STONE, 1), RequiredItem(ItemRegistry.SCRAP, 2)),
            ItemRegistry.STONE_SHOVEL
        ),
        CraftRecipe(
            listOf(RequiredItem(ItemRegistry.STONE, 3), RequiredItem(ItemRegistry.SCRAP, 2)),
            ItemRegistry.STONE_AXE
        ),
        CraftRecipe(
            listOf(RequiredItem(ItemRegistry.IRON_INGOT, 2), RequiredItem(ItemRegistry.SCRAP, 2)),
            ItemRegistry.IRON_HOE
        ),
        CraftRecipe(
            listOf(RequiredItem(ItemRegistry.IRON_INGOT, 1), RequiredItem(ItemRegistry.SCRAP, 2)),
            ItemRegistry.IRON_SHOVEL
        ),
        CraftRecipe(
            listOf(RequiredItem(ItemRegistry.IRON_INGOT, 3), RequiredItem(ItemRegistry.SCRAP, 2)),
            ItemRegistry.IRON_AXE
        ),
    )
}

private val globalItemCraftMap = mutableMapOf<Item, CraftRecipe>()

class CraftRecipe(
    val requiredItems: List<RequiredItem>, val craftItem: Item, val craftItemAmount: Int = 1
) {
    init {
        globalItemCraftMap[craftItem] = this
    }

    fun canCraft(player: Player): Boolean {
        if (!ServerData.craftableItems.contains(craftItem.id)) {
            return false
        }

        root@ for (requiredItem in requiredItems) {
            var amount = 0
            for (itemStack in player.inventory.iterator()) {
                val item = itemStack?.getItem() ?: continue

                if (requiredItem.item == item) {
                    amount += itemStack.amount

                    if (amount >= requiredItem.amount) {
                        continue@root
                    }
                }
            }
            return false
        }
        return true
    }

    fun createCraftIconFor(player: Player): ItemStack {
        if (!ServerData.craftableItems.contains(craftItem.id)) {
            val item = ItemStack(Material.IRON_BARS)
            val meta = item.itemMeta
            meta.displayName(
                Component.translatable(Text.UNCRAFTABLE.toString())
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)
                    .decorate(TextDecoration.BOLD)
            )
            item.itemMeta = meta
            return item
        }

        val canCraft = this.canCraft(player)
        val item = if (canCraft) {
            craftItem.createItemStack().also { item -> item.amount = craftItemAmount }
        } else {
            craftItem.createInactiveItemStack()
        }

        val meta = item.itemMeta
        val lore = meta.lore() ?: listOf()

        val newLore = mutableListOf<Component>()

        if (canCraft) {
            newLore.add(Component.translatable(Text.CRAFT_REQUIRED.toString()).color(NamedTextColor.GRAY))
        } else {
            newLore.add(Component.translatable(Text.CANNOT_CRAFT.toString()).color(NamedTextColor.RED))
        }

        for (required in requiredItems) {
            var has = false
            var amount = 0
            for (itemStack in player.inventory.iterator()) {
                val item = itemStack?.getItem() ?: continue

                if (required.item == item) {
                    amount += itemStack.amount

                    if (amount >= required.amount) {
                        has = true
                        break
                    }
                }
            }

            val ok = if (has) {
                Component.text(" ✔ ").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)
            } else {
                Component.text(" ✘ ").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)
            }
            newLore.add(
                ok.append(
                    Component.text(required.item.fontIcon ?: "")
                ).append(
                    Component.translatable(required.item.displayName.toString()).color(NamedTextColor.GRAY)
                ).append(
                    Component.text(" x${required.amount}").color(NamedTextColor.GRAY)
                )
            )
        }

        newLore.add(Component.empty())
        newLore.addAll(lore)

        meta.lore(newLore)

        item.itemMeta = meta

        return item
    }
}

class RequiredItem(val item: Item, val amount: Int = 1)
