package com.lenlino.harddiscordbot

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.arnx.jsonic.JSON
import net.dv8tion.jda.internal.requests.WebSocketClient
import org.java_websocket.drafts.Draft
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import javax.net.ssl.SSLContext


class eewset: Command() {
    init {
        this.name = "eewset"
    }

    override fun execute(event: CommandEvent?) {
        if (event!!.guild.ownerIdLong==event.member.idLong) {
            val conn = getConnection()
            val psts = conn?.prepareStatement("SELECT * FROM discord WHERE server_id = ?")
            psts?.setString(1, event?.guild?.id)
            val rs = psts?.executeQuery()
            if (!rs!!.next()) {
                val ppst = conn?.prepareStatement("INSERT INTO discord (server_id,eewch_id) values (?,?)")
                ppst?.setString(1,event?.guild?.id)
                ppst?.setString(2,event?.channel?.id)
                ppst?.executeUpdate()
                ppst?.close()
                event?.reply("地震速報用チャンネルの設定が完了しました")
            } else {
                val ppstadd = conn?.prepareStatement("UPDATE discord SET eewch_id = ? WHERE server_id = ?")
                ppstadd?.setString(1,event?.channel?.id)
                ppstadd?.setString(2,event?.guild?.id)
                ppstadd?.executeUpdate()
                ppstadd?.close()
                event?.reply("地震速報用チャンネルの更新が完了しました")
            }
            psts?.close()
            conn?.close()
        } else {
            event.reply("このコマンドはサーバー管理者のみが実行できます")
        }
    }
}

class eew_websocket_client(uri: URI): org.java_websocket.client.WebSocketClient(uri) {
    override fun onOpen(handshakedata: ServerHandshake?) {


    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {

    }

    override fun onMessage(message: String?) {
    }

    override fun onError(ex: Exception?) {

    }
}