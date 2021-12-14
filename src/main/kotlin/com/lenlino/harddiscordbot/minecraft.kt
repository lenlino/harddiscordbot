package com.lenlino.harddiscordbot

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONString
import java.awt.Color
import java.io.*
import java.net.URL
import java.net.URLConnection
import java.nio.charset.Charset

class uuid: Command(){
    init {
        this.name="uuid"
    }

    override fun execute(event: CommandEvent?) {
        val api = readJsonFromUrl("https://api.mojang.com/users/profiles/minecraft/"+event?.args)
        if (!api.has("null")) {
            val embed = EmbedBuilder()
                .setTitle(api.getString("name"))
                .setFooter(api.getString("id"))
                .setColor(Color.PINK)
                .build()
            event?.reply(embed)
        } else {
            nouserembed(event)
        }
    }
}

class xuid: Command(){
    init {
        this.name="xuid"
    }

    override fun execute(event: CommandEvent?) {
        if(event?.args?.isEmpty()==false) {
            //APIよりゲーマタグからxuid取得
            try {
                val xuid = readJsonFromUrl("https://xbl-api.prouser123.me/xuid/" + event.args)
                if (!xuid.has("error")) {
                    //GeyserMCサーバーよりテクスチャIDを取得
                    val id = xuid.getString("xuid")
                    val embed = EmbedBuilder()
                        .setTitle(event.args)
                        .addField("xuid",id,false)
                        .setColor(Color.PINK)
                        .build()
                    event.reply(embed)
                } else {
                    nouserembed(event)
                }
            } catch (e: FileNotFoundException) {
                nouserembed(event)
            }
        } else {
            val embed = EmbedBuilder()
                .setTitle("ユーザー名を入力してください")
                .setColor(Color.RED)
                .build()
            event?.reply(embed)
        }
    }
}

class mcserver: Command(){
    init {
        this.name="mcserver"
        this.help="BOTの情報を表示"
    }

    override fun execute(event: CommandEvent?) {
        if(event?.args?.isEmpty()==false){
            val apiResponse:JSONObject = readJsonFromUrl("https://api.mcsrvstat.us/2/" + event.args)
            if (apiResponse.getBoolean("online") ) {
                val players:JSONObject = apiResponse.getJSONObject("players")
                val motd:JSONObject = apiResponse.getJSONObject("motd")
                val embed = EmbedBuilder()
                    .setTitle("サーバー情報")
                    .addField("バージョン",apiResponse.getString("version"),false)
                    .addField("アドレス",apiResponse.getString("ip"),false)
                    .addField("プレーヤー数",players.getInt("online").toString()+"/"+players.getInt("max").toString(),false)
                    .addField("MOTD",motd.getJSONArray("clean").toString(),false)
                    .setThumbnail("https://api.mcsrvstat.us/icon/" + event.args)
                    .setColor(Color.PINK)
                    .build()
                event?.reply(embed)

            } else {
                val embed = EmbedBuilder()
                    .setTitle("サーバー情報")
                    .appendDescription("サーバーはオフラインです")
                    .setColor(Color.RED)
                    .build()
                event?.reply(embed)
            }

        } else {
            val embed = EmbedBuilder()
                .setTitle("サーバーアドレスを入力してください")
                .setColor(Color.RED)
                .build()
            event?.reply(embed)
        }
    }


}

class mcskin: Command() {
    init {
        this.name = "mcskin"
        this.help = "minecraftのスキンを取得"
    }

    override fun execute(event: CommandEvent?) {
        if(event?.args?.isEmpty()==false) {
            val api = readJsonFromUrl("https://api.mojang.com/users/profiles/minecraft/"+event.args)
            if (!api.has("null")) {
                val embed = EmbedBuilder()
                    .setTitle(api.getString("name"))
                    .setThumbnail("https://crafatar.com/skins/"+api.getString("id"))
                    .setImage("https://crafatar.com/renders/body/"+api.getString("id"))
                    .setColor(Color.PINK)
                    .build()
                event?.reply(embed)
            } else {
                nouserembed(event)
            }
        } else {
            val embed = EmbedBuilder()
                .setTitle("ユーザー名を入力してください")
                .setColor(Color.RED)
                .build()
            event?.reply(embed)
        }
    }
}

class mcbeskin: Command() {
    init {
        this.name = "mcbeskin"
    }

    override fun execute(event: CommandEvent?) {
        //https://github.com/crafatar/crafatar/issues/287を参考に作成
        if(event?.args?.isEmpty()==false) {
            //APIよりゲーマタグからxuid取得
            try {
                val xuid = readJsonFromUrl("https://xbl-api.prouser123.me/xuid/" + event.args)
                if (!xuid.has("error")) {
                    //GeyserMCサーバーよりテクスチャIDを取得
                    val id = readJsonFromUrl("https://api.geysermc.org/v2/skin/"+xuid.getString("xuid"))
                    if (!id.isnull("texture_id"){
                        val embed = EmbedBuilder()
                            .setTitle(event?.args)
                            .setImage("https://mc-heads.net/player/"+id.getString("texture_id"))
                            .setThumbnail("http://textures.minecraft.net/texture/"+id.getString("texture_id"))
                            .setColor(Color.PINK)
                            .build()
                        event?.reply(embed)
                        return
                    } else {
                        val embed = EmbedBuilder()
                            .setColor(Color.RED)
                            .setTitle("GeyserMCのサーバー上にデータがありません。")
                            .setDescription("GeyserMCが導入されているサーバーに入るとスキンが登録されます")
                            .build()
                        event?.reply(embed)

                    }
                } else {
                    nouserembed(event)
                    return
                }
            } catch (e: FileNotFoundException) {
                nouserembed(event)
                return
            }
        } else {
            val embed = EmbedBuilder()
                .setTitle("ユーザー名を入力してください")
                .setColor(Color.RED)
                .build()
            event?.reply(embed)
        }
    }
}

@Throws(IOException::class)
fun readAll(rd: Reader): String {
    val sb = StringBuilder()
    var cp: Int
    while (rd.read().also { cp = it } != -1) {
        sb.append(cp.toChar())
    }
    return sb.toString()
}

@Throws(IOException::class, JSONException::class)
fun readJsonFromUrl(url: String?): JSONObject {
    val conn: URLConnection = URL(url).openConnection()
    return try {
        conn.setRequestProperty("User-agent", "Mozilla / 5.0")
        val rd = BufferedReader(InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8")))
        val jsonText = readAll(rd)
        if (jsonText.isEmpty()) {
            val emptyjson = "{null:true}"
            JSONObject(emptyjson)
        } else {
            JSONObject(jsonText)
        }
    } finally {
        conn.getInputStream().close()
    }
}

class about: Command(){
    init {
        this.name="about"
        this.help="BOTの情報を表示"
    }

    override fun execute(event: CommandEvent?) {
        val embed = EmbedBuilder()
            .setTitle("Info")
            .addField("サーバー導入数",event?.jda?.guilds?.size.toString(),false)
            .setAuthor("BOTの招待はこちらから！","https://discord.com/api/oauth2/authorize?client_id=860827174541721600&permissions=0&scope=bot")
            .setColor(Color.PINK)
            .build()
        event?.reply(embed)


    }
}

fun nouserembed(event: CommandEvent?) {
    val embed = EmbedBuilder()
        .setTitle("ユーザーが存在しません")
        .setColor(Color.RED)
        .build()
    event?.reply(embed)
}
