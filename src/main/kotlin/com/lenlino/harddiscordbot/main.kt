package com.lenlino.harddiscordbot

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandClientBuilder
import com.jagrosh.jdautilities.command.CommandEvent
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.managers.AudioManager
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.json.JSONObject
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.sql.*


class Neko:Command(){/*Commandクラスを継承してコマンドを定義*/
init {
    this.name = "neko" /*コマンド文字列の定義はinitブロックの中に書く必要があります。*/
}
    override fun execute(event: CommandEvent?){
/*executeメソッドはコマンドを叩かれたイベントをキャッチして対応する処理を実行する中核部分です*/
        event?.reply("にゃーん")
/*
ここで使われているreplyメソッドは
event?.message?.channel?.sendMessageFormat("")?.queue()
の簡易呼び出しです。Discordの「返信」とは異なりますのでご注意ください。
*/
    }
}

class kick:Command(){/*Commandクラスを継承してコマンドを定義*/
init {
    this.name = "kick" /*コマンド文字列の定義はinitブロックの中に書く必要があります。*/
}
    override fun execute(event: CommandEvent?){
/*executeメソッドはコマンドを叩かれたイベントをキャッチして対応する処理を実行する中核部分です*/
        event!!.reply("test")
/*
ここで使われているreplyメソッドは
event?.message?.channel?.sendMessageFormat("")?.queue()
の簡易呼び出しです。Discordの「返信」とは異なりますのでご注意ください。
*/
    }
}

class help:Command(){/*Commandクラスを継承してコマンドを定義*/
init {
    this.name = "help" /*コマンド文字列の定義はinitブロックの中に書く必要があります。*/
}
    override fun execute(event: CommandEvent?){
/*executeメソッドはコマンドを叩かれたイベントをキャッチして対応する処理を実行する中核部分です*/
        val embed = EmbedBuilder()//EmbedBuilderでインスタンスを作成して、後から中身をセットします。
            //タイトル文字列。第2引数にURLを入れるとタイトルを指定URLへのリンクにできます
            .setTitle("ヘルプ")

            //Botの情報。タイトルと同じくリンクを指定できる他、第3引数にアイコン画像を指定できます。
            //今回は自分のアバターアイコンを指定しました。

            .appendDescription("すべてのコマンドの前には.をつける必要があります") //Embedの説明文
            .setColor(0x00ff00) //Embed左端の色を設定します。今回は緑。
            .addField("about","BOTの導入数・BOT招待URLを表示",false)
            .addField("neko","にゃー",false) //以下3つフィールドをセット
            .addField("mcserver <サーバーアドレス>","minecraftサーバーステータスを取得",false)
            .addField("mcskin <ユーザー名>","minecraftスキンを取得",false)
            .addField("mcbeskin <ユーザー名>","minecraft(BE)スキンを取得",false)
            .addField("gcset","グローバルチャットを設定",false)
            .addField("poll <タイトル> <項目１> <項目２>...","投票を設定",false)
            .addField("pollr <投票ID>","投票結果をグラフで表示",false)
            .addField("omikuji","おみくじ",false)
            .addField("dice","サイコロ 1から6",false)
            .addField("vc","読み上げの開始/停止(新機能)",false)
            .build() //buildは一番最後の組み立て処理です。書き忘れないようにしましょう。
        event?.reply(embed)
/*
ここで使われているreplyメソッドは
event?.message?.channel?.sendMessageFormat("")?.queue()
の簡易呼び出しです。Discordの「返信」とは異なりますのでご注意ください。
*/
    }
}



class BotClient: ListenerAdapter(){

    lateinit var jda: JDA
    private val commandPrefix = "." /*コマンドプレフィックスの指定
                                     空文字列にするとBotへのメンションがプレフィックスとして機能します。
　　　　　　　　　　　　　　　　　　　　このほうがいいかも。*/

    fun main(token: String) {



        val commandClient = CommandClientBuilder()
            .setPrefix(commandPrefix)
            .setOwnerId("") /*本来であれば開発者のIDを入れますが、空文字列でもOKです。*/
            .addCommands(Neko(),kick(),help(),about(),mcskin(),gcset(),poll(),pollresult(),mcserver(),omikuzi(),dice(),omikujiset(),mcbeskin())
            .useHelpBuilder(false)
            .build()

        jda = JDABuilder.createDefault(token,
            GatewayIntent.GUILD_MESSAGES)
            .addEventListeners(commandClient)
            .addEventListeners(this)
            .build()


    }

    override fun onReady(event: ReadyEvent) { //Botがログインしたときの処理
        val playerManager: AudioPlayerManager = DefaultAudioPlayerManager()
        AudioSourceManagers.registerRemoteSources(playerManager)
        println("起動しました")
    }

    override fun onGuildMessageReceived(event : GuildMessageReceivedEvent) {
        //Botがメッセージを受信したときの処理
        if(!event.member?.user?.isBot!!){//メッセージ内容を確認
            val conn = getConnection()
            val stmt: Statement = conn!!.createStatement()
            val psts: PreparedStatement = conn.prepareStatement("SELECT * FROM discord WHERE server_id=?")
            psts.setString(1, event?.guild?.id);
            val rs: ResultSet = psts.executeQuery()

            while (rs.next()) {
                //送信元チャンネルが登録されているか確認
                if(rs.getString("gchannel_id").equals(event.channel.id)) {
                    val rs = stmt.executeQuery("SELECT * FROM discord")
                    while (rs.next()) {
                        //送信元チャンネルではないことを確認
                        if (!rs.getString("gchannel_id").equals(event.channel.id)) {
                            val guild = jda.getGuildById(rs.getString("server_id"))
                            val channel = guild?.getTextChannelById(rs.getString("gchannel_id"))
                            channel?.sendMessage(event.member!!.effectiveName + " » " + event.message.contentDisplay)?.queue()
                            if (event.message.attachments.size>0) {
                                val imagelist = event.message.attachments
                                for (i in 0..imagelist.size-1) {
                                    channel?.sendMessage(imagelist[i].url)?.queue()
                                }
                            }
                        }
                    }
                }
            }
            stmt.close()
            psts.close()
            conn.close()
        }
}}


fun main() {
    val token = System.getenv("Discord_Bot_Token")
    val bot = BotClient()
    bot.main(token)
}

@Throws(URISyntaxException::class, SQLException::class)
fun getConnection(): Connection? {
    val url = System.getenv("DATABASE_URL")
    val dbUri = URI(url)
    val username: String = dbUri.getUserInfo().split(":").get(0)
    val password: String = dbUri.getUserInfo().split(":").get(1)
    val dbUrl = "jdbc:postgresql://" + dbUri.getHost() + dbUri.getPath()
    return DriverManager.getConnection(dbUrl, username, password)
}

