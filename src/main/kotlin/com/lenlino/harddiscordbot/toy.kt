package com.lenlino.harddiscordbot

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import java.awt.Color
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import java.util.*

fun dice(event: SlashCommandEvent) {
    event.deferReply().queue()
    val embed = EmbedBuilder()
        .setColor(Color.PINK)
        .setTitle((1..6).random().toString())
        .build()
    event.hook.sendMessageEmbeds(embed).queue()
}