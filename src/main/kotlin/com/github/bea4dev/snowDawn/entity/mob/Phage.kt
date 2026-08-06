package com.github.bea4dev.snowDawn.entity.mob

import com.github.bea4dev.snowDawn.SnowDawn
import com.github.bea4dev.snowDawn.item.ItemRegistry
import com.github.bea4dev.snowDawn.player.PlayerManager
import com.github.bea4dev.vanilla_source.api.VanillaSourceAPI
import com.github.bea4dev.vanilla_source.api.entity.EngineEntity
import com.github.bea4dev.vanilla_source.api.entity.ai.navigation.GoalSelector
import com.github.bea4dev.vanilla_source.api.entity.ai.navigation.Navigator
import com.github.bea4dev.vanilla_source.api.entity.ai.navigation.goal.PathfindingGoal
import com.github.bea4dev.vanilla_source.api.entity.ai.pathfinding.BlockPosition
import com.github.bea4dev.vanilla_source.api.entity.tick.TickThread
import com.github.bea4dev.vanilla_source.api.util.collision.EngineBoundingBox
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minecraft.world.entity.LivingEntity
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import kotlin.math.pow
import kotlin.random.Random

enum class PhageTier(
    val health: Float,
    val attackDamage: Float,
    val armor: Material,
) {
    TIER_1(10.0F, 2.0F, Material.AIR),
    TIER_2(20.0F, 8.0F, Material.STONE),
    TIER_3(40.0F, 8.0F, Material.DEEPSLATE),
    TIER_4(80.0F, 16.0F, Material.BEDROCK),
}

