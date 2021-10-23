package com.lenlino.harddiscordbot

import com.fasterxml.jackson.databind.KeyDeserializer
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.interactions.components.Button
import net.dv8tion.jda.api.interactions.components.ButtonStyle
import okhttp3.internal.wait
import org.apache.commons.lang3.ObjectUtils
import java.awt.Color
import java.util.concurrent.TimeUnit
import kotlin.math.max


class del: Command() {
    private var page = 0
    private var result:Member? = null
    init {
        this.name = "del"
    }

    override fun execute(event: CommandEvent) {
        //権限チェック
        if (!event.member.hasPermission(Permission.MESSAGE_MANAGE)) {
            val embed = EmbedBuilder()
                .setTitle("メッセージの管理権限が必要です")
                .setColor(Color.RED)
                .build()
            event.reply(embed)
            return
        }
        //サブが空出ないか確認
        if (event.args?.isEmpty() == true) {
            val embed = EmbedBuilder()
              .setTitle("ターゲットと削除するメッセージ数を含めてください")
              .setColor(Color.RED)
              .build()
            event.reply(embed)
            return
        }
        val args = event.args.replace("　"," ").split(" ")
        val target_list = event.guild.getMembersByName(args[0],false)
        val target = event.guild.getMemberById(args[0])
        if (target_list.size==0 && target == null) {
            val embed = EmbedBuilder()
                .setTitle("ユーザーが見つかりません")
                .setColor(Color.RED)
                .build()
            event.reply(embed)
            return
        } else if (target != null) {
            select_member(event,args[0],false)
        }
        select_member(event,args[0],true)
    //メッセージ取得

    }

    fun select_member(event: CommandEvent, string: String, id: Boolean) {
        if (id == true){

            val members: ArrayList<Member> = arrayListOf(event.guild.getMemberById(string)!!)
            waiter(button_menu(event,page,members),members,event)
            return
        }
        val members = event.guild.getMembersByName(string,false)
        waiter(button_menu(event,page,members),members,event)
    }

    fun button_menu(event: CommandEvent,page: Int,members: List<Member>):Message{
        val message = event.channel.sendMessage(make_embed(members[page],page,members.size))
        if (members.size==1){
            message.setActionRow( // Add our Buttons (max 5 per ActionRow)
                Button.of(ButtonStyle.SECONDARY, "左", "左").asDisabled(),     // 🐱
                Button.of(ButtonStyle.PRIMARY, "決定", "決定"),
                Button.of(ButtonStyle.SECONDARY, "右", "右").asDisabled())
            return message.complete()
        } else if(page==0) {
            message.setActionRow(
                Button.of(ButtonStyle.SECONDARY, "左", "左").asDisabled(),     // 🐱
                Button.of(ButtonStyle.PRIMARY, "決定", "決定"),
                Button.of(ButtonStyle.SECONDARY, "右", "右")
            )
            return message.complete()
        } else if(page==members.size-1) {
            message.setActionRow(
                Button.of(ButtonStyle.SECONDARY, "左", "左"),     // 🐱
                Button.of(ButtonStyle.PRIMARY, "決定", "決定"),
                Button.of(ButtonStyle.SECONDARY, "右", "右").asDisabled()
            )
            return message.complete()
        }
        message.setActionRow(
            Button.of(ButtonStyle.SECONDARY, "左", "左"),     // 🐱
            Button.of(ButtonStyle.PRIMARY, "決定", "決定"),
            Button.of(ButtonStyle.SECONDARY, "右", "右")
        )
        return message.complete()
    }

    private fun waiter(message: Message,members: List<Member>,event: CommandEvent) {
        BotClient.waiter.waitForEvent(ButtonClickEvent::class.java,{e-> return@waitForEvent true },{e->section(e, message ,members,event)})

    }

    private fun section(e:ButtonClickEvent,message:Message,members:List<Member>,event: CommandEvent){
        val args = event.args.replace("　"," ").split(" ")
        var count = 0
        var del_count = 0
        val goal_count = Integer.parseInt(args[1])
        val target_list = event.guild.getMembersByName(args[0],false)

        val removemessages: ArrayList<Message> = arrayListOf()
        if (e.button?.id.equals("左")) {
            page-=1
            message.delete().queue()
            waiter(button_menu(event,page,members),members,event)
            return
        } else if (e.button?.id.equals("右")) {
            page+=1
            message.delete().queue()
            waiter(button_menu(event,page,members),members,event)
            return
        } else if (e.button?.id.equals("決定")) {
            val target = target_list[page]
            result=members[page]
            message.delete().queue()
            event.channel.history.retrievePast(100).queue { history ->
                while (del_count < goal_count && count < history.size) {
                    if (history[count].member==target) {
                        removemessages.add(history[count])
                        del_count += 1
                    }
                    count += 1
                }
                //削除するメッセージが100個以上ある場合
                while (del_count < goal_count)  {
                    if (history.size<100) {
                        break
                    }
                    event.channel.getHistoryBefore(history[99],100).queue { history_sub ->
                        count = 0
                        while (del_count < goal_count && count < history_sub.size()) {
                            if (history_sub.retrievedHistory[count].member == target) {
                                removemessages.add(history[count])
                                del_count += 1
                            }
                            count += 1
                        }
                    }
                }

                val embed_check= EmbedBuilder()
                    .setTitle("本当に削除しますか？")
                    .appendDescription(removemessages.size.toString()+"個のメッセージが削除されます!")
                    .setFooter("１分後に自動キャンセル")
                    .setColor(Color.RED)
                    .build()

                //確認
                event.channel.sendMessage(embed_check).setActionRow( // Add our Buttons (max 5 per ActionRow)
                    Button.of(ButtonStyle.DANGER, "丸", "はい"),     // 🐱
                    Button.of(ButtonStyle.SECONDARY, "バツ", "いいえ"),     // 🐶
                ).queue { message ->
                    BotClient.waiter.waitForEvent(
                        ButtonClickEvent::class.java,
                        { e -> return@waitForEvent e.messageId.equals(message.id)&&e.user.id.equals(event.member.user.id) },
                        { e ->if(e.button?.label.equals("はい")) {
                            val embed = EmbedBuilder()
                                .setColor(Color.PINK)
                                .setTitle("メッセージの削除が完了しました")
                                .build()
                            e.channel.purgeMessages(removemessages)
                            event.channel.sendMessage(embed).queue{mes ->
                                mes.delete().queueAfter(10,TimeUnit.SECONDS)
                            }
                        }else if(e.button?.label.equals("いいえ")){
                            val embed = EmbedBuilder()
                                .setColor(Color.PINK)
                                .setTitle("キャンセルしました")
                                .build()
                            event.channel.sendMessage(embed).queue{mes ->
                                mes.delete().queueAfter(10,TimeUnit.SECONDS)
                            }
                        }
                            message.delete().queue()
                            return@waitForEvent
                        },
                        1, TimeUnit.MINUTES,
                        {
                            val embed = EmbedBuilder()
                                .setTitle("キャンセルしました")
                                .setColor(Color.PINK)
                                .build()
                            event.channel.sendMessage(embed).queue{mes ->
                                mes.delete().queueAfter(10,TimeUnit.SECONDS)
                            }
                            message.delete().queue()
                        }
                    )
                }
            }
        }
    }

    private fun make_embed(member: Member,page: Int,maxpage: Int):MessageEmbed {
        val embed = EmbedBuilder()
            .setTitle(member.user.asTag)
            .setThumbnail(member.user.avatarUrl)
            .setAuthor((page+1).toString()+"/"+ maxpage.toString())
            .build()
        return embed
    }
}




