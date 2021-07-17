package com.lenlino.harddiscordbot

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.exceptions.ContextException
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartUtils
import org.jfree.chart.StandardChartTheme
import org.jfree.chart.labels.StandardPieSectionLabelGenerator
import org.jfree.chart.plot.PiePlot
import org.jfree.data.general.DefaultPieDataset
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.pbkdf2.Integers
import java.awt.Color
import java.io.File
import java.util.*


class poll: Command() {/*Commandクラスを継承してコマンドを定義*/
init {
    this.name = "poll" /*コマンド文字列の定義はinitブロックの中に書く必要があります。*/
}

    override fun execute(event: CommandEvent?) {
/*executeメソッドはコマンドを叩かれたイベントをキャッチして対応する処理を実行する中核部分です*/
        if (event?.args?.isEmpty() == true) {
            event.reply("タイトルを指定してください")
            return
        }
        val args = event?.args?.replace("　"," ")?.split(" ")?.toList()
        event?.reply(args.toString())

        if (args?.size!! >1) {
            val alphabed :List<String> = Arrays.asList("\uD83C\uDDE6","\uD83C\uDDE7","\uD83C\uDDE8","\uD83C\uDDE9","\uD83C\uDDEA","\uD83C\uDDEB","\uD83C\uDDEC")
            val embed = EmbedBuilder().setTitle(args[0])
            for (i in 1..args.size-1) {
                embed.appendDescription(alphabed[i-1]+args[i]+"\n")
            }
            embed.setColor(Color.PINK)
            embed.setAuthor(event.member.effectiveName,null,event.member.user.effectiveAvatarUrl)
            //リアクション追加
            event.channel.sendMessage(embed.build()).queue(({ t: Message? ->
                for (i in 0..args.size-2) {
                    t?.addReaction(alphabed[i])?.queue()
                }
                event.reply("結果を画像出力:arrow_down:")
                event.reply(".pollr " + t?.id)
            }))

        } else if (args?.size == 1) {
            val embed = EmbedBuilder().setTitle(event.args[0].toString())
                .setDescription("⭕\n❌")
                .setAuthor(event.member.effectiveName,null,event.member.user.effectiveAvatarUrl)
                .build()
            event.channel.sendMessage(embed).queue(({ message -> message.addReaction("⭕").queue()
            message.addReaction("❌").queue()
            event.reply("グラフで結果を表示")
            event.reply(".pollr " + message.id)}))
        } else if (args?.size == 0){
            event.channel.sendMessage("タイトルを指定してください")
        }
    }
}

class pollresult: Command() {/*Commandクラスを継承してコマンドを定義*/
init {
    this.name = "pollr" /*コマンド文字列の定義はinitブロックの中に書く必要があります。*/
}

    override fun execute(event: CommandEvent?) {
        if(event?.args?.isNotEmpty() == true) {
            try {
                event.channel.retrieveMessageById(event.args).queue(
                    ({t ->
                    if (t.member?.user?.isBot == true&&t.embeds.isNotEmpty()) {
                        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme())
                        val embed = t.embeds[0]
                        val list = embed.description?.split("\n")?.toList()
                        val pie = DefaultPieDataset<String>()
                        val reaction = t.reactions
                        val file = File("pie.png")
                        for(i in 0..list?.size!! -1) {
                            pie.setValue(list[i],reaction[i].count-1)
                        }
                        val chart = ChartFactory.createPieChart(embed.title, pie, true, false, false)
                        val piePlot = chart.plot as PiePlot<*>
                        piePlot.setLabelGenerator(StandardPieSectionLabelGenerator("{0} {2} {1}票"))


                        ChartUtils.saveChartAsPNG(file, chart, 400, 400)
                        val emb = EmbedBuilder()
                            .setImage("attachment://pie.png")
                            .build()
                        event.channel.sendMessage(emb)
                            .addFile(file, "pie.png")
                            .queue()
                    }
                }), ({error->
                        event.reply("メッセージが存在しません")
                    }))
            } catch (e :IllegalArgumentException) {
                event.reply("不正なIDが指定されています")
            }
        }

    }
}
