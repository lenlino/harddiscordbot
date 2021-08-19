package com.lenlino.harddiscordbot

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import java.util.*

class omikuzi: Command() {
    init {
        this.name = "omikuji"
    }

    override fun execute(event: CommandEvent?) {
        val contexts: List<String> = Arrays.asList("大吉","中吉","小吉","吉","末吉","凶","大凶")
        val embed = EmbedBuilder()
            .setTitle(contexts[(0..6).random()])
            .build()
        event?.reply(embed)
    }
}
class dice: Command() {
    init {
        this.name = "dice"
    }

    override fun execute(event: CommandEvent?) {
        event?.reply((1..6).random().toString())
    }
}
class omikujiset: Command() {
    init {
        this.name = "omikujiset"
    }

    override fun execute(event: CommandEvent?) {
        if (event!!.guild.ownerIdLong==event.member.idLong) {
            val args = event.args.replace("　"," ").split(" ").toList()
            if (args.size==7) {
                val conn = getConnection()
                val stmt: Statement = conn!!.createStatement()
                val psts: PreparedStatement = conn.prepareStatement("SELECT * FROM discord WHERE server_id=?")
                psts.setString(1, event?.guild?.id)
                val rs: ResultSet = psts.executeQuery()
                if (!rs!!.next()) {
                    val ppst = conn?.prepareStatement("INSERT INTO discord (server_id,omikuji_contents)values (?,?)")
                    ppst?.setString(1,event?.guild?.id)
                    ppst?.setString(2,args.toString().replace("[","").replace("]",""))
                    ppst?.executeUpdate()
                    ppst?.close()
                    event.reply("メッセージを設定しました")
                } else {
                    val ppstadd = conn?.prepareStatement("UPDATE discord SET omikuji_contents = ? WHERE server_id = ?")
                    ppstadd?.setString(1,args.toString().replace("[","").replace("]",""))
                    ppstadd?.setString(2,event?.guild?.id)
                    ppstadd?.executeUpdate()
                    ppstadd?.close()
                    event?.reply("メッセージを更新しました")
                }
                conn.close()
            } else {
                event.reply("メッセージがすべて入力されていません。")
                event.reply(args.toString())
            }

        }
    }
}