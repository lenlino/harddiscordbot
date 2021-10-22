package com.lenlino.harddiscordbot

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartUtils
import org.jfree.chart.StandardChartTheme
import org.jfree.chart.labels.StandardPieSectionLabelGenerator
import org.jfree.chart.plot.PiePlot
import org.jfree.chart.title.TextTitle
import org.jfree.data.general.DefaultPieDataset
import java.awt.Color
import java.awt.Font
import java.io.File
import java.util.*

val alphabed :List<String> = Arrays.asList("\uD83C\uDDE6","\uD83C\uDDE7","\uD83C\uDDE8","\uD83C\uDDE9","\uD83C\uDDEA","\uD83C\uDDEB","\uD83C\uDDEC")

fun poll(event: SlashCommandEvent) {
    event.deferReply().queue()

    print(event?.getOption("title_and_options")?.asString)
    val args = event?.getOption("title_and_options")?.asString?.replace("　"," ")?.split(" ")?.toList()
    if (args?.size!! >1) {
        val embed = EmbedBuilder().setTitle(args[0])
        for (i in 1..args.size-1) {
            embed.appendDescription(alphabed[i-1]+args[i]+"\n")
        }
        embed.setColor(Color.PINK)
        embed.setAuthor(event.member?.effectiveName,null, event.member?.user?.effectiveAvatarUrl)
        embed.addField("グラフで結果を表示:arrow_down:","",false)
        //リアクション追加
        event.hook.sendMessageEmbeds(embed.build()).queue(({ t: Message? ->
            for (i in 0..args.size-2) {
                t?.addReaction(alphabed[i])?.queue()
            }
            event.reply(".pollr " + t?.id)
        }))

    } else if (args?.size == 1) {
        val embed = EmbedBuilder().setTitle(event.getOption("title_and_options").toString())
            .setDescription("⭕\n❌")
            .setAuthor(event.member?.effectiveName,null,event.member?.user?.effectiveAvatarUrl)
            .addField("グラフで結果を表示:arrow_down:","",false)
            .build()
        event.hook.sendMessageEmbeds(embed)

            .queue(({ message -> message.addReaction("⭕").queue()
                message.addReaction("❌").queue()
                event.reply(".pollr " + message.id)}))
    }
}

fun pollr(event: SlashCommandEvent) {
    event.deferReply().queue()
    try {
        event.channel.retrieveMessageById(event.getOption("id")!!.asString).queue(
            ({ t ->
                if (t.member?.user?.isBot == true && t.embeds.isNotEmpty()) {
                    ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme())
                    val embed = t.embeds[0]
                    val list = embed.description?.split("\n")?.toList()
                    val pie = DefaultPieDataset<String>()
                    val reaction = t.reactions
                    val file = File("pie.png")
                    for (i in 0..list?.size!! - 1) {
                        pie.setValue(list[i].replace(alphabed[i], ""), reaction[i].count - 1)
                    }
                    val chart = ChartFactory.createPieChart(embed.title, pie, true, false, false)
                    val piePlot = chart.plot as PiePlot<*>
                    val Font = Font("TakaoPGothic", Font.PLAIN, 12)
                    piePlot.setLabelGenerator(StandardPieSectionLabelGenerator("{0} {2} {1}票"))
                    piePlot.setLabelFont(Font)


                    ChartUtils.saveChartAsPNG(file, chart, 400, 400)
                    val emb = EmbedBuilder()
                        .setImage("attachment://pie.png")
                        .build()
                    event.hook.sendMessageEmbeds(emb)
                        .addFile(file, "pie.png")
                        .queue()
                }
            }), ({ error ->
                event.hook.sendMessage("メッセージが存在しません").queue()
            })
        )
    } catch (e: IllegalArgumentException) {
        event.hook.sendMessage("不正なIDが指定されています").queue()
    }
}
