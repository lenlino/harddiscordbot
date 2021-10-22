package com.lenlino.harddiscordbot

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent

fun gcset(event: SlashCommandEvent){
    event.deferReply().queue()
    if (event!!.guild?.ownerIdLong==event.member?.idLong) {
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
            event?.hook.sendMessage("グローバルチャット用チャンネルの設定が完了しました").queue()
        } else {
            val ppstadd = conn?.prepareStatement("UPDATE discord SET gchannel_id = ? WHERE server_id = ?")
            ppstadd?.setString(1,event?.channel?.id)
            ppstadd?.setString(2,event?.guild?.id)
            ppstadd?.executeUpdate()
            ppstadd?.close()
            event?.hook.sendMessage("グローバルチャット用チャンネルの更新が完了しました").queue()
        }
        psts?.close()
        conn?.close()
    } else {
        event.hook.sendMessage("このコマンドはサーバー管理者のみが実行できます").queue()
    }
}