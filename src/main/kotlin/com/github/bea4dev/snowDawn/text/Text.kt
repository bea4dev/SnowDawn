package com.github.bea4dev.snowDawn.text

import net.kyori.adventure.key.Key
import net.kyori.adventure.translation.GlobalTranslator
import net.kyori.adventure.translation.TranslationRegistry
import org.bukkit.entity.Player
import java.text.MessageFormat
import java.util.Locale

enum class Text(val jp: String) {
    EMPTY(""),

    CLOSE("閉じる"),
    BACK("戻る"),

    CRAFT_UI("クラフト"),
    STORY_MEMO_LIST("解放済みストーリーメモ"),
    BGM_NOW_PLAYING("Now Playing: {0}"),
    CRAFT_UI_CLICK_TO_OPEN("クリックで開く"),
    CRAFT_REQUIRED("> 必要なアイテム"),
    CANNOT_CRAFT("材料が足りません"),
    UNCRAFTABLE("未解放のレシピ"),
    RECIPE_UNLOCKED("新レシピ解放！"),
    STORY_UNLOCKED("新ストーリー開放"),

    ITEM_SCRAP("スクラップ"),
    ITEM_SCRAP_LORE_0("何らかの残骸、クラフトに使用する。"),

    ITEM_CATALYST("肥料触媒"),
    ITEM_CATALYST_LORE_0("かまどで熱すると肥料になる"),
    ITEM_BLUE_PRINT_CATALYST("肥料触媒の設計図"),
    ITEM_FERTILIZER("化学肥料"),
    ITEM_FERTILIZER_LORE_0("植物に振りかけると成長を早める"),

    ITEM_FUEL("合成燃料"),
    ITEM_FUEL_LORE_0("化石燃料よりも高効率な燃料"),

    ITEM_SCRAP_PIPE("スクラップのパイプ"),
    ITEM_SCRAP_PIPE_LORE_0("スクラップの寄せ集めでできた武器。"),
    ITEM_SCRAP_PIPE_LORE_1("敵の攻撃に合わせてクリックすると、"),
    ITEM_SCRAP_PIPE_LORE_2("相手の攻撃を弾くことができる。"),
    ITEM_SCRAP_PIPE_LORE_3(""),
    ITEM_SCRAP_PIPE_LORE_4("パリィ！"),

    ITEM_SCRAP_PICKAXE("スクラップのピッケル"),
    ITEM_STONE_PICKAXE("石のピッケル"),
    ITEM_STONE_HOE("石のくわ"),
    ITEM_STONE_SHOVEL("石のシャベル"),
    ITEM_STONE_AXE("石の斧"),
    ITEM_IRON_PICKAXE("鉄のピッケル"),
    ITEM_IRON_HOE("鉄のくわ"),
    ITEM_IRON_SHOVEL("鉄のシャベル"),
    ITEM_IRON_AXE("鉄の斧"),
    ITEM_FURNACE("かまど"),

    ITEM_ICE("氷"),
    ITEM_COAL("石炭・木炭"),
    ITEM_TORCH("松明"),
    ITEM_CAMPFIRE("焚き火"),
    ITEM_FLINT_AND_STEEL("火打石と打ち金"),
    ITEM_WOOD("トウヒの原木"),
    ITEM_CHEST("チェスト"),
    ITEM_COLD_SLEEP_KEY("コールドスリープキー"),
    ITEM_COLD_SLEEP_KEY_LORE_0("コールドスリープ室の保管チェストを開くためのキー"),
    ITEM_CRAFTING_TABLE("作業台"),
    ITEM_BLUE_PRINT_CHEST("チェストの設計図"),
    ITEM_BLUE_PRINT_CRAFTING_TABLE("作業台の設計図"),
    ITEM_BOAT("トウヒのボート"),
    ITEM_BLUE_PRINT_BOAT("ボートの設計図"),
    ITEM_TORCH_LORE_0("体を温めることができる"),
    ITEM_STONE("丸石"),
    ITEM_COPPER_INGOT("銅のインゴット"),
    ITEM_IRON_INGOT("鉄のインゴット"),
    ITEM_SAPLING("苗木"),
    ITEM_DIRT("土"),

    ITEM_COMPASS("追跡機"),
    ITEM_COMPASS_LORE_0("施設への方角を示してくれる"),
    ITEM_COMPASS_LORE_1("手に持って右クリック！"),

    ITEM_IRON_HELMET("鉄のヘルメット"),
    ITEM_IRON_CHEST_PLATE("鉄のチェストプレート"),
    ITEM_IRON_LEGGINGS("鉄のレギンス"),
    ITEM_IRON_BOOTS("鉄のブーツ"),

    ITEM_BLUE_PRINT_IRON_EQUIPMENTS("鉄装備の設計図"),
    ITEM_BLUE_PRINT_STONE_HOE("石のくわの設計図"),
    ITEM_BLUE_PRINT_STONE_SHOVEL("石のシャベルの設計図"),
    ITEM_BLUE_PRINT_STONE_AXE("石の斧の設計図"),
    ITEM_BLUE_PRINT_IRON_HOE("鉄のくわの設計図"),
    ITEM_BLUE_PRINT_IRON_SHOVEL("鉄のシャベルの設計図"),
    ITEM_BLUE_PRINT_IRON_AXE("鉄の斧の設計図"),

