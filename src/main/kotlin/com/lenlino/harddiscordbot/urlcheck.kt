package com.lenlino.harddiscordbot

import com.google.common.base.Optional
import com.google.common.collect.Interners.newBuilder
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import java.awt.Color
import java.io.IOException
import java.net.URI

import java.net.URISyntaxException
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*


class urlredirect: Command() {
    init {
        this.name="url"
    }

    override fun execute(event: CommandEvent?) {
        if (event?.args==null) {
            val embed = EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("URLを入力してください")
                .build()
            event?.reply(embed)
        }
        val args = event?.args
        val embed = EmbedBuilder()
            .setColor(Color.PINK)
            .addField("元URL",args,false)
        var now_url = getRedirectUrl(args)?.get()
        while (now_url!=null) {
            embed.addField("↓",now_url,false)
            print(now_url)
            now_url = getRedirectUrl(now_url)?.get()
        }
        if (embed.fields.size==1) {
            embed.setFooter("リダイレクトはありません")
        }
        event?.reply(embed.build())
    }

    @Throws(URISyntaxException::class, IOException::class, InterruptedException::class)
    fun getRedirectUrl(srcUrl: String?): java.util.Optional<String>? {
        // HTTP リクエスト情報を構築
        val req: HttpRequest = HttpRequest.newBuilder(URI(srcUrl)).GET().build()

        // HTTP リクエスト
        // HttpClient は 4xx や 5xx でも例外が発生しないので注意
        val client: HttpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NEVER) // 自動でリダイレクトしない設定
            .build()
        val res: HttpResponse<String> = client.send(req, HttpResponse.BodyHandlers.ofString())

        // HTTP レスポンスから Location ヘッダを取得
        if (res.headers().firstValue("location").isEmpty) {
            return null
        }
        return res.headers().firstValue("location")
    }
}