class Phage(
    location: Location,
    private var health: Float,
    private val attackDamage: Float,
) : EngineEntity(
    SnowDawn.ENTITY_THREAD.threadLocalCache.getGlobalWorld(location.world.name),
    VanillaSourceAPI.getInstance().nmsHandler.createNMSEntityController(
        location.world,
        location.x,
        location.y,
        location.z,
        EntityType.ITEM_DISPLAY,
        null
    ),
    SnowDawn.ENTITY_THREAD,
    null
) {
    constructor(location: Location, tier: PhageTier) : this(location, tier.health, tier.attackDamage) {
        block = tier.armor
    }

    val bukkitWorld = location.world
    private var state = PhageState.IDLE
    private var stateVariables = PhageStateVariables()
    var target: Player? = null
    private var tick = 0
    private var noDamageTick = 0

    private val attackRange = 4.0
    private val attackJumpTick = 20
    private val maxStuck = TickThread.TPS * 5
    private val maxDamageTick = 10

    private val maxInactiveTick = 400
    private var inactiveTick = 0

    private val height = 1.6
    private val width = 0.9

    // 攻撃判定用
    private val dummyLivingEntity = (VanillaSourceAPI.getInstance().nmsHandler.createNMSEntityController(
        location.world,
        0.0,
        0.0,
        0.0,
        EntityType.ARMOR_STAND,
        null
    ) as LivingEntity).also { entity -> entity.bukkitEntity.customName = "Phage" }

    var dropItems = listOf(
        listOf(
            ItemRegistry.SCRAP.createItemStack()
        )
    )

    var block: Material = Material.AIR

    init {
        super.aiController.goalSelector.registerGoal(0, PhageAIGoal(this))
        super.aiController.navigator.maxIterations = 100
        super.aiController.navigator.speed = 0.25F
        super.aiController.navigator.pathfindingInterval = 20

        super.hasGravity = true
        super.collideEntities = true

        super.setModel("phage_normal")
        super.getAnimationHandler().playAnimation(PhageAnimation.IDLE.key, 0.3, 0.3, 1.0, true)

        super.entityController.resetBoundingBoxForMovement(
            EngineBoundingBox(
                location.x - width / 2.0,
                location.y,
                location.z - width / 2.0,
                location.x + width / 2.0,
                location.y + height,
                location.z + width / 2.0
            )
        )
    }

    private fun setState(state: PhageState) {
        this.state = state
        this.stateVariables.reset()
    }

    @Synchronized
    override fun tick() {
        if (!chunk.isLoaded || super.y < -64 || inactiveTick > maxInactiveTick) {
            kill()
            return
        }

        if (target == null) {
            inactiveTick++
        } else {
            inactiveTick = 0
        }

        tick++
        super.tick()
        dummyLivingEntity.setPosRaw(super.x, super.y, super.z)

        if (noDamageTick > 0) {
            noDamageTick--
        }

        super.modeledEntityHolder.dummy.bodyRotationController.yBodyRot = super.yaw

        val activeModel = super.modeledEntityHolder.modeledEntity.models.values.first()
        activeModel.getBone("block").orElseThrow().model = ItemStack(block)

        tickState()

        if (tick % 3 == 0 && state == PhageState.RUN && !super.getMoveDelta().isZero) {
            PlayerManager.ONLINE_PLAYERS.forEach { player ->
                player.playSound(
                    Sound.sound(
                        Material.COPPER_BLOCK.createBlockData().soundGroup.stepSound,
                        Sound.Source.AMBIENT,
                        0.5f,
                        2f
                    ),
                    super.x,
                    super.y,
                    super.z
                )
            }
        }
    }

    private fun tickState() {
        when (state) {
            PhageState.IDLE -> {
                super.aiController.navigator.enableNavigation = true
                // TODO : walk randomly

                playIdleOrWalkAnimation()

                val target = this.target
                if (target != null) {
                    this.setState(PhageState.RUN)
                }
            }

            PhageState.RUN -> {
                super.aiController.navigator.enableNavigation = true

                val target = this.target
                if (target == null) {
                    this.setState(PhageState.IDLE)
                } else {
                    playIdleOrWalkAnimation()

                    if (target.location.toVector().distanceSquared(super.getPosition()) < attackRange.pow(2)) {
                        animationHandler.stopAnimation(PhageAnimation.WALK.key)
                        this.setState(PhageState.ATTACK)
                        this.stateVariables.attackTarget = target
                    }
                }
            }

            PhageState.ATTACK -> {
                super.aiController.navigator.enableNavigation = false

                val target = this.stateVariables.attackTarget!!

                if (this.stateVariables.attackTick == 0) {
                    animationHandler.playAnimation(PhageAnimation.ATTACK.key, 0.3, 0.3, 1.0, true)
                }

                if (this.stateVariables.attackTick == 5) {
                    PlayerManager.ONLINE_PLAYERS.forEach {
                        it.spawnParticle(
                            Particle.SONIC_BOOM,
                            super.x,
                            super.y + 1.25,
                            super.z,
                            1
                        )
                    }
                }

                this.stateVariables.attackTick++

                if (this.stateVariables.attackTick < attackJumpTick) {
                    super.setRotationLookAt(target.x, target.y, target.z)
                } else if (this.stateVariables.attackTick == attackJumpTick) {
                    val direction = target.location.toVector().subtract(super.getPosition())

                    if (!direction.isZero) {
                        direction.normalize().multiply(0.8).add(Vector(0.0, 0.4, 0.0))
                    }

                    super.velocity = direction
                } else {
                    PlayerManager.ONLINE_PLAYERS
                        .filter { it.boundingBox.overlaps(super.getBoundingBox()!!) }
                        .forEach { player ->
                            Bukkit.getScheduler()
                                .runTaskLater(SnowDawn.plugin, Runnable {
                                    if (state == PhageState.ATTACK) {
                                        val entityPlayer = (player as CraftPlayer).handle
                                        entityPlayer.hurt(
                                            entityPlayer.damageSources().mobAttack(dummyLivingEntity),
                                            this.attackDamage
                                        )
                                    }
                                }, 1)
                        }

                    super.velocity.add(Vector(0.0, -0.08, 0.0))

                    if (super.isOnGround()) {
                        animationHandler.stopAnimation(PhageAnimation.ATTACK.key)
                        this.setState(PhageState.RUN)
                    }
                }
            }

            PhageState.STUCK -> {
                super.aiController.navigator.enableNavigation = false
                stateVariables.stuckTick++

                if (stateVariables.stuckTick == 14) {
                    animationHandler.playAnimation(PhageAnimation.STUCK.key, 0.0, 0.3, 1.0, true)
                }

                if (stateVariables.stuckTick > maxStuck) {
                    animationHandler.stopAnimation(PhageAnimation.STUCK.key)
                    this.setState(PhageState.RUN)
                }
            }

            PhageState.DEATH -> {
                stateVariables.deathTick++

                if (stateVariables.deathTick == 13) {
                    kill()

                    val dropItems = this.dropItems[Random.nextInt(this.dropItems.size)]
                    val location = Location(bukkitWorld, super.x, super.y, super.z)

                    Bukkit.getScheduler().runTask(SnowDawn.plugin, Runnable {
                        for (item in dropItems) {
                            bukkitWorld.dropItemNaturally(location, item)
                        }
                    })

                    PlayerManager.ONLINE_PLAYERS.forEach {
                        it.spawnParticle(
                            Particle.POOF,
                            super.x,
                            super.y + 0.5,
                            super.z,
                            0,
                            0.0,
                            0.0,
                            0.0,
                        )
                    }
                }
            }
        }
    }

    private fun playIdleOrWalkAnimation() {
        if (super.getMoveDelta().isZero) {
            animationHandler.stopAnimation(PhageAnimation.WALK.key)
            if (!animationHandler.animations.containsKey(PhageAnimation.IDLE.key)) {
                animationHandler.playAnimation(PhageAnimation.IDLE.key, 0.3, 0.3, 1.0, true)
            }
        } else {
            animationHandler.stopAnimation(PhageAnimation.IDLE.key)
            if (!animationHandler.animations.containsKey(PhageAnimation.WALK.key)) {
                animationHandler.playAnimation(PhageAnimation.WALK.key, 0.3, 0.3, 2.0, true)
            }
        }
    }

    @Synchronized
    fun tryParry(): Boolean {
        return state == PhageState.ATTACK && stateVariables.attackTick >= attackJumpTick
    }

    @Synchronized
    fun parryBy(player: Player) {
        if (health <= 0.0F) {
            return
        }

        val location = player.location
        super.setRotationLookAt(location.x, location.y, location.z)

        val direction = super.getPosition().subtract(location.toVector())
        direction.setY(0.0)
        if (!direction.isZero) {
            direction.normalize().multiply(0.2).add(Vector(0.0, 0.1, 0.0))
        }
        super.velocity = direction

        animationHandler.playAnimation(PhageAnimation.PARRY.key, 0.0, 0.0, 1.0, true)
        this.setState(PhageState.STUCK)

        if (!block.isEmpty) {
            PlayerManager.ONLINE_PLAYERS.forEach {
                it.spawnParticle(
                    Particle.BLOCK,
                    super.x,
                    super.y + 1.0,
                    super.z,
                    10,
                    0.5,
                    0.5,
                    0.5,
                    block.createBlockData()
                )
                it.playSound(
                    Sound.sound(block.createBlockData().soundGroup.breakSound, Sound.Source.AMBIENT, 1f, 1f),
                    super.x,
                    super.y,
                    super.z
                )
                it.playSound(
                    Sound.sound(block.createBlockData().soundGroup.breakSound, Sound.Source.AMBIENT, 1f, 1f),
                    super.x,
                    super.y,
                    super.z
                )
                it.playSound(
                    Sound.sound(block.createBlockData().soundGroup.breakSound, Sound.Source.AMBIENT, 1f, 1f),
                    super.x,
                    super.y,
                    super.z
                )
            }
            block = Material.AIR
        }
    }

    @Synchronized
    fun damage(player: Player, damage: Float, critical: Boolean) {
        if (health <= 0.0F || noDamageTick > 0) {
            return
        }

        if (!block.isEmpty && state != PhageState.STUCK) {
            PlayerManager.ONLINE_PLAYERS.forEach {
                it.playSound(
                    Sound.sound(
                        org.bukkit.Sound.BLOCK_COPPER_HIT,
                        Sound.Source.AMBIENT,
                        1f,
                        1.4f
                    ),
                    super.x,
                    super.y,
                    super.z,
                )
            }
            for (i in 0..<10) {
                val size = 2.5
                val x = Random.nextDouble(size) - (size / 2.0)
                val y = Random.nextDouble(size) - (size / 2.0)
                val z = Random.nextDouble(size) - (size / 2.0)
                PlayerManager.ONLINE_PLAYERS.forEach {
                    it.spawnParticle(Particle.ENCHANTED_HIT, super.x, super.y + 1.0, super.z, 0, x, y, z, 1.5)
                }
            }

            val damageIndicator = DamageIndicator(
                Component.text(0.0).color(NamedTextColor.WHITE),
                super.position.clone().add(Vector(0.0, 1.0, 0.0)),
                player.world
            )
            SnowDawn.ENTITY_THREAD.addEntity(damageIndicator)

            return
        }

        if (critical) {
            val damageIndicator = DamageIndicator(
                Component.text(damage).color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD),
                super.position.clone().add(Vector(0.0, 1.0, 0.0)),
                player.world
            )
            SnowDawn.ENTITY_THREAD.addEntity(damageIndicator)
        } else {
            val damageIndicator = DamageIndicator(
                Component.text(damage).color(NamedTextColor.WHITE),
                super.position.clone().add(Vector(0.0, 1.0, 0.0)),
                player.world
            )
            SnowDawn.ENTITY_THREAD.addEntity(damageIndicator)
        }

        if (critical) {
            // effect
            for (i in 0..<20) {
                val size = 1.5
                val x = Random.nextDouble(size) - (size / 2.0)
                val y = Random.nextDouble(size) - (size / 2.0)
                val z = Random.nextDouble(size) - (size / 2.0)
                PlayerManager.ONLINE_PLAYERS.forEach {
                    it.spawnParticle(Particle.CRIT, super.x, super.y, super.z, 0, x, y, z, 1.5)
                }
            }
        }

        if (critical) {
            player.playSound(Sound.sound(org.bukkit.Sound.ENTITY_PLAYER_ATTACK_WEAK, Sound.Source.PLAYER, 1.0F, 1.0F))
        } else {
            player.playSound(Sound.sound(org.bukkit.Sound.ENTITY_PLAYER_ATTACK_STRONG, Sound.Source.PLAYER, 1.0F, 1.0F))
        }

        noDamageTick = maxDamageTick

        health -= if (state == PhageState.STUCK) {
            damage * 2.0F
        } else {
            damage
        }

        modeledEntityHolder.modeledEntity.markHurt()

        if (health <= 0.0F) {
            val location = player.location
            super.setRotationLookAt(location.x, location.y, location.z)

            val direction = super.getPosition().subtract(location.toVector())
            direction.setY(0.0)
            if (!direction.isZero) {
                direction.normalize().multiply(0.3).add(Vector(0.0, 0.25, 0.0))
            }
            super.velocity = direction

            PlayerManager.ONLINE_PLAYERS.forEach {
                it.playSound(
                    Sound.sound(
                        org.bukkit.Sound.ENTITY_IRON_GOLEM_DEATH,
                        Sound.Source.AMBIENT,
                        1f,
                        2.0f
                    ),
                    super.x,
                    super.y,
                    super.z,
                )
            }

            animationHandler.stopAnimation(PhageAnimation.STUCK.key)
            animationHandler.stopAnimation(PhageAnimation.ATTACK.key)
            animationHandler.playAnimation(PhageAnimation.PARRY.key, 0.0, 0.0, 1.0, true)
            this.setState(PhageState.DEATH)
        } else {
            val pitch = if (critical) {
                1.3F
            } else {
                1.4F
            }
            PlayerManager.ONLINE_PLAYERS.forEach {
                it.playSound(
                    Sound.sound(
                        org.bukkit.Sound.ENTITY_IRON_GOLEM_REPAIR,
                        Sound.Source.AMBIENT,
                        1f,
                        pitch,
                    ),
                    super.x,
                    super.y,
                    super.z,
                )
            }
        }
    }

    override fun kill() {
        MobSpawnProcessor.mobCount--
        super.kill()
    }
}

