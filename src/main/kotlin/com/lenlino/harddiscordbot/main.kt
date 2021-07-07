package com.lenlino.harddiscordbot

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandClientBuilder
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent


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
        event?.reply("にゃーん2")
/*
ここで使われているreplyメソッドは
event?.message?.channel?.sendMessageFormat("")?.queue()
の簡易呼び出しです。Discordの「返信」とは異なりますのでご注意ください。
*/
    }
}

class help:Command(){/*Commandクラスを継承してコマンドを定義*/
init {
    this.name = "hel" /*コマンド文字列の定義はinitブロックの中に書く必要があります。*/
}
    override fun execute(event: CommandEvent?){
/*executeメソッドはコマンドを叩かれたイベントをキャッチして対応する処理を実行する中核部分です*/
        val embed = EmbedBuilder()//EmbedBuilderでインスタンスを作成して、後から中身をセットします。
            //タイトル文字列。第2引数にURLを入れるとタイトルを指定URLへのリンクにできます
            .setTitle("Tutorial Embed","https://example.com")

            //Botの情報。タイトルと同じくリンクを指定できる他、第3引数にアイコン画像を指定できます。
            //今回は自分のアバターアイコンを指定しました。
            .setAuthor("tutorial bot","https://repo.exapmle.com/bot",event?.selfUser?.avatarUrl)

            .appendDescription("Embed made with Kotlin JDA!!") //Embedの説明文
            .setColor(0x00ff00) //Embed左端の色を設定します。今回は緑。
            .addField("フィールド1","値1",false) //以下3つフィールドをセット
            .addField("フィールド2","値2",true)
            .addField("フィールド3","値3",true)
            .setThumbnail("https://image.example.com/thumbnail.png") //サムネイル(小さい画像)
            .setImage("https://image.example.com/main.png") //イメージ(大きい画像)

            //フッターには開発者情報を入れるといいでしょう。
            .setFooter("made by NashiroAoi","https://dev.exapmple.com/profile.png")
            .build() //buildは一番最後の組み立て処理です。書き忘れないようにしましょう。
        event?.reply(embed)
/*
ここで使われているreplyメソッドは
event?.message?.channel?.sendMessageFormat("")?.queue()
の簡易呼び出しです。Discordの「返信」とは異なりますのでご注意ください。
*/
    }
}



class BotClient{
    lateinit var jda: JDA
    private val commandPrefix = "." /*コマンドプレフィックスの指定
                                     空文字列にするとBotへのメンションがプレフィックスとして機能します。
　　　　　　　　　　　　　　　　　　　　このほうがいいかも。*/

    fun main(token: String) {

        val commandClient = CommandClientBuilder()
            .setPrefix(commandPrefix)
            .setOwnerId("") /*本来であれば開発者のIDを入れますが、空文字列でもOKです。*/
            .addCommands(Neko(),kick(),help(),about())
            .useHelpBuilder(false)
            .build()

        jda = JDABuilder.createLight(token,
            GatewayIntent.GUILD_MESSAGES)
            .addEventListeners(commandClient)
            .build()
    }
}



fun main(args:Array<String>) {
    val token = System.getenv("Discord_Bot_Token")
    val bot = BotClient()
    bot.main("ODYwODI3MTc0NTQxNzIxNjAw.YOA5xw.HRgcoZNRnR8fmAAM2rZZqNOY6oU")
}