package com.lenlino.harddiscordbot

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.ApplicationInfo
import net.dv8tion.jda.api.entities.MessageActivity
import okhttp3.internal.wait
import org.json.JSONObject
import java.awt.Color
import java.sql.Statement
import java.util.*

class covidset: Command() {
    init {
        this.name="covidset"
    }

    override fun execute(event: CommandEvent?) {
        if (event!!.guild.ownerIdLong==event.member.idLong) {
            val conn = getConnection()
            val psts = conn?.prepareStatement("SELECT * FROM discord WHERE server_id = ?")
            psts?.setString(1, event?.guild?.id)
            val rs = psts?.executeQuery()
            val embed = EmbedBuilder()
                .setTitle("covid通知用チャンネルの設定が完了しました")
                .setFooter("日本時間7時に自動配信されます。")
                .setColor(Color.PINK)
                .build()
            if (!rs!!.next()) {
                val ppst = conn?.prepareStatement("INSERT INTO discord (?,?) values (?,?)")
                ppst?.setString(1,"server_id")
                ppst?.setString(2,"covid")
                ppst?.setString(3,event?.guild?.id)
                ppst?.setString(4,event?.channel?.id)
                ppst?.executeUpdate()
                ppst?.close()
                event?.reply(embed)
            } else {
                val ppstadd = conn?.prepareStatement("UPDATE discord SET covid = ? WHERE server_id = ?")
                ppstadd?.setString(1,event?.channel?.id)
                ppstadd?.setString(2,event?.guild?.id)
                ppstadd?.executeUpdate()
                ppstadd?.close()
                event?.reply(embed)
            }
            psts?.close()
            conn?.close()
        } else {
            event.reply("このコマンドはサーバー管理者のみが実行できます")
        }
    }
}

fun covidtimer(jda: JDA) {
    val task: TimerTask = object : TimerTask() {
        //timertask内容
        override fun run() {
            val conn = getConnection()
            val stmt: Statement = conn!!.createStatement()
            val rs = stmt.executeQuery("SELECT * FROM discord WHERE covid IS NOT NULL")
            //jsonを取得
            val jsons = readJsonFromUrl("https://www.stopcovid19.jp/data/covid19japan.json")
            val embed1 = EmbedBuilder()
                .setTitle("現在患者数("+jsons.getString("lastUpdate")+"時点)")
                .setColor(Color.RED)
                .setAuthor("stopcovid19.jp","https://www.stopcovid19.jp/")
            for (i in 0..jsons.getJSONArray("area").length()-1) {
                val area: JSONObject = jsons.getJSONArray("area")[i] as JSONObject
                embed1.addField(area.getString("name_jp"),area.getInt("ncurrentpatients").toString(),true)
            }
            val embed2 = EmbedBuilder()
                .setColor(Color.RED)
            for (i in 25..jsons.getJSONArray("area").length()-1) {
                val area: JSONObject = jsons.getJSONArray("area")[i] as JSONObject
                embed2.addField(area.getString("name_jp"),area.getInt("ncurrentpatients").toString(),true)
            }

            while (rs.next()) {
                val guild = jda.getGuildById(rs.getString("server_id"))
                val channel = guild?.getTextChannelById(rs.getString("covid"))
                channel?.sendMessage(embed1.build())?.queue()
                channel?.sendMessage(embed2.build())?.queue()
            }
            stmt.close()
            conn.close()
        }
    }
    val date = Date()
    date.setHours(19)
    date.setMinutes(0)
    date.setSeconds(0)
    val timer = Timer(false)
    timer.schedule(task,date,1000*60*60*24)
}