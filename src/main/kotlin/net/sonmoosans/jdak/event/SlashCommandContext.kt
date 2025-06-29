package net.sonmoosans.jdak.event

import kotlinx.serialization.json.Json
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.sonmoosans.jdak.command.OptionsContainer
import net.sonmoosans.jdak.command.TypedCommandOption

class SlashCommandContext(
    val event: SlashCommandInteractionEvent
) {
    val options = hashMapOf<String, Any?>()

    fun parseOptions(command: OptionsContainer) {
        for (option in command.options) {
            this.options[option.name] = option.parse(event)
        }
    }

    inline operator fun <reified T> TypedCommandOption<T>.invoke() = value()


    inline fun<reified T> TypedCommandOption<T>.value(): T {
        if (this.type == OptionType.STRING) {
            return Json.decodeFromString<T>(options[name] as String)
        }

        return options[name] as T
    }
}
