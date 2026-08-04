package com.github.bea4dev.snowDawn.text

import com.github.bea4dev.snowDawn.coroutine.play
import com.github.bea4dev.snowDawn.listeners.StoryMemoUnlockEventRegistry
import com.github.bea4dev.snowDawn.music.BGMProcessorRegistry
import com.github.bea4dev.snowDawn.music.BGM_TRACK_THE_END
import com.github.bea4dev.snowDawn.save.ServerData
import com.github.bea4dev.snowDawn.scenario.DEFAULT_TEXT_BOX
import com.github.bea4dev.snowDawn.scenario.lowSound
import com.github.bea4dev.snowDawn.toast.ToastKind
import com.github.bea4dev.snowDawn.toast.ToastNotification
import com.github.bea4dev.snowDawn.toast.sendToast
import com.github.bea4dev.snowDawn.world.WorldRegistry
import com.github.bea4dev.vanilla_source.api.text.TextBox
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.translation.GlobalTranslator
import net.kyori.adventure.translation.TranslationRegistry
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.inventory.ItemStack
import java.text.MessageFormat
import java.util.Locale

object StoryMemoText {
    data class StoryMemo(
        val worldName: String,
        val index: Int,
        val lines: List<Component>,
    )

    private val STORY_MEMO_TEXT = mapOf(
        WorldRegistry.SNOW_LAND to listOf(
            "観測記録・一\n三十年前の星図と\n今夜の空を照合した。\n星の位置が寸分違わない。\n一つとして動いていない。\n天は回っていない。\n……あるいは、\nあれは天ではないのか。",
            "観測記録・二\n氷を三日掘った。\n土が出るはずの深さで、\n鶴嘴が硬い音を立てた。\n出てきたのは岩盤ではない。\n継ぎ目のある、灰色の壁だ。\n世界には床がある。\n誰がこれを信じるだろう。\n私自身、まだ信じたくない。",
            "手記\n北の集落が空になった。\n燃料が尽きたのだと言う。\n皆、南の村に身を寄せたが、\n南とて余裕はない。\n祖父の代には、この谷に\n千人が暮らしていたそうだ。\n今は五十人に届かない。\n寒さは毎年、確実に深くなる。",
            "手記\n村の大炉が壊れた。\n直せる者がいない。\n仕組みを知っていた者は\n先代で絶えた。\n皆、炉の前で祈っている。\n祈って直るなら苦労はしない。\n我々は先祖の遺した道具を、\n意味も分からず使い、\n壊れれば捨てるだけだ。\n学問は、私が生まれるより\nずっと前に死んでいたのだと思う。",
            "手記\n古い記録を頼りに\n温度計を自作した。\n長老に見せたら\n「数字で寒さが測れるものか」\nと笑われた。\n寒さは測れる。\n測れないのは、\n測ろうとしない心のほうだ。",
            "手記\n兄から手紙が来た。\n短いものだった。\n「もう村でお前の名を\n口にするな」と。\n家族に累が及ぶのは道理だ。\n分かっている。\n分かってはいるのだ。",
            "手記\n旧い友人に、初めて全てを話した。\n壁のこと、星のこと、\n外の宇宙のこと。\n案の定、腹を抱えて笑われた。\nだが帰り際、彼はこう聞いた。\n「で、ポッドは二台あるのか」と。\n冗談だと思って流してしまった。\n彼はいつも冗談ばかり言う。",
            "手記\n彼まで村での立場を\n失いつつあるらしい。\n私と口をきくというだけで、だ。\nもう誰も巻き込むべきではない。\nこの先は一人でやる。\n古文書にあった「制御中枢」\n――ドームが実在するなら、\nその心臓部も実在するはずだ。\nそれを探す。",
            "メモ\n見つけた。\n三百年前の測量記録の余白に、それはあった。\n制御中枢。X: ████ / Z: ████\n巨大な壁の内側だという。\nこの座標を計器に打ち込めば、あとは針が導いてくれる。",
        ),
        WorldRegistry.SECOND_MEGA_STRUCTURE to listOf(
            "メモ\n岩を被ったPhageに気をつけてほしい。\nそのままだと攻撃が通らない。\nアイツの攻撃時に弾き飛ばす必要がある。\nいわゆるパリィだ！",
            "EAGLE調査隊記録: No.7\nこの空間は非常に広大だ。\n足を滑らせないように気をつけたい。\n降下途中に通路を見つけた。\n後に調査を進める。",
            "メモ\n合成燃料の出どころが不明だ。\nPhageの中には燃料が入っているが、\nこれがどこから供給されているのかが不明だ。",
            "EAGLE調査隊記録: No.9\nこの空間の一番下には氷塊が張っている。\nしかし一部は透明な氷が張っている箇所がある。\n詳しく調査する予定だ。",
            "メモ\nこの氷の上をボートで滑ってみたい。\n隊長に提案したら怒られた。",
            "EAGLE調査隊記録: No.10\n透明な氷の下にまた通路を見つけた。\n下の階層へ続いているらしい。\nこれより調査へ向かう。",
            "メモ\n調査調査調査調査…ずっと調査続きで、\nみんなの顔に疲れが見えてきている。\nそろそろ引き返したほうが良いと思うな。\n少し胸騒ぎがする。"
        ),
    )

