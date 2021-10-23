package com.lenlino.harddiscordbot

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import java.awt.Color
import java.io.IOException

class Joinset: Command(){
    init {
        this.name = "setjoin"
    }
    override fun execute(event: CommandEvent?){
        val embed = EmbedBuilder().setColor(Color.PINK)
        try {
            val url = readJsonFromUrl("https://aws.random.cat/meow").getString("file")
            embed.setImage(url)
                .setAuthor("random.cat","https://aws.random.cat/view/876")
        } catch (e: IOException) {
            val url = readJsonFromUrl("https://randomfox.ca/floof/").getString("image")
            embed.setImage(url)
                .setAuthor("randomfox.ca","https://randomfox.ca/")
        }
        event?.reply(embed.build())
    }
}