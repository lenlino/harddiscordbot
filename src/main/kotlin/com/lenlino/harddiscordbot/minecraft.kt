package com.lenlino.harddiscordbot

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONString
import java.awt.Color
import java.io.*
import java.net.URL
import java.net.URLConnection
import java.nio.charset.Charset

fun uuid(event: SlashCommandEvent) {
    event.deferReply().queue()
    val api = readJsonFromUrl("https://api.mojang.com/users/profiles/minecraft/"+event?.getOption("username")?.asString)
    if (!api.has("null")) {
        val embed = EmbedBuilder()
            .setTitle(api.getString("name"))
            .setFooter(api.getString("id"))
            .setColor(Color.PINK)
            .build()
        event?.hook.sendMessageEmbeds(embed).queue()
    } else {
        nouserembed(event)
    }
}

fun xuid(event: SlashCommandEvent) {
    event.deferReply().queue()
    try {
        val xuid = readJsonFromUrl("https://xbl-api.prouser123.me/xuid/" + event.getOption("username")?.asString)
        if (!xuid.has("error")) {
            //GeyserMCサーバーよりテクスチャIDを取得
            val id = xuid.getString("xuid")
            val embed = EmbedBuilder()
                .setTitle(event.getOption("name")?.asString)
                .addField("xuid",id,false)
                .setColor(Color.PINK)
                .build()
            event.hook.sendMessageEmbeds(embed).queue()
        } else {
            nouserembed(event)
        }
    } catch (e: FileNotFoundException) {
        nouserembed(event)
    }
}

fun mcserver(event: SlashCommandEvent) {
    event.deferReply().queue()
    val apiResponse:JSONObject = readJsonFromUrl("https://api.mcsrvstat.us/2/" + event.getOption("address")?.asString)
    if (apiResponse.getBoolean("online") ) {
        val players:JSONObject = apiResponse.getJSONObject("players")
        val motd:JSONObject = apiResponse.getJSONObject("motd")
        val embed = EmbedBuilder()
            .setTitle("サーバー情報")
            .addField("バージョン",apiResponse.getString("version"),false)
            .addField("アドレス",apiResponse.getString("ip"),false)
            .addField("プレーヤー数",players.getInt("online").toString()+"/"+players.getInt("max").toString(),false)
            .addField("MOTD",motd.getJSONArray("clean").toString(),false)
            .setThumbnail("https://api.mcsrvstat.us/icon/" + event.getOption("address")?.asString)
            .setColor(Color.PINK)
            .build()
        event?.hook.sendMessageEmbeds(embed).queue()

    } else {
        val embed = EmbedBuilder()
            .setTitle("サーバー情報")
            .appendDescription("サーバーはオフラインです")
            .setColor(Color.RED)
            .build()
        event?.hook.sendMessageEmbeds(embed).queue()
    }
}

fun mcskin(event: SlashCommandEvent) {
    event.deferReply().queue()
    val api = readJsonFromUrl("https://api.mojang.com/users/profiles/minecraft/"+event.getOption("username")?.asString)
    if (!api.has("null")) {
        val embed = EmbedBuilder()
            .setTitle(api.getString("name"))
            .setThumbnail("https://crafatar.com/skins/"+api.getString("id"))
            .setImage("https://crafatar.com/renders/body/"+api.getString("id"))
            .setColor(Color.PINK)
            .build()
        event?.hook.sendMessageEmbeds(embed).queue()
    } else {
        nouserembed(event)
    }
}

fun mcbeskin(event: SlashCommandEvent) {
    //https://github.com/crafatar/crafatar/issues/287を参考に作成
    event.deferReply().queue()
    try {
        val xuid = readJsonFromUrl("https://xbl-api.prouser123.me/xuid/" + event.getOption("username")?.asString)
        if (!xuid.has("error")) {
            //GeyserMCサーバーよりテクスチャIDを取得
            val id = readJsonFromUrl("https://api.geysermc.org/v1/skin/"+xuid.getString("xuid"))
            if (id.getJSONObject("data").length()!=0){
                val embed = EmbedBuilder()
                    .setTitle(event?.getOption("name")?.asString)
                    .setImage("https://mc-heads.net/player/"+id.getJSONObject("data").getString("texture_id"))
                    .setThumbnail("http://textures.minecraft.net/texture/"+id.getJSONObject("data").getString("texture_id"))
                    .setColor(Color.PINK)
                    .build()
                event?.hook.sendMessageEmbeds(embed).queue()
                return
            } else {
                val embed = EmbedBuilder()
                    .setColor(Color.RED)
                    .setTitle("GeyserMCのサーバー上にデータがありません。")
                    .setDescription("GeyserMCが導入されているサーバーに入るとスキンが登録されます")
                    .build()
                event?.hook.sendMessageEmbeds(embed).queue()

            }
        } else {
            nouserembed(event)
            return
        }
    } catch (e: FileNotFoundException) {
        nouserembed(event)
        return
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

fun about(event: SlashCommandEvent) {
    val embed = EmbedBuilder()
        .setTitle("Info")
        .addField("サーバー導入数",event?.jda?.guilds?.size.toString(),false)
        .setAuthor("BOTを招待","https://discord.com/api/oauth2/authorize?client_id=860827174541721600&permissions=0&scope=bot%20applications.commands")
        .setColor(Color.PINK)
        .build()
    event?.replyEmbeds(embed).queue()
}

private fun nouserembed(event: SlashCommandEvent) {
    val embed = EmbedBuilder()
        .setTitle("ユーザーが存在しません")
        .setColor(Color.RED)
        .build()
    event?.hook.sendMessageEmbeds(embed).queue()
}