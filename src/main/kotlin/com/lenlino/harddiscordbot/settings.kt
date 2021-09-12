package com.lenlino.harddiscordbot

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import java.awt.Color

class urlchecksetting: Command() {
    init {
        this.name = "urlcheck"
    }

    override fun execute(event: CommandEvent) {
        if (!event.member.hasPermission(Permission.MANAGE_SERVER)) {
            val embed = EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("サーバーの管理権限が必要です")
                .build()
            event.reply(embed)
            return
        }
        val conn = getConnection()
        val psts = conn?.prepareStatement("SELECT * FROM discord WHERE server_id = ?")
        psts?.setString(1, event?.guild?.id)
        val rs = psts?.executeQuery()
        if (!rs!!.next()) {
            val ppst = conn?.prepareStatement("INSERT INTO discord (server_id,urlcheck) values (?,?)")
            ppst?.setString(1,event?.guild?.id)
            ppst?.setBoolean(2,true)
            ppst?.executeUpdate()
            ppst?.close()
            val embed = EmbedBuilder().setColor(Color.PINK).setTitle("危険なURL検出をオンにしました").build()
            event.reply(embed)
        } else {
            if (!rs.getBoolean("urlcheck")) {
                val ppstadd = conn?.prepareStatement("UPDATE discord SET urlcheck = ? WHERE server_id = ?")
                ppstadd?.setBoolean(1,true)
                ppstadd?.setString(2,event.guild.id)
                ppstadd?.executeUpdate()
                ppstadd?.close()
                val embed = EmbedBuilder().setColor(Color.PINK).setTitle("危険なURL検出をオンにしました").build()
                event.reply(embed)
            } else {
                val ppstadd = conn?.prepareStatement("UPDATE discord SET urlcheck = ? WHERE server_id = ?")
                ppstadd?.setBoolean(1,false)
                ppstadd?.setString(2,event.guild.id)
                ppstadd?.executeUpdate()
                ppstadd?.close()
                val embed = EmbedBuilder().setColor(Color.PINK).setTitle("危険なURL検出をオフにしました").build()
                event.reply(embed)
            }
        }
        psts?.close()
        conn?.close()
    }
}