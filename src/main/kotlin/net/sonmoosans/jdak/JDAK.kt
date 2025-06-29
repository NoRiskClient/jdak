package net.sonmoosans.jdak

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.sonmoosans.jdak.builder.CommandBuilder
import net.sonmoosans.jdak.listener.CommandListener
import net.sonmoosans.jdak.listener.CommandListenerBuilderImpl

data class CommandsBuild(
    val commands: List<CommandData>,
    val listener: CommandListener
)

object JDAK {
    val globalListener = CommandListenerBuilderImpl()

    private fun build(init: CommandBuilder.() -> Unit): CommandsBuild {
        val builder = CommandBuilder().apply(init)
        val commands = arrayListOf<CommandData>()

        builder.buildTo(commands, globalListener)

        return CommandsBuild(commands, globalListener.build())
    }

    fun guilds(jda: JDA, guilds: List<Guild>, init: CommandBuilder.() -> Unit) {
        val data = build(init)

        for (guild in guilds) {
            guild.updateCommands()
                .addCommands(data.commands)
                .queue()
        }

        jda.addEventListener(data.listener)
    }

    fun global(jda: JDA, init: CommandBuilder.() -> Unit) {
        val data = build(init)

        jda.updateCommands()
            .addCommands(data.commands)
            .queue()

        jda.addEventListener(data.listener)
    }

    fun registerListeners(jda: JDA) {
        jda.addEventListener(globalListener.build())
    }
}