    init {
        val registry = TranslationRegistry.create(Key.key("snow_dawn", "story"))
        registry.defaultLocale(Locale.JAPAN)
        for ((world, texts) in STORY_MEMO_TEXT) {
            for ((index, text) in texts.withIndex()) {
                for ((lineIndex, line) in text.split("\n").withIndex()) {
                    registry.register("memo:${world.name}:${index}:${lineIndex}", Locale.JAPAN, MessageFormat(line))
                }
            }
        }
        val translator = GlobalTranslator.translator()
        translator.addSource(registry)

        StoryMemoUnlockEventRegistry.register(WorldRegistry.SNOW_LAND, 0) { player ->
            TextBox(
                player,
                DEFAULT_TEXT_BOX,
                Text.LUCAS[player],
                1,
                Text.MEMO_0[player]
            ).lowSound().play().await()
        }
        StoryMemoUnlockEventRegistry.register(WorldRegistry.SNOW_LAND, 4) { player ->
            TextBox(
                player,
                DEFAULT_TEXT_BOX,
                Text.LUCAS[player],
                1,
                Text.MEMO_4[player]
            ).lowSound().play().await()
        }
        StoryMemoUnlockEventRegistry.register(WorldRegistry.SNOW_LAND, 8) { player ->
            ServerData.unlockTheEndBgm()
            BGMProcessorRegistry[player].addTrack(BGM_TRACK_THE_END)

            TextBox(
                player,
                DEFAULT_TEXT_BOX,
                Text.LUCAS[player],
                1,
                Text.MEMO_8[player, player.name]
            ).lowSound().play().await()

            player.playSound(
                player.location,
                Sound.ENTITY_ARROW_HIT_PLAYER,
                Float.MAX_VALUE,
                1.5F
            )

            player.sendToast(
                ToastNotification(
                    Component.translatable(Text.GOTO_COMPASS.toString())
                        .color(NamedTextColor.AQUA)
                        .decoration(TextDecoration.ITALIC, false)
                        .decorate(TextDecoration.BOLD),
                    ItemStack(Material.COMPASS),
                    ToastKind.TASK
                )
            )
        }
    }

    fun getNext(world: World): StoryMemo? {
        val index = ServerData.storyTextIndex.computeIfAbsent(world.name) { 0 }

        val storyMemoText = STORY_MEMO_TEXT[world] ?: return null

        if (index >= storyMemoText.size) {
            return null
        }

        ServerData.storyTextIndex[world.name] = index + 1

        return get(world, index)
    }

    fun get(world: World, index: Int): StoryMemo? {
        val storyMemoText = STORY_MEMO_TEXT[world] ?: return null
        if (index !in storyMemoText.indices) {
            return null
        }

        val lines = storyMemoText[index]
            .split("\n")
            .indices
            .map { line -> "memo:${world.name}:${index}:${line}" }
            .map { key -> Component.translatable(key) }

        return StoryMemo(world.name, index, lines)
    }

    fun getUnlocked(world: World): List<StoryMemo> {
        return ServerData.unlockedStoryMemos[world.name]
            .orEmpty()
            .sorted()
            .mapNotNull { index -> get(world, index) }
    }
}