    ITEM_BLUE_PRINT_CLICK("クリックでレシピを解放"),

    PROLOGUE_0("おはようございます\n\nあなたはコールドスリープからの\n復帰シーケンスの直前の状態にあります\n"),
    PROLOGUE_1("あなたが眠っていた期間は\n9999……\n99……日です\n現在の外気温: -15℃\n"),
    PROLOGUE_2("ここでは外の環境で生き抜くための\nアドバイスを行います\n"),
    PROLOGUE_3("まずは E を押してインベントリから\nクラフト画面を開いてください\n"),
    PROLOGUE_4("まずはコレをどうぞ\n\n"),
    PROLOGUE_5("武器をクラフトしてみてください\n\n"),
    PROLOGUE_6("そうです。その調子です\n\n"),
    PROLOGUE_7("次は松明をクラフトしてみましょう\n\n"),
    PROLOGUE_8("外の世界は非常に苛烈です\n行動には常に松明が必要です\n"),
    PROLOGUE_9("案内は以上です。\n\n"),
    PROLOGUE_10("おっと、最後に忠告があります\n\nコールドスリープは通常、\n脳に重大なダメージをもたらします\n幻覚等の症状がみられる場合は\n医師に相談してください"),
    PROLOGUE_11("それでは。復帰シーケンスを再開します\n\n"),
    PROLOGUE_12("おーい、俺を忘れるなよ！\n\n"),
    PROLOGUE_13("ポケットの中だ！\nコンパスを探してくれ！\n"),
    PROLOGUE_14("俺はルーカスだ！\nお前の名前は？\n……思い出せないのか？"),
    PROLOGUE_15("そうだな……%0 ってのはどうだ？\n我ながらセンスが良いと思うな。\n"),
    PROLOGUE_16("いきなりだが、この寒い氷の世界から\n抜け出せるとしたらどうする？\nもしこの外の世界が存在するとしたら。\nでもその前に、\nその装備じゃぁ生き残れないだろうな。"),
    PROLOGUE_17("まずは装備を整えようぜ。\n\n"),

    MEMO_0("メモを手に入れたようだな。\n重要な情報だろうから\nよく読んでおいたほうが良いぜ。\nあと、コンパスを右クリックすれば\n過去に拾ったメモを見返すことが出来るぞ。"),
    MEMO_4("冷えるなあ……。\nなあ、最初に言った「外の世界」の話な。\nあれ、正直に言うと俺は一ミリも信じてないからな。\nじゃあなんで付き合ってるかって？ \n決まってるだろ。\n借りを回収するまで、\nお前に死なれちゃ困るからだ。\n利子もつけとくぞ。\n……覚えてないだろうが、\nお前は昔からそうやって\n人を疑う目をするんだよ。"),
    MEMO_8("よし、入った。\nあとはコンパスの針が指す方へ歩くだけだ。\n簡単だろ？\n……まったく、\nなんで俺がこんな寒空の下を……って、\n寝てる間もずっと思ってたんだろうな、\n俺は。\n行くぞ、%0。\n薪割り二年分……ん、\n一年だったか？ \nまあどっちでもいい。\nとにかく貸しを取り立てるまで、\nお前を凍死させるわけにはいかないんでな。"),
    GOTO_COMPASS("コンパスの指す方を目指す"),

    FIRST_CRAFT_0("お、器用なもんだな。\n体は覚えてるってやつか。\n……あ、そうだ。記憶がないなら、\nこいつは言っとかなきゃ不公平だよな。\nいいか、よく聞けよ。\nお前は俺に借りがある。 \nデカい借りだ。具体的には薪割り一年分。\n"),
    FIRST_CRAFT_1("……おい、\n「記憶のない相手に言い放題だな」って顔をするな。\n心外だぞ。契約は成立してるからな。"),

    MESSAGE_SET_RESPAWN("リスポーン地点を設定しました"),
    MESSAGE_COLD_SLEEP_CHEST_LOCKED("キーが必要です"),
    MESSAGE_STRUCTURE_CHEST_ITEM_REQUIRED("開くには{0}が必要です"),

    LUCAS("ルーカス"),
    GET_BLUE_PRINT_COMPASS_0("これは追跡機の設計図ですね。\n追跡機を使えば施設への方角がわかります。\n"),
    GET_BLUE_PRINT_COMPASS_1("施設までは距離があるので、\n食料や物資を整えてから出発しましょう！\n"),
    ;

    companion object {
        init {
            val registry = TranslationRegistry.create(Key.key("snow_dawn", "global"))
            registry.defaultLocale(Locale.JAPAN)
            for (text in entries) {
                registry.register(text.toString(), Locale.JAPAN, MessageFormat(text.jp))
            }
            val translator = GlobalTranslator.translator()
            translator.addSource(registry)
        }
    }

    operator fun get(player: Player): String {
        return this.jp
    }

    operator fun get(player: Player, vararg arguments: String): String {
        var message = this[player]
        for (argument in arguments.iterator().withIndex()) {
            message = message.replace("%${argument.index}", argument.value)
        }
        return message
    }
}
