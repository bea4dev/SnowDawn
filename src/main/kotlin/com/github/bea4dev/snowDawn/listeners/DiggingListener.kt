package com.github.bea4dev.snowDawn.listeners

import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockDamageAbortEvent
import org.bukkit.event.player.PlayerInteractEvent

class DiggingListener : Listener {
    /*
    @EventHandler
    fun onStartDigging(event: PlayerInteractEvent) {
        if (event.action != Action.LEFT_CLICK_BLOCK) {
            return
        }

        val player = event.player
        val item = player.inventory.itemInMainHand
        val block = event.clickedBlock!!

        if (item.type != Material.IRON_PICKAXE && block.type == Material.DEEPSLATE) {
            player.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED)!!.baseValue = 0.0
        }
    }

    @EventHandler
    fun onStopDigging(event: BlockDamageAbortEvent) {
        val player = event.player
        val attribute = player.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED)!!
        attribute.baseValue = attribute.defaultValue
    }*/
}