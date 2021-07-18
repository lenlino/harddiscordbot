package com.lenlino.harddiscordbot

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
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