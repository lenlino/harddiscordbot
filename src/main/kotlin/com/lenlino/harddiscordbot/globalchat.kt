package com.lenlino.harddiscordbot

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.command.annotation.JDACommand
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.JDAInfo
import net.dv8tion.jda.api.entities.ClientType
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.*
import java.net.URI
import java.net.URISyntaxException
import java.sql.*

class gcset:Command(){/*Commandクラスを継承してコマンドを定義*/

init {
    this.name = "gcset" /*コマンド文字列の定義はinitブロックの中に書く必要があります。*/
}
    override fun execute(event: CommandEvent?){
/*executeメソッドはコマンドを叩かれたイベントをキャッチして対応する処理を実行する中核部分です*/
        if (event!!.guild.ownerIdLong==event.member.idLong) {
            val conn = getConnection()
            val psts = conn?.prepareStatement("SELECT * FROM discord WHERE server_id = ?")
            psts?.setString(1, event?.guild?.id)
            val rs = psts?.executeQuery()
            if (!rs!!.next()) {
                val ppst = conn?.prepareStatement("INSERT INTO discord values (?,?)")
                ppst?.setString(1,event?.guild?.id)
                ppst?.setString(2,event?.channel?.id)
                ppst?.executeUpdate()
                ppst?.close()
                event?.reply("グローバルチャット用チャンネルの設定が完了しました")
            } else {
                val ppstadd = conn?.prepareStatement("UPDATE discord SET gchannel_id = ? WHERE server_id = ?")
                ppstadd?.setString(1,event?.channel?.id)
                ppstadd?.setString(2,event?.guild?.id)
                ppstadd?.executeUpdate()
                ppstadd?.close()
                event?.reply("グローバルチャット用チャンネルの更新が完了しました")
            }
            psts?.close()
            conn?.close()
        } else {
            event.reply("このコマンドはサーバー管理者のみが実行できます")
        }


/*
ここで使われているreplyメソッドは
event?.message?.channel?.sendMessageFormat("")?.queue()
の簡易呼び出しです。Discordの「返信」とは異なりますのでご注意ください。
*/
    }
}