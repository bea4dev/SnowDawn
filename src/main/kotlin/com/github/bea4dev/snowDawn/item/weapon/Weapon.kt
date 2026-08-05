package com.github.bea4dev.snowDawn.item.weapon

import com.github.bea4dev.snowDawn.entity.mob.Phage
import com.github.bea4dev.snowDawn.item.Item
import com.github.bea4dev.snowDawn.item.getItem
import com.github.bea4dev.snowDawn.player.PlayerManager
import com.github.bea4dev.snowDawn.text.Text
import com.github.bea4dev.vanilla_source.api.util.collision.CollideOption
import com.github.bea4dev.vanilla_source.api.world.cache.AsyncWorldCache
import de.tr7zw.changeme.nbtapi.NBT
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.FluidCollisionMode
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.function.Function
import kotlin.random.Random

class Weapon(
    id: String,
    material: Material,
    customModelData: Int,
    inactiveModelData: Int?,
    displayName: Text,
    lore: List<Text>,
    private val maxAttackTick: Int,
    private val parryWaveRange: Double,
    private val attackDamage: Float,
) : Item(
    id, material, customModelData, inactiveModelData, displayName, lore
) {
    private val parryCheckRange = 3.0

    override fun createItemStack(): ItemStack {
        return super.createItemStack()
            .also { item ->
                val meta = item.itemMeta
                val lore = meta.lore()?.toMutableList() ?: mutableListOf()
                val displayedAttackDamage = if (attackDamage % 1.0F == 0.0F) {
                    attackDamage.toInt().toString()
                } else {
                    attackDamage.toString()
                }

                lore.add(
                    Component.translatable(
                        Text.ITEM_WEAPON_ATTACK_DAMAGE.toString(),
                        Component.text(displayedAttackDamage),
                    ).color(NamedTextColor.DARK_GREEN)
                        .decoration(TextDecoration.ITALIC, false)
                )
                meta.lore(lore)
                item.itemMeta = meta
            }
            .also { item -> NBT.modify(item) { nbt -> nbt.setInteger(WeaponTag.MAX_ATTACK_TICK.key, maxAttackTick) } }
    }

    fun onSwing(player: Player, item: ItemStack) {
        val attackTick = NBT.get(item, Function { nbt -> nbt.getOrDefault(WeaponTag.ATTACK_TICK.key, maxAttackTick) })

        if (attackTick == 0) {
            return
        }

        if (attackTick == maxAttackTick) {
            player.playSound(Sound.sound(org.bukkit.Sound.ENTITY_PLAYER_ATTACK_SWEEP, Sound.Source.PLAYER, 1.0F, 0.8F))
        }

        NBT.modify(item) { nbt -> nbt.setInteger(WeaponTag.ATTACK_TICK.key, 0) }

        val location = player.eyeLocation
        val world = AsyncWorldCache.getAsyncWorld(location.world.name)

        val result =
            world.rayTrace(location.toVector(), location.direction, 3.0, CollideOption(FluidCollisionMode.NEVER, true))

        if (result != null) {
            val hitEntity = result.hitEntity
            if (hitEntity != null) {
                val damage = if (attackTick == maxAttackTick) {
                    attackDamage
                } else {
                    attackDamage / 2.0F
                }
                for (entity in world.getNearbyEntities(
                    result.hitPosition.x,
                    result.hitPosition.y,
                    result.hitPosition.z,
                    2.0
                )) {
                    if (entity is Phage) {
                        entity.damage(player, damage, attackTick == maxAttackTick)
                    }
                }
            }
        }

        if (attackTick != maxAttackTick) {
            return
        }

        val parryCenter = location.toVector().add(location.direction.multiply(parryCheckRange - 0.5))
        val parryEntities = world.getNearbyEntities(parryCenter.x, parryCenter.y, parryCenter.z, parryCheckRange)
        var successParry = false
        for (entity in parryEntities) {
            if (entity is Phage && entity.tryParry()) {
                successParry = true
            }
        }

        if (!successParry) {
            return
        }

        PlayerManager.ONLINE_PLAYERS.forEach {
            it.playSound(
                Sound.sound(
                    org.bukkit.Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, Sound.Source.PLAYER, 1.0F, 1.2F
                ),
                location.x,
                location.y,
                location.z,
            )
        }

        for (parryEntity in world.getNearbyEntities(parryCenter.x, parryCenter.y, parryCenter.z, parryWaveRange)) {
            if (parryEntity is Phage) {
                parryEntity.parryBy(player)
            }
        }

        player.velocity = location.direction.multiply(-0.8)

        // effect
        for (i in 0..<20) {
            val size = 2.5
            val x = Random.nextDouble(size) - (size / 2.0)
            val y = Random.nextDouble(size) - (size / 2.0)
            val z = Random.nextDouble(size) - (size / 2.0)
            PlayerManager.ONLINE_PLAYERS.forEach {
                it.spawnParticle(Particle.CRIT, parryCenter.x, parryCenter.y, parryCenter.z, 0, x, y, z, 1.5)
            }
        }
        PlayerManager.ONLINE_PLAYERS.forEach {
            it.spawnParticle(
                Particle.FLASH, parryCenter.x, parryCenter.y, parryCenter.z, 0, 0.0, 0.0, 0.0, 0.5
            )
        }
    }
}

fun ItemStack.getWeapon(): Weapon? {
    val item = getItem() ?: return null

    val hasMaxAttackTick = NBT.get(this, Function { nbt -> nbt.hasTag(WeaponTag.MAX_ATTACK_TICK.key) })
    if (!hasMaxAttackTick) {
        return null
    }

    return item as Weapon
}