private enum class PhageAnimation(val key: String) {
    IDLE("idle"),
    WALK("walk"),
    ATTACK("attack"),
    PARRY("parry"),
    STUCK("stuck"),
}

private enum class PhageState {
    IDLE,
    RUN,
    ATTACK,
    STUCK,
    DEATH,
}

private class PhageStateVariables {
    var attackTarget: Player? = null
    var attackTick = 0
    var stuckTick = 0
    var deathTick = 0

    fun reset() {
        attackTarget = null
        attackTick = 0
        stuckTick = 0
        deathTick = 0
    }
}

private class PhageAIGoal(private val phage: Phage) : PathfindingGoal {
    private val maxTargetFollowTick = TickThread.TPS * 10
    private val targetSearchInterval = TickThread.TPS * 2
    private val maxPlayerDetectionRange = 20.0

    private var tick = 0
    private var targetFollowTick = maxTargetFollowTick

    override fun run(selector: GoalSelector, navigator: Navigator) {
        tick++

        if (phage.target != null) {
            targetFollowTick++
        }

        if (targetFollowTick >= maxTargetFollowTick && tick % targetSearchInterval == 0) {
            val target = PlayerManager.getNearestPlayer(phage.position.toLocation(phage.bukkitWorld))

            if (target != null
                && target.location.toVector().distanceSquared(phage.position) < maxPlayerDetectionRange.pow(2)
                && target.gameMode == GameMode.SURVIVAL
            ) {
                phage.target = target
            } else {
                phage.target = null
            }
        }

        val targetLocation = phage.target?.location
        if (targetLocation != null) {
            navigator.navigationGoal =
                BlockPosition(targetLocation.blockX, targetLocation.blockY, targetLocation.blockZ)
        }

        selector.isFinished = true
    }
}
