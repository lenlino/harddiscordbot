package com.lenlino.harddiscordbot

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONString
import java.io.*
import java.net.URL
import java.net.URLConnection
import java.nio.charset.Charset


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
                    .build()
                event?.reply(embed)

            } else {
                val embed = EmbedBuilder()
                    .setTitle("サーバー情報")
                    .appendDescription("サーバーはオフラインです")
                    .build()
                event?.reply(embed)
            }

        } else {
            event?.reply("サーバーipを入力してください")
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
                    .build()
                event?.reply(embed)
            } else {
                event?.reply("ユーザーが存在しません")
            }
        } else {
            event?.reply("ユーザー名/UUIDを入力してください")
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
                    val id = readJsonFromUrl("https://api.geysermc.org/v1/skin/"+xuid.getString("xuid"))
                    if (id.getJSONObject("data").length()!=0){
                        val embed = EmbedBuilder()
                            .setTitle(event?.args)
                            .setImage("https://mc-heads.net/player/"+id.getJSONObject("data").getString("texture_id"))
                            .setThumbnail("http://textures.minecraft.net/texture/"+id.getJSONObject("data").getString("texture_id"))
                            .build()
                        event?.reply(embed)
                    } else {
                        event?.reply("GeyserMCのサーバー上にデータがありません。(GeyserMCが導入されているサーバーに入るとスキンが登録されます)")
                    }
                } else {
                    event?.reply("ユーザーが存在しません")
                }
            } catch (e: FileNotFoundException) {
                event?.reply("ユーザーが存在しません")
            }
        } else {
            event?.reply("ユーザー名を入力してください")
        }
    }
}

@Throws(IOException::class)
private fun readAll(rd: Reader): String {
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
            .build()
        event?.reply(embed)


    }
}