package com.github.bea4dev.snowDawn.toast

import net.kyori.adventure.text.Component
import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementHolder
import net.minecraft.advancements.AdvancementProgress
import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.AdvancementRewards
import net.minecraft.advancements.AdvancementType
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.DisplayInfo
import net.minecraft.advancements.critereon.ImpossibleTrigger
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket
import net.minecraft.resources.ResourceLocation
import org.bukkit.Material
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.craftbukkit.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.Optional

private val TOAST_ADVANCEMENT_KEY = ResourceLocation.parse("tpost:toast")
private val DUMMY_CRITERION = Criterion(
    ImpossibleTrigger(),
    ImpossibleTrigger.TriggerInstance()
)
private val DUMMY_REQUIREMENTS = AdvancementRequirements(listOf(listOf("dummy")))
private val DUMMY_PROGRESS = AdvancementProgress().also {
    it.update(DUMMY_REQUIREMENTS)
    it.grantProgress("dummy")
}
private val EMPTY_REWARDS = AdvancementRewards(0, listOf(), listOf(), Optional.empty())

fun Player.sendToast(toast: ToastNotification) {
    toast.send(this)
}

class ToastNotification(
    private val title: Component,
    private val icon: ItemStack,
    private val frame: ToastKind
) {

    @Synchronized
    fun send(player: Player) {
        val connection = (player as CraftPlayer).handle.connection
        connection.sendPacket(packet(true))
        connection.sendPacket(packet(false))
    }

    fun packet(init: Boolean): Packet<*> {
        val advancements = mutableListOf<AdvancementHolder>()
        val removes = mutableSetOf<ResourceLocation>()
        val progress = mutableMapOf<ResourceLocation, AdvancementProgress>()

        if (init) {
            val displayInfo = DisplayInfo(
                CraftItemStack.asNMSCopy(icon),
                title.asNMS(),
                Component.empty().asNMS(),
                Optional.empty(),
                frame.advancementType,
                true,
                false,
                true
            )
            val advancement = Advancement(
                Optional.empty(),
                Optional.of(displayInfo),
                EMPTY_REWARDS,
                mapOf(Pair("dummy", DUMMY_CRITERION)),
                DUMMY_REQUIREMENTS,
                false
            )

            advancements.add(AdvancementHolder(TOAST_ADVANCEMENT_KEY, advancement))
            progress[TOAST_ADVANCEMENT_KEY] = DUMMY_PROGRESS
        } else {
            removes.add(TOAST_ADVANCEMENT_KEY)
        }

        return ClientboundUpdateAdvancementsPacket(false, advancements, removes, progress)
    }
}

private fun Component.asNMS(): net.minecraft.network.chat.Component {
    val itemStack = ItemStack(Material.PAPER)

    val meta = itemStack.itemMeta
    meta.displayName(this)
    itemStack.itemMeta = meta
    val nmsItem = CraftItemStack.asNMSCopy(itemStack)

    return nmsItem.displayName
}

enum class ToastKind(val advancementType: AdvancementType) {
    TASK(AdvancementType.TASK),
    GOAL(AdvancementType.GOAL),
    CHALLENGE(AdvancementType.CHALLENGE),
}