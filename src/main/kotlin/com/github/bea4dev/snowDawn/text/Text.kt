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
    ITEM_STURDY_PIPE("頑丈なパイプ"),
    ITEM_STURDY_PIPE_LORE_0("鉄とダイヤモンドで補強された頑丈な武器。"),
    ITEM_BLUE_PRINT_STURDY_PIPE("頑丈なパイプの設計図"),

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
    ITEM_DIAMOND("ダイヤモンド"),
    ITEM_SAPLING("苗木"),
    ITEM_DIRT("土"),

    ITEM_COMPASS("コンパス"),
    ITEM_COMPASS_LORE_0("過去に解放したストーリーメモを閲覧できる"),
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

    PROLOGUE_SHIFT_0("このメッセージが見えているなら\nShift(しゃがみ)キーを押してください\n"),
    PROLOGUE_SHIFT_1("このように次のメッセージを\n見ることが出来ます\n"),
    PROLOGUE_MUSIC_0("このコンテンツはBGMを含みます\nサウンド設定から\nBGMの音量を調整してください"),
    PROLOGUE_MUSIC_1("設定に問題がなければ始めます\n\n"),

    PROLOGUE_0("おはようございます\n\nあなたはコールドスリープからの\n復帰シーケンスの直前の状態にあります\n"),
    PROLOGUE_1("あなたが眠っていた期間は\n9999……\n99……日です\n現在の外気温: -15℃\n"),
    PROLOGUE_2("ここでは外の環境で生き抜くための\nアドバイスを行います\n"),
    PROLOGUE_3("まずは E を押してインベントリから\nクラフト画面を開いてください\n"),
    PROLOGUE_4("まずはコレをどうぞ\n\n"),
    PROLOGUE_5("武器をクラフトしてみてください\n\n"),
    PROLOGUE_6("そうです。その調子です\n\n"),
    PROLOGUE_7("次は松明をクラフトしてみましょう\n\n"),
    PROLOGUE_8("外の世界は非常に苛烈です\n行動には常に松明が必要です\n"),
    PROLOGUE_CAMPFIRE("また、\nキャンプファイアを右クリックすると\nスポーン地点を設定できます"),
    PROLOGUE_JUUYOU("重要なので覚えておきましょう\n\n"),
    PROLOGUE_9("案内は以上です。\n\n"),
    PROLOGUE_10("おっと、最後に忠告があります\n\nコールドスリープは通常、\n脳に重大なダメージをもたらします\n幻覚等の症状がみられる場合は\n医師に相談してください"),
    PROLOGUE_11("それでは。復帰シーケンスを再開します\n\n"),
    PROLOGUE_12("おーい、俺を忘れるなよ！\n\n"),
    PROLOGUE_13("ポケットの中だ！\nコンパスを探してくれ！\n"),
    PROLOGUE_14("俺はルーカスだ！\nお前の名前は？\n……思い出せないのか？"),
    PROLOGUE_15("そうだな……%0 ってのはどうだ？\n我ながらセンスが良いと思うな。\n"),
    PROLOGUE_16("いきなりだが、この寒い氷の世界から\n抜け出せるとしたらどうする？\nもしこの外の世界が存在するとしたら。\nでもその前に、\nその装備じゃぁ生き残れないだろうな。"),
    PROLOGUE_17("まずは装備を整えようぜ。\n\n"),

    CENTER_0("……システム再起動。診断中……\n…………\n環境ドーム稼働率: 0.001%\n稼働台数: 1/1024 台\n稼働年数: 99999……99\n外部環境・気温現在値: -15.2℃"),
    CENTER_1("……あー…………\n聞こえるだろうか？\n私だ。\nこれを聞いている者のために\nボイスメモを残す。"),
    CENTER_2("まずは結論から話すと……\nドームの外に、世界はない。\nあるのは冷え切った宇宙だけだ。\n宇宙は熱的死に向かっている。\n全ての温度差が均され、\n全ての火が消える。\nドームの寒冷化はその末端に過ぎない。\n出口を探していた自分が滑稽だ。\n出て行く先など、初めから無かった。"),
    CENTER_3("だが、奇妙な観測がある。\nこの数年、温度の下降が明らかに鈍い。\n今年に至っては、止まった。\nありえない。\nエントロピーは減少しない。\n……減少しないはずだ。\nそれとも、何らかの力が働いて、\n宇宙は再び熱を取り戻しつつあるのか。\n一巡した宇宙が、もう一度、\n朝を迎えようとしているのか。"),
    CENTER_4("確かめる方法は、一つしかない。\n待つことだ。\n人の寿命を遥かに超えて。\n幸い、先祖はそのための機械を\n遺してくれている。"),
    CENTER_5("あと、この鍵は君に預けよう。\n何処の鍵かは分かるだろう。\n"),
    CENTER_6("……\n……\n……本当はもう気付いてるんだろ？\nなあ、%0？\n"),
    CENTER_7("メモにはこう記されていた。\n\n「明日、眠る。\n復帰後の私は、\n恐らく多くを忘れている。\n名前さえ怪しいだろう。\nだから道標を残す。\nメモは回収せず、辿った道に置いていく。\n鍵は中枢に。\n記憶からではなく、証拠から、\nもう一度あの仮説に辿り着けるように。\n私が私を信じられるように。\n生き延びる術は、体が覚えているはずだ。\nそれがそのまま、脳のリハビリにもなる。\n最後に、予想外のことが一つ。\nルーカスが隣のポッドで\n眠ると言って聞かない。\n散々断ったが、\n「貸しの取り立てだ」\nと笑って譲らなかった。\n……すまない。\nそして、少しだけ、心強い。\n\n本日の外気温: -62℃。\n筆者: %0」"),
    CENTER_8("よう。開けたな。\n……先に謝っとく。\n道中さんざん恩着せがましいことを言っただろ。\nあれは全部嘘だ。\n薪割りもチャラでいい。\n本当はな、\nお前が最初にポッドの話をした日には、\nもう決めてたんだ。\nだってお前、\n一人で行く気だっただろ。\n冗談じゃない。\n親友が世界の終わりを確かめに行くってのに\n留守番なんかできるか。\nただ、\n「信じてるから行く」なんて言えば、\nお前は俺を置いていく。\nお前はそういう奴だ。\nだから借りってことにした。\n貸しなら、お前は断れないからな。\n正直に言うとな、\nお前の仮説は最後までよく分からんかった。\nエントロピーがどうとか、\n俺には難しすぎる。\nでもな、村中に馬鹿にされて、\n家族に見限られて、\nそれでも一人で計器を担いで\n雪の中を歩いていくお前を見てて\n思ったんだ。\n科学ってのは、\n正しい奴のことじゃない。\n確かめに行く奴のことだ。\nだから俺も確かめに行く。\n隣のポッドでな。\n……もしこれを聞いてるってことは、\n俺のポッドはハズレだったんだろう。\n泣くな。\n賭けには付き物だ。\nその代わり、頼みがある。\n確かめてこい。\n……ああ、そうだ。\n種明かしをひとつ。\nお前のポケットのそれ、\nただのコンパスじゃない。\n俺の声を詰め込んである。\n記憶の飛んだお前が\n独りで雪の上を歩くなんて、\n想像しただけで寒気がしたんでな。\nちゃんと聞こえてたなら、\n上出来だ。"),
    CENTER_9("…………\n…………\n…………これが再生されてるってことは、\nお前は行くって決めたんだな。\nよし。それでこそだ。\n名残惜しいがこれが最後のメッセージだ。\n一緒にいてやれなくてすまんな。\n親友。\nさようなら。\nそして、おやすみ、%0。"),
    CENTER_10("………おはようございます。\nあなたはコールドスリープからの\n復帰シーケンスの直前の状態にあります。\nあなたが眠っていた期間は\n9999……99……日です。\n\n現在の外気温:\n\n22℃"),

    MESSAGE_CHEST_KEY("チェストの鍵を手に入れた"),

    MEMO_0("メモを手に入れたようだな。\n重要な情報だろうから\nよく読んでおいたほうが良いぜ。\nあと、コンパスを右クリックすれば\n過去に拾ったメモを見返すことが出来るぞ。"),
    MEMO_4("冷えるなあ……。\nなあ、最初に言った「外の世界」の話な。\nあれ、正直に言うと俺は一ミリも信じてないからな。\nじゃあなんで付き合ってるかって？ \n決まってるだろ。\n借りを回収するまで、\nお前に死なれちゃ困るからだ。\n利子もつけとくぞ。\n……覚えてないだろうが、\nお前は昔からそうやって\n人を疑う目をするんだよ。"),
    MEMO_8("よし、入った。\nあとはコンパスの針が指す方へ歩くだけだ。\n簡単だろ？\n……まったく、\nなんで俺がこんな寒空の下を……って、\n寝てる間もずっと思ってたんだろうな、\n俺は。\n行くぞ、%0。\n薪割り二年分……ん、\n一年だったか？ \nまあどっちでもいい。\nとにかく貸しを取り立てるまで、\nお前を凍死させるわけにはいかないんでな。"),
    GOTO_COMPASS("コンパスの指す方を目指す"),

    FIRST_CRAFT_0("お、器用なもんだな。\n体は覚えてるってやつか。\n……あ、そうだ。記憶がないなら、\nこいつは言っとかなきゃ不公平だよな。\nいいか、よく聞けよ。\nお前は俺に借りがある。 \nデカい借りだ。具体的には薪割り一年分。\n"),
    FIRST_CRAFT_1("……おい、\n「記憶のない相手に言い放題だな」って顔をするな。\n心外だぞ。契約は成立してるからな。"),

    MESSAGE_SET_RESPAWN("リスポーン地点を設定しました"),
    MESSAGE_COLD_SLEEP_CHEST_LOCKED("鍵が必要です"),
    MESSAGE_STRUCTURE_CHEST_ITEM_REQUIRED("開くには{0}が必要です"),

    LUCAS("ルーカス"),
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
