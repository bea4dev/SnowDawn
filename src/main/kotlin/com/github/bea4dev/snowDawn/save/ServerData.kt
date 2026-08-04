package com.github.bea4dev.snowDawn.save

import com.github.bea4dev.snowDawn.item.ItemRegistry
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object ServerData {
    private val file = File("server_saves.yml")

    val storyTextIndex = mutableMapOf<String, Int>()
    val unlockedStoryMemos = mutableMapOf<String, MutableSet<Int>>()
    var theEndBgmUnlocked = false
        private set
    var centerScenarioStarted = false
        private set
    val craftableItems = mutableListOf(
        ItemRegistry.SCRAP_PIPE.id,
        ItemRegistry.WOODEN_PICKAXE.id,
        ItemRegistry.STONE_PICKAXE.id,
        ItemRegistry.TORCH.id,
        ItemRegistry.FURNACE.id,
        ItemRegistry.CAMPFIRE.id,
        ItemRegistry.FLINT_AND_STEEL.id,
        ItemRegistry.STONE_HOE.id,
        ItemRegistry.STONE_AXE.id,
        ItemRegistry.STONE_SHOVEL.id,
        ItemRegistry.IRON_AXE.id,
        ItemRegistry.IRON_HOE.id,
        ItemRegistry.IRON_SHOVEL.id,
        ItemRegistry.IRON_PICKAXE.id,
        ItemRegistry.IRON_CHESTPLATE.id,
        ItemRegistry.IRON_HELMET.id,
        ItemRegistry.IRON_LEGGINGS.id,
        ItemRegistry.IRON_BOOTS.id,
        ItemRegistry.CRAFTING_TABLE.id
    )

    fun load() {
        // 初回はデフォルトで保存してから読む
        if (!file.exists()) {
            file.parentFile?.mkdirs()
            save()
        }

        val yml = YamlConfiguration.loadConfiguration(file)

        // storyTextIndex 読み込み（存在すれば上書き、無ければ現状維持）
        yml.getConfigurationSection("storyTextIndex")?.let { sec ->
            storyTextIndex.clear()
            for (key in sec.getKeys(false)) {
                val n = when (val v = sec.get(key)) {
                    is Number -> v.toInt()
                    is String -> v.toIntOrNull() ?: 0
                    else -> 0
                }
                storyTextIndex[key] = n
            }
        }

        yml.getConfigurationSection("unlockedStoryMemos")?.let { sec ->
            unlockedStoryMemos.clear()
            for (worldName in sec.getKeys(false)) {
                unlockedStoryMemos[worldName] = sec.getIntegerList(worldName).toMutableSet()
            }
        }

        theEndBgmUnlocked = if (yml.contains("theEndBgmUnlocked")) {
            yml.getBoolean("theEndBgmUnlocked")
        } else {
            unlockedStoryMemos["snow_land"]?.contains(8) == true
        }
        centerScenarioStarted = yml.getBoolean("centerScenarioStarted", false)

        // craftableItems 読み込み（存在すれば上書き、無ければ現状維持＝デフォルトのまま）
        if (yml.contains("craftableItems")) {
            craftableItems.clear()
            craftableItems.addAll(yml.getStringList("craftableItems"))
        }
    }

    fun save() {
        val yml = YamlConfiguration()

        // Map はセクションとして保存
        if (storyTextIndex.isNotEmpty()) {
            yml.createSection(
                "storyTextIndex",
                storyTextIndex.mapValues { it.value as Any } // Map<String, Any> にする
            )
        } else {
            // 空でもキーを残したい場合はコメントアウト解除
            // yml.createSection("storyTextIndex")
        }


        if (unlockedStoryMemos.isNotEmpty()) {
            yml.createSection(
                "unlockedStoryMemos",
                unlockedStoryMemos.mapValues { (_, indices) -> indices.sorted() }
            )
        }

        // リストはそのまま保存
        yml.set("craftableItems", craftableItems)
        yml.set("theEndBgmUnlocked", theEndBgmUnlocked)
        yml.set("centerScenarioStarted", centerScenarioStarted)

        runCatching {
            file.parentFile?.mkdirs()
            yml.save(file)
        }.onFailure { it.printStackTrace() }
    }

    fun unlockStoryMemo(worldName: String, index: Int): Boolean {
        val unlocked = unlockedStoryMemos.computeIfAbsent(worldName) { mutableSetOf() }
        if (!unlocked.add(index)) {
            return false
        }

        save()
        return true
    }

    @Synchronized
    fun unlockTheEndBgm(): Boolean {
        if (theEndBgmUnlocked) {
            return false
        }

        theEndBgmUnlocked = true
        save()
        return true
    }

    @Synchronized
    fun markCenterScenarioStarted(): Boolean {
        if (centerScenarioStarted) {
            return false
        }

        centerScenarioStarted = true
        save()
        return true
    }
}
