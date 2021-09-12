package com.lenlino.harddiscordbot

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandClientBuilder
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent
import org.json.JSONObject
import java.awt.Color
import java.awt.SystemColor.text
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintStream
import java.net.HttpURLConnection
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.sql.*
import java.util.regex.Pattern


class Neko:Command(){
init {
    this.name = "neko"
}
    override fun execute(event: CommandEvent?){
        val embed = EmbedBuilder().setColor(Color.PINK)
        try {
            val url = readJsonFromUrl("https://aws.random.cat/meow").getString("file")
            embed.setImage(url)
                .setAuthor("random.cat","https://aws.random.cat/view/876")
        } catch (e: IOException) {
            val url = readJsonFromUrl("https://randomfox.ca/floof/").getString("image")
            embed.setImage(url)
                .setAuthor("randomfox.ca","https://randomfox.ca/")
        }
        event?.reply(embed.build())
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
            .setColor(Color.PINK) //Embed左端の色を設定します。今回は緑。
            .addField("about","BOTの導入数・BOT招待URLを表示",false)
            .addField("neko","にゃー",false) //以下3つフィールドをセット
            .addField("mcserver <サーバーアドレス>","minecraftサーバーステータスを取得",false)
            .addField("mcskin <ユーザー名>","minecraftスキンを取得",false)
            .addField("mcbeskin <ユーザー名>","minecraft(BE)スキンを取得(new)",false)
            .addField("gcset","グローバルチャットを設定",false)
            .addField("poll <タイトル> <項目１> <項目２>...","投票を設定",false)
            .addField("pollr <投票ID>","投票結果をグラフで表示",false)
            .addField("omikuji","おみくじ",false)
            .addField("dice","サイコロ 1から6",false)
            .addField("vc","読み上げの開始/停止",false)
            .addField("uuid","uuidを取得",false)
            .addField("xuid","xuidを取得",false)
            .addField("vote","v",false)
            .addField("del <ユーザ名> <削除する数>","指定ユーザーのメッセージを一括削除",false)
            .addField("url <url>","urlのリダイレクト先を表示",false)
            .build() //buildは一番最後の組み立て処理です。書き忘れないようにしましょう。
        event?.reply(embed)
    }
}



class BotClient: ListenerAdapter(){

    lateinit var jda: JDA
    private val commandPrefix = "."
    companion object {
        var waiter = EventWaiter()
    }
    fun main(token: String) {



        val commandClient = CommandClientBuilder()
            .setPrefix(commandPrefix)
            .setOwnerId("")
            .addCommands(urlchecksetting(),covidset(),Neko(),help(),about(),mcskin(),gcset(),poll(),pollresult(),mcserver(),omikuzi(),dice(),mcbeskin(),uuid(),xuid(),del(),urlredirect())
            .useHelpBuilder(false)
            .build()

        jda = JDABuilder.createDefault(token,
            GatewayIntent.GUILD_MESSAGES,GatewayIntent.GUILD_MEMBERS)
            .addEventListeners(commandClient)
            .addEventListeners(this,waiter)
            .build()


    }



    override fun onReady(event: ReadyEvent) { //Botがログインしたときの処理
        val playerManager: AudioPlayerManager = DefaultAudioPlayerManager()
        AudioSourceManagers.registerRemoteSources(playerManager)
        covidtimer(event.jda)
        println("起動しました")
    }

    fun getEventWaiter(): EventWaiter? {
        return waiter
    }

