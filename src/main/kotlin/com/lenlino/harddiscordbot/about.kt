package com.lenlino.harddiscordbot

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.Reader
import java.net.URL
import java.nio.charset.Charset


class about: Command(){
    init {
        this.name="mcserver"
        this.help="BOTの情報を表示"
    }

    override fun execute(event: CommandEvent?) {
        if(event?.args?.isEmpty()==false){
            val apiResponse:JSONObject = readJsonFromUrl("https://api.mcsrvstat.us/2/" + event.args)
            if (apiResponse.getBoolean("online")) {
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
        val `is` = URL(url).openStream()
        return try {
            val rd = BufferedReader(InputStreamReader(`is`, Charset.forName("UTF-8")))
            val jsonText = readAll(rd)
            JSONObject(jsonText)
        } finally {
            `is`.close()
        }
    }
}