    override fun onGuildMessageReceived(event : GuildMessageReceivedEvent) {
        if (dbcheck(event)) {
            val regex = "(http://|https://){1}[\\w\\.\\-/:\\#\\?\\=\\&\\;\\%\\~\\+]+"
            val urls = regex.toRegex(RegexOption.IGNORE_CASE).findAll(event.message.contentDisplay).map { it.value }
            for (url in urls) {
                urlcheck(url,event)
            }
        }
        //URLCheck


        //Botがメッセージを受信したときの処理
        if(!event.member?.user?.isBot!!){//メッセージ内容を確認
            val conn = getConnection()
            val stmt: Statement = conn!!.createStatement()
            val psts: PreparedStatement = conn.prepareStatement("SELECT * FROM discord WHERE server_id=?")
            psts.setString(1, event.guild.id)
            val rs: ResultSet = psts.executeQuery()

            while (rs.next()) {
                //送信元チャンネルが登録されているか確認
                if(rs.getString("gchannel_id").equals(event.channel.id)) {
                    val embed = EmbedBuilder()
                        .setColor(Color.PINK)
                        .setTitle(event.member?.user?.asTag)
                        .setThumbnail(event.member?.user?.effectiveAvatarUrl)
                        .appendDescription(event.message.contentDisplay)
                        .setFooter(event.guild.name,event.guild.iconUrl)
                    val rs = stmt.executeQuery("SELECT * FROM discord")
                    if (event.message.attachments.size>0) {
                        val imagelist = event.message.attachments
                        embed.setImage(imagelist[0].url)
                    }
                    while (rs.next()) {
                        //送信元チャンネルではないことを確認
                        if (!rs.getString("gchannel_id").equals(event.channel.id)) {
                            val guild = jda.getGuildById(rs.getString("server_id"))
                            val channel = guild?.getTextChannelById(rs.getString("gchannel_id"))
                            channel?.sendMessage(embed.build())?.queue()
                        }
                    }
                }
            }
            stmt.close()
            psts.close()
            conn.close()
        }

}
    fun dbcheck(event: GuildMessageReceivedEvent): Boolean {
        val conn = getConnection()
        val psts = conn?.prepareStatement("SELECT * FROM discord WHERE server_id = ?")
        psts?.setString(1, event?.guild?.id)
        val rs = psts?.executeQuery()
        try {
            if (rs!!.next()) {
                if (!rs.getBoolean("urlcheck")) {
                    return false
                }
                return true
            } else {
                return false
            }
        } finally {
            psts?.close()
            conn?.close()
        }
    }

    fun urlcheck(target_url: String, event: GuildMessageReceivedEvent): Boolean {
        val url = URL("https://safebrowsing.googleapis.com/v4/threatMatches:find?key=AIzaSyBUsc2WXTu9O4kjdjnWrKwijLuR6E1AzoU")
        val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
        val json = "  {\n" +
                "    \"client\": {\n" +
                "      \"clientId\":      \"yourcompanyname\",\n" +
                "      \"clientVersion\": \"1.5.2\"\n" +
                "    },\n" +
                "    \"threatInfo\": {\n" +
                "      \"threatTypes\":      [\"MALWARE\", \"SOCIAL_ENGINEERING\"],\n" +
                "      \"platformTypes\":    [\"WINDOWS\"],\n" +
                "      \"threatEntryTypes\": [\"URL\"],\n" +
                "      \"threatEntries\": [\n" +
                "     {\"url\": \""+target_url+"\"}\n" +
                "      ]\n" +
                "    }\n" +
                "  }"

        conn.setRequestMethod("POST")
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")
        conn.setDoInput(true);
        conn.setDoOutput(true);

        conn.connect()

        val ps = PrintStream(conn.outputStream)
        ps.print(json)
        ps.close()

        if (conn.getResponseCode() != 200) {
            //エラー処理
        }

        val br = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8"))

        val sb = readAll(br)
        br.close()

        if (JSONObject(sb).isNull("matches")) {
            return true
        }

        val embed = EmbedBuilder()
            .setTitle("URLは危険です！")
            .setColor(Color.PINK)
            .addField("Type",JSONObject(sb).getJSONArray("matches").getJSONObject(0).getString("threatType"),false)
            .setFooter("Safe Browsing Lookup API")
            .build()
        event?.message.reply(embed).queue()
        return false

        //結果は呼び出し元に返しておく
    }
}


